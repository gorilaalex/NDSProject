package com.example.agostours16.ndsproject.Listeners;

import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;

/**
 * Created by Agos Tours 16 on 03.03.2015.
 */
public class MyRegistrationListener implements NsdManager.RegistrationListener {



    @Override
    public void onRegistrationFailed(NsdServiceInfo serviceInfo, int errorCode) {

    }

    @Override
    public void onUnregistrationFailed(NsdServiceInfo serviceInfo, int errorCode) {

    }

    @Override
    public void onServiceRegistered(NsdServiceInfo serviceInfo) {
        //mServiceName = serviceInfo.getServiceName();
    }

    @Override
    public void onServiceUnregistered(NsdServiceInfo serviceInfo) {

    }

    public void returnServiceName() {

    }
}
