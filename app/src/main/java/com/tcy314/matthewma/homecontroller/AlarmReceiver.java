package com.tcy314.matthewma.homecontroller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.tcy314.matthewma.homecontroller.DatabaseAndClass.Appliance;
import com.tcy314.matthewma.homecontroller.DatabaseAndClass.BLE;
import com.tcy314.matthewma.homecontroller.DatabaseAndClass.ControllerDbHelper;
import com.tcy314.matthewma.homecontroller.DatabaseAndClass.Event;

/**
 * Created by Matthew Ma on 24/9/2015.
 */
public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        ControllerDbHelper mDbHelper = new ControllerDbHelper(context);
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
