package com.thilojaeggi.frooze.Intro;


import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.transition.Fade;
import android.transition.Slide;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.heinrichreimersoftware.materialintro.app.IntroActivity;
import com.heinrichreimersoftware.materialintro.slide.FragmentSlide;
import com.heinrichreimersoftware.materialintro.slide.SimpleSlide;
import com.thilojaeggi.frooze.LoginActivity;
import com.thilojaeggi.frooze.R;
import com.thilojaeggi.frooze.SignUpActivity;


public class AppIntro extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        FloatingActionButton next = findViewById(R.id.buttonnext);
        Fragment fragment = new FirstSlide();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.intro_container, fragment, "1");
        transaction.commit();

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment visibleFragment = getCurrentFragment();
                String[] currentFragment = visibleFragment.toString().split("\\{");
                Toast.makeText(getApplicationContext(), currentFragment[0], Toast.LENGTH_SHORT).show();
                if (currentFragment[0].equals("FirstSlide")) {
                    Fragment fragment = new SecondSlide();
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
                    transaction.replace(R.id.intro_container, fragment, "2");
                    transaction.commit();
                }
                if (currentFragment[0].equals("SecondSlide")) {
                    Fragment fragment = new SlideFollowHashtags();
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
                    transaction.replace(R.id.intro_container, fragment, "3");
                    transaction.commit();
                }
                if (currentFragment[0].equals("SlideFollowHashtags")) {
                    Fragment fragment = new ThirdSlide();
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
                    transaction.replace(R.id.intro_container, fragment, "3");
                    transaction.commit();
                    next.setImageResource(R.drawable.mi_ic_finish);
                }
                if (currentFragment[0].equals("ThirdSlide")){
                    Intent intent = new Intent(AppIntro.this, LoginActivity.class);
                    startActivity(intent);
                }
            }
        });

    }


    Fragment getCurrentFragment()
    {
        Fragment currentFragment = getSupportFragmentManager()
                .findFragmentById(R.id.intro_container);
        return currentFragment;
    }
}


