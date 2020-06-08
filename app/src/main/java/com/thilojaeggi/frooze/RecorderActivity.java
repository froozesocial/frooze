package com.thilojaeggi.frooze;

import android.icu.text.AlphabeticIndex;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.trinity.OnRecordingListener;
import com.trinity.camera.CameraCallback;
import com.trinity.camera.TrinityPreviewView;
import com.trinity.listener.OnRenderListener;
import com.trinity.record.PreviewResolution;
import com.trinity.record.TrinityRecord;

public class RecorderActivity extends AppCompatActivity {
    TrinityRecord mRecord;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recorder);

    }
}
