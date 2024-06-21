package com.example.myapplication;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
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

public class CommandPage extends AppCompatActivity {
    MediaRecorder mediaRecorder;
    TextToSpeech textToSpeech;
    float x1,y1,x2,y2;
    int touch;
    String username;
   boolean isRecording;
    private static final int SPEECH_REQUEST_CODE = 0;
SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_command_page);
        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

        //Start of app using voice
        textToSpeech = new TextToSpeech(this, status -> {

            if (status == TextToSpeech.SUCCESS) {
                textToSpeech.setLanguage(Locale.US);
                textToSpeech.setSpeechRate(1f);
                textToSpeech.speak(" Command Please   ", TextToSpeech.QUEUE_FLUSH, null);

            }
        });
        SpeechRecognizer("");

    }
    //Touch Sensor

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
                        touch++; if(touch > 1) {
                            SpeechRecognizer("example say username suchita");
                        }


                    }
                    if (x1 < x2) {
                        //Lgo to Login page
                        Intent intent = new Intent(CommandPage.this, Register.class);
                        startActivity(intent);

                    }
                    if (x1 > x2) {
                        //Voice command
                        Intent intent1 = new Intent(CommandPage.this, CommandPage.class);

                        startActivity(intent1);
                    }
                    break;

            }
        }catch (Exception e)
        {
            SpeechRecognizer("Say username and password onces agin");
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
        if (requestCode == SPEECH_REQUEST_CODE && resultCode == RESULT_OK) {
            List<String> results = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS);
            assert results != null;
            String spokenText = results.get(0);
            textToSpeech.speak(spokenText,TextToSpeech.QUEUE_FLUSH,null);

            if(spokenText.equals("read" ))
            {
               // Toast.makeText(this, "click face", Toast.LENGTH_SHORT).show();
                // Check if the user is logged in
                if (!isLoggedIn()) {
                    Toast.makeText(getApplicationContext(),username,Toast.LENGTH_LONG).show();
                    // If not logged in, redirect to the login activity
                    Intent intent=new Intent(CommandPage.this,Read.class);
                    startActivity(intent);
                    finish();
                }
                else{
                    textToSpeech.speak("please login ",TextToSpeech.QUEUE_FLUSH,null);
                SpeechRecognizer(" if want then say login");

                }

                }else if (spokenText.equalsIgnoreCase("date and time")) {

                Intent intent=new Intent(CommandPage.this,Date_time.class);
                startActivity(intent);
                finish();
            }
            else if(spokenText.equalsIgnoreCase("login"))
                {
                    Intent intent=new Intent(CommandPage.this,Login.class);
                    startActivity(intent);
                    finish();
                }else if(spokenText.equalsIgnoreCase("register"))
            {
                Intent intent=new Intent(CommandPage.this,Register.class);
                startActivity(intent);
                finish();
            }else if(spokenText.equalsIgnoreCase("Home")){
                Intent intent=new Intent(CommandPage.this,FirstPage.class);
                startActivity(intent);
                finish();
            } else if (spokenText.equalsIgnoreCase("Battery percentage")) {

                Intent intent=new Intent(CommandPage.this,Battery.class);
                startActivity(intent);
                finish();
            }
            else if (spokenText.equalsIgnoreCase("calculator")) {

                Intent intent=new Intent(CommandPage.this,Calculator.class);
                startActivity(intent);
                finish();
            }
            else if (spokenText.contains("audio")) {

                Intent intent=new Intent(CommandPage.this,Audio_record.class);
                startActivity(intent);
                finish();
            }

            else if (spokenText.contains("location")) {

                Intent intent=new Intent(CommandPage.this,Location.class);
                startActivity(intent);
                finish();
            }
            else if (spokenText.toString().contains("exit")) {
              //  mVoiceInputTv.setText(null);
                finishAffinity();
            } else {
                SpeechRecognizer("Say again");

            }

            // Do something with spokenText.
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    private boolean isLoggedIn() {
        // Retrieve the login status from SharedPreferences
        username=sharedPreferences.getString("username","log");
        return sharedPreferences.getBoolean("isLoggedIn", true);
    }

    //recording
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            // Handle volume up button released

            // Add your logic here
            try {

                VibrationHelper.vibrate(getApplicationContext(), 1000); // Vibrate for 1 second

                mediaRecorder =new MediaRecorder();
                mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                mediaRecorder.setOutputFile(getRecoFile());

                mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                mediaRecorder.prepare();
                mediaRecorder.start();
                isRecording=true;
            } catch (IOException e) {
                e.printStackTrace();        }

            return true; // Return true to indicate that the event has been handled
        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            // Handle volume down button released
            // Add your logic here
if(isRecording) {
    VibrationHelper.vibrate(getApplicationContext(), 1000); // Vibrate for 1 second

    mediaRecorder.stop();
    mediaRecorder.release();
    mediaRecorder = null;
    isRecording = false;
} else{
    textToSpeech.speak("recoding is alredy stop",TextToSpeech.QUEUE_FLUSH,null);
}
            return true; // Return true to indicate that the event has been handled
        }

        // Let the system handle other key events
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

}