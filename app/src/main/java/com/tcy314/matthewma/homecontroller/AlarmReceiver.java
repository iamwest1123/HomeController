package com.tcy314.matthewma.homecontroller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.tcy314.matthewma.homecontroller.DatabaseAndClass.ControllerDbHelper;
import com.tcy314.matthewma.homecontroller.DatabaseAndClass.Event;

/**
 * Created by Matthew Ma on 24/9/2015.
 */
public class AlarmReceiver extends BroadcastReceiver {
    private static ControllerDbHelper mDbHelper;
    private Event event;

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO
        mDbHelper = new ControllerDbHelper(context);
        int eventId = intent.getIntExtra(Alarm.EVENT_ID, -1);
        int startEnd = intent.getIntExtra(Alarm.START_END, -1);
        event = mDbHelper.getEventByPrimaryKey(eventId);
        if (event != null)
            if (startEnd == Alarm.START)
                Log.i("AlarmReceived","T:" + event.getTitle() + "(S)");
            else if (startEnd == Alarm.END)
                Log.i("AlarmReceived","T:" + event.getTitle() + "(E)");
            else
                Log.e("AlarmReceived","start/end not found" + String.valueOf(eventId));
        else
            Log.e("AlarmReceived","ID not found: "+ String.valueOf(eventId));
//        Toast.makeText(context, "I'm running", Toast.LENGTH_SHORT).show();
    }
}
