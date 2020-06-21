package com.thilojaeggi.frooze;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.widget.ImageViewCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.brouding.doubletaplikeview.DoubleTapLikeView;
import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.AppBarLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.MODE_PRIVATE;

public class ShowSinglePost  extends Fragment {
    private SimpleExoPlayer mPlayer;
    public SimpleExoPlayerView player;
    public ImageView profileimg, like, comment, save;
    public TextView usernametv, likes, publishertv, descriptiontv, comments;
    public AppBarLayout dangerous;
    public ImageButton share, open, close;
    public DoubleTapLikeView doubletap;
    ImageButton more;
    public AdView mAdView;
    public CardView buttonscv;
    ValueEventListener likelistener, islikedlistener;
    DatabaseReference likereference;
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_postfrominternet, container, false);
        doubletap = view.findViewById(R.id.doubletap);
        close = view.findViewById(R.id.closecv);
        open = view.findViewById(R.id.opencv);
        buttonscv = view.findViewById(R.id.buttonscv);
        mAdView = view.findViewById(R.id.adView);
        share = view.findViewById(R.id.share);
        profileimg = view.findViewById(R.id.image_profile);
        like = view.findViewById(R.id.like);
        comment = view.findViewById(R.id.comment);
        likes = view.findViewById(R.id.likes);
        dangerous = view.findViewById(R.id.dangerous);
        descriptiontv = view.findViewById(R.id.description);
        comments = view.findViewById(R.id.comments);
        usernametv = view.findViewById(R.id.username);
        player = view.findViewById(R.id.videoplayer);
        more = view.findViewById(R.id.more);
        ImageButton finish = view.findViewById(R.id.finish);
        like.setBackgroundResource(R.drawable.heart_white);

        SharedPreferences prefs = getContext().getSharedPreferences("PREFS", MODE_PRIVATE);
        String postid = prefs.getString("postid", "none");
        String publisher = prefs.getString("publisher", "none");
        String postvideo = prefs.getString("postvideo", "none");
        String description = prefs.getString("description", "none");
        String textcolor = prefs.getString("textcolor", "none");

        if (textcolor.equals("black")){
            finish.setColorFilter(Color.BLACK);
            usernametv.setTextColor(Color.BLACK);
            descriptiontv.setTextColor(Color.BLACK);
        } else {
            finish.setColorFilter(Color.WHITE);
            usernametv.setTextColor(Color.WHITE);
            descriptiontv.setTextColor(Color.WHITE);
        }


        descriptiontv.setText(description);
        if (postid != "Error"){
            nrLikes(likes, postid);
            getComments(postid, comments);
            isLiked(postid, like);
        }else{
            like.setVisibility(View.GONE);
            share.setVisibility(View.GONE);
            comment.setVisibility(View.GONE);
        }
        doubletap.setOnTapListener(new DoubleTapLikeView.OnTapListener() {
            @Override
            public void onDoubleTap(View view) {
                if (like.getTag().equals("like")) {
                    like.setBackgroundResource(R.drawable.heart);
                    FirebaseDatabase.getInstance().getReference().child("Likes").child(postid)
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(true);
                    sendNotification(publisher, postid);
                } else {
                    like.setBackgroundResource(R.drawable.heart_white);
                    FirebaseDatabase.getInstance().getReference().child("Likes").child(postid)
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeValue();
                }
            }

            @Override
            public void onTap() {
                mPlayer.setPlayWhenReady(!mPlayer.getPlayWhenReady());

            }
        });
        if (!postid.isEmpty()){
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts").child(postid);
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()){

                        if (dataSnapshot.child("trendingviewedby").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).exists()) {

                        } else {
                            Integer views =  dataSnapshot.child("trendingviews").getValue(Integer.class);
                            Integer viewed = views + 1;
                            reference.child("trendingviews").setValue(viewed);
                            reference.child("trendingviewedby").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(true);
                        }
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Task<ShortDynamicLink> shortLinkTask = FirebaseDynamicLinks.getInstance().createDynamicLink()
                        .setLink(Uri.parse("https://app.frooze.ch/post/?postid=" + postid))
                        .setDomainUriPrefix("https://app.frooze.ch/link")
                        .setAndroidParameters(new DynamicLink.AndroidParameters.Builder("com.thilojaeggi.frooze")
                                .setFallbackUrl(Uri.parse("https://app.frooze.ch/post/?postid=" + postid))
                                .build()

                        )
                        .setIosParameters(new DynamicLink.IosParameters.Builder("com.thilojaeggi.frooze")
                                .setFallbackUrl(Uri.parse("https://app.frooze.ch/post/?postid=" + postid))
                                .build()
                        )
                        .buildShortDynamicLink()
                        .addOnCompleteListener(new OnCompleteListener<ShortDynamicLink>() {
                            @Override
                            public void onComplete(@NonNull Task<ShortDynamicLink> task) {
                                if (task.isSuccessful()) {
                                    // Short link created
                                    Uri shortLink = task.getResult().getShortLink();
                                    Uri flowchartLink = task.getResult().getPreviewLink();
                                    String username = usernametv.getText().toString();
                                    Intent sendIntent = new Intent();
                                    sendIntent.setAction(Intent.ACTION_SEND);
                                    sendIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.sharetext) + username + ":\n" + shortLink);
                                    sendIntent.setType("text/plain");
                                    Intent shareIntent = Intent.createChooser(sendIntent, null);
                                    startActivity(shareIntent);
                                } else {

                                }
                            }
                        });

            }
        });
        more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(getContext(), more);
                if (publisher.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                    popup.getMenuInflater().inflate(R.menu.delete, popup.getMenu());
                } else {
                    popup.getMenuInflater().inflate(R.menu.post_menu, popup.getMenu());
                }
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.toString().equals("Report")){
                            RequestQueue queue = Volley.newRequestQueue(getContext());
                            String url = "https://maker.ifttt.com/trigger/froozereport/with/key/bX8uNSFbAeoqUKPdfSztoA?value1=https://app.frooze.ch/post/?postid=" + postid;
// Request a string response from the provided URL.
                            StringRequest reportRequest = new StringRequest(Request.Method.POST, url,
                                    new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            Toast.makeText(getContext(), "Successfully reported", Toast.LENGTH_SHORT).show();
                                            // Display the first 500 characters of the response string.
                                        }
                                    }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Toast.makeText(getContext(), "An error occured. Please contact support.", Toast.LENGTH_LONG).show();
                                }
                            });
                            queue.add(reportRequest);

                        } if (item.toString().equals("Delete") && publisher.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                            RequestQueue queue = Volley.newRequestQueue(getContext());
                            String url = "https://maker.ifttt.com/trigger/postdeleted/with/key/bX8uNSFbAeoqUKPdfSztoA?value1="+postid;
// Request a string response from the provided URL.
                            StringRequest deleteRequest = new StringRequest(Request.Method.POST, url,
                                    new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            // Display the first 500 characters of the response string.
                                        }
                                    }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                }
                            });
                            queue.add(deleteRequest);
                            FirebaseDatabase.getInstance().getReference("Posts").child(postid).removeValue();
                            FirebaseDatabase.getInstance().getReference("Comments").child(postid).removeValue();
                            FirebaseDatabase.getInstance().getReference("Likes").child(postid).removeValue();
                            FragmentManager fragmentManager = getFragmentManager();
                            fragmentManager.popBackStack(null, 0);
                        }
                            Toast.makeText(getContext(), item.toString(), Toast.LENGTH_LONG).show();
                        return true;
                    }
                });
                popup.show();

            }
        });
        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPlayer.release();
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.popBackStack(null, 0);
            }
        });

        open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RotateAnimation rotate = new RotateAnimation(0, 180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                rotate.setDuration(200);
                rotate.setInterpolator(new LinearInterpolator());
                ImageButton image = (ImageButton) open;
                image.startAnimation(rotate);
                buttonscv.setVisibility(View.VISIBLE);
                buttonscv.setAlpha(1.0f);
                buttonscv.animate()
                        .translationX(-buttonscv.getWidth())
                        .alpha(1.0f)
                        .setListener(null);
                Runnable r = new Runnable() {
                    @Override
                    public void run() {
                        open.setVisibility(View.GONE);
                        close.setVisibility(View.VISIBLE);
                    }
                };

                Handler h = new Handler();
                h.postDelayed(r, 200);

            }
        });
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RotateAnimation rotate = new RotateAnimation(0, -180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                rotate.setDuration(200);
                rotate.setInterpolator(new LinearInterpolator());
                ImageButton image = (ImageButton) close;
                image.startAnimation(rotate);
                buttonscv.setVisibility(View.VISIBLE);
                buttonscv.setAlpha(1.0f);
                buttonscv.animate()
                        .translationX(+buttonscv.getWidth() - buttonscv.getWidth())
                        .alpha(1.0f)
                        .setListener(null);
                Runnable r = new Runnable() {
                    @Override
                    public void run() {
                        open.setVisibility(View.VISIBLE);
                        close.setVisibility(View.GONE);
                    }
                };

                Handler h = new Handler();
                h.postDelayed(r, 200);

            }
        });
        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (like.getTag().equals("like")){
                    like.setBackgroundResource(R.drawable.heart);
                    FirebaseDatabase.getInstance().getReference().child("Likes").child(postid)
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(true);
                    sendNotification(publisher, postid);
                } else{
                    like.setBackgroundResource(R.drawable.heart_white);

                    FirebaseDatabase.getInstance().getReference().child("Likes").child(postid)
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeValue();
                }
            }
        });
        comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPlayer.getPlayWhenReady() == true){
                    mPlayer.setPlayWhenReady(false);
                }

                SharedPreferences.Editor editor = getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                editor.putString("postid", postid);
                editor.putString("publisher", postid);
                editor.apply();
               CommentsActivity commentsActivity =
                        CommentsActivity.newInstance();
                commentsActivity.show(((FragmentActivity)getContext()).getSupportFragmentManager(),
                        "comments");
            }
        });
        profileimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (publisher != "Error"){
                SharedPreferences.Editor editor = getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                editor.putString("profileid", publisher);

                editor.apply();
                FragmentTransaction transaction = ((FragmentActivity)getContext()).getSupportFragmentManager().beginTransaction();
                transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
                transaction.replace(R.id.fragment_container,
                        new ProfileFragment()).addToBackStack(null).commit();
            }
            }
        });
        if (publisher != "Error"){


        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(publisher).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String username = dataSnapshot.child("username").getValue(String.class);
                String imageurl = dataSnapshot.child("imageurl").getValue(String.class);

                usernametv.setText(username);
                Glide.with(getContext())
                        .load(imageurl)
                        .into(profileimg);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                throw databaseError.toException();
            }
        });
        }
        loadvideo(postvideo);
        return view;
    }

    private void loadvideo(String postvideo) {
        Uri uri = Uri.parse(postvideo);
        Handler mainHandler = new Handler();
        player.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory =
                new AdaptiveTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector =
                new DefaultTrackSelector(videoTrackSelectionFactory);
        mPlayer = ExoPlayerFactory.newSimpleInstance(getContext(), trackSelector);
        mPlayer.setRepeatMode(Player.REPEAT_MODE_ALL);
        player.setUseController(false);
        player.setPlayer(mPlayer);
        DefaultBandwidthMeter defaultBandwidthMeter = new DefaultBandwidthMeter();
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(getContext(),
                Util.getUserAgent(getContext(), "frooze"),
                defaultBandwidthMeter);
        MediaSource videoSource = new HlsMediaSource.Factory(dataSourceFactory).createMediaSource(uri);
        mPlayer.prepare(videoSource);
        mPlayer.setPlayWhenReady(true);
    }

    private void getComments(String postid, TextView comments){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Comments").child(postid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                comments.setText(dataSnapshot.getChildrenCount()+"");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void nrLikes(TextView likes, String postid) {
        likereference = FirebaseDatabase.getInstance().getReference()
                .child("Likes")
                .child(postid);
        likelistener = likereference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                likes.setText(dataSnapshot.getChildrenCount()+"");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private void isLiked(String postid, ImageView imageView){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        likereference = FirebaseDatabase.getInstance().getReference()
                .child("Likes")
                .child(postid);
        islikedlistener = likereference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(user.getUid()).exists()){
                    imageView.setTag("liked");
                }else{
                    imageView.setTag("like");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void sendNotification(String userid, String postid){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child(userid);
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("userid", FirebaseAuth.getInstance().getCurrentUser().getUid());
        hashMap.put("text", "likedyourpost");
        hashMap.put("postid", postid);
        hashMap.put("ispost", true);
        reference.push().setValue(hashMap);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mPlayer != null){
            mPlayer.release();
        }
        if (likelistener != null){
            likereference.removeEventListener(likelistener);
        }
        if (islikedlistener != null){
            likereference.removeEventListener(islikedlistener);
        }
    }
    @Override
    public void onDetach(){
        super.onDetach();
        if (likelistener != null){
            likereference.removeEventListener(likelistener);
        }
        if (islikedlistener != null){
            likereference.removeEventListener(islikedlistener);
        }
        if (mPlayer != null){
            mPlayer.release();
        }
    }
}
