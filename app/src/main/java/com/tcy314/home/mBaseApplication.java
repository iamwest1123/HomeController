package com.tcy314.home;

import android.app.Application;

import com.tcy314.home.DBnClass.ControllerDbHelper;
import com.tcy314.home.alarm.Alarm;

/**
 * Created by Matthew Ma on 27/9/2015.
 */
public class mBaseApplication extends Application {

    private ControllerDbHelper dbHelper;
    private Alarm alarm;

    @Override
    public void onCreate()
    {
        super.onCreate();
        dbHelper = new ControllerDbHelper(this);
        alarm = new Alarm(this);
    }

    public ControllerDbHelper getDbHelper() {
        return dbHelper;
    }

    public Alarm getAlarm() {
        return alarm;
    }
}
