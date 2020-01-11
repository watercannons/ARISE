package com.example.alarmproject;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.view.View;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.TextView;

import java.util.Random;
import java.util.Calendar;
import java.util.concurrent.ThreadLocalRandom;

public class MainActivity extends AppCompatActivity
{
    TimePicker alarmTimePicker;
    TimePicker alarmTimePicker2;
    TextView text;
    PendingIntent pendingIntent;
    AlarmManager alarmManager;
    Random rand;
    RingtonePointer ringtonePointer = new RingtonePointer();

    public int getTimeCode(int h, int m){
        return h*60 + m;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        alarmTimePicker = (TimePicker) findViewById(R.id.timePicker);
        alarmTimePicker2 = (TimePicker) findViewById(R.id.timePicker2);
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        text = (TextView) findViewById(R.id.textView);
    }
    public void OnToggleClicked(View view)
    {
        long time;

        if (((ToggleButton) view).isChecked()) {
            Toast.makeText(MainActivity.this, "ALARM ON", Toast.LENGTH_SHORT).show();
            Calendar calendar = Calendar.getInstance();

            int startH = alarmTimePicker.getCurrentHour();      //both ints
            int startM = alarmTimePicker.getCurrentMinute();
            int endH = alarmTimePicker2.getCurrentHour();
            int endM = alarmTimePicker2.getCurrentMinute();

            int startTimeCode = getTimeCode(startH, startM);
            int endTimeCode = getTimeCode(endH,endM);

            if(endTimeCode < startTimeCode) endTimeCode += 1440; //add a day if the endTime < startTime

            Random rand = new Random();
            int rnd = rand.nextInt(endTimeCode-startTimeCode) + startTimeCode;


            //int randomNum = ThreadLocalRandom.current().nextInt(min, max + 1);
            int randomH = rnd/60;
            int randomM = rnd%60;

            if(randomH > 23) randomH-=24; //in case when the hours was > 24, subtract a day

            text.setText("Hour: " + randomH + " Minute: " + randomM);

            //calendar.set(Calendar.HOUR_OF_DAY, alarmTimePicker.getCurrentHour());
            //calendar.set(Calendar.MINUTE, alarmTimePicker.getCurrentMinute());

            calendar.set(Calendar.HOUR_OF_DAY, randomH);
            calendar.set(Calendar.MINUTE, randomM);

            Intent intent = new Intent(this, AlarmReceiver.class);
            pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);

            time=(calendar.getTimeInMillis()-(calendar.getTimeInMillis()%60000));
            if(System.currentTimeMillis()>time)
            {
                if (calendar.AM_PM == 0)
                    time = time + (1000*60*60*12);
                else
                    time = time + (1000*60*60*24);
            }
            alarmManager.set(AlarmManager.RTC_WAKEUP, time, pendingIntent);
        }
        else {
            alarmManager.cancel(pendingIntent);

            if (ringtonePointer.playing) {
                ringtonePointer.stop();
                Toast.makeText(MainActivity.this, "Good morning!", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(MainActivity.this, "ALARM OFF", Toast.LENGTH_SHORT).show();
            }
        }
    }
}