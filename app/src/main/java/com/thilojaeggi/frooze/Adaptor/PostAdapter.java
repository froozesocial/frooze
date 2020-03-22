package com.thilojaeggi.frooze.Adaptor;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.thilojaeggi.frooze.Model.Post;
import com.thilojaeggi.frooze.Model.User;
import com.thilojaeggi.frooze.R;
import com.thilojaeggi.frooze.VideoConstant;

import java.util.List;

import cn.jzvd.JZDataSource;
import cn.jzvd.Jzvd;
import cn.jzvd.JzvdStd;

import static com.parse.Parse.getApplicationContext;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder>  {
    public static final String TAG = "AdapterTikTokRecyclerView";
    private Context context;
    int[] videoIndexs = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
    public Context mContext;
    public List<Post> mPost;


    private FirebaseUser firebaseUser;

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

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            //post_video = itemView.findViewById(R.id.post_video);
            image_profile = itemView.findViewById(R.id.image_profile);
            like = itemView.findViewById(R.id.like);
            comment = itemView.findViewById(R.id.comment);
            save = itemView.findViewById(R.id.save);
            likes = itemView.findViewById(R.id.likes);
           // publisher = itemView.findViewById(R.id.publisher);
            description = itemView.findViewById(R.id.description);
            comments = itemView.findViewById(R.id.comments);
            username = itemView.findViewById(R.id.username);
            jzvdStd = itemView.findViewById(R.id.videoplayer);

        }
    }

    private void publisherInfo(ImageView image_profile, TextView username, TextView publisher, String userid) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);

        reference.addValueEventListener(new ValueEventListener() {
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
}
