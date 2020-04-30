package com.thilojaeggi.frooze;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.RepeatModeUtil;
import com.google.android.exoplayer2.util.Util;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lsjwzh.widget.recyclerviewpager.RecyclerViewPager;
import com.race604.drawable.wave.WaveDrawable;
import com.thilojaeggi.frooze.Adaptor.PostAdapter;
import com.thilojaeggi.frooze.Model.Post;

import java.util.ArrayList;
import java.util.List;



public class HomeFragment extends Fragment {
    private RecyclerView recyclerView;
    private PostAdapter postAdapter;
    private List<Post> postLists;
    private List<String> followingList;



    private SimpleExoPlayer mPlayer;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = rootView.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL,false);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        postLists = new ArrayList<>();
        postAdapter = new PostAdapter(getContext(), postLists);
        recyclerView.setAdapter(postAdapter);
        checkFollowing();

//set adapter
//You just need to impelement ViewPageAdapter by yourself like a normal RecyclerView.Adpater.
//        getPlayer();

        return rootView;

    }



    private void checkFollowing() {
    followingList = new ArrayList<>();

    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Follow")
            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
            .child("following");

    reference.addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            followingList.clear();
            for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                followingList.add(snapshot.getKey());
            }

            readPosts();


        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    });
    }

    private void readPosts() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postLists.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Post post = snapshot.getValue(Post.class);
                    for (String id : followingList){
                        if (post.getPublisher().equals(id)) {
                            postLists.add(post);
                        }
                    }
                }


                postAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getPlayer() {
        // URL of the video to stream
        //String videoURL = "https://frooze-hls.cdnvideo.ru/hls/5e554223daed02000157ce82/playlist.m3u8";
       // Uri uri = Uri.parse(videoURL);

        // Handler for the video player
        Handler mainHandler = new Handler();
        //mPlayerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_ZOOM);
	/* A TrackSelector that selects tracks provided by the MediaSource to be consumed by each of the available Renderers.
	  A TrackSelector is injected when the player is created. */
      //  BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
      //  TrackSelection.Factory videoTrackSelectionFactory =
                //new AdaptiveTrackSelection.Factory(bandwidthMeter);
       // TrackSelector trackSelector =
        //        new DefaultTrackSelector(videoTrackSelectionFactory);

        // Create the player with previously created TrackSelector
     //   mPlayer = ExoPlayerFactory.newSimpleInstance(getContext(), trackSelector);

        // Load the default controller
       // mPlayerView.setUseController(false);
       // mPlayerView.requestFocus();
        // Load the SimpleExoPlayerView with the created player
       // mPlayerView.setPlayer(mPlayer);

        // Measures bandwidth during playback. Can be null if not required.
   //     DefaultBandwidthMeter defaultBandwidthMeter = new DefaultBandwidthMeter();

        // Produces DataSource instances through which media data is loaded.
      //  DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(
           //     getContext(),
            //    Util.getUserAgent(getContext(), "frooze"),
              //  defaultBandwidthMeter);



        // This is the MediaSource representing the media to be played.
       // MediaSource videoSource = new HlsMediaSource(
           //     Uri.parse(videoURL),
             //   dataSourceFactory,
             //   null,
              //  null);

        // Prepare the player with the source.
//        mPlayer.prepare(videoSource);

        // Autoplay the video when the player is ready
      //  mPlayer.setPlayWhenReady(true);
    }

    public void onViewCreated(View rootView, Bundle savedInstanceState) {
        super.onViewCreated(rootView, savedInstanceState);
        // initialise your views

        RecyclerViewPager recyclerViewPager = rootView.findViewById(R.id.recycler_view);
        recyclerViewPager.addOnPageChangedListener(new RecyclerViewPager.OnPageChangedListener() {
            @Override
            public void OnPageChanged(int oldPosition, int newPosition) {
            }
        });
        SwipeRefreshLayout swipeRefreshLayout = getView().findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your recyclerview reload logic function will be here!!!
                readPosts();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 2000);
            }

        });
    }



    @Override
    public void onDestroyView() {
      super.onDestroyView();
      //  unbinder.unbind();

        // Release the player when it is not needed
    }
    @Override
    public void onPause() {
        super.onPause();

    }
}