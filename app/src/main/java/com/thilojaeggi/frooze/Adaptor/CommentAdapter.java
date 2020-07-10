package com.thilojaeggi.frooze.Adaptor;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.thilojaeggi.frooze.MainActivity;
import com.thilojaeggi.frooze.Model.Comment;
import com.thilojaeggi.frooze.Model.User;
import com.thilojaeggi.frooze.ProfileFragment;
import com.thilojaeggi.frooze.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder>{

private Context mContext;
private List<Comment> mComment;
private FirebaseUser firebaseUser;
    private PostAdapter postAdapter;
    ValueEventListener listener;

    public CommentAdapter(Context mContext, List<Comment> mComment) {
        this.mContext = mContext;
        this.mComment = mComment;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.comment_item, viewGroup, false);
        return new CommentAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
    firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    Comment comment = mComment.get(i);
        viewHolder.comment.setText(comment.getComment().trim());
    getUserInfo(viewHolder.image_profile, viewHolder.username, comment.getPublisher());
        postAdapter = new PostAdapter(mContext, null);

    viewHolder.comment.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(mContext, MainActivity.class);
            intent.putExtra("publisherid", comment.getPublisher());
            mContext.startActivity(intent);
        }
    });
        viewHolder.image_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showprofile(comment.getPublisher());
            }
        });
        if (!firebaseUser.getUid().equals(comment.getPublisher())) {
            viewHolder.more.setVisibility(View.GONE);
        }
            viewHolder.more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(mContext, viewHolder.more);
                popup.getMenuInflater().inflate(R.menu.delete, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                            if (firebaseUser.getUid().equals(comment.getPublisher())){
                                FirebaseDatabase.getInstance().getReference().child("Comments").child(comment.getPostid())
                                        .child(comment.getKey()).removeValue();
                            }
                        return true;
                    }
                });
                popup.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mComment.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public CircleImageView image_profile;
        public TextView username, comment;
        ImageButton more;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            more = itemView.findViewById(R.id.commentmore);
            image_profile = itemView.findViewById(R.id.image_profile);
            username = itemView.findViewById(R.id.username);

            comment = itemView.findViewById(R.id.comment);

        }
    }

    private void getUserInfo(CircleImageView imageView, TextView username, String publisherid){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child(publisherid);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
            if (user != null){
                if (user.getId() != null){
                    Glide.with(mContext)
                            .load(user.getImageurl())
                            .into(imageView);
                    username.setText(user.getUsername());
                }
                if (user.getId() == null){
                    user.setUsername("Error");
                    user.setFullname("Error");
                    user.setBio("Error");
                    user.setImageurl("https://firebasestorage.googleapis.com/v0/b/frooze-b2248.appspot.com/o/profileimages%2F8x0BVAdw2VUopobTE0sWVadU0ms1_200x200.jpg?alt=media&token=def2aaff-0a0c-4c60-82b4-65b856c2fe95");
                }
            }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void showprofile(String publisher){
        

        SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
        editor.putString("profileid", publisher);
        editor.apply();
        FragmentTransaction transaction = ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
        transaction.replace(R.id.fragment_container,
                new ProfileFragment()).addToBackStack(null).commit();
    }

}