package saa.com.testexoplayer;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.DynamicConcatenatingMediaSource;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.util.ArrayList;
import java.util.List;

import saa.com.testexoplayer.Model.video;

public class PlayerActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    Context mContext;
    VideoRecyclerViewAdapter adapter;
    List<video> arrVideoList = new ArrayList<>();
    PlayerView playerView;
    ExoPlayer player;
    int currentWindow;
    long playbackPosition;
    Boolean playWhenReady = true;
    DynamicConcatenatingMediaSource dynamicConcatenatingMediaSource;
    String TAG = "PlayerActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mContext = this;

        this.populateVideoList();

        playerView = (PlayerView)findViewById(R.id.video_view) ;
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        adapter = new VideoRecyclerViewAdapter();
        adapter.setMoviesList(arrVideoList);

        recyclerView.setAdapter(adapter);

        Drawable d = this.getResources().getDrawable(R.mipmap.fury_road);

        if(savedInstanceState != null){
            Log.i(TAG,"getFromSavedInstanceState playbackPosition");
            playbackPosition = savedInstanceState.getLong("playbackPosition",playbackPosition);
            currentWindow = savedInstanceState.getInt("currentWindow",currentWindow);
            playWhenReady = savedInstanceState.getBoolean("playWhenReady",false);
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    private void initializePlayer() {
        player = ExoPlayerFactory.newSimpleInstance(
                new DefaultRenderersFactory(this),
                new DefaultTrackSelector(), new DefaultLoadControl());

        playerView.setPlayer(player);


        dynamicConcatenatingMediaSource = new DynamicConcatenatingMediaSource();

        for(int i =0; i < arrVideoList.size(); i++) {
            Uri uri = Uri.parse(arrVideoList.get(i).videoLink);
            MediaSource mediaSource = buildMediaSource(uri);
            dynamicConcatenatingMediaSource.addMediaSource(mediaSource);
        }


        player.prepare(dynamicConcatenatingMediaSource, true, false);

        player.setPlayWhenReady(playWhenReady);
        player.seekTo(currentWindow, playbackPosition);

        player.addListener(new Player.EventListener() {
            @Override
            public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {

            }

            @Override
            public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

            }

            @Override
            public void onLoadingChanged(boolean isLoading) {

            }

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                //Toast.makeText(mContext,"State end "+ playWhenReady,Toast.LENGTH_SHORT).show();
                switch (playbackState) {
                    case Player.STATE_ENDED : {
                        Toast.makeText(mContext,"State end",Toast.LENGTH_SHORT).show();
                        final int index = player.getCurrentWindowIndex();
                        final ProgressDialog dialog = new ProgressDialog(mContext);
                        dialog.setMessage("Loading");
                        dialog.show();
                        new Handler().postDelayed(new Runnable() {

                            @Override
                            public void run() {
                                dialog.dismiss();
                                MediaSource mediaSource = dynamicConcatenatingMediaSource.getMediaSource(index + 1);
                                player.prepare(mediaSource);
                                player.setPlayWhenReady(true);
                            }

                        }, 5000);
                        break;
                    }
                }
            }

            @Override
            public void onRepeatModeChanged(int repeatMode) {

            }

            @Override
            public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {

            }

            @Override
            public void onPositionDiscontinuity(int reason) {
                Toast.makeText(mContext,"discontinuity "+ reason,Toast.LENGTH_SHORT).show();
                if(reason == 0) {
                    final int index = player.getCurrentWindowIndex();
                    final ProgressDialog dialog = new ProgressDialog(mContext);
                    dialog.setMessage("Loading");
                    player.setPlayWhenReady(false);
                    dialog.show();
                    new Handler().postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            dialog.dismiss();
                            player.setPlayWhenReady(true);
                        }

                    }, 5000);
                }
            }

            @Override
            public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

            }

            @Override
            public void onSeekProcessed() {

            }
        });
    }

    private MediaSource buildMediaSource(Uri uri) {
        return new ExtractorMediaSource.Factory(
                new DefaultDataSourceFactory(this.getApplicationContext(),"saa")).
                createMediaSource(uri);
       //ExtractorMediaSource mediaSource = new ExtractorMediaSource(uri, DefaultExtractorsFactory(),null,null);

      //return ExtractorMediaSource(Uri.parse("assets:///onboarding_video.mp4"), dataSourceFactory, DefaultExtractorsFactory(), null, null);


    }

    private void releasePlayer() {
        if (player != null) {
            playbackPosition = player.getCurrentPosition();
            currentWindow = player.getCurrentWindowIndex();
            playWhenReady = player.getPlayWhenReady();
            player.release();
            player = null;
        }
    }
    @Override
    public void onStart() {
        super.onStart();
        if (Util.SDK_INT > 23) {
            Log.i(TAG, "onStart playbackPosition" + playbackPosition);
            initializePlayer();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        //hideSystemUi();
        if ((Util.SDK_INT <= 23 || player == null)) {
            Log.i(TAG, "onResume playbackPosition" + playbackPosition);
            initializePlayer();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (Util.SDK_INT <= 23) {

            releasePlayer();
            Log.i(TAG, "onPause playbackPosition" + playbackPosition);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT > 23) {
            releasePlayer();
            Log.i(TAG, "onStop playbackPosition" + playbackPosition);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        Log.i(TAG, "onSaveInstanceState1 playbackPosition");
        if(Util.SDK_INT >= 21) {
            outPersistentState.putLong("playbackPosition", playbackPosition);
            outPersistentState.putInt("currentWindow", currentWindow);
        }
        super.onSaveInstanceState(outState, outPersistentState);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.i(TAG, "onSaveInstanceState2 playbackPosition");
        playbackPosition = player.getCurrentPosition();
        currentWindow = player.getCurrentWindowIndex();
        playWhenReady = player.getPlayWhenReady();
        outState.putLong("playbackPosition", playbackPosition);
        outState.putInt("currentWindow", currentWindow);
        outState.putBoolean("playWhenReady", playWhenReady);
        super.onSaveInstanceState(outState);
    }

    public void populateVideoList(){
        String packageName = "android.resource://saa.com.testexoplayer/";
        video video = new video("Mad Max: Fury Road", "Action & Adventure", "2015",packageName + R.mipmap.fury_road,"file:///android_asset/fury_road.mp4");
        this.arrVideoList.add(video);

        video = new video("Inside Out", "Animation, Kids & Family", "2015",packageName + R.mipmap.inside_out,"file:///android_asset/inside_out.mp4");
        arrVideoList.add(video);

        video = new video("Star Wars: Episode VII - The Force Awakens", "Action", "2015",packageName + R.mipmap.star_war,"file:///android_asset/the_force_awakens.mp4");
        arrVideoList.add(video);

        video = new video("Shaun the Sheep", "Animation", "2015",packageName + R.mipmap.shaun_the_sheep,"file:///android_asset/shaun_the_sheep.mp4");
        arrVideoList.add(video);

       /* video = new video("The Martian", "Science Fiction & Fantasy", "2015");
        arrVideoList.add(video);

        video = new video("Mission: Impossible Rogue Nation", "Action", "2015");
        arrVideoList.add(video);

        video = new video("Up", "Animation", "2009");
        arrVideoList.add(video);

        video = new video("Star Trek", "Science Fiction", "2009");
        arrVideoList.add(video);

        video = new video("The LEGO video", "Animation", "2014");
        arrVideoList.add(video);

        video = new video("Iron Man", "Action & Adventure", "2008");
        arrVideoList.add(video);

        video = new video("Aliens", "Science Fiction", "1986");
        arrVideoList.add(video);

        video = new video("Chicken Run", "Animation", "2000");
        arrVideoList.add(video);

        video = new video("Back to the Future", "Science Fiction", "1985");
        arrVideoList.add(video);

        video = new video("Raiders of the Lost Ark", "Action & Adventure", "1981");
        arrVideoList.add(video);

        video = new video("Goldfinger", "Action & Adventure", "1965");
        arrVideoList.add(video);

        video = new video("Guardians of the Galaxy", "Science Fiction & Fantasy", "2014");
        arrVideoList.add(video);
        */
        
    }
}
