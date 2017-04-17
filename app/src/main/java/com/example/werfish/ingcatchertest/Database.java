package com.example.werfish.ingcatchertest;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

/**
 * Created by Werfish on 12.04.2017.
 */
public class Database extends SQLiteOpenHelper{
    public static final String DATABASE_NAME ="IngTest.db";
    public static final String INGTEST_TABLE_NAME = "Notifications";
    public static final String INGTEST_COLUMN_NOTIFICATION_ID = "Notif_ID";
    public static final String INGTEST_COLUMN_NOTIFICATION_APPNAME = "App_Name";
    public static final String INGTEST_COLUMN_NOTIFICATION_TITLE = "Notif_Title";
    public static final String INGTEST_COLUMN_NOTIFICATION_TEXT = "Notif_Text";
    public static final String INGTEST_COLUMN_TIMESTAMP = "Notif_Timestamp";


    public Database(Context context) {
        super(context, DATABASE_NAME , null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL(
                "CREATE TABLE IF NOT EXISTS Notifications (Notif_ID Integer Primary Key,App_Name VARCHAR,Notif_Title VARCHAR,Notif_Text VARCHAR, Notif_Timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS Notifications");
        onCreate(db);
    }

    public void destroy()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS Notifications");
        onCreate(db);
    }

    public boolean insertNotification(String appName,String title, String text) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(INGTEST_COLUMN_NOTIFICATION_APPNAME, appName);
        contentValues.put(INGTEST_COLUMN_NOTIFICATION_TITLE, title);
        contentValues.put(INGTEST_COLUMN_NOTIFICATION_TEXT, text);
        db.insert("Notifications", null, contentValues);
        return true;
    }

    public Cursor getData(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor record =  db.rawQuery( "select * from Notifications where Notif_ID="+id+"", null );
        return record;
    }

    public int numberOfRows(){
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, INGTEST_TABLE_NAME);
        return numRows;
    }

    public ArrayList<String> getAllNotifications() {
        ArrayList<String> array_list = new ArrayList<String>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor =  db.rawQuery( "select * from Notifications;", null );
        cursor.moveToFirst();

        while(!cursor.isAfterLast()){
            array_list.add(cursor.getString(cursor.getColumnIndex(INGTEST_COLUMN_NOTIFICATION_TITLE)));
            cursor.moveToNext();
        }
        return array_list;
    }

    public ArrayList<String> getNotificationsByAppName(String appName) {
        ArrayList<String> array_list = new ArrayList<String>();

        SQLiteDatabase db = this.getReadableDatabase();
        String table = "Notifications";
        String selection = "App_Name =?";
        String[] selectionArgs = { appName }; // matched to "?" in selection
        Cursor cursor = db.query(table, null, selection, selectionArgs, null, null, null);
        cursor.moveToFirst();

        while(!cursor.isAfterLast()){
            array_list.add(cursor.getString(cursor.getColumnIndex(INGTEST_COLUMN_NOTIFICATION_TITLE)));
            cursor.moveToNext();
        }
        return array_list;
    }

    public void reset(){
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL("DELETE FROM Notifications;");
    }
}
