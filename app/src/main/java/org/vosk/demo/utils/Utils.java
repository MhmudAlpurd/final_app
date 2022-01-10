package org.vosk.demo.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import org.vosk.demo.setting.SettingActivity;

public class Utils {
    String MyPref = "MyPrefs";
    Context applicationContext = SettingActivity.getmContext();
    SharedPreferences sharedPref = applicationContext.getSharedPreferences(MyPref, Context.MODE_PRIVATE);
    float tts_pitch = sharedPref.getFloat("tts_pitch", 1.3f);
    float tts_speed = sharedPref.getFloat("tts_speed", 1.0f);



    public float ttsSpeed = tts_speed;
    public float ttsPitch = tts_pitch;

    public static void toast(Context context, String txt){
        Toast.makeText(context, txt, Toast.LENGTH_SHORT).show();
    }

    public static Bitmap getBitmap(Bitmap  bitmap){
        return bitmap;
    }


    public float getTts_pitch(){
        return ttsPitch;
    }

    public float getTts_speed() {
        return ttsSpeed;
    }
}
