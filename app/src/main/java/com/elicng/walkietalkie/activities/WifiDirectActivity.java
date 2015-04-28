package com.elicng.walkietalkie.activities;

import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceInfo;
import android.net.wifi.p2p.nsd.WifiP2pServiceInfo;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.elicng.walkietalkie.R;
import com.elicng.walkietalkie.net.NsdWifiDirectManager;

import java.util.HashMap;
import java.util.Map;

public class WifiDirectActivity extends ActionBarActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_direct);
        NsdWifiDirectManager nsdWifiDirectManager = new NsdWifiDirectManager();

        WifiP2pManager wifiP2pManager = (WifiP2pManager) getSystemService(WIFI_P2P_SERVICE);
        nsdWifiDirectManager.initialize(wifiP2pManager, getApplicationContext(), getMainLooper());
        nsdWifiDirectManager.advertiseService(wifiP2pManager);
        nsdWifiDirectManager.discover(wifiP2pManager);

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
}
