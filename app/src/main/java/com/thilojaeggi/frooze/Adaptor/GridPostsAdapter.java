package com.thilojaeggi.frooze.Adaptor;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
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
import com.thilojaeggi.frooze.Model.Post;
import com.thilojaeggi.frooze.Model.User;
import com.thilojaeggi.frooze.R;
import com.thilojaeggi.frooze.ShowSinglePost;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.MODE_PRIVATE;


public class GridPostsAdapter extends RecyclerView.Adapter<GridPostsAdapter.ViewHolder>{
    public Context mContext;
    public List<Post> mPost;
    private FirebaseUser firebaseUser;
    String postvideo, postid, publisher, description, textcolor;
    String thumbnailurl;
    SharedPreferences prefs;
    public GridPostsAdapter(Context mContext, List<Post> mPost) {
        this.mContext = mContext;
        this.mPost = mPost;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.grid_item, viewGroup, false);
        return new GridPostsAdapter.ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        Post post = mPost.get(i);
        if (post.getPostvideo() != null && !post.getPostvideo().isEmpty()){
            String thumbnailgif = post.getPostvideo().replace("m3u8","jpg");
            thumbnailurl = thumbnailgif.replace("/video/upload/", "/video/upload/q_30/");
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

                SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", MODE_PRIVATE).edit();
                editor.putInt("position", viewHolder.getAdapterPosition());
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
        prefs = mContext.getSharedPreferences("PREFS", MODE_PRIVATE);
        String type = prefs.getString("type", "null");
        if (!type.equals("user")){
            publisherInfo(viewHolder.profile, viewHolder.username, post.getPublisher());
            if (post.getTextColor().equals("black")){
                viewHolder.username.setTextColor(mContext.getResources().getColor(R.color.black));
            }
        }
    }
    private void publisherInfo(ImageView image_profile, TextView username, String userid) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user != null) {
                    if (user.getImageurl() != null && !user.getImageurl().isEmpty()) {
                        Glide.with(mContext)
                                .load(user.getImageurl())
                                .into(image_profile);
                    } else {
                        Toast.makeText(mContext, "Error", Toast.LENGTH_SHORT).show();

                    }
                    if (user.getUsername() != null && !user.getUsername().isEmpty()) {
                        username.setText(user.getUsername());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return mPost.size();

    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        CardView cardview;
        TextView username;
        CircleImageView profile;
        SimpleDraweeView thumbnail;
        ViewHolder(View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.username);
            cardview = itemView.findViewById(R.id.cardview);
            thumbnail = itemView.findViewById(R.id.thumbnail);
            profile = itemView.findViewById(R.id.profile);
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
