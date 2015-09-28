package com.tcy314.home.alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.tcy314.home.DBnClass.Appliance;
import com.tcy314.home.DBnClass.BLE;
import com.tcy314.home.DBnClass.ControllerDbHelper;
import com.tcy314.home.DBnClass.Event;
import com.tcy314.home.BaseApplication;

/**
 * Created by Matthew Ma on 24/9/2015.
 */
public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        ControllerDbHelper mDbHelper = ((BaseApplication)context.getApplicationContext()).getDbHelper();
        int eventId = intent.getIntExtra(Alarm.EVENT_ID, -1);
        int startEnd = intent.getIntExtra(Alarm.START_END, -1);
        Event event = mDbHelper.getEventByPrimaryKey(eventId);
        if (event == null) {
            Log.e("AlarmReceived","event is null");
            return;
        }
        Appliance appliance = mDbHelper.getApplianceByPrimaryKey(event.getAppk());
        if (appliance == null) {
            Log.e("AlarmReceived","appliance is null");
            return;
        }
        BLE ble = mDbHelper.getBleByPrimaryKey(appliance.getBleId());
        if (ble == null) {
            Log.e("AlarmReceived","ble is null");
            return;
        }

        // TODO connect ble

        if (startEnd == Alarm.START)
            Log.i("AlarmReceived","T:" + event.getTitle() + "(S)");
        else if (startEnd == Alarm.END)
            Log.i("AlarmReceived","T:" + event.getTitle() + "(E)");
        else
            Log.e("AlarmReceived","start/end not found" + String.valueOf(eventId));
    }
}
