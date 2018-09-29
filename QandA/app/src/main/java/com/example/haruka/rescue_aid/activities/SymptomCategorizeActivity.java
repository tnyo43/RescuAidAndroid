package com.example.haruka.rescue_aid.activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;

import com.example.haruka.rescue_aid.R;
import com.example.haruka.rescue_aid.recognition_list.ListSymptom;
import com.example.haruka.rescue_aid.utils.MedicalCertification;

import java.util.ArrayList;

public class SymptomCategorizeActivity extends LocationActivity {

    ImageView BtnToIll, BtnToInjury;
    Intent interviewIntent;
    private ArrayList<String>[] dictionary;

    final int SCENARIO_ID_ILL = MedicalCertification.SCENARIO_ID_ILL;
    final int SCENARIO_ID_INJURY = MedicalCertification.SCENARIO_ID_INJURY;


    SpeechRecognizer sr;

    class SpeechListener implements RecognitionListener {

        @Override
        public void onBeginningOfSpeech() {
            // TODO Auto-generated method stub

        }

        @Override
        public void onBufferReceived(byte[] buffer) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onEndOfSpeech() {
            // TODO Auto-generated method stub

        }

        @Override
        public void onError(int error) {
            if(error == 9) {
                //get Permission
                ActivityCompat.requestPermissions(SymptomCategorizeActivity.this, new String[]{Manifest.permission.RECORD_AUDIO}, 0);
            }else if (error != 7){
                //Toast.makeText(getApplicationContext(), "エラー " + error, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onEvent(int eventType, Bundle params) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onPartialResults(Bundle partialResults) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onReadyForSpeech(Bundle params) {
            //Toast.makeText(getApplicationContext(), "認識開始", Toast.LENGTH_SHORT).show();
        }

        private void voiceAnswer(ArrayList<String> candidates){
            int yes = 0;
            Log.d("voice answer", candidates.get(0));
            for(yes = 0; yes < 2; yes++){
                for(int index = 0; index < dictionary[yes].size(); index++){
                    if(dictionary[yes].get(index).equals(candidates.get(0))){
                        //Toast.makeText(getApplicationContext(), (yes == 0) ?"Yes":"No" , Toast.LENGTH_SHORT).show();
                        if(yes == 0){
                            BtnToIll.callOnClick();
                        }else{
                            BtnToInjury.callOnClick();
                        }
                        break;
                    }
                }
            }

        }

        @Override
        public void onResults(Bundle results) {
            ArrayList<String> candidates = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            voiceAnswer(candidates);
        }

        @Override
        public void onRmsChanged(float rmsdB) {
            // TODO Auto-generated method stub

        }

    }


    private void setSpeechRecognizer(){
        Log.i("progress", "speech recognizer started to be set");

        sr = SpeechRecognizer.createSpeechRecognizer(getApplicationContext());
        sr.setRecognitionListener(new SpeechListener());
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getPackageName());
        sr.startListening(intent);
    }

    private void askSymptom(){
        speechText("急病ですか、怪我ですか");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTitle("問診");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_symptom_categorize);

        interviewIntent = new Intent(this, InterviewActivity.class);

        BtnToIll = (ImageView)findViewById(R.id.btn_to_ill);
        BtnToIll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO go to ill interview
                //interviewIntent.putExtra("SCENARIO", scenarioIll);
                interviewIntent.putExtra("SCENARIO_ID", SCENARIO_ID_ILL);
                try {
                    sr.cancel();
                }catch (Exception e){

                }
                startActivity(interviewIntent);
                finish();
            }
        });
        BtnToInjury = (ImageView)findViewById(R.id.btn_to_injury);
        BtnToInjury.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO go to injury interview
                //interviewIntent.putExtra("SCENARIO", scenarioInjury);
                interviewIntent.putExtra("SCENARIO_ID", SCENARIO_ID_INJURY);
                try {
                    sr.cancel();
                }catch (Exception e){

                }
                startActivity(interviewIntent);
                startActivity(interviewIntent);
                finish();
            }
        });

        askSymptom();
        dictionary = ListSymptom.getDictionary();
    }

    @Override
    protected void setTtsListener(){
        // android version more than 15th
        if (Build.VERSION.SDK_INT >= 15)
        {
            int listenerResult = tts.setOnUtteranceProgressListener(new UtteranceProgressListener()
            {

                public void onDone(String utteranceId) {
                    // TODO Auto-generated method stub
                    SymptomCategorizeActivity.this.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            setSpeechRecognizer();
                        }
                    });
                }

                @Override
                public void onError(String utteranceId)
                {
                    Log.d(TAG,"progress on Error " + utteranceId);
                }

                @Override
                public void onStart(String utteranceId)
                {
                    Log.d(TAG,"progress on Start " + utteranceId);
                }

            });
            if (listenerResult != TextToSpeech.SUCCESS)
            {
                Log.e(TAG, "failed to add utterance progress listener");
            }
        }
        else {
            Log.e(TAG, "Build VERSION is less than API 15");
        }

    }

    @Override
    protected void onRestart(){
        super.onRestart();

        askSymptom();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            new AlertDialog.Builder(SymptomCategorizeActivity.this)
                    .setTitle("終了")
                    .setMessage("問診を終了しますか")
                    .setNegativeButton("はい", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                sr.cancel();
                            }catch (Exception e){

                            }
                            finish();
                        }
                    })
                    .setPositiveButton("いいえ", null)
                    .show();

            return true;
        }
        return false;
    }

}
