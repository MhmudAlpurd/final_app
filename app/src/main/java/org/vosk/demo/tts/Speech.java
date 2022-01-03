package org.vosk.demo.tts;

import android.speech.tts.TextToSpeech;
import android.util.Log;

import org.vosk.demo.MainActivity;
import org.vosk.demo.utils.Utils;

import java.util.Locale;


public class Speech {
    private static TextToSpeech tts;
    private static String S_str;

    public static void talk(String str){
        S_str  = str;
        tts = new TextToSpeech(MainActivity.getmContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {

                if(status != TextToSpeech.ERROR ){
                    tts.setLanguage(Locale.US);
                    tts.setSpeechRate(Utils.ttsSpeed);
                    tts.setPitch(Utils.ttsPitch);
                    tts.speak(S_str, TextToSpeech.QUEUE_FLUSH, null);
                    Log.v("sss01", "speak");
                }
            }
        });



    }



}
