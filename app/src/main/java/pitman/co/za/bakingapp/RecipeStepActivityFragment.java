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

//public class RecipeStepActivityFragment extends Fragment implements ExoPlayer.EventListener {
public class RecipeStepActivityFragment extends Fragment {

    private static String LOG_TAG = RecipeStepActivityFragment.class.getSimpleName();
    private View rootView;
    private Recipe mSelectedRecipe;
    private int mRecipeStepIndex;
    private SimpleExoPlayer mExoPlayer;
    private SimpleExoPlayerView mPlayerView;
    //    private static MediaSessionCompat mMediaSession;
//    private PlaybackStateCompat.Builder mStateBuilder;
    private long playerCurrentPosition = 0;
    private long playerStoppedPosition = 0;
    private boolean currentlyPlaying = false;
    private boolean isTablet = false;
    private RecipeStepActivityFragment.Callbacks mCallbacks;

    public RecipeStepActivityFragment() {
        Log.d(LOG_TAG, "RecipeStepActivityFragment constructor called");
    }

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
        outState.putParcelable("selectedRecipe", mSelectedRecipe);
        outState.putInt("selectedStep", mRecipeStepIndex);
        outState.putLong("currentPosition", mExoPlayer.getCurrentPosition());
        outState.putBoolean("isTablet", isTablet);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(LOG_TAG, "3. onCreateView()");

        rootView = inflater.inflate(R.layout.activity_recipe_step, container, false);
        Log.d(LOG_TAG, "onCreateView() in RecipeStepActivityFragment called");

        if (savedInstanceState != null) {
            mSelectedRecipe = savedInstanceState.getParcelable("selectedRecipe");
            mRecipeStepIndex = savedInstanceState.getInt("selectedStep", 0);
            playerCurrentPosition = savedInstanceState.getLong("currentPosition");
            isTablet = savedInstanceState.getBoolean("isTablet");

            Log.d(LOG_TAG, "recipe retrieved from savedInstanceState");

            // Fragment instantiated from RecipeSkeletonActivity, in master-detail layout
        } else if (this.getArguments() != null) {
            Bundle arguments = this.getArguments();
            mSelectedRecipe = arguments.getParcelable("selectedRecipe");
            mRecipeStepIndex = arguments.getInt("selectedItemPosition");
            isTablet = arguments.getBoolean("isTablet");    // todo: not necessarily the case...if called from RecipeStepActivity due to previous/next button selected, will also execute

            // Fragment instantiated by RecipeStepActivity, as stand-alone fragment
        } else {
            Intent intent = getActivity().getIntent();
            if ((intent != null)) {
                mSelectedRecipe = intent.getParcelableExtra("recipe");
                mRecipeStepIndex = intent.getIntExtra("selectedStep", 0);
            }
        }

        // if user selects 'Ingredients' tab, mRecipeStepIndex may == -1, so set to 0
        mRecipeStepIndex = (mRecipeStepIndex < 0 ? 0 : mRecipeStepIndex);

        RecipeStep selectedStep = mSelectedRecipe.getRecipeSteps().get(mRecipeStepIndex);
        Log.d(LOG_TAG, "selectedRecipe is null? " + (mSelectedRecipe == null) + ". Selected step ID: " + mRecipeStepIndex);
        Log.d(LOG_TAG, "" + selectedStep.getShortDescription() + " .:. " + selectedStep.getVideoUrl());

        TextView longDescriptionView = (TextView) rootView.findViewById(R.id.recipe_instructions);
        longDescriptionView.setText(selectedStep.getDescription());
        mPlayerView = rootView.findViewById(R.id.playerView);

        // Video IRL may be empty, or empty but the video URL mistakenly in the image URL field, so check and copy it across
        String videoUrl = selectedStep.getVideoUrl();
        String imageUrl = selectedStep.getThumbnailUrl();
        if (videoUrl.isEmpty() && !imageUrl.isEmpty()) {
            videoUrl = imageUrl.contains(".mp4") ? imageUrl : "";
        }

        // Initialize the player view.
        if (!videoUrl.isEmpty()) {
            mPlayerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
            initializePlayer(Uri.parse(videoUrl));
        } else {
            // Hint on setting the mPlayerView invisible if no video available
            assert mPlayerView != null;
            mPlayerView.setVisibility(View.GONE);
        }

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
        int numberOfSteps = mSelectedRecipe.getRecipeSteps().size() - 1;
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

