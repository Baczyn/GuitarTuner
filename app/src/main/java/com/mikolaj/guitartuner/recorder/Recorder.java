package com.mikolaj.guitartuner.recorder;

import android.media.AudioRecord;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.mikolaj.guitartuner.recorder.configuration.AudioConfiguration;

public class Recorder {

    private final AudioConfiguration audioConfiguration;
    private final AudioRecord recorder;
    public float[] buffer;
    public final int readSize;

    public Recorder(){
        audioConfiguration = new AudioConfiguration();
        recorder = new AudioRecord( audioConfiguration.getRecordAudioSource(),
                                    audioConfiguration.getSampleRate(),
                                    audioConfiguration.getRecordAudioChannel(),
                                    audioConfiguration.getRecordAudioFormat(),
                                    audioConfiguration.getRecordAudioBuffSize() );
        readSize = audioConfiguration.getReadSize();
        buffer = new float[readSize];

    }



    public void startRecording() { recorder.startRecording();}

    public void stopRecording() { recorder.stop(); }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public float[] readNextBuffer(){
        recorder.read(buffer,0,readSize,AudioRecord.READ_NON_BLOCKING);
        return buffer;
    }

    public AudioConfiguration getAudioConfiguration() {
        return audioConfiguration;
    }
}
