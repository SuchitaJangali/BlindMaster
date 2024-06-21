package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Date_time extends AppCompatActivity {
    TextView date_time;
    TextToSpeech textToSpeech;
    float x1,x2,y1,y2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_time);
        date_time=findViewById(R.id.dt);
        Calendar calendar= Calendar.getInstance();
        String CurrentDate= DateFormat.getDateInstance(DateFormat.FULL).format(calendar.getTime());
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        Date currentDateAndTime = new Date();
        String time = dateFormat.format(currentDateAndTime);


        date_time.setText(String.format("%s and time is %s", CurrentDate, time));
        //Start of app using voice
        textToSpeech = new TextToSpeech(this, status -> {

            if (status == TextToSpeech.SUCCESS) {
                textToSpeech.setLanguage(Locale.US);
                textToSpeech.setSpeechRate(1f);
                textToSpeech.speak("Cuurent  Date is  "+date_time.getText() , TextToSpeech.QUEUE_ADD, null);
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
                        Speak("Current Date  is"+date_time.getText());}
                    //Left swap
                    if (x1 < x2) {
                        //Lgo to Login page
                        Intent intent = new Intent(Date_time.this, FirstPage.class);
                        startActivity(intent);

                    }
                    if (x1 > x2) {
                        //Voice command
                        Intent intent1 = new Intent(Date_time.this, CommandPage.class);

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