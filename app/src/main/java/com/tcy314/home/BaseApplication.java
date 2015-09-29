package com.tcy314.home;

import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import com.tcy314.home.DBnClass.ControllerDbHelper;
import com.tcy314.home.DBnClass.DbEntry;
import com.tcy314.home.DBnClass.ElectronicType;
import com.tcy314.home.alarm.Alarm;
import com.tcy314.home.service.BluetoothLeService;
import com.tcy314.home.service.ServiceManager;

import java.util.ArrayList;

/**
 * Created by Matthew Ma on 27/9/2015.
 */
public class BaseApplication extends Application {
    private final static String TAG = BaseApplication.class.getSimpleName();

    private ControllerDbHelper dbHelper;
    private Alarm alarm;
    private ServiceManager serviceManager;

    @Override
    public void onCreate()
    {
        super.onCreate();
        // check if BLE is available
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
        }

        dbHelper = new ControllerDbHelper(this);
        alarm = new Alarm(this);
        serviceManager = new ServiceManager(this);
        insertTestEntry();
    }

    private void insertTestEntry() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(DbEntry.Appliance.TABLE_NAME, null, null);
        db.delete(DbEntry.BLE.TABLE_NAME, null, null);
        db.delete(DbEntry.Room.TABLE_NAME, null, null);
        db.delete(DbEntry.ElectronicType.TABLE_NAME, null, null);

        ContentValues values;
        values = DbEntry.ElectronicType.put(1, "Light", ElectronicType.TOGGLE_BUTTON, 2);
        db.insert(DbEntry.ElectronicType.TABLE_NAME, null, values);
        values.clear();
        values = DbEntry.ElectronicType.put(2, "Fan", ElectronicType.SWITCH, 1);
        db.insert(DbEntry.ElectronicType.TABLE_NAME, null, values);
        values.clear();

        values = DbEntry.Appliance.put(1, 1, "TestSwitch 01", 1, false, 0, 2, 0);
        db.insert(DbEntry.Appliance.TABLE_NAME, null, values);
        values.clear();
        values = DbEntry.Appliance.put(1, 2, "TestSwitch 02", 1, false, 0, 2, 300);
        db.insert(DbEntry.Appliance.TABLE_NAME, null, values);
        values.clear();
        values = DbEntry.Appliance.put(1, 3, "TestSwitch 03", 1, false, -1, 2, 200);
        db.insert(DbEntry.Appliance.TABLE_NAME, null, values);
        values.clear();
        values = DbEntry.Appliance.put(2, 1, "TestSwitch 04", 1, false, 0, 3, 100);
        db.insert(DbEntry.Appliance.TABLE_NAME, null, values);
        values.clear();
        values = DbEntry.Appliance.put(2, 2, "TestFan 01", 2, false, 0, 3, 201);
        db.insert(DbEntry.Appliance.TABLE_NAME, null, values);
        values.clear();

        values = DbEntry.Room.put(1, "Frequently used");
        db.insert(DbEntry.Room.TABLE_NAME, null, values);
        values.clear();
        values = DbEntry.Room.put(2, "TestRoom 1");
        db.insert(DbEntry.Room.TABLE_NAME, null, values);
        values.clear();
        values = DbEntry.Room.put(3, "TestRoom 2");
        db.insert(DbEntry.Room.TABLE_NAME, null, values);
        values.clear();

        values = DbEntry.BLE.put(1, "DUMMY", 1);
        db.insert(DbEntry.BLE.TABLE_NAME, null, values);
        values.clear();

        Log.i("insertTestEntry", "Complete");

        db.close();
    }

    public ControllerDbHelper getDbHelper() {
        return dbHelper;
    }

    public Alarm getAlarm() {
        return alarm;
    }

    public ServiceManager getServiceManager() {
        return serviceManager;
    }
}
