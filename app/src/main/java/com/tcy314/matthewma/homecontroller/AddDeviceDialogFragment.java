package com.tcy314.matthewma.homecontroller;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Matthew Ma on 5/9/2015.
 */
public class AddDeviceDialogFragment extends DialogFragment {
    private static ControllerDbHelper mDbHelper;
    private Activity activity;
    private ArrayList<String> roomList;
    private boolean hasBleError;
    private boolean hasRoomNameError;
    private boolean isSelectRoom;
    private Spinner mSpinner;
    private Button mButton;
    private EditText et_address;
    private EditText et_room;
    private LinearLayout ll_spinner;
    private LinearLayout ll_et;
    private TextView tv_roomError;
    private TextView tv_addressError;
    private Button positiveButton;

    static AddDeviceDialogFragment newInstance() {
        return new AddDeviceDialogFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hasBleError = true;
        hasRoomNameError = true;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Create the AlertDialog object and return it
        activity = getActivity();
        mDbHelper = new ControllerDbHelper(activity);
        LayoutInflater li = LayoutInflater.from(activity);
        View view = li.inflate(R.layout.add_ble_device, null);

        // init button
        mSpinner = (Spinner) view.findViewById(R.id.spinner_room);
        mButton = (Button) view.findViewById(R.id.button);
        et_address = (EditText) view.findViewById(R.id.et_bleAddress);
        et_room = (EditText) view.findViewById(R.id.et_roomName);
        ll_spinner = (LinearLayout) view.findViewById(R.id.ll_addToSpinner);
        ll_et = (LinearLayout) view.findViewById(R.id.ll_addToEditText);
        tv_roomError = (TextView) view.findViewById(R.id.tv_roomErrorMsg);
        tv_addressError = (TextView) view.findViewById(R.id.tv_addressErrorMsg);
        ll_et.setVisibility(View.GONE);
        isSelectRoom = true;

        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
        alertDialogBuilder.setView(view);
        alertDialogBuilder.setTitle("Add Device");
        //alertDialogBuilder.show().getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);

        // Create Room List
        roomList = new ArrayList<>();
        for (int i = 2; mDbHelper.getRoomByPrimaryKey(i) != null; i++) {
            roomList.add(mDbHelper.getRoomByPrimaryKey(i));
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, roomList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(adapter);

        // create alert dialog
        alertDialogBuilder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
                if (!isSelectRoom) {
                    SQLiteDatabase db = mDbHelper.getWritableDatabase();
                    Cursor cursor = db.query(DbEntry.Room.TABLE_NAME,
                            DbEntry.Room.SELECT_ALL,
                            null, null, null, null, null);
                    cursor.moveToFirst();
                    int count = cursor.getCount();
                    ContentValues cv = DbEntry.Room.put(count + 1, et_room.getText().toString());
                    db.insert(DbEntry.Room.TABLE_NAME, null, cv);
                    cursor.close();
                    // TODO Bug fix: navigation drawer does not update
                }
                // TODO Connect with BLE and fetch data
                // TODO Add data to database
            }
        });
        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });
        return alertDialogBuilder.create();
    }

    @Override
    public void onStart() {
        super.onStart();
        AlertDialog d = (AlertDialog) getDialog();
        positiveButton = (Button)d.getButton(Dialog.BUTTON_POSITIVE);
        positiveButton.setEnabled(false);

        et_address.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String address = s.toString();
                if (s.toString().isEmpty()) {
                    tv_addressError.setText("BLE Address should not be blank");
                    tv_addressError.setVisibility(View.VISIBLE);
                    hasBleError = true;
                } else {
                    // TODO Check if the address is valid
                    tv_addressError.setVisibility(View.INVISIBLE);
                    hasBleError = false;
                }
                updatePositiveButton();
            }
        });

        et_room.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String name = s.toString();
                if (s.toString().isEmpty()) {
                    tv_roomError.setText("Room name should not be blank");
                    tv_roomError.setVisibility(View.VISIBLE);
                    hasRoomNameError = true;
                    updatePositiveButton();
                    return;
                } else {
                    for (String string : roomList)
                        if (name.equals(string)) {
                            tv_roomError.setText("Room name should be unique");
                            tv_roomError.setVisibility(View.VISIBLE);
                            hasRoomNameError = true;
                            updatePositiveButton();
                            return;
                        }
                }
                hasRoomNameError = false;
                tv_roomError.setVisibility(View.INVISIBLE);
                updatePositiveButton();
            }
        });

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSelectRoom) { // change to add room
                    ll_spinner.setVisibility(View.GONE);
                    ll_et.setVisibility(View.VISIBLE);
                    mButton.setText(getString(R.string.dialog_add_to_existing_room));
                    isSelectRoom = false;
                    updatePositiveButton();
                } else {    // change to select room
                    ll_et.setVisibility(View.GONE);
                    ll_spinner.setVisibility(View.VISIBLE);
                    mButton.setText(getString(R.string.dialog_add_to_new_room));
                    isSelectRoom = true;
                    updatePositiveButton();
                }
            }
        });
    }

    private void updatePositiveButton() {
        boolean hasError = false;
        if (hasBleError)
            hasError = true;
        if ((!isSelectRoom) && hasRoomNameError)
            hasError = true;
        if (hasError)
            positiveButton.setEnabled(false);
        else
            positiveButton.setEnabled(true);
    }
}
