package com.mikolaj.guitartuner;

import android.os.Build;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.mikolaj.guitartuner.detection.FrequencyConverter;
import com.mikolaj.guitartuner.detection.Note;
import com.mikolaj.guitartuner.detection.YinEstimator;
import com.mikolaj.guitartuner.recorder.Recorder;
import com.mikolaj.guitartuner.views.Plotter;
import com.mikolaj.guitartuner.views.Tuner;

import java.util.Arrays;

public class DetectThread extends Thread {

    private Handler handler;
    //views
    private Tuner tuner;
    private Plotter plotter;

    private Recorder recorder;
    private FrequencyConverter frequencyConverter;

    private boolean shouldBeStop = false;

    public DetectThread(Tuner tuner, Plotter plotter) {
        this.handler = new Handler();
        this.frequencyConverter = new FrequencyConverter();
        this.tuner = tuner;
        this.plotter = plotter;
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
        float[] buffer;
        float pitch;
        recorder = new Recorder();
        YinEstimator yinEstimator = new YinEstimator(recorder.readSize);

        recorder.startRecording();

        while (!shouldBeStop) {
            final float maxAmp;
            buffer = recorder.readNextBuffer();

            pitch = yinEstimator.detect(buffer, recorder.getAudioConfiguration().getSampleRate());
            final Note note = frequencyConverter.getNote(pitch);

//            if (buffer.length > 0) {
//                Arrays.sort(buffer);
//                maxAmp = buffer[buffer.length - 1];
//            } else
//                maxAmp = 0;

            try {
                this.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            handler.post(new Runnable() {
                @Override
                public void run() {
                    tuner.updateView(note.getFrequency() + "Hz", note.getCent(), note.getNote());
                    //plotter.addAmplitude(maxAmp);

                }
            });
        }
    }
}

