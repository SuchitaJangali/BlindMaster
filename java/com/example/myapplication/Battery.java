package com.example.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class Battery extends AppCompatActivity {
TextView battery;
TextToSpeech textToSpeech;
float x1,y1,x2,y2;
private BroadcastReceiver batteryLevelReceiver=new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
  int level =intent.getIntExtra(BatteryManager.EXTRA_LEVEL,0);
  battery.setText(String.valueOf(level)+"%");
    }
};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_battery);
        battery=findViewById(R.id.battery);
this.registerReceiver(this.batteryLevelReceiver,new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

        //Start of app using voice
        textToSpeech = new TextToSpeech(this, status -> {

            if (status == TextToSpeech.SUCCESS) {
                textToSpeech.setLanguage(Locale.US);
                textToSpeech.setSpeechRate(1f);
                textToSpeech.speak("your battery Percentage is  "+battery.getText() , TextToSpeech.QUEUE_ADD, null);
            }
        });
        Speak("to repeat touch for new command right swap");
        }
    public boolean onTouchEvent(MotionEvent event) {
        try {
            // Handle touch events here
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    // Action when finger touches the screen
                    x1 = event.getX();
                    y1 = event.getY();
                    break;
                case MotionEvent.ACTION_UP:
                    // Action when finger is lifted from the screen
                    x2 = event.getX();
                    y2 = event.getY();
                    if (x1 == x2) {
                        // geting input  to say user name and password
                     Speak("your battery Percentage is"+battery.getText());}
                    //  Left swap
                    if (x1 < x2) {
                        //Lgo to Login page
                        Intent intent = new Intent(Battery.this, HelpingPage.class);
                        startActivity(intent);

                    }
                    if (x1 > x2) {
                        //Voice command
                        Intent intent1 = new Intent(Battery.this, CommandPage.class);

                        startActivity(intent1);
                    }
                    break;

            }
        }
        catch (Exception e)
        {
            Log.i("Touch","error");
        }
        // Return 'true' to indicate that the touch event has been consumed
        return true;
    }
    void Speak(String s){
        textToSpeech.speak(s,TextToSpeech.QUEUE_FLUSH,null);

    }
}