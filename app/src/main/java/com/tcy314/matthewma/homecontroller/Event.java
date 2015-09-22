package com.tcy314.matthewma.homecontroller;

import android.database.Cursor;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Matthew Ma on 22/9/2015.
 */
public class Event {
    public final static SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("E, d MMM yyyy kk:mm", Locale.UK);
    public final static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("E, d MMM yyyy", Locale.UK);
    private int id;
    private String name;
    private Appliance.PrimaryKey appk;
    private Calendar startTime;
    private Calendar endTime;
    private Calendar untilTime;
    private int startState;
    private int endState;
    private int repeatOption;

    public Event (Cursor c) {
        this.id = c.getInt(c.getColumnIndex(DbEntry.Event.COLUMN_ID));
        this.name = c.getString(c.getColumnIndex(DbEntry.Event.COLUMN_TITLE));
        this.appk = new Appliance.PrimaryKey(c.getInt(c.getColumnIndex(DbEntry.Event.COLUMN_BLE_ID)),
                c.getInt(c.getColumnIndex(DbEntry.Event.COLUMN_PORT_ID)));
        startTime = Calendar.getInstance();
        endTime = Calendar.getInstance();
        untilTime = Calendar.getInstance();
        this.startTime.setTimeInMillis(c.getLong(c.getColumnIndex(DbEntry.Event.COLUMN_START)));
        this.endTime.setTimeInMillis(c.getLong(c.getColumnIndex(DbEntry.Event.COLUMN_END)));
        this.untilTime.setTimeInMillis(c.getLong(c.getColumnIndex(DbEntry.Event.COLUMN_UNTIL)));
        this.startState = c.getInt(c.getColumnIndex(DbEntry.Event.COLUMN_START_STATE));
        this.endState = c.getInt(c.getColumnIndex(DbEntry.Event.COLUMN_END_STATE));
        this.repeatOption = c.getInt(c.getColumnIndex(DbEntry.Event.COLUMN_REPEAT_OPTION));
    }

    public boolean isEndTimeSet() {
        return (endState != DbEntry.Appliance.STATE_NOT_SET);
    }

    public int getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public Appliance.PrimaryKey getAppk() {
        return appk;
    }
    public Calendar getStartTime() {
        return startTime;
    }
    public Calendar getEndTime() {
        return endTime;
    }
    public Calendar getUntilTime() {
        return untilTime;
    }
    public String getStartTimeInString() {
        return DATE_TIME_FORMAT.format(startTime.getTime());
    }
    public String getEndTimeInString() {
        return DATE_TIME_FORMAT.format(endTime.getTime());
    }
    public String getUntilTimeInString() {
        return DATE_FORMAT.format(untilTime.getTime());
    }
    public int getStartState() {
        return startState;
    }
    public int getEndState() {
        return endState;
    }
    public int getRepeatOption() {
        return repeatOption;
    }
}
