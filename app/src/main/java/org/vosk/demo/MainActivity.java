package org.vosk.demo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;

import android.content.Context;
import android.widget.Toast;

import org.vosk.demo.asr.Vosk;
import org.vosk.demo.od.tflite.Classifier;
import org.vosk.demo.od.tflite.YoloV5Classifier;
import org.vosk.demo.od.tracking.MultiBoxTracker;
import org.vosk.demo.tts.Speech;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;
import org.vosk.demo.od.env.Utils;
import org.vosk.demo.od.env.ImageUtils;


public class MainActivity extends AppCompatActivity {
    private TextToSpeech mTTS;
    private EditText mEditText;
    private SeekBar mSeekBarPitch;
    private SeekBar mSeekBarSpeed;
    private Button mButtonSpeak;
    boolean permission = false;
    private static final int PERMISSIONS_REQUEST_RECORD_AUDIO = 1;
    public static final float MINIMUM_CONFIDENCE_TF_OD_API = 0.3f;
    //private static final Logger LOGGER = new Logger();

    public static final int TF_OD_API_INPUT_SIZE = 320;

    private static final boolean TF_OD_API_IS_QUANTIZED = false;

    private static final String TF_OD_API_MODEL_FILE = "YOLOv5m_CognitEye_int8_640.tflite";

    private static final String TF_OD_API_LABELS_FILE = "file:///android_asset/coco.txt";

    // Minimum detection confidence to track a detection.
    private static final boolean MAINTAIN_ASPECT = true;
    private Integer sensorOrientation = 90;

    private Classifier detector;

    private Matrix frameToCropTransform;
    private Matrix cropToFrameTransform;
    private MultiBoxTracker tracker;

    protected int previewWidth = 0;
    protected int previewHeight = 0;

    private Bitmap sourceBitmap;
    private Bitmap cropBitmap;

    private static Context mContext;
    private static Activity mActivity;

    Vosk vosk = new Vosk();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main22);

     mContext = getApplicationContext();
     mActivity = getmActivity();

     //TTS
     Speech.talk("welcome to the Buddy");


     //ASR
     if(checkPermission()){
            vosk.recognize();
     }

     //OD
        processImg("kite.jpg");
        initBox();
        handlerImg();

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
    private void processImg(String nameImg){
        this.sourceBitmap = Utils.getBitmapFromAsset(MainActivity.this, nameImg);
        this.cropBitmap = Utils.processBitmap(sourceBitmap, TF_OD_API_INPUT_SIZE);
    }

    private void handlerImg(){
        Handler handler = new Handler();
        new Thread(() -> {
            final List<Classifier.Recognition> results = detector.recognizeImage(cropBitmap);
            Log.v("ttt", ""+ results);
        }).start();
    }


    private void initBox() {
        previewHeight = TF_OD_API_INPUT_SIZE;
        previewWidth = TF_OD_API_INPUT_SIZE;
        frameToCropTransform =
                ImageUtils.getTransformationMatrix(
                        previewWidth, previewHeight,
                        TF_OD_API_INPUT_SIZE, TF_OD_API_INPUT_SIZE,
                        sensorOrientation, MAINTAIN_ASPECT);

        cropToFrameTransform = new Matrix();
        frameToCropTransform.invert(cropToFrameTransform);

        tracker = new MultiBoxTracker(this);

        tracker.setFrameConfiguration(TF_OD_API_INPUT_SIZE, TF_OD_API_INPUT_SIZE, sensorOrientation);

        try {
            detector =
                    YoloV5Classifier.create(
                            getAssets(),
                            TF_OD_API_MODEL_FILE,
                            TF_OD_API_LABELS_FILE,
                            TF_OD_API_IS_QUANTIZED,
                            TF_OD_API_INPUT_SIZE);
        } catch (final IOException e) {
            e.printStackTrace();
            Toast.makeText(
                    getApplicationContext(), "Exception initializing classifier!", Toast.LENGTH_SHORT);
            Toast toast =
                    Toast.makeText(
                            getApplicationContext(), "Classifier could not be initialized", Toast.LENGTH_SHORT);
            toast.show();
            finish();
        }
    }



}