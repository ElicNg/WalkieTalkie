package com.elicng.walkietalkie.net;

import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.util.Log;

/**
 * Created by Elic on 15-04-25.
 */
public class NsdHelper {

    private NsdManager nsdManager;
    private final String SERVICE_TYPE = "_http._tcp.";
    private ServerFoundListener serverFoundListener;
    private String serviceName;

    public NsdHelper(NsdManager nsdManager) {
        this.nsdManager = nsdManager;
    }

    private final static String SERVICE_NAME = "WALKIETALKIE";

    public void registerService(int port) {
        NsdServiceInfo nsdServiceInfo = new NsdServiceInfo();
        nsdServiceInfo.setServiceName(SERVICE_NAME);
        nsdServiceInfo.setServiceType(SERVICE_TYPE);
        nsdServiceInfo.setPort(port);

        nsdManager.registerService(nsdServiceInfo, NsdManager.PROTOCOL_DNS_SD, new NsdManager.RegistrationListener() {
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
                log("onServiceRegistered " + serviceInfo.getServiceName());
                serviceName = serviceInfo.getServiceName();
            }

            @Override
            public void onServiceUnregistered(NsdServiceInfo serviceInfo) {
                log("onServiceUnregistered");
            }
        });
    }

    public void initDiscovery(ServerFoundListener serverFoundListener) {
        this.serverFoundListener = serverFoundListener;
        nsdManager.discoverServices(SERVICE_TYPE, NsdManager.PROTOCOL_DNS_SD, new DiscoveryListener());
    }

    private void log(String message) {
        Log.d("com.elicng.walkietalkie", message);
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
            if (!serviceInfo.getServiceType().equals(SERVICE_TYPE)) {
                log("Unknown service type");
            } else if (serviceName != null && serviceInfo.getServiceName().equals(serviceName)) {
                log("Discovered ourself! Skip.");
            } else if (serviceInfo.getServiceName().contains(SERVICE_NAME)) {
                // Found a service! Try to resolve the ip and port!
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
