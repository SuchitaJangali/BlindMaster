package com.example.myapplication;

import android.Manifest;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Audio_record extends AppCompatActivity {
    ListView listView;
    public ArrayList<File> mySongs;
    ArrayList<String> items;
    ArrayAdapter<String> adapter;
    EditText editText;
    MediaPlayer mediaPlayer;
    //MainActivity m;
    private static int MICROPHONE_PERMISSION_CODE = 200;
    MediaRecorder mediaRecorder;
    TextToSpeech textToSpeech;
    private static final int SPEECH_REQUEST_CODE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_record);
        listView = findViewById(R.id.Listsongs);
        editText = findViewById(R.id.searchView);

        mySongs = new ArrayList<>();
        items = new ArrayList<>();

        //Start of app using voice
        textToSpeech = new TextToSpeech(this, status -> {

            if (status == TextToSpeech.SUCCESS) {
                textToSpeech.setLanguage(Locale.US);
                textToSpeech.setSpeechRate(1f);
                textToSpeech.speak("your in audio", TextToSpeech.QUEUE_ADD, null);
            }
        });


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            Toast.makeText(getApplicationContext(), "in denired", Toast.LENGTH_LONG).show();
            Log.d("den", "permission");
        } else {
            loadSongs();
            Toast.makeText(getApplicationContext(), "in load song", Toast.LENGTH_LONG).show();

        }

        adapter = new ArrayAdapter<>(Audio_record.this, android.R.layout.simple_list_item_1, items);
        listView.setAdapter(adapter);



        Log.i("dd",items.toString());
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                ContextWrapper contextWrapper = new ContextWrapper((getApplicationContext()));
                File musicDic = new File("storage/emulated/0/Android/data/com.example.myapplication/files/Music/");
                Toast.makeText(getApplicationContext(), items.get(position), Toast.LENGTH_LONG).show();
                Log.i("mu", items.get(position));
                File f = new File(musicDic, String.valueOf(items.get(position)));
                Log.d("AudioFile",String.valueOf(items.get(position)));

                try {
                    mediaPlayer = new MediaPlayer();
                    mediaPlayer.setDataSource(f.getPath());
                    mediaPlayer.prepare();
                    mediaPlayer.start();

                    Toast.makeText(getApplicationContext(), "play", Toast.LENGTH_LONG).show();

                } catch (IOException e) {
                  e.printStackTrace();}
            }

            });
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                filter(s.toString());

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());
            }
        });
    }

    public void filter(String text) {
        ArrayList<String> filteredList = new ArrayList<>();

        for (String s : items) {
            if (s.toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(s);
            }
        }

        adapter.clear();
        adapter.addAll(filteredList);
        adapter.notifyDataSetChanged();
    }

    public void loadSongs() {
    ContextWrapper contextWrapper = new ContextWrapper((getApplicationContext()));
        File home = new File("storage/emulated/0/Android/data/com.example.myapplication/files/Music/");
    File internalDir = getFilesDir();
int i=1;
        if (home.listFiles(new FileExtensionFilter()).length > 0) {

            for (File file : home.listFiles(new FileExtensionFilter())) {
                Toast.makeText(getApplicationContext(),"done",Toast.LENGTH_LONG).show();
                mySongs.add(file);
                items.add(file.getName());

                String a=file.getName();
                String[] aa=a.split("_");
                Speak("Number "+i+a);

            }
        }
    }


    class FileExtensionFilter implements FilenameFilter {

        public boolean accept(File dir, String name) {
Log.w("Extensio","file");
            return (name.endsWith(".mp3") || name.endsWith(".wav"));
        }
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
            textToSpeech.speak(spokenText, TextToSpeech.QUEUE_FLUSH, null);
            String[] ss=spokenText.split(" ");
            if(ss[0].equalsIgnoreCase("date")){
                Toast.makeText(this, "date", Toast.LENGTH_SHORT).show();
                String s=spokenText.replace(" ","_");
                filter(s);

            }

            if(ss[0].equalsIgnoreCase("number")&& ss.length>=2) {
if(ss[1].equalsIgnoreCase("one")){
    ss[1]="1";
}
            int position = (Integer.parseInt(ss[1]))-1;
            if (items.get(position) == null) {
                SpeechRecognizer("Say again");
            } else {
                File musicDic = new File("storage/emulated/0/Android/data/com.example.myapplication/files/Music/");
                Toast.makeText(getApplicationContext(), items.get(position), Toast.LENGTH_LONG).show();
                Log.i("mu", items.get(position));
                File f = new File(musicDic, String.valueOf(items.get(position)));
                Log.d("AudioFile", String.valueOf(items.get(position)));

                try {
                    mediaPlayer = new MediaPlayer();
                    mediaPlayer.setDataSource(f.getPath());
                    mediaPlayer.prepare();
                    mediaPlayer.start();

                    Toast.makeText(getApplicationContext(), "play", Toast.LENGTH_LONG).show();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else if (spokenText.equalsIgnoreCase("play all") ) {
            File musicDic = new File("storage/emulated/0/Android/data/com.example.myapplication/files/Music/");
          int position=0;
            while ( position < items.size()) {

                if (!mediaPlayer.isPlaying()) {
                    File f = new File(musicDic, String.valueOf(items.get(position)));
                    Log.d("AudioFile", String.valueOf(items.get(position)));

                    try {
                 position+=1;
                        mediaPlayer = new MediaPlayer();
                        mediaPlayer.setDataSource(f.getPath());
                        mediaPlayer.prepare();
                        mediaPlayer.start();
                        Toast.makeText(getApplicationContext(), "play", Toast.LENGTH_LONG).show();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        }

        }



   private void Speak(String s){
        textToSpeech.speak(s,TextToSpeech.QUEUE_ADD,null);
   }
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            // Handle volume up button released
            SpeechRecognizer("please start with date with year,month,day");

            // Add your logic here



            return true; // Return true to indicate that the event has been handled
        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            // Handle volume down button released
            // Add your logic here

              String[] string= new String[]{String.valueOf(items)};
         int i=0;
          while (i>=string.length) {
              Handler handler=new Handler();
              handler.postDelayed(new Runnable() {
                  @Override
                  public void run() {
                      Speak("Number "+i+1+items.get(i));

                  }
              },4000);
             }

            return true; // Return true to indicate that the event has been handled
        }

        // Let the system handle other key events
        return super.onKeyUp(keyCode, event);
    }

}