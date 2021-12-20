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
import org.vosk.demo.MainActivity;

import java.io.IOException;

public class Vosk extends AppCompatActivity implements RecognitionListener {

    static private final int STATE_START = 0;
    static private final int STATE_READY = 1;
    static private final int STATE_DONE = 2;
    static private final int STATE_FILE = 3;
    static private final int STATE_MIC = 4;

    /* Used to handle permission request */
    private static final int PERMISSIONS_REQUEST_RECORD_AUDIO = 1;

    private Model model;
    private SpeechService speechService;
    private SpeechStreamService speechStreamService;
    private TextView moduletask;
    private TextView modulename;
    private TextView recievedcommand;
    private ImageView img_module;
    COMMANDREC find_required_madule = new COMMANDREC();


    public void recognize() {
        Log.v("result01", "1");
        // findViewById(R.id.recognize_mic).setOnClickListener(view -> recognizeMicrophone());
        //((ToggleButton) findViewById(R.id.pause)).setOnCheckedChangeListener((view, isChecked) -> pause(isChecked));


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

        StorageService.unpack(MainActivity.getmContext(), "model-en-us", "model",
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
        Log.v("result01", "10: " + hypothesis);
        //analyze_hypo(hypothesis);
        // resultView.append(hypothesis + "\n");

    }

    @Override
    public void onFinalResult(String hypothesis) {
        Log.v("result01", "11: " + hypothesis);
        //resultView.append(hypothesis + "\n");
        if (speechStreamService != null) {
            speechStreamService = null;
        }

    }

    @Override
    public void onPartialResult(String hypothesis) {
        Log.v("result01", "12: " + hypothesis);
        // resultView.append(hypothesis + "\n");
    }

    @Override
    public void onError(Exception e) {

        Log.v("result01", "13");
        setErrorState(e.getMessage());
    }

    @Override
    public void onTimeout() {
        Log.v("result01", "14");
        Log.v("result", "timeout");
    }

    private void setUiState(int state) {
        Log.v("result01", "15");
        Log.v("result", "setUiState");
        switch (state) {
            case STATE_START:
                //resultView.setText(R.string.preparing);
                //resultView.setMovementMethod(new ScrollingMovementMethod());
                //findViewById(R.id.recognize_file).setEnabled(false);
                //findViewById(R.id.recognize_mic).setEnabled(false);
                //findViewById(R.id.pause).setEnabled((false));
                break;
            case STATE_READY:
                //resultView.setText(R.string.ready);
                //((Button) findViewById(R.id.recognize_mic)).setText(R.string.recognize_microphone);
                //findViewById(R.id.recognize_file).setEnabled(true);
                //findViewById(R.id.recognize_mic).setEnabled(true);
                //findViewById(R.id.pause).setEnabled((false));
                break;
            case STATE_DONE:
                //((Button) findViewById(R.id.recognize_file)).setText(R.string.recognize_file);
                //((Button) findViewById(R.id.recognize_mic)).setText(R.string.recognize_microphone);
                //findViewById(R.id.recognize_file).setEnabled(true);
                //findViewById(R.id.recognize_mic).setEnabled(true);
                //findViewById(R.id.pause).setEnabled((false));
                break;
            case STATE_FILE:
                //((Button) findViewById(R.id.recognize_file)).setText(R.string.stop_file);
                //resultView.setText(getString(R.string.starting));
                //findViewById(R.id.recognize_mic).setEnabled(false);
                //findViewById(R.id.recognize_file).setEnabled(true);
                //findViewById(R.id.pause).setEnabled((false));
                break;
            case STATE_MIC:
                //((Button) findViewById(R.id.recognize_mic)).setText(R.string.stop_microphone);
                //resultView.setText(getString(R.string.say_something));
                //findViewById(R.id.recognize_file).setEnabled(false);
                //findViewById(R.id.recognize_mic).setEnabled(true);
                //findViewById(R.id.pause).setEnabled((true));
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + state);
        }
    }

    private void setErrorState(String message) {
        Log.v("result01", "16");
        Log.v("result", "setErrorState");
        //resultView.setText(message);
        //((Button) findViewById(R.id.recognize_mic)).setText(R.string.recognize_microphone);
        //findViewById(R.id.recognize_file).setEnabled(false);
        //findViewById(R.id.recognize_mic).setEnabled(false);
    }


    private void recognizeMicrophone() {
        Log.v("result01", "17");
        Log.v("result", "recognizeMicrophone");
        if (speechService != null) {
            speechService.stop();
            speechService = null;
        } else {
            try {
                Log.v("result01", "18");
                Log.v("result01", "modelrec: " + model);
                Recognizer rec = new Recognizer(model, 16000.0f);
                Log.v("result01", "18.1");
                speechService = new SpeechService(rec, 16000.0f);
                Log.v("result01", "18.2");
                speechService.startListening(this);
                Log.v("result01", "18.3");
            } catch (IOException e) {
                Log.v("result01", "18.5: " + e.getMessage());
                setErrorState(e.getMessage());
            }
        }
    }


    private void pause(boolean checked) {
        Log.v("result01", "19");
        Log.v("result", "pause");
        if (speechService != null) {
            speechService.setPause(checked);
        }
    }



}
