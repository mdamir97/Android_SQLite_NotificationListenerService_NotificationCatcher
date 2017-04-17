package com.example.werfish.ingcatchertest;

import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class DisplayNotification extends AppCompatActivity {


    private Database db;

    TextView id ;
    TextView appname;
    TextView title;
    TextView content;
    TextView timestamp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_notification);

        //Make the query to the db
        db = new Database(this);

        id = (TextView) findViewById(R.id.Text_ID);
        appname = (TextView) findViewById(R.id.Text_AppName);
        title = (TextView) findViewById(R.id.Text_Title);
        content = (TextView) findViewById(R.id.Text_Content);
        timestamp = (TextView) findViewById(R.id.Text_Timestamp);

        Bundle extras = getIntent().getExtras();
        if(extras !=null){
            int Value = extras.getInt("id");

            Cursor rs = db.getData(Value);
            rs.moveToFirst();

            id.setText(rs.getString(rs.getColumnIndex(Database.INGTEST_COLUMN_NOTIFICATION_ID)));
            appname.setText(rs.getString(rs.getColumnIndex(Database.INGTEST_COLUMN_NOTIFICATION_APPNAME)));
            title.setText(rs.getString(rs.getColumnIndex(Database.INGTEST_COLUMN_NOTIFICATION_TITLE)));
            content.setText(rs.getString(rs.getColumnIndex(Database.INGTEST_COLUMN_NOTIFICATION_TEXT)));
            timestamp.setText(rs.getString(rs.getColumnIndex(Database.INGTEST_COLUMN_TIMESTAMP)));

            if (!rs.isClosed())  {
                rs.close();
            }
        }

    }
}
