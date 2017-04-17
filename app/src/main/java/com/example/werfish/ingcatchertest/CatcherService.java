package com.example.werfish.ingcatchertest;

import android.app.Service;
import android.content.Intent;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.IBinder;
import android.widget.Toast;

public class CatcherService extends Service {
    Database db;

    public CatcherService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Let it continue running until it is stopped.
        Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();
        db = new Database(this);
        //Pseudocode
        //getNotificationContent and  put it to content_string
        //getNotificationTitle and put it to title_string
        //db.insertNotification(title_string,content_string,)

        //TEST COMMIT COMMENT

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();
    }
}
