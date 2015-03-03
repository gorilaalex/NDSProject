package com.example.agostours16.ndsproject;

import android.content.Context;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.util.Log;

import java.net.InetAddress;
import java.net.ServerSocket;

/**
 * Created by Agos Tours 16 on 03.03.2015.
 */
public class NsdSI {

    Context mContext;
    NsdManager mNsdManager;
    NsdManager.DiscoveryListener mDiscoveryListener;
    NsdManager.RegistrationListener mRegistrationListener;
    NsdManager.ResolveListener mResolveListener;

    public static final String TAG = "NsdSI";

    public static final String SERVICE_TYPE = "_http._tcp.";
    public String mServiceName = "GorilaChat";

    NsdServiceInfo mService;

    ServerSocket mServerSocket;

    public NsdSI(Context context) {
        mContext = context;
        mNsdManager = (NsdManager) context.getSystemService(Context.NSD_SERVICE);
    }

    public void registerService(int port){
        //create the nsd service info object and populate it
        NsdServiceInfo serviceInfo = new NsdServiceInfo();

        //the name is the subject to change based on conflicts
        //with other services advertised on the same network
        serviceInfo.setServiceName(mServiceName);
        serviceInfo.setServiceType(SERVICE_TYPE);
        serviceInfo.setPort(port);

        mNsdManager.registerService(
                serviceInfo, NsdManager.PROTOCOL_DNS_SD, new NsdManager.RegistrationListener() {
                    @Override
                    public void onRegistrationFailed(NsdServiceInfo serviceInfo, int errorCode) {

                    }

                    @Override
                    public void onUnregistrationFailed(NsdServiceInfo serviceInfo, int errorCode) {

                    }

                    @Override
                    public void onServiceRegistered(NsdServiceInfo serviceInfo) {
                        mServiceName = serviceInfo.getServiceName();
                    }

                    @Override
                    public void onServiceUnregistered(NsdServiceInfo serviceInfo) {

                    }
                });
    }

   /* public void initializeServerSocket(){
        //initialize a server socket on the next available port.
        mServerSocket = new ServerSocket(0);
        int mLocalPort = mServerSocket.getLocalPort();
    }*/

    public void initializeDiscoveryListener() {
        //instantiate a new discovery listener
        mDiscoveryListener = new NsdManager.DiscoveryListener() {
            @Override
            public void onStartDiscoveryFailed(String serviceType, int errorCode) {
                Log.e(TAG, "Discovery start failed: Error code : " + errorCode);
                mNsdManager.stopServiceDiscovery(this);
            }

            @Override
            public void onStopDiscoveryFailed(String serviceType, int errorCode) {
                Log.e(TAG, "Discovery stop failed: Error code : " + errorCode);
                mNsdManager.stopServiceDiscovery(this);
            }

            @Override
            public void onDiscoveryStarted(String serviceType) {
                Log.d(TAG,"Service discovered started : "+ serviceType);
            }

            @Override
            public void onDiscoveryStopped(String serviceType) {
                Log.d(TAG,"Service discovered stopped " + serviceType);
            }

            @Override
            public void onServiceFound(NsdServiceInfo serviceInfo) {
                //a service is found, do something with it
                Log.d(TAG,"Service discovery success " + serviceInfo);
                if(!serviceInfo.getServiceType().equals(SERVICE_TYPE)){
                    //service type is the string containing the protocol
                    //and transport layer for this service
                    Log.d(TAG,"Unknown service type: " + serviceInfo.getServiceType());
                }
                else if(serviceInfo.getServiceName().equals(mServiceName)){
                    //the name of the service tells the user what they'd be
                    //connecting to. It could be Bob's chat app.
                    Log.d(TAG,"Same machine: " + mServiceName);
                }
                else if(serviceInfo.getServiceName().contains(mServiceName)){
                    mNsdManager.resolveService(serviceInfo,new NsdManager.ResolveListener() {
                        @Override
                        public void onResolveFailed(NsdServiceInfo serviceInfo, int errorCode) {
                            Log.e(TAG, "Resolve Failed: " + serviceInfo);
                        }
                        @Override
                        public void onServiceResolved(NsdServiceInfo serviceInfo) {
                            Log.i(TAG, "Service Resolved: " + serviceInfo);
                        }
                    });
                }
            }

            @Override
            public void onServiceLost(NsdServiceInfo serviceInfo) {
                //when the network service is no longer available
                // internal bookkeeping code goes here.
                Log.e(TAG,"Service lost "+ serviceInfo);
                if (mService == serviceInfo) {
                    mService = null;
                }
            }
        };
    }

