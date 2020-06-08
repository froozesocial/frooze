package com.thilojaeggi.frooze.Adaptor;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.thilojaeggi.frooze.Model.Post;
import com.thilojaeggi.frooze.R;
import com.thilojaeggi.frooze.ShowSinglePost;

import java.util.List;


public class GridPostsAdapter extends RecyclerView.Adapter<GridPostsAdapter.ViewHolder>{
    public Context mContext;
    public List<Post> mPost;
    private FirebaseUser firebaseUser;
    String postvideo, postid, publisher, description;
    String thumbnailurl;
    public GridPostsAdapter(Context mContext, List<Post> mPost) {
        this.mContext = mContext;
        this.mPost = mPost;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.hashtag_post, viewGroup, false);
        return new GridPostsAdapter.ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        Post post = mPost.get(i);
        if (post.getPostvideo() != null && !post.getPostvideo().isEmpty()){
            thumbnailurl = post.getPostvideo().replace("m3u8","jpg");
        } else {
            thumbnailurl = "https://res.cloudinary.com/frooze/video/upload/paiiyo6qjfca5bktzhuc.jpg";

        }
        RoundingParams roundingParams = RoundingParams.fromCornersRadius(5f);
        roundingParams.setBorder(mContext.getResources().getColor(R.color.transparent), 0f);
        viewHolder.thumbnail.setHierarchy(new GenericDraweeHierarchyBuilder(mContext.getResources())
                .setRoundingParams(roundingParams)
                .build());
        viewHolder.thumbnail.setImageURI(Uri.parse(thumbnailurl));
        System.out.println(post.getPostid());
        viewHolder.cardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (post.getPostvideo() != null && !post.getPostvideo().isEmpty()){
                    postvideo = post.getPostvideo();
                } else {
                    postvideo = "https://res.cloudinary.com/frooze/video/upload/paiiyo6qjfca5bktzhuc.m3u8";
                }
                if (post.getPublisher() != null && !post.getPublisher().isEmpty()){
                    publisher = post.getPublisher();
                } else {
                    publisher ="Error";
                }
                if (post.getPostid() != null && !post.getPostid().isEmpty()){
                    postid = post.getPostid();
                } else {
                    postid ="Error";
                }
                if (post.getDescription() != null && !post.getDescription().isEmpty()){
                    description = post.getDescription();
                } else {
                    description ="Error";
                }
                SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                editor.putString("postid", postid);
                editor.putString("publisher", publisher);
                editor.putString("postvideo", postvideo);
                editor.putString("description", description);
                editor.apply();
                FragmentTransaction transaction = ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction();
                transaction.setCustomAnimations(R.anim.slide_in_bottom,
                        R.anim.slide_out_top,
                        R.anim.slide_in_top,
                        R.anim.slide_out_bottom);
                transaction.replace(R.id.fragment_container,
                        new ShowSinglePost()).addToBackStack(null).commit();
            }
        });

    }


    @Override
    public int getItemCount() {
        return mPost.size();

    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        CardView cardview;
        SimpleDraweeView thumbnail;
        ViewHolder(View itemView) {
            super(itemView);
            cardview = itemView.findViewById(R.id.cardview);
            thumbnail = itemView.findViewById(R.id.thumbnail);

        }
    }


    public void clear() {
        mPost.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<Post> list) {
        mPost.addAll(list);
        notifyDataSetChanged();
    }
}
