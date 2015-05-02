package com.elicng.walkietalkie.activities;

import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceInfo;
import android.net.wifi.p2p.nsd.WifiP2pServiceInfo;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.elicng.walkietalkie.Properties;
import com.elicng.walkietalkie.R;
import com.elicng.walkietalkie.audios.AudioRecorderRunnable;
import com.elicng.walkietalkie.net.Client;
import com.elicng.walkietalkie.net.NsdWifiDirectManager;
import com.elicng.walkietalkie.net.Server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class WifiDirectActivity extends ActionBarActivity implements AudioRecorderRunnable.AudioRecorderHandler {

    private NsdWifiDirectManager nsdWifiDirectManager;
    private WifiP2pManager wifiP2pManager;

    private ArrayList<String> discoveredDevices = new ArrayList<>();
    private ArrayAdapter<String> arrayAdapter;
    private ProgressBar audioAmplitude;
    private AudioRecorderRunnable audioRecorder;
    private Server server;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_direct);

        audioAmplitude = (ProgressBar) findViewById(R.id.progressBar);

        // Create a server instance to listen to connecting clients
        server = new Server();
        server.start();

        nsdWifiDirectManager = new NsdWifiDirectManager();

        wifiP2pManager = (WifiP2pManager) getSystemService(WIFI_P2P_SERVICE);
        nsdWifiDirectManager.initialize(wifiP2pManager, getApplicationContext(), getMainLooper());

        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, discoveredDevices);
        ListView discoveredDeviceList = (ListView) findViewById(R.id.discoveredDeviceList);
        discoveredDeviceList.setAdapter(arrayAdapter);

        discoveredDeviceList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String deviceAddress = arrayAdapter.getItem(position);
                nsdWifiDirectManager.connect(deviceAddress, new NsdWifiDirectManager.ConnectionListener() {
                    @Override
                    public void onConnect(String deviceAddress) {
                        Client client = new Client();
                        client.listen(deviceAddress, Properties.SERVER_PORT);
                    }

                    @Override
                    public void onFailure() {

                    }
                });
            }
        });

        setUpMicrophone();
    }

    private void setUpMicrophone() {

        final AudioRecorderRunnable.AudioRecorderHandler audioRecorderHandler = this;

        View btnMicrophone = findViewById(R.id.btnMicrophone);
        //final AudioRecorderRunnable.AudioRecorderHandler audioRecorderHandler = this;

        btnMicrophone.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                TextView textView = (TextView) v;
                switch (event.getAction()) {

                    case MotionEvent.ACTION_DOWN: {
                        if (audioRecorder == null) {
                            audioRecorder = new AudioRecorderRunnable(audioRecorderHandler);
                            new Thread(audioRecorder).start();
                        }
                        textView.setTextColor(0x00FF00FF);
                        return true;
                    }
                    case MotionEvent.ACTION_HOVER_EXIT:
                    case MotionEvent.ACTION_UP: {
                        if (audioRecorder != null) {
                            audioRecorder.stopRecording();
                            audioRecorder = null;
                        }
                        textView.setTextColor(0xFF33B5E5);

                        audioAmplitude.setProgress(0);
                        return true;
                    }

                }

                return false;
            }
        });
    }

    public void btnStartDiscovery_onClick(View view) {
        nsdWifiDirectManager.discover(new NsdWifiDirectManager.DiscoveryListener() {
            @Override
            public void onDiscovery(String instanceName, String registrationType, WifiP2pDevice srcDevice) {
                boolean isNewItem = true;
                for (int i = 0; i < arrayAdapter.getCount(); i++) {
                    if (arrayAdapter.getItem(i) == srcDevice.deviceAddress) {
                        isNewItem = false;
                        break;
                    }
                }
                if (isNewItem) {
                    arrayAdapter.add(srcDevice.deviceAddress);
                }

                // nsdWifiDirectManager.connect(srcDevice.deviceAddress);
            }

            @Override
            public void onFailure() {

            }
        });
    }

    public void btnStartAdvertise_onClick(View view) {
        nsdWifiDirectManager.advertiseService(wifiP2pManager);
    }

    public void btnStopAdvertise_onClick(View view) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_wifi_direct, menu);
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

    @Override
    public void onRecording(byte[] buffer) {
        server.writeByte(buffer);

        // Find the recording amplitude ( on 100 )
        int sum = 0;
        for (int i = 0; i < buffer.length; i++) {
            sum += buffer[i] * buffer[i];
        }
        final int amplitude = (int) Math.sqrt(sum / buffer.length);

        // set the visual
        audioAmplitude.setProgress(amplitude);
    }
}
