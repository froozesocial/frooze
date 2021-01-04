package com.thilojaeggi.frooze.Adaptor;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.thilojaeggi.frooze.MainActivity;
import com.thilojaeggi.frooze.Model.User;
import com.thilojaeggi.frooze.ProfileFragment;
import com.thilojaeggi.frooze.R;

import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<User> mUsers;
    private FirebaseUser firebaseUser;
    public DatabaseReference reference;
    public ValueEventListener listener;
    private static final int LIST_AD_DELTA = 3;
    private static final int CONTENT = 0;
    private static final int AD = 1;
    public UserAdapter(Context mContext, List<User> mUsers) {
        this.mContext = mContext;
        this.mUsers = mUsers;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
      return new UserViewHolder(LayoutInflater.from(mContext).inflate(R.layout.user_item, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        holder.setIsRecyclable(false);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            final User user = mUsers.get(position);
        UserViewHolder viewHolder = (UserViewHolder) holder;
        if (user.getId() != null && user.getId().equals("") && user.getImageurl() != null && user.getImageurl().equals("") && user.getBio() != null && user.getBio().equals("") && user.getFullname() != null && user.getFullname().equals("") && user.getUsername() != null && user.getUsername().equals("")){
            viewHolder.btn_follow.setVisibility(View.GONE);
            viewHolder.fullname.setVisibility(View.GONE);
            viewHolder.username.setVisibility(View.GONE);
            viewHolder.image_profile.setVisibility(View.GONE);
        }
        if (user.getId() != null && !user.getId().isEmpty()){
            isFollowing(user.getId(), viewHolder.btn_follow);
            
        }
            viewHolder.btn_follow.setVisibility(View.VISIBLE);
            if (user.getUsername() != null) {
                viewHolder.username.setText(
                        user.getUsername());
            } else {
                viewHolder.username.setText("Error");
            }

            if (user.getFullname() != null) {
                viewHolder.fullname.setText(user.getFullname());
            } else {
                viewHolder.fullname.setText("Error");
            }
            if (user.getImageurl() != null) {
                Glide.with(mContext)
                        .load(user.getImageurl())
                        .into(viewHolder.image_profile);
            } else {
            }

            if (user.getId() != null) {
                if (user.getId().equals(firebaseUser.getUid())) {
                    viewHolder.btn_follow.setVisibility(View.GONE);
                }
            } else {
                viewHolder.btn_follow.setVisibility(View.GONE);
            }

            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (user.getId() != null) {
                        SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                        editor.putString("profileid", user.getId());
                        editor.apply();
                        FragmentTransaction transaction = ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction();
                        transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
                        transaction.replace(R.id.fragment_container,
                                new ProfileFragment()).addToBackStack(null).commit();
                    } else {
                        Toast.makeText(mContext, "Error", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            viewHolder.btn_follow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (viewHolder.btn_follow.getText().toString().equals("Follow")) {
                        FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                                .child("following").child(user.getId()).setValue(true);
                        FirebaseDatabase.getInstance().getReference().child("Follow").child(user.getId())
                                .child("followers").child(firebaseUser.getUid()).setValue(true);
                        sendNotification(user.getId());
                        isFollowing(user.getId(), viewHolder.btn_follow);
                    } else {
                        FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                                .child("following").child(user.getId()).removeValue();
                        FirebaseDatabase.getInstance().getReference().child("Follow").child(user.getId())
                                .child("followers").child(firebaseUser.getUid()).removeValue();
                        isFollowing(user.getId(), viewHolder.btn_follow);

                    }
                }

            });

    }
    @Override
    public void onViewDetachedFromWindow(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        if (reference != null && listener != null) {
            reference.removeEventListener(listener);
        }
    }


    @Override
    public void onViewAttachedToWindow(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
       UserViewHolder viewHolder = (UserViewHolder) holder;

        final User user = mUsers.get(holder.getAdapterPosition());
        if (user.getId() != null){

            isFollowing(user.getId(), viewHolder.btn_follow);
}
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class UserViewHolder extends RecyclerView.ViewHolder{

        public TextView username;
        public TextView fullname;
        public CircleImageView image_profile;
        public Button btn_follow;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.username);
            fullname = itemView.findViewById(R.id.fullname);
            image_profile = itemView.findViewById(R.id.image_profile);
            btn_follow = itemView.findViewById(R.id.btn_follow);

        }
    }

    private void sendNotification(String userid){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child(userid);
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("userid", firebaseUser.getUid());
        hashMap.put("text", "followingyou");
        hashMap.put("postid", "");
        hashMap.put("ispost", false);
        reference.push().setValue(hashMap);
    }
    private void isFollowing(String userid, Button button) {
        reference = FirebaseDatabase.getInstance().getReference()
                .child("Follow").child(firebaseUser.getUid()).child("following");
        listener = reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(userid).exists()){
                    button.setText("Unfollow");
                    button.setTextSize(17);
                    button.setTextAppearance(mContext, Typeface.BOLD);
                    button.setTextColor(button.getResources().getColor(R.color.dark_blue));
                    button.setPadding(0,0,0,5);
                    button.setBackgroundColor(button.getResources().getColor(R.color.white));
                    button.setShadowLayer(0,0,0,0);
                } else {
                    button.setText("Follow");
                    button.setTextSize(17);
                    button.setTextAppearance(mContext, Typeface.BOLD);
                    button.setPadding(0,0,0,5);
                    button.setTextColor(button.getResources().getColor(R.color.white));
                    button.setBackgroundColor(button.getResources().getColor(R.color.dark_blue));
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        if (reference != null && listener != null) {
            reference.removeEventListener(listener);
        }
    }
}
