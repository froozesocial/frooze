package com.thilojaeggi.frooze;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.thilojaeggi.frooze.Adaptor.GridPostsAdapter;
import com.thilojaeggi.frooze.Model.Post;
import com.thilojaeggi.frooze.Model.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

public class ProfileFragment extends Fragment {

    CircleImageView profileimage;
    public Button followbutton;
    public String profileid;
    private String m_Text = "";
    public Uri imageuri;
    private GridPostsAdapter gridPostsAdapter;
    private RecyclerView recyclerView;
    AppCompatButton backbutton;
    FrameLayout profileframe;
    public TextView biotv, fullnametv, followers, following, posts;
    public FirebaseUser firebaseUser;
    public ValueEventListener followlistener;
    public DatabaseReference reference;
    public ArrayList postList;
    private static final int EDIT_PROFILE = 1991;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        String result;
        following = view.findViewById(R.id.following);
        recyclerView = view.findViewById(R.id.recyclerview);
        followers = view.findViewById(R.id.followers);
        ImageButton editprofile = view.findViewById(R.id.editprofile);
        ImageButton settingsbutton = view.findViewById(R.id.settings);
        backbutton = view.findViewById(R.id.backbutton);
        posts = view.findViewById(R.id.posts);
        fullnametv = view.findViewById(R.id.fullname);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        followbutton = (Button) view.findViewById(R.id.followprofile);
        SharedPreferences prefs = getContext().getSharedPreferences("PREFS", MODE_PRIVATE);
        profileid = prefs.getString("profileid", "none");
        biotv = view.findViewById(R.id.bio);
        postList = new ArrayList<>();
        recyclerView.setHasFixedSize(true);
        int mNoOfColumns = Utility.calculateNoOfColumns(getContext(), 130);
        LinearLayoutManager mLayoutManager = new GridLayoutManager(getContext(), mNoOfColumns);
        recyclerView.setLayoutManager(mLayoutManager);
        gridPostsAdapter = new GridPostsAdapter(getContext(), postList);
        profileframe = view.findViewById(R.id.profileframe);
        if (profileid.equals("n4nLAkT3lIfe8zdqcqI3XYpknQd2")){
            profileframe.setBackgroundResource(R.drawable.profileframe_special);
        }
        recyclerView.setAdapter(gridPostsAdapter);
        if (profileid.equals(firebaseUser.getUid())) {
            followbutton.setVisibility(View.GONE);
        } else {
            editprofile.setVisibility(View.GONE);
            backbutton.setVisibility(View.VISIBLE);
            settingsbutton.setVisibility(View.GONE);
            followbutton.setVisibility(View.VISIBLE);
            checkFollow();
        }
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
            reference.child(profileid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    profileimage = (CircleImageView) view.findViewById(R.id.profile_image);
                    if (user != null && user.getImageurl() != null && !user.getImageurl().isEmpty()){
                        imageuri = Uri.parse(user.getImageurl());
                        Glide.with(view).load(imageuri).into(profileimage);
                    }

                    if (!user.getBio().isEmpty()) {
                        biotv.setText(user.getBio());
                    }

                    TextView usernametv = view.findViewById(R.id.username);
                    usernametv.setText("@" + user.getUsername());
                    fullnametv.setText(user.getFullname());
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    throw databaseError.toException();
                }
            });
        }
        followbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(followbutton.getText().toString().equals("follow")){
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                            .child("following").child(profileid).setValue(true);
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(profileid)
                            .child("followers").child(firebaseUser.getUid()).setValue(true);
                    sendNotification();
                } else{
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                            .child("following").child(profileid).removeValue();
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(profileid)
                            .child("followers").child(firebaseUser.getUid()).removeValue();
                }
            }
        });
        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.popBackStack(null, 0);
            }
        });
        settingsbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentsettings = new Intent(getActivity(), SettingsActivity.class);
                startActivity(intentsettings);
            }
        });
            editprofile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent editprofileintent = new Intent(getActivity(), EditProfileActivity.class);
                    editprofileintent.putExtra("name", fullnametv.getText().toString());
                    editprofileintent.putExtra("bio", biotv.getText().toString());
                    editprofileintent.putExtra("oldimage", imageuri);
                    startActivityForResult(editprofileintent, EDIT_PROFILE);
                }
            });

        getFollowers();
        getNrPosts();
        getPosts();
        return view;
    }
    private void getNrPosts(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int i = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Post post = snapshot.getValue(Post.class);
                    if (post != null && post.getPublisher() != null && !post.getPublisher().isEmpty() && post.getPublisher().equals(profileid)){
                        i++;
                    }
                }
                posts.setText(""+i);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private void getFollowers(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Follow").child(profileid).child("followers");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                followers.setText(""+dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Follow").child(profileid).child("following");
        reference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                following.setText(""+dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private void getPosts() {
        SharedPreferences.Editor editor = getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
        editor.putString("type", "user");
        editor.putString("user", profileid);
        editor.apply();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                postList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Post post = snapshot.getValue(Post.class);
                    if (post != null && post.getPublisher() != null && !post.getPublisher().isEmpty() && post.getPublisher().equals(profileid)){
                        postList.add(post);
                    }
                }
                Collections.reverse(postList);
                gridPostsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private void checkFollow(){
        reference = FirebaseDatabase.getInstance().getReference()
                .child("Follow").child(firebaseUser.getUid()).child("following");
        followlistener = reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (profileid != null && !profileid.isEmpty() && dataSnapshot.child(profileid).exists()){
                    followbutton.setText("following");
                } else{
                    followbutton.setText("follow");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK){
            if (requestCode == EDIT_PROFILE) {
                String bio = data.getStringExtra("bio");
                String name =  data.getStringExtra("fullname");
                String image = data.getStringExtra("image");
                if (image != null && !image.isEmpty()){
                    Glide.with(getContext()).load(Uri.parse(image)).into(profileimage);
                }
                biotv.setText(bio);
                fullnametv.setText(name);
            }
        }
    }

    private void sendNotification(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child(profileid);

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("userid", firebaseUser.getUid());
        hashMap.put("text", "followingyou");
        hashMap.put("postid", "");
        hashMap.put("ispost", false);
        reference.push().setValue(hashMap);
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }



    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        profileid = null;
    }
    @Override
    public void onDetach(){
        super.onDetach();
        profileid = null;
        if (followlistener != null){
            reference.removeEventListener(followlistener);
        }
    }

}