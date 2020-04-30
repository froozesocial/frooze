package com.thilojaeggi.frooze;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
public class SettingsActivity extends AppCompatActivity {

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        ImageButton goback = findViewById(R.id.backbutton);
        goback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();

            }
        });
        Button logout = findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intentsettings = new Intent(getApplicationContext(), LoginActivity.class);
                intentsettings.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intentsettings);

            }
        });
        Button oss = findViewById(R.id.oss);
        oss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), OssLicensesMenuActivity.class));
            }
        });
        Button support = findViewById(R.id.support);
        support.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                String uid = user.getUid();
                String yourproblem = getString(R.string.yourproblem);
                String device = Build.MODEL;
                String version = Build.VERSION.RELEASE;
                String versionName = BuildConfig.VERSION_NAME;
                Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto","support@frooze.ch", null));
                intent.putExtra(Intent.EXTRA_SUBJECT, "Support Android");
                intent.putExtra(Intent.EXTRA_TEXT, "Device: " + device + "\n OS: " + version + "\n App Version: " + versionName + "\n User: " + uid + "\n" + yourproblem);
                startActivity(Intent.createChooser(intent, "Choose an Email client:"));            }
        });
        Button delete = findViewById(R.id.delete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new AlertDialog.Builder(SettingsActivity.this)
                        .setMessage("Are you sure?\nThis cannot be undone")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which)
                            {
                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                String uid = user.getUid();
                                DatabaseReference followerreference = FirebaseDatabase.getInstance().getReference().child("Follow");


//read the node and loop through all UIDs

                                ValueEventListener listener = new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                        //loop through all UIDs under "Follow" and remove the user ID that deleted his/her account from "followers"

                                        for(DataSnapshot ds : dataSnapshot.getChildren()){

                                            if(ds.child("followers").child(uid).exists()){

                                                ds.child("followers").child(uid).getRef().removeValue();

                                            }
                                        }

                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {


                                    }
                                };
                                DatabaseReference followRef = FirebaseDatabase.getInstance().getReference().child("Follow");
                                followRef.child(user.getUid()).removeValue();
                                followerreference.addValueEventListener(listener);
                                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(uid);
                                userRef.removeValue();
                                user.delete();
                                FirebaseAuth.getInstance().signOut();
                                Toast.makeText(getApplicationContext(), "Account deleted", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                dialog.cancel();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which)
                            {

                                dialog.cancel();
                            }
                        }).show();
            }

        });
        String versionName = BuildConfig.VERSION_NAME;
        TextView credits = findViewById(R.id.credits);
        credits.setText("Version " + versionName + "\n © 2020 frooze a Jaeggi company");
    }

}

