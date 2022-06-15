package com.example.termproj;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationBuilderWithBuilderAccessor;
import androidx.core.app.NotificationCompat;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.time.DayOfWeek;
import java.time.Month;
import java.time.Year;

import android.os.Bundle;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Calendar;

class database extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "term_project.db";
    private static final int DATABASE_VERSION = 2;

    public database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE planner ( year TEXT, month TEXT, day TEXT, todo TEXT)");
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS planner" + ";");
        onCreate(db);
    }
    public void onDrop(SQLiteDatabase db) {
        db.execSQL("DROP TABLE planner");
    }
    public void insert(int year, int month, int day, String todo ) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("INSERT INTO planner VALUES ('"+ year + "','"+ month +"','"+ day +"','"+ todo +"')");
    }
};



public class MainActivity extends AppCompatActivity
{
    public String readDay = null;
    public String str = null;
    public CalendarView calendarView;
    public Button del_Btn, save_Btn, btn_alarm;
    public TextView diaryTextView, textView2, textView3;
    public EditText contextEditText;
    database helper;
    SQLiteDatabase db;

    //alarm
    NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        helper = new database(this);
        db = helper.getWritableDatabase();
        helper.onDrop(db);
        helper.onCreate(db);
        String[] from = { "year", "month", "day", "todo" };
        calendarView = findViewById(R.id.calendarView);
        diaryTextView = findViewById(R.id.diaryTextView);
        save_Btn = findViewById(R.id.save_Btn);
        del_Btn = findViewById(R.id.del_Btn);
        textView2 = findViewById(R.id.textView2);
        textView3 = findViewById(R.id.textView3);
        contextEditText = findViewById(R.id.contextEditText);
        btn_alarm = findViewById(R.id.btn_alarm);
        btn_alarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NotificationManager notificationManager = (NotificationManager)MainActivity.this.getSystemService(MainActivity.this.NOTIFICATION_SERVICE);
                Intent intent1 = new Intent(MainActivity.this.getApplicationContext(),MainActivity.class);

                Notification.Builder builder = new Notification.Builder(getApplicationContext());
                intent1.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);

                PendingIntent pendingNotificationIndent = PendingIntent.getActivity(MainActivity.this,0,intent1,PendingIntent.FLAG_UPDATE_CURRENT);

                builder.setSmallIcon(R.drawable.image0).setTicker("HETT").setWhen(System.currentTimeMillis()).setNumber(1).setContentTitle("alart title").setContentText("Push Content").setDefaults(Notification.DEFAULT_SOUND).setContentIntent(pendingNotificationIndent).setAutoCancel(true).setOngoing(true);
                notificationManager.notify(1,builder.build());
            }
        });


        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener()
        {
            @SuppressLint("DefaultLocale")
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth)
            {
                diaryTextView.setVisibility(View.VISIBLE);
                save_Btn.setVisibility(View.VISIBLE);
                contextEditText.setVisibility(View.VISIBLE);
                textView2.setVisibility(View.VISIBLE);
                //cha_Btn.setVisibility(View.INVISIBLE);
                del_Btn.setVisibility(View.INVISIBLE);
                diaryTextView.setText(String.format("%d / %d / %d", year, month + 1, dayOfMonth));
                contextEditText.setText("");
                checkDay(year, month, dayOfMonth , null);

                save_Btn.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        //SQLiteDatabase db = helper.getWritableDatabase();
                        //String sql = null;
                        //db.execSQL();
                        //db.close();
                        Toast.makeText(MainActivity.this,"saved",Toast.LENGTH_SHORT).show();

                        saveDiary(readDay);
                        str = contextEditText.getText().toString();
                        textView2.setText(str);
                        save_Btn.setVisibility(View.INVISIBLE);
                        //cha_Btn.setVisibility(View.VISIBLE);
                        del_Btn.setVisibility(View.VISIBLE);
                        contextEditText.setVisibility(View.INVISIBLE);
                        textView2.setVisibility(View.VISIBLE);
                        helper.insert(year , month+1 , dayOfMonth , str);
                    }
                });
                btn_alarm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //호출
                    }
                });
            }

        });

    }
    public void sendNotification(View view){
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this , "my_channel_id_01");

        Intent intent = new Intent (Intent.ACTION_VIEW);
        PendingIntent pendingIntent= PendingIntent.getActivity(this, 0 , intent , 0);

        notificationBuilder.setSmallIcon(R.drawable.image0).setContentTitle("mail notification").setContentText("new mail here").setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, notificationBuilder.build());
    }

    public void checkDay(int cYear, int cMonth, int cDay , String todo)
    {
        String Year = String.valueOf(cYear);
        String Month = String.valueOf(cMonth);
        String Day = String.valueOf(cDay);

        Cursor cursor = db.rawQuery("SELECT todo FROM planner WHERE year = ? AND month = ? AND day = ?;", new String[]{Year, Month, Day});

        if(cursor != null) {
            if(cursor.moveToFirst()) {
                do {
                    @SuppressLint("Range")
                    String str = cursor.getString(cursor.getColumnIndex("TODO"));
                }while(cursor.moveToNext());
            }
        }
        //str = new String(cursor);
        contextEditText.setVisibility(View.INVISIBLE);
        textView2.setVisibility(View.VISIBLE);
        textView2.setText(str);
        save_Btn.setVisibility(View.INVISIBLE);
        //cha_Btn.setVisibility(View.VISIBLE);
        del_Btn.setVisibility(View.VISIBLE);

        del_Btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                textView2.setVisibility(View.INVISIBLE);
                contextEditText.setText("");
                contextEditText.setVisibility(View.VISIBLE);
                save_Btn.setVisibility(View.VISIBLE);
                //cha_Btn.setVisibility(View.INVISIBLE);
                del_Btn.setVisibility(View.INVISIBLE);
                //emoveDiary(readDay);
            }
        });
        if (textView2.getText() == null)
        {
            textView2.setVisibility(View.INVISIBLE);
            diaryTextView.setVisibility(View.VISIBLE);
            save_Btn.setVisibility(View.VISIBLE);
            //cha_Btn.setVisibility(View.INVISIBLE);
            del_Btn.setVisibility(View.INVISIBLE);
            contextEditText.setVisibility(View.VISIBLE);
        }
    }

    @SuppressLint("WrongConstant")
    public void removeDiary(String readDay)
    {
        Toast.makeText(MainActivity.this, "removed from DB", Toast.LENGTH_SHORT).show();
    }

    @SuppressLint("WrongConstant")
    public void saveDiary(String readDay)
    {
        Toast.makeText(MainActivity.this, "Saved in DB", Toast.LENGTH_SHORT).show();
    }
}