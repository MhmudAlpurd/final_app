package org.vosk.demo.tts;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.vosk.demo.MainActivity;
import org.vosk.demo.od.CameraActivity;
import org.vosk.demo.setting.SettingActivity;
import org.vosk.demo.utils.Utils;

import java.util.Locale;


public class Speech extends AppCompatActivity {
    private static TextToSpeech tts;
    private static String S_str;
    String MyPref = "MyPrefs";
    int tts_pitch;
    int tts_speed;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPreferences = getSharedPreferences(MyPref, Context.MODE_PRIVATE);
        tts_pitch = (int) sharedPreferences.getFloat("tts_pitch", 50);
        tts_speed = (int) sharedPreferences.getFloat("tts_speed", 50);
        Log.v("ssstu", tts_pitch+"");


    }


    public static void talk(String str){
        S_str  = str;
        tts = new TextToSpeech(CameraActivity.getmContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                Speech speech = new Speech();

                if(status != TextToSpeech.ERROR ){
                    tts.setLanguage(Locale.US);
                    tts.setSpeechRate(speech.tts_speed / 100.0f);
                    tts.setPitch(speech.tts_pitch / 100.0f);
                    tts.speak(S_str, TextToSpeech.QUEUE_FLUSH, null);
                    Log.v("sss01", "speak");
                    Log.v("ttsparams", speech.tts_speed+"");
                }
            }
        });



    }



}
