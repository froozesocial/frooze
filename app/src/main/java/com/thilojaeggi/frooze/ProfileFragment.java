package com.thilojaeggi.frooze;


import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.view.SimpleDraweeView;
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
    SimpleDraweeView draweeView;
    private Context mContext;
    public String imageurl;
    private String m_Text = "";
    private static String LOG_TAG = "EXAMPLE";
    NativeExpressAdView mAdView;
    VideoController mVideoController;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String uid = user.getUid();
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
            reference.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String username = dataSnapshot.child("username").getValue(String.class);
                    String fullname = dataSnapshot.child("fullname").getValue(String.class);
                    String imageurl = dataSnapshot.child("imageurl").getValue(String.class);
                    String premium = dataSnapshot.child("premium").getValue(String.class);
                    String biography = dataSnapshot.child("bio").getValue(String.class);
                  //  profileImageView = view.findViewById(R.id.profile_image);
                    draweeView = (SimpleDraweeView) view.findViewById(R.id.profile_image);


                    if (premium.equals("true")){
                        int color = getResources().getColor(R.color.premium);
                        RoundingParams roundingParams = RoundingParams.fromCornersRadius(5f);
                        roundingParams.setBorder(color, 22f);
                        roundingParams.setRoundAsCircle(true);
                        draweeView.setHierarchy(new GenericDraweeHierarchyBuilder(getResources())
                                .setRoundingParams(roundingParams)
                                .build());

                    } else {
                     //   profileImageView.setBorderColor(getResources().getColor(R.color.transparent));
                     //   profileImageView.setBorderWidth(0);
                        int color = getResources().getColor(R.color.transparent);
                        RoundingParams roundingParams = RoundingParams.fromCornersRadius(5f);
                        roundingParams.setBorder(color, 0f);
                        roundingParams.setRoundAsCircle(true);
                        draweeView.setHierarchy(new GenericDraweeHierarchyBuilder(getResources())
                                .setRoundingParams(roundingParams)
                                .build());

                    }

                    Uri uri = Uri.parse(imageurl);
                    draweeView.setImageURI(uri);
                    if (biography.isEmpty()){
                        TextView bioplaceholdertv = view.findViewById(R.id.bioplaceholder);
                        TextView biotv = view.findViewById(R.id.bio);
                        biotv.setText("Set Bio by tapping this Text.\nOnly you can see this");
                    } if(!biography.isEmpty()){
                        TextView biotv = view.findViewById(R.id.bio);
                        biotv.setText(biography);
                    }
                    TextView usernametv = view.findViewById(R.id.username);
                    usernametv.setText("@" + username);
                    TextView fullnametv = view.findViewById(R.id.fullname);
                    fullnametv.setText(fullname);
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    throw databaseError.toException();
                }
            });
        }

        ImageButton settingslistener = view.findViewById(R.id.settings);
        settingslistener.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentsettings = new Intent(getActivity(), SettingsActivity.class);
                startActivity(intentsettings);
            }
        });

        TextView biotv = view.findViewById(R.id.bio);
        biotv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Bio");
// Set up the input
                final EditText input = new EditText(getContext());
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);
// Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        m_Text = input.getText().toString();
                            biotv.setText(m_Text);
                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                            reference.child("bio").setValue(m_Text);
                            if (m_Text.isEmpty()){
                                biotv.setText("Set Bio by tapping this Text. \nOnly you can see this");
                            }
                    }
                });
                AlertDialog dialog = builder.create();

                dialog.setOnShowListener( new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface arg0) {
                        dialog.getButton(dialog.BUTTON_POSITIVE).setBackgroundColor(getResources().getColor(R.color.transparent));
                        dialog.getButton(dialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.dark_blue));
                        dialog.getButton(dialog.BUTTON_POSITIVE).setTextSize(20);

                    }
                });
                dialog.show();

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


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SimpleDraweeView profilebutton = getView().findViewById(R.id.profile_image);

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