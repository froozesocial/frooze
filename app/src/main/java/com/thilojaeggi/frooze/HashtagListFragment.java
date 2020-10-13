package com.thilojaeggi.frooze;

import android.animation.ValueAnimator;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.thilojaeggi.frooze.Adaptor.HashtagAdapter;
import java.util.ArrayList;
import java.util.List;


public class HashtagListFragment extends Fragment {
    private RecyclerView recyclerView;
    private HashtagAdapter hashtagAdapter;
    private List<String> hashtagLists;
    final float startSize = 33;
    final float endSize = 20;
    long animationDuration = 175;
    LinearLayoutManager linearLayoutManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_hashtag, container, false);
        recyclerView = rootView.findViewById(R.id.hashtagrecyclerview);
        recyclerView.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL,false);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        hashtagLists = new ArrayList<String>();
        hashtagAdapter = new HashtagAdapter(getContext(), hashtagLists);
        recyclerView.setAdapter(hashtagAdapter);
        TextView users = rootView.findViewById(R.id.users);

        ValueAnimator animator = ValueAnimator.ofFloat(startSize, endSize);
        animator.setDuration(animationDuration);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float animatedValue = (float) valueAnimator.getAnimatedValue();
                users.setTextSize(animatedValue);
            }
        });
        animator.start();
        users.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment searchfrag= new SearchFragment();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, searchfrag)
                        .addToBackStack(null)
                        .commit();
            }
        });
        EditText search_bar = rootView.findViewById(R.id.search_bar);
        search_bar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String swithouthashtag = s.toString().replaceAll("#","");
                searchHashtags(swithouthashtag.toLowerCase());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        readHashtags();
        AdView mAdView = rootView.findViewById(R.id.adView);
        FirebaseAuth user = FirebaseAuth.getInstance();
        String uid = user.getUid();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String premium = dataSnapshot.child("premium").getValue(String.class);
                if (premium.equals("true")){
                    mAdView.setVisibility(View.GONE);
                }if (premium.equals("false")) {
                    AdRequest adRequest = new AdRequest.Builder().build();
                    mAdView.loadAd(adRequest);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                throw databaseError.toException();
            }
        });


        return rootView;
    }


    private void searchHashtags(String s){
        Query query = FirebaseDatabase.getInstance().getReference("Hashtags").orderByKey()
                .startAt(s)
                .endAt(s+"\uf8ff");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                hashtagLists.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
              //      Hashtag hashtag = snapshot.getValue(Hashtag.class);
                    String hashtagkey = snapshot.getKey().toString();
                    hashtagLists.add(hashtagkey);
                }
                hashtagAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void readHashtags() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Hashtags");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                hashtagLists.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                   // Hashtag hashtag = snapshot.getValue(Hashtag.class);
                    String hashtagkey = snapshot.getKey().toString();
                    hashtagLists.add(hashtagkey);
                }
                hashtagAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }



    public void onViewCreated(View rootView, Bundle savedInstanceState) {
        super.onViewCreated(rootView, savedInstanceState);


    }



    @Override
    public void onDestroyView() {
      super.onDestroyView();
    }
    @Override
    public void onPause() {
        super.onPause();
    }
}