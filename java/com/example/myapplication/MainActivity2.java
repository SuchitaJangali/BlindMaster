package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        ImageView imageView =  findViewById(R.id.img);

        Intent intent = getIntent();
        if (intent != null) {
            String imagePath = intent.getStringExtra("IMAGE_PATH");
            // Log the received image path for debugging
            Toast.makeText(MainActivity2.this, "Image Path: " + imagePath, Toast.LENGTH_LONG).show();
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            imageView.setImageBitmap(bitmap);

        }
    }
    }
