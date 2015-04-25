package com.elicng.walkietalkie.audio;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

/**
 * Created by Elic on 15-04-25.
 */
public class AudioRecorderRunnable implements Runnable {

    private final int SAMPLING_RATE = 44100;

    private AudioRecord audioRecord;
    private boolean isRecording = true;
    private byte[] audioBuffer;
    private final int bufferSize;
    private AudioRecorderHandler handler;

    public AudioRecorderRunnable(AudioRecorderHandler handler) {
        this.handler = handler;
        bufferSize = AudioRecord.getMinBufferSize(SAMPLING_RATE,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT);

        audioBuffer = new byte[bufferSize];

    }

    @Override
    public void run() {

        audioRecord =
                new AudioRecord(
                        MediaRecorder.AudioSource.VOICE_COMMUNICATION,
                        SAMPLING_RATE,
                        AudioFormat.CHANNEL_IN_MONO,
                        AudioFormat.ENCODING_PCM_16BIT,
                        bufferSize
                );


        audioRecord.startRecording();

        int audioRead;
        while (isRecording) {
            audioRead = audioRecord.read(audioBuffer, 0, bufferSize);

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
        audioRecord.stop();
    }

    public synchronized void stopRecording() {
        isRecording = false;
    }


    private void log(String message) {
        Log.d("com.elicng.walkietalkie", message);
    }
}
