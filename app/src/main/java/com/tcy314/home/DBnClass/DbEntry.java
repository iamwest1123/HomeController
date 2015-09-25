package com.tcy314.home.DBnClass;

import android.content.ContentValues;

/**
 * Created by Matthew Ma on 27/8/2015.
 */
public class DbEntry {
    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    public DbEntry() {}

    /* Inner class that defines the table contents */
    public static abstract class Appliance {
        public static final int STATE_NOT_SET = -2;
        public static final int STATE_ON = -1;
        public static final int STATE_OFF = 0;
        public static final int STATE_DEFAULT = STATE_OFF;
        public static final String TABLE_NAME = "Appliance";
        public static final String TABLE_TEMP_NAME = "temp";
        public static final String COLUMN_BLE_ID = "BLE_ID";
        public static final String COLUMN_PORT_ID = "Port";
        public static final String COLUMN_NAME = "Name";
        public static final String COLUMN_TYPE_ID = "TypeID";
        public static final String COLUMN_ON_WHEN_VACANT = "OnWhenVacant";
        public static final String COLUMN_STATE = "State";
        public static final String COLUMN_ROOM_ID = "RoomID";
        public static final String COLUMN_COUNT = "Counter";
        public static final String CREATE_ENTRIES =
                "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                        COLUMN_BLE_ID + " INTEGER NOT NULL, " +
                        COLUMN_PORT_ID + " INTEGER NOT NULL, " +
                        COLUMN_NAME + " TEXT," +
                        COLUMN_TYPE_ID + " INTEGER," +
                        COLUMN_ON_WHEN_VACANT + " BOOLEAN," +
                        COLUMN_STATE + " INTEGER," +
                        COLUMN_ROOM_ID + " INTEGER," +
                        COLUMN_COUNT + " INTEGER," +
                        "PRIMARY KEY (" + COLUMN_BLE_ID + ", " + COLUMN_PORT_ID +
                        ") )";
        public static final String CREATE_TEMP_ENTRIES =
                "CREATE TABLE IF NOT EXISTS temp (" +
                        COLUMN_BLE_ID + " INTEGER NOT NULL, " +
                        COLUMN_PORT_ID + " INTEGER NOT NULL, " +
                        COLUMN_NAME + " TEXT," +
                        COLUMN_TYPE_ID + " INTEGER," +
                        COLUMN_ON_WHEN_VACANT + " BOOLEAN," +
                        COLUMN_STATE + " INTEGER," +
                        COLUMN_ROOM_ID + " INTEGER," +
                        COLUMN_COUNT + " INTEGER," +
                        "PRIMARY KEY (" + COLUMN_BLE_ID + ", " + COLUMN_PORT_ID +
                        ") )";
        public static final String DROP_TABLE =
                "DROP TABLE IF EXISTS " + TABLE_NAME;
        public static final String[] SELECT_ALL = {
                COLUMN_BLE_ID,
                COLUMN_PORT_ID,
                COLUMN_NAME,
                COLUMN_TYPE_ID,
                COLUMN_ON_WHEN_VACANT,
                COLUMN_STATE,
                COLUMN_ROOM_ID,
                COLUMN_COUNT,
        };

        public static ContentValues put(int bleId, int portId, String name, int type,
                                 boolean onWhenVacant, int state, int roomId, int count)
        {
            ContentValues values = new ContentValues();
            values.put(COLUMN_BLE_ID, bleId);
            values.put(COLUMN_PORT_ID, portId);
            values.put(COLUMN_NAME, name);
            values.put(COLUMN_TYPE_ID, type);
            values.put(COLUMN_ON_WHEN_VACANT, onWhenVacant);
            values.put(COLUMN_STATE, state);
            values.put(COLUMN_ROOM_ID, roomId);
            values.put(COLUMN_COUNT, count);
            return values;
        }
        public static ContentValues put(com.tcy314.home.DBnClass.Appliance ap)
        {
            ContentValues values = new ContentValues();
            values.put(COLUMN_BLE_ID, ap.getBleId());
            values.put(COLUMN_PORT_ID, ap.getPortId());
            values.put(COLUMN_NAME, ap.getName());
            values.put(COLUMN_TYPE_ID, ap.getTypeId());
            values.put(COLUMN_ON_WHEN_VACANT, ap.isOnWhenVacant());
            values.put(COLUMN_STATE, ap.getState());
            values.put(COLUMN_ROOM_ID, ap.getRoomId());
            values.put(COLUMN_COUNT, ap.getCount());
            return values;
        }
    }

    public static abstract class ElectronicType {
        public static final String TABLE_NAME = "Type";
        public static final String COLUMN_TYPE_ID = "TypeID";
        public static final String COLUMN_NUMBER_OF_COLUMN = "NumberOfColumn";
        public static final String COLUMN_NAME = "Name";
        public static final String BUTTON_TYPE = "ButtonType";
        public static final String CREATE_ENTRIES =
                "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                        COLUMN_TYPE_ID + " INTEGER PRIMARY KEY, " +
                        COLUMN_NUMBER_OF_COLUMN + " INTEGER, " +
                        COLUMN_NAME + " TEXT, " +
                        BUTTON_TYPE + " TEXT" +
                        ")";
        public static  final String DROP_TABLE =
                "DROP TABLE IF EXISTS " + TABLE_NAME;
        public static final String[] SELECT_ALL = {
                COLUMN_TYPE_ID,  COLUMN_NUMBER_OF_COLUMN, COLUMN_NAME, BUTTON_TYPE
        };
        public static ContentValues put(int id, String name, String buttonType, int numberOfColumn) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_TYPE_ID, id);
            values.put(COLUMN_NAME, name);
            values.put(BUTTON_TYPE, buttonType);
            values.put(COLUMN_NUMBER_OF_COLUMN, numberOfColumn);
            return values;
        }
    }

    public static abstract class Room {
        public static final String TABLE_NAME = "Room";
        public static final String COLUMN_ROOM_ID = "RoomID";
        public static final String COLUMN_NAME = "Name";
        public static final String CREATE_ENTRIES =
                "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                        COLUMN_ROOM_ID + " INTEGER PRIMARY KEY, " +
                        COLUMN_NAME + " TEXT" +
                        ")";
        public static  final String DROP_TABLE =
                "DROP TABLE IF EXISTS " + TABLE_NAME;
        public static final String[] SELECT_ALL = {
                COLUMN_ROOM_ID, COLUMN_NAME
        };

        public static ContentValues put(int id, String name) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_ROOM_ID, id);
            values.put(COLUMN_NAME, name);
            return values;
        }
    }

    public static abstract class BLE {
        public static final String TABLE_NAME = "BLE";
        public static final String COLUMN_BLE_ID = "BLE_ID";
        public static final String COLUMN_ADDRESS = "Address";
        public static final String COLUMN_ROOM_ID = "RoomID";
        public static final String CREATE_ENTRIES =
                "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                        COLUMN_BLE_ID + " INTEGER PRIMARY KEY, " +
                        COLUMN_ADDRESS + " TEXT," +
                        COLUMN_ROOM_ID + " INTEGER" +
                        ")";
        public static  final String DROP_TABLE =
                "DROP TABLE IF EXISTS " + TABLE_NAME;
        public static final String[] SELECT_ALL = {
                COLUMN_BLE_ID, COLUMN_ADDRESS, COLUMN_ROOM_ID
        };

        public static ContentValues put(int bleId, String address, int roomId)
        {
            ContentValues values = new ContentValues();
            values.put(COLUMN_BLE_ID, bleId);
            values.put(COLUMN_ADDRESS, address);
            values.put(COLUMN_ROOM_ID, roomId);
            return values;
        }
    }

    public static abstract class Event {
        public final static long INTERVAL_NEVER = 0;
        public final static long INTERVAL_MINUTE = 60000;
        public final static long INTERVAL_HOUR = 1800000;
        public final static long INTERVAL_DAY = 86400000;
        public final static long INTERVAL_WEEK = 604800000;
        public static final String TABLE_NAME = "Event";
        public static final String COLUMN_ID = "ID";
        public static final String COLUMN_TITLE = "Title";
        public static final String COLUMN_BLE_ID = "BLE_ID";
        public static final String COLUMN_PORT_ID = "Port";
        public static final String COLUMN_START = "StartTime";
        public static final String COLUMN_START_STATE = "StartState";
        public static final String COLUMN_END = "EndTime";
        public static final String COLUMN_END_STATE = "EndState";
        public static final String COLUMN_REPEAT_OPTION = "RepeatOption";
        public static final String COLUMN_UNTIL = "UntilTime";
        public static final String CREATE_ENTRIES =
                "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                        COLUMN_ID + " INTEGER PRIMARY KEY, " +
                        COLUMN_TITLE + " TEXT, " +
                        COLUMN_BLE_ID + " INTEGER NOT NULL, " +
                        COLUMN_PORT_ID + " INTEGER NOT NULL,  " +
                        COLUMN_START + " INTEGER NOT NULL, " +
                        COLUMN_START_STATE + " INTEGER NOT NULL, " +
                        COLUMN_END + " INTEGER, " +
                        COLUMN_END_STATE + " INTEGER, " +
                        COLUMN_REPEAT_OPTION + " INTEGER,  " +
                        COLUMN_UNTIL + " INTEGER" +
                        ")";
        public static  final String DROP_TABLE =
                "DROP TABLE IF EXISTS " + TABLE_NAME;
        public static final String[] SELECT_ALL = {
                COLUMN_ID, COLUMN_TITLE, COLUMN_BLE_ID, COLUMN_PORT_ID,
                COLUMN_START, COLUMN_START_STATE, COLUMN_END, COLUMN_END_STATE,
                COLUMN_REPEAT_OPTION, COLUMN_UNTIL
        };

        public static ContentValues put(String name,
                com.tcy314.home.DBnClass.Appliance.PrimaryKey primaryKey,
                long startTime, int startState, long endTime, int endState,
                int repeatOption, long untilTime)
        {
            ContentValues values = new ContentValues();
            values.put(COLUMN_TITLE, name);
            values.put(COLUMN_BLE_ID, primaryKey.bleId);
            values.put(COLUMN_PORT_ID, primaryKey.portId);
            values.put(COLUMN_START, startTime);
            values.put(COLUMN_START_STATE, startState);
            values.put(COLUMN_END, endTime);
            values.put(COLUMN_END_STATE, endState);
            values.put(COLUMN_REPEAT_OPTION, repeatOption);
            values.put(COLUMN_UNTIL, untilTime);
            return values;
        }
        public static ContentValues put(com.tcy314.home.DBnClass.Event event) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_TITLE, event.getTitle());
            values.put(COLUMN_BLE_ID, event.getAppk().bleId);
            values.put(COLUMN_PORT_ID, event.getAppk().portId);
            values.put(COLUMN_START, event.getStartCalendar().getTimeInMillis());
            values.put(COLUMN_END, event.getEndCalendar().getTimeInMillis());
            values.put(COLUMN_UNTIL, event.getUntilCalendar().getTimeInMillis());
            values.put(COLUMN_START_STATE, event.getStartState());
            values.put(COLUMN_END_STATE, event.getEndState());
            values.put(COLUMN_REPEAT_OPTION, event.getRepeatOption());
            return values;
        }
    }
}
