package com.thilojaeggi.frooze;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.ads.NativeExpressAdView;
import com.google.android.gms.ads.VideoController;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {
    int TAKE_IMAGE_CODE = 10001;
    String DISPLAY_NAME = null;
    CircleImageView profileImageView;
    private Context mContext;
    private static String LOG_TAG = "EXAMPLE";
    NativeExpressAdView mAdView;
    VideoController mVideoController;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        profileImageView = getActivity().findViewById(R.id.profile_image);
        if (user != null) {
            String uid = user.getUid();
            DatabaseReference mref = FirebaseDatabase.getInstance().getReference("Users").child(uid).child("username");
            mref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String username = dataSnapshot.getValue(String.class);
                    TextView usernametv = (TextView) view.findViewById(R.id.username);
                    usernametv.setText("@" + username);

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            DatabaseReference mreffullname = FirebaseDatabase.getInstance().getReference("Users").child(uid).child("fullname");
            mreffullname.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String fullname = dataSnapshot.getValue(String.class);
                    TextView fullnametv = (TextView) view.findViewById(R.id.fullname);
                    fullnametv.setText(fullname);

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        ImageButton settingslistener = (ImageButton) view.findViewById(R.id.settings);
        settingslistener.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentsettings = new Intent(getActivity(), SettingsActivity.class);
                startActivity(intentsettings);
            }
        });


        return view;
    }


    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // To parse the html page
    }




    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final CircleImageView profilebutton = getView().findViewById(R.id.profile_image);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();

        DatabaseReference mref = FirebaseDatabase.getInstance().getReference("Users").child(uid).child("imageurl");
        mref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String imageurl = dataSnapshot.getValue(String.class);
                profileImageView = getActivity().findViewById(R.id.profile_image);
                Glide.with(getContext())
                        .load(imageurl)
                        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                        .into(profileImageView);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        profilebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), UpdateProfilePicture.class);
                startActivity(intent);
            }
        });

    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }
}
