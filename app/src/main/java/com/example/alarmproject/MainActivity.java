package com.example.alarmproject;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
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
    private static String _FILE_;
    TimePicker alarmTimePicker;
    TimePicker alarmTimePicker2;
    TextView text;
    PendingIntent pendingIntent;
    AlarmManager alarmManager;
    RingtonePointer ringtonePointer = new RingtonePointer();

    public int getID(int start_time, int end_time) {return (int)(Math.random() * (7*(start_time+123) + 3*(end_time+456)));}

    public int getTimeCode(int h, int m) {return h*60 + m;}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        alarmTimePicker = (TimePicker) findViewById(R.id.timePicker);
        alarmTimePicker2 = (TimePicker) findViewById(R.id.timePicker2);
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        text = (TextView) findViewById(R.id.textView);
        Toast.makeText(this, "hello",  Toast.LENGTH_LONG).show();
        load();
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

            add(startTimeCode, endTimeCode);

            disable(1670);

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



    private void load() {
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

            _FILE_ = SB.toString();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (FIS != null) {
                try {FIS.close();} catch (IOException e) {e.printStackTrace();}
            }
        }
    }
    public void delete(int ID) {

        if (_FILE_.indexOf(ID+"") == -1) return;

        String new_file = "";
        FileOutputStream FOS = null;


        new_file = _FILE_.substring(0,_FILE_.indexOf(ID+"")) + _FILE_.substring( _FILE_.indexOf(ID+"") + _FILE_.substring(_FILE_.indexOf(ID+"")).indexOf('\n') + 1);


        try {
            FOS = openFileOutput(FILE_NAME, MODE_PRIVATE);
            FOS.write(new_file.getBytes());
            _FILE_ = new_file;

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
    public void add(int start_time, int end_time) {

        String new_line = getID(start_time,end_time) + ":" +start_time + ":" + end_time + ":" + "T" + "\n";
        String new_file = "";
        FileOutputStream FOS = null;

        new_file = _FILE_ + new_line;



        try {
            FOS = openFileOutput(FILE_NAME, MODE_PRIVATE);
            FOS.write(new_file.getBytes());
            _FILE_ = new_file;


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (FOS != null) {
                try {FOS.close();} catch (IOException e) {e.printStackTrace();}
            }
        }

        Toast.makeText(this, "save",  Toast.LENGTH_LONG).show();


    }

    public int get_enabled(int ID) {
        if (_FILE_.indexOf(ID+"") == -1) return -1;
        else if (_FILE_.charAt(_FILE_.indexOf(ID+"") + _FILE_.substring(_FILE_.indexOf(ID+"")).indexOf('\n')-1) == 'T')
            return 1;
        else
            return 0;
    }
    public void enable(int ID) {
        if (_FILE_.indexOf(ID+"") == -1) return;

        String new_file = "";
        FileOutputStream FOS = null;


        new_file = _FILE_.substring(0,_FILE_.indexOf(ID+"") +  _FILE_.substring(_FILE_.indexOf(ID+"")).indexOf('\n')-1 ) + "T" +
                _FILE_.substring(_FILE_.indexOf(ID+"") + _FILE_.substring(_FILE_.indexOf(ID+"")).indexOf('\n'));


        try {
            FOS = openFileOutput(FILE_NAME, MODE_PRIVATE);
            FOS.write(new_file.getBytes());
            _FILE_ = new_file;

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
    public void disable(int ID) {
        if (_FILE_.indexOf(ID+"") == -1) return;

        String new_file = "";
        FileOutputStream FOS = null;


        new_file = _FILE_.substring(0,_FILE_.indexOf(ID+"") +  _FILE_.substring(_FILE_.indexOf(ID+"")).indexOf('\n')-1 ) + "f" +
                _FILE_.substring(_FILE_.indexOf(ID+"") + _FILE_.substring(_FILE_.indexOf(ID+"")).indexOf('\n'));


        try {
            FOS = openFileOutput(FILE_NAME, MODE_PRIVATE);
            FOS.write(new_file.getBytes());
            _FILE_ = new_file;

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

    public int get_start_time(int ID) {
        if (_FILE_.indexOf(ID+"") == -1) return -1;
        String s = _FILE_.substring(_FILE_.indexOf(ID+""));
        s = s.substring(s.indexOf(":")+1);
        s = s.substring(0, s.indexOf(":"));
        return Integer.parseInt(s);
    }
    public int get_end_time(int ID) {
        if (_FILE_.indexOf(ID+"") == -1) return -1;
        String s = _FILE_.substring(_FILE_.indexOf(ID+""));
        s = s.substring(s.indexOf(":")+1);
        s = s.substring(s.indexOf(":")+1);
        s = s.substring(0, s.indexOf(":"));
        return Integer.parseInt(s);
    }
    public void update(int ID, int start_time, int end_time) {

        if (_FILE_.indexOf(ID+"") == -1) return;

        String new_file = "";
        FileOutputStream FOS = null;

        String s1 = _FILE_.substring(0, _FILE_.indexOf(ID+""));
        String s2 = _FILE_.substring( _FILE_.indexOf(ID+"") + _FILE_.substring(_FILE_.indexOf(ID+"")).indexOf('\n')-1 );

        new_file = s1 + ":" + start_time + ":" + end_time + ":" + s2;


        try {
            FOS = openFileOutput(FILE_NAME, MODE_PRIVATE);
            FOS.write(new_file.getBytes());
            _FILE_ = new_file;

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
