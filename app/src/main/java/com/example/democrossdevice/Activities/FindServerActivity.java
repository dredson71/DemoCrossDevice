package com.example.democrossdevice.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.democrossdevice.R;

public class FindServerActivity extends AppCompatActivity {
    EditText editTxtIp;
    Button btnFindServer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_server);
        editTxtIp = findViewById(R.id.editTxtIpAddressServer);
        btnFindServer = findViewById(R.id.btnFindServer);

        btnFindServer.setOnClickListener(view -> {
            String ipServer =  editTxtIp.getText().toString();
            if(!ipServer.isEmpty() &&  Patterns.IP_ADDRESS.matcher(ipServer).matches()){
                Intent intent = new Intent(FindServerActivity.this,ClientActivity.class);
                intent.putExtra("ipServer",ipServer);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(getApplicationContext(),"Enter a valid Ip Address",Toast.LENGTH_SHORT).show();
            }
            });

    }
}
