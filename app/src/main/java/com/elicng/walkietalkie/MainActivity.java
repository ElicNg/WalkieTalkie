package com.elicng.walkietalkie;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.audiofx.Visualizer;
import android.net.Uri;
import android.net.nsd.NsdManager;
import android.os.Environment;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import com.elicng.walkietalkie.activities.AudioRecorderBufferActivity;
import com.elicng.walkietalkie.net.NsdHelper;

import java.io.File;
import java.io.IOException;


public class MainActivity extends ActionBarActivity {

    private MediaRecorder mediaRecorder;
    private String filePath;
    private TextToSpeech tts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/latestFile.3gp";

        Button btnRecord = (Button) findViewById(R.id.btnRecord);
        btnRecord.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        if (mediaRecorder == null) {
                            File file = new File(filePath);
                            if (file.exists()) {
                                file.delete();
                            }

                            Visualizer visualizer = new Visualizer(0);
                            visualizer.setDataCaptureListener(new Visualizer.OnDataCaptureListener() {
                                @Override
                                public void onWaveFormDataCapture(Visualizer visualizer, byte[] waveform, int samplingRate) {

                                }

                                @Override
                                public void onFftDataCapture(Visualizer visualizer, byte[] fft, int samplingRate) {

                                }
                            }, Visualizer.getMaxCaptureRate() / 2, false, false);

                            mediaRecorder = new MediaRecorder();
                            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                            mediaRecorder.setOutputFile(filePath);
                            mediaRecorder.setOnInfoListener(new MediaRecorder.OnInfoListener() {
                                @Override
                                public void onInfo(MediaRecorder mr, int what, int extra) {
                                    // On error
                                    log("what:" + what + "|extra:" + extra);
                                }
                            });
                            try {
                                mediaRecorder.prepare();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }


                            mediaRecorder.start();
                        }
                        return true;

                    }
                    case MotionEvent.ACTION_UP: {
                        if (mediaRecorder != null) {
                            mediaRecorder.stop();
                            mediaRecorder.release();
                            mediaRecorder = null;
                        }
                        return true;
                    }
                    default: {
                        // Nothing
                    }
                }
                return false;
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    public void btnPlayLatest_onClick(View view) {
        MediaPlayer mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mediaPlayer.setDataSource(getApplicationContext(), Uri.parse(filePath));
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.start();

    }

    public void btnOpenAudioRecorderBufferActivity_onClick(View view) {
        Intent intent = new Intent(getApplicationContext(), AudioRecorderBufferActivity.class);
        startActivity(intent);
    }

    private void log(String message) {
        Log.d("com.elicng.walkietalkie", message);
    }


}
