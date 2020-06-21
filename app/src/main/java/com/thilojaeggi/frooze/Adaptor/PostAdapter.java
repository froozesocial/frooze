package com.thilojaeggi.frooze.Adaptor;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ShareCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.brouding.doubletaplikeview.DoubleTapLikeView;
import com.bumptech.glide.Glide;
import com.cloudinary.Cloudinary;
import com.cloudinary.Search;
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
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.cache.Cache;
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor;
import com.google.android.exoplayer2.upstream.cache.SimpleCache;
import com.google.android.exoplayer2.util.Util;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
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
import com.thilojaeggi.frooze.CommentsActivity;
import com.thilojaeggi.frooze.Model.Post;
import com.thilojaeggi.frooze.Model.User;
import com.thilojaeggi.frooze.OnSwipeTouchListener;
import com.thilojaeggi.frooze.PostActivity;
import com.thilojaeggi.frooze.ProfileFragment;
import com.thilojaeggi.frooze.R;
import com.thilojaeggi.frooze.SearchFragment;
import com.thilojaeggi.frooze.ViewHashtagFragment;
import com.volokh.danylo.hashtaghelper.HashTagHelper;

import org.w3c.dom.Text;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import im.ene.toro.CacheManager;
import im.ene.toro.ToroPlayer;
import im.ene.toro.ToroUtil;
import im.ene.toro.exoplayer.Config;
import im.ene.toro.exoplayer.ExoPlayerViewHelper;
import im.ene.toro.exoplayer.MediaSourceBuilder;
import im.ene.toro.media.PlaybackInfo;
import im.ene.toro.widget.Container;


public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> implements ExoPlayer.EventListener {
    public Context mContext;
    public List<Post> mPost;
    private FirebaseUser firebaseUser;
    Uri uri;
    Post post;
    Config config;
    private static long cacheFile = 2 * 2048 * 2048;
    SimpleCache cache;
    DatabaseReference reference;
    ValueEventListener listener, likelistener, nrlikelistener;
    CommentsActivity commentsActivity;
    private SimpleExoPlayer mPlayer;
    private HashTagHelper mTextHashTagHelper;
    private static final int CONTENT_TYPE = 0;
    private static final int AD_TYPE = 1;
    private int[] viewTypes;
    public int viewType;
    DatabaseReference likereference;
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

      /*    viewHolder.setIsRecyclable(true);
        //URL of the video to stream
        if (post.getPostid() != null && !post.getPostid().isEmpty()){
            isLiked(post.getPostid(), viewHolder.like);
            nrLikes(viewHolder.likes, post.getPostid());
            getComments(post.getPostid(), viewHolder.comments);
        } else {
            viewHolder.like.setVisibility(View.GONE);
            viewHolder.comment.setVisibility(View.GONE);
            viewHolder.share.setVisibility(View.GONE);
        }

        viewHolder.doubletap.setOnTapListener(new DoubleTapLikeView.OnTapListener() {
            @Override
            public void onDoubleTap(View view) {
                if (post.getPostid() != null && !post.getPostid().isEmpty()) {
                    if (viewHolder.like.getTag().equals("like")) {
                        FirebaseDatabase.getInstance().getReference().child("Likes").child(post.getPostid())
                                .child(firebaseUser.getUid()).setValue(true);
                    } else {
                        FirebaseDatabase.getInstance().getReference().child("Likes").child(post.getPostid())
                                .child(firebaseUser.getUid()).removeValue();
                    }
                } else {
                    Toast.makeText(mContext, "Error", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onTap() {
                mPlayer.setPlayWhenReady(!mPlayer.getPlayWhenReady());

            }
        });


        viewHolder.image_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPlayer.getPlayWhenReady() == true){
                    mPlayer.setPlayWhenReady(false);
                }
                if (post.getPublisher() != null && !post.getPublisher().isEmpty()) {
                    showprofile(post.getPublisher());
                    }
            else {
                Toast.makeText(mContext, "Error", Toast.LENGTH_SHORT).show();
                }
            }
        });
        viewHolder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    if (viewHolder.like.getTag().equals("like")) {
                        FirebaseDatabase.getInstance().getReference().child("Likes").child(post.getPostid())
                                .child(firebaseUser.getUid()).setValue(true);
                    } else {
                        FirebaseDatabase.getInstance().getReference().child("Likes").child(post.getPostid())
                                .child(firebaseUser.getUid()).removeValue();
                    }
                }
        });
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
        if (post.getPostvideo() != null && !post.getPostvideo().isEmpty()){
            uri = Uri.parse(post.getPostvideo());
        } else {
            uri = Uri.parse("https://res.cloudinary.com/frooze/video/upload/v1591303571/piu9bb4bk0tj0m8yv2us.m3u8");
        }


          BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
          TrackSelection.Factory videoTrackSelectionFactory =
        new AdaptiveTrackSelection.Factory(bandwidthMeter);
         TrackSelector trackSelector =
               new DefaultTrackSelector(videoTrackSelectionFactory);

        // Create the player with previously created TrackSelector
        mPlayer = ExoPlayerFactory.newSimpleInstance(mContext, trackSelector);
        viewHolder.player.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
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



        FirebaseAuth user = FirebaseAuth.getInstance();
        String uid = user.getUid();
        viewHolder.share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Task<ShortDynamicLink> shortLinkTask = FirebaseDynamicLinks.getInstance().createDynamicLink()
                        .setLink(Uri.parse("https://app.frooze.ch/post/?postid="+post.getPostid()))
                        .setDomainUriPrefix("https://app.frooze.ch/link")
                        .setAndroidParameters(new DynamicLink.AndroidParameters.Builder("com.thilojaeggi.frooze")
                                .setFallbackUrl(Uri.parse("https://app.frooze.ch/post/?postid="+post.getPostid()))
                                .build()

                        )
                        .setIosParameters(new DynamicLink.IosParameters.Builder("com.thilojaeggi.frooze")
                                .setFallbackUrl(Uri.parse("https://app.frooze.ch/post/?postid="+post.getPostid()))
                                .build()
                        )
                        .buildShortDynamicLink()
                        .addOnCompleteListener((Activity) mContext, new OnCompleteListener<ShortDynamicLink>() {
                            @Override
                            public void onComplete(@NonNull Task<ShortDynamicLink> task) {
                                if (task.isSuccessful()) {
                                    // Short link created
                                    Uri shortLink = task.getResult().getShortLink();
                                    Uri flowchartLink = task.getResult().getPreviewLink();
                                    String username = viewHolder.username.getText().toString();
                                    Intent sendIntent = new Intent();
                                    sendIntent.setAction(Intent.ACTION_SEND);
                                    sendIntent.putExtra(Intent.EXTRA_TEXT, "Check out this video on frooze.ch by @" + username + ":\n" + shortLink);
                                    sendIntent.setType("text/plain");
                                    Intent shareIntent = Intent.createChooser(sendIntent, null);
                                    mContext.startActivity(shareIntent);
                                } else {

                                }
                            }
                        });

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
            mTextHashTagHelper = HashTagHelper.Creator.create(mContext.getResources().getColor(R.color.colorPrimary), new HashTagHelper.OnHashTagClickListener() {
                @Override
                public void onHashTagClicked(String hashTag) {
                    mPlayer.release();
                    SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                    editor.putString("hashtag", hashTag);
                    editor.apply();
                    FragmentTransaction transaction = ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction();
                    transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left,R.anim.slide_in_left, R.anim.slide_out_right);
                    transaction.replace(R.id.fragment_container,
                            new ViewHashtagFragment()).addToBackStack(null).commit();
                }
            });
            mTextHashTagHelper.handle(viewHolder.description);

        }
        viewHolder.more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder alert = new AlertDialog.Builder(mContext);
                alert.setTitle("");
                alert.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                    } });
                alert.setNeutralButton("Report", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        RequestQueue queue = Volley.newRequestQueue(mContext);
                        String url ="https://maker.ifttt.com/trigger/froozereport/with/key/bX8uNSFbAeoqUKPdfSztoA?value1=https://app.frooze.ch/post/?postid="+post.getPostid();
// Request a string response from the provided URL.
                        StringRequest reportRequest = new StringRequest(Request.Method.POST, url,
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        Toast.makeText(mContext, "Successfully reported", Toast.LENGTH_SHORT).show();
                                        // Display the first 500 characters of the response string.
                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(mContext, "An error occured. Please contact support.", Toast.LENGTH_LONG).show();
                            }
                        });

// Add the request to the RequestQueue.
                        queue.add(reportRequest);
                    }
                });

                alert.show();

            }
        });
        publisherInfo(viewHolder.image_profile, viewHolder.username, viewHolder.publisher, post.getPublisher());
        viewHolder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPlayer.getPlayWhenReady() == true){
                    mPlayer.setPlayWhenReady(false);
                }
                Intent comment = new Intent(mContext, CommentsActivity.class);
                comment.putExtra("postid", post.getPostid());
                comment.putExtra("publisherid", post.getPublisher());
               mContext.startActivity(comment);

            }
        });
        */
    }



    @Override
    public int getItemCount() {
        return mPost.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements ToroPlayer {

        static final int LAYOUT_RES = R.layout.post_item;

        @Nullable ExoPlayerViewHelper helper;
        @Nullable private Uri mediaUri;

        @BindView(R.id.videoplayer)
        PlayerView playerView;
        @BindView(R.id.image_profile)
        ImageView profileimage;
        @BindView(R.id.username)
        TextView username;
        @BindView(R.id.likes)
        TextView likes;
        @BindView(R.id.comments)
        TextView comments;
        @BindView(R.id.description)
        TextView description;
        @BindView(R.id.like)
        ImageView like;
        @BindView(R.id.comment)
        ImageView comment;
        @BindView(R.id.more)
        ImageView more;
        @BindView(R.id.share)
        ImageView share;
        @BindView(R.id.opencv)
        ImageButton open;
        @BindView(R.id.closecv)
        ImageButton close;
        @BindView(R.id.buttonscv)
        CardView buttonscv;
        @BindView(R.id.doubletap)
        DoubleTapLikeView doubletap;
        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }


        // called from Adapter to setup the media
        void bind(@NonNull RecyclerView.Adapter adapter, Uri item, List<Object> payloads) {
            if (item != null) {
                mediaUri = item;
            }
        }

        @NonNull @Override public View getPlayerView() {
            return playerView;
        }

        @NonNull @Override public PlaybackInfo getCurrentPlaybackInfo() {
            return helper != null ? helper.getLatestPlaybackInfo() : new PlaybackInfo();

        }

        @Override
        public void initialize(@NonNull Container container, @Nullable PlaybackInfo playbackInfo) {
           try {

           } catch (Exception e){
            
           }

            config = new Config.Builder().setMediaSourceBuilder(MediaSourceBuilder.LOOPING)
                    .setCache(cache)
                    .build();//this is use for lopping
            post = mPost.get(getPosition());
            publisherInfo(profileimage, username, post.getPublisher());
            if (post.getPostid() != null && !post.getPostid().isEmpty()){
                isLiked(post.getPostid(), like);
                nrLikes(likes, post.getPostid());
                getComments(post.getPostid(), comments);
            } else {
                like.setVisibility(View.GONE);
                comment.setVisibility(View.GONE);
                share.setVisibility(View.GONE);
            }
            if (post.getTextColor() != null && !post.getTextColor().isEmpty()){
                if (post.getTextColor().equals("black")){
                    description.setTextColor(Color.BLACK);
                } else {
                    description.setTextColor(Color.WHITE);
                }
            } else {
                description.setTextColor(Color.WHITE);
            }
            // For trending function
            if (post.getPostid() != null && !post.getPostid().isEmpty()){
            FirebaseDatabase.getInstance().getReference("Posts").child(post.getPostid()).child("trendingviewedby").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(true);
            }
            profileimage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (post.getPublisher() != null && !post.getPublisher().isEmpty()) {
                        pause();

                        showprofile(post.getPublisher());
                    }
                    else {
                        Toast.makeText(mContext, "Error", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            more.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            PopupMenu popup = new PopupMenu(mContext, more);
                                            if (post.getPublisher().equals(firebaseUser.getUid())){
                                                popup.getMenuInflater().inflate(R.menu.delete, popup.getMenu());
                                            } else {
                                                popup.getMenuInflater().inflate(R.menu.post_menu, popup.getMenu());
                                            }

                                            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                                public boolean onMenuItemClick(MenuItem item) {
                                                    if (item.toString().equals("Report")){
                                                        RequestQueue queue = Volley.newRequestQueue(mContext);
                                                        String url = "https://maker.ifttt.com/trigger/froozereport/with/key/bX8uNSFbAeoqUKPdfSztoA?value1=https://app.frooze.ch/post/?postid=" + post.getPostid();
// Request a string response from the provided URL.
                                                        StringRequest reportRequest = new StringRequest(Request.Method.POST, url,
                                                                new Response.Listener<String>() {
                                                                    @Override
                                                                    public void onResponse(String response) {
                                                                        Toast.makeText(mContext, "Successfully reported", Toast.LENGTH_SHORT).show();
                                                                        // Display the first 500 characters of the response string.
                                                                    }
                                                                }, new Response.ErrorListener() {
                                                            @Override
                                                            public void onErrorResponse(VolleyError error) {
                                                                Toast.makeText(mContext, "An error occured. Please contact support.", Toast.LENGTH_LONG).show();
                                                            }
                                                        });
                                                        queue.add(reportRequest);

                                                    }
                                                    if (item.toString().equals("Delete") && post.getPublisher().equals(firebaseUser.getUid())){
                                                        RequestQueue queue = Volley.newRequestQueue(mContext);
                                                        String url = "https://maker.ifttt.com/trigger/postdeleted/with/key/bX8uNSFbAeoqUKPdfSztoA?value1=" + post.getPostid();
                                                        StringRequest reportRequest = new StringRequest(Request.Method.POST, url,
                                                                new Response.Listener<String>() {
                                                                    @Override
                                                                    public void onResponse(String response) {
                                                                        Toast.makeText(mContext, "Successfully reported", Toast.LENGTH_SHORT).show();
                                                                        // Display the first 500 characters of the response string.
                                                                    }
                                                                }, new Response.ErrorListener() {
                                                            @Override
                                                            public void onErrorResponse(VolleyError error) {
                                                                Toast.makeText(mContext, "An error occured. Please contact support.", Toast.LENGTH_LONG).show();
                                                            }
                                                        });
                                                        queue.add(reportRequest);
                                                    }
                                                    return true;
                                                }
                                            });
                                            popup.show();

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

            if (post.getPostvideo() != null && !post.getPostvideo().isEmpty()){
                uri = Uri.parse(post.getPostvideo());
            } else {
                uri = Uri.parse("https://res.cloudinary.com/frooze/video/upload/v1591303571/piu9bb4bk0tj0m8yv2us.m3u8");
            }
            FirebaseAuth user = FirebaseAuth.getInstance();
            String uid = user.getUid();
            share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                sharepost(username);
                }
            });
            doubletap.setOnTapListener(new DoubleTapLikeView.OnTapListener() {
                @Override
                public void onDoubleTap(View view) {
                    if (post.getPostid() != null && !post.getPostid().isEmpty()) {
                        if (like.getTag().equals("like")) {
                            FirebaseDatabase.getInstance().getReference().child("Likes").child(post.getPostid())
                                    .child(firebaseUser.getUid()).setValue(true);
                            sendNotification(post.getPublisher(), post.getPostid());

                        } else {
                            FirebaseDatabase.getInstance().getReference().child("Likes").child(post.getPostid())
                                    .child(firebaseUser.getUid()).removeValue();
                        }
                    } else {
                        Toast.makeText(mContext, "Error", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onTap() {

                }
            });
            like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (like.getTag().equals("like")) {
                        FirebaseDatabase.getInstance().getReference().child("Likes").child(post.getPostid())
                                .child(firebaseUser.getUid()).setValue(true);
                        sendNotification(post.getPublisher(), post.getPostid());

                    } else {
                        FirebaseDatabase.getInstance().getReference().child("Likes").child(post.getPostid())
                                .child(firebaseUser.getUid()).removeValue();
                    }
                }
            });

            comment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                    editor.putString("postid", post.getPostid());
                    editor.putString("publisher", post.getPublisher());
                    editor.apply();
                    commentsActivity =
                            CommentsActivity.newInstance();
                    commentsActivity.show(((FragmentActivity)mContext).getSupportFragmentManager(),
                            "comments");

                }
            });
            if (post.getDescription().equals("")){
                description.setVisibility(View.GONE);
            } else {
                description.setVisibility(View.VISIBLE);
                description.setText(post.getDescription());
                mTextHashTagHelper = HashTagHelper.Creator.create(mContext.getResources().getColor(R.color.dark_blue), new HashTagHelper.OnHashTagClickListener() {
                    @Override
                    public void onHashTagClicked(String hashTag) {
                        pause();
                        SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                        editor.putString("hashtag", hashTag.toLowerCase());
                        editor.apply();
                        FragmentTransaction transaction = ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction();
                        transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left,R.anim.slide_in_left, R.anim.slide_out_right);
                        transaction.replace(R.id.fragment_container,
                                new ViewHashtagFragment()).addToBackStack(null).commit();
                    }
                });
                mTextHashTagHelper.handle(description);

            }
            if (helper == null) {
                helper = new ExoPlayerViewHelper(this, uri, null, config);

            }
            helper.initialize(container, playbackInfo);
        }

        @Override public void release() {
            if (helper != null) {
                helper.release();
                helper = null;
            }
        }

        @Override public void play() {
            if (helper != null) helper.play();
        }

        @Override public void pause() {
            if (helper != null) helper.pause();
        }

        @Override public boolean isPlaying() {
            return helper != null && helper.isPlaying();
        }

        @Override public boolean wantsToPlay() {
            return ToroUtil.visibleAreaOffset(this, itemView.getParent()) >= 0.85;
        }

        @Override public int getPlayerOrder() {
            return getAdapterPosition();
        }
    }
    private void getComments(String postid, TextView comments){
        reference = FirebaseDatabase.getInstance().getReference().child("Comments").child(postid);
        listener = reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                comments.setText(dataSnapshot.getChildrenCount()+"");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void isLiked(String postid, ImageView imageView){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        likereference = FirebaseDatabase.getInstance().getReference()
                .child("Likes")
                .child(postid);
        likelistener = likereference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(user.getUid()).exists()){
                    imageView.setBackgroundResource(R.drawable.heart);

                    imageView.setTag("liked");
                }
                else{
                    imageView.setBackgroundResource(R.drawable.heart_white);

                    imageView.setTag("like");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void nrLikes(TextView likes, String postid) {
        nrlikelistener = likereference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                likes.setText(dataSnapshot.getChildrenCount()+"");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

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

    private void publisherInfo(ImageView image_profile, TextView username, String userid) {
         reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);
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
public void sharepost(TextView username){
        String usernamestring = username.getText().toString();
    Task<ShortDynamicLink> shortLinkTask = FirebaseDynamicLinks.getInstance().createDynamicLink()
            .setLink(Uri.parse("https://app.frooze.ch/post/?postid="+post.getPostid()))
            .setDomainUriPrefix("https://app.frooze.ch/link")
            .setAndroidParameters(new DynamicLink.AndroidParameters.Builder("com.thilojaeggi.frooze")
                    .setFallbackUrl(Uri.parse("https://app.frooze.ch/post/?postid="+post.getPostid()))
                    .build()

            )
            .setIosParameters(new DynamicLink.IosParameters.Builder("com.thilojaeggi.frooze")
                    .setFallbackUrl(Uri.parse("https://app.frooze.ch/post/?postid="+post.getPostid()))
                    .build()
            )
            .buildShortDynamicLink()
            .addOnCompleteListener((Activity) mContext, new OnCompleteListener<ShortDynamicLink>() {
                @Override
                public void onComplete(@NonNull Task<ShortDynamicLink> task) {
                    if (task.isSuccessful()) {
                        // Short link created
                        Uri shortLink = task.getResult().getShortLink();
                        Uri flowchartLink = task.getResult().getPreviewLink();
                        Intent sendIntent = new Intent();
                        sendIntent.setAction(Intent.ACTION_SEND);
                        sendIntent.putExtra(Intent.EXTRA_TEXT, mContext.getString(R.string.sharetext) + usernamestring + ":\n" + shortLink);
                        sendIntent.setType("text/plain");
                        Intent shareIntent = Intent.createChooser(sendIntent, null);
                        mContext.startActivity(shareIntent);
                    } else {

                    }
                }
            });
}
    private void sendNotification(String userid, String postid){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child(userid);
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("userid", firebaseUser.getUid());
        hashMap.put("text", "likedyourpost");
        hashMap.put("postid", postid);
        hashMap.put("ispost", true);
        reference.push().setValue(hashMap);
    }
    public void clear() {
        mPost.clear();
        notifyDataSetChanged();
    }


    @Override
    public void onViewDetachedFromWindow(@NonNull ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        if (reference != null && listener != null) {
            reference.removeEventListener(listener);
        }
        if (likereference != null && likelistener != null) {
            likereference.removeEventListener(likelistener);
        }
        if (likereference != null && nrlikelistener != null) {
            likereference.removeEventListener(nrlikelistener);
        }
    }

    // Add a list of items -- change to type used
    public void addAll(List<Post> list) {
        mPost.addAll(list);
        notifyDataSetChanged();
    }
}
