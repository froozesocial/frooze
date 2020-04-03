package com.thilojaeggi.frooze.Adaptor;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ShareCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.AppBarLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.thilojaeggi.frooze.Model.Post;
import com.thilojaeggi.frooze.Model.User;
import com.thilojaeggi.frooze.PostActivity;
import com.thilojaeggi.frooze.R;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.util.List;

import cn.jzvd.JZDataSource;
import cn.jzvd.Jzvd;
import cn.jzvd.JzvdStd;
import jp.wasabeef.blurry.Blurry;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder>  {
    public static final String TAG = "AdapterTikTokRecyclerView";
    public Context mContext;
    public List<Post> mPost;
    private FirebaseUser firebaseUser;
    String username = null;

    public PostAdapter(Context mContext, List<Post> mPost) {
        this.mContext = mContext;
        this.mPost = mPost;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.post_item, viewGroup, false);

        return new PostAdapter.ViewHolder(view);


    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        Post post = mPost.get(i);
        JZDataSource jzDataSource = new JZDataSource(post.getPostvideo());
        jzDataSource.looping = true;
        viewHolder.jzvdStd.setUp(jzDataSource,Jzvd.SCREEN_NORMAL);
        viewHolder.jzvdStd.startButton.performClick();
        viewHolder.jzvdStd.progressBar.setVisibility(View.GONE);
        viewHolder.jzvdStd.totalTimeTextView.setVisibility(View.GONE);
        viewHolder.jzvdStd.currentTimeTextView.setVisibility(View.GONE);
        viewHolder.jzvdStd.fullscreenButton.setVisibility(View.GONE);
        viewHolder.jzvdStd.bottomProgressBar.setVisibility(View.GONE);
        viewHolder.share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = viewHolder.username.getText().toString();

                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "Check out this video on frooze.ch by: @" + username + "\n" + post.getPostvideo() );
                sendIntent.setType("text/plain");
                Intent shareIntent = Intent.createChooser(sendIntent, null);
                mContext.startActivity(shareIntent);
            }
        });
        if (post.getDangerous().equals("true")){
            viewHolder.dangerous.setVisibility(View.VISIBLE);
        }
        if (post.getDescription().equals("")){
            viewHolder.description.setVisibility(View.GONE);
        } else {
            viewHolder.description.setVisibility(View.VISIBLE);
            viewHolder.description.setText(post.getDescription());
        }

        publisherInfo(viewHolder.image_profile, viewHolder.username, viewHolder.publisher, post.getPublisher());

    }

    @Override
    public int getItemCount() {
        return mPost.size();

    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        JzvdStd jzvdStd;
        public ImageView image_profile, like, comment, save;
        public TextView username, likes, publisher, description, comments;
        public AppBarLayout dangerous;
        public ImageButton share;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            share = itemView.findViewById(R.id.share);
            //post_video = itemView.findViewById(R.id.post_video);
            image_profile = itemView.findViewById(R.id.image_profile);
            like = itemView.findViewById(R.id.like);
            comment = itemView.findViewById(R.id.comment);
            save = itemView.findViewById(R.id.save);
            likes = itemView.findViewById(R.id.likes);
            dangerous = itemView.findViewById(R.id.dangerous);
           // publisher = itemView.findViewById(R.id.publisher);
            description = itemView.findViewById(R.id.description);
           // comments = itemView.findViewById(R.id.comments);
            username = itemView.findViewById(R.id.username);
            jzvdStd = itemView.findViewById(R.id.videoplayer);

        }
    }

    private void publisherInfo(ImageView image_profile, TextView username, TextView publisher, String userid) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                Glide.with(mContext)
                        .load(user.getImageurl())
                        .into(image_profile);
                        username.setText(user.getUsername());
//                        publisher.setText(user.getUsername());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void clear() {
        mPost.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Post> list) {
        mPost.addAll(list);
        notifyDataSetChanged();
    }
}
