package com.tcy314.home.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.tcy314.home.DBnClass.DbEntry;
import com.tcy314.home.DBnClass.Event;

import java.util.Calendar;

/**
 * Created by Matthew Ma on 24/9/2015.
 */
public class Alarm {
    public final static String EVENT_ID = "com.tcy314.alarm.eventid";
    public final static String START_END = "com.tcy314.alarm.start_end";
    public final static int START = 0;
    public final static int END = 1;
    public final static long INTERVAL_SECOND = 1000;
    public final static long INTERVAL_MINUTE = 60000;
    public final static long INTERVAL_HOUR = 1800000;
    public final static long INTERVAL_DAY = 86400000;
    public final static long INTERVAL_WEEK = 604800000;

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
            // TODO
            if (event.getRepeatOption() == DbEntry.Event.INTERVAL_NEVER)
                alarmMgr.setExact(AlarmManager.RTC_WAKEUP, triggerAtMillis, pi);
            else if (event.getRepeatOption() == DbEntry.Event.INTERVAL_MINUTE)
                alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, triggerAtMillis,
                        Alarm.INTERVAL_MINUTE, pi);
            else if (event.getRepeatOption() == DbEntry.Event.INTERVAL_HOUR)
                alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, triggerAtMillis,
                        Alarm.INTERVAL_HOUR, pi);
            else if (event.getRepeatOption() == DbEntry.Event.INTERVAL_DAY)
                alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, triggerAtMillis,
                        Alarm.INTERVAL_DAY, pi);
            else if (event.getRepeatOption() == DbEntry.Event.INTERVAL_WEEK)
                alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, triggerAtMillis,
                        Alarm.INTERVAL_WEEK, pi);

            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(triggerAtMillis);
            Log.i("Alarm added", Event.TIME_FORMAT.format(c.getTime()));
        }
    }

}
