package com.thilojaeggi.frooze;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingResult;
import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static android.drm.DrmInfoRequest.SUBSCRIPTION_ID;

public class PremiumActivity extends AppCompatActivity implements BillingProcessor.IBillingHandler{
    private BillingClient billingClient;
    BillingProcessor bp;
    private static final String SUBSCRIPTION_ID = "subspro";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_premium);
        bp = new BillingProcessor(getApplicationContext(), "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAtF6vzGWT3jyRKdkNagWdw5CaW4TvPYEflDTAQzDr3f/rxFqpilu9jGLCjnJ3HNfMbtrWqgttc7yHOpuV/AMzOF61n+yhQRfHEwysGSxsXklccZ0OxHEXzcWz1MEtjesvbf9s1P/cGevKEwtsEQiM/fl4wemUbowNmVDIhd71xQnGzNuJ7J+hMyj/1VmXhebTaaKyd9TwnEbO1DH9/eLLntaruWwHqD02XsAqmTyi+PVNUluM0ZJbamXk3+vsvxwABdPxfofYAduHe/9JHp4q8YrBQQZmApt+g1dOyTRI50gvse7koL9KbTdCWKfJbT9MdxLUQ+HJvRauWmD+URXs/wIDAQAB", this);
        bp.initialize();
        bp.loadOwnedPurchasesFromGoogle();
        View cover = findViewById(R.id.cover);
        TextView alreadysubscribed = findViewById(R.id.alreadysubscribed);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        Button monthly = findViewById(R.id.getpremiummonthly);
        reference.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String premium = dataSnapshot.child("premium").getValue(String.class);
                if (premium.equals("true")){
                    cover.setVisibility(View.VISIBLE);
                    monthly.setVisibility(View.GONE);
                    alreadysubscribed.setVisibility(View.VISIBLE);
                }if (premium.equals("false")){
                    cover.setVisibility(View.GONE);
                    monthly.setVisibility(View.VISIBLE);
                    alreadysubscribed.setVisibility(View.GONE);
                }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                throw databaseError.toException();
            }
        });
        ImageButton goback = findViewById(R.id.backbutton);
        goback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
          finish();
            }
        });

        monthly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bp.subscribe(PremiumActivity.this, "premiumonemonth");
            }
        });


    }
    @Override
    public void onBillingInitialized() {
        /*
         * Called when BillingProcessor was initialized and it's ready to purchase
         */
        Button monthly = findViewById(R.id.getpremiummonthly);
        monthly.setText(getText(R.string.premiumbuttontext) + " " + bp.getSubscriptionListingDetails("premiumonemonth").priceText);
        TextView alreadysubscribed = findViewById(R.id.alreadysubscribed);
        View cover = findViewById(R.id.cover);

    }


    @Override
    public void onProductPurchased(String productId, TransactionDetails details) {
        /*
         * Called when requested PRODUCT ID was successfully purchased
         */
        Toast.makeText(getApplicationContext(),"Thank you so much for your purchase", Toast.LENGTH_LONG).show();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        reference.child("premium").setValue("true");

    }

    @Override
    public void onBillingError(int errorCode, Throwable error) {
        /*
         * Called when some error occurred. See Constants class for more details
         *
         * Note - this includes handling the case where the user canceled the buy dialog:
         * errorCode = Constants.BILLING_RESPONSE_RESULT_USER_CANCELED
         */
    }

    @Override
    public void onPurchaseHistoryRestored() {
        /*
         * Called when purchase history was restored and the list of all owned PRODUCT ID's
         * was loaded from Google Play
         */
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!bp.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
    @Override
    public void onDestroy() {
        if (bp != null) {
            bp.release();
        }
        super.onDestroy();
    }
}
