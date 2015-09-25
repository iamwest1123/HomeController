package com.tcy314.home.DBnClass;

import android.database.Cursor;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Matthew Ma on 22/9/2015.
 */
public class Event {
    public final static SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("E, d MMM yyyy HH:mm", Locale.UK);
    public final static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("E, d MMM yyyy", Locale.UK);
    public final static SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm", Locale.UK);
    private int id;
    private String title;
    private Appliance.PrimaryKey appk;
    private Calendar startCalendar;
    private Calendar endCalendar;
    private Calendar untilCalendar;
    private int startState;
    private int endState;
    private long repeatOption;

    public Event () {
        this.startCalendar = Calendar.getInstance();
        this.endCalendar = Calendar.getInstance();
        this.untilCalendar = Calendar.getInstance();
        this.startCalendar.set(Calendar.SECOND, 0);
        this.endCalendar.set(Calendar.SECOND, 0);
        this.untilCalendar.set(Calendar.SECOND, 0);
        this.startState = DbEntry.Appliance.STATE_NOT_SET;
        this.endState = DbEntry.Appliance.STATE_NOT_SET;
        this.repeatOption = DbEntry.Event.INTERVAL_NEVER;
    }

    public Event (int id, String title, Appliance.PrimaryKey appk,
                  long startCalendar, long endCalendar, long untilCalendar,
                  int startState, int endState, long repeatOption) {
        this();
        this.id = id;
        this.title = title;
        this.appk = appk;
        this.startCalendar.setTimeInMillis(startCalendar);
        this.endCalendar.setTimeInMillis(endCalendar);
        this.untilCalendar.setTimeInMillis(untilCalendar);
        this.startState = startState;
        this.endState = endState;
        this.repeatOption = repeatOption;
    }

    public Event (Cursor c) {
        this(
                c.getInt(c.getColumnIndex(DbEntry.Event.COLUMN_ID)),
                c.getString(c.getColumnIndex(DbEntry.Event.COLUMN_TITLE)),
                new Appliance.PrimaryKey(c.getInt(c.getColumnIndex(DbEntry.Event.COLUMN_BLE_ID)),
                        c.getInt(c.getColumnIndex(DbEntry.Event.COLUMN_PORT_ID))),
                c.getLong(c.getColumnIndex(DbEntry.Event.COLUMN_START)),
                c.getLong(c.getColumnIndex(DbEntry.Event.COLUMN_END)),
                c.getLong(c.getColumnIndex(DbEntry.Event.COLUMN_UNTIL)),
                c.getInt(c.getColumnIndex(DbEntry.Event.COLUMN_START_STATE)),
                c.getInt(c.getColumnIndex(DbEntry.Event.COLUMN_END_STATE)),
                c.getInt(c.getColumnIndex(DbEntry.Event.COLUMN_REPEAT_OPTION))
                );
    }

    public boolean isEndTimeSet() {
        return (endState != DbEntry.Appliance.STATE_NOT_SET);
    }
    public boolean isHappenAfterTime(long timeInMillis) {
        long startInMillis = startCalendar.getTimeInMillis();
        long endInMillis = endCalendar.getTimeInMillis();
        long untilInMillis = untilCalendar.getTimeInMillis();
        if (this.repeatOption == DbEntry.Event.INTERVAL_NEVER) {
            if (startInMillis > timeInMillis)
                return true;
            else if (isEndTimeSet() && (endInMillis > timeInMillis))
                return true;
            return false;
        }
        else {
            if ((startInMillis > timeInMillis) && (untilInMillis > startInMillis))
                return true;
            else if (isEndTimeSet() && (endInMillis > timeInMillis) && (untilInMillis > endInMillis))
                return true;
            return false;
        }
    }

    public int getId() {
        return id;
    }
    public String getTitle() {
        return title;
    }
    public Appliance.PrimaryKey getAppk() {
        return appk;
    }
    public Calendar getStartCalendar() {
        return startCalendar;
    }
    public Calendar getEndCalendar() {
        return endCalendar;
    }
    public Calendar getUntilCalendar() {
        return untilCalendar;
    }
    public String getStartCalendarInString(SimpleDateFormat sdf) {
        return sdf.format(startCalendar.getTime());
    }
    public String getEndCalendarInString(SimpleDateFormat sdf) {
        return sdf.format(endCalendar.getTime());
    }
    public String getUntilCalendarInString(SimpleDateFormat sdf) {
        return sdf.format(untilCalendar.getTime());
    }
    public int getStartState() {
        return startState;
    }
    public int getEndState() {
        return endState;
    }
    public long getRepeatOption() {
        return repeatOption;
    }

    public void setId(int id) {
        this.id = id;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public void setAppk(Appliance.PrimaryKey appk) {
        this.appk = appk;
    }
    public void setStartCalendar(Calendar startCalendar) {
        this.startCalendar = startCalendar;
    }
    public void setEndCalendar(Calendar endCalendar) {
        this.endCalendar = endCalendar;
    }
    public void setUntilCalendar(Calendar untilCalendar) {
        this.untilCalendar = untilCalendar;
    }
    public void setStartState(int startState) {
        this.startState = startState;
    }
    public void setEndState(int endState) {
        this.endState = endState;
    }
    public void setRepeatOption(long repeatOption) {
        this.repeatOption = repeatOption;
    }

    public void setNow(long now) {
        if (repeatOption != DbEntry.Event.INTERVAL_NEVER) {
            long until = untilCalendar.getTimeInMillis();
            if (until > now) {
                long start = startCalendar.getTimeInMillis();
                while ((start < now) && (start < until)) {
                    start += repeatOption;
                }
                startCalendar.setTimeInMillis(start);
                long end = endCalendar.getTimeInMillis();
                while ((end < now) && (end < until)) {
                    end += repeatOption;
                }
            }
        }
    }
    public static int getRepeatEnum(long interval) {
        if (interval == DbEntry.Event.INTERVAL_NEVER)
            return 0;
        else if (interval == DbEntry.Event.INTERVAL_MINUTE)
            return 1;
        else if (interval == DbEntry.Event.INTERVAL_HOUR)
            return 2;
        else if (interval == DbEntry.Event.INTERVAL_DAY)
            return 3;
        else if (interval == DbEntry.Event.INTERVAL_WEEK)
            return 4;
        return -1;
    }
}
