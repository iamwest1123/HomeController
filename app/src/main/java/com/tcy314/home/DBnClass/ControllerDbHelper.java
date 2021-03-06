package com.tcy314.home.DBnClass;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.util.SparseArray;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Matthew Ma on 27/8/2015.
 */
public class ControllerDbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 10;
    public static final String DATABASE_NAME = "HomeControl.db";
    private static SQLiteDatabase db;
    private static SparseArray<String> roomMap = new SparseArray<>();
    private static SparseArray<ElectronicType> electronicTypeMap = new SparseArray<>();
    private static SparseArray<BLE> bleMap = new SparseArray<>();
    private static SparseArray<Event> eventMap = new SparseArray<>();
    private static HashMap<Appliance.PrimaryKey, Appliance> applianceMap = new HashMap<>();
    private int hit;
    private int miss;

    public ControllerDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        hit = 0;
        miss = 0;
    }

    public void onCreate(SQLiteDatabase db) {
        DropAddTable(db);
        Log.i("ControllerDbHelper", "onCreate Completed");
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        DropAddTable(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        DropAddTable(db);
    }

    private void DropAddTable(SQLiteDatabase db) {
        db.execSQL(DbEntry.Room.DROP_TABLE);
        db.execSQL(DbEntry.BLE.DROP_TABLE);
        db.execSQL(DbEntry.Appliance.DROP_TABLE);
        db.execSQL(DbEntry.ElectronicType.DROP_TABLE);
        db.execSQL(DbEntry.Event.DROP_TABLE);
        db.execSQL(DbEntry.Room.CREATE_ENTRIES);
        db.execSQL(DbEntry.BLE.CREATE_ENTRIES);
        db.execSQL(DbEntry.Appliance.CREATE_ENTRIES);
        db.execSQL(DbEntry.ElectronicType.CREATE_ENTRIES);
        db.execSQL(DbEntry.Event.CREATE_ENTRIES);
    }

    public Appliance getApplianceByPrimaryKey(Appliance.PrimaryKey appk) {
        // check psudo cache memory
        if (applianceMap.get(appk) != null){
            hit++;
            return applianceMap.get(appk);
        }
        miss++;
        db = this.getWritableDatabase();
        Cursor cursor = db.query(DbEntry.Appliance.TABLE_NAME,
                DbEntry.Appliance.SELECT_ALL,
                DbEntry.Appliance.COLUMN_BLE_ID + " =? and " + DbEntry.Appliance.COLUMN_PORT_ID + " =?",
                new String[]{String.valueOf(appk.bleId), String.valueOf(appk.portId)},
                null, null, null);
        if (cursor.moveToFirst()) {
            // write back to map
            Appliance result = new Appliance(cursor);
            applianceMap.put(appk, result);
            cursor.close();
            return result;
        }
        cursor.close();
        return null;
    }
    public Appliance getApplianceByPrimaryKey(int bleId, int portId) {
        return getApplianceByPrimaryKey(new Appliance.PrimaryKey(bleId, portId));
    }
    // TODO Untested
    public BLE getBleByPrimaryKey(int id) {
        if (bleMap.get(id) != null) {
            hit++;
            return bleMap.get(id);
        }
        miss++;
        db = this.getWritableDatabase();
        Cursor cursor = db.query(DbEntry.BLE.TABLE_NAME,
                DbEntry.BLE.SELECT_ALL,
                DbEntry.BLE.COLUMN_BLE_ID + " =?",
                new String[]{String.valueOf(id)},
                null, null, null);
        if (cursor.moveToFirst()) {
            // write back to map
            BLE result = new BLE(cursor);
            bleMap.append(id, result);
            cursor.close();
            return result;
        }
        cursor.close();
        return null;
    }
    public ElectronicType getElectronicTypeByPrimaryKey(int id) {
        if (electronicTypeMap.get(id) != null) {
            hit++;
            return electronicTypeMap.get(id);
        }
        miss++;
        db = this.getWritableDatabase();
        Cursor cursor = db.query(DbEntry.ElectronicType.TABLE_NAME,
                DbEntry.ElectronicType.SELECT_ALL,
                DbEntry.ElectronicType.COLUMN_TYPE_ID + " =?",
                new String[]{String.valueOf(id)},
                null, null, null);
        if (cursor.moveToFirst()) {
            // write back to map
            ElectronicType result = new ElectronicType(cursor);
            electronicTypeMap.append(id, result);
            cursor.close();
            return result;
        }
        cursor.close();
        return null;
    }
    public Event getEventByPrimaryKey(int id) {
        // check cache memory
        if (eventMap.get(id) != null) {
            hit++;
            return eventMap.get(id);
        }
        miss++;
        db = this.getWritableDatabase();
        Cursor cursor = db.query(DbEntry.Event.TABLE_NAME,
                DbEntry.Event.SELECT_ALL,
                DbEntry.Event.COLUMN_ID + " =?",
                new String[]{String.valueOf(id)},
                null, null, null);
        if (cursor.moveToFirst()) {
            // write back to map
            Event result = new Event(cursor);
            eventMap.append(id, result);
            cursor.close();
            return result;
        }
        return null;
    }
    public String getRoomByPrimaryKey(int roomId) {
        // check cache memory
        if (roomMap.get(roomId) != null) {
            hit++;
            return roomMap.get(roomId);
        }
        miss++;
        db = this.getWritableDatabase();
        Cursor cursor = db.query(DbEntry.Room.TABLE_NAME,
                DbEntry.Room.SELECT_ALL,
                DbEntry.Room.COLUMN_ROOM_ID + " =?",
                new String[]{String.valueOf(roomId)},
                null, null, null);
        if (cursor.moveToFirst()) {
            // write back to map
            String result = cursor.getString(cursor.getColumnIndex(DbEntry.Room.COLUMN_NAME));
            roomMap.append(roomId, result);
            cursor.close();
            return result;
        }
        cursor.close();
        return null;
    }

    // TODO Untested
    public boolean updateApplianceByPrimaryKey(Appliance.PrimaryKey appk, ContentValues cv) {
        return updateApplianceByPrimaryKey(appk.bleId, appk.portId, cv);
    }
    public boolean updateApplianceByPrimaryKey(int bleId, int portId, ContentValues cv) {
        db = this.getWritableDatabase();
        int result = db.update(DbEntry.Appliance.TABLE_NAME,
                cv,
                DbEntry.Appliance.COLUMN_BLE_ID + " =? and " + DbEntry.Appliance.COLUMN_PORT_ID + " =?",
                new String[]{String.valueOf(bleId), String.valueOf(portId)});
        // re-populate map
        Appliance.PrimaryKey appk = new Appliance.PrimaryKey(bleId, portId);
        applianceMap.remove(appk);
        this.getApplianceByPrimaryKey(appk);
        return (result != 0);
    }
    public boolean updateBleByPrimaryKey(int id, ContentValues cv) {
        db = this.getWritableDatabase();
        int result = db.update(DbEntry.BLE.TABLE_NAME,
                cv,
                DbEntry.BLE.COLUMN_BLE_ID + " =?",
                new String[]{String.valueOf(id)});
        // re-populate map
        bleMap.remove(id);
        this.getBleByPrimaryKey(id);
        return (result != 0);
    }
    public boolean updateElectronicTypeByPrimaryKey(int id, ContentValues cv) {
        db = this.getWritableDatabase();
        int result = db.update(DbEntry.ElectronicType.TABLE_NAME,
                cv,
                DbEntry.ElectronicType.COLUMN_TYPE_ID + " =?",
                new String[]{String.valueOf(id)});
        // re-populate map
        electronicTypeMap.remove(id);
        this.getElectronicTypeByPrimaryKey(id);
        return (result != 0);
    }
    public boolean updateEventByPrimaryKey(int id, ContentValues cv) {
        db = this.getWritableDatabase();
        int result = db.update(DbEntry.Event.TABLE_NAME,
                cv,
                DbEntry.Event.COLUMN_ID + " =?",
                new String[]{String.valueOf(id)});
        // re-populate map
        eventMap.remove(id);
        this.getEventByPrimaryKey(id);
        return (result != 0);
    }
    public boolean updateRoomByPrimaryKey(int id, ContentValues cv) {
        db = this.getWritableDatabase();
        int result = db.update(DbEntry.Room.TABLE_NAME,
                cv,
                DbEntry.Room.COLUMN_ROOM_ID + " =?",
                new String[]{String.valueOf(id)});
        // re-populate map
        roomMap.remove(id);
        this.getRoomByPrimaryKey(id);
        return (result != 0);
    }

    //TODO Untested
    public boolean deleteApplianceByPrimaryKey(Appliance.PrimaryKey appk) {
        return deleteApplianceByPrimaryKey(appk.bleId,appk.portId);
    }
    public boolean deleteApplianceByPrimaryKey(int bleId, int portId) {
        db = this.getWritableDatabase();
        int result = db.delete(DbEntry.Appliance.TABLE_NAME,
                DbEntry.Appliance.COLUMN_BLE_ID + " =? and " + DbEntry.Appliance.COLUMN_PORT_ID + " =?",
                new String[]{String.valueOf(bleId), String.valueOf(portId)});
        Appliance.PrimaryKey appk = new Appliance.PrimaryKey(bleId, portId);
        applianceMap.remove(appk);
        return (result != 0);
    }
    public boolean deleteBleByPrimaryKey(int id) {
        db = this.getWritableDatabase();
        int result = db.delete(DbEntry.BLE.TABLE_NAME,
                DbEntry.BLE.COLUMN_BLE_ID + " =?",
                new String[]{String.valueOf(id)});
        bleMap.remove(id);
        return (result != 0);
    }
    public boolean deleteElectronicTypeByPrimaryKey(int id) {
        db = this.getWritableDatabase();
        int result = db.delete(DbEntry.ElectronicType.TABLE_NAME,
                DbEntry.ElectronicType.COLUMN_TYPE_ID + " =?",
                new String[]{String.valueOf(id)});
        electronicTypeMap.remove(id);
        return (result != 0);
    }
    public boolean deleteEventByPrimaryKey(int id) {
        db = this.getWritableDatabase();
        int result = db.delete(DbEntry.Event.TABLE_NAME,
                DbEntry.Event.COLUMN_ID + " =?",
                new String[]{String.valueOf(id)});
        eventMap.remove(id);
        return (result != 0);
    }
    public boolean deleteRoomByPrimaryKey(int id) {
        db = this.getWritableDatabase();
        int result = db.delete(DbEntry.Room.TABLE_NAME,
                DbEntry.Room.COLUMN_ROOM_ID + " =?",
                new String[]{String.valueOf(id)});
        roomMap.remove(id);
        return (result != 0);
    }


    public void addEventToMap(Event e) {
        if (e != null)
            eventMap.put(e.getId(), e);
    }
    public void addEventToMap(ArrayList<Event> aList) {
        for (Event e : aList) {
            addEventToMap(e);
        }
    }
}
