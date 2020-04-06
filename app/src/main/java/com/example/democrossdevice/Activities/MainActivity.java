package com.example.democrossdevice.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.app.mg.connectionlibraryandroid.Implementations.ConnectMethods;
import com.example.democrossdevice.R;

public class MainActivity extends AppCompatActivity {
    ConnectMethods connectMethods = new ConnectMethods();
    TextView myIpText;
    String myIpAddress;
    Button btnClient,btnServer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myIpAddress = connectMethods.FindMyIpAddress(this);
        myIpText = findViewById(R.id.txtIpAddress);
        myIpText.setText(myIpAddress);
        btnClient = findViewById(R.id.btnClient);
        btnServer = findViewById(R.id.btnServer);
        btnServer.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), ServerActivity.class);
            startActivity(intent);
        });
        btnClient.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), FindServerActivity.class);
            startActivity(intent);
        });
    }
}
