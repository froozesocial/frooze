package com.thilojaeggi.frooze;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Locale;

public class ForgotPasswordActivity extends AppCompatActivity {
    private EditText mailView;
    private FirebaseAuth mAuth;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgotpassword);
        mAuth = FirebaseAuth.getInstance();

        String locale = Locale.getDefault().getLanguage();
        mAuth.setLanguageCode(locale);
        FrameLayout background = findViewById(R.id.background);
        background.setBackgroundResource(R.drawable.gradient_animationfpass);
        AnimationDrawable animation = (AnimationDrawable) background.getBackground();
        animation.setEnterFadeDuration(10);
        animation.setExitFadeDuration(5000);
        animation.start();


        final ImageButton back_button = findViewById(R.id.back_button);
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Setting up a progress dialog
                Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

            }
        });

        final Button forgotpass_button = findViewById(R.id.send);
        forgotpass_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText edt = findViewById(R.id.email);
                String mail = edt.getText().toString();
                if (!TextUtils.isEmpty(mail)) {
                    mAuth.sendPasswordResetEmail(mail)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(getApplicationContext(), getString(R.string.emailsent), Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
                                        startActivity(intent);
                                        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                                    } else {
                                        Toast.makeText(getApplicationContext(), getString(R.string.emailnotsent), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                } if (TextUtils.isEmpty(mail)) {
                    Toast.makeText(getApplicationContext(), getString(R.string.missingmail), Toast.LENGTH_LONG).show();
                }

            }
        });
    }
    @Override
    public void finish(){
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
