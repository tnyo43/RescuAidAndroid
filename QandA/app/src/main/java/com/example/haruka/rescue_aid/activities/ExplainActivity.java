package com.example.haruka.rescue_aid.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.haruka.rescue_aid.R;
import com.example.haruka.rescue_aid.utils.ExplainCare;
import com.example.haruka.rescue_aid.utils.MedicalCertification;
import com.example.haruka.rescue_aid.utils.QADateFormat;
import com.example.haruka.rescue_aid.utils.Record;
import com.example.haruka.rescue_aid.utils.Utils;

import java.util.ArrayList;
import java.util.Date;

import jp.fsoriented.cactusmetronome.lib.Click;
import jp.fsoriented.cactusmetronome.lib.DefaultHighClickCallback;
import jp.fsoriented.cactusmetronome.lib.Metronome;

public class ExplainActivity extends LocationActivity {

    private static final int SUB_ACTIVITY = 1001;

    Metronome mMetronome;
    //Button explainButton;
    //Button finishButton;
    Button finishBtn, aedBtn;
    ImageView  forwardBtn, backBtn;
    ExplainCare mainEmergencyExplanation, subEmergencyExplanation;

    TextView textView, pageText;
    ImageView imageView;

    int explainIndex;
    Handler _handler;
    boolean useSwitchTimer;
    boolean careAED;
    boolean isLastSaved;

    Date start;


    private static class BpmUtil {
        public static int getSampleLength(double bpm) {
            // 1beatあたりの長さ（care_sample）
            return (int)(60 * Metronome.FREQUENCY / bpm);
        }
    }

    private static enum NoteEnum {
        // basic notes
        BASIC_4(new double[]{0}, 1.0/8),
        BASIC_8(new double[]{0, 0.5}, 1.0/8);


        private final double[] beats;
        private final double length;

        private NoteEnum(double[] beats, double length) {
            this.beats = beats;
            this.length = length;
        }

        /**
         *
         * @param destination
         * @param lengthOfQuarter
         * @param index
         */
        public void addNewClicks(ArrayList<Click> destination, int lengthOfQuarter, int index) {
            for (int i=0; i<beats.length; i++) {
                double beat = beats[i];
                int when = (int)(beat * lengthOfQuarter) + lengthOfQuarter * index;
                int len = (int)(length * lengthOfQuarter);
                Click c;
                if (index == 0 && i == 0) {
                    c = new Click(when, len, new DefaultHighClickCallback());
                } else {
                    c = new Click(when, len);
                }
                destination.add(c);
            }
        }
    }

    public void startMetronome() {
        stopMetronome();

        int tempo = 100;

        ArrayList<Click> list = new ArrayList<Click>();
        int samples = BpmUtil.getSampleLength(tempo);
        int beatsPerMeasure = 1;
        NoteEnum note = NoteEnum.BASIC_4;
        for (int i=0; i<beatsPerMeasure ; i++) {
            note.addNewClicks(list, samples, i);
        }
        mMetronome.start();
        mMetronome.setPattern(list, samples * beatsPerMeasure);
    }

    public void stopMetronome() {
        try{
            mMetronome.finish();
        }catch (Exception e){
            Log.i("stop metronome", e.toString());
        }

    }

    void setTextView(String text){
        textView.setText(text);
        speechText(text);
    }

    void setPage(){
        String page = Integer.toString(explainIndex+1) + "/" + Integer.toString(mainEmergencyExplanation.numSituation);
        pageText.setText(page);
    }

    void nextExplanation(){

        explainIndex = (explainIndex+1) % mainEmergencyExplanation.numSituation;
        setExplain(explainIndex);
        setPage();

        try {
            if (TitleActivity.MODE_DEMO) {
                _handler.removeCallbacksAndMessages(null);
                _handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        nextExplanation();
                    }
                }, 60000);
            } else {
                _handler.removeCallbacksAndMessages(null);
                _handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        nextExplanation();
                    }
                }, mainEmergencyExplanation.getDuration(explainIndex));

            }
        } catch (Exception e){
            _handler.removeCallbacksAndMessages(null);
            _handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    nextExplanation();
                }
            }, mainEmergencyExplanation.getDuration(explainIndex));
        }
    }

    void previousExplanation(){
        explainIndex = (explainIndex+mainEmergencyExplanation.numSituation-1) % mainEmergencyExplanation.numSituation;
        setExplain(explainIndex);
        setPage();

        try {
            if (TitleActivity.MODE_DEMO) {
                _handler.removeCallbacksAndMessages(null);
                _handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        nextExplanation();
                    }
                }, 60000);
            } else {
                _handler.removeCallbacksAndMessages(null);
                _handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        nextExplanation();
                    }
                }, mainEmergencyExplanation.getDuration(explainIndex));

            }
        } catch (Exception e){
            _handler.removeCallbacksAndMessages(null);
            _handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    nextExplanation();
                }
            }, mainEmergencyExplanation.getDuration(explainIndex));
        }

    }

    public void setExplain(int index){
        setTextView(mainEmergencyExplanation.getText(index));
        imageView.setImageDrawable(mainEmergencyExplanation.getImage(index));
        //explainButton.setText(mainEmergencyExplanation.getButtonText(index));
        //finishButton.setText(mainEmergencyExplanation.getButton2Text(index));
    }

    void mainExplain(){

        Log.d("Care", Integer.toString(mainEmergencyExplanation.id_));
        start = new Date();

        _handler.removeCallbacksAndMessages(null);
        stopMetronome();
        if (mainEmergencyExplanation.isMetronomeRequired){
            startMetronome();
        }else{
            stopMetronome();
        }

        explainIndex = 0;
        setExplain(explainIndex);

        try {
            if (TitleActivity.MODE_DEMO) {
                _handler.removeCallbacksAndMessages(null);
                _handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        nextExplanation();
                    }
                }, 60000);
            } else {
                _handler.removeCallbacksAndMessages(null);
                _handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        nextExplanation();
                    }
                }, mainEmergencyExplanation.getDuration(0));

            }
        } catch (Exception e){
            _handler.removeCallbacksAndMessages(null);
            _handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    nextExplanation();
                }
            }, mainEmergencyExplanation.getDuration(0));
        }
        careAED = false;

    }

    void setSubExplanation(int index){
        setTextView(subEmergencyExplanation.getText(index));
        imageView.setImageDrawable(subEmergencyExplanation.getImage(index));
        careAED = true;
    }

    void subExplaination(){
        start = new Date();
        _handler.removeCallbacksAndMessages(null);
        stopMetronome();
        if (subEmergencyExplanation.isMetronomeRequired){
            startMetronome();
        }

        aedBtn.setText("胸骨圧迫を\n再開する");
        setSubExplanation(0);
        aedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aedBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Date end = new Date();
                        Log.d("care end" , QADateFormat.getStringDate(new Date((end.getTime() - start.getTime()) + QADateFormat.getDate(medicalCertification.records.get(0).getTime()).getTime())));
                        String duration = QADateFormat.getStringDate(new Date((end.getTime() - start.getTime()) + QADateFormat.getDate(medicalCertification.records.get(0).getTime()).getTime()));
                        Record r = new Record(duration, "Care", Integer.toString(mainEmergencyExplanation.id_));
                        Log.d("care record", r.toString());
                        medicalCertification.addRecord(r);

                        subExplaination();
                    }
                });

                Date end = new Date();
                Log.d("care end" , QADateFormat.getStringDate(new Date((end.getTime() - start.getTime()) + QADateFormat.getDate(medicalCertification.records.get(0).getTime()).getTime())));
                String duration = QADateFormat.getStringDate(new Date((end.getTime() - start.getTime()) + QADateFormat.getDate(medicalCertification.records.get(0).getTime()).getTime()));
                Record r = new Record(duration, "Care", Integer.toString(subEmergencyExplanation.id_));
                Log.d("care record", r.toString());
                medicalCertification.addRecord(r);

                mainExplain();
                aedBtn.setText("AEDが到着した");
                //nextExplanation();
            }
        });
    }

    @Override
    public void finish(){
        savelast();
        isLastSaved = true;
        Intent data = new Intent();
        data.putExtra(Utils.TAG_INTENT_CERTIFICATION, medicalCertification);
        setResult(RESULT_OK, data);

        super.finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTitle("応急手当");
        super.onCreate(savedInstanceState);

        try {
            medicalCertification = (MedicalCertification) getIntent().getSerializableExtra("CERTIFICATION");
        }catch (Exception e){
            Log.e("ExplainActivity", e.toString());
        }
        if(medicalCertification == null){
            Log.i("medical certification", "is null");
            medicalCertification = new MedicalCertification();
        }
        String careXML = getIntent().getStringExtra("CARE_XML");
        if (careXML == null){
            careXML = "";
        }
        if(!careXML.equals("care_chest_compression")) { // 胸骨圧迫だけUIが違う
            setContentView(R.layout.activity_explain);
            textView = (TextView) findViewById(R.id.textview_explain_heart_massage);
            imageView = (ImageView) findViewById(R.id.imageview_explain_heart_massage);
            finishBtn = (Button) findViewById(R.id.btn_explain_finish);
            backBtn = (ImageView) findViewById(R.id.btn_explain_back);
            forwardBtn = (ImageView) findViewById(R.id.btn_explain_next);
            pageText = (TextView)findViewById(R.id.explain_page);
        } else {
            setContentView(R.layout.activity_explain_2);
            textView = (TextView) findViewById(R.id.textview_explain_heart_massage2);
            imageView = (ImageView) findViewById(R.id.imageview_explain_heart_massage2);
            finishBtn = (Button) findViewById(R.id.btn_explain_finish2);
            backBtn = (ImageView) findViewById(R.id.btn_explain_back2);
            forwardBtn = (ImageView) findViewById(R.id.btn_explain_next2);
            pageText = (TextView)findViewById(R.id.explain_page2);
            aedBtn = (Button)findViewById(R.id.btn_explain_aed);
            aedBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Date end = new Date();
                    Log.d("care end" , QADateFormat.getStringDate(new Date((end.getTime() - start.getTime()) + QADateFormat.getDate(medicalCertification.records.get(0).getTime()).getTime())));
                    String duration = QADateFormat.getStringDate(new Date((end.getTime() - start.getTime()) + QADateFormat.getDate(medicalCertification.records.get(0).getTime()).getTime()));
                    Record r = new Record(duration, "Care", Integer.toString(mainEmergencyExplanation.id_));
                    Log.d("care record", r.toString());
                    medicalCertification.addRecord(r);

                    subExplaination();
                }
            });
        }

        finishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishProcess();
            }
        });
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                previousExplanation();
            }
        });
        forwardBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                nextExplanation();
            }
        });

        mMetronome = new Metronome();
        _handler = new Handler();

        mainEmergencyExplanation = new ExplainCare(this, careXML);

        setPage();

        if (mainEmergencyExplanation.sub.equals("")) {
            subEmergencyExplanation = null;
        } else {
            subEmergencyExplanation = new ExplainCare(this, mainEmergencyExplanation.sub);
            if (!subEmergencyExplanation.isActive) {
                subEmergencyExplanation = null;
            }
        }
        mainExplain();
        useSwitchTimer = true;
        careAED = false;
        isLastSaved = false;

        medicalCertification.save(this);

    }

    protected void speechTextFinish(String text){
        speechText(text);
        _handler.removeCallbacksAndMessages(null);
        stopMetronome();
    }

    private void finishProcess(){
        speechTextFinish("応急手当を終了しますか");
        new AlertDialog.Builder(ExplainActivity.this)
                .setMessage("応急手当を終了しますか")
                .setNegativeButton("はい", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setPositiveButton("いいえ", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ExplainCare explainCare = (!careAED) ? mainEmergencyExplanation : subEmergencyExplanation;

                        try {
                            if (TitleActivity.MODE_DEMO) {
                                _handler.removeCallbacksAndMessages(null);
                                _handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        nextExplanation();
                                    }
                                }, 60000);
                            } else {
                                _handler.removeCallbacksAndMessages(null);
                                _handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        nextExplanation();
                                    }
                                }, explainCare.getDuration(explainIndex));

                            }
                        } catch (Exception e){
                            _handler.removeCallbacksAndMessages(null);
                            _handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    nextExplanation();
                                }
                            }, explainCare.getDuration(explainIndex));
                        }
                        speechText(explainCare.getText(explainIndex));
                        if (explainCare.isMetronomeRequired){
                            startMetronome();
                        }
                    }
                })
                .setCancelable(false)
                .show();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            finishProcess();
            return true;
        }
        return false;
    }

    private void savelast(){
        // AEDの処理は特別に分ける
        // 前の処理を覚えていてAEDの説明と入れ替える
        Date end = new Date();
        Log.d("care end" , QADateFormat.getStringDate(new Date((end.getTime() - start.getTime()) + QADateFormat.getDate(medicalCertification.records.get(0).getTime()).getTime())));
        String duration = QADateFormat.getStringDate(new Date((end.getTime() - start.getTime()) + QADateFormat.getDate(medicalCertification.records.get(0).getTime()).getTime()));
        Record r;
        if (careAED) {
            r = new Record(duration, "Care", Integer.toString(subEmergencyExplanation.id_));
        } else {
            r = new Record(duration, "Care", Integer.toString(mainEmergencyExplanation.id_));
        }
        medicalCertification.addRecord(r);
        Log.d("care record", r.toString());

        medicalCertification.save(this);
    }

    @Override
    protected void onPause(){
        super.onPause();

        stopMetronome();
        _handler.removeCallbacksAndMessages(null);

        if(!isLastSaved) {
            savelast();
        }
    }
}
