package org.vosk.demo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import org.vosk.demo.od.DetectorActivity;
import org.vosk.demo.setting.SettingActivity;

import java.util.List;

public class RoootActivity extends AppCompatActivity {
float x1, x2, y1, y2;
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
                Intent intent = new Intent(RoootActivity.this, DetectorActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent touchEvent) {
        switch (touchEvent.getAction()){

            case MotionEvent.ACTION_DOWN:
               x1 = touchEvent.getX();
               y1 = touchEvent.getY();
                break;

            case MotionEvent.ACTION_UP:
               x2 = touchEvent.getX();
               y2 = touchEvent.getY();


            if(x1 < x2){
                Intent intent = new Intent(RoootActivity.this, SettingActivity.class);
                startActivity(intent);
            }
                break;

        }
        return false;
    }
}