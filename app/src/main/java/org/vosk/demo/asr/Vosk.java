// Copyright 2019 Alpha Cephei Inc.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//       http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package org.vosk.demo.asr;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.vosk.LibVosk;
import org.vosk.LogLevel;
import org.vosk.Model;
import org.vosk.Recognizer;
import org.vosk.android.RecognitionListener;
import org.vosk.android.SpeechService;
import org.vosk.android.SpeechStreamService;
import org.vosk.android.StorageService;
import org.vosk.demo.Callback;
import org.vosk.demo.MainActivity;
import org.vosk.demo.R;
import org.vosk.demo.RoootActivity;
import org.vosk.demo.od.CameraActivity;
import org.vosk.demo.tts.Speech;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Vosk implements RecognitionListener {
    MyCallBacks myCallBacks = null;
    public Vosk(MyCallBacks callback) {
        this.myCallBacks = callback;
    }

    static private final int STATE_START = 0;
    static private final int STATE_READY = 1;
    static private final int STATE_DONE = 2;
    static private final int STATE_FILE = 3;
    static private final int STATE_MIC = 4;

    List resultVosk = new ArrayList();


    /* Used to handle permission request */
    private static final int PERMISSIONS_REQUEST_RECORD_AUDIO = 1;

    private Model model;
    private SpeechService speechService;
    private SpeechStreamService speechStreamService;


    public void recognize() {
        Log.v("result01", "1");
        // findViewById(R.id.recognize_mic).setOnClickListener(view -> recognizeMicrophone());
        //((ToggleButton) findViewById(R.id.pause)).setOnCheckedChangeListener((view, isChecked) -> pause(isChecked))
        Log.v("tsttst", "ThreadIdRECOGNIZEVOSK: " + Thread.currentThread().getId());
        Log.v("result01", "2");
        LibVosk.setLogLevel(LogLevel.INFO);
        Log.v("result01", "3");
        pause(false);
        Log.v("result01", "4");
        // Check if user has given permission to record audio, init the model after permission is granted
    initModel();

    }




    private void initModel() {
        Log.v("result01", "7");
        Log.v("result01", "model: " + model);

        StorageService.unpack(CameraActivity.getmContext(), "model-en-us", "model",
                (model) -> {
                    Log.v("result01", "8");
                    this.model = model;
                    Log.v("result01", "modelrrr: " + model);
                    recognizeMicrophone();
                },
                (exception) -> setErrorState("Failed to unpack the model" + exception.getMessage()));
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onResult(String hypothesis) {

       Log.v("res03", "Text: " + hypothesis);
        Log.v("tsttst" ,"hypothesis: " + hypothesis );
        String[] result_splited = hypothesis.split(":");
        String result = result_splited[1].trim().replaceAll("\"", "");

        //String resultt = result.trim().replaceAll("", "");
        String result_end = result.replace("}", "");
        String madule_object = COMMANDREC.find_madule_and_object(result_end);
        recognizeCommand(result_end, madule_object);


        // analyze_hypo(hypothesis);
        // resultView.append(hypothesis + "\n");

    }

    @Override
    public void onFinalResult(String hypothesis) {
       // COMMANDREC.find_madule_and_object(hypothesis);
        //resultView.append(hypothesis + "\n");
        if (speechStreamService != null) {
            speechStreamService = null;
        }

    }

    @Override
    public void onPartialResult(String hypothesis) {

        // resultView.append(hypothesis + "\n");
    }

    @Override
    public void onError(Exception e) {

        setErrorState(e.getMessage());
    }

    @Override
    public void onTimeout() {

    }


    private void setErrorState(String message) {

        //resultView.setText(message);
        //((Button) findViewById(R.id.recognize_mic)).setText(R.string.recognize_microphone);
        //findViewById(R.id.recognize_file).setEnabled(false);
        //findViewById(R.id.recognize_mic).setEnabled(false);
    }


    private void recognizeMicrophone() {

        if (speechService != null) {
            speechService.stop();
            speechService = null;
        } else {
            try {

                Recognizer rec = new Recognizer(model, 16000.0f);
                speechService = new SpeechService(rec, 16000.0f);
                speechService.startListening(this);
            } catch (IOException e) {
                setErrorState(e.getMessage());
            }
        }
    }


    private void pause(boolean checked) {
        if (speechService != null) {
            speechService.setPause(checked);
        }
    }

    public void recognizeCommand(String result, String command){
        resultVosk.clear();
        //return: command, madule, object, status
        Log.v("tsttst" ,"command: " + result  );
        String status;
if(command.split("\\|").length > 1) {
    String recognizedMadule = command.split("\\|")[0];
    String recognizedObject = command.split("\\|")[1];

    Log.v("tsttst", recognizedMadule);
    Log.v("tsttst", recognizedObject);
    if (recognizedMadule.trim().equals("Trigger Word")) {
        status = "Buddy is ready, say you command in 15 seconds!";
    } else if (recognizedMadule.trim().equals("Finding Object")) {
        status = "Buddy is finding your " + recognizedObject.trim();
    } else if (recognizedMadule.trim().equals("Scene Description")) {
        status = "Buddy is describing the scene you want.";
    } else if (recognizedMadule.trim().equals("Text Reading")) {
        status = "Buddy is reading the text you want.";
    } else {
        status = command;
    }
    Log.v("tsttst", "status: " + status);

    //mainActivity.runThread();
    resultVosk.add(result);
    resultVosk.add(recognizedMadule);
    resultVosk.add(recognizedObject);
    resultVosk.add(status);

    CameraActivity.initElements(resultVosk);
    Log.v("tsttst", "resultvosk0: " + resultVosk.get(0));
    Log.v("tsttst", "resultvosk1: " + resultVosk.get(1));
    Log.v("tsttst", "resultvosk2: " + resultVosk.get(2));
    Log.v("tsttst", "resultvosk3: " + resultVosk.get(3));
    Log.v("test03", resultVosk.get(0)+"");
    //Log.v("test03", myCallBacks+"");


 if (myCallBacks != null) {
     myCallBacks.updateMyText(resultVosk);
    }
}

    }



}