    @Override
    public void onStart() {
        super.onStart();
    }

    // Taken from Udacity ClassicalMusicQuiz application
    // https://medium.com/@yusufcakmak/playing-audio-and-video-with-exoplayer-2-4f4c2c2d9772 used as an additional source
    // https://medium.com/google-exoplayer/customizing-exoplayers-ui-components-728cf55ee07a
    // https://stackoverflow.com/questions/45481775/exoplayer-restore-state-when-resumed

    /**
     * Initializes the Media Session to be enabled with media buttons, transport controls, callbacks
     * and media controller.
     */
//    private void initializeMediaSession() {
//
//        // Create a MediaSessionCompat.
//        mMediaSession = new MediaSessionCompat(getActivity(), LOG_TAG);
//
//        // Enable callbacks from MediaButtons and TransportControls.
//        mMediaSession.setFlags(
//                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
//                        MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
//
//        // Do not let MediaButtons restart the player when the app is not visible.
//        mMediaSession.setMediaButtonReceiver(null);
//
//        // Set an initial PlaybackState with ACTION_PLAY, so media buttons can start the player.
//        mStateBuilder = new PlaybackStateCompat.Builder()
//                .setActions(
//                        PlaybackStateCompat.ACTION_PLAY |
//                                PlaybackStateCompat.ACTION_PAUSE |
//                                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
//                                PlaybackStateCompat.ACTION_PLAY_PAUSE);
//
//        mMediaSession.setPlaybackState(mStateBuilder.build());
//
//
//        // MySessionCallback has methods that handle callbacks from a media controller.
//        mMediaSession.setCallback(new MySessionCallback());
//
//        // Start the Media Session since the activity is active.
//        mMediaSession.setActive(true);
//    }

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
            mExoPlayer.setPlayWhenReady(true);
            // Start video playback at the correct location if device is rotated, or fragment navigated away from
            if (playerCurrentPosition != 0 && !currentlyPlaying) {
                mExoPlayer.seekTo(playerCurrentPosition);
            } else {
                mExoPlayer.seekTo(playerStoppedPosition);
            }
        }


//  https://stackoverflow.com/questions/45481775/exoplayer-restore-state-when-resumed
//        private void initializePlayer(Uri mediaUri) {
//            if (player == null) {
//                TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveVideoTrackSelection.Factory(bandwidthMeter);
//                DefaultTrackSelector trackSelector = new DefaultTrackSelector(mainHandler, videoTrackSelectionFactory);
//                LoadControl loadControl = new DefaultLoadControl();
//                player = ExoPlayerFactory.newSimpleInstance(getContext(), trackSelector, loadControl);
//                simpleExoPlayerView.setPlayer(player);
//                String userAgent = Util.getUserAgent(getContext(), "Baking App");
//                MediaSource mediaSource = new ExtractorMediaSource(mediaUri, new DefaultDataSourceFactory(getContext(), userAgent), new DefaultExtractorsFactory(), null, null);
//                player.prepare(mediaSource);
//                player.setPlayWhenReady(true);
//            }
//        }
    }

    private void releasePlayer() {
        if (mExoPlayer != null) {
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


    /*
     * Method that is called when the ExoPlayer state changes. Used to update the MediaSession
     * PlayBackState to keep in sync, and post the media notification.
     * @param playWhenReady true if ExoPlayer is playing, false if it's paused.
     * @param playbackState int describing the state of ExoPlayer. Can be STATE_READY, STATE_IDLE,
     *                      STATE_BUFFERING, or STATE_ENDED.
     */
//    @Override
//    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
//        if((playbackState == ExoPlayer.STATE_READY) && playWhenReady){
//            mStateBuilder.setState(PlaybackStateCompat.STATE_PLAYING,
//                    mExoPlayer.getCurrentPosition(), 1f);
//        } else if((playbackState == ExoPlayer.STATE_READY)){
//            mStateBuilder.setState(PlaybackStateCompat.STATE_PAUSED,
//                    mExoPlayer.getCurrentPosition(), 1f);
//        }
//        mMediaSession.setPlaybackState(mStateBuilder.build());
////        showNotification(mStateBuilder.build());
//    }
//
//    @Override
//    public void onPlayerError(ExoPlaybackException error) {
//    }

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//  Fragment end-of-lifecycle methods - stop/tear-down media player components ///////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

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
