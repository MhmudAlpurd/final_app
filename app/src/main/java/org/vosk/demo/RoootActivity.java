package org.vosk.demo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.List;

public class RoootActivity extends AppCompatActivity {

    Button connecting;
    Button tapToConnect;
    final Handler handler = new Handler();
    final int delay = 3000; // 1000 milliseconds == 1 second

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rooot);

        connecting = findViewById(R.id.btn_connecting);
        tapToConnect = findViewById(R.id.btn_taptoconnect);

        //tapToConnect.setVisibility(View.VISIBLE);
        //connecting.setVisibility(View.GONE);

        tapToConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tapToConnect.setVisibility(View.GONE);
                connecting.setVisibility(View.VISIBLE);
                Intent intent = new Intent(RoootActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }


}