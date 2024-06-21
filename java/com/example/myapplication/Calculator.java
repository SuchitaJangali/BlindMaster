package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class Calculator extends AppCompatActivity {
TextToSpeech textToSpeech;
TextView text;
    private static final int SPEECH_REQUEST_CODE = 0;
Boolean kup=false,kdown=false;
    double reslut=0;
    float x1,x2,y1,y2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator);
        text=findViewById(R.id.textView3);
        //Start of app using voice
        textToSpeech = new TextToSpeech(this, status -> {

            if (status == TextToSpeech.SUCCESS) {
                textToSpeech.setLanguage(Locale.US);
                textToSpeech.setSpeechRate(1f);
                textToSpeech.speak("your in  calculator", TextToSpeech.QUEUE_ADD, null);
            textToSpeech.speak("volume key up for new calculation  key down for operation on old anser. touch to repeat ",TextToSpeech.QUEUE_ADD,null);
            }
        });
                Speak("");


    }

    private void Speak(String s){
        textToSpeech.speak(s,TextToSpeech.QUEUE_ADD,null);
    }
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            // Handle volume up button released
            SpeechRecognizer("start with  number");
kup=true;
kdown=false;            // Add your logic here



            return true; // Return true to indicate that the event has been handled
        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            // Handle volume down button released
            // Add your logic here
            SpeechRecognizer("Start with number");
            kup=false;
            kdown=true;
            return true; // Return true to indicate that the event has been handled
        }

        // Let the system handle other key events
        return super.onKeyUp(keyCode, event);
    }

    // Create an intent that can start the Speech Recognizer activity
    private void SpeechRecognizer(String s) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        textToSpeech.speak(s, TextToSpeech.QUEUE_FLUSH,null);
// This starts the activity and populates the intent with the speech text.
        startActivityForResult(intent, SPEECH_REQUEST_CODE);
    }
    // This callback is invoked when the Speech Recognizer returns.
// This is where you process the intent and extract the speech text from the intent.
    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SPEECH_REQUEST_CODE && resultCode == RESULT_OK) {
            List<String> results = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS);
            assert results != null;
            String spokenText = results.get(0);
            String[] ss=spokenText.split(" ");
            if(ss[0].equalsIgnoreCase("number")) {
                //textToSpeech.speak(spokenText, TextToSpeech.QUEUE_FLUSH, null);
                String[] k = spokenText.split(" ");
                for (int i = 0; i<k.length; i++) {

                    if (ss[i].equalsIgnoreCase("one")) {
                        ss[i] = "1";
                    }
                     if (ss[i].equalsIgnoreCase("multiply")) {
                        ss[i] = "*";
                    }
                     if (ss[i].equalsIgnoreCase("into")) {
                        ss[i] = "*";

                    }
                     if (ss[i].equalsIgnoreCase("divide")) {
                        ss[i] = "/";
                    }
                }
               String h= Arrays.toString(ss);
                h=h.replace(","," ");
                h=h.replace("["," ");

                h=h.replace("]"," ");
                cal c=new cal();
         if(kup){
                 reslut=c.calculation(h.substring(7));
                Toast.makeText(getApplicationContext(), String.valueOf(reslut), Toast.LENGTH_SHORT).show();
                Speak("result is "+String.valueOf(reslut));

             text.setText("result is "+reslut);
            }else if (kdown){
             reslut+=c.calculation(spokenText.substring(7));

             text.setText("result is "+reslut);
             Toast.makeText(getApplicationContext(), String.valueOf(reslut), Toast.LENGTH_SHORT).show();
             Speak("result is "+reslut);

         }
             }else {
            SpeechRecognizer("Say again");}
        }
    }

    //touch evnt
    public boolean onTouchEvent(MotionEvent event) {
        // Handle touch events here
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // Action when finger touches the screen
                x1=event.getX();
                y1=event.getY();
                break;
            case MotionEvent.ACTION_UP:
                // Action when finger is lifted from the screen
                x2=event.getX();
                y2=event.getY();
                if(x1==x2){
                    Speak("result is"+reslut);
                }
                if(x1<x2){
                    //helping page
                    Intent intent = new Intent(Calculator.this, HelpingPage.class);
                    startActivity(intent);
                }
                if(x1>x2){
                    //Voice command
                    Intent intent1 = new Intent(Calculator.this, CommandPage.class);

                    startActivity(intent1);
                }
                break;

        }
        // Return 'true' to indicate that the touch event has been consumed
        return true;
    }
}