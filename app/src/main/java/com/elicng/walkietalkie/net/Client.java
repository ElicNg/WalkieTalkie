package com.elicng.walkietalkie.net;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;

import com.elicng.walkietalkie.Properties;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;

/**
 * Created by Elic on 15-04-26.
 */
public class Client {

    private boolean listening = true;
    public Client() {

    }

    public void Listen(final String hostName, final int portNumber) {
        Thread listeningThread = new Thread(new Runnable(){
            @Override
            public void run() {
                try {
                    Socket socket = new Socket(hostName, portNumber);
                    Log.d("com.elicng.walkietalkie", "Listening to server : " + hostName);
                    InputStream inputStream = socket.getInputStream();
                    OutputStream outputStream = socket.getOutputStream();

                    AudioTrack audioTrack =
                            new AudioTrack(
                                    AudioManager.STREAM_MUSIC,
                                    Properties.SAMPLING_RATE,
                                    AudioFormat.CHANNEL_OUT_MONO,
                                    AudioFormat.ENCODING_PCM_16BIT,
                                    Properties.BUFFER_SIZE,
                                    AudioTrack.MODE_STREAM);

                    audioTrack.play();
                    byte[] audioBuffer = new byte[Properties.BUFFER_SIZE];
                    int read;
                    while (listening) {
                        read = inputStream.read(audioBuffer);
                        audioTrack.write(audioBuffer, 0, audioBuffer.length);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        listeningThread.start();
    }

    public void StopListening() {
        listening = false;
    }
}
