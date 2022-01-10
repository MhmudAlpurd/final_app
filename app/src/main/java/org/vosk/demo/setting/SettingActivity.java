package org.vosk.demo.setting;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Toast;

import org.vosk.demo.R;

public class SettingActivity extends AppCompatActivity {

    SeekBar seek_bar_speed, seek_bar_pitch;
    Button save_setting;
    SharedPreferences shPref;
    String MyPref = "MyPrefs";
    private static Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        seek_bar_pitch = findViewById(R.id.seek_bar_pitch);
        seek_bar_speed = findViewById(R.id.seek_bar_speed);
        save_setting = findViewById(R.id.btn_save_setting);
        seek_bar_pitch.setMax(100);
        seek_bar_speed.setMax(100);
        shPref = getSharedPreferences(MyPref, Context.MODE_PRIVATE);

        mContext = getApplicationContext();

        save_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                float pitch  = seek_bar_pitch.getProgress() ;

                float speed  = seek_bar_speed.getProgress();

                Log.v("setporgress", "1: "+pitch);

                SharedPreferences.Editor sEdit = shPref.edit();
                sEdit.putFloat("tts_pitch", pitch);
                sEdit.putFloat("tts_speed", speed);

                sEdit.apply();

                Toast.makeText(SettingActivity.this, "Saved", Toast.LENGTH_SHORT).show();

            }
        });

        if (shPref.contains("tts_pitch")) {
            seek_bar_pitch.setProgress((int) shPref.getFloat("tts_pitch", 50.0f));
            //seek_bar_pitch.setProgress(80);
            Log.v("setporgress", "2: "+ shPref.getFloat("tts_pitch", 50.0f));
        }

        if (shPref.contains("tts_speed")) {
            seek_bar_pitch.setProgress((int) shPref.getFloat("tts_speed", 50.0f));
        }


    }

    public static Context getmContext() { return mContext; }




}