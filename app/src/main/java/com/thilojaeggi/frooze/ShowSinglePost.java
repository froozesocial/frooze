package com.thilojaeggi.frooze;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.thilojaeggi.frooze.Adaptor.GridPostsAdapter;
import com.thilojaeggi.frooze.Adaptor.PostAdapter;
import com.thilojaeggi.frooze.Model.Post;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;


public class ShowSinglePost extends Fragment {
    public String hashtag, type, user;
    public int position;
    private PostAdapter postAdapter;
    private List<Post> postLists;
    private List<String> hashtagPostsList;
    LinearLayoutManager linearLayoutManager;
    RecyclerView recyclerView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_singlepost, container, false);
        ImageButton finish = view.findViewById(R.id.finish);
        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction ftransaction = fragmentManager.beginTransaction();
                fragmentManager.popBackStack(null, 0);
                fragmentManager.beginTransaction().commit();
                ftransaction.commit();

            }
        });
        SharedPreferences prefs = getContext().getSharedPreferences("PREFS", MODE_PRIVATE);
        hashtag = prefs.getString("hashtag", "none");
        type = prefs.getString("type", "null");
        user = prefs.getString("user", "null");
        position = prefs.getInt("position", 0);
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL,false);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        postLists = new ArrayList<>();
        postAdapter = new PostAdapter(getContext(), postLists);
        recyclerView.setAdapter(postAdapter);
        PagerSnapHelper helper = new PagerSnapHelper();
        helper.attachToRecyclerView(recyclerView);
        if (type.equals("user")){
            getUserPosts();
        } else if (type.equals("hashtag")){
            getHashtagPosts();
        }

        return view;
    }

    private void getHashtagPosts() {
        hashtagPostsList = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Hashtags")
                .child(hashtag)
                .child("posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                hashtagPostsList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    hashtagPostsList.add(snapshot.getKey());
                }
                readPosts();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void getUserPosts() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                postLists.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Post post = snapshot.getValue(Post.class);
                    if (post.getPublisher() != null && !post.getPublisher().isEmpty() && post.getPublisher().equals(user)){
                        postLists.add(post);
                    }
                }
                postAdapter.notifyDataSetChanged();
                recyclerView.scrollToPosition(recyclerView.getAdapter().getItemCount() - position - 1);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

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
                    System.out.println(post.getPostid());

                    for (String id : hashtagPostsList){
                        if (post.getPostid() != null && !post.getPostid().isEmpty() && post.getPostid().equals(id)) {
                            postLists.add(post);
                        }
                    }
                }
                postAdapter.notifyDataSetChanged();
                recyclerView.scrollToPosition(recyclerView.getAdapter().getItemCount() - position - 1);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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