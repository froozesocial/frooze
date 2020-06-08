package com.thilojaeggi.frooze.Intro;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.RawResourceDataSource;
import com.thilojaeggi.frooze.R;


public class SecondSlide extends Fragment {


    ExoPlayer player;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.secondslide, container, false);
        TextView videotext = view.findViewById(R.id.videotext);
        SimpleExoPlayerView playerView = view.findViewById(R.id.videoview);
        TrackSelection.Factory videoTrackSelectionFactory =
                new AdaptiveTrackSelection.Factory(null);
        player = ExoPlayerFactory.newSimpleInstance(getContext(), new DefaultTrackSelector(videoTrackSelectionFactory));
        player.setRepeatMode(Player.REPEAT_MODE_OFF);
        playerView.setUseController(false);
        playerView.setPlayer(player);
        playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
        player.setPlayWhenReady(true);
        final RawResourceDataSource rawResourceDataSource = new RawResourceDataSource(getContext());
        DataSpec dataSpec = new DataSpec(RawResourceDataSource.buildRawResourceUri(R.raw.homesmall));
        try {
            rawResourceDataSource.open(dataSpec);
            DataSource.Factory factory = new DataSource.Factory() {
                @Override
                public DataSource createDataSource() {
                    return rawResourceDataSource;
                }
            };
            MediaSource videoSource = new ExtractorMediaSource.Factory(factory).createMediaSource(rawResourceDataSource.getUri());
            player.prepare(videoSource);
        } catch (RawResourceDataSource.RawResourceDataSourceException e) {
            e.printStackTrace();
        }
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                videotext.setText(getString(R.string.dtlike));
            }
        }, 1080);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                videotext.setText(getString(R.string.wholemenu));
                videotext.setTextSize(25);
            }
        }, 7000);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                videotext.setText(R.string.tapprofile);
                videotext.setTextSize(25);
            }
        }, 11000);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                videotext.setText(getString(R.string.viewhashtag));
                videotext.setTextSize(25);
            }
        }, 23000);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                videotext.setText(getString(R.string.thatsall));
                videotext.setTextSize(30);
            }
        }, 30000);
        return view;
    }







    public void onViewCreated(View rootView, Bundle savedInstanceState) {
        super.onViewCreated(rootView, savedInstanceState);

    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }

    @Override
    public void onPause() {
        super.onPause();

    }


}