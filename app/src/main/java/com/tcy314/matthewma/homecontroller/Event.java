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
    public final static SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("kk:mm", Locale.UK);
    private int id;
    private String title;
    private Appliance.PrimaryKey appk;
    private Calendar startTime;
    private Calendar endTime;
    private Calendar untilTime;
    private int startState;
    private int endState;
    private int repeatOption;

    public Event () {
        this.startTime = Calendar.getInstance();
        this.endTime = Calendar.getInstance();
        this.untilTime = Calendar.getInstance();
    }

    public Event (int id, String title, Appliance.PrimaryKey appk,
                  long startTime, long endTime, long untilTime,
                  int startState, int endState, int repeatOption) {
        this();
        this.id = id;
        this.title = title;
        this.appk = appk;
        this.startTime.setTimeInMillis(startTime);
        this.endTime.setTimeInMillis(endTime);
        this.untilTime.setTimeInMillis(untilTime);
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

    public int getId() {
        return id;
    }
    public String getTitle() {
        return title;
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

    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    public void setAppk(Appliance.PrimaryKey appk) {
        this.appk = appk;
    }
    public void setStartTime(Calendar startTime) {
        this.startTime = startTime;
    }
    public void setEndTime(Calendar endTime) {
        this.endTime = endTime;
    }
    public void setUntilTime(Calendar untilTime) {
        this.untilTime = untilTime;
    }
    public void setStartState(int startState) {
        this.startState = startState;
    }
    public void setEndState(int endState) {
        this.endState = endState;
    }
    public void setRepeatOption(int repeatOption) {
        this.repeatOption = repeatOption;
    }
}
