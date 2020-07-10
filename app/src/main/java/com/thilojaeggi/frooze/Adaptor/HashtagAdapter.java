package com.thilojaeggi.frooze.Adaptor;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.thilojaeggi.frooze.R;
import com.thilojaeggi.frooze.GridView;

import java.util.List;

public class HashtagAdapter extends RecyclerView.Adapter<HashtagAdapter.ViewHolder>{

    private Context mContext;
    private List<String> mHashtag;
    private FirebaseUser firebaseUser;

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
        String hashtag = mHashtag.get(i);
        viewHolder.hashtag.setText("#"+hashtag);
        viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                editor.putString("type", "hashtag");
                editor.putString("hashtag", hashtag);
                editor.apply();
                FragmentTransaction transaction = ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction();
                transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
                transaction.replace(R.id.fragment_container,
                        new GridView()).addToBackStack(null).commit();
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