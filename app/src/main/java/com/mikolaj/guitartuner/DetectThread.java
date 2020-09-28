package com.mikolaj.guitartuner;

import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.mikolaj.guitartuner.detection.FrequencyConverter;
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

    public void startDetection(){
        this.start();
    }

    public void stopDetection(){
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

        while(!shouldBeStop){
            int newCent;
            pitch = yinEstimator.detect(recorder.readNextBuffer(),recorder.getAudioConfiguration().getSampleRate());
            newCent = frequencyConverter.getNote(pitch);
            if(newCent > (cent +4)) {
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
                        }
                    });
                }
            }
            else if(newCent < (cent -4)){
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
                        }
                    });
                }
            }


            cent = newCent;
        }



}

}
