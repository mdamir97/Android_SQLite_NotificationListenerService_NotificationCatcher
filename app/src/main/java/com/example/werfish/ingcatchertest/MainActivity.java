package com.example.werfish.ingcatchertest;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    final Database db = new Database(this);

    TextView stateField;
    TextView foundField;
    TextView packageField;
    Switch serviceSwitch;
    AutoCompleteTextView appChooser;

    String APPNAME;
    String PACKAGENAME;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initializing all the text fields and buttons
        stateField = (TextView) findViewById(R.id.TextState);
        foundField = (TextView) findViewById(R.id.TextFound);
        packageField = (TextView) findViewById(R.id.TextPackage);

        //Initializing the start searching switch
        serviceSwitch = (Switch) findViewById(R.id.ServiceSwitch);

        //Setting up the app chooser which user will choose the app with
        appChooser = (AutoCompleteTextView) findViewById(R.id.App_Chooser);

        String[] apps = getAppNames();

        //Setting up the adapter for the app chooser which will contain names of all installed apps
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, apps);
        appChooser.setAdapter(adapter);

        //Set all the default statuses of controls and application statuses
        APPNAME = appChooser.getText().toString();
        setFound(checkIfFound());
        //All the status checks and setting textviews and switch on the main activity
        setState(checkServiceState(NotificationListener.class));
        serviceSwitch.setChecked(false);

        //Check if the setting for reading notifications by the app is enabled
        //if it is not enables then guide the user to it
        notificationsAccessDialog(isNotificationsAccessChecked());

        Button btnNotifications = (Button) findViewById(R.id.Notification_Button);

        //Insert test records to see if everything is working
        if (db.numberOfRows() == 0) {
            db.insertNotification("TestApp", "Test Title.", "Robert Mazurowski has set this notification");
            db.insertNotification("PoliceApp", "From the Police.", "This is a notification from the police that you need to be there tommorow.");
            db.insertNotification("Pit2017", "Your Pit is still not paid.", "This is a notification you haven't done the Pit for the year yet, don't worry it is not until 2nd May:D");
        }

        //Adding the action listener to the switch
        serviceSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // do something, the isChecked will be
                // true if the switch is in the On position
                if (isChecked == true) {
                    //check if the service is running, and set a TextView to "Running"
                    if (checkServiceState(NotificationListener.class) == false) {
                        if (isNotificationsAccessChecked()) {
                            errorToast("Service not running!");
                        } else {
                            notificationsAccessDialog(true);
                        }
                        serviceSwitch.setChecked(false);
                    } else if (checkIfFound() == false) {
                        errorToast("Application is not found!");
                        serviceSwitch.setChecked(false);
                    } else {
                        appChooser.setEnabled(false);
                        sendAppName();
                        ListenerCommander("StartCollection");
                    }
                } else {
                    //stop the notification collection
                    ListenerCommander("StopCollection");
                    appChooser.setEnabled(true);

                }
            }
        });


        btnNotifications.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, NotificationsActivity.class);
                startActivity(intent);
            }
        });

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private Boolean checkIfFound() {
        if (APPNAME.equals("All")) {
            setNone();
            return true;
        }
        PackageManager pm = this.getApplicationContext().getPackageManager();
        List<ApplicationInfo> appsList = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        for (ApplicationInfo ai : appsList) {
            String n = (String) pm.getApplicationLabel(ai);
            if (n.contains(APPNAME) || APPNAME.contains(n)) {
                PACKAGENAME = ai.packageName;
                packageField.setText(PACKAGENAME);
                return true;
            }
        }
        setNone();
        packageField.setText(PACKAGENAME);
        return false;
    }

    //the below method gets list of names of all aplications to put in the ArrayAdapter for the appChooser field
    private String[] getAppNames() {
        PackageManager pm = this.getApplicationContext().getPackageManager();
        List<ApplicationInfo> appsList = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        ArrayList<String> appNames = new ArrayList<String>();
        appNames.add("All");
        for (ApplicationInfo ai : appsList) {
            String n = (String) pm.getApplicationLabel(ai);
            appNames.add(n);
        }
        //Due to a bug in android 4 the below makes: java.lang.ClassCastException: java.lang.Object[] cannot be cast to java.lang.String[]
        //I left in as this is the proper code
        String[] newArray = appNames.toArray(new String[0]);
        return newArray;

        //WorkArround

    }

    private void setFound(Boolean isFound) {
        if (isFound == true) {
            foundField.setText("Found");
        } else {
            foundField.setText("Not Found");
        }
    }

    private void setState(Boolean isRunning) {
        if (isRunning == true) {
            stateField.setText("Running");
        } else {
            stateField.setText("Stopped");
        }
    }

    private boolean checkServiceState(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                appChooser.setEnabled(false);
                return true;
            }
        }
        appChooser.setEnabled(true);
        return false;
    }

    private void ListenerCommander(String command) {
        Intent i = new Intent("com.example.werfish.NOTIFICATION_LISTENER_COMMANDER");
        //It can Be StartCollection or StopCollection
        i.putExtra("command", command);
        sendBroadcast(i);
    }

    private void sendAppName() {
        Intent i = new Intent("com.example.werfish.NOTIFICATION_LISTENER_COMMANDER");
        i.putExtra("command", "AppUpdate");
        i.putExtra("appname", APPNAME);
        i.putExtra("packagename", PACKAGENAME);
        sendBroadcast(i);
    }

    private void setAll() {
        PACKAGENAME = "All";
        APPNAME = "All";
        sendAppName();
    }

    private void setNone() {
        PACKAGENAME = "None";
        APPNAME = "None";
        sendAppName();
    }

    private void errorToast(String error) {
        Toast.makeText(MainActivity.this, error, Toast.LENGTH_SHORT).show();
    }

    private void notificationsAccessDialog(Boolean isSet) {
        if (!isSet) {
            AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(MainActivity.this);
            myAlertDialog.setTitle("Enable Notifications Access!");
            myAlertDialog.setMessage("The notification access is not enabled for this app. The notification capture will not work/n" +
                    "if this setting is not set. Would you like to continue to enable this setting?");
            myAlertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface arg0, int arg1) {
                    // do something when the OK button is clicked
                    //take user to the setting screen
                    Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
                    startActivity(intent);
                }
            });
            myAlertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface arg0, int arg1) {
                    // do something when the Cancel button is clicked
                }
            });
            myAlertDialog.show();
        }
    }

    private Boolean isNotificationsAccessChecked() {
        if (Settings.Secure.getString(this.getContentResolver(), "enabled_notification_listeners").contains(getApplicationContext().getPackageName())) {
            //if notifications access is enabled return true
            return true;
        } else {
            //notifications access is not enabled return false
            return false;
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.example.werfish.ingcatchertest/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.example.werfish.ingcatchertest/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
}
