package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.model.Database;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Login extends AppCompatActivity {
    float x1, x2, y1, y2;

    int touch = 0,flag=0;
    public TextToSpeech textToSpeech;
    TextView uname, pass;
    String spokenText;
    private static final int SPEECH_REQUEST_CODE = 0;

    // creating a variable for our
    // Firebase Database.
    FirebaseDatabase firebaseDatabase;

    // creating a variable for our Database
    // Reference for Firebase.
    DatabaseReference databaseReference;
    ArrayList<String> name=new ArrayList<>();
    ArrayList<String> password=new ArrayList<>();
    String d="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Start of app using voice
        Toast.makeText(getApplicationContext(), "Ti", Toast.LENGTH_LONG).show();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("users");

        textToSpeech = new TextToSpeech(this, status -> {

            if (status == TextToSpeech.SUCCESS) {
                textToSpeech.setLanguage(Locale.US);
                textToSpeech.setSpeechRate(1f);

                textToSpeech.speak("Swipe left  go back to command and touch to login", TextToSpeech.QUEUE_FLUSH, null);

            }
            textToSpeech.speak("your in login ", TextToSpeech.QUEUE_ADD, null);
            textToSpeech.speak("Swipe right for register ", TextToSpeech.QUEUE_ADD, null);
        });
        uname = (TextView) findViewById(R.id.Euname);
        pass = findViewById(R.id.Epass);

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
                        touch++; if(touch > 1) {
                            SpeechRecognizer("example say username suchita");
                        }


                    }
                    if (x1 < x2) {
                        //Lgo to Login page
                        Intent intent = new Intent(Login.this, Register.class);
                        startActivity(intent);

                    }
                    if (x1 > x2) {
                        //Voice command
                        Intent intent1 = new Intent(Login.this, CommandPage.class);

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
        textToSpeech.speak(s,TextToSpeech.QUEUE_FLUSH,null);
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
// This starts the activity and populates the intent with the speech text.
        startActivityForResult(intent, SPEECH_REQUEST_CODE);
        textToSpeech.speak(s, TextToSpeech.QUEUE_FLUSH, null);
    }

    // This callback is invoked when the Speech Recognizer returns.
// This is where you process the intent and extract the speech text from the intent.
    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        if (requestCode == SPEECH_REQUEST_CODE && resultCode == RESULT_OK) {
            List<String> results = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS);
            spokenText = results.get(0);
            if (touch == 0) {
                //verifcation code
                SpeechRecognizer("say username and password  ");

            }  else if (uname.getText() != null || pass.getText() != null) {
                String[] up = spokenText.split(" ");
                if ("username".equalsIgnoreCase(up[0])) {
                    Toast.makeText(getApplicationContext(), "in else", Toast.LENGTH_LONG).show();
                    try {
                        uname.setText(up[1]);
                        textToSpeech.speak("username has set  as " + spokenText, TextToSpeech.QUEUE_FLUSH, null);
                    } catch (IndexOutOfBoundsException e) {
                        SpeechRecognizer("Say  username name ");
                    }
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // Code to be executed after the delay
                            SpeechRecognizer("say password with same manner");
                        }
                    }, 4000); // 2000 milliseconds (2 seconds)

                } else if ("password".equalsIgnoreCase(up[0])) {
                    try {
                        pass.setText(up[1]);
                        textToSpeech.speak("password has set  as " + spokenText, TextToSpeech.QUEUE_FLUSH, null);

                    } catch (IndexOutOfBoundsException e) {
                        SpeechRecognizer("Say  password   ");
                    }
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // Code to be executed after the delay
                            SpeechRecognizer("say yes  if all are right");
                        }
                    }, 4000); // 2000 milliseconds (2 seconds)

                }

            else if (spokenText.equalsIgnoreCase("yes")) {
                    String s = String.valueOf(uname.getText());
                    String p = String.valueOf(pass.getText());
                    Toast.makeText(getApplicationContext(),"yes",Toast.LENGTH_LONG).show();
                    RetriveData();
                    LoginUser(s, p);
            }
            }else {
                // verfify the user name and password

                SpeechRecognizer("say it again ");
            }

            flag = 1;
            super.onActivityResult(requestCode, resultCode, data);


        }
    }

    private void LoginUser(String s, String p) {

        for(int i=0;i< name.size();i++)
         {
             if(name.get(i).equalsIgnoreCase(s) && password.get(i).equalsIgnoreCase(p))
             {
                 SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                 textToSpeech.speak("you are Login ",TextToSpeech.QUEUE_FLUSH,null);
               //for checking the login
                 SharedPreferences.Editor editor = sharedPreferences.edit();
                 editor.putBoolean("isLoggedIn", false);
                 editor.putString("username",name.get(i));
                 editor.apply();
                 Toast.makeText(getApplicationContext(),"your login",Toast.LENGTH_LONG ).show();
                 Intent intent=new Intent(Login.this,MainActivity.class);
                 startActivity(intent);
             }
         }
    }

    //retrive data
    private void RetriveData() {
        Toast.makeText(getApplicationContext(),"in data",Toast.LENGTH_LONG).show();
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren())
                {
                    Database database = dataSnapshot1.getValue(Database.class);
                    name.add(database.getName());
                    password.add(database.getPassword());

                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(String.valueOf(getApplicationContext()), "loadPost:onCancelled", databaseError.toException());
            }
        };
        databaseReference.addValueEventListener(postListener);
    }

}
