package com.example.alarmproject;

import android.media.Ringtone;
import android.widget.Toast;

public class RingtonePointer {

    public static Ringtone ringtone = null;


    public static boolean playing = false;

    public void play() {
        ringtone.play();
        playing = true;
    }

    public void stop() {
//        Toast.makeText(context, "Good Morning!", Toast.LENGTH_LONG).show();
        ringtone.stop();
        playing = false;
    }
}