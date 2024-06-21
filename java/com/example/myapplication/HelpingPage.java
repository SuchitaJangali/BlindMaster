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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.List;
import java.util.Locale;

public class HelpingPage extends AppCompatActivity {
    TextToSpeech textToSpeech;
    float x1,x2,y1,y2;
    String main = "";
    TextView textView;
    JSONObject jsonObject;
int presskey=0;
    private static final int SPEECH_REQUEST_CODE = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_helping_page);
        textView = findViewById(R.id.dis);
        //Start of app using voice
        textToSpeech = new TextToSpeech(this, status -> {

            if (status == TextToSpeech.SUCCESS) {
                textToSpeech.setLanguage(new Locale("en", "IN"));
                textToSpeech.setSpeechRate(1f);
                textToSpeech.speak("welcome to help page  ", TextToSpeech.QUEUE_FLUSH, null);
                textToSpeech.speak("if you want to read all features the press volume up key` ", TextToSpeech.QUEUE_ADD, null);
            }
        });
        String jsonString = String.valueOf(loadJSONFromAsset());
        try {
            jsonObject = new JSONObject(jsonString);
//            Toast.makeText(getApplicationContext(),jsonObject.length(),Toast.LENGTH_LONG).show();
            boolean f= true;
            for(int i=1;i<=jsonObject.length();i++) {
                f=true;
               JSONObject j= (JSONObject) jsonObject.get(String.valueOf(i));
                while(f){
                    textToSpeech.setSpeechRate(0.5f);

                    main+= j.getString("key")+":  "+j.getString("dis");
                   f=false;
                }
                main+="\n";
            }
            textView.setText(main);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

    }

    public JSONObject loadJSONFromAsset() {
        JSONObject jsonObject = null;
        try {
            InputStream is = getAssets().open("help.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            jsonObject = new JSONObject(new String(buffer, "UTF-8"));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }


    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            presskey+=1;
            String a = String.valueOf(textView.getText());
                    try {
                    JSONObject j= (JSONObject) jsonObject.get(String.valueOf(presskey));
                    textToSpeech.speak(j.getString("key")+":  "+j.getString("dis"),TextToSpeech.QUEUE_ADD,null);

                } catch (JSONException e) {

                    e.printStackTrace();                }



        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {

        }
        return super.onKeyUp(keyCode, event);


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
                try {


                    if (x1 == x2) {
                        //Toast.makeText(getApplicationContext(),"Touch",Toast.LENGTH_SHORT).show();
                        //go to Login page
                        Intent intent = new Intent(HelpingPage.this, Login.class);
                        Toast.makeText(getApplicationContext(), "Touch", Toast.LENGTH_LONG).show();
                        startActivity(intent);
                        Toast.makeText(getApplicationContext(), "Touch", Toast.LENGTH_LONG).show();

                    }
                }catch (Exception e){
                    System.out.println(e.getMessage());
                }
                if(x1<x2){
                    //helping page
                    Intent intent = new Intent(HelpingPage.this, FirstPage.class);
                    startActivity(intent);
                }
                if(x1>x2){
                    //Voice command
                    Intent intent1 = new Intent(HelpingPage.this, CommandPage.class);

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
            String[] ss = spokenText.split(" ");
        }
    }


}


