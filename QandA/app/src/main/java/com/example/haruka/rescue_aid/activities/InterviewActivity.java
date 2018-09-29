package com.example.haruka.rescue_aid.activities;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
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
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.haruka.rescue_aid.R;
import com.example.haruka.rescue_aid.recognition_list.ListYesNo;
import com.example.haruka.rescue_aid.utils.InterviewAnswers;
import com.example.haruka.rescue_aid.utils.MedicalCertification;
import com.example.haruka.rescue_aid.utils.Question;
import com.example.haruka.rescue_aid.utils.Record;
import com.example.haruka.rescue_aid.utils.Utils;
import com.example.haruka.rescue_aid.views.HistoryButton;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.StringTokenizer;

import static com.example.haruka.rescue_aid.R.id.interview;
import static java.lang.Integer.parseInt;

public class InterviewActivity extends LocationActivity{

    private Context context;
    private Button mBtnYes;
    private Button mBtnNo;
    private Button mBtnUnsure;
    private ImageView imageView;
    private TextView mInterviewContent;
    private HorizontalScrollView historyScroll;
    private LinearLayout historyScrollLayout;
    //TODO check ListView

    private ArrayList<Question> questions;
    private Question currentQuestion;
    private ArrayList<Question> usedQuestions;
    private boolean isInterviewDone;
    private ArrayList<String>[] dictionary;

    Handler _handler;

    private final boolean IS_THROUGH_INTERVIEW = true;

    int scenarioID;
    String scenario;
    String iconFolder;

    class SpeechListener implements RecognitionListener {

        @Override
        public void onBeginningOfSpeech() {
        }

        @Override
        public void onBufferReceived(byte[] buffer) {
        }

        @Override
        public void onEndOfSpeech() {
        }

