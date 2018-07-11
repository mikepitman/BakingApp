package pitman.co.za.bakingapp;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import pitman.co.za.bakingapp.domainObjects.Recipe;
import pitman.co.za.bakingapp.domainObjects.RecipeStep;

/**
 * Created by Michael on 2018/02/13.
 * Hints and guidance on some aspects taken from: (like hiding the exoplayer view when URL is empty)
 * // https://github.com/tonynguyen0523/Baking-App/blob/master/app/src/main/java/com/swipeacademy/kissthebaker/BakingInstructions/DirectionsFragment.java
 */

public class RecipeStepActivityFragment extends Fragment {

    private static final String SELECTED_RECIPE = "selectedRecipe";
    private static final String SELECTED_STEP = "selectedStep";
    private static final String IS_TABLET = "isTablet";
    private static final String CURRENT_POSITION = "currentPosition";
    private static final String PLAY_WHEN_READY = "playWhenReady";

    private static String LOG_TAG = RecipeStepActivityFragment.class.getSimpleName();
    private View rootView;
    private Recipe mSelectedRecipe;
    private int mRecipeStepIndex;
    private SimpleExoPlayer mExoPlayer;
    private SimpleExoPlayerView mPlayerView;
    private long playerCurrentPosition = 0;
    private long playerStoppedPosition = 0;
    private boolean playWhenReady = true;
    private boolean isTablet = false;
    private RecipeStepActivityFragment.Callbacks mCallbacks;

    public RecipeStepActivityFragment() {}

