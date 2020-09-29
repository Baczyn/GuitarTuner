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
    private int cent;
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

    int k;
    int i;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void run() {
        recorder = new Recorder();
        YinEstimator yinEstimator = new YinEstimator(recorder.readSize);
        recorder.startRecording();
        i = k = 0;
        while (!shouldBeStop) {
            Note note;
            pitch = yinEstimator.detect(recorder.readNextBuffer(), recorder.getAudioConfiguration().getSampleRate());
            note = frequencyConverter.getNote(pitch);

            int newCent = note.getCent();
            final String noteName = note.getNote();
            final String noteFrequency = note.getFrequency()+"Hz";


            if (newCent > (cent )) {
                for (i = cent; i < newCent; i++) {
                    try {
                        sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            tuner.updateHand(i);
                            tuner.updateNote(noteName);
                            tuner.updateFrequency(noteFrequency);
                        }
                    });
                }

            } else if (newCent < (cent )) {
                for (k = cent; k > newCent; k--) {
                    try {
                        sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            tuner.updateHand(k);
                            tuner.updateNote(noteName);
                            tuner.updateFrequency(noteFrequency);
                        }
                    });
                }
            }
            try {
                sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            cent = newCent;
        }


    }

}
