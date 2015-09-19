package com.tcy314.matthewma.homecontroller;

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
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "HomeControl.db";
    SQLiteDatabase db;
    private static SparseArray roomMap = new SparseArray();
    private static SparseArray electronicTypeMap = new SparseArray();
    private static HashMap<Appliance.PrimaryKey, Appliance> applianceMap = new HashMap<>();

    public ControllerDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DbEntry.Room.DROP_TABLE);
        db.execSQL(DbEntry.BLE.DROP_TABLE);
        db.execSQL(DbEntry.Appliance.DROP_TABLE);
        db.execSQL(DbEntry.ElectronicType.DROP_TABLE);
        db.execSQL(DbEntry.Room.CREATE_ENTRIES);
        db.execSQL(DbEntry.BLE.CREATE_ENTRIES);
        db.execSQL(DbEntry.Appliance.CREATE_ENTRIES);
        db.execSQL(DbEntry.ElectronicType.CREATE_ENTRIES);
        Log.i("ControllerDbHelper", "onCreate Completed");
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Empty
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Empty
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

    public boolean updateApplianceByPrimaryKey(int bleId, int portId, ContentValues cv) {
        db = this.getWritableDatabase();
        int result = db.update(DbEntry.Appliance.TABLE_NAME,
                cv,
                DbEntry.Appliance.COLUMN_BLE_ID + " =? and " + DbEntry.Appliance.COLUMN_PORT_ID + " =?",
                new String[]{String.valueOf(bleId), String.valueOf(portId)});
        return (result != 0);
    }

    public String getRoomNameByPrimaryKey(int roomId) {
        // check psudo cache memory
        if (roomMap.get(roomId) != null) {
            return (String)roomMap.get(roomId);
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

    public ElectronicType getElectronicTypeByPrimaryKey(int typeId) {
        if (electronicTypeMap.get(typeId) != null) {
            return (ElectronicType)electronicTypeMap.get(typeId);
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
}