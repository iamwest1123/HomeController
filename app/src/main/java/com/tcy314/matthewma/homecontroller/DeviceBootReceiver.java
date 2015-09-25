package com.tcy314.matthewma.homecontroller;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import com.tcy314.matthewma.homecontroller.DatabaseAndClass.ControllerDbHelper;
import com.tcy314.matthewma.homecontroller.DatabaseAndClass.DbEntry;
import com.tcy314.matthewma.homecontroller.DatabaseAndClass.Event;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Matthew Ma on 24/9/2015.
 */
public class DeviceBootReceiver extends BroadcastReceiver {
    private static ControllerDbHelper mDbHelper;

    @Override
    public void onReceive(Context context, Intent intent) {
        mDbHelper = new ControllerDbHelper(context);
        ArrayList<Event> eventArrayList = new ArrayList<>();
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            /* Setting the alarm here */
            // get all possible future events
            SQLiteDatabase db = mDbHelper.getWritableDatabase();
            long nowInMillis = Calendar.getInstance().getTimeInMillis();
            Cursor cursor = db.query(DbEntry.Event.TABLE_NAME,
                    DbEntry.Event.SELECT_ALL,
                    DbEntry.Event.COLUMN_START + " >? or " + DbEntry.Event.COLUMN_END + " >? or " +
                    DbEntry.Event.COLUMN_UNTIL + " >?",
                    new String[]{String.valueOf(nowInMillis),String.valueOf(nowInMillis),
                            String.valueOf(nowInMillis)},
                    null, null, null);
            if (cursor.moveToFirst()) {
                do {
                    Event tempEvent = new Event(cursor);
                    // modify time for event (when device is off, some repeating event start time should be change)
                    tempEvent.setNow(nowInMillis);
                    mDbHelper.addEventToMap(tempEvent);
                    if (tempEvent.isHappenAfterTime(nowInMillis))
                        eventArrayList.add(tempEvent);
                } while (cursor.moveToNext());
            }
            cursor.close();
            // set alarms
            for (Event e : eventArrayList) {
                MainActivity.alarm.set(e);
            }
        }
    }
}
