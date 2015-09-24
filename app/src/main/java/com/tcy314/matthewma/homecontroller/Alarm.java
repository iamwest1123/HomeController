package com.tcy314.matthewma.homecontroller;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.tcy314.matthewma.homecontroller.DatabaseAndClass.DbEntry;
import com.tcy314.matthewma.homecontroller.DatabaseAndClass.Event;

import java.util.Calendar;

/**
 * Created by Matthew Ma on 24/9/2015.
 */
public class Alarm {
    public final static String EVENT_ID = "com.tcy314.alarm.eventid";
    public final static String START_END = "com.tcy314.alarm.start_end";
    public final static int START = 0;
    public final static int END = 1;
    private static Context context;
    private static AlarmManager alarmMgr;

    public Alarm(Context c) {
        if (context == null) {
            this.context = c;
            this.alarmMgr = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);
        }
    }

    public void set(Event event) {
        if (context != null) {
            Intent intent = new Intent(context, AlarmReceiver.class);
            intent.putExtra(EVENT_ID, event.getId());
            intent.putExtra(START_END, START);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, event.getId(), intent, 0);
            setAlarmManager(event, event.getStartCalendar().getTimeInMillis(), pendingIntent);
            if (event.isEndTimeSet()) {
                intent = new Intent(context, AlarmReceiver.class);
                intent.putExtra(EVENT_ID, event.getId());
                intent.putExtra(START_END, END);
                // have to get another request code, the easiest way is to use negative request code
                pendingIntent = PendingIntent.getBroadcast(context, 0 - event.getId(), intent, 0);
                setAlarmManager(event, event.getEndCalendar().getTimeInMillis(), pendingIntent);
            }
        }
    }

    public void delete(Event event) {
        if (context != null) {
            // delete start one
            Intent intent = new Intent(context, AlarmReceiver.class);
            intent.putExtra(EVENT_ID, event.getId());
            intent.putExtra(START_END, START);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, event.getId(), intent, 0);
            alarmMgr.cancel(pendingIntent);

            intent = new Intent(context, AlarmReceiver.class);
            intent.putExtra(EVENT_ID, event.getId());
            intent.putExtra(START_END, END);
            // have to get another request code, the easiest way is to use negative request code
            pendingIntent = PendingIntent.getBroadcast(context, 0 - event.getId(), intent, 0);
            alarmMgr.cancel(pendingIntent);
        }
    }

    public void update(Event event) {
        delete(event);
        set(event);
    }

    private void setAlarmManager(Event event, long triggerAtMillis, PendingIntent pi) {
        long nowInMillis = Calendar.getInstance().getTimeInMillis();
        // Check if trigger time is in the future
        if (nowInMillis < triggerAtMillis) {
            switch (event.getRepeatOption()) {
                case DbEntry.Event.REPEAT_NEVER:
                    alarmMgr.setExact(AlarmManager.RTC_WAKEUP, triggerAtMillis, pi);
                    break;
                case DbEntry.Event.REPEAT_EVERY_DAY:
                    alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, triggerAtMillis,
                            AlarmManager.INTERVAL_DAY, pi);
                    break;
                case DbEntry.Event.REPEAT_EVERY_WEEK:
                    alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, triggerAtMillis,
                            AlarmManager.INTERVAL_DAY * 7, pi);
                    break;
                case DbEntry.Event.REPEAT_EVERY_MONTH:
                    // set one time event only since the separation is too long anyway
                    alarmMgr.set(AlarmManager.RTC_WAKEUP, triggerAtMillis, pi);
                    break;
                case DbEntry.Event.REPEAT_EVERY_YEAR:
                    // set one time event only since the separation is too long anyway
                    alarmMgr.set(AlarmManager.RTC_WAKEUP, triggerAtMillis, pi);
                    break;
                default:
                    break;
            }
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(triggerAtMillis);
            Log.i("Alarm added", Event.TIME_FORMAT.format(c.getTime()));
        }
    }

}
