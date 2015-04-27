package com.elicng.walkietalkie.audios;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

import com.elicng.walkietalkie.Properties;

/**
 * Created by Elic on 15-04-25.
 */
public class AudioRecorderRunnable implements Runnable {

    private AudioRecord audioRecord;
    private boolean isRecording = true;
    private byte[] audioBuffer;
    private AudioRecorderHandler handler;

    public AudioRecorderRunnable(AudioRecorderHandler handler) {
        this.handler = handler;
        audioBuffer = new byte[Properties.BUFFER_SIZE];
    }

    @Override
    public void run() {

        audioRecord =
                new AudioRecord(
                        MediaRecorder.AudioSource.VOICE_COMMUNICATION,
                        Properties.SAMPLING_RATE,
                        AudioFormat.CHANNEL_IN_MONO,
                        AudioFormat.ENCODING_PCM_16BIT,
                        Properties.BUFFER_SIZE
                );

        audioRecord.startRecording();

        int audioRead;

        while (isRecording) {
            audioRead = audioRecord.read(audioBuffer, 0, Properties.BUFFER_SIZE);

            if ((audioRead == AudioRecord.ERROR) ||
                (audioRead == AudioRecord.ERROR_BAD_VALUE) ||
                (audioRead == AudioRecord.ERROR_INVALID_OPERATION) ||
                (audioRead <= 0)) {
                // Error while reading. Leaving the loop.
                log("Error while recording");
                continue;
            }

            if (handler != null) {
                handler.onRecording(audioBuffer);
            }

        }

        // audioTrack.stop();
        audioRecord.stop();
        audioRecord = null;
    }

    public synchronized void stopRecording() {
        isRecording = false;
    }


    private void log(String message) {
        Log.d("com.elicng.walkietalkie", message);
    }

    public interface AudioRecorderHandler {
        void onRecording(byte[] buffer);
    }
}
