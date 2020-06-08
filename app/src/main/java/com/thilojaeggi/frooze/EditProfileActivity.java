package com.thilojaeggi.frooze;


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class EditProfileActivity extends AppCompatActivity {
    public static final int REQUEST_CODE = 100;
    public String result;
    SimpleDraweeView profileimage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editprofile);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        profileimage = findViewById(R.id.profile_image);
        Intent intent = getIntent();
        String oldname = intent.getStringExtra("name");
        String oldbio = intent.getStringExtra("bio");
        EditText name = findViewById(R.id.fullnameedittext);
        EditText bio = findViewById(R.id.bioedittext);
        name.setText(oldname);
        bio.setText(oldbio);
        ImageButton backbutton = findViewById(R.id.backbutton);
        TextView donebutton = findViewById(R.id.donebutton);
        donebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid());
                reference.child("bio").setValue(bio.getText().toString());
                reference.child("fullname").setValue(name.getText().toString());
                finish();
            }
        });
        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("imageurl").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String imageurl = dataSnapshot.getValue(String.class);
                profileimage.setImageURI(Uri.parse(imageurl));

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                throw databaseError.toException();
            }
        });
        profileimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditProfileActivity.this,
                        UpdateProfilePicture.class);
                startActivityForResult(intent , REQUEST_CODE);
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
            result = data.getStringExtra("result");
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    profileimage.setImageURI(Uri.parse(result));
                }
            }, 2000);
        }
    }
}
