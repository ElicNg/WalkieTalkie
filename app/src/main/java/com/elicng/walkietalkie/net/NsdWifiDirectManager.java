package com.elicng.walkietalkie.net;

import android.content.Context;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceInfo;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest;
import android.os.Looper;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Elic on 15-04-27.
 */
public class NsdWifiDirectManager {

    private WifiP2pManager.Channel channel;

    public void initialize(WifiP2pManager wifiP2pManager, Context srcContext, Looper srcLooper) {
        channel = wifiP2pManager.initialize(srcContext, srcLooper, new WifiP2pManager.ChannelListener() {
            @Override
            public void onChannelDisconnected() {
                log("onChannelDisconnected");
            }
        });
    }

    public void advertiseService(WifiP2pManager wifiP2pManager) {
        Map record = new HashMap();
        record.put("listenport", String.valueOf(0));
        record.put("buddyname", "John Doe" + (int) (Math.random() * 1000));
        record.put("available", "visible");

        WifiP2pDnsSdServiceInfo serviceInfo = WifiP2pDnsSdServiceInfo.newInstance("WifiDirectTest", "_presence._tcp", record);
        wifiP2pManager.addLocalService(channel, serviceInfo, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                log("Advertise:onSuccess");
            }

            @Override
            public void onFailure(int reason) {
                log("Advertise:onFailure");
            }
        });
    }

    public void discover(WifiP2pManager wifiP2pManager) {
        wifiP2pManager.setDnsSdResponseListeners(channel, new WifiP2pManager.DnsSdServiceResponseListener() {
            @Override
            public void onDnsSdServiceAvailable(String instanceName, String registrationType, WifiP2pDevice srcDevice) {
                log("onDnsSdServiceAvailable ~ " + instanceName + " ~ " + registrationType + " ~ " + srcDevice.deviceAddress);
            }
        }, new WifiP2pManager.DnsSdTxtRecordListener() {
            @Override
            public void onDnsSdTxtRecordAvailable(String fullDomainName, Map<String, String> txtRecordMap, WifiP2pDevice srcDevice) {
                log("onDnsSdTxtRecordAvailable ~ " + fullDomainName + " ~ " + srcDevice.deviceAddress);
            }
        });

        WifiP2pDnsSdServiceRequest serviceRequest = WifiP2pDnsSdServiceRequest.newInstance();
        wifiP2pManager.addServiceRequest(channel, serviceRequest, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                log("addServiceRequest:onSuccess");
            }

            @Override
            public void onFailure(int reason) {
                log("addServiceRequest:onFailure");
            }
        });

        wifiP2pManager.discoverServices(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                log("discoverServices:onSuccess");
            }

            @Override
            public void onFailure(int reason) {
                log("discoverServices:onFailure");
            }
        });
    }

    private void log(String message) {
        Log.d("com.elicng.wifidirect", message);
    }
}