    //// Callbacks-related code //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public interface Callbacks {
        void onRecipeStepChange(Recipe recipe, int step);
    }

    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mCallbacks = (RecipeStepActivityFragment.Callbacks) activity;
    }

    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }
    //// Callbacks-related code //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(SELECTED_RECIPE, mSelectedRecipe);
        outState.putInt(SELECTED_STEP, mRecipeStepIndex);
        outState.putBoolean(IS_TABLET, isTablet);
        if (mExoPlayer != null) {
            outState.putLong(CURRENT_POSITION, mExoPlayer.getCurrentPosition());
            outState.putBoolean(PLAY_WHEN_READY, mExoPlayer.getPlayWhenReady());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_recipe_step, container, false);

        if (savedInstanceState != null) {
            mSelectedRecipe = savedInstanceState.getParcelable(SELECTED_RECIPE);
            mRecipeStepIndex = savedInstanceState.getInt(SELECTED_STEP, 0);
            isTablet = savedInstanceState.getBoolean(IS_TABLET);
            // These values only saved when mExoPlayer != null, ie there is a video to play, so default values don't affect anything
            playerCurrentPosition = savedInstanceState.getLong(CURRENT_POSITION);
            playWhenReady = savedInstanceState.getBoolean(PLAY_WHEN_READY);

            // Fragment instantiated from RecipeSkeletonActivity, in master-detail layout
        } else if (this.getArguments() != null) {
            Bundle arguments = this.getArguments();
            mSelectedRecipe = arguments.getParcelable("selectedRecipe");
            mRecipeStepIndex = arguments.getInt("selectedItemPosition");
            isTablet = arguments.getBoolean("isTablet");

            // Fragment instantiated by RecipeStepActivity, as stand-alone fragment
        } else {
            Intent intent = getActivity().getIntent();
            if ((intent != null)) {
                mSelectedRecipe = intent.getParcelableExtra("recipe");
                mRecipeStepIndex = intent.getIntExtra("selectedStep", 0);
                isTablet = false;
            }
        }

        // if user selects 'Ingredients' tab, mRecipeStepIndex may == -1, so set to 0
        mRecipeStepIndex = (mRecipeStepIndex < 0 ? 0 : mRecipeStepIndex);

        createMediaPlayer();

        Button backButton = (Button) rootView.findViewById(R.id.previousStepButton);
        if (mRecipeStepIndex == 0 || isTablet) {            // First step - button should not be displayed
            backButton.setVisibility(View.GONE);
        } else {
            backButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    Log.d(LOG_TAG, "BackButton pressed");
                    mCallbacks.onRecipeStepChange(mSelectedRecipe, mRecipeStepIndex - 1);
                }
            });
        }

        Button nextButton = (Button) rootView.findViewById(R.id.nextStepButton);
        int numberOfSteps = mSelectedRecipe.getSteps().size() - 1;
        if (mRecipeStepIndex == numberOfSteps || isTablet) {      // Last recipe step - button should not be displayed
            nextButton.setVisibility(View.GONE);
        } else {
            nextButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    Log.d(LOG_TAG, "nextButton pressed");
                    mCallbacks.onRecipeStepChange(mSelectedRecipe, mRecipeStepIndex + 1);
                }
            });
        }

        return rootView;
    }

    private void createMediaPlayer() {
        RecipeStep selectedStep = mSelectedRecipe.getSteps().get(mRecipeStepIndex);

        TextView longDescriptionView = (TextView) rootView.findViewById(R.id.recipe_instructions);
        longDescriptionView.setText(selectedStep.getDescription());
        mPlayerView = rootView.findViewById(R.id.playerView);
        ImageView thumbnailImageView = (ImageView) rootView.findViewById(R.id.thumbnail_image);

        // Video IRL may be empty, or empty but the video URL mistakenly in the image URL field, so check and copy it across
        String videoUrl = selectedStep.getVideoUrl();
        String imageUrl = selectedStep.getThumbnailUrl();
        if (videoUrl.isEmpty() && !imageUrl.isEmpty()) {
            videoUrl = imageUrl.contains(".mp4") ? imageUrl : "";
        }

        // Initialize the player view.
        if (!videoUrl.isEmpty() ) {
            mPlayerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
            initializePlayer(Uri.parse(videoUrl));
            thumbnailImageView.setVisibility(View.GONE);
        } else {
            // Hint on setting the mPlayerView invisible if no video available
            assert mPlayerView != null;
            mPlayerView.setVisibility(View.GONE);

            if (!imageUrl.isEmpty()) {
                Uri thumbnailUri = Uri.parse(imageUrl);
                thumbnailImageView.setImageURI(thumbnailUri);
            } else {
                thumbnailImageView.setVisibility(View.GONE);
            }
        }

    }

    /**
     * Initialize ExoPlayer.
     *
     * @param mediaUri The URI of the sample to play.
     */
    private void initializePlayer(Uri mediaUri) {
        // Create an instance of the ExoPlayer.
        if (mExoPlayer == null) {
            TrackSelector trackSelector = new DefaultTrackSelector();
            LoadControl loadControl = new DefaultLoadControl();
            mExoPlayer = ExoPlayerFactory.newSimpleInstance(getActivity(), trackSelector, loadControl);
            mPlayerView.setPlayer(mExoPlayer);
            String userAgent = Util.getUserAgent(getActivity(), "Baking App");
            MediaSource mediaSource = new ExtractorMediaSource(mediaUri, new DefaultDataSourceFactory(getContext(), userAgent),
                    new DefaultExtractorsFactory(), null, null);
            mExoPlayer.prepare(mediaSource);

            // Start video playback at the correct location if device is rotated, or fragment navigated away from
            if (playerCurrentPosition != 0 && playWhenReady) {
                mExoPlayer.seekTo(playerCurrentPosition);
            } else {
                mExoPlayer.seekTo(playerStoppedPosition);
            }
            mExoPlayer.setPlayWhenReady(playWhenReady);
        }
    }

    private void releasePlayer() {
        if (mExoPlayer != null) {
            // https://github.com/google/ExoPlayer/blob/release-v2/demos/main/src/main/java/com/google/android/exoplayer2/demo/PlayerActivity.java
            // To ensure that the video starts again at the point it left off
            playWhenReady = mExoPlayer.getPlayWhenReady();
            playerCurrentPosition = mExoPlayer.getCurrentPosition();

            mExoPlayer.stop();
            mExoPlayer.release();
            mExoPlayer = null;
        }
    }

    /**
     * Media Session Callbacks, where all external clients control the player.
     */
    private class MySessionCallback extends MediaSessionCompat.Callback {
        @Override
        public void onPlay() {
            mExoPlayer.setPlayWhenReady(true);
        }

        @Override
        public void onPause() {
            mExoPlayer.setPlayWhenReady(false);
        }

        @Override
        public void onSkipToPrevious() {
            mExoPlayer.seekTo(0);
        }
    }

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//  Fragment end-of-lifecycle methods - stop/tear-down media player components ///////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mExoPlayer == null) {
            createMediaPlayer();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        releasePlayer();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        releasePlayer();
    }
}
