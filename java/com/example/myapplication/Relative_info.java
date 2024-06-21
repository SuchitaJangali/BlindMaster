package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.EditText;

public class Relative_info extends AppCompatActivity {
EditText rname,rphone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_relative_info);
        rname=findViewById(R.id.uname);
        rphone=findViewById(R.id.phone);


    }
}