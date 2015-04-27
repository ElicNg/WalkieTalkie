package com.elicng.walkietalkie.net;

import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;

/**
 * Created by Elic on 15-04-25.
 */
public class DiscoveryRegistrationListener implements NsdManager.RegistrationListener {
    @Override
    public void onRegistrationFailed(NsdServiceInfo serviceInfo, int errorCode) {

    }

    @Override
    public void onUnregistrationFailed(NsdServiceInfo serviceInfo, int errorCode) {

    }

    @Override
    public void onServiceRegistered(NsdServiceInfo serviceInfo) {

    }

    @Override
    public void onServiceUnregistered(NsdServiceInfo serviceInfo) {

    }
}
