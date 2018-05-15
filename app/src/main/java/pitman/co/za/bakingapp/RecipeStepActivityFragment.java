package pitman.co.za.bakingapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import java.util.ArrayList;

import pitman.co.za.bakingapp.domainObjects.Recipe;
import pitman.co.za.bakingapp.domainObjects.RecipeStep;

/**
 * Created by Michael on 2018/02/13.
 */

//public class RecipeStepActivityFragment extends Fragment implements ExoPlayer.EventListener {
public class RecipeStepActivityFragment extends Fragment {

    private static String LOG_TAG = RecipeStepActivityFragment.class.getSimpleName();
    private View rootView;
    private Recipe mSelectedRecipe;
    private int mRecipeStepIndex;
    private ArrayList<RecipeStep> mRecipeSteps;
    private SimpleExoPlayer mExoPlayer;
    private SimpleExoPlayerView mPlayerView;
    private static MediaSessionCompat mMediaSession;
    private PlaybackStateCompat.Builder mStateBuilder;

    public RecipeStepActivityFragment() { Log.d(LOG_TAG, "RecipeStepActivityFragment constructor called"); }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("selectedRecipe", mSelectedRecipe);
        outState.putInt("selectedStep", mRecipeStepIndex);
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
            Log.d(LOG_TAG, "recipe retrieved from savedInstanceState");

        } else {
            Intent intent = getActivity().getIntent();
            if ((intent != null)) {
                mSelectedRecipe = intent.getParcelableExtra("recipe");
                mRecipeStepIndex = intent.getIntExtra("selectedStep", 0);
                Log.d(LOG_TAG, "In RecipeStepActivityFragment, selectedRecipe is null? " + (mSelectedRecipe == null));
            }

            // master-detail layout for a tablet -- change this to be more accurate/resilient
//            if (mSelectedRecipe == null) {
//                Bundle arguments = this.getArguments();
//                mSelectedRecipe = arguments.getParcelable("selectedRecipe");
//                mRecipeStepIndex = arguments.getInt("selectedStep", 0);
//            }
        }

        RecipeStep selectedStep = mSelectedRecipe.getRecipeSteps().get(mRecipeStepIndex);
        Log.d(LOG_TAG, "selectedRecipe is null? " + (mSelectedRecipe == null) + ". Selected step ID: " + mRecipeStepIndex);
        Log.d(LOG_TAG, "" + selectedStep.getShortDesciption() + "  " + selectedStep.getVideoUrl());

        TextView longDescriptionView = (TextView) rootView.findViewById(R.id.recipe_instructions);
        longDescriptionView.setText(selectedStep.getDescription());

        // Initialize the player view.
        mPlayerView = rootView.findViewById(R.id.playerView);
        mPlayerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
        if (selectedStep.getVideoUrl() != null) {
            initializePlayer(Uri.parse(selectedStep.getVideoUrl()));
        }

        return rootView;
    }


    // Taken from Udacity ClassicalMusicQuiz application
    // https://medium.com/@yusufcakmak/playing-audio-and-video-with-exoplayer-2-4f4c2c2d9772 used as an additional source
    // https://medium.com/google-exoplayer/customizing-exoplayers-ui-components-728cf55ee07a
    // https://stackoverflow.com/questions/45481775/exoplayer-restore-state-when-resumed
    /**
     * Initializes the Media Session to be enabled with media buttons, transport controls, callbacks
     * and media controller.
     */
    private void initializeMediaSession() {

        // Create a MediaSessionCompat.
        mMediaSession = new MediaSessionCompat(getActivity(), LOG_TAG);

        // Enable callbacks from MediaButtons and TransportControls.
        mMediaSession.setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                        MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        // Do not let MediaButtons restart the player when the app is not visible.
        mMediaSession.setMediaButtonReceiver(null);

        // Set an initial PlaybackState with ACTION_PLAY, so media buttons can start the player.
        mStateBuilder = new PlaybackStateCompat.Builder()
                .setActions(
                        PlaybackStateCompat.ACTION_PLAY |
                                PlaybackStateCompat.ACTION_PAUSE |
                                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
                                PlaybackStateCompat.ACTION_PLAY_PAUSE);

        mMediaSession.setPlaybackState(mStateBuilder.build());


        // MySessionCallback has methods that handle callbacks from a media controller.
        mMediaSession.setCallback(new MySessionCallback());

        // Start the Media Session since the activity is active.
        mMediaSession.setActive(true);
    }

    /**
     * Initialize ExoPlayer.
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


    /**
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



}
