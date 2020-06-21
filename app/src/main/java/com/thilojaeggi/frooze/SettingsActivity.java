package com.thilojaeggi.frooze;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
    TemplateView adView;
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        user = FirebaseAuth.getInstance().getCurrentUser();


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
                startActivity(new Intent(getApplicationContext(), AdActivity.class));
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
                                user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        FirebaseAuth.getInstance().signOut();
                                        Toast.makeText(getApplicationContext(), getString(R.string.accountdeleted), Toast.LENGTH_LONG).show();
                                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        dialog.cancel();
                                    }
                                });

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

        String versionName = BuildConfig.VERSION_NAME;
        TextView credits = findViewById(R.id.credits);
        credits.setText("Version " + versionName + "\n © 2020 frooze a Jaeggi company");
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

