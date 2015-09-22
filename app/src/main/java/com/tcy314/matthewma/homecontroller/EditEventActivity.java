package com.tcy314.matthewma.homecontroller;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Matthew Ma on 12/9/2015.
 */
public class EditEventActivity extends Activity {
    public final static String TITLE = "com.tcy314.editeventactivity.title";
    public final static String APPLIANCE = "com.tcy314.editeventactivity.appliance";
    public final static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("E, d MMM yyyy", Locale.UK);
    public final static SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("kk:mm", Locale.UK);
    private static ControllerDbHelper mDbHelper;
    private Appliance appliance;
    private ElectronicType eType;
    private static Calendar startCalendar;
    private static Calendar endCalendar;
    private static Calendar untilCalendar;
    private boolean endTimeAdded = false;
    private int startState = DbEntry.Appliance.STATE_NOT_SET;
    private int endState = DbEntry.Appliance.STATE_NOT_SET;
    private int repeatOption = DbEntry.Event.REPEAT_NEVER;
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
        int[] apArray = getIntent().getIntArrayExtra(APPLIANCE);
        appliance = mDbHelper.getApplianceByPrimaryKey(new Appliance.PrimaryKey(apArray[0],apArray[1]));
        eType = mDbHelper.getElectronicTypeByPrimaryKey(appliance.getTypeId());


        final LayoutInflater inflater = (LayoutInflater) getActionBar().getThemedContext()
                .getSystemService(LAYOUT_INFLATER_SERVICE);
        final View customActionBarView = inflater.inflate(
                R.layout.event_edit_actionbar_custom_view, null);
        customActionBarView.findViewById(R.id.actionbar_save).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ContentValues values;
                        values = DbEntry.Event.put(et_title.getText().toString(),
                                appliance.getPrimaryKey(),
                                startCalendar.getTimeInMillis(), startState,
                                endCalendar.getTimeInMillis(), endState,
                                repeatOption, untilCalendar.getTimeInMillis()
                        );
                        mDbHelper.getWritableDatabase().insert(
                                DbEntry.Event.TABLE_NAME, null, values);
                        // "Save"
                        finish();
                    }
                });

        TextView actionbarTitle = (TextView) customActionBarView.findViewById(R.id.edit_event_actionbar_title);
        actionbarTitle.setText(getIntent().getStringExtra(TITLE));

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
        startCalendar = Calendar.getInstance();
        endCalendar = Calendar.getInstance();
        untilCalendar = Calendar.getInstance();


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

        String dateString = DATE_FORMAT.format(startCalendar.getTime());
        String timeString = TIME_FORMAT.format(startCalendar.getTime());
        tv_startDate.setText(dateString);
        tv_startTime.setText(timeString);
        tv_endDate.setText(dateString);
        tv_endTime.setText(timeString);
        tv_untilDate.setText(dateString);

        switch (eType.getButtonType()) {
            case ElectronicType.TOGGLE_BUTTON:
            case ElectronicType.SWITCH:
                startLayout = inflater.inflate(R.layout.event_edit_switch, null);
                Switch startView = (Switch) startLayout.findViewById(R.id.event_edit_switch);
                startView.setText("Set " + appliance.getName());
                startView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (((Switch) v).isChecked())
                            startState = DbEntry.Appliance.STATE_ON;
                        else
                            startState = DbEntry.Appliance.STATE_OFF;
                    }
                });
                ((ViewGroup)startView.getParent()).removeView(startView);
                ll_main.addView(startView, 2);
                startState = 0;
                break;
            default: break;
        }

        tv_addEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                endTimeAdded = true;
                ll_addEndTime.setVisibility(View.GONE);
                ll_endDateTime.setVisibility(View.VISIBLE);
                div_endDateTime.setVisibility(View.VISIBLE);
                switch (eType.getButtonType()) {
                    case ElectronicType.TOGGLE_BUTTON:
                    case ElectronicType.SWITCH:
                        endLayout = inflater.inflate(R.layout.event_edit_switch, null);
                        Switch endView = (Switch) endLayout.findViewById(R.id.event_edit_switch);
                        endView.setText("Set " + appliance.getName());
                        endView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (((Switch) v).isChecked())
                                    endState = DbEntry.Appliance.STATE_ON;
                                else
                                    endState = DbEntry.Appliance.STATE_OFF;
                            }
                        });
                        ((ViewGroup)endView.getParent()).removeView(endView);
                        ll_main.addView(endView, 5);
                        endState = 0;
                        break;
                    default: break;
                }
            }
        });
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
        tv_untilDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = DatePickerFragment.newInstance(R.id.event_edit_tv_untilDate);
                newFragment.show(getFragmentManager(), "datePicker");
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
                        tv_repeat.setText(getResources().getStringArray(R.array.event_repeat_option)[which]);
                        switch (which) {
                            case 0: ll_untilDate.setVisibility(View.GONE);
                                repeatOption = DbEntry.Event.REPEAT_NEVER;
                                break;
                            case 1: ll_untilDate.setVisibility(View.VISIBLE);
                                repeatOption = DbEntry.Event.REPEAT_EVERY_DAY;
                                break;
                            case 2: ll_untilDate.setVisibility(View.VISIBLE);
                                repeatOption = DbEntry.Event.REPEAT_EVERY_WEEK;
                                break;
                            case 3: ll_untilDate.setVisibility(View.VISIBLE);
                                repeatOption = DbEntry.Event.REPEAT_EVERY_MONTH;
                                break;
                            case 4: ll_untilDate.setVisibility(View.VISIBLE);
                                repeatOption = DbEntry.Event.REPEAT_EVERY_YEAR;
                                break;
                            default: ll_untilDate.setVisibility(View.VISIBLE);
                                break;
                        }
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
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
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            TextView tv = (TextView) getActivity().findViewById(id_update);
            if (id_update == R.id.event_edit_tv_startDate) {
                startCalendar.set(year, month, day);
                tv.setText(DATE_FORMAT.format(startCalendar.getTime()));
            } else if (id_update == R.id.event_edit_tv_endDate) {
                endCalendar.set(year, month, day);
                tv.setText(DATE_FORMAT.format(endCalendar.getTime()));
            } else {
                untilCalendar.set(year, month, day);
                tv.setText(DATE_FORMAT.format(untilCalendar.getTime()));
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
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            TextView tv = (TextView) getActivity().findViewById(id_update);

            if (id_update == R.id.event_edit_tv_startTime) {
                startCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                startCalendar.set(Calendar.MINUTE, minute);
                tv.setText(TIME_FORMAT.format(startCalendar.getTime()));
            } else {
                endCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                endCalendar.set(Calendar.MINUTE, minute);
                tv.setText(TIME_FORMAT.format(endCalendar.getTime()));
            }
        }
    }
}
