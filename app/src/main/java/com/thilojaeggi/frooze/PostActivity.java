package com.thilojaeggi.frooze;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;

public class PostActivity extends AppCompatActivity {

Uri videoUri;
String myUrl = "";
StorageTask uploadTask;
StorageReference storageReference;

ImageView close;
VideoView video_added;
TextView post;
EditText description;
    private static final int PICK_VIDEO_REQUEST = 1001;

@Override
    protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_post);
    close = findViewById(R.id.close);
    video_added = findViewById(R.id.video_added);
    post = findViewById(R.id.post);
    description = findViewById(R.id.description);

    storageReference = FirebaseStorage.getInstance().getReference("posts");

    close.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            startActivity(new Intent(PostActivity.this, MainActivity.class));
            finish();
        }
    });

    post.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            uploadVideo();
        }
    });

    Intent intent = new Intent(Intent.ACTION_PICK);
    intent.setType("video/*");
    intent.setAction(Intent.ACTION_GET_CONTENT);
    startActivityForResult(Intent.createChooser(intent, "Select Video"), PICK_VIDEO_REQUEST );
}

private String getFileExtension(Uri uri) {
    ContentResolver contentResolver = getContentResolver();
    MimeTypeMap mime = MimeTypeMap.getSingleton();
    return mime.getExtensionFromMimeType(contentResolver.getType(uri));
}

    private void uploadVideo() {
    ProgressDialog progressDialog = new ProgressDialog(this);
    progressDialog.setMessage("Uploading...");
    progressDialog.show();
            if (videoUri != null){
                StorageReference filereference = storageReference.child(System.currentTimeMillis()
                        + "."+ getFileExtension(videoUri));


                uploadTask = filereference.putFile(videoUri);
                uploadTask.continueWithTask(new Continuation() {
                    @Override
                    public Object then(@NonNull Task task) throws Exception {
                        if (!task.isSuccessful()){
                            throw task.getException();
                        }

                        return filereference.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()){
                            Uri downloadUri = task.getResult();
                            myUrl = downloadUri.toString();

                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");

                            String postid = reference.push().getKey();
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("postid", postid);
                            hashMap.put("postvideo", myUrl);
                            hashMap.put("description", description.getText().toString());
                            hashMap.put("publisher", FirebaseAuth.getInstance().getCurrentUser().getUid());

                            reference.child(postid).setValue(hashMap);

                            progressDialog.dismiss();

                            startActivity(new Intent(PostActivity.this, MainActivity.class));
                            finish();

                        } else {
                            Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_LONG).show();

                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), ""+e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            } else {
                Toast.makeText(getApplicationContext(),"No Video selected", Toast.LENGTH_LONG).show();
            }
    }
// ctrl + O

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_VIDEO_REQUEST && resultCode == RESULT_OK
            && data != null && data.getData() != null) {
            videoUri = data.getData();
            video_added.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.setLooping(true);
                }
            });
            video_added.setVideoURI(videoUri);
            video_added.start();
        } else {
            Toast.makeText(this, "Something gone wrong", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(PostActivity.this, MainActivity.class));
            finish();
        }
    }
}