    //when app finds a service in the network, first determine the connection information for
    // that service, using resolveService()method
    public void initializeResolveListener() {
        mResolveListener = new NsdManager.ResolveListener() {
            @Override
            public void onResolveFailed(NsdServiceInfo serviceInfo, int errorCode) {
                //call when the resolve fails. Use error code to debug.
                Log.e(TAG,"Resolve failed : "+ errorCode);
            }

            @Override
            public void onServiceResolved(NsdServiceInfo serviceInfo) {
                Log.e(TAG,"Resolve succeded. "+ serviceInfo);

                if(serviceInfo.getServiceName().equals(mService)){
                    Log.d(TAG,"Same ip.");
                    return;
                }

                mService = serviceInfo;
               // int port = mService.getPort();
                //InetAddress host = mService.getHost();
            }
        };
    }

    public void initializeRegistrationListener () {
        mRegistrationListener = new NsdManager.RegistrationListener() {
            @Override
            public void onRegistrationFailed(NsdServiceInfo serviceInfo, int errorCode) {

            }

            @Override
            public void onUnregistrationFailed(NsdServiceInfo serviceInfo, int errorCode) {

            }

            @Override
            public void onServiceRegistered(NsdServiceInfo serviceInfo) {
                mServiceName = serviceInfo.getServiceName();
            }

            @Override
            public void onServiceUnregistered(NsdServiceInfo serviceInfo) {

            }
        };
    }
    public void discoverServices() {
        mNsdManager.discoverServices(SERVICE_TYPE,NsdManager.PROTOCOL_DNS_SD,new NsdManager.DiscoveryListener() {
            @Override
            public void onDiscoveryStarted(String regType) {
                Log.d(TAG, "Service discovery started");
            }

            @Override
            public void onServiceFound(NsdServiceInfo service) {
                Log.d(TAG, "Service discovery success" + service);
                if (!service.getServiceType().equals(SERVICE_TYPE)) {
                    Log.d(TAG, "Unknown Service Type: " + service.getServiceType());
                } else {
                    if (service.getServiceName().equals(mServiceName)) {
                        Log.d(TAG, "Same machine: " + mServiceName);
                    } else {
                        if (service.getServiceName().contains(mServiceName)) {
                            mNsdManager.resolveService(service, new NsdManager.ResolveListener() {
                                @Override
                                public void onResolveFailed(NsdServiceInfo serviceInfo, int errorCode) {

                                }

                                @Override
                                public void onServiceResolved(NsdServiceInfo serviceInfo) {
                                    Log.e(TAG,"Resolve succeded. "+ serviceInfo);

                                    if(serviceInfo.getServiceName().equals(mService)){
                                        Log.d(TAG,"Same ip.");
                                        return;
                                    }

                                    mService = serviceInfo;
                                }
                            });
                        }
                    }
                }
            }

            @Override
            public void onServiceLost(NsdServiceInfo service) {
                Log.e(TAG, "service lost" + service);
                if (mService == service) {
                    mService = null;
                }
            }

            @Override
            public void onDiscoveryStopped(String serviceType) {
                Log.i(TAG, "Discovery stopped: " + serviceType);
            }

            @Override
            public void onStartDiscoveryFailed(String serviceType, int errorCode) {
                Log.e(TAG, "Discovery failed: Error code:" + errorCode);
                mNsdManager.stopServiceDiscovery(this);
            }

            @Override
            public void onStopDiscoveryFailed(String serviceType, int errorCode) {
                Log.e(TAG, "Discovery failed: Error code:" + errorCode);
                mNsdManager.stopServiceDiscovery(this);
            }
        });
    }

    public void stopDiscovery() {
        mNsdManager.stopServiceDiscovery(mDiscoveryListener);
    }

    public void resolveService(){
        mNsdManager.resolveService(mService,mResolveListener);
    }

    public NsdServiceInfo getChosenServiceInfo() {
        return mService;
    }


    public void tearDown() {
        mNsdManager.unregisterService(mRegistrationListener);
        stopDiscovery();
    }
}
