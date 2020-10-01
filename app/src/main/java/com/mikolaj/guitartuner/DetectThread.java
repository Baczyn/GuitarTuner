package com.mikolaj.guitartuner;

import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.mikolaj.guitartuner.detection.FrequencyConverter;
import com.mikolaj.guitartuner.detection.Note;
import com.mikolaj.guitartuner.detection.YinEstimator;
import com.mikolaj.guitartuner.recorder.Recorder;
import com.mikolaj.guitartuner.views.Tuner;

public class DetectThread extends Thread {

    private Handler handler;
    private float pitch;
    private Tuner tuner;
    private Recorder recorder;
    private boolean shouldBeStop = false;
    private FrequencyConverter frequencyConverter;

    public DetectThread(Tuner tuner) {
        this.handler = new Handler();
        frequencyConverter = new FrequencyConverter();
        this.tuner = tuner;
    }

    public void startDetection() {
        this.start();
    }

    public void stopDetection() {
        shouldBeStop = true;
        recorder.stopRecording();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void run() {
        recorder = new Recorder();
        YinEstimator yinEstimator = new YinEstimator(recorder.readSize);
        recorder.startRecording();

        while (!shouldBeStop) {
            pitch = yinEstimator.detect(recorder.readNextBuffer(), recorder.getAudioConfiguration().getSampleRate());
            final Note note = frequencyConverter.getNote(pitch);

            handler.post(new Runnable() {
                @Override
                public void run() {
                    tuner.updateView(note.getFrequency() + "Hz", note.getCent(), note.getNote());
                }
            });

        }
    }


}
