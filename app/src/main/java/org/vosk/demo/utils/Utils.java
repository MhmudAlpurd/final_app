package org.vosk.demo.utils;

import android.content.Context;
import android.widget.Toast;

public class Utils {

    public static float ttsSpeed = 1.3f;
    public static float ttsPitch = 1f ;

    public static void toast(Context context, String txt){
        Toast.makeText(context, txt, Toast.LENGTH_SHORT).show();
    }


}
