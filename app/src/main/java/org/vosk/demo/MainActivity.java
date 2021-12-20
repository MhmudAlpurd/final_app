package org.vosk.demo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;

import android.content.Context;

import org.vosk.demo.R;
import org.vosk.demo.utils.Utils;
import org.vosk.demo.asr.Vosk;
import org.vosk.demo.tts.Speech;

public class MainActivity extends AppCompatActivity {
    private TextToSpeech mTTS;
    private EditText mEditText;
    private SeekBar mSeekBarPitch;
    private SeekBar mSeekBarSpeed;
    private Button mButtonSpeak;
    boolean permission = false;
    private static final int PERMISSIONS_REQUEST_RECORD_AUDIO = 1;

    private static Context mContext;
    private static Activity mActivity;

    Vosk vosk = new Vosk();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

     mContext = getApplicationContext();
     mActivity = getmActivity();

     //TTS
     Speech.talk("welcome to the Buddy");


     //ASR
     if(checkPermission()){
            vosk.recognize();
     }

    }


    public static Context getmContext() { return mContext; }

    public static Activity getmActivity(){return mActivity; }

    private boolean checkPermission() {
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }else{
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, PERMISSIONS_REQUEST_RECORD_AUDIO);
            return false;
        }

    }



}