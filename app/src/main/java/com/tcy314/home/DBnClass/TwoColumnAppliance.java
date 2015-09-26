package com.tcy314.home.DBnClass;

import android.database.Cursor;

import com.tcy314.home.DBnClass.Appliance;

/**
 * Created by Matthew Ma on 30/8/2015.
 */
public class TwoColumnAppliance {
    private Appliance[] arrayList = new Appliance[2];
    private int length = 0;

    public TwoColumnAppliance(Cursor cursor, int count) {
        if (count >= 2) {
            length = 2;
            arrayList[0] = new Appliance(cursor);
            cursor.moveToNext();
            arrayList[1] = new Appliance(cursor);
        }
        else {
            length = 1;
            arrayList[0] = new Appliance(cursor);
            arrayList[1] = null;
        }
        cursor.moveToNext();
    }

    public TwoColumnAppliance(Appliance a) {
        length = 1;
        arrayList[0] = a;
        arrayList[1] = null;
    }

    public TwoColumnAppliance(Appliance a, Appliance b) {
        length = 2;
        arrayList[0] = a;
        arrayList[1] = b;
    }

    public Appliance getAppliance(int i) {
        if (i>=2)
            return null;
        return arrayList[i];
    }

    public int getType() {
        return arrayList[0].getTypeId();
    }

    public int getLength() {
        return length;
    }

    public void setColumnOne(Appliance a) {
        arrayList[0] = a;
    }

    public void setColumnTwo(Appliance b) {
        if ((b != null) && (length == 1)) {
            length = 2;
        }
        if (b == null)
            length = 1;
        arrayList[1] = b;
    }
}
