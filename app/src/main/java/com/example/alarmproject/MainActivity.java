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

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Random;
import java.util.Calendar;
import java.util.concurrent.ThreadLocalRandom;

public class MainActivity extends AppCompatActivity {
    private static final String FILE_NAME = "alarms.arise";
    TimePicker alarmTimePicker;
    TimePicker alarmTimePicker2;
    TextView text;
    PendingIntent pendingIntent;
    AlarmManager alarmManager;
    RingtonePointer ringtonePointer = new RingtonePointer();

    public int getID(int start_time, int end_time) {return (int)(Math.random() * (7*(start_time+123) + 3*(end_time+456)));}

    public int getTimeCode(int h, int m){return h*60 + m;}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        alarmTimePicker = (TimePicker) findViewById(R.id.timePicker);
        alarmTimePicker2 = (TimePicker) findViewById(R.id.timePicker2);
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        text = (TextView) findViewById(R.id.textView);
    }
    public void OnToggleClicked(View view) {
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
            int rnd = rand.nextInt(endTimeCode-startTimeCode+1) + startTimeCode;


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

            save(startTimeCode, endTimeCode);

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

    private void save(int start_time, int end_time) {

        String new_line = getID(start_time,end_time) + ":" +start_time + ":" + end_time + "\n";
        String new_file = "";
        FileOutputStream FOS = null;
        FileInputStream FIS = null;

        try {
            FIS = openFileInput(FILE_NAME);
//            Toast.makeText(this, "opened",  Toast.LENGTH_LONG).show();
            InputStreamReader ISR = new InputStreamReader(FIS);
            BufferedReader BR = new BufferedReader(ISR);
            StringBuilder SB = new StringBuilder();
            String FIS_in;

//            Toast.makeText(this, "almost read",  Toast.LENGTH_LONG).show();

            while ((FIS_in = BR.readLine()) != null) {
                SB.append(FIS_in).append("\n");
            }

            new_file = SB.toString();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (FIS != null) {
                try {FIS.close();} catch (IOException e) {e.printStackTrace();}
            }
        }

        new_file += new_line;

        try {
            FOS = openFileOutput(FILE_NAME, MODE_PRIVATE);
            FOS.write(new_file.getBytes());

//            Toast.makeText(this, "Saved to " + getFilesDir() + "/" + FILE_NAME,  Toast.LENGTH_LONG).show();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (FOS != null) {
                try {FOS.close();} catch (IOException e) {e.printStackTrace();}
            }
        }


    }


}