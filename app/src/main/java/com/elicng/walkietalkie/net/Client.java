package com.elicng.walkietalkie.net;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;

import com.elicng.walkietalkie.Properties;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

/**
 * Connect to server through a socket. Play audio to speakers.
 */
public class Client {

    private boolean listening = true;
    private String hostName;
    private int portNumber;

    public Client() {

    }

    public void listen(final String hostName, final int portNumber) {
        this.hostName = hostName;
        this.portNumber = portNumber;
        Thread listeningThread = new Thread(new Runnable(){
            @Override
            public void run() {
                try {
                    Socket socket = new Socket(hostName, portNumber);
                    Log.d("com.elicng.walkietalkie", "Listening to server : " + hostName);
                    InputStream inputStream = socket.getInputStream();

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
                        if (read != -1) {
                            // Write audio buffer to the AudioTrack.
                            audioTrack.write(audioBuffer, 0, audioBuffer.length);
                        } else {
                            listening = false;
                        }
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

    public int getPort() {
        return portNumber;
    }

    public String getAddress() {
        return hostName;
    }
}
