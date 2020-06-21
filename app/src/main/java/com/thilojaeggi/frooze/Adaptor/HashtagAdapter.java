package com.thilojaeggi.frooze.Adaptor;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
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
import com.thilojaeggi.frooze.Model.Hashtag;
import com.thilojaeggi.frooze.Model.User;
import com.thilojaeggi.frooze.ProfileFragment;
import com.thilojaeggi.frooze.R;
import com.thilojaeggi.frooze.ViewHashtagFragment;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class HashtagAdapter extends RecyclerView.Adapter<HashtagAdapter.ViewHolder>{

    private Context mContext;
    private List<Hashtag> mHashtag;
    private FirebaseUser firebaseUser;

    public HashtagAdapter(Context mContext, List<Hashtag> mHashtag) {
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
        Hashtag hashtag = mHashtag.get(i);
        viewHolder.hashtag.setText("#"+hashtag.getHashtag());
        viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                editor.putString("hashtag", hashtag.getHashtag());
                editor.apply();
                FragmentTransaction transaction = ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction();
                transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
                transaction.replace(R.id.fragment_container,
                        new ViewHashtagFragment()).addToBackStack(null).commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mHashtag.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView hashtag;
        public CardView cardView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardview);
            hashtag = itemView.findViewById(R.id.hashtag);
        }
    }


}