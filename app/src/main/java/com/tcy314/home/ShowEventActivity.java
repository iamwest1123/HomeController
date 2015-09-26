package com.tcy314.home;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.tcy314.home.DBnClass.Appliance;
import com.tcy314.home.DBnClass.ControllerDbHelper;
import com.tcy314.home.DBnClass.DbEntry;
import com.tcy314.home.DBnClass.Event;
import com.tcy314.home.alarm.Alarm;

import java.util.ArrayList;

/**
 * Created by Matthew Ma on 22/9/2015.
 */
public class ShowEventActivity extends Activity {
    public final static String APPLIANCE = "com.tcy314.showeventactivity.appliance";
    private ControllerDbHelper mDbHelper;
    private Alarm mAlarm;
    private Appliance appliance;
    private EventAdapter eventAdapter;
    private ArrayList<Event> eventArrayList = new ArrayList<>();
    private Context context;
    private TextView tv_title;
    private TextView tv_startTime;
    private TextView tv_startStatus;
    private TextView tv_endTime;
    private TextView tv_endStatus;
    private TextView tv_repeat;
    private LinearLayout ll_end;
    private boolean isArrayListUpdated = false;
    private int eventIdToBeUpdate;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_show_main);
        mDbHelper = ((mBaseApplication)this.getApplicationContext()).getDbHelper();
        mAlarm = ((mBaseApplication)this.getApplicationContext()).getAlarm();
        context = this;
        int[] apArray = getIntent().getIntArrayExtra(APPLIANCE);
        appliance = mDbHelper.getApplianceByPrimaryKey(new Appliance.PrimaryKey(apArray[0],apArray[1]));
        if (appliance != null)
            getActionBar().setTitle(appliance.getName());
        eventAdapter = new EventAdapter();
        ListView lv = (ListView) findViewById(R.id.event_show_lv);
        lv.setAdapter(eventAdapter);
        updateEventList();

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setItems(R.array.event_show_alert_dialog, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // The 'which' argument contains the index position
                        // of the selected item
                        switch (which) {
                            case 0:
                                StartEditActivity(position);
                                break;
                            case 1:
                                CreateDeleteDialog(position);
                                break;
                            default:
                                break;
                        }
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isArrayListUpdated) {
            updateEventList();
        }
    }

    private void updateEventList() {
        eventArrayList.clear();
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor cursor = db.query(DbEntry.Event.TABLE_NAME,
                DbEntry.Event.SELECT_ALL,
                DbEntry.Event.COLUMN_BLE_ID + " =? and " + DbEntry.Event.COLUMN_PORT_ID + " =?",
                new String[]{String.valueOf(appliance.getBleId()), String.valueOf(appliance.getPortId())},
                null, null, DbEntry.Event.COLUMN_START + " ASC");
        if (cursor.moveToFirst()) {
            do {
                eventArrayList.add(new Event(cursor));
            } while (cursor.moveToNext());
            mDbHelper.addEventToMap(eventArrayList);
        }
        cursor.close();
        eventAdapter.notifyDataSetChanged();
    }

    private void StartEditActivity(final int position) {
        Intent intent = new Intent(context, EditEventActivity.class);
        int eventId = eventArrayList.get(position).getId();
        intent.putExtra(EditEventActivity.TITLE, context.getString(R.string.edit_event));
        intent.putExtra(EditEventActivity.EVENT_ID, eventId);
        context.startActivity(intent);
        isArrayListUpdated = true;
    }

    private void CreateDeleteDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.event_delete_message)
                .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Delete from database
                        isArrayListUpdated = true;
                        int eventId = eventArrayList.get(position).getId();
                        //  delete alarm
                        mAlarm.delete(mDbHelper.getEventByPrimaryKey(eventId));
                        boolean deleted = mDbHelper.deleteEventByPrimaryKey(eventId);
                        updateEventList();
                        if (!deleted) {
                            Log.e("Delete event","Nothing is deleted");
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                        // Do nothing
                    }
                });
        // Create the AlertDialog object and return it
        AlertDialog alert = builder.create();
        alert.show();
    }

    public class EventAdapter extends ArrayAdapter<Event> {
        private LayoutInflater inflater;

        public EventAdapter() {
            super(context, R.layout.event_show_row, eventArrayList);
            inflater = ( LayoutInflater )context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null)
                convertView = inflater.inflate(R.layout.event_show_row, parent, false);
            Event event = eventArrayList.get(position);
            tv_title = (TextView) convertView.findViewById(R.id.event_show_title);
            tv_startTime = (TextView) convertView.findViewById(R.id.event_show_tv_startTime);
            tv_endTime = (TextView) convertView.findViewById(R.id.event_show_tv_endTime);
            tv_startStatus = (TextView) convertView.findViewById(R.id.event_show_tv_startStatus);
            tv_endStatus = (TextView) convertView.findViewById(R.id.event_show_tv_endStatus);
            tv_repeat = (TextView) convertView.findViewById(R.id.event_show_tv_repeat);
            ll_end = (LinearLayout) convertView.findViewById(R.id.event_show_ll_end);

            tv_title.setText(event.getTitle());
            tv_startTime.setText(event.getStartCalendarInString(Event.DATE_TIME_FORMAT));
            tv_endTime.setText(event.getEndCalendarInString(Event.DATE_TIME_FORMAT));
            switch (event.getStartState())
            {
                case DbEntry.Appliance.STATE_ON:
                    tv_startStatus.setText("On"); break;
                case DbEntry.Appliance.STATE_OFF:
                    tv_startStatus.setText("Off"); break;
                case DbEntry.Appliance.STATE_NOT_SET:
                    tv_startStatus.setText("Not set"); break;
                default: tv_startStatus.setText(String.valueOf(event.getStartState())+"%"); break;
            }
            switch (event.getEndState())
            {
                case DbEntry.Appliance.STATE_ON:
                    tv_endStatus.setText("On"); break;
                case DbEntry.Appliance.STATE_OFF:
                    tv_endStatus.setText("Off"); break;
                case DbEntry.Appliance.STATE_NOT_SET:
                    ll_end.setVisibility(View.GONE);break;
                default: tv_startStatus.setText(String.valueOf(event.getStartState())+"%"); break;
            }

            String repeatString =
                    getResources().getStringArray(R.array.event_repeat_option)[Event.getRepeatEnum(event.getRepeatOption())];

            if (event.getRepeatOption() != DbEntry.Event.INTERVAL_NEVER)
                repeatString += " until " + event.getUntilCalendarInString(Event.DATE_FORMAT);
            tv_repeat.setText(repeatString);
            return convertView;
        }
    }
}
