package com.tcy314.matthewma.homecontroller.DatabaseAndClass;

import android.database.Cursor;

/**
 * Created by Matthew Ma on 29/8/2015.
 */
public class Appliance {
    private int bleId;
    private int portId;
    private String name;
    private int typeId;
    private boolean onWhenVacant;
    private int state;
    private int roomId;
    private int count;

    public Appliance(Cursor c) {
        if (c == null)
            return;
        this.bleId = c.getInt(0);
        this.portId = c.getInt(1);
        this.name = c.getString(2);
        this.typeId = c.getInt(3);
        this.onWhenVacant = c.getInt(4) > 0;
        this.state = c.getInt(5);
        this.roomId = c.getInt(6);
        this.count = c.getInt(7);
    }

    public int getBleId() {
        return bleId;
    }
    public int getPortId() {
        return portId;
    }
    public String getName() {
        return name;
    }
    public int getTypeId() {
        return typeId;
    }
    public boolean isOnWhenVacant() {
        return onWhenVacant;
    }
    public int getState() {
        return state;
    }
    public int getRoomId() {
        return roomId;
    }
    public int getCount() {
        return count;
    }
    public PrimaryKey getPrimaryKey() {
        return new PrimaryKey(bleId,portId);
    }

    public void setBleId(int bleId) {
        this.bleId = bleId;
    }
    public void setPortId(int portId) {
        this.portId = portId;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }
    public void setOnWhenVacant(boolean onWhenVacant) {
        this.onWhenVacant = onWhenVacant;
    }
    public void setState(int state) {
        this.state = state;
    }
    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }
    public void setCount(int count) {
        this.count = count;
    }

    public static class PrimaryKey {
        public int bleId;
        public int portId;

        public PrimaryKey(int bleId, int portId) {
            this.bleId = bleId;
            this.portId = portId;
        }

        public int[] toArray() {
            return new int[] {bleId,portId};
        }
    }
}
