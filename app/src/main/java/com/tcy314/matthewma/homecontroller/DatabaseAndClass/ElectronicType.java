package com.tcy314.matthewma.homecontroller.DatabaseAndClass;

import android.database.Cursor;

/**
 * Created by Matthew Ma on 4/9/2015.
 */
public class ElectronicType {
    public final static String TOGGLE_BUTTON = "ToggleButton";
    public final static String SWITCH = "Switch";
    private int typeId;
    private String name;
    private String buttonType;
    private int numberOfColumn;

    public ElectronicType(Cursor cursor) {
        this.typeId = cursor.getInt(cursor.getColumnIndex(DbEntry.ElectronicType.COLUMN_TYPE_ID));
        this.name = cursor.getString(cursor.getColumnIndex(DbEntry.ElectronicType.COLUMN_NAME));
        this.buttonType = cursor.getString(cursor.getColumnIndex(DbEntry.ElectronicType.BUTTON_TYPE));
        this.numberOfColumn = cursor.getInt(cursor.getColumnIndex(DbEntry.ElectronicType.COLUMN_NUMBER_OF_COLUMN));
    }

    public int getTypeId() {
        return typeId;
    }

    public String getName() {
        return name;
    }

    public String getButtonType() {
        return buttonType;
    }

    public int getNumberOfColumn() {
        return numberOfColumn;
    }
}
