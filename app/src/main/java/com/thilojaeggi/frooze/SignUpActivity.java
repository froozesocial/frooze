package com.thilojaeggi.frooze;

import android.content.Intent;

import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.thilojaeggi.frooze.Intro.AppIntro;

import java.util.HashMap;
public class SignUpActivity extends AppCompatActivity {


    private EditText emailTV, passwordTV, usernameTV, fullnameTV;
    private Button regBtn;
    private FirebaseAuth mAuth;
    private static FirebaseAnalytics firebaseAnalytics;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        mAuth = FirebaseAuth.getInstance();
        FrameLayout background = findViewById(R.id.background);
        background.setBackgroundResource(R.drawable.gradient_animationregister);
        AnimationDrawable animation = (AnimationDrawable) background.getBackground();
        animation.setEnterFadeDuration(10);
        animation.setExitFadeDuration(5000);
        animation.start();
        initializeUI();

        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usernamecheck();
            }
        });
        final ImageButton backbutton = findViewById(R.id.back_button);
        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });


    }


    private void usernamecheck() {
        String username;
        username = usernameTV.getText().toString();
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference().child("Users");
        ref.orderByChild("username").equalTo(username).addListenerForSingleValueEvent(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot){
                if(!dataSnapshot.exists()) {
                    registerNewUser();
                }
                else{
                    Toast.makeText(getApplicationContext(), getString(R.string.usernameexists), Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void registerNewUser() {

        String email, password, username, fullname;
        fullname = fullnameTV.getText().toString();
        email = emailTV.getText().toString();
        password = passwordTV.getText().toString();
        username = usernameTV.getText().toString();

        if (TextUtils.isEmpty(fullname)) {
            Toast.makeText(getApplicationContext(), getString(R.string.missingfullname), Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(username)) {
            Toast.makeText(getApplicationContext(), getString(R.string.missingusername), Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(), getString(R.string.missingmail), Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(), getString(R.string.missingpassword), Toast.LENGTH_LONG).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            String userid = user.getUid();

                            reference = FirebaseDatabase.getInstance().getReference().child("Users").child(userid);

                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("premium", "false");
                            hashMap.put("id", userid);
                            hashMap.put("username", username.toLowerCase());
                            hashMap.put("fullname", fullname);
                            hashMap.put("bio", "");
                            hashMap.put("imageurl", "https://firebasestorage.googleapis.com/v0/b/frooze-b2248.appspot.com/o/default.jpg?alt=media&token=303e7fbb-6bd4-440f-b3b3-303b761d40da");
                            reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        // Verification Mail
                                        sendVerificationEmail();
                                        FirebaseAuth.getInstance().signOut();
                                        Intent intent = new Intent(SignUpActivity.this, AppIntro.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                    }
                                }
                            });

                        }
                        else {
                            // If registration fails
                            Toast.makeText(getApplicationContext(), getString(R.string.registrationfail), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
    public void sendVerificationEmail() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        // if User logged in
        if (user != null) {
            user.sendEmailVerification()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), getString(R.string.registrationsuccessful), Toast.LENGTH_LONG).show();

                            }
                        }
                    });
        }

    }
    private void initializeUI() {
        fullnameTV = findViewById(R.id.fullname);
        emailTV = findViewById(R.id.email);
        usernameTV = findViewById(R.id.username);
        passwordTV = findViewById(R.id.password);
        regBtn = findViewById(R.id.register);
    }
    @Override
    public void finish(){
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}