package com.elicng.walkietalkie.activities;

import android.net.nsd.NsdManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.elicng.walkietalkie.R;
import com.elicng.walkietalkie.audios.AudioRecorderHandler;
import com.elicng.walkietalkie.audios.AudioRecorderRunnable;
import com.elicng.walkietalkie.net.Client;
import com.elicng.walkietalkie.net.NsdHelper;
import com.elicng.walkietalkie.net.Server;

import java.util.ArrayList;
import java.util.Collection;

/**
 * ProgressBar details: http://developer.samsung.com/technical-doc/view.do?v=T000000086
 */

public class AudioRecorderBufferActivity extends ActionBarActivity {

    private AudioRecorderRunnable audioRecorder;
    private Collection<Client> clients = new ArrayList<>();
    private Server server;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_recorder_buffer);

        server = new Server();
        server.start();
        /*server.start(new Server.ClientConnectListener() {
            @Override
            public void onConnect(InputStream stream) {
                AudioTrack audioTrack =
                        new AudioTrack(
                                AudioManager.STREAM_MUSIC,
                                Properties.SAMPLING_RATE,
                                AudioFormat.CHANNEL_OUT_MONO,
                                AudioFormat.ENCODING_PCM_16BIT,
                                Properties.BUFFER_SIZE,
                                AudioTrack.MODE_STREAM);

                audioTrack.play();
                audioTrack.write(stream);
            }
        });*/
        NsdHelper nsdHelper = new NsdHelper((NsdManager) getSystemService(NSD_SERVICE));
        nsdHelper.initDiscovery(new NsdHelper.ServerFoundListener() {
            @Override
            public void onServerFound(String ipAddress, int port) {
                Client client = new Client();
                client.Listen(ipAddress, port);
                clients.add(client);
            }
        });
        nsdHelper.registerService(server.getPort());
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
                    server.writeByte(buffer);
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
