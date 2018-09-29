package com.example.haruka.rescue_aid.activities;

import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.haruka.rescue_aid.R;
import com.example.haruka.rescue_aid.utils.Care;
import com.example.haruka.rescue_aid.utils.MedicalCertification;
import com.example.haruka.rescue_aid.utils.QADateFormat;
import com.example.haruka.rescue_aid.utils.Question;
import com.example.haruka.rescue_aid.utils.Record;
import com.example.haruka.rescue_aid.utils.Utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.StringTokenizer;

import static java.lang.Integer.parseInt;

public class CertificationActivity extends LocationActivity {

    ArrayList<Record> interviewRecords;
    ArrayList<Record> careRecords;

    ArrayList<Care> cares;
    ArrayList<Question> questions;

    boolean throughInterview;

    private void loadCare(){
        AssetManager assetManager = getResources().getAssets();

        cares = new ArrayList<>();
        try{
            //InputStream is = assetManager.open("scenarios/" + scenario);
            String _careList = Utils.LIST_CARE;
            String careList = "care/" + _careList;
            Log.d("Care", careList);
            InputStream is = assetManager.open(careList);
            InputStreamReader inputStreamReader = new InputStreamReader(is);
            BufferedReader bufferReader = new BufferedReader(inputStreamReader);
            String line = "";
            int _i = 0;
            while ((line = bufferReader.readLine()) != null) {
                StringTokenizer st = new StringTokenizer(line, ",");
                Log.d("scenario line", line);
                _i++;
                String id = st.nextToken();
                if(id == "id") continue;
                int index = parseInt(id);
                String name = st.nextToken();
                Log.d("text", name);
                Care c = new Care(index, name, "");
                cares.add(c);
            }
            is.close();
        } catch (IOException e) {
            Log.e(CertificationActivity.this.getClass().getSimpleName(), e.toString());
            e.printStackTrace();
        }
    }

    private void loadQuestions(int scenarioID){
        AssetManager assetManager = getResources().getAssets();
        questions = new ArrayList<>();
        try{
            //InputStream is = assetManager.open("scenarios/" + scenario);
            String scenario = Utils.getScenario(scenarioID);
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

                    q = new Question(index, text, yesIndex, noIndex);
                } catch (Exception e){
                    q = new Question(index, text, yesIndex, noIndex);
                }
                questions.add(q);

                Log.d(" question" , q.getQuestion());
            }

            is.close();
        } catch (IOException e) {
            Log.e(CertificationActivity.this.getClass().getSimpleName(), e.toString());
            e.printStackTrace();
        }
    }

    public void setCertification(){
        interviewRecords = new ArrayList<>();
        careRecords = new ArrayList<>();

        for (int i = 0; i < medicalCertification.records.size(); i++){

            Record r = medicalCertification.records.get(i);
            try{
                //Integer.parseInt(r.getTag());
                int index = parseInt(r.getTag());
                Question question = questions.get(index);
                boolean _ans = r.getValue().equals(Utils.ANSWER_SHORT_YES);
                String answer = Utils.getAnswerString(r.getValue());
                //String answer = _ans ? Utils.ANSWER_JP_YES : Utils.ANSWER_JP_NO;
                interviewRecords.add(new Record(question.getQuestion(), answer));
            } catch (Exception e){
                if (r.getTag().equals(Utils.TAG_CARE)){
                    try{
                        Date start = QADateFormat.getDate(medicalCertification.records.get(0).getTime());
                        Date end = QADateFormat.getDate(r.getTime());
                        Log.d("carerecord", "start");
                        int careIndex = parseInt(r.getValue());
                        Care c = cares.get(careIndex);
                        String careTitle = c.name;
                        String time = Long.toString((end.getTime() - start.getTime())/1000);
                        careRecords.add(new Record(careTitle, time));

                    } catch (Exception e1){
                        Log.e("carerecord", e1.toString());
                    }
                } else if (r.getTag().equals(Utils.TAG_END)){

                }
            }
        }

    }

    private void setTable(){

        TableLayout tableLayout = (TableLayout)findViewById(R.id.tablelayout_certification1);
        if (interviewRecords.size() == 0){
            TableRow tableRow = new TableRow(this);

            TextView textView = new TextView(this);
            textView.setText("　ー　");
            textView.setGravity(Gravity.CENTER_HORIZONTAL);
            textView.setTextColor(getResources().getColor(R.color.black));
            textView.setLayoutParams(new TableRow.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    10));
            textView.setBackgroundResource(R.color.default_background);
            tableRow.addView(textView);
            ViewGroup.LayoutParams lp = textView.getLayoutParams();
            ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams)lp;
            mlp.setMargins(2, 2, 1, 2);
            textView.setLayoutParams(mlp);

            textView = new TextView(this);
            textView.setText("　ー　");
            textView.setGravity(Gravity.CENTER_HORIZONTAL);
            textView.setTextColor(getResources().getColor(R.color.black));
            textView.setLayoutParams(new TableRow.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    3));
            textView.setBackgroundResource(R.color.default_background);
            tableRow.addView(textView);
            lp = textView.getLayoutParams();
            mlp = (ViewGroup.MarginLayoutParams)lp;
            mlp.setMargins(1, 2, 2, 2);
            textView.setLayoutParams(mlp);


            tableLayout.addView(tableRow);
        }
        for (int i = 0; i < interviewRecords.size(); i++){
            Record record = interviewRecords.get(i);
            TableRow tableRow = new TableRow(this);
            TextView textView = new TextView(this);
            String text = record.getTag();
            if (text.length() > 15){
                String _text = text;
                text = "";
                int j = 0;
                while(_text.length() > 15*j) {
                    String sub = _text.substring(j*15, Math.min((j+1)*15, _text.length()));
                    Log.d("textview text", sub);
                    text += "　" + sub + "\n";
                    j++;
                }
                text = text.substring(0, text.length()-1);
            } else {
                text = "　" + text;
            }
            textView.setText(text);
            textView.setTextColor(getResources().getColor(R.color.black));
            textView.setLayoutParams(new TableRow.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    10));
            textView.setBackgroundResource(R.color.default_background);
            ViewGroup.LayoutParams lp = textView.getLayoutParams();
            ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams)lp;
            if(i == 0) {
                mlp.setMargins(2, 2, 1, 1);
            } else if (i == interviewRecords.size()-1){
                mlp.setMargins(2, 1, 1, 2);
            } else {
                mlp.setMargins(2, 1, 1, 1);
            }
            textView.setLayoutParams(mlp);
            tableRow.addView(textView);

            textView = new TextView(this);
            textView.setText("　" + record.getValue());
            if (record.getValue().equals(Utils.ANSWER_JP_YES)){
                textView.setTextColor(getResources().getColor(R.color.yes));
            } else if (record.getValue().equals(Utils.ANSWER_JP_NO)){
                textView.setTextColor(getResources().getColor(R.color.no));
            } else {
                textView.setTextColor(getResources().getColor(R.color.unsure));
            }
            textView.setLayoutParams(new TableRow.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    3));
            textView.setBackgroundResource(R.color.default_background);
            lp = textView.getLayoutParams();
            mlp = (ViewGroup.MarginLayoutParams)lp;
            if(i == 0) {
                mlp.setMargins(1, 2, 2, 1);
            } else if (i == interviewRecords.size()-1){
                mlp.setMargins(1, 1, 2, 2);
            } else {
                mlp.setMargins(1, 1, 2, 1);
            }
            tableRow.addView(textView);
            tableLayout.addView(tableRow);
        }

        tableLayout = (TableLayout)findViewById(R.id.tablelayout_certification2);
        if (careRecords.size() == 0){
            TableRow tableRow = new TableRow(this);

            TextView textView = new TextView(this);
            textView.setText("　ー　");
            textView.setGravity(Gravity.CENTER_HORIZONTAL);
            textView.setTextColor(getResources().getColor(R.color.black));
            textView.setLayoutParams(new TableRow.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    10));
            textView.setBackgroundResource(R.color.default_background);
            tableRow.addView(textView);
            ViewGroup.LayoutParams lp = textView.getLayoutParams();
            ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams)lp;
            mlp.setMargins(2, 2, 1, 2);
            textView.setLayoutParams(mlp);

            textView = new TextView(this);
            textView.setText("　ー　");
            textView.setGravity(Gravity.CENTER_HORIZONTAL);
            textView.setTextColor(getResources().getColor(R.color.black));
            textView.setLayoutParams(new TableRow.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    3));
            textView.setBackgroundResource(R.color.default_background);
            tableRow.addView(textView);
            lp = textView.getLayoutParams();
            mlp = (ViewGroup.MarginLayoutParams)lp;
            mlp.setMargins(1, 2, 2, 2);
            textView.setLayoutParams(mlp);


            tableLayout.addView(tableRow);
        }
        for (int i = 0; i < careRecords.size(); i++){
            Record record = careRecords.get(i);
            TableRow tableRow = new TableRow(this);
            TextView textView = new TextView(this);
            String text = record.getTag();
            if (text.length() > 15){
                String _text = text;
                text = "";
                int j = 0;
                while(_text.length() > 15*j) {
                    String sub = _text.substring(j*15, Math.min((j+1)*15, _text.length()));
                    Log.d("textview text", sub);
                    text += "　" + sub + "\n";
                    j++;
                }
                text = text.substring(0, text.length()-1);
            } else {
                text = "　" + text;
            }
            textView.setText(text);
            textView.setTextColor(getResources().getColor(R.color.black));
            textView.setLayoutParams(new TableRow.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    10));
            textView.setBackgroundResource(R.color.default_background);
            ViewGroup.LayoutParams lp = textView.getLayoutParams();
            ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams)lp;
            if (careRecords.size() == 1){
                mlp.setMargins(2, 2, 1, 2);
            } else if(i == 0) {
                mlp.setMargins(2, 2, 1, 1);
            } else if (i == careRecords.size()-1){
                mlp.setMargins(2, 1, 1, 2);
            } else {
                mlp.setMargins(2, 1, 1, 1);
            }
            textView.setLayoutParams(mlp);
            tableRow.addView(textView);

            textView = new TextView(this);
            textView.setText("　" + record.getValue()+"秒　");
            textView.setTextColor(getResources().getColor(R.color.black));
            textView.setLayoutParams(new TableRow.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    3));
            textView.setGravity(Gravity.RIGHT);
            textView.setBackgroundResource(R.color.default_background);
            lp = textView.getLayoutParams();
            mlp = (ViewGroup.MarginLayoutParams)lp;
            if (careRecords.size() == 1){
                mlp.setMargins(1, 2, 2, 2);
            } else if (i == 0) {
                mlp.setMargins(1, 2, 2, 1);
            } else if (i == interviewRecords.size()-1){
                mlp.setMargins(1, 1, 2, 2);
            } else {
                mlp.setMargins(1, 1, 2, 1);
            }
            tableRow.addView(textView);
            tableLayout.addView(tableRow);
        }

    }

    private void setDate(){
        TextView dateTextView = (TextView)findViewById(R.id.textview_date_certification);
        dateTextView.setText(medicalCertification.getStartAtJap() + " 開始\n" + medicalCertification.getEndAtJap() + " 発行");
    }

    private void setAddress(){
        TextView addressTextView = (TextView)findViewById(R.id.textview_address_dms_certification);
        String address = medicalCertification.getCallNoteAddress();
        if (address.equals("")){
            address = " 　ー　";
        }
        addressTextView.setText("場所：" + address.substring(1));
    }

    private void setButton(){
        Button QRButton, resultButton;
        QRButton = (Button)findViewById(R.id.btn_certification_qr);
        resultButton = (Button)findViewById(R.id.btn_certification_result);

        //QRButton.setText(getString(R.string.gotoQR));
        QRButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                Intent intent = new Intent(CertificationActivity.this, QRDisplayActivity.class);
                intent.putExtra(Utils.TAG_INTENT_CERTIFICATION, medicalCertification);
                intent.putExtra(Utils.TAG_INTENT_THROUGH_INTERVIEW, throughInterview);
                startActivity(intent);
                finish();
            }
        });
        //resultButton.setText(getString(R.string.gotoResult));
        resultButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                Intent intent = new Intent(CertificationActivity.this, ResultActivity.class);
                intent.putExtra(Utils.TAG_INTENT_CERTIFICATION, medicalCertification);
                intent.putExtra(Utils.TAG_INTENT_THROUGH_INTERVIEW, throughInterview);
                startActivity(intent);
                finish();
            }
        });
    }


    @Override
    protected void onCreate(Bundle bundle){
        super.onCreate(bundle);
        setContentView(R.layout.layout_certification);
        setTitle("診断書");

        medicalCertification = (MedicalCertification)getIntent().getSerializableExtra(Utils.TAG_INTENT_CERTIFICATION);
        throughInterview = getIntent().getBooleanExtra(Utils.TAG_INTENT_THROUGH_INTERVIEW, false);

        loadCare();
        loadQuestions(medicalCertification.getScenarioID());
        setCertification();

        setTable();
        setDate();
        setAddress();
        setButton();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            finish();
            /*
            if (throughInterview) {
                new AlertDialog.Builder(CertificationActivity.this)
                        .setTitle("終了")
                        .setMessage("タイトルに戻りますか")
                        .setNegativeButton("はい", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(CertificationActivity.this, TitleActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        })
                        .setPositiveButton("いいえ", null)
                        .show();
            } else {
                finish();
            }
            */
            return true;
        }
        return false;
    }

}
