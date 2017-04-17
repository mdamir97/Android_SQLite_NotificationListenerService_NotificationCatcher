package com.example.werfish.ingcatchertest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.widget.Toast;

import java.util.List;

/**
 * Created by Werfish on 13.04.2017.
 */
public class NotificationListener extends NotificationListenerService {
    private CatcherServiceReceiver receiver;

    Database db;
    String APPNAME;
    String PACKAGENAME;
    Boolean captureSwitch;
    @Override
    public void onCreate() {
        super.onCreate();
        CatcherServiceReceiver receiver = new CatcherServiceReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.example.werfish.NOTIFICATION_LISTENER_COMMANDER");
        registerReceiver(receiver,filter);
        captureSwitch = false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        if(captureSwitch = true) {
            if (PACKAGENAME.equals("None")) {
                insertNotification(sbn);
            }else if (sbn.getPackageName().equals(PACKAGENAME)){
                insertNotification(sbn);
            }
        }
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {

    }

    private void insertNotification(StatusBarNotification sbn){
        db = new Database(this);

        //get all the notification data
        //String ticker = sbn.getNotification().tickerText.toString();
        Bundle extras = sbn.getNotification().extras;
        String title = extras.getString("android.title");
        String text = extras.getCharSequence("android.text").toString();

        //get the aplication Name
        String packageName = sbn.getPackageName();
        String appName = getAppName(packageName);


        db.insertNotification(appName,title,text);
        db.close();
        Toast.makeText(this, "Notification Saved", Toast.LENGTH_LONG).show();
    }

    private String getAppName(String packageName){
        PackageManager pm = getApplicationContext().getPackageManager();
        ApplicationInfo ai;
        try {
            ai = pm.getApplicationInfo( packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            ai = null;
        }
        return (String) (ai != null ? pm.getApplicationLabel(ai) : "(unknown)");
    }

    class CatcherServiceReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getStringExtra("command").contains("Collection")){
                if(intent.getStringExtra("command").equals("StartCollection")){
                    captureSwitch = true;
                }else if(intent.getStringExtra("command").equals("StopCollection")){
                    captureSwitch = false;
                }
            }else if(!intent.getStringExtra("command").equals("AppUpdate")){
                APPNAME = intent.getStringExtra("appname");
                PACKAGENAME = intent.getStringExtra("packagename");
            }

        }

    }
}
