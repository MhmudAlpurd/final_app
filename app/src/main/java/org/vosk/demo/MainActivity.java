package org.vosk.demo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;

import android.content.Context;
import android.widget.TextView;
import android.widget.Toast;

import org.vosk.demo.asr.MyCallback;
import org.vosk.demo.asr.Vosk;
import org.vosk.demo.frames.TakePhotoActivity;
import org.vosk.demo.od.CameraActivity;
import org.vosk.demo.od.tflite.Classifier;
import org.vosk.demo.od.tflite.YoloV5Classifier;
import org.vosk.demo.od.tracking.MultiBoxTracker;
import org.vosk.demo.tts.Speech;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import org.vosk.demo.od.env.Utils;
import org.vosk.demo.od.env.ImageUtils;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.net.Uri;
import android.provider.Settings;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;



public class MainActivity extends AppCompatActivity implements MyCallback{

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



     //ASR
        if(checkPermission()){
            Vosk vosk = new Vosk(MainActivity.this);
            vosk.recognize();

        }


     //OD
     startTimer();

    }

    public void set_txt(String txt){
        txt_modulee = findViewById(R.id.txt_module);
        txt_modulee.setText(txt);
    }

    public void getAnalyseBitmap(Bitmap bitmap){

        processImg(bitmap);
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
/*    private void processImg(String nameImg){
       // this.sourceBitmap = Utils.getBitmapFromAsset(MainActivity.this, nameImg);
        this.cropBitmap = Utils.processBitmap(sourceBitmap, TF_OD_API_INPUT_SIZE);
    }*/

    private void processImg(Bitmap bitmap){
        // this.sourceBitmap = Utils.getBitmapFromAsset(MainActivity.this, nameImg);
        this.cropBitmap = Utils.processBitmap(bitmap, TF_OD_API_INPUT_SIZE);
    }

    private void handlerImg(){
        Handler handler = new Handler();
        new Thread(() -> {
            final List<Classifier.Recognition> results = detector.recognizeImage(cropBitmap);
            Log.v("ttt", ""+ results);
            resultsObj(results);
        }).start();
    }

    private List resultsObj(List<Classifier.Recognition> results){
        resultObjs.clear();
        for (Classifier.Recognition i: results) {
            String[] r = i.toString().toLowerCase().trim().split(" ");
            resultObjs.add(r[1]);
        }
       // txt_result.setText(resultObjs.toString());
        return resultObjs;
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

    public void takePhotoFromBackCamera(){
        requestAppPermissions("0");
    }

    public void takePhotoFromFrontCamera(){
        requestAppPermissions("1");
    }

    private void requestAppPermissions(String type) {
        Dexter.withActivity(MainActivity.this)
                .withPermissions(
                        Manifest.permission.CAMERA)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            if (type.equals("0")) {
                                CaptureBackPhoto();
                            } else {
                                CaptureFrontPhoto();
                            }
                        }
                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            // permission is denied permenantly, navigate user to app settings
                            showSettingsDialog();
                        }
                    }
                    @Override

                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }

                })
                .onSameThread()
                .check();
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

    private void CaptureFrontPhoto() {
        Log.d(TAG, "Preparing to take photo");
        Camera camera = null;
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        int frontCamera = 1;
        //int backCamera=0;
        Camera.getCameraInfo(frontCamera, cameraInfo);
        try {
            camera = Camera.open(frontCamera);
            camera.enableShutterSound(false);
        } catch (RuntimeException e) {
            Log.d(TAG, "Camera not available: " + 1);
            camera = null;
            //e.printStackTrace();
        }

        try {

            if (null == camera) {
                Log.d(TAG, "Could not get camera instance");
            } else {
                Log.d(TAG, "Got the camera, creating the dummy surface texture");
                try {
                    camera.setPreviewTexture(new SurfaceTexture(0));
                    camera.startPreview();
                } catch (Exception e) {
                    Log.d(TAG, "Could not set the surface preview texture");
                    e.printStackTrace();
                }
                camera.takePicture(null, null, new Camera.PictureCallback() {

                    @Override

                    public void onPictureTaken(byte[] data, Camera camera) {
                        try {
                            bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
                            Log.v("testcamera", "bitmaprecieved!");
                            iv_result.setImageBitmap(bmp);
                            getAnalyseBitmap(bmp);
                            // iv_image.setImageBitmap(bmp);

                        } catch (Exception e) {
                            System.out.println(e.getMessage());
                        }
                        camera.release();
                    }
                });
            }
        } catch (Exception e) {
            camera.release();
        }

    }
    private void CaptureBackPhoto() {
        Log.d(TAG, "Preparing to take photo");
        Camera camera = null;
        try {
            camera = Camera.open();
            camera.enableShutterSound(false);
        } catch (RuntimeException e) {
            Log.d(TAG, "Camera not available: " + 1);
            camera = null;
            //e.printStackTrace();
        }
        try {
            if (null == camera) {
                Log.d(TAG, "Could not get camera instance");
            } else {
                Log.d(TAG, "Got the camera, creating the dummy surface texture");
                try {
                    camera.setPreviewTexture(new SurfaceTexture(0));
                    camera.startPreview();
                } catch (Exception e) {
                    Log.d(TAG, "Could not set the surface preview texture");
                    e.printStackTrace();
                }
                camera.takePicture(null, null, new Camera.PictureCallback() {
                    @Override
                    public void onPictureTaken(byte[] data, Camera camera) {
                        try {
                            Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
                            Log.v("testcamera", "bitmaprecieved!02");
                           // getAnalyseBitmap(bmp);
                    /*        if(callback != null){
                                callback.updateImg(bmp);
                            }*/
                            // iv_image.setImageBitmap(bmp);
                        } catch (Exception e) {
                            System.out.println(e.getMessage());
                        }
                        camera.release();
                    }
                });
            }
        } catch (Exception e) {
            camera.release();
        }

    }

    public void startTimer() {
                //set a new Timer
                timer = new Timer();

                //initialize the TimerTask's job
                initializeTimerTask();
                Log.v("tsttst", "ThreadIdStartTimer: " + Thread.currentThread().getId());

                timer.schedule(timerTask, 0, 300);


    }

    public void initializeTimerTask() {
        timerTask = new TimerTask() {
            public void run() {
                handler.post(new Runnable() {
                    public void run() {

                takePhotoFromBackCamera();
                //takePhotoFromFrontCamera();



                    }
                });
            }
        };
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


    @Override
    public void updateMyText(List resultVosk) {
        ((TextView)findViewById(R.id.txt_command)).setText("Command: " + resultVosk.get(0));
        ((TextView)findViewById(R.id.txt_module)).setText("Madule: " + resultVosk.get(1));
        ((TextView)findViewById(R.id.txt_object)).setText("Object: " + resultVosk.get(2));
        ((TextView)findViewById(R.id.txt_status)).setText("Status: "+ resultVosk.get(3));
        boolean isEx = objIsExist(resultVosk.get(2).toString().trim().toLowerCase(), resultObjs);
        ((TextView)findViewById(R.id.txt_isExist)).setText("IsExist: "+ isEx);

        if(!resultVosk.get(3).toString().equals("Say, Hey Buddy!| nothing")){
            talk(resultVosk.get(3).toString());
        }

    }



public Bitmap getImg(){
        return bmp;
}

public List getResultObjs(){
        return resultObjs;
}



}