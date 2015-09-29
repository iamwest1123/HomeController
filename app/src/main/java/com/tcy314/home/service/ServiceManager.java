package com.tcy314.home.service;

import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;

import java.util.ArrayList;

/**
 * Created by Matthew Ma on 28/9/2015.
 */
public class ServiceManager {
    private static final String TAG = ServiceManager.class.getSimpleName();

    // Keep track of the service run state.
    private boolean serviceStarted = false;

    // The context from the application.
    private Context context;
    // The intent we will use to start the service and bind activities.
    private Intent mBluetoothLeService = null;
    private ArrayList<String> tagArrayList = new ArrayList<String>();

    public ServiceManager(Context context)
    {
        this.context = context;
        // Create our intent.
        mBluetoothLeService = new Intent(this.context, BluetoothLeService.class);
    }

    /**
     * Determine if the service is started.
     * @return The run state of the service.
     */
    public boolean isServiceStarted()
    {
        return (!tagArrayList.isEmpty());
    }

    /**
     * Start the service.
     */
    public void startService(String tag)
    {
        if (tagArrayList.isEmpty())
            this.context.startService(mBluetoothLeService);
        tagArrayList.add(tag);
    }

    /**
     * Stop the service.
     */
    public void stopService(String tag)
    {
        if (tagArrayList.contains(tag))
            tagArrayList.remove(tag);
        if (tagArrayList.isEmpty())
            this.context.stopService(mBluetoothLeService);
    }
}
