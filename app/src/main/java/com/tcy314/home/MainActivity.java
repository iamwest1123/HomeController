package com.tcy314.home;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.app.DialogFragment;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.tcy314.home.DBnClass.Appliance;
import com.tcy314.home.DBnClass.ControllerDbHelper;
import com.tcy314.home.DBnClass.DbEntry;
import com.tcy314.home.DBnClass.ElectronicType;
import com.tcy314.home.DBnClass.TwoColumnAppliance;
import com.tcy314.home.service.BluetoothLeService;
import com.tcy314.home.service.ServiceManager;

import java.util.ArrayList;

public class MainActivity extends Activity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {
    private final static String TAG = MainActivity.class.getSimpleName();

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    private ControllerDbHelper mDbHelper;
    private static ApplianceAdapter applianceAdapter;
    private ArrayList<Object> frequentlyUsedArrayList;

    /**
     * Bluetooth related variables
     */
    //region Ble service variables
    private ServiceManager mServiceManager;
    private BluetoothLeService mBluetoothLeService;
    private String mDeviceAddress = "00:15:83:00:46:FE";
    // Code to manage Service lifecycle.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }
            // Automatically connects to the device upon successful start-up initialization.
            mBluetoothLeService.connect(mDeviceAddress);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };
    //endregion

    //region Override activity method
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDbHelper = ((BaseApplication)this.getApplicationContext()).getDbHelper();

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        // Initializes a Bluetooth adapter.  For API level 18 and above, get a reference to
        // BluetoothAdapter through BluetoothManager.
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            finish();
        }

        mServiceManager = ((BaseApplication)this.getApplicationContext()).getServiceManager();

        // Checks if Bluetooth is supported on the device.
        if (mServiceManager == null) {
            Toast.makeText(this, R.string.error_bluetooth_not_supported, Toast.LENGTH_SHORT).show();
            finish();
        }
        // bind with ble service
        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Toast.makeText(this, "Settings clicked", Toast.LENGTH_SHORT).show();
            return true;
        }
        else if (id == R.id.action_addDevices) {
            DialogFragment newFragment = AddDeviceDialogFragment.newInstance();
            newFragment.show(getFragmentManager(), "dialog");

            //Toast.makeText(this, "Add devices clicked", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //endregion

    public void onSectionAttached(int number) {
        // get title
        mTitle = mDbHelper.getRoomByPrimaryKey(number);
        switch (number)
        {
            case 1: //freq use
                setFrequentlyUsedArrayList(3);
                break;
            default:
                // Connect to BLE Devices in background thread
                setApplianceAdapterByRoomID(number);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

    private void setFrequentlyUsedArrayList(int limit) {
        // check if arraylist have the up to date
        if (frequentlyUsedArrayList != null) {
            int count = 0;
            for (Object ob : frequentlyUsedArrayList) {
                if (ob instanceof Appliance)
                    count++;
                else if (ob instanceof TwoColumnAppliance)
                    count += ((TwoColumnAppliance) ob).getLength();
                else {};
            }
            if (count == limit) {
                applianceAdapter = new ApplianceAdapter(this,frequentlyUsedArrayList);
                return;
            }
            frequentlyUsedArrayList.clear();
        }

        //update arraylist
        frequentlyUsedArrayList = new ArrayList<>();
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        Cursor appliance = db.query(DbEntry.Appliance.TABLE_NAME,
                DbEntry.Appliance.SELECT_ALL, null, null, null, null,
                DbEntry.Appliance.COLUMN_COUNT + " DESC", String.valueOf(limit));
        // store them in temp
        ArrayList<Appliance> temp = new ArrayList<>();
        if (appliance.moveToFirst()) {
            do {
                temp.add(new Appliance(appliance));
            } while (appliance.moveToNext());
        }
        db.execSQL("DROP TABLE IF EXISTS temp");
        db.execSQL(DbEntry.Appliance.CREATE_TEMP_ENTRIES);
        for (Appliance ap : temp) {
            ContentValues cv = DbEntry.Appliance.put(ap);
            db.insert("temp", null, cv);
        }
        temp.clear();
        appliance = db.query(DbEntry.Appliance.TABLE_TEMP_NAME,
                DbEntry.Appliance.SELECT_ALL, null, null, null, null,
                DbEntry.Appliance.COLUMN_ROOM_ID + " ASC, " + DbEntry.Appliance.COLUMN_TYPE_ID + " ASC");
        if (appliance.moveToFirst()) {
            int prevRoomId = 0, prevTypeId = 0;
            String headerString;
            do {
                Appliance ap = new Appliance(appliance);
                ElectronicType eType = mDbHelper.getElectronicTypeByPrimaryKey(ap.getTypeId());
                if ((prevRoomId != ap.getRoomId()) || (prevTypeId != ap.getTypeId())) {
                    prevRoomId = ap.getRoomId();
                    prevTypeId = ap.getTypeId();
                    headerString = mDbHelper.getRoomByPrimaryKey(prevRoomId);
                    headerString += " " + eType.getName();
                    frequentlyUsedArrayList.add(headerString);
                }
                if (eType.getNumberOfColumn() == 1) {
                    frequentlyUsedArrayList.add(new Appliance(appliance));
                }
                else {
                    // fit 2 buttons into 1 row if possible
                    if (appliance.moveToNext()) {
                        Appliance ap2 = new Appliance(appliance);
                        if ((prevRoomId==ap2.getRoomId()) && (prevTypeId==ap2.getTypeId())) {
                            frequentlyUsedArrayList.add(new TwoColumnAppliance(ap, ap2));
                        }
                        else {
                            appliance.moveToPrevious();
                            frequentlyUsedArrayList.add(new TwoColumnAppliance(ap));
                        }
                    }
                    else {  // ap is the last
                        frequentlyUsedArrayList.add(new TwoColumnAppliance(ap));
                    }
                }
            } while (appliance.moveToNext());
        }
        applianceAdapter = new ApplianceAdapter(this,frequentlyUsedArrayList);
        appliance.close();
        db.execSQL("DROP TABLE IF EXISTS temp");
    }

    private void setApplianceAdapterByRoomID(int roomId) {
        ArrayList<Object> arrayList = new ArrayList<>();
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        Cursor distinctTypeCursor = db.query(true,
                DbEntry.Appliance.TABLE_NAME,
                new String[]{DbEntry.Appliance.COLUMN_TYPE_ID},
                DbEntry.Appliance.COLUMN_ROOM_ID + "=?",
                new String[]{String.valueOf(roomId)},
                null,
                null,
                DbEntry.Appliance.COLUMN_TYPE_ID + " ASC ", null, null);
        if (distinctTypeCursor.moveToFirst()) {
            do {
                // set header
                int distinctTypeId = distinctTypeCursor.getInt(0);
                ElectronicType eType = mDbHelper.getElectronicTypeByPrimaryKey(distinctTypeId);
                arrayList.add(eType.getName());
                // -SELECT * from Appliance WHERE type=type SORT BY name ASC
                Cursor applianceCursor = db.query(DbEntry.Appliance.TABLE_NAME,
                        DbEntry.Appliance.SELECT_ALL,
                        DbEntry.Appliance.COLUMN_ROOM_ID + "=? and " + DbEntry.Appliance.COLUMN_TYPE_ID + "=? ",
                        new String[]{String.valueOf(roomId), String.valueOf(distinctTypeId)},
                        null,
                        null,
                        DbEntry.Appliance.COLUMN_NAME + " ASC ");
                applianceCursor.moveToFirst();
                if (eType.getNumberOfColumn() == 1) {
                    do {
                        arrayList.add(new Appliance(applianceCursor));
                    } while (applianceCursor.moveToNext());
                }
                else {
                    for (int i = applianceCursor.getCount(); i > 0; i -= 2) {
                        arrayList.add(new TwoColumnAppliance(applianceCursor,i));
                    }
                }
                applianceCursor.close();
            } while (distinctTypeCursor.moveToNext());
        }
        applianceAdapter = new ApplianceAdapter(this,arrayList);
        distinctTypeCursor.close();
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView;
            int i = getArguments().getInt(ARG_SECTION_NUMBER);
            switch (i)
            {
                case 1:
                    /*rootView = inflater.inflate(R.layout.fragment_freq_use, container, false);
                    break;*/
                default:
                    rootView = inflater.inflate(R.layout.fragment_main, container, false);
                    ListView lv = (ListView) rootView.findViewById(R.id.listView);
                    lv.setAdapter(applianceAdapter);
                    break;
            }
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((MainActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }



}
