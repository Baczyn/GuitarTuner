package com.mikolaj.guitartuner;


import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.mikolaj.guitartuner.views.Tuner;

public class MainActivity extends AppCompatActivity {
    private final int MY_PERMISSIONS_RECORD_AUDIO = 1;
    private boolean permissionIsGranted;

    private Tuner tuner;
    private DetectThread detectThread;
    Button button;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tuner = findViewById(R.id.tuner);
        button = findViewById(R.id.button);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (getPermission()) {
                    Log.i("ELO", "jestem 1");
                    if ((detectThread == null) || (!detectThread.isAlive())) {
                        button.setText("Stop");
                        detectThread = new DetectThread(tuner);
                        detectThread.startDetection();
                        Log.i("ELO", "jestem 2");

                    } else {
                        detectThread.stopDetection();
                        button.setText("Start");
                        Log.i("ELO", "jestem 3");
                    }
                    Log.i("ELO", "jestem 4");

                }
                Log.i("ELO", "jestem 5");
            }

        });


    }


    boolean getPermission() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            permissionIsGranted = true;
        } else if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO)) {
            Toast.makeText(this, "Please grant permissions to record audio", Toast.LENGTH_LONG).show();
            //Give user option to still opt-in the permissions
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, MY_PERMISSIONS_RECORD_AUDIO);
            Log.i("ELO", "loguje po request1");
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, MY_PERMISSIONS_RECORD_AUDIO);
            Log.i("ELO", "loguje po request2");
        }
        return permissionIsGranted;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_RECORD_AUDIO: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permissions is granted", Toast.LENGTH_LONG).show();

                    // permission was granted, yay!
                    //recordAudio();
//                    button.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            if ((detectThread == null) || (!detectThread.isAlive())) {
//                                button.setText("Stop");
//                                detectThread = new DetectThread(tuner);
//                                detectThread.startDetection();
//
//                            } else {
//                                detectThread.stopDetection();
//                                button.setText("Start");
//                            }
//
//                        }
//                    });
                    permissionIsGranted = true;
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "Permissions Denied to record audio", Toast.LENGTH_LONG).show();
                    permissionIsGranted = false;
                }
                return;
            }
        }

    }

}
