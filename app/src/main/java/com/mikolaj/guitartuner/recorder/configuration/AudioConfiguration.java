package com.mikolaj.guitartuner.recorder.configuration;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

public class AudioConfiguration {

    private final int RECORD_AUDIO_SOURCE = MediaRecorder.AudioSource.DEFAULT;
    private final int RECORD_AUDIO_SAMPLE_RATE = 16000; //Hz
    private final int RECORD_AUDIO_CHANNEL = AudioFormat.CHANNEL_IN_MONO;
    private final int RECORD_AUDIO_FORMAT = AudioFormat.ENCODING_PCM_FLOAT;
    private final int RECORD_AUDIO_BUFFER_SIZE =  AudioRecord.getMinBufferSize(RECORD_AUDIO_SAMPLE_RATE,RECORD_AUDIO_CHANNEL,RECORD_AUDIO_FORMAT); //total size of the buffer (in bytes)
    private final int RECORD_AUDIO_READ_SIZE = RECORD_AUDIO_BUFFER_SIZE / 4 ; // flaot jest 4 razy wiekszy

    public int getReadSize() {
        return RECORD_AUDIO_READ_SIZE;
    }

    public int getRecordAudioSource() { return RECORD_AUDIO_SOURCE; }

    public int getSampleRate() {
        return RECORD_AUDIO_SAMPLE_RATE;
    }

    public int getRecordAudioChannel() {
        return RECORD_AUDIO_CHANNEL;
    }

    public int getRecordAudioFormat() {
        return RECORD_AUDIO_FORMAT;
    }

    public int getRecordAudioBuffSize() {
        return RECORD_AUDIO_BUFFER_SIZE;
    }
}
