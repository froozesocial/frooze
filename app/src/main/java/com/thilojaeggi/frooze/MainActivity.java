package com.thilojaeggi.frooze;

import androidx.annotation.NonNull;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.*;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.cloudinary.android.MediaManager;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;

import java.io.Console;
import java.util.HashMap;
import java.util.Map;

public class MainActivity  extends AppCompatActivity implements BillingProcessor.IBillingHandler {
    Context mContext = this;
    private PlayerView playerView;
    private boolean playWhenReady = true;
    private int currentWindow = 0;
    private long playbackPosition = 0;
    private static final String SUBSCRIPTION_ID = "subspro";
    private BillingProcessor bp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);

        setContentView(R.layout.activity_main);
        SharedPreferences sharedPreferences = this.getSharedPreferences("com.thilojaeggi.frooze.app_preferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("com.package.name_preferences.products.cache.v2_6.version", "");
        editor.putString("com.package.name_preferences.products.cache.v2_6", "");
        editor.putBoolean("com.package.name_preferences.products.restored.v2_6", false);
        editor.putString("com.package.name_preferences.subscriptions.cache.v2_6", "");
        editor.putString("com.package.name_preferences.subscriptions.cache.v2_6.version", "");
        Bundle intent = getIntent().getExtras();
        if (intent != null){
            String publisher = intent.getString("publisherid");

            SharedPreferences.Editor editor2 = getSharedPreferences("PREFS", MODE_PRIVATE).edit();
            editor2.putString("profileid", publisher);
            editor2.apply();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment()).commit();
        } else{
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();

        }
        editor.apply();
        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(getIntent())
                .addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
                    @Override
                    public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                        // Get deep link from result (may be null if no link is found)
                        Uri deepLink = null;
                        if (pendingDynamicLinkData != null) {
                            deepLink = pendingDynamicLinkData.getLink();
                            Intent intent = new Intent(MainActivity.this, PostFromLink.class);
                            intent.putExtra("postid", deepLink.toString());
                            startActivity(intent);
                        }


                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("DynamicLinks", "getDynamicLink:onFailure", e);
                    }
                });
        bp = new BillingProcessor(getApplicationContext(), "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAtF6vzGWT3jyRKdkNagWdw5CaW4TvPYEflDTAQzDr3f/rxFqpilu9jGLCjnJ3HNfMbtrWqgttc7yHOpuV/AMzOF61n+yhQRfHEwysGSxsXklccZ0OxHEXzcWz1MEtjesvbf9s1P/cGevKEwtsEQiM/fl4wemUbowNmVDIhd71xQnGzNuJ7J+hMyj/1VmXhebTaaKyd9TwnEbO1DH9/eLLntaruWwHqD02XsAqmTyi+PVNUluM0ZJbamXk3+vsvxwABdPxfofYAduHe/9JHp4q8YrBQQZmApt+g1dOyTRI50gvse7koL9KbTdCWKfJbT9MdxLUQ+HJvRauWmD+URXs/wIDAQAB", this);
        bp.initialize();
        bp.loadOwnedPurchasesFromGoogle();
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);
        FloatingActionButton uploadvideobutton = findViewById(R.id.upload);
        final FloatingActionButton uploadvideo = uploadvideobutton;
        uploadvideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PostActivity.class);
                startActivity(intent);
            }
        });
        //I added this if statement to keep the selected fragment when rotating the device
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new HomeFragment()).commit();
        }
        // playerView = findViewById(R.id.video_view);
//Corner radius
    }


    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;

                    switch (item.getItemId()) {
                        case R.id.nav_home:
                            selectedFragment = new HomeFragment();
                            break;
                        case R.id.nav_search:
                            selectedFragment = new SearchFragment();
                            break;

                        case R.id.nav_notifications:
                            selectedFragment = new NotificationFragment();
                            break;

                        case R.id.nav_profile:
                            selectedFragment = new ProfileFragment();
                            break;
                    }


                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            selectedFragment).commit();

                    return true;

                }
            };

    @Override
    public void onProductPurchased(String productId, TransactionDetails details) {

    }

    @Override
    public void onPurchaseHistoryRestored() {

    }

    @Override
    public void onBillingError(int errorCode, Throwable error) {

    }

    @Override
    public void onBillingInitialized() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {

        boolean purchaseResult = bp.loadOwnedPurchasesFromGoogle();
        if(purchaseResult){
            TransactionDetails subscriptionTransactionDetails = bp.getSubscriptionTransactionDetails("premiumonemonth");
            if(subscriptionTransactionDetails!=null) {
                //User is still subscribed
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                reference.child("premium").setValue("true");
            } else {
                //Not subscribed
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                reference.child("premium").setValue("false");
            }
        }else{
            Log.d("BILLING", "loadOwnedPurchasesFromGoogle returned false");
        }
        }
    }
}