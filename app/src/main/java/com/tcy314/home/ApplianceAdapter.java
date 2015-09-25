package com.tcy314.home;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.tcy314.home.DBnClass.Appliance;
import com.tcy314.home.DBnClass.ControllerDbHelper;
import com.tcy314.home.DBnClass.DbEntry;

import java.util.ArrayList;

/**
 * Created by Matthew Ma on 29/8/2015.
 */
public class ApplianceAdapter extends BaseAdapter {
    private ArrayList<Object> applianceArray;
    private LayoutInflater inflater;
    private Context context;
    private ControllerDbHelper mDbHelper;
    private static final int TYPE_SWITCH = 0;
    private static final int TYPE_DIVIDER = 1;
    private static final int TYPE_TOGGLE_BUTTON = 2;

    public ApplianceAdapter(Context context, ArrayList<Object> applianceArray) {
        this.applianceArray = applianceArray;
        this.context = context;
        this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mDbHelper = new ControllerDbHelper(context);
    }

    public Context getContext() {
        return context;
    }

    @Override
    public int getCount() {
        return applianceArray.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        return applianceArray.get(position);
    }

    @Override
    public int getViewTypeCount() {
        ControllerDbHelper mDbHelper = new ControllerDbHelper(context);
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor distinctViewTypeCursor = db.query(true,
                DbEntry.Appliance.TABLE_NAME,
                new String[]{DbEntry.Appliance.COLUMN_TYPE_ID},
                null, null, null, null, null, null, null);
        int count = distinctViewTypeCursor.getCount();
        distinctViewTypeCursor.close();
        return count + 1;   // plus the separator
    }

    @Override
    public int getItemViewType(int position) {
        Object object = getItem(position);
        if (object instanceof Appliance) {
            switch (((Appliance)object).getTypeId()) {
                case 2: return TYPE_SWITCH;    // not safe
                default: return TYPE_SWITCH;
            }
        }
        else if (object instanceof TwoColumnAppliance)
            switch (((TwoColumnAppliance)object).getType()) {
                case 1: return TYPE_TOGGLE_BUTTON; // not safe
                default: return TYPE_TOGGLE_BUTTON;
            }
        return TYPE_DIVIDER;
    }

    @Override
    public boolean isEnabled(int position) {
        return (getItemViewType(position) == TYPE_SWITCH);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int type = getItemViewType(position);
        if (convertView == null) {
            switch (type) {
                case TYPE_TOGGLE_BUTTON:
                    convertView = inflater.inflate(R.layout.ap_adapter_toggle_button, parent, false);
                    break;
                case TYPE_SWITCH:
                    convertView = inflater.inflate(R.layout.ap_adapter_switch, parent, false);
                    break;
                case TYPE_DIVIDER:
                    convertView = inflater.inflate(R.layout.ap_adapter_header, parent, false);
                    break;
            }
        }

        switch (type) {
            case TYPE_TOGGLE_BUTTON:
                TwoColumnAppliance ap = ((TwoColumnAppliance) getItem(position));
                ToggleButton tb;
                if (ap.getLength() >= 1 ) {
                    tb = (ToggleButton) convertView.findViewById(R.id.toggleButtonL);
                    setupToggleButton(tb, ap.getAppliance(0));
                }
                if (ap.getLength() >= 2) {
                    tb = (ToggleButton) convertView.findViewById(R.id.toggleButtonR);
                    setupToggleButton(tb, ap.getAppliance(1));
                }
                break;
            case TYPE_SWITCH:
                Appliance item = (Appliance)getItem(position);
                Switch aSwitch = (Switch)convertView.findViewById(R.id.switch1);
                aSwitch.setText(item.getName());
                aSwitch.setChecked(item.getState() != 0);
                setupLongClick(aSwitch, item);
                break;
            case TYPE_DIVIDER:
                TextView title = (TextView)convertView.findViewById(R.id.headerTitle);
                String titleString = (String)getItem(position);
                title.setText(titleString);
                break;
        }
        return convertView;
    }

    private void setupLongClick(final View v, final Appliance ap) {
        // TODO
        v.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(ap.getName())
                        .setItems(R.array.long_click_option, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // The 'which' argument contains the index position
                                // of the selected item
                                Intent intent;
                                switch (which) {
                                    case 0:
                                        intent = new Intent(context, EditEventActivity.class);
                                        intent.putExtra(EditEventActivity.TITLE, context.getString(R.string.new_event));
                                        intent.putExtra(EditEventActivity.APPLIANCE, ap.getPrimaryKey().toArray());
                                        context.startActivity(intent);
                                        break;
                                    case 1:
                                        intent = new Intent(context, ShowEventActivity.class);
                                        intent.putExtra(ShowEventActivity.APPLIANCE, ap.getPrimaryKey().toArray());
                                        context.startActivity(intent);
                                        break;
                                    default: break;
                                }
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
                return false;
            }
        });
    }

    private void setupToggleButton(ToggleButton tb, Appliance ap) {
        tb.setText(ap.getName());
        tb.setTextOn(ap.getName() + "\n[ON]");
        tb.setTextOff(ap.getName() + "\n[OFF]");
        tb.setAllCaps(false);
        tb.setFontFeatureSettings("");
        tb.setChecked(ap.getState() != 0);
        tb.setVisibility(View.VISIBLE);
        tb.setTag(ap.getPrimaryKey());
        setupLongClick(tb, ap);
        tb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Appliance.PrimaryKey appk = (Appliance.PrimaryKey) v.getTag();
                boolean b = ((ToggleButton) v).isChecked();
                // count++, update database
                Appliance appliance = mDbHelper.getApplianceByPrimaryKey(appk);
                ContentValues cv = new ContentValues();
                cv.put(DbEntry.Appliance.COLUMN_COUNT, appliance.getCount() + 1);
                mDbHelper.updateApplianceByPrimaryKey(appk.bleId, appk.portId, cv);
                // connect with BLE, Send msg to BLE
/*                String s = "BLE ID: " + i[0] +
                        "\nPort ID: " + i[1];
                if (((ToggleButton)v).isChecked())
                    s += "\nChecked";
                else
                    s += "\nNot Checked";
                s += "\nCount: " + String.valueOf(appliance.getCount()+1);
                Toast.makeText(context, s, Toast.LENGTH_SHORT).show();*/
            }
        });
    }
}
