package com.thilojaeggi.frooze;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.thilojaeggi.frooze.Adaptor.PostAdapter;
import com.thilojaeggi.frooze.Model.Post;

import java.util.ArrayList;
import java.util.List;

import im.ene.toro.exoplayer.Config;
import im.ene.toro.exoplayer.MediaSourceBuilder;
import im.ene.toro.exoplayer.ToroExo;


public class NewPostsFragment extends Fragment {
    private RecyclerView recyclerView;
    private PostAdapter postAdapter;
    private PostAdapter.ViewHolder viewHolder;
    private List<Post> postLists;
    private List<String> followingList;
    LinearLayoutManager linearLayoutManager;
    Config config;

    private SimpleExoPlayer mPlayer;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        config = ToroExo.with(getContext()).getDefaultConfig().newBuilder()
                .setMediaSourceBuilder(MediaSourceBuilder.LOOPING).build();
        recyclerView = rootView.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL,false);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        postLists = new ArrayList<>();
        postAdapter = new PostAdapter(getContext(), postLists);
        recyclerView.setAdapter(postAdapter);
        readPosts();
        PagerSnapHelper helper = new PagerSnapHelper();
        helper.attachToRecyclerView(recyclerView);
        AdView mAdView = rootView.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        TextView newposts = rootView.findViewById(R.id.newposts);
        TextView followingposts = rootView.findViewById(R.id.followingposts);
        followingposts.setTextSize(13);
        TextView trendingposts = rootView.findViewById(R.id.trendingposts);
        trendingposts.setTextSize(13);
        newposts.setTextSize(17);
        newposts.setTypeface(newposts.getTypeface(), Typeface.BOLD);
        followingposts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment sortbyfollowing = new FollowPostsFragment();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, sortbyfollowing)
                        .addToBackStack(null)
                        .commit();
            }
        });
        trendingposts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment sortbytrending = new TrendingPostsFragment();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, sortbytrending)
                        .addToBackStack(null)
                        .commit();
            }
        });
//set adapter
//You just need to impelement ViewPageAdapter by yourself like a normal RecyclerView.Adpater.
//        getPlayer();

        return rootView;
    }



    private void readPosts() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postLists.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Post post = snapshot.getValue(Post.class);
                        if (post.getPublisher() != null && !post.getPublisher().isEmpty()) {
                            postLists.add(post);

                    }
                }
                postAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }



    public void onViewCreated(View rootView, Bundle savedInstanceState) {
        super.onViewCreated(rootView, savedInstanceState);
        // initialise your views

    /*    SwipeRefreshLayout swipeRefreshLayout = getView().findViewById(R.id.swiperefresh);
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

        });*/
    }



    @Override
    public void onDestroyView() {
      super.onDestroyView();

    }
    @Override
    public void onResume() {

        super.onResume();
    }
    @Override
    public void onPause() {
        super.onPause();

    }


}