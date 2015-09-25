package com.tcy314.home;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.os.Bundle;
import android.app.DialogFragment;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import com.tcy314.home.DBnClass.Appliance;
import com.tcy314.home.DBnClass.ControllerDbHelper;
import com.tcy314.home.DBnClass.DbEntry;
import com.tcy314.home.DBnClass.ElectronicType;
import com.tcy314.home.DBnClass.Event;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Matthew Ma on 12/9/2015.
 */
public class EditEventActivity extends Activity {
    public final static String TITLE = "com.tcy314.editeventactivity.title";
    public final static String APPLIANCE = "com.tcy314.editeventactivity.appliance";
    public final static String EVENT_ID = "com.tcy314.editeventactivity.eventid";
    public final static SimpleDateFormat DATE_FORMAT = Event.DATE_FORMAT;
    public final static SimpleDateFormat TIME_FORMAT = Event.TIME_FORMAT;
    private static ControllerDbHelper mDbHelper;
    private Appliance appliance;
    private static Event event;
    private ElectronicType eType;
    private boolean isEditingEvent;
    private LinearLayout ll_main;
    private LinearLayout ll_addEndTime;
    private LinearLayout ll_endDateTime;
    private LinearLayout ll_untilDate;
    private TextView tv_startDate;
    private TextView tv_startTime;
    private TextView tv_endDate;
    private TextView tv_endTime;
    private TextView tv_addEndTime;
    private TextView tv_repeat;
    private TextView tv_untilDate;
    private EditText et_title;
    private View div_endDateTime;
    private View startLayout;
    private View endLayout;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDbHelper = new ControllerDbHelper(this);
        // set event, appliance, eType
        String titleString = getIntent().getStringExtra(TITLE);
        int[] apArray = getIntent().getIntArrayExtra(APPLIANCE);
        final int eventId = getIntent().getIntExtra(EVENT_ID, -1);
        isEditingEvent = titleString.equals(getString(R.string.edit_event));
        if (isEditingEvent) {
            event = mDbHelper.getEventByPrimaryKey(eventId);
            appliance = mDbHelper.getApplianceByPrimaryKey(event.getAppk());
            eType = mDbHelper.getElectronicTypeByPrimaryKey(appliance.getTypeId());
        }
        else {  // add new event
            event = new Event();
            Appliance.PrimaryKey appk = new Appliance.PrimaryKey(apArray[0],apArray[1]);
            event.setAppk(appk);
            appliance = mDbHelper.getApplianceByPrimaryKey(appk);
            eType = mDbHelper.getElectronicTypeByPrimaryKey(appliance.getTypeId());
        }


        final LayoutInflater inflater = (LayoutInflater) getActionBar().getThemedContext()
                .getSystemService(LAYOUT_INFLATER_SERVICE);
        final View customActionBarView = inflater.inflate(
                R.layout.event_edit_actionbar, null);
        customActionBarView.findViewById(R.id.actionbar_save).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ContentValues values;
                        String title = et_title.getText().toString();
                        if (title.equals(""))
                            title = "(No title)";
                        event.setTitle(title);
                        values = DbEntry.Event.put(event);
                        if (isEditingEvent) {
                            mDbHelper.updateEventByPrimaryKey(event.getId(), values);
                            MainActivity.alarm.update(event);
                        } else {
                            int eventId = (int) mDbHelper.getWritableDatabase().insert(
                                    DbEntry.Event.TABLE_NAME, null, values);
                            Log.i("Event added", "ID: " + String.valueOf(eventId));
                            event.setId(eventId);
                            MainActivity.alarm.set(event);
                        }
                        finish();
                    }
                });

        TextView actionbarTitle = (TextView) customActionBarView.findViewById(R.id.edit_event_actionbar_title);
        actionbarTitle.setText(titleString);

        // Show the custom action bar view and hide the normal Home icon and title.
        final ActionBar actionBar = getActionBar();
        actionBar.setDisplayOptions(
                ActionBar.DISPLAY_SHOW_CUSTOM,
                ActionBar.DISPLAY_SHOW_CUSTOM
                        | ActionBar.DISPLAY_SHOW_HOME
                        | ActionBar.DISPLAY_SHOW_TITLE);
        actionBar.setCustomView(customActionBarView,
                new ActionBar.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT));
        // END_INCLUDE (inflate_set_custom_view)

        setContentView(R.layout.event_edit_main);

        ll_main = (LinearLayout) findViewById(R.id.event_edit_ll_main);
        ll_addEndTime = (LinearLayout) findViewById(R.id.event_edit_ll_addEndTime);
        ll_endDateTime = (LinearLayout) findViewById(R.id.event_edit_ll_endDateTime);
        ll_untilDate = (LinearLayout) findViewById(R.id.event_edit_ll_untilDate);
        tv_startDate = (TextView) findViewById(R.id.event_edit_tv_startDate);
        tv_startTime = (TextView) findViewById(R.id.event_edit_tv_startTime);
        tv_endDate = (TextView) findViewById(R.id.event_edit_tv_endDate);
        tv_endTime = (TextView) findViewById(R.id.event_edit_tv_endTime);
        tv_addEndTime = (TextView) findViewById(R.id.event_edit_tv_addEndTime);
        tv_repeat = (TextView) findViewById(R.id.event_edit_tv_repeat);
        tv_untilDate = (TextView) findViewById(R.id.event_edit_tv_untilDate);
        div_endDateTime = (View) findViewById(R.id.event_edit_divider_endDateTime);
        et_title = (EditText) findViewById(R.id.event_edit_et_title);

        if (isEditingEvent)
            et_title.setText(event.getTitle());

        // set status button and end time
        switch (eType.getButtonType()) {
            case ElectronicType.TOGGLE_BUTTON:
            case ElectronicType.SWITCH:
                startLayout = inflater.inflate(R.layout.event_edit_switch, null);
                Switch startView = (Switch) startLayout.findViewById(R.id.event_edit_switch);
                startView.setText("Set " + appliance.getName());
                event.setStartState(DbEntry.Appliance.STATE_DEFAULT);
                if (isEditingEvent) {
                    if (event.getStartState() == DbEntry.Appliance.STATE_ON)
                        startView.setChecked(true);
                    else
                        startView.setChecked(false);
                }
                startView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (((Switch) v).isChecked())
                            event.setStartState(DbEntry.Appliance.STATE_ON);
                        else
                            event.setStartState(DbEntry.Appliance.STATE_OFF);
                    }
                });
                ((ViewGroup)startView.getParent()).removeView(startView);
                ll_main.addView(startView, 2);
                break;
            default: break;
        }
        tv_addEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isEditingEvent) {
                    event.setEndCalendar(Calendar.getInstance());
                    event.getEndCalendar().set(Calendar.SECOND,0);
                }
                tv_endDate.setText(DATE_FORMAT.format(event.getEndCalendar().getTime()));
                tv_endTime.setText(TIME_FORMAT.format(event.getEndCalendar().getTime()));
                ll_addEndTime.setVisibility(View.GONE);
                ll_endDateTime.setVisibility(View.VISIBLE);
                div_endDateTime.setVisibility(View.VISIBLE);
                switch (eType.getButtonType()) {
                    case ElectronicType.TOGGLE_BUTTON:
                    case ElectronicType.SWITCH:
                        endLayout = inflater.inflate(R.layout.event_edit_switch, null);
                        Switch endView = (Switch) endLayout.findViewById(R.id.event_edit_switch);
                        endView.setText("Set " + appliance.getName());
                        event.setEndState(DbEntry.Appliance.STATE_DEFAULT);
                        if (isEditingEvent) {
                            if (event.getEndState() == DbEntry.Appliance.STATE_ON)
                                endView.setChecked(true);
                            else
                                endView.setChecked(false);
                        }
                        endView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (((Switch) v).isChecked())
                                    event.setEndState(DbEntry.Appliance.STATE_ON);
                                else
                                    event.setEndState(DbEntry.Appliance.STATE_OFF);
                            }
                        });
                        ((ViewGroup)endView.getParent()).removeView(endView);
                        ll_main.addView(endView, 5);
                        break;
                    default: break;
                }
            }
        });

        // set calendar
        if (isEditingEvent) {
            if (event.isEndTimeSet()) {
                tv_addEndTime.performClick();
            }
            setRepeatOption(event.getRepeatOption());
        }
        tv_startDate.setText(DATE_FORMAT.format(event.getStartCalendar().getTime()));
        tv_startTime.setText(TIME_FORMAT.format(event.getStartCalendar().getTime()));
        // tv_endDate, tv_endTime is set in tv_addEndTime.setOnClickListener
        // TODO maybe should move until date away too
        tv_untilDate.setText(DATE_FORMAT.format(event.getUntilCalendar().getTime()));



        tv_startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = DatePickerFragment.newInstance(R.id.event_edit_tv_startDate);
                newFragment.show(getFragmentManager(), "datePicker");
            }
        });
        tv_endDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = DatePickerFragment.newInstance(R.id.event_edit_tv_endDate);
                newFragment.show(getFragmentManager(), "datePicker");
            }
        });
        tv_untilDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = DatePickerFragment.newInstance(R.id.event_edit_tv_untilDate);
                newFragment.show(getFragmentManager(), "datePicker");
            }
        });
        tv_startTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = TimePickerFragment.newInstance(R.id.event_edit_tv_startTime);
                newFragment.show(getFragmentManager(), "timePicker");
            }
        });
        tv_endTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = TimePickerFragment.newInstance(R.id.event_edit_tv_endTime);
                newFragment.show(getFragmentManager(), "timePicker");
            }
        });

        tv_repeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActionBar().getThemedContext());
                builder.setItems(R.array.event_repeat_option, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // The 'which' argument contains the index position
                        // of the selected item
                            setRepeatOption(which);
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
    }

    private void setRepeatOption(long repeat) {
        tv_repeat.setText(getResources().getStringArray(R.array.event_repeat_option)[Event.getRepeatEnum(repeat)]);
        switch (Event.getRepeatEnum(repeat)) {
            case 0: ll_untilDate.setVisibility(View.GONE);
                event.setRepeatOption(DbEntry.Event.INTERVAL_NEVER);
                break;
            case 1:
                ll_untilDate.setVisibility(View.VISIBLE);
                event.setRepeatOption(DbEntry.Event.INTERVAL_MINUTE);
                break;
            case 2:
                ll_untilDate.setVisibility(View.VISIBLE);
                event.setRepeatOption(DbEntry.Event.INTERVAL_HOUR);
                break;
            case 3:
                ll_untilDate.setVisibility(View.VISIBLE);
                event.setRepeatOption(DbEntry.Event.INTERVAL_DAY);
                break;
            case 4:
                ll_untilDate.setVisibility(View.VISIBLE);
                event.setRepeatOption(DbEntry.Event.INTERVAL_WEEK);
                break;
            default: ll_untilDate.setVisibility(View.VISIBLE);
                break;
        }
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {
        private int id_update;

        static DatePickerFragment newInstance(int i) {
            DatePickerFragment dpf = new DatePickerFragment();
            dpf.id_update = i;
            return dpf;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            Calendar c;
            if (id_update == R.id.event_edit_tv_startDate) {
                c = event.getStartCalendar();
            } else if (id_update == R.id.event_edit_tv_endDate) {
                c = event.getEndCalendar();
            } else {
                c = event.getUntilCalendar();
            }
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            TextView tv = (TextView) getActivity().findViewById(id_update);
            if (id_update == R.id.event_edit_tv_startDate) {
                event.getStartCalendar().set(year, month, day);
                tv.setText(DATE_FORMAT.format(event.getStartCalendar().getTime()));
            } else if (id_update == R.id.event_edit_tv_endDate) {
                event.getEndCalendar().set(year, month, day);
                tv.setText(DATE_FORMAT.format(event.getEndCalendar().getTime()));
            } else {
                event.getUntilCalendar().set(year, month, day);
                tv.setText(DATE_FORMAT.format(event.getUntilCalendar().getTime()));
            }
        }
    }

    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {
        private int id_update;

        static TimePickerFragment newInstance (int i) {
            TimePickerFragment tpf = new TimePickerFragment();
            tpf.id_update = i;
            return tpf;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            Calendar c;
            if (id_update == R.id.event_edit_tv_startTime) {
                c = event.getStartCalendar();
            } else {
                c = event.getEndCalendar();
            }
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            TextView tv = (TextView) getActivity().findViewById(id_update);

            if (id_update == R.id.event_edit_tv_startTime) {
                event.getStartCalendar().set(Calendar.HOUR_OF_DAY, hourOfDay);
                event.getStartCalendar().set(Calendar.MINUTE, minute);
                tv.setText(TIME_FORMAT.format(event.getStartCalendar().getTime()));
            } else {
                event.getEndCalendar().set(Calendar.HOUR_OF_DAY, hourOfDay);
                event.getEndCalendar().set(Calendar.MINUTE, minute);
                tv.setText(TIME_FORMAT.format(event.getEndCalendar().getTime()));
            }
        }
    }
}
