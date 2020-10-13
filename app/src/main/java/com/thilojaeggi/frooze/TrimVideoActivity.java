package com.thilojaeggi.frooze;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import life.knowledge4.videotrimmer.K4LVideoTrimmer;
import life.knowledge4.videotrimmer.interfaces.OnTrimVideoListener;


public class TrimVideoActivity extends AppCompatActivity implements OnTrimVideoListener {
    private K4LVideoTrimmer mVideoTrimmer;
    private ProgressDialog mProgressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trim);

        Intent extraIntent = getIntent();
        String path = "";

        if (extraIntent != null) {
            path = extraIntent.getStringExtra("EXTRA_VIDEO_PATH");
        }

        //setting progressbar
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage("Wird getrimmt");

        mVideoTrimmer = ((K4LVideoTrimmer) findViewById(R.id.timeLine));
        if (mVideoTrimmer != null) {
            mVideoTrimmer.setMaxDuration(17);
            mVideoTrimmer.setOnTrimVideoListener(this);
         //   mVideoTrimmer.setDestinationPath("/storage/emulated/0/frooze/Media/");
            mVideoTrimmer.setVideoURI(Uri.parse(path));
        }
    }



    @Override
    public void getResult(final Uri uri) {
        mProgressDialog.cancel();
        Intent intent = new Intent();
        intent.setData(uri);
        setResult(PostActivity.RESULT_OK ,intent);
        finish();
    }

    @Override
    public void cancelAction() {
        mProgressDialog.cancel();
        mVideoTrimmer.destroy();
        finish();
    }



}