package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.model.Database;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class Register extends AppCompatActivity {

    String d="";
    int k=1;
  public   ArrayList<String> name=new ArrayList<>();

    ImageView photo;
    // creating a variable for our
    // Firebase Database.
    FirebaseDatabase firebaseDatabase;

    // creating a variable for our Database
    // Reference for Firebase.
    DatabaseReference databaseReference;

    //data from firebase
    String Data;

    EditText uname,pass,phone;
TextToSpeech textToSpeech;
float x1,y1,x2,y2;
int touch=0,flag=0;

    private static final int SPEECH_REQUEST_CODE = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        // below line is used to get the
        // instance of our FIrebase database.
        firebaseDatabase = FirebaseDatabase.getInstance();

//retrive data

        System.out.print(Data);
        uname=findViewById(R.id.uname);
        pass=findViewById(R.id.pass);
        phone=findViewById(R.id.phone);
        //Start of app using voice
        textToSpeech = new TextToSpeech(this, status -> {

            if (status == TextToSpeech.SUCCESS) {
                textToSpeech.setLanguage(Locale.US);
                textToSpeech.setSpeechRate(1f);
                textToSpeech.speak("your in  registion page ", TextToSpeech.QUEUE_ADD, null);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // Code to be executed after the delay
                        SpeechRecognizer("if have alreday register then say login else  say no ");
                    }
                }, 2000); // 2000 milliseconds (2 seconds)


            }
        });
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
                        touch++;
                        if (touch > 1) {
                            SpeechRecognizer("example say username suchita");

                        }
                    }
                    if (x1 < x2) {
                        //Lgo to Login page
                        Intent intent = new Intent(Register.this, Register.class);
                        startActivity(intent);

                    }
                    if (x1 > x2) {
                        //Voice command
                        Intent intent1 = new Intent(Register.this, CommandPage.class);

                        startActivity(intent1);
                    }
                    break;

            }
            }
        catch (Exception e)
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
// This starts the activity and populates the intent with the speech text.
        textToSpeech.speak(s,TextToSpeech.QUEUE_FLUSH,null);
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
            String spokenText = results.get(0);
            if (touch == 0) {
                if (spokenText.equals("login")) {
                    Intent intent = new Intent(Register.this, Login.class);
                    startActivity(intent);

                } else {
                    textToSpeech.speak(" touch ", TextToSpeech.QUEUE_FLUSH, null);

                }
            }else if(uname.getText()!= null || pass.getText()!=null ||  phone.getText()!=null)
            {
               String[] up= spokenText.split(" ");
                if("username".equalsIgnoreCase(up[0]))
                {

try {

    uname.setText(up[1]);

}catch (IndexOutOfBoundsException e)
{
    SpeechRecognizer("say again");
}
textToSpeech.speak("username has set  as "+spokenText,TextToSpeech.QUEUE_FLUSH,null);

                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // Code to be executed after the delay
                            SpeechRecognizer("say password with same manner");
                        }
                    }, 4000); // 2000 milliseconds (2 seconds)

                    }
                else if("password".equalsIgnoreCase(up[0]))
                {
                    String[] pass1= Arrays.copyOfRange(up,1,up.length);
                    Toast.makeText(getApplicationContext(),pass1.toString(),Toast.LENGTH_LONG).show();
                    try {pass.setText(up[1]);}catch (IndexOutOfBoundsException e) {
                    SpeechRecognizer("Say again");}
                    textToSpeech.speak("password has set  as "+spokenText,TextToSpeech.QUEUE_FLUSH,null);
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // Code to be executed after the delay
                            SpeechRecognizer("say phone");
                        }
                    }, 4000); // 2000 milliseconds (2 seconds)

                }
              else if("phone".equalsIgnoreCase(up[0]))
                {
                    String p="";
                    for( int i=1 ;i<up.length;i++) p += up[i];

                    phone.setText(p);
                    textToSpeech.speak(" phone is "+p,TextToSpeech.QUEUE_FLUSH,null);
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // Code to be executed after the delay
                            SpeechRecognizer("if it is right then say yes");
                        }
                    }, 4000); // 2000 milliseconds (2 seconds)

                }
                else if(spokenText.equalsIgnoreCase( "yes" ))
                {
                    String s = String.valueOf(uname.getText());
                    String p = String.valueOf(pass.getText());
                    String ph= String.valueOf(phone.getText());
                    registerUser(s,p,ph);
                }
            }else {
                SpeechRecognizer("say it again ");
            }


            // Do something with spokenText.
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    private void registerUser(String user, String password ,String phone) {

boolean flag=false;
        Database database = new Database(user, password, phone);
        // below line is used to get reference for our database.
        databaseReference = firebaseDatabase.getReference("users");
        RetriveData();
        for (String n : name) {
            if (n.equalsIgnoreCase(user)) {
                flag = true;
                break;
            }
        }


        if(!flag){
                Toast.makeText(getApplicationContext(),String.valueOf(k),Toast.LENGTH_LONG).show();
                databaseReference.child("user" +k+1).setValue(database);
                textToSpeech.speak("your register", TextToSpeech.QUEUE_FLUSH, null);

            }else {
            textToSpeech.speak("Change username ",TextToSpeech.QUEUE_FLUSH,null);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // Code to be executed after the delay
                        SpeechRecognizer(" say username ");
                    }
                }, 4000); // 2000 milliseconds (2 seconds)

            }
            }

    //retrive data
    private void RetriveData() {
        ValueEventListener postListener = new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren())
                {
                    Database database = dataSnapshot1.getValue(Database.class);
                    name.add(database.getName());
                     Toast.makeText(getApplicationContext(),database.getName(),Toast.LENGTH_LONG).show();
                    k=k+1;

                }

            }


            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(String.valueOf(getApplicationContext()), "loadPost:onCancelled", databaseError.toException());
            }
        };
        databaseReference.addValueEventListener(postListener);    }

}