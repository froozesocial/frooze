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
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.thilojaeggi.frooze.Adaptor.GridPostsAdapter;
import com.thilojaeggi.frooze.Model.Post;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;


public class ViewHashtagFragment extends Fragment {
    public String hashtag;
    private GridPostsAdapter postAdapter;
    private List<Post> postLists;
    private List<String> hashtagPostsList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_viewhashtag, container, false);

        ImageButton backbutton = view.findViewById(R.id.backbutton);
        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction ftransaction = fragmentManager.beginTransaction();
                ftransaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);
                fragmentManager.popBackStack(null, 0);
                fragmentManager.beginTransaction().commit();
                ftransaction.commit();

            }
        });
        int mNoOfColumns = Utility.calculateNoOfColumns(getContext(), 130);

        SharedPreferences prefs = getContext().getSharedPreferences("PREFS", MODE_PRIVATE);
        hashtag = prefs.getString("hashtag", "none");
        TextView hashtagtv = view.findViewById(R.id.hashtagname);
        hashtagtv.setText("#"+hashtag);
        RecyclerView recyclerView = view.findViewById(R.id.hashtagvideos);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), mNoOfColumns);
        recyclerView.setLayoutManager(gridLayoutManager);
        postLists = new ArrayList<>();
        postAdapter = new GridPostsAdapter(getContext(), postLists);
        recyclerView.setAdapter(postAdapter);

        getHashtags();
        return view;
    }

    private void getHashtags() {
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
                Collections.reverse(postLists);

                postAdapter.notifyDataSetChanged();
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