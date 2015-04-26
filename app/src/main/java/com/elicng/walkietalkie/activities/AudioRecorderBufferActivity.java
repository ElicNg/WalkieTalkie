package com.elicng.walkietalkie.activities;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.elicng.walkietalkie.R;
import com.elicng.walkietalkie.audio.AudioRecorderHandler;

import com.elicng.walkietalkie.audio.AudioRecorderRunnable;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * ProgressBar details: http://developer.samsung.com/technical-doc/view.do?v=T000000086
 */

public class AudioRecorderBufferActivity extends ActionBarActivity {

    private AudioRecorderRunnable audioRecorder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_recorder_buffer);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_audio_recorder_buffer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void btnStartRecording_onClick(View view) {
        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);


        if (audioRecorder == null) {
            audioRecorder = new AudioRecorderRunnable(new AudioRecorderHandler() {
                @Override
                public void onRecording(byte[] buffer) {
                    int sum = 0;
                    for (int i = 0; i < buffer.length; i++) {
                        sum += buffer[i] * buffer[i];
                    }
                    final int amplitude = (int) Math.sqrt(sum / buffer.length);
                    progressBar.setProgress(amplitude);
                    log("Amplitude: " + amplitude);

                }
            });
            new Thread(audioRecorder).start();
        }

    }

    public void btnStopRecording_onClick(View view) {
        if (audioRecorder != null) {
            audioRecorder.stopRecording();
            audioRecorder = null;

        }

    }

    private void log(String message) {
        Log.d("com.elicng.walkietalkie", message);
    }
}
