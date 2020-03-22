package com.thilojaeggi.frooze;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.oss.licenses.OssLicensesMenuActivity;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.thilojaeggi.frooze.BuildConfig;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.CheckBoxPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SeekBarPreference;

import com.google.firebase.auth.FirebaseAuth;
import com.parse.ParseUser;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        private static final String TAG = "Kek";

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {

            setPreferencesFromResource(R.xml.root_preferences, rootKey);
            SettingsActivity m = new SettingsActivity();

            Preference button = getPreferenceManager().findPreference("logout");

            if (button != null) {
                button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference arg) {
                        FirebaseAuth.getInstance().signOut();
                        Intent intentsettings = new Intent(getContext(), LoginActivity.class);
                        intentsettings.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intentsettings);
                        return true;
                    }
                });
                }
            Preference support = getPreferenceManager().findPreference("support");
                support.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference arg) {
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        String uid = user.getUid();
                        String yourproblem = getString(R.string.yourproblem);
                        String device = android.os.Build.MODEL.toString();
                        String version = Build.VERSION.RELEASE;
                        String versionName = BuildConfig.VERSION_NAME;
                        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                                "mailto","support@frooze.ch", null));
                        intent.putExtra(Intent.EXTRA_SUBJECT, "Support");
                        intent.putExtra(Intent.EXTRA_TEXT, "Device: " + device + "\n OS: " + version + "\n App Version: " + versionName + "\n User: " + uid + "\n" + yourproblem);
                        startActivity(Intent.createChooser(intent, "Choose an Email client:"));
                        return true;
                    }
                });
            Preference licenses = getPreferenceManager().findPreference("licenses");
            licenses.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference arg) {
                    startActivity(new Intent(getContext(), OssLicensesMenuActivity.class));

                    return true;
                }
            });
            Preference deleteaccount = getPreferenceManager().findPreference("deleteaccount");
            deleteaccount.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference arg) {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    String uid = user.getUid();
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child(uid);;
                    ref.removeValue();
                    user.delete();
                    Toast.makeText(getContext(), "Account deleted", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(getContext(), LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    return true;

                    };

        });

        }

    }
}