        @Override
        public void onError(int error) {
            if(error == 9) {
                //get Permission
                ActivityCompat.requestPermissions(InterviewActivity.this, new String[]{Manifest.permission.RECORD_AUDIO}, 0);
            }else if (error != 7){
                Toast.makeText(getApplicationContext(), "エラー " + error, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onEvent(int eventType, Bundle params) {
        }

        @Override
        public void onPartialResults(Bundle partialResults) {
        }

        @Override
        public void onReadyForSpeech(Bundle params) {
            //Toast.makeText(getApplicationContext(), "認識開始", Toast.LENGTH_SHORT).show();
        }

        private void voiceAnswer(ArrayList<String> candidates){
            int yes = 0;

            for(yes = 0; yes < 3; yes++){
                for(int index = 0; index < dictionary[yes].size(); index++){
                    if(dictionary[yes].get(index).equals(candidates.get(0))){
                        String message = "";
                        if (yes == 0){
                            mBtnYes.callOnClick();
                            message = "YES";
                        } else if (yes == 1){
                            mBtnNo.callOnClick();
                            message = "NO";
                        } else {
                            mBtnUnsure.callOnClick();
                            message = "UNSURE";
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
        }

    }


    private SpeechRecognizer sr;

    private void showReadQuestion(){
        mInterviewContent.setText(currentQuestion.getQuestion());
        String img = iconFolder + "img" + String.format("%02d", currentQuestion.getIndex()) + ".jpg";
        Log.d("iconFolder", img);
        imageView.setImageDrawable(getDrawable(img));
        try{
            speechText(currentQuestion.getQuestion());
        }catch (Exception e){

        }
    }

    private void setNextQuestion(Question q){
        currentQuestion = q;
        showReadQuestion();
    }

    public String getCareString(boolean[] cares){
        String s = "";
        for (boolean c : cares){
            s += c ? "Y" : "N";
        }

        return s;
    }

    private void loadQuestions(){
        AssetManager assetManager = this.context.getResources().getAssets();
        try{
            String scenario_ = "scenarios/" + scenario;
            Log.d("Scenario", scenario_);
            InputStream is = assetManager.open(scenario_);
            InputStreamReader inputStreamReader = new InputStreamReader(is);
            BufferedReader bufferReader = new BufferedReader(inputStreamReader);
            String line = "";
            line = bufferReader.readLine();
            int _i = 0;
            while ((line = bufferReader.readLine()) != null) {
                Question q;
                StringTokenizer st = new StringTokenizer(line, ",");
                Log.d("scenario line", line);
                _i++;
                String id = st.nextToken();
                if(id == "id") continue;
                int index = parseInt(id);
                String text = st.nextToken();
                Log.d("text", text);
                int yesIndex = parseInt(st.nextToken());
                Log.d("yes_index", Integer.toString(yesIndex));
                int noIndex = parseInt(st.nextToken());
                Log.d("no_index", Integer.toString(noIndex));
                try {
                    int yesUrgency = parseInt(st.nextToken());
                    Log.d("yes_urgency", Integer.toString(yesUrgency));
                    int noUrgency = parseInt(st.nextToken());
                    Log.d("no_urgency", Integer.toString(noUrgency));
                    boolean[] yesCare = new boolean[Utils.NUM_CARE], noCare = new boolean[Utils.NUM_CARE];
                    try{
                        String yesCare_ = st.nextToken();
                        Log.d("yes care", yesCare_);
                        yesCare = MedicalCertification.makeCareList(yesCare_);
                        String noCare_ = st.nextToken();
                        Log.d("no care", noCare_);
                        noCare = MedicalCertification.makeCareList(noCare_);
                        Log.i("Question", "has been made perfectly");
                    } catch (Exception e) {
                        Log.e("load question", e.toString());
                    }
                    q = new Question(index, text, yesIndex, noIndex, yesUrgency, noUrgency, yesCare, noCare);
                } catch (Exception e){
                    q = new Question(index, text, yesIndex, noIndex);
                }
                questions.add(q);

                Log.d(" question" , q.getQuestion());
            }

            is.close();
        } catch (IOException e) {
            Log.e(InterviewActivity.this.getClass().getSimpleName(), e.toString());
            e.printStackTrace();
        }

        currentQuestion = questions.get(0);
    }

    private void backToQuestion(int index){ //the index given is to quote usedQuestions
        Question q = usedQuestions.remove(index);
        setNextQuestion(q);
        isInterviewDone = false;
    }

    private boolean isAnswered(int index){
        Question question = questions.get(index);

        for (Question q : usedQuestions){
            if (q.getIndex() == index){
                //the question is used
                return true;
            }
        }

        return false;
    }

    private void addUsedQuestion(Question q){
        addUsedQuestion(q, false);
    }

    private void addUsedQuestion(Question q, boolean isAnswered){
        final Question q_ = q;
        usedQuestions.add(q_);
        if (!isAnswered) {
            Record r = new Record(Integer.toString(q_.getIndex()), Utils.getAnswerString(q_));
            Log.d("Unsure Record", r.getTagValue());
    /*
            if(q_.isUnsure){
                r = new Record(Integer.toString(q_.getIndex()), "U");
            } else {
                r = new Record(Integer.toString(q_.getIndex()), Utils.getAnswerString(q_.getAnswer()));
            }
    */
            medicalCertification.updateRecord(r);
        }
        final HistoryButton btn = new HistoryButton(this, q_.getIndex());
        btn.setText(q_);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int x = btn.index;
                int i = 0;
                for (i = 0; i < usedQuestions.size(); i++){
                    if (usedQuestions.get(i).getIndex() == x){
                        Log.d("Interview", usedQuestions.get(i).getQuestion());
                        historyScrollLayout.removeView(btn);
                        break;
                    }
                }
                if (currentQuestion.isAnswered){
                    addUsedQuestion(currentQuestion, true);
                }
                backToQuestion(i);
            }
        });
        historyScrollLayout.addView(btn);
        historyScroll.post(new Runnable() {
            @Override
            public void run() {
                historyScroll.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
            }
        });
    }

    private int getUrgency(){
        int maxUrgency = 0;
        for(int i = 0; i < historyScrollLayout.getChildCount(); i++) {
            HistoryButton hb = (HistoryButton)historyScrollLayout.getChildAt(i);
            maxUrgency = Math.max(maxUrgency, hb.urgency);
        }
        return maxUrgency;
    }

    private boolean[] getCares(){
        Log.d("care", "is required");
        boolean[] cares = new boolean[Utils.NUM_CARE];
        for(int i = 0; i < historyScrollLayout.getChildCount(); i++) {
            HistoryButton hb = (HistoryButton)historyScrollLayout.getChildAt(i);
            Log.d(Integer.toString(i),hb.getCareString());
            for (int j = 0; j < Utils.NUM_CARE; j++) {
                cares[j] = (cares[j] | hb.cares[j]);
            }
        }

        for (int i = 0; i < Utils.NUM_CARE; i++){
            if (cares[i]){
                Log.d("care", i + " is used");
            }
        }

        return cares;
    }

    private void showFinishAlart(){
        isInterviewDone = true;

        int urgency = getUrgency();
        boolean[] cares = getCares();
        Log.d("CERTIFICATION", Integer.toString(urgency));
        final Intent intentCertification = new Intent(this, ResultActivity.class);
        intentCertification.putExtra("URGENCY", urgency);
        intentCertification.putExtra("CARES", cares);
        intentCertification.putExtra("CERTIFICATION", medicalCertification);
        intentCertification.putExtra(Utils.TAG_INTENT_THROUGH_INTERVIEW, IS_THROUGH_INTERVIEW);
        medicalCertification.showRecords("InterviewActivity");
        //interviewData.setListOfQuestions(usedQuestions);

        speechText("問診は終了しました。");
        new AlertDialog.Builder(context).setCancelable(false).setMessage("問診は終了しました").setPositiveButton("次へ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                _handler.removeCallbacksAndMessages(null);
                //makeMedicalCertification();
                startActivity(intentCertification);
                finish();
            }
        }).show();
        _handler = new Handler();
        _handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(intentCertification);
                finish();
            }
        }, 3000);
    }

    private void produceNextQuestion(int viewID){
        try {
            sr.cancel();
        }catch (Exception e){

        }
        int nextIndex = 0;
        boolean answer = false;
        switch(viewID){
            case R.id.btn_yes:
                currentQuestion.isUnsure = false;
                answer = InterviewAnswers.YES;
                break;
            case R.id.btn_no:
                currentQuestion.isUnsure = false;
                answer = InterviewAnswers.NO;
                break;
            case R.id.btn_unsure:
                currentQuestion.isUnsure = true;
                if (currentQuestion.compareUrgency()){
                    answer = InterviewAnswers.YES;
                } else {
                    answer = InterviewAnswers.NO;
                }
                break;
        }
        currentQuestion.answer(answer);

        if(!isInterviewDone) {
            addUsedQuestion(currentQuestion);
        }

        nextIndex = currentQuestion.getNextIndex();
        if (nextIndex >= 0) {
            while(nextIndex >= 0) {
                if (isAnswered(nextIndex)) {
                    Question q = questions.get(nextIndex);
                    if (q.getAnswer()) {
                        nextIndex = q.getYesIndex();
                    } else {
                        nextIndex = q.getNoIndex();
                    }
                } else {
                    break;
                }
            }
            if(nextIndex == -1){
                showFinishAlart();
            } else {
                currentQuestion.isAnswered = true;
                setNextQuestion(questions.get(nextIndex));
            }
        }else {
            showFinishAlart();
        }
    }


    public Drawable getDrawable(String filename){
        AssetManager assetManager = this.getAssets();
        Drawable drawable = null;
        try {
            InputStream is = assetManager.open(filename);
            drawable = Drawable.createFromStream(is, null);
        }catch (Exception e){
            Log.e("Interview get drawable", e.toString());
        }

        return drawable;
    }


    private void setLayout(){

        mBtnYes = (Button) findViewById(R.id.btn_yes);
        mBtnYes.setOnClickListener(interAnsBtnListener);
        mBtnYes.setTextColor(getResources().getColor(R.color.yes));
        mBtnYes.setBackgroundColor(getResources().getColor(R.color.yes_back));

        mBtnNo = (Button) findViewById(R.id.btn_no);
        mBtnNo.setOnClickListener(interAnsBtnListener);
        mBtnNo.setTextColor(getResources().getColor(R.color.no));
        mBtnNo.setBackgroundColor(getResources().getColor(R.color.no_back));

        mBtnUnsure = (Button) findViewById(R.id.btn_unsure);
        mBtnUnsure.setOnClickListener(interAnsBtnListener);
        mBtnUnsure.setTextColor(getResources().getColor(R.color.unsure));
        mBtnUnsure.setBackgroundColor(getResources().getColor(R.color.unsure_back));

        mInterviewContent = (TextView) findViewById(interview);
        mInterviewContent.setTextColor(getResources().getColor(R.color.black));
        imageView = (ImageView)findViewById(R.id.imageview_interview);

        iconFolder = (scenarioID == 0) ? "icons/ill_icon/" : "icons/kega_icon/";
        Log.d("iconFolder" , iconFolder);
        imageView.setImageDrawable(getDrawable(iconFolder + "img00.jpg"));
        showReadQuestion();

        historyScroll = (HorizontalScrollView)findViewById(R.id.history_scroll);
        historyScrollLayout = (LinearLayout) findViewById(R.id.history_scroll_layout);
    }

    private void setSpeechRecognizer(){
        sr = SpeechRecognizer.createSpeechRecognizer(getApplicationContext());
        sr.setRecognitionListener(new SpeechListener());
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getPackageName());
        sr.startListening(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTitle("問診");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interview);
        context = this;

        Intent intent = getIntent();
        scenario = intent.getStringExtra("SCENARIO");
        scenarioID = intent.getIntExtra("SCENARIO_ID", 0);
        Log.d("SCENARIO_ID", Integer.toString(scenarioID));
        dictionary = ListYesNo.getDictionary();

        questions = new ArrayList<>();
        usedQuestions = new ArrayList<>();
        medicalCertification = new MedicalCertification();
        medicalCertification.setScenario(scenarioID);
        scenario = Utils.getScenario(scenarioID);
        Log.d("SCENARIO", scenario + " is chosen");

        loadQuestions();
        setLayout();
        isInterviewDone = false;

        medicalCertification.setLocation();
    }

    View.OnClickListener interAnsBtnListener = new View.OnClickListener(){
        @Override
        public void onClick(View v){
            produceNextQuestion(v.getId());
        }
    };


    static String InputStreamToString(InputStream is) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        br.close();
        return sb.toString();
    }

    @Override
    protected void setTtsListener(){
        // android version more than 15th
        if (Build.VERSION.SDK_INT >= 15)
        {
            int listenerResult = tts.setOnUtteranceProgressListener(new UtteranceProgressListener()
            {

                public void onDone(String utteranceId) {
                    Log.d(TAG,"progress on Done " + utteranceId);
                    InterviewActivity.this.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {

                            if (!isInterviewDone) {
                                Log.d(TAG, "Done listen start");
                                setSpeechRecognizer();
                            }
                        }
                    });
                }

                @Override
                public void onError(String utteranceId) {
                    Log.d(TAG,"progress on Error " + utteranceId);
                }

                @Override
                public void onStart(String utteranceId) {
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
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            new AlertDialog.Builder(InterviewActivity.this)
                    .setTitle("終了")
                    .setMessage("問診を終了しますか")
                    .setNegativeButton("はい", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try{
                                sr.cancel();
                            } catch (Exception e){

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
