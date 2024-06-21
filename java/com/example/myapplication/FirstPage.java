package com.example.myapplication;

import android.content.ContextWrapper;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class FirstPage extends AppCompatActivity {

    boolean isRecording=false;
    MediaRecorder mediaRecorder;
    String AudioFilename=null;
    private static final int SPEECH_REQUEST_CODE = 0;

    Audio_record audioRecord=new Audio_record();
    TextToSpeech textToSpeech ;
        float x1,x2,y1,y2;
        @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_page);
        //Start of app using voice
            textToSpeech = new TextToSpeech(this, status -> {

                if (status == TextToSpeech.SUCCESS) {
                textToSpeech.setLanguage(Locale.US);
                textToSpeech.setSpeechRate(1f);
                textToSpeech.speak("Welcome to Blind Friend " +
                        "application  . Swipe Right  to listen the application swipe left to Command  and touch the screen to open Login Page ", TextToSpeech.QUEUE_FLUSH, null);}
        });

    }
//recording
public boolean onKeyUp(int keyCode, KeyEvent event) {
    if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
        // Handle volume up button released

        // Add your logic here
        try {

            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mediaRecorder.setOutputFile(getRecoFile());

            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mediaRecorder.prepare();
            mediaRecorder.start();
            VibrationHelper.vibrate(getApplicationContext(), 1000); // Vibrate for 1 second
isRecording=true;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return true; // Return true to indicate that the event has been handled
    } else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
        // Handle volume down button released
        // Add your logic here

        if (isRecording) {
            VibrationHelper.vibrate(getApplicationContext(), 1000); // Vibrate for 1 second

            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
            isRecording = false;
        } else {
            textToSpeech.speak("recoding is already stop", TextToSpeech.QUEUE_FLUSH, null);
        }
        // Let the system handle other key events
    }
    return super.onKeyUp(keyCode, event);

}

    public   String getRecoFile(){

        ContextWrapper contextWrapper=new ContextWrapper((getApplicationContext()));
        File musicDic=contextWrapper.getExternalFilesDir(Environment.DIRECTORY_MUSIC);
        // Get current date and time as a formatted string
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.getDefault());
        Date currentDateAndTime = new Date();
        String time = dateFormat.format(currentDateAndTime);

        File file =new File(musicDic,"test"+time+".mp3");

        return file.getPath();  }

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
                try {


                    if (x1 == x2) {
                        //Toast.makeText(getApplicationContext(),"Touch",Toast.LENGTH_SHORT).show();
                        //go to Login page
                        Intent intent = new Intent(FirstPage.this, Login.class);
                        Toast.makeText(getApplicationContext(), "Touch", Toast.LENGTH_LONG).show();
                        startActivity(intent);
                        Toast.makeText(getApplicationContext(), "Touch", Toast.LENGTH_LONG).show();

                    }
                }catch (Exception e){
                    System.out.println(e.getMessage());
                }
                if(x1<x2){
                    //helping page
                     Intent intent = new Intent(FirstPage.this, HelpingPage.class);
                    startActivity(intent);
                }
                if(x1>x2){
                    //Voice command
                     Intent intent1 = new Intent(FirstPage.this, CommandPage.class);

                    startActivity(intent1);
                }
                break;

        }
        // Return 'true' to indicate that the touch event has been consumed
        return true;
    }
    // Create an intent that can start the Speech Recognizer activity
    private void SpeechRecognizer(String s) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        textToSpeech.speak(s,TextToSpeech.QUEUE_FLUSH,null);
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
            textToSpeech.speak(spokenText, TextToSpeech.QUEUE_FLUSH, null);
            String[] ss=spokenText.split(" ");
            if(ss[0].equalsIgnoreCase("name") && ss.length==2){
                AudioFilename=ss[1];
            }else {SpeechRecognizer("say it again");}
        Toast.makeText(getApplicationContext(),AudioFilename,Toast.LENGTH_LONG).show();
        }

        }

}