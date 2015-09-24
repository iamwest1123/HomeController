package com.tcy314.matthewma.homecontroller.DatabaseAndClass;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.util.SparseArray;

import java.util.HashMap;

/**
 * Created by Matthew Ma on 27/8/2015.
 */
public class ControllerDbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 9;
    public static final String DATABASE_NAME = "HomeControl.db";
    private static SQLiteDatabase db;
    private static SparseArray<String> roomMap = new SparseArray<>();
    private static SparseArray<ElectronicType> electronicTypeMap = new SparseArray<>();
    private static SparseArray<BLE> BleMap = new SparseArray<>();
    private static HashMap<Appliance.PrimaryKey, Appliance> applianceMap = new HashMap<>();

    public ControllerDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
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
            return applianceMap.get(appk);
        }
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
    public BLE getBleByPrimaryKey(int BleId) {
        if (BleMap.get(BleId) != null) {
            return BleMap.get(BleId);
        }
        db = this.getWritableDatabase();
        Cursor cursor = db.query(DbEntry.BLE.TABLE_NAME,
                DbEntry.BLE.SELECT_ALL,
                DbEntry.BLE.COLUMN_BLE_ID + " =?",
                new String[]{String.valueOf(BleId)},
                null, null, null);
        if (cursor.moveToFirst()) {
            // write back to map
            BLE result = new BLE(cursor);
            BleMap.put(BleId, result);
            cursor.close();
            return result;
        }
        cursor.close();
        return null;
    }
    public ElectronicType getElectronicTypeByPrimaryKey(int typeId) {
        if (electronicTypeMap.get(typeId) != null) {
            return electronicTypeMap.get(typeId);
        }
        db = this.getWritableDatabase();
        Cursor cursor = db.query(DbEntry.ElectronicType.TABLE_NAME,
                DbEntry.ElectronicType.SELECT_ALL,
                DbEntry.ElectronicType.COLUMN_TYPE_ID + " =?",
                new String[]{String.valueOf(typeId)},
                null, null, null);
        if (cursor.moveToFirst()) {
            // write back to map
            ElectronicType result = new ElectronicType(cursor);
            electronicTypeMap.put(typeId, result);
            cursor.close();
            return result;
        }
        cursor.close();
        return null;
    }
    public Event getEventByPrimaryKey(int id) {
        db = this.getWritableDatabase();
        Cursor cursor = db.query(DbEntry.Event.TABLE_NAME,
                DbEntry.Event.SELECT_ALL,
                DbEntry.Event.COLUMN_ID + " =?",
                new String[]{String.valueOf(id)},
                null, null, null);
        if (cursor.moveToFirst()) {
            // write back to map
            Event result = new Event(cursor);
            cursor.close();
            return result;
        }
        return null;
    }
    public String getRoomByPrimaryKey(int roomId) {
        // check cache memory
        if (roomMap.get(roomId) != null) {
            return roomMap.get(roomId);
        }
        db = this.getWritableDatabase();
        Cursor cursor = db.query(DbEntry.Room.TABLE_NAME,
                DbEntry.Room.SELECT_ALL,
                DbEntry.Room.COLUMN_ROOM_ID + " =?",
                new String[]{String.valueOf(roomId)},
                null, null, null);
        if (cursor.moveToFirst()) {
            // write back to map
            String result = cursor.getString(cursor.getColumnIndex(DbEntry.Room.COLUMN_NAME));
            roomMap.put(roomId, result);
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
        return (result != 0);
    }
    public boolean updateBleByPrimaryKey(int bleId, ContentValues cv) {
        db = this.getWritableDatabase();
        int result = db.update(DbEntry.BLE.TABLE_NAME,
                cv,
                DbEntry.BLE.COLUMN_BLE_ID + " =?",
                new String[]{String.valueOf(bleId)});
        return (result != 0);
    }
    public boolean updateElectronicTypeByPrimaryKey(int typeId, ContentValues cv) {
        db = this.getWritableDatabase();
        int result = db.update(DbEntry.ElectronicType.TABLE_NAME,
                cv,
                DbEntry.ElectronicType.COLUMN_TYPE_ID + " =?",
                new String[]{String.valueOf(typeId)});
        return (result != 0);
    }
    public boolean updateEventByPrimaryKey(int eventId, ContentValues cv) {
        db = this.getWritableDatabase();
        int result = db.update(DbEntry.Event.TABLE_NAME,
                cv,
                DbEntry.Event.COLUMN_ID + " =?",
                new String[]{String.valueOf(eventId)});
        return (result != 0);
    }
    public boolean updateRoomByPrimaryKey(int roomId, ContentValues cv) {
        db = this.getWritableDatabase();
        int result = db.update(DbEntry.Room.TABLE_NAME,
                cv,
                DbEntry.Room.COLUMN_ROOM_ID + " =?",
                new String[]{String.valueOf(roomId)});
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
        return (result != 0);
    }
    public boolean deleteBleByPrimaryKey(int bleId) {
        db = this.getWritableDatabase();
        int result = db.delete(DbEntry.BLE.TABLE_NAME,
                DbEntry.BLE.COLUMN_BLE_ID + " =?",
                new String[]{String.valueOf(bleId)});
        return (result != 0);
    }
    public boolean deleteElectronicTypeByPrimaryKey(int typeId) {
        db = this.getWritableDatabase();
        int result = db.delete(DbEntry.ElectronicType.TABLE_NAME,
                DbEntry.ElectronicType.COLUMN_TYPE_ID + " =?",
                new String[]{String.valueOf(typeId)});
        return (result != 0);
    }
    public boolean deleteEventByPrimaryKey(int eventId) {
        db = this.getWritableDatabase();
        int result = db.delete(DbEntry.Event.TABLE_NAME,
                DbEntry.Event.COLUMN_ID + " =?",
                new String[]{String.valueOf(eventId)});
        return (result != 0);
    }
    public boolean deleteRoomByPrimaryKey(int roomId) {
        db = this.getWritableDatabase();
        int result = db.delete(DbEntry.Room.TABLE_NAME,
                DbEntry.Room.COLUMN_ROOM_ID + " =?",
                new String[]{String.valueOf(roomId)});
        return (result != 0);
    }

}
