package com.thilojaeggi.frooze;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostFromLink extends AppCompatActivity {
    private SimpleExoPlayer mPlayer;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_postfrominternet);
        TextView usernametv = findViewById(R.id.username);
        TextView descriptiontv = findViewById(R.id.description);
        SimpleExoPlayerView player = findViewById(R.id.videoplayer);
        CircleImageView profileimg = findViewById(R.id.image_profile);
        CardView buttonscv = findViewById(R.id.buttonscv);
        ImageButton open = findViewById(R.id.opencv);
        ImageButton close = findViewById(R.id.closecv);
        ImageButton finish = findViewById(R.id.finish);
        ImageButton share = findViewById(R.id.share);

        Intent intent = getIntent();
        String id = intent.getStringExtra("postid");
        String[] segments = id.split("=");
        String postid = segments[segments.length - 1];
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernametv.getText().toString();
                DynamicLink dynamicLink = FirebaseDynamicLinks.getInstance().createDynamicLink()
                        .setLink(Uri.parse("https://app.frooze.ch/post/?postid="+postid))
                        .setDomainUriPrefix("https://froozepost.page.link")
                        // Open links with this app on Android
                        .setAndroidParameters(new DynamicLink.AndroidParameters.Builder("com.thilojaeggi.frooze")
                                .setFallbackUrl(Uri.parse("https://app.frooze.ch/post/?postid="+postid))
                                .build()

                        )

                        // Open links with com.example.ios on iOS
                        .setIosParameters(new DynamicLink.IosParameters.Builder("com.thilojaeggi.frooze").build())
                        .buildDynamicLink();

                Uri dynamicLinkUri = dynamicLink.getUri();
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "Check out this video on frooze.ch by @" + username + ":\n" + dynamicLinkUri);
                sendIntent.setType("text/plain");
                Intent shareIntent = Intent.createChooser(sendIntent, null);
                startActivity(shareIntent);

            }
            });
        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RotateAnimation rotate = new RotateAnimation(0, 180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                rotate.setDuration(200);
                rotate.setInterpolator(new LinearInterpolator());
                ImageButton image= (ImageButton) open;
                image.startAnimation(rotate);
                buttonscv.setVisibility(View.VISIBLE);
                buttonscv.setAlpha(1.0f);
                buttonscv.animate()
                        .translationX(- buttonscv.getWidth())
                        .alpha(1.0f)
                        .setListener(null);
                Runnable r = new Runnable() {
                    @Override
                    public void run(){
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
                ImageButton image= (ImageButton) close;
                image.startAnimation(rotate);
                buttonscv.setVisibility(View.VISIBLE);
                buttonscv.setAlpha(1.0f);
                buttonscv.animate()
                        .translationX(+ buttonscv.getWidth() - buttonscv.getWidth())
                        .alpha(1.0f)
                        .setListener(null);
                Runnable r = new Runnable() {
                    @Override
                    public void run(){
                        open.setVisibility(View.VISIBLE);
                        close.setVisibility(View.GONE);
                    }
                };

                Handler h = new Handler();
                h.postDelayed(r, 200);

            }
        });
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        reference.child(postid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String publisher = dataSnapshot.child("publisher").getValue(String.class);
                String postvideo = dataSnapshot.child("postvideo").getValue(String.class);
                String description = dataSnapshot.child("description").getValue(String.class);
                String dangerous = dataSnapshot.child("dangerous").getValue(String.class);
                descriptiontv.setText(description);

                Uri uri = Uri.parse(postvideo);

                // Handler for the video player
                Handler mainHandler = new Handler();
                player.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_ZOOM);
	/* A TrackSelector that selects tracks provided by the MediaSource to be consumed by each of the available Renderers.
	  A TrackSelector is injected when the player is created. */
                BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
                TrackSelection.Factory videoTrackSelectionFactory =
                        new AdaptiveTrackSelection.Factory(bandwidthMeter);
                TrackSelector trackSelector =
                        new DefaultTrackSelector(videoTrackSelectionFactory);

                // Create the player with previously created TrackSelector
                mPlayer = ExoPlayerFactory.newSimpleInstance(getApplicationContext(), trackSelector);
                mPlayer.setRepeatMode(Player.REPEAT_MODE_ALL);
                //    Load the default controller
                player.setUseController(false);
                //     Load the SimpleExoPlayerView with the created player
                player.setPlayer(mPlayer);

                // Measures bandwidth during playback. Can be null if not required.
                DefaultBandwidthMeter defaultBandwidthMeter = new DefaultBandwidthMeter();

                // Produces DataSource instances through which media data is loaded.
                DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(getApplicationContext(),
                        Util.getUserAgent(getApplicationContext(), "frooze"),
                        defaultBandwidthMeter);



                // This is the MediaSource representing the media to be played.
                MediaSource videoSource = new HlsMediaSource.Factory(dataSourceFactory).createMediaSource(uri);

                // Prepare the player with the source.
                mPlayer.prepare(videoSource);

                // Autoplay the video when the player is ready
                mPlayer.setPlayWhenReady(true);
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
                reference.child(publisher).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String username = dataSnapshot.child("username").getValue(String.class);
                        String imageurl = dataSnapshot.child("imageurl").getValue(String.class);

                        usernametv.setText(username);
                        Glide.with(getApplicationContext())
                                .load(imageurl)
                                .into(profileimg);
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        throw databaseError.toException();
                    }
                });
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                throw databaseError.toException();
            }
        });
    }
    @Override
    public void finish(){
        super.finish();
        mPlayer.release();
        overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_bottom);
    }
}
