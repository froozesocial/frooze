package com.thilojaeggi.frooze;

import android.Manifest;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;

import com.cloudinary.Cloudinary;
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.cloudinary.android.preprocess.ImagePreprocessChain;
import com.cloudinary.android.preprocess.Limit;
import com.cloudinary.utils.ObjectUtils;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;

import com.thilojaeggi.frooze.Model.Post;

import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class PostActivity extends AppCompatActivity {

    private static final int SELECT_VIDEO = 2;
    private static final int TRIM_VIDEO = 3;
    private static final int REQUEST_VIDEO_TRIMMER = 0x01;
    private static final int REQUEST_STORAGE_READ_ACCESS_PERMISSION = 101;
    static final String EXTRA_VIDEO_PATH = "EXTRA_VIDEO_PATH";
    private static ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        Map config = new HashMap();
        config.put("cloud_name", "frooze");
        MediaManager.init(this, config);
        //ringProgressBar = findViewById(R.id.progress_bar_2);
        //ringProgressBar.setProgress(0);


        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage(getString(R.string.uploading));
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMax(100);
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

        pickFromGallery();

        ImageButton close = findViewById(R.id.close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_VIDEO_TRIMMER) {
                final Uri selectedUri = data.getData();
                if (selectedUri != null) {
                    MediaMetadataRetriever retriever = new MediaMetadataRetriever();
//use one of overloaded setDataSource() functions to set your data source
                    retriever.setDataSource(getApplicationContext(), selectedUri);
                    String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                    long timeInMillisec = Long.parseLong(time);
                    retriever.release();
                    if (timeInMillisec <= 30000) {
                        VideoView preview = findViewById(R.id.video_added);
                        preview.setVideoURI(selectedUri);
                        preview.start();
                        TextView post = findViewById(R.id.post);
                        post.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                uploadVideoCloudinary(selectedUri);
                            }
                        });
                    } else {
                        Toast.makeText(PostActivity.this, getString(R.string.toolong), Toast.LENGTH_SHORT).show();
                        pickFromGallery();
                    }
                        }else {
                    Toast.makeText(PostActivity.this, getString(R.string.novideoselected), Toast.LENGTH_SHORT).show();
                    }
                }
            }
    }



    private void pickFromGallery() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE, "Read Storage", REQUEST_STORAGE_READ_ACCESS_PERMISSION);
        } else {
            Intent intent = new Intent();
            intent.setTypeAndNormalize("video/*");
            intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 30);
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            startActivityForResult(Intent.createChooser(intent, "Video"), REQUEST_VIDEO_TRIMMER);
        }
    }

    private void uploadVideoCloudinary(Uri videoUri) {
        
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        mProgressDialog.show();
        String postid = reference.push().getKey();
        String requestId = MediaManager.get().upload(videoUri)
                .option("public_id", "frooze/posts/" + uid + "/" + postid)
                .option("resource_type", "video")
                .unsigned("frooze")

                .callback(new UploadCallback() {
                    @Override
                    public void onStart(String requestId) {
                        //ringProgressBar.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onProgress(String requestId, long bytes, long totalBytes) {
                        double progress = (double) bytes/totalBytes;
                        progress = progress * 100;
                        Log.i("progress", String.valueOf(progress));
                        //ringProgressBar.setProgress((int)progress);
                        mProgressDialog.setProgress((int)progress);
                    }

                    @Override
                    public void onSuccess(String requestId, Map resultData) {
                        EditText description = findViewById(R.id.description);

                        String text = description.getText().toString();
                        String[] hashtags = text.split(" ");
                        List<String> tags = new ArrayList<String>();

                        for ( String hashtag : hashtags) {
                            if (hashtag.substring(0, 1).equals("#")) {
                                tags.add(hashtag);
                                String hashtagwithouthash = hashtag.replace("#","");
                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Hashtags").child(hashtagwithouthash);
                                reference.child("hashtag").setValue(hashtagwithouthash);
                                reference.child("posts").child(postid).setValue(true);
                            }
                        }
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        String uid = user.getUid();
                        Switch dangerousswitch = findViewById(R.id.dangerousswitch);
                        Boolean switchState = dangerousswitch.isChecked();
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("dangerous", switchState.toString());

                        hashMap.put("postid", postid);
                        hashMap.put("postvideo", "https://res.cloudinary.com/frooze/video/upload/q_auto:eco/frooze/posts/" + uid +"/" + postid + ".m3u8");
                        hashMap.put("description", description.getText().toString());
                        hashMap.put("publisher", FirebaseAuth.getInstance().getCurrentUser().getUid());

                        reference.child(postid).setValue(hashMap);

                        Toast.makeText(getApplicationContext(), getString(R.string.success),Toast.LENGTH_LONG).show();
                        //ringProgressBar.setVisibility(View.INVISIBLE);
                        mProgressDialog.dismiss();
                        finish();
                    }

                    @Override
                    public void onError(String requestId, ErrorInfo error) {
                        Toast.makeText(getApplicationContext(), getString(R.string.fail),Toast.LENGTH_LONG).show();
                        //ringProgressBar.setVisibility(View.INVISIBLE);
                        mProgressDialog.dismiss();
                    }

                    @Override
                    public void onReschedule(String requestId, ErrorInfo error) {

                    }
                })
                .dispatch(getApplicationContext());
    }

    /**
     * Requests given permission.
     * If the permission has been denied previously, a Dialog will prompt the user to grant the
     * permission, otherwise it is requested directly.
     */
    private void requestPermission(final String permission, String rationale, final int requestCode) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.permrequired));
            builder.setMessage(getString(R.string.permdesc));
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ActivityCompat.requestPermissions(PostActivity.this, new String[]{permission}, requestCode);
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            builder.show();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);
        }
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_STORAGE_READ_ACCESS_PERMISSION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    pickFromGallery();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}