package com.mikolaj.guitartuner;


import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.mikolaj.guitartuner.views.Plotter;
import com.mikolaj.guitartuner.views.Tuner;

public class MainActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    private final int MY_PERMISSIONS_RECORD_AUDIO = 1;

    private Tuner tuner;
    private Plotter plotter;
    private DetectThread detectThread;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tuner = findViewById(R.id.tuner);
        button = findViewById(R.id.button);
        plotter = findViewById(R.id.plotter);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startTuningPreview();
            }
        });

    }

    private void startTuning() {

        if ((detectThread == null) || (!detectThread.isAlive())) {
            button.setText("Stop");
            detectThread = new DetectThread(tuner,plotter);
            detectThread.startDetection();

        } else {
            detectThread.stopDetection();
            button.setText("Start");
        }
    }

    private void startTuningPreview() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            // Permission is already available, start camera preview
            startTuning();

        } else {
            // Permission is missing and must be requested.
            requestRecordPermission();
        }
    }

    private void requestRecordPermission() {
        // Permission has not been granted and must be requested.
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO)) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, MY_PERMISSIONS_RECORD_AUDIO);

        } else {
            // Request the permission. The result will be received in onRequestPermissionResult().
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, MY_PERMISSIONS_RECORD_AUDIO);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == MY_PERMISSIONS_RECORD_AUDIO) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permissions is granted", Toast.LENGTH_LONG).show();
                startTuning();
            } else {

                Toast.makeText(this, "Permissions Denied to record audio\n" +
                        "          Please grant permissions", Toast.LENGTH_LONG).show();
            }
        }
    }
}
