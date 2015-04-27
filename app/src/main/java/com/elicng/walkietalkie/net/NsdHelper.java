package com.elicng.walkietalkie.net;

import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.util.Log;

import java.net.InetAddress;

/**
 * Created by Elic on 15-04-25.
 */
public class NsdHelper {

    private NsdManager nsdManager;
    private final String serviceType = "_http._tcp.";
    private ServerFoundListener serverFoundListener;

    public NsdHelper(NsdManager nsdManager) {
        this.nsdManager = nsdManager;
    }

    private final static String SERVICE_NAME = "com.elicng.walkietalkie";

    public void registerService(int port) {
        NsdServiceInfo nsdServiceInfo = new NsdServiceInfo();
        nsdServiceInfo.setServiceName(SERVICE_NAME);
        nsdServiceInfo.setPort(port);
        nsdServiceInfo.setServiceType(serviceType);

        nsdManager.registerService(nsdServiceInfo, NsdManager.PROTOCOL_DNS_SD, new RegistrationListener());
    }

    public void initDiscovery(ServerFoundListener serverFoundListener) {
        this.serverFoundListener = serverFoundListener;
        nsdManager.discoverServices(serviceType, NsdManager.PROTOCOL_DNS_SD, new DiscoveryListener());
    }

    private void log(String message) {
        Log.d("com.elicng.walkietalkie", message);
    }

    class RegistrationListener implements NsdManager.RegistrationListener {
        @Override
        public void onRegistrationFailed(NsdServiceInfo serviceInfo, int errorCode) {
            log("onRegistrationFailed");
        }

        @Override
        public void onUnregistrationFailed(NsdServiceInfo serviceInfo, int errorCode) {
            log("onUnregistrationFailed");
        }

        @Override
        public void onServiceRegistered(NsdServiceInfo serviceInfo) {
            log("onServiceRegistered");
        }

        @Override
        public void onServiceUnregistered(NsdServiceInfo serviceInfo) {
            log("onServiceUnregistered");
        }
    }

    class DiscoveryListener implements NsdManager.DiscoveryListener {
        @Override
        public void onStartDiscoveryFailed(String serviceType, int errorCode) {
            log("onStartDiscoveryFailed " + serviceType + " " + errorCode);
        }

        @Override
        public void onStopDiscoveryFailed(String serviceType, int errorCode) {
            log("onStopDiscoveryFailed " + errorCode);
        }

        @Override
        public void onDiscoveryStarted(String serviceType) {
            log("onDiscoveryStarted");
        }

        @Override
        public void onDiscoveryStopped(String serviceType) {
            log("onDiscoveryStopped");
        }

        @Override
        public void onServiceFound(NsdServiceInfo serviceInfo) {
            log("onServiceFound");
            if (serviceInfo.getServiceName() !=  SERVICE_NAME) {
                nsdManager.resolveService(serviceInfo, new ResolveListener());
            }
        }

        @Override
        public void onServiceLost(NsdServiceInfo serviceInfo) {
            log("onServiceLost");
        }
    }

    class ResolveListener implements NsdManager.ResolveListener {
        @Override
        public void onResolveFailed(NsdServiceInfo serviceInfo, int errorCode) {
            log("onResolveFailed");
        }

        @Override
        public void onServiceResolved(NsdServiceInfo serviceInfo) {
            log("onServiceResolved!!!!!!");
            if (serverFoundListener != null) {
                serverFoundListener.onServerFound(serviceInfo.getHost().getHostAddress(), serviceInfo.getPort());
            }
        }
    }

    public interface ServerFoundListener {
        void onServerFound(String ipAddress, int port);
    }
}
