package com.example.mysamplelib;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageButton;

import com.example.mylib.KuvrrSDK;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageButton call911Button = findViewById(R.id.call_911_button);
        ImageButton sosButton = findViewById(R.id.sos_button);
        ImageButton planButton = findViewById(R.id.erp_button);
        ImageButton mapButton = findViewById(R.id.map_button);

        call911Button.setOnClickListener(view -> {
            KuvrrSDK.startDial911(getApplicationContext(), MainActivity.this, "ritesh@kuvrr.com", "159753");
        });

        sosButton.setOnClickListener(view -> {
            KuvrrSDK.startSos(getApplicationContext(), MainActivity.this, "ritesh@kuvrr.com", "159753");
        });

        planButton.setOnClickListener(view -> {
            KuvrrSDK.openPlans(getApplicationContext(), MainActivity.this);
        });

        mapButton.setOnClickListener(view -> {
            KuvrrSDK.openMaps(getApplicationContext(), MainActivity.this);
        });

    }
}