package org.vosk.demo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import android.content.Context;
import android.widget.TextView;

import org.vosk.demo.asr.MyCallBacks;
import org.vosk.demo.frames.TakePhotoActivity;

import org.vosk.demo.tts.Speech;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.widget.ImageView;


public class MainActivity extends AppCompatActivity {

    private String TAG = TakePhotoActivity.class.getName();

    public TextView txt_commandd;
    public TextView txt_modulee;
    public TextView txt_objectt;
    public TextView txt_statuss;
    public TextView txt_isExistt;
    public TextView txt_result;
    public ImageView iv_result;


    boolean permission = false;
    private static final int PERMISSIONS_REQUEST_RECORD_AUDIO = 1;
    public static final float MINIMUM_CONFIDENCE_TF_OD_API = 0.3f;
    //private static final Logger LOGGER = new Logger();

    public static final int TF_OD_API_INPUT_SIZE = 320;

    private static final boolean TF_OD_API_IS_QUANTIZED = false;

    private static final String TF_OD_API_MODEL_FILE = "yolov5m_fp162.tflite";

    private static final String TF_OD_API_LABELS_FILE = "file:///android_asset/coco.txt";

    // Minimum detection confidence to track a detection.
    private static final boolean MAINTAIN_ASPECT = true;
    private Integer sensorOrientation = 0;



    private Matrix frameToCropTransform;
    private Matrix cropToFrameTransform;


    protected int previewWidth = 0;
    protected int previewHeight = 0;

    private Bitmap sourceBitmap;
    private Bitmap cropBitmap;

    private static Context mContext;
    private static Activity mActivity;
    Timer timer;
    TimerTask timerTask;
    final Handler handler = new Handler();
    List resultObjs = new ArrayList();

    String COMMAND =  "commandddd";
    String MODULE = "Moduleeee";
    String OBJECT;
    String STATUS;
    Bitmap bmp;
    //MyCallback myCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

     mContext = getApplicationContext();
     mActivity = getmActivity();



     txt_commandd = findViewById(R.id.txt_command);
     txt_objectt = findViewById(R.id.txt_object);
     txt_statuss = findViewById(R.id.txt_status);
     txt_isExistt = findViewById(R.id.txt_isExist);
     txt_result = findViewById(R.id.txt_result);
     iv_result = findViewById(R.id.iv_result);


     //TTS
     talk("welcome to the buddy!");



    /* //ASR
        if(checkPermission()){
            Vosk vosk = new Vosk();
            vosk.recognize();

        }
*/

     //OD


    }

    public void set_txt(String txt){
        txt_modulee = findViewById(R.id.txt_module);
        txt_modulee.setText(txt);
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


    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Need Permissions");
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
        builder.setPositiveButton("GOTO SETTINGS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                openSettings();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }
    private void openSettings() {

        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }




    public void talk(String txt){
        Speech.talk(txt);
    }

    public void initElements(@NonNull List<String> resultVosk){
        Log.v("tsttst" ,"recvosk0: " + resultVosk.get(0) );
        Log.v("tsttst" ,"recvosk1: " + resultVosk.get(1) );
        Log.v("tsttst" ,"recvosk2: " + resultVosk.get(2) );
        Log.v("tsttst" ,"recvosk3: " + resultVosk.get(3) );

        try{

            txt_commandd.setText(resultVosk.get(0));
        }catch (NullPointerException e){
            Log.v("tsttst" ,"null: " + e );
        }

    }


    public boolean  objIsExist(String desiredObj, @NonNull List<String> recognizedObjs){

        boolean isExist = false;
        for(String i: recognizedObjs){
            if (i.equals(desiredObj)){
                isExist = true;
            }else {
                isExist = false;
            }

        }

        return isExist;
    }








public Bitmap getImg(){
        return bmp;
}

public List getResultObjs(){
        return resultObjs;
}



}