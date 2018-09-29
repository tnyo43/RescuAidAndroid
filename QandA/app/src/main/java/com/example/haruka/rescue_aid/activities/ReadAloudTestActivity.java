package com.example.haruka.rescue_aid.activities;

import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import android.widget.EditText;

import java.util.HashMap;

public class ReadAloudTestActivity extends OptionActivity implements TextToSpeech.OnInitListener{

    protected TextToSpeech tts;
    protected EditText editor;
    protected static final String TAG = "TestTTS";
    protected boolean isInitialized = false;
    protected String willBeSpoken = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tts = new TextToSpeech(this, this);
    }

    @Override
    public void onInit(int status) {
        if (TextToSpeech.SUCCESS == status) {
            Log.d(TAG, "initialized");
            isInitialized = true;
            speechText(willBeSpoken);
            willBeSpoken = "";
        } else {
            Log.e(TAG, "faile to initialize");
        }
    }

    private void shutDown(){
        if (null != tts) {
            // to release the resource of TextToSpeech
            tts.shutdown();
        }
    }

    // 音声を再生する、設定を変えないならStringのtextを投げるだけ
    protected void speechText(String text) {
        Log.d(TAG, "text is " + text);
        if (text.length() > 0) {
            if (!isInitialized){
                willBeSpoken = text;
            }else {
                Log.d(TAG, "is speaking " + Boolean.toString(tts.isSpeaking()));
                if (tts.isSpeaking()) {
                    tts.stop();
                }
                Log.d(TAG, "set rate and pitch");

                setSpeechRate(1.0f);
                setSpeechPitch(1.0f);

                // tts.speak(text, TextToSpeech.QUEUE_FLUSH, null) に
                // KEY_PARAM_UTTERANCE_ID を HasMap で設定
                HashMap<String, String> map = new HashMap<String, String>();
                map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "messageID");

                tts.speak(text, TextToSpeech.QUEUE_FLUSH, map);
                setTtsListener();
            }
        }
    }

    protected void setSpeechRate(float rate){
        if (null != tts) {
            tts.setSpeechRate(rate);
        }
    }

    protected void setSpeechPitch(float pitch){
        if (null != tts) {
            tts.setPitch(pitch);
        }
    }

    protected void setTtsListener(){
        // android version more than 15th
        if (Build.VERSION.SDK_INT >= 15)
        {
            int listenerResult = tts.setOnUtteranceProgressListener(new UtteranceProgressListener()
            {
                @Override
                public void onDone(String utteranceId)
                {
                    Log.d(TAG,"progress on Done " + utteranceId);
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
    protected void onPause() {
        super.onPause();
        tts.stop();
    }

    protected void onDestroy() {
        super.onDestroy();
        tts.stop();
        shutDown();
    }
}
