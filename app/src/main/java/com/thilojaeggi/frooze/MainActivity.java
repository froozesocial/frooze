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
import androidx.fragment.app.FragmentTransaction;

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

import im.ene.toro.exoplayer.Config;
import im.ene.toro.exoplayer.MediaSourceBuilder;
import im.ene.toro.exoplayer.ToroExo;

public class MainActivity  extends AppCompatActivity implements BillingProcessor.IBillingHandler {
    Context mContext = this;
    private BillingProcessor bp;
    private int startingPosition;
    private int newPosition;
    Config config;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);

        setContentView(R.layout.activity_main);
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
        if (savedInstanceState == null) {
            loadFragment(new HomeFragment(), 1);

        }

    }


    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;

                    switch (item.getItemId()) {
                        case R.id.nav_home:
                            selectedFragment = new HomeFragment();
                            newPosition = 1;

                            break;
                        case R.id.nav_search:
                            selectedFragment = new SearchFragment();
                            newPosition = 2;

                            break;

                        case R.id.nav_notifications:
                            selectedFragment = new NotificationFragment();
                            newPosition = 3;

                            break;

                        case R.id.nav_profile:
                            selectedFragment = new ProfileFragment();
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                            editor.putString("profileid", user.getUid());
                            editor.apply();
                            newPosition = 4;

                            break;
                    }

                    return loadFragment(selectedFragment, newPosition);
                }
            };
    private boolean loadFragment(Fragment fragment, int newPosition) {
        if(fragment != null) {
            if(startingPosition > newPosition) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right );
                transaction.replace(R.id.fragment_container, fragment);
                transaction.commit();
            }
            if(startingPosition < newPosition) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
                transaction.replace(R.id.fragment_container, fragment);
                transaction.commit();
            }
            startingPosition = newPosition;
            return true;
        }

        return false;
    }
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