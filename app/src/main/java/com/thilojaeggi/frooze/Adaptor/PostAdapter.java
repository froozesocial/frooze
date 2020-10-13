package com.thilojaeggi.frooze.Adaptor;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentActivity;
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
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.cache.SimpleCache;
import com.google.android.gms.tasks.Continuation;
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
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;
import com.thilojaeggi.frooze.CommentFragment;
import com.thilojaeggi.frooze.Model.Post;
import com.thilojaeggi.frooze.Model.User;
import com.thilojaeggi.frooze.ProfileFragment;
import com.thilojaeggi.frooze.R;
import com.thilojaeggi.frooze.GridView;
import com.volokh.danylo.hashtaghelper.HashTagHelper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
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
    AdView adView;
    DatabaseReference reference;
    ValueEventListener listener, likelistener, nrlikelistener;
    CommentFragment commentsActivity;
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
        post = mPost.get(i);
        viewHolder.doubletap.setOnTapListener(new DoubleTapLikeView.OnTapListener() {
            @Override
            public void onDoubleTap(View view) {
                if (post.getPostid() != null && !post.getPostid().isEmpty()) {
                    if (viewHolder.like.getTag().equals("like")) {
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
                if (viewHolder.isPlaying()){
                    viewHolder.pause();
                } else {
                    viewHolder.play();
                }
            }
        });

    }



    @Override
    public int getItemCount() {
        return mPost.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements ToroPlayer, ToroPlayer.EventListener {

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
        @BindView(R.id.adView)
        LinearLayout adContainer;
        @BindView(R.id.comment)
        ImageView comment;
        @BindView(R.id.more)
        ImageView more;
        @BindView(R.id.share)
        ImageView share;
        @BindView(R.id.dangerous)
        AppBarLayout dangerous;
        @BindView(R.id.playprogress)
        ProgressBar playprogress;
        @BindView(R.id.doubletap)
        DoubleTapLikeView doubletap;
        @BindView(R.id.thumbnail)
        ImageView thumbnail;
        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }


        @NonNull @Override public View getPlayerView() {
            return playerView;
        }

        @NonNull @Override public PlaybackInfo getCurrentPlaybackInfo() {
            return helper != null ? helper.getLatestPlaybackInfo() : new PlaybackInfo();
        }
        @Override
        public void initialize(@NonNull Container container, @Nullable PlaybackInfo playbackInfo) {
            post = mPost.get(getPosition());
            publisherInfo(profileimage, username, post.getPublisher());
            config = new Config.Builder()
                    .setMediaSourceBuilder(MediaSourceBuilder.LOOPING)
                    .build();
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
                switch(post.getTextColor()){
                    case "black":
                        username.setTextColor(Color.BLACK);
                        description.setTextColor(Color.BLACK);
                        break;
                    default:
                        username.setTextColor(Color.WHITE);
                        description.setTextColor(Color.WHITE);
                }
            }
            if (post.getPostid() != null && !post.getPostid().isEmpty()){
            FirebaseDatabase.getInstance().getReference("Posts").child(post.getPostid()).child("trendingviewedby").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(true);
            }
            if (post.getDangerous().equals("true")){
                adContainer.setVisibility(View.GONE);
                dangerous.setVisibility(View.VISIBLE);
            } else {
                adContainer.setVisibility(View.VISIBLE);
                adView = new AdView(mContext, "224573008994773_227062902079117", AdSize.BANNER_HEIGHT_50);
                adContainer.addView(adView);
                adView.loadAd();
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
                                                    if (item.toString().equals("Delete") && post.getPublisher().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                                        deletePost(post.getPostid());
                                                        RequestQueue queue = Volley.newRequestQueue(mContext);
                                                        String url = "https://maker.ifttt.com/trigger/postdeleted/with/key/bX8uNSFbAeoqUKPdfSztoA?value1="+post.getPostid();
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
                                                        String postid = post.getPostid();
                                                        FirebaseDatabase.getInstance().getReference("Posts").child(postid).removeValue();
                                                        FirebaseDatabase.getInstance().getReference("Comments").child(postid).removeValue();
                                                        FirebaseDatabase.getInstance().getReference("Likes").child(postid).removeValue();

                                                    }
                                                    return true;
                                                }
                                            });
                                            popup.show();

                                        }
                                    });




            if (post.getPostvideo() != null && !post.getPostvideo().isEmpty()){
                uri = Uri.parse(post.getPostvideo());
            } else {
                uri = Uri.parse("https://res.cloudinary.com/froozecdn/video/upload/v1593292413/novideo.m3u8");
            }
            FirebaseAuth user = FirebaseAuth.getInstance();
            String uid = user.getUid();
            share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                sharepost(username);
                }
            });

            like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (like != null && like.getTag().equals("like")) {
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
                            CommentFragment.newInstance();
                    commentsActivity.show(((FragmentActivity)mContext).getSupportFragmentManager(),
                            "comments");

                }
            });
            if (post.getDescription().equals("")){
                description.setVisibility(View.GONE);
            } else {
                description.setVisibility(View.VISIBLE);
                description.setText(post.getDescription());
                HashTagHelper mTextHashTagHelper = HashTagHelper.Creator.create(mContext.getResources().getColor(R.color.dark_blue), new HashTagHelper.OnHashTagClickListener() {
                    @Override
                    public void onHashTagClicked(String hashTag) {
                        pause();
                        SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                        editor.putString("hashtag", hashTag.toLowerCase());
                        editor.apply();
                        FragmentTransaction transaction = ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction();
                        transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
                        transaction.replace(R.id.fragment_container,
                                new GridView()).addToBackStack(null).commit();
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

        @Override
        public void onFirstFrameRendered() {
//            thumbnail.setVisibility(View.GONE);

        }

        @Override
        public void onBuffering() {

        }

        @Override
        public void onPlaying() {
    //        thumbnail.setVisibility(View.GONE);
        }

        @Override
        public void onPaused() {

        }

        @Override
        public void onCompleted() {

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
    private void isWTF(){}
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
    private void deletePost(String postid) {
        Map<String, Object> data = new HashMap<>();
        data.put("postid", postid);
        FirebaseFunctions.getInstance() // Optional region: .getInstance("europe-west1")
                .getHttpsCallable("deletePost")
                .call(data)
                .continueWith(new Continuation<HttpsCallableResult, String>() {
            @Override
            public String then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                String result = (String) task.getResult().getData();
                return result;
            }
        });
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
