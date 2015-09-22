package com.tcy314.matthewma.homecontroller;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Matthew Ma on 22/9/2015.
 */
public class ShowEventActivity extends Activity {
    public final static String APPLIANCE = "com.tcy314.showeventactivity.appliance";
    private static ControllerDbHelper mDbHelper;
    private Appliance appliance;
    private EventAdapter eventAdapter;
    private ArrayList<Event> arrayList = new ArrayList<>();
    private TextView tv_title;
    private TextView tv_startTime;
    private TextView tv_startStatus;
    private TextView tv_endTime;
    private TextView tv_endStatus;
    private TextView tv_repeat;
    private LinearLayout ll_end;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_show_main);
        mDbHelper = new ControllerDbHelper(this);
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        int[] apArray = getIntent().getIntArrayExtra(APPLIANCE);
        appliance = mDbHelper.getApplianceByPrimaryKey(new Appliance.PrimaryKey(apArray[0],apArray[1]));
            getActionBar().setTitle(appliance.getName());
        Cursor cursor = db.query(DbEntry.Event.TABLE_NAME,
                DbEntry.Event.SELECT_ALL,
                DbEntry.Event.COLUMN_BLE_ID + " =? and " + DbEntry.Event.COLUMN_PORT_ID + " =?",
                new String[]{String.valueOf(appliance.getBleId()), String.valueOf(appliance.getPortId())},
                null, null, DbEntry.Event.COLUMN_START + " ASC");
        if (cursor.moveToFirst()) {
            do {
                arrayList.add(new Event(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        eventAdapter = new EventAdapter(this);
        ListView lv = (ListView) findViewById(R.id.event_show_lv);
        lv.setAdapter(eventAdapter);
    }

    public class EventAdapter extends BaseAdapter {
        Context context;
        private LayoutInflater inflater;

        public EventAdapter(Context context) {
            this.context = context;
            inflater = ( LayoutInflater )context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        @Override
        public int getCount() {
            return arrayList.size();
        }

        @Override
        public Event getItem(int position) {
            return arrayList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            Event event = getItem(position);
            convertView = inflater.inflate(R.layout.event_show_layout, parent, false);
            tv_title = (TextView) convertView.findViewById(R.id.event_show_title);
            tv_startTime = (TextView) convertView.findViewById(R.id.event_show_tv_startTime);
            tv_endTime = (TextView) convertView.findViewById(R.id.event_show_tv_endTime);
            tv_startStatus = (TextView) convertView.findViewById(R.id.event_show_tv_startStatus);
            tv_endStatus = (TextView) convertView.findViewById(R.id.event_show_tv_endStatus);
            tv_repeat = (TextView) convertView.findViewById(R.id.event_show_tv_repeat);
            ll_end = (LinearLayout) convertView.findViewById(R.id.event_show_ll_end);

            tv_title.setText(event.getName());
            tv_startTime.setText(event.getStartTimeInString());
            tv_endTime.setText(event.getEndTimeInString());
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
            String repeatString = getResources().getStringArray(R.array.event_repeat_option)[event.getRepeatOption()];
            if (event.getRepeatOption() != DbEntry.Event.REPEAT_NEVER)
                repeatString += " until " + event.getUntilTimeInString();
            tv_repeat.setText(repeatString);
            return convertView;
        }
    }
}
