package com.thilojaeggi.frooze;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityOptions;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.BuildConfig;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.tasks.OnCompleteListener;
import com.google.android.play.core.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.thilojaeggi.frooze.Model.Hashtag;
import com.thilojaeggi.frooze.Model.Post;

import java.util.ArrayList;
import java.util.List;

public class SettingsActivity extends AppCompatActivity {
    FirebaseUser user;
    List<String> listposts = new ArrayList<String>();
    Integer selection;
    SharedPreferences.Editor editor;
    SharedPreferences prefs;
    ReviewInfo reviewInfo;
    ReviewManager manager;
    Button testreview;
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        user = FirebaseAuth.getInstance().getCurrentUser();
        testreview = findViewById(R.id.reviewtest);

        if (user.getUid().equals("8x0BVAdw2VUopobTE0sWVadU0ms1")){
            testreview.setVisibility(View.VISIBLE);
        }
        testreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Review();
            }
        });
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
                Intent intentsettings = new Intent(getApplicationContext(), LoginActivity.class);
                intentsettings.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intentsettings);
                FirebaseAuth.getInstance().signOut();

            }
        });
        editor = getSharedPreferences("PREFS", MODE_PRIVATE).edit();
        prefs = getSharedPreferences("PREFS", MODE_PRIVATE);
        Button selectdefaulttab = findViewById(R.id.defaulttab);
        selectdefaulttab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selection = prefs.getInt("selectdefaulttab", 1);
                String[] exploretabitems = getResources().getStringArray(R.array.exploretabs);
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(SettingsActivity.this);
                mBuilder.setTitle(getString(R.string.selectdefaulttab));
                mBuilder.setSingleChoiceItems(exploretabitems, selection, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        selection = i;
                        editor.putInt("selectdefaulttab", i);
                        String selection = "Selection: " + i;
                        editor.apply();
                        Runnable r = new Runnable() {
                            @Override
                            public void run(){
                                dialogInterface.dismiss();
                            }
                        };
                        Handler h = new Handler();
                        h.postDelayed(r, 500);
                    }
                });
                AlertDialog mDialog = mBuilder.create();
                mDialog.setIcon(R.drawable.ic_baseline_library_music_24);
                mDialog.show();
            }
        });
        Button privacypolicy = findViewById(R.id.privacypolicy);
        privacypolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://frooze.ch/?page_id=83575")));
            }
        });
        Button oss = findViewById(R.id.oss);
        oss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), OssLicensesMenuActivity.class));
            }
        });

        Button getpremium = findViewById(R.id.getpremium);
        getpremium.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, PremiumActivity.class);
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(SettingsActivity.this).toBundle());
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
                startActivity(Intent.createChooser(intent, getString(R.string.chooseemail)));
            }
        });
        Button delete = findViewById(R.id.delete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(SettingsActivity.this)
                        .setMessage(getString(R.string.deleteaccconfirm))
                        .setCancelable(false)
                        .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                                String url = "https://maker.ifttt.com/trigger/accountdelete/with/key/bX8uNSFbAeoqUKPdfSztoA?value1="+user.getUid();
// Request a string response from the provided URL.
                                StringRequest deleteRequest = new StringRequest(Request.Method.POST, url,
                                        new Response.Listener<String>() {
                                            @Override
                                            public void onResponse(String response) {
                                                Toast.makeText(getApplicationContext(), "Successfully deleted", Toast.LENGTH_SHORT).show();
                                                // Display the first 500 characters of the response string.
                                            }
                                        }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Toast.makeText(getApplicationContext(), "An error occured. Please contact support.", Toast.LENGTH_LONG).show();
                                    }
                                });
                                queue.add(deleteRequest);
                                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                user.delete();
                                FirebaseAuth.getInstance().signOut();
                                Toast.makeText(getApplicationContext(), getString(R.string.accountdeleted), Toast.LENGTH_LONG).show();
                                dialog.cancel();

                            }
                        })
                        .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which)
                            {
                                dialog.cancel();
                            }
                        });
                alert.show();
            }
        });
        String versionName = com.thilojaeggi.frooze.BuildConfig.VERSION_NAME;
        TextView credits = findViewById(R.id.credits);
        credits.setText("Version " + versionName + "\n © 2020 frooze a Jaeggi company");
    }
    private void Review(){
        manager.requestReviewFlow().addOnCompleteListener(new com.google.android.play.core.tasks.OnCompleteListener<ReviewInfo>() {
            @Override
            public void onComplete(@NonNull com.google.android.play.core.tasks.Task<ReviewInfo> task) {
                if(task.isSuccessful()){
                    reviewInfo = task.getResult();
                    manager.launchReviewFlow(SettingsActivity.this, reviewInfo).addOnFailureListener(new com.google.android.play.core.tasks.OnFailureListener() {
                        @Override
                        public void onFailure(Exception e) {
                            Toast.makeText(getApplicationContext(), "Rating Failed", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(getApplicationContext(), "Review Completed, Thank You!", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            }
        }).addOnFailureListener(new com.google.android.play.core.tasks.OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                Toast.makeText(getApplicationContext(), "In-App Request Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void deleteComments(String id){
        Query commentreference = FirebaseDatabase.getInstance().getReference("Comments").child(id).orderByChild("publisher").equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid());
        commentreference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    ds.getRef().removeValue();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}

