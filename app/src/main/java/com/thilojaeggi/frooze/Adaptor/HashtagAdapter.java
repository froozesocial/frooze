package com.thilojaeggi.frooze.Adaptor;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
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
import com.thilojaeggi.frooze.GridView;

import java.util.ArrayList;
import java.util.List;
public class HashtagAdapter extends RecyclerView.Adapter<HashtagAdapter.ViewHolder>{

    private Context mContext;
    private List<String> mHashtag;
    private FirebaseUser firebaseUser;
    private List<Post> postLists;
    private List<String> hashtagPostsList;

    ArrayList<String> count;
    public HashtagAdapter(Context mContext, List<String> mHashtag) {
        this.mContext = mContext;
        this.mHashtag = mHashtag;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.hashtag_item, viewGroup, false);
        return new HashtagAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        count = new ArrayList<String>();
        String hashtag = mHashtag.get(i);
        viewHolder.hashtag.setText("#"+hashtag);
        viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPosts(hashtag);
            }
        });
        viewHolder.forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPosts(hashtag);
            }
        });
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Hashtags")
                .child(hashtag)
                .child("posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                count.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                        count.add("wtf");
                }
                viewHolder.postcount.setText(String.valueOf(count.size()) + " posts");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void showPosts(String hashtag){
        SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
        editor.putString("type", "hashtag");
        editor.putString("hashtag", hashtag);
        editor.apply();
        FragmentTransaction transaction = ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
        transaction.replace(R.id.fragment_container,
                new GridView()).addToBackStack(null).commit();
    }
    @Override
    public int getItemCount() {
        return mHashtag.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView hashtag, postcount;
        public CardView cardView;
        RecyclerView preview_rv;
        ImageButton forward;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardview);
            postcount = itemView.findViewById(R.id.postcount);
            forward = itemView.findViewById(R.id.forwardbutton);
            hashtag = itemView.findViewById(R.id.hashtag);
        }
    }


}