package com.tcy314.home.DBnClass;

import android.database.Cursor;

/**
 * Created by Matthew Ma on 24/9/2015.
 */
public class BLE {
    private int id;
    private String address;
    private int roomId;

    public BLE(int id, String address, int roomId) {
        this.id = id;
        this.address = address;
        this.roomId = roomId;
    }
    public BLE(Cursor c) {
        this(c.getInt(c.getColumnIndex(DbEntry.BLE.COLUMN_BLE_ID)),
                c.getString(c.getColumnIndex(DbEntry.BLE.COLUMN_ADDRESS)),
                c.getInt(c.getColumnIndex(DbEntry.BLE.COLUMN_ROOM_ID))
        );
    }

    public int getId() {
        return id;
    }
    public String getAddress() {
        return address;
    }
    public int getRoomId() {
        return roomId;
    }

    public void setId(int id) {
        this.id = id;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }
}
