package com.thilojaeggi.frooze.Adaptor;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.ShareCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.AdaptiveMediaSourceEventListener;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.material.appbar.AppBarLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.thilojaeggi.frooze.Model.Post;
import com.thilojaeggi.frooze.Model.User;
import com.thilojaeggi.frooze.PostActivity;
import com.thilojaeggi.frooze.R;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cn.jzvd.JZDataSource;
import cn.jzvd.Jzvd;
import cn.jzvd.JzvdStd;
import jp.wasabeef.blurry.Blurry;

import static android.widget.ListPopupWindow.MATCH_PARENT;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> implements ExoPlayer.EventListener {
    public static final String TAG = "AdapterTikTokRecyclerView";
    public Context mContext;
    public List<Post> mPost;
    private FirebaseUser firebaseUser;
    String username = null;
    private SimpleExoPlayer mPlayer;

    public PostAdapter(Context mContext, List<Post> mPost) {
        this.mContext = mContext;
        this.mPost = mPost;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.post_item, viewGroup, false);

        return new PostAdapter.ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        Post post = mPost.get(i);
        //URL of the video to stream

        viewHolder.open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RotateAnimation rotate = new RotateAnimation(0, 180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                rotate.setDuration(200);
                rotate.setInterpolator(new LinearInterpolator());
                ImageButton image= (ImageButton) viewHolder.open;
                image.startAnimation(rotate);
                viewHolder.buttonscv.setVisibility(View.VISIBLE);
                viewHolder.buttonscv.setAlpha(1.0f);
                viewHolder.buttonscv.animate()
                        .translationX(- viewHolder.buttonscv.getWidth())
                        .alpha(1.0f)
                        .setListener(null);
                Runnable r = new Runnable() {
                    @Override
                    public void run(){
                        viewHolder.open.setVisibility(View.GONE);
                        viewHolder.close.setVisibility(View.VISIBLE);
                    }
                };

                Handler h = new Handler();
                h.postDelayed(r, 200);

            }
        });
        viewHolder.close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RotateAnimation rotate = new RotateAnimation(0, -180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                rotate.setDuration(200);
                rotate.setInterpolator(new LinearInterpolator());
                ImageButton image= (ImageButton) viewHolder.close;
                image.startAnimation(rotate);
                viewHolder.buttonscv.setVisibility(View.VISIBLE);
                viewHolder.buttonscv.setAlpha(1.0f);
                viewHolder.buttonscv.animate()
                        .translationX(+ viewHolder.buttonscv.getWidth() - viewHolder.buttonscv.getWidth())
                        .alpha(1.0f)
                        .setListener(null);
                Runnable r = new Runnable() {
                    @Override
                    public void run(){
                        viewHolder.open.setVisibility(View.VISIBLE);
                        viewHolder.close.setVisibility(View.GONE);
                    }
                };

                Handler h = new Handler();
                h.postDelayed(r, 200);

            }
        });
         Uri uri = Uri.parse(post.getPostvideo());

        // Handler for the video player
        Handler mainHandler = new Handler();
        viewHolder.player.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_ZOOM);
	/* A TrackSelector that selects tracks provided by the MediaSource to be consumed by each of the available Renderers.
	  A TrackSelector is injected when the player is created. */
          BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
          TrackSelection.Factory videoTrackSelectionFactory =
        new AdaptiveTrackSelection.Factory(bandwidthMeter);
         TrackSelector trackSelector =
               new DefaultTrackSelector(videoTrackSelectionFactory);

        // Create the player with previously created TrackSelector
        mPlayer = ExoPlayerFactory.newSimpleInstance(mContext, trackSelector);
        mPlayer.setRepeatMode(Player.REPEAT_MODE_ALL);
     //    Load the default controller
        viewHolder.player.setUseController(false);
    //     Load the SimpleExoPlayerView with the created player
         viewHolder.player.setPlayer(mPlayer);

        // Measures bandwidth during playback. Can be null if not required.
             DefaultBandwidthMeter defaultBandwidthMeter = new DefaultBandwidthMeter();

        // Produces DataSource instances through which media data is loaded.
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(mContext,
        Util.getUserAgent(mContext, "frooze"),
        defaultBandwidthMeter);



        // This is the MediaSource representing the media to be played.
         MediaSource videoSource = new HlsMediaSource.Factory(dataSourceFactory).createMediaSource(uri);

        // Prepare the player with the source.
       mPlayer.prepare(videoSource);

        // Autoplay the video when the player is ready
        mPlayer.setPlayWhenReady(true);




        AdRequest adRequest = new AdRequest.Builder().build();
        viewHolder.mAdView.loadAd(adRequest);
        viewHolder.share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = viewHolder.username.getText().toString();

                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "Check out this video on frooze.ch by @" + username + "\n" + ": " + "https://frooze.ch/post.html?postid=" + post.getPostid());
                sendIntent.setType("text/plain");
                Intent shareIntent = Intent.createChooser(sendIntent, null);
                mContext.startActivity(shareIntent);
            }
        });
        if (post.getDangerous().equals("true")){
            viewHolder.dangerous.setVisibility(View.VISIBLE);
        }
        if (post.getDescription().equals("")){
            viewHolder.description.setVisibility(View.GONE);
        } else {
            viewHolder.description.setVisibility(View.VISIBLE);
            viewHolder.description.setText(post.getDescription());
        }

        publisherInfo(viewHolder.image_profile, viewHolder.username, viewHolder.publisher, post.getPublisher());

    }

    @Override
    public int getItemCount() {
        return mPost.size();

    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public SimpleExoPlayerView player;
        public ImageView image_profile, like, comment, save;
        public TextView username, likes, publisher, description, comments;
        public AppBarLayout dangerous;
        public ImageButton share, open, close;
        public AdView mAdView;
        public CardView buttonscv;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            close = itemView.findViewById(R.id.closecv);
            open = itemView.findViewById(R.id.opencv);
            buttonscv = itemView.findViewById(R.id.buttonscv);
            mAdView = itemView.findViewById(R.id.adView);
            share = itemView.findViewById(R.id.share);
            //post_video = itemView.findViewById(R.id.post_video);
            image_profile = itemView.findViewById(R.id.image_profile);
            like = itemView.findViewById(R.id.like);
            comment = itemView.findViewById(R.id.comment);
     //       save = itemView.findViewById(R.id.save);
      //      likes = itemView.findViewById(R.id.likes);
            dangerous = itemView.findViewById(R.id.dangerous);
           // publisher = itemView.findViewById(R.id.publisher);
            description = itemView.findViewById(R.id.description);
           // comments = itemView.findViewById(R.id.comments);
            username = itemView.findViewById(R.id.username);
            player = itemView.findViewById(R.id.videoplayer);

        }
    }

    private void publisherInfo(ImageView image_profile, TextView username, TextView publisher, String userid) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                Glide.with(mContext)
                        .load(user.getImageurl())
                        .into(image_profile);
                        username.setText(user.getUsername());
//                        publisher.setText(user.getUsername());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void clear() {
        mPost.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Post> list) {
        mPost.addAll(list);
        notifyDataSetChanged();
    }
}
