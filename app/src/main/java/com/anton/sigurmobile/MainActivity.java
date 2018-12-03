package com.anton.sigurmobile;

import android.arch.persistence.room.Room;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.anton.sigurmobile.db.EntitiesSettings;
import com.anton.sigurmobile.db.EntitiesSettingsRoomDatabase;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements MainMVP.view {

    String[] nameClass = (getClass().getName().toString()).split("\\.");
    int index = nameClass.length - 1;

    //----------------------------------------------------------------------------------------------
    //Global variables =============================================================================

    String LOG_TAG = nameClass[index];
    FrameLayout frameLayoutMain;
    PresenterMain presenterMain;
    ServiceSockets serviceSockets;
    Boolean boundService;
    private static EntitiesSettingsRoomDatabase settingsRoomDatabase;
    private String tagFragmentToolbar = FragmentToolbar.class.getSimpleName();
    private String tagFragmentPhoto = FragmentPhoto.class.getSimpleName();
    private String tagFragmentPosition = FragmentPosition.class.getSimpleName();
    private String tagFragmentConnectToServer = FragmentConnectToServer.class.getSimpleName();
    private String tagFragmentConnectionStatus = FragmentConnectionStatus.class.getSimpleName();
    private String tagFragmentButtons = FragmentButtons.class.getSimpleName();
    private String tagFragmentSettings = FragmentSettings.class.getSimpleName();

    //----------------------------------------------------------------------------------------------
    //Global Methods ===============================================================================

    private Fragment getFragment(String tag) {
        Fragment fragmentForReturn = null;
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.executePendingTransactions();
        for (Fragment fragment : fragmentManager.getFragments()) {
            if (fragment.getTag().contains(tag)) {
                fragmentForReturn = fragment;
            }
        }
        return fragmentForReturn;
    }

    //----------------------------------------------------------------------------------------------
    //Life Cicle Activivty =========================================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        presenterMain = new PresenterMain(this);
        frameLayoutMain = (FrameLayout) findViewById(R.id.frameLayout_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        settingsRoomDatabase = Room.databaseBuilder(getApplicationContext(),
                EntitiesSettingsRoomDatabase.class, "settingsDb").allowMainThreadQueries().build();
        presenterMain.mainActivityStarted();
    }

    @Override
    public void showToast(String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast toast = Toast.makeText(getApplicationContext(),
                        message, Toast.LENGTH_SHORT);
                toast.show();
            }
        });

    }

    //----------------------------------------------------------------------------------------------
    //Fragment Position ==============================================================================

    @Override
    public void updatePerson(String person) {

    }

    @Override
    public void updatePosition(String position) {

    }

    //----------------------------------------------------------------------------------------------
    //Fragment Buttons =============================================================================

    @Override
    public void buttonAcceptOnClick() {
        Log.d(LOG_TAG, "Button Accept onClick");
        presenterMain.buttonAcceptPressed();
    }

    @Override
    public void buttonRejectOnClick() {
        Log.d(LOG_TAG, "Button Reject onClick");
        presenterMain.buttonRejectPressed();
    }

    @Override
    public void enableButtons(Boolean enabled) {

    }

    @Override
    public void updateButtonStatus(Boolean enabled) {

    }

    //----------------------------------------------------------------------------------------------
    //Fragment Toolbar ========================================================================

    @Override
    public void imageButtonSettingsOnclick() {
        presenterMain.imageButtonSettingsPressed();
    }

    @Override
    public void enableToolbarButtonSettings(Boolean enable) {
        FragmentToolbar fragmentToolbar = (FragmentToolbar) getFragment(tagFragmentToolbar);
        try {
            fragmentToolbar.settingsButtonToolbarEnable(enable);
        } catch (NullPointerException e) {
            Log.e(LOG_TAG, "Null fragment");
        }
    }

    //----------------------------------------------------------------------------------------------
    //Service bind =================================================================================

    @Override
    public void bindServiceSocket() {
        Intent intent = null;
        intent = new Intent(this, ServiceSockets.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void unBindServiceSocket() {
        unbindService(mConnection);
    }

    public ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            Log.w(LOG_TAG, "Service connected");
            ServiceSockets.ServiceBinder binder = (ServiceSockets.ServiceBinder) service;
            serviceSockets = binder.getService();
            serviceSockets.setPresenterMain(presenterMain);
            presenterMain.serviceBinded();
            boundService = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.w(LOG_TAG, "Service disconnected");
        }
    };

    @Override
    public void serviceSocketConnectToserver(Map<String, String> params) {
        if (serviceSockets != null) {
            serviceSockets.connectToServer(params);
        } else {
            Log.w(LOG_TAG, "Service Socket is null");
        }

    }

    @Override
    public void serviceSocketRunServer(int port) {
        serviceSockets.runServer(port);
    }

    @Override
    public void serviceSocketSendData(String data) {
        serviceSockets.sendData(data);
    }

    @Override
    public void serviceSocketReceivedData(String data) {
        presenterMain.receivedMessage(data);
    }

    //----------------------------------------------------------------------------------------------
    //ALL Fragments ================================================================================

    @Override
    public void showAllFragments() {
        for (Fragment fragment:getSupportFragmentManager().getFragments()) {
            if (fragment.isHidden()) {
                try {
                    Log.w(LOG_TAG, "Hidden Fragment: " + fragment.getTag());
                } catch (NullPointerException e) {
                    Log.e(LOG_TAG, "Null: " + e);
                }
                getSupportFragmentManager().beginTransaction().show(fragment).commit();
            } else {
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentManager.popBackStack();
                fragmentTransaction.commit();
            }
        }
    }

    @Override
    public void hideAllFragments() {
        Log.w(LOG_TAG, "method name: " + String.valueOf(Thread.currentThread().getStackTrace()[2].getMethodName()));
        for (Fragment fragment:getSupportFragmentManager().getFragments()) {
            if (fragment.isVisible()) {
                Log.w(LOG_TAG, fragment.getTag() + " visible");
                if (!fragment.getTag().contains(tagFragmentToolbar)) {
                    getSupportFragmentManager().beginTransaction().hide(fragment).commit();
                }
                else {
                    Log.w(LOG_TAG, "Toolbar still visible");
                }
            }
        }
    }

    //----------------------------------------------------------------------------------------------
    //Fragment Settings ============================================================================

    @Override
    public void fragmentSettingsOnCreate() {
    }

    @Override
    public void updateUiSettings(Map settings) {
        FragmentSettings fragmentSettings = (FragmentSettings) getFragment(tagFragmentSettings);
        if (fragmentSettings != null) {
            fragmentSettings.showSetingsFromDb(settings);
        }
        else {
            Log.w(LOG_TAG, "Fragment settings is NULL for update UI");
        }
    }

    @Override
    public void updateTextSettings(String[] textArray) {
        FragmentSettings fragmentSettings = (FragmentSettings) getFragment(tagFragmentSettings);
        if (fragmentSettings != null) {
            fragmentSettings.showText(textArray);
        }
        else {
            Log.w(LOG_TAG, "Fragment settings is NULL for update UI");
        }
    }

    @Override
    public void showFragmentSettings() {
        Log.w(LOG_TAG, "method name: " + String.valueOf(Thread.currentThread().getStackTrace()[2].getMethodName()));
        FragmentSettings fragmentSettings = (FragmentSettings) getFragment(tagFragmentSettings);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (fragmentSettings != null) {
            Log.w(LOG_TAG, "Fragment NOT NULL");
            fragmentTransaction.show(fragmentSettings);
            fragmentTransaction.commit();
            Log.w(LOG_TAG, fragmentSettings.getTag());
        } else {
            Log.w(LOG_TAG, "Fragment is NULL");
            fragmentTransaction.add(R.id.layout_fragments_container, new FragmentSettings(), FragmentSettings.class.getSimpleName());
            fragmentTransaction.commit();
        }
    }

    @Override
    public void hideFragmentSettings() {
        Log.w(LOG_TAG, "method name: " + String.valueOf(Thread.currentThread().getStackTrace()[2].getMethodName()));
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        for (Fragment fragment:fragmentManager.getFragments()) {
            Log.w(LOG_TAG, "TAG: " + fragment.getTag());
            if (fragment.getTag().contains("FragmentSettings")) {
                Log.w(LOG_TAG, tagFragmentSettings + " visible: " + fragment.isVisible());
                fragmentTransaction.hide(fragment);
                fragmentTransaction.commit();
            }
        }

    }

    @Override
    public void buttonSettingsSaveOnclick(String ip, String login, String password, int port) {
        presenterMain.buttonSettingsSavePressed(ip, login, password, port);
    }

    @Override
    public void buttonSettingsCloseOnclick() {
        Log.w(LOG_TAG, "button onclick close settings");
        presenterMain.buttonSettingsClosePressed();
    }

    //----------------------------------------------------------------------------------------------
    //Fragment Connect To Server ===================================================================

    @Override
    public void showFragmentConnectToServer() {
        Log.w(LOG_TAG, "method name: " + String.valueOf(Thread.currentThread().getStackTrace()[2].getMethodName()));
        FragmentConnectToServer fragmentConnectToServer = (FragmentConnectToServer) getFragment(tagFragmentConnectToServer);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (fragmentConnectToServer != null) {
            Log.w(LOG_TAG, "Fragment NOT NULL");
            fragmentTransaction.show(fragmentConnectToServer);
            fragmentTransaction.commit();
            Log.w(LOG_TAG, fragmentConnectToServer.getTag());
        } else {
            fragmentTransaction.add(R.id.layout_fragments_container, new FragmentConnectToServer());
            fragmentTransaction.commit();
            Log.w(LOG_TAG, "Fragment is NULL");
        }
    }

    @Override
    public void hideConnectToServerLayout() {
        Log.w(LOG_TAG, "method name: " + String.valueOf(Thread.currentThread().getStackTrace()[2].getMethodName()));
        Thread thread = new Thread();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                FragmentConnectToServer fragmentConnectToServer = (FragmentConnectToServer) getFragment(tagFragmentConnectToServer);
                if (fragmentConnectToServer != null) {
                    Log.w(LOG_TAG, "Fragment NOT NULL");
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.hide(fragmentConnectToServer);
                    fragmentTransaction.commit();
                    Log.w(LOG_TAG, fragmentConnectToServer.getTag());
                } else {
                    Log.w(LOG_TAG, "Fragment is NULL");
                    hideAllFragments();
                }
            }
        });
    }

    @Override
    public void buttonConnectToServerOnClick() {
        presenterMain.buttonConnectToServerPressed();
    }

    //----------------------------------------------------------------------------------------------
    //Fragments Photo ==============================================================================

    @Override
    public void showPhoto(byte[] decodedImage) {

    }

    @Override
    public void showNoNamePerson() {

    }

    //----------------------------------------------------------------------------------------------
    //DB Communication =============================================================================

    @Override
    public void writeToDb(EntitiesSettings entitySettings) {
        settingsRoomDatabase.settingsDao().insert(entitySettings);
    }

    @Override
    public Map readFromDb() {
        List<EntitiesSettings> entitySettings = settingsRoomDatabase.settingsDao().getAllElements();
        Map<String, String> settings = new HashMap<>();
        for (EntitiesSettings setting : entitySettings) {
            settings.put("ip", setting.getServerIP());
            settings.put("login", setting.getLogin());
            settings.put("password", setting.getPassword());
            settings.put("port", String.valueOf(setting.getPort()));
        }

        return settings;
    }

    //==============================================================================================

    @Override
    public void toolbarChangeLable(String lable) {
        getSupportActionBar().setTitle(lable);
    }

    @Override
    public void udpateDestination(String detination) {

    }

}