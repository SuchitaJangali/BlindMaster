package com.example.myapplication;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.List;
import java.util.Locale;

public class Location extends AppCompatActivity {
    TextView textView;
    private FusedLocationProviderClient fusedLocationClient;
    double latitude;
    double longitude;
TextToSpeech textToSpeech;
    float x1,x2,y1,y2;
    String addressDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);


        textView=findViewById(R.id.location);
        //Start of app using voice
        textToSpeech = new TextToSpeech(this, status -> {

            if (status == TextToSpeech.SUCCESS) {
                textToSpeech.setLanguage(Locale.US);
                textToSpeech.setSpeechRate(1f);
                textToSpeech.speak("your in  Location", TextToSpeech.QUEUE_ADD, null);
                textToSpeech.speak("touch to  konwn location ",TextToSpeech.QUEUE_ADD,null);
            }
        });

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            getLastLocation();
        }

        private void getLastLocation() {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }else {
                fusedLocationClient.getLastLocation()
                        .addOnSuccessListener(new OnSuccessListener<android.location.Location>() {
                            @Override
                            public void onSuccess(android.location.Location location) {
                                // Got last known location. In some rare situations this can be null.
                                if (location != null) {
                                    // Use the location
                                    latitude = location.getLatitude();
                                    longitude = location.getLongitude();
                                    Toast.makeText(Location.this, latitude+""+longitude, Toast.LENGTH_SHORT).show();
                                    // Do something with latitude and longitude

                                    Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
//Geocoding refers to transforming street address or any address
                                    List<Address> addresses = null;
                                    try {
                                        addresses = geocoder.getFromLocation(latitude, longitude,1);
                                    } catch (Exception ioException) {
                                        Log.e("", "Error in getting address for the location");
                                    }
                                    if (addresses == null || addresses.size() == 0) {
                                        String msg = "No address found for the location";
                                    } else {
                                        Address address = addresses.get(0);
                                        addressDetails = address.getFeatureName() + "." + "\n" +
                                                "Locality is, " + address.getLocality() + "." + "\n" + "City is ," + address.getSubAdminArea() + "."
                                                + "\n" +
                                                "State is, " + address.getAdminArea() + "." + "\n" + "Country is, " + address.getCountryName()
                                                + "." + "\n";
                                        Toast.makeText(Location.this, addressDetails, Toast.LENGTH_SHORT).show();
                                        textView.setText(addressDetails);
                                    Speak(addressDetails);
                                    }
                                }

                            }
                        });
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
                    Speak(addressDetails);
                }
                if(x1<x2){
                    //helping page
                    Intent intent = new Intent(Location.this, HelpingPage.class);
                    startActivity(intent);
                }
                if(x1>x2){
                    //Voice command
                    Intent intent1 = new Intent(Location.this, CommandPage.class);

                    startActivity(intent1);
                }
                break;

        }
        // Return 'true' to indicate that the touch event has been consumed
        return true;
    }
    private void Speak(String s){
        textToSpeech.speak(s,TextToSpeech.QUEUE_ADD,null);
    }

}