package com.example.haruka.rescue_aid.activities;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.XmlResourceParser;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.haruka.rescue_aid.R;
import com.example.haruka.rescue_aid.utils.Care;
import com.example.haruka.rescue_aid.utils.CareList;
import com.example.haruka.rescue_aid.utils.MedicalCertification;
import com.example.haruka.rescue_aid.utils.Question;
import com.example.haruka.rescue_aid.utils.Record;
import com.example.haruka.rescue_aid.utils.Utils;
import com.example.haruka.rescue_aid.views.CareAdapter;
import com.example.haruka.rescue_aid.views.CareListView;

import org.xmlpull.v1.XmlPullParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.StringTokenizer;

import static java.lang.Integer.parseInt;

public class ResultActivity extends LocationActivity {

    private static final int SUB_ACTIVITY = 1001;
    private static final int DIALOG_ID_LOAD = 1;
    private int urgency;
    private ArrayList<Care> cares;
    private ArrayList<Question> questions;
    TextView textView;
    Button qrBtn, certificationBtn;
    CareList careList;

    private boolean throughInterview;

    private void startCare(Care care){
        String tag = care.name;
        // TODO stringにおきたいけど多くなりすぎそうなので対処を考える
        if (tag.equals("119番通報")){
            call119();
        } else if (tag.equals("AED使用")) {
            showAEDmap();
        } else if (tag.equals("医師の診察を")){
            searchHospital();
        } else if (tag.equals("救急相談センターに発信")){
            call7119();
        } else {
            Intent intent = new Intent(this, ExplainActivity.class);
            intent.putExtra("CERTIFICATION", medicalCertification);
            intent.putExtra("CARE_XML", care.getXml());
            startActivityForResult(intent, SUB_ACTIVITY);
        }
    }

    private void setListView(){
        CareListView listView = (CareListView)findViewById(R.id.listview_care);
        CareAdapter careAdapter = new CareAdapter(this);
        Log.d("set listview", Integer.toString(cares.size()));

        // 0番目の手当がある時、すでにある処置だけで補完できている
        boolean has0 = false;
        for (Care care : cares){
            Log.d("cares index", Integer.toString(care.index));
            if(care.index == 0){
                has0 = true;
                break;
            }
        }
        if(!has0){
            //FIXME 別のキーワードで処置を分ける方がいい
            if(urgency == 1) {
                cares.add(0, CareList.getCare(7));
            } else {
                cares.add(0, CareList.getCare(8));
            }
            cares.add(1, CareList.getCare(9));
        }

        careAdapter.setCareList(cares);

        LinearLayout linearLayout = (LinearLayout)findViewById(R.id.linearlayout_result);
        if (urgency == 1 || urgency == 0) {
            linearLayout.setBackgroundResource(R.drawable.frame_listview1);
        } else if (urgency == 2) {
            linearLayout.setBackgroundResource(R.drawable.frame_listview2);
        } else if (urgency == 3) {
            linearLayout.setBackgroundResource(R.drawable.frame_listview3);
        }

        listView.setAdapter(careAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("adapter", Integer.toString(position));
                Care care = cares.get(position);
                switch(view.getId()){
                    case R.id.btn_explain_care:
                        Toast.makeText(ResultActivity.this, care.name , Toast.LENGTH_SHORT).show();
                        startCare(care);
                        break;
                }
            }
        });
    }

    private void setTextView(){
        textView = (TextView)findViewById(R.id.textview_notice_result); //

        if (urgency != 0) {
            textView.setTextColor(getResources().getColor(Utils.URGENCY_COLORS[urgency], null));
            textView.setText(Utils.URGENCY_WARNING[urgency]);
        } else {
            textView.setTextColor(getResources().getColor(Utils.URGENCY_COLORS[1], null));
            textView.setText("問診は行っていません");
        }
    }

    // 下のボタン群の設定
    private void setDealingBtn(){
        qrBtn = (Button)findViewById(R.id.btn_result_qr_display);
        qrBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog = new ProgressDialog(ResultActivity.this);
                progressDialog.setMessage("しばらくお待ち下さい");
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.setCancelable(false);
                progressDialog.show();
                Intent intent = new Intent(ResultActivity.this, QRDisplayActivity.class);
                intent.putExtra(Utils.TAG_INTENT_CERTIFICATION, medicalCertification);
                intent.putExtra(Utils.TAG_INTENT_THROUGH_INTERVIEW, throughInterview);
                startActivity(intent);
                if (!throughInterview) {
                    finish();
                }
            }
        });

        certificationBtn = (Button)findViewById(R.id.btn_result_certification);
        certificationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog = new ProgressDialog(ResultActivity.this);
                progressDialog.setMessage("しばらくお待ち下さい");
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.setCancelable(false);
                progressDialog.show();
                Intent intent = new Intent(ResultActivity.this, CertificationActivity.class);
                intent.putExtra(Utils.TAG_INTENT_CERTIFICATION, medicalCertification);
                intent.putExtra(Utils.TAG_INTENT_THROUGH_INTERVIEW, throughInterview);
                startActivity(intent);
                if (!throughInterview) {
                    finish();
                }
            }
        });
    }

    private void loadQuestions(){
        questions = new ArrayList<>();
        AssetManager assetManager = getResources().getAssets();
        try{
            String scenario = Utils.getScenario(medicalCertification.getScenarioID());
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
            Log.e(ResultActivity.this.getClass().getSimpleName(), e.toString());
            e.printStackTrace();
        }
    }

    public String getCareString(boolean[] care_boolean){
        if (care_boolean == null){
            //TODO get care_boolean by MedicalCertification
            care_boolean = new boolean[7];
        }

        cares = new ArrayList<>();
        String s = "";
        for (int i = 0; i < care_boolean.length; i++){
            if (care_boolean[i]){
                s += "Y";
                Care c = CareList.getCare(i);

                cares.add(c);
            } else {
                s += "N";
            }
        }

        return s;
    }

    private void analyzeCertification(){
        urgency = 0;
        medicalCertification.showRecords("ResultActivity");

        boolean[] cares_flag = new boolean[Utils.NUM_CARE];
        for (Record record : medicalCertification.records){
            try{
                int questionIndex = Integer.parseInt(record.getTag());
                boolean answer = Utils.getAnswerBoolean(record.getValue());
                boolean unsure = Utils.getUnsureBoolean(record.getValue());
                Question q = questions.get(questionIndex);
                q.answer(answer);
                q.isUnsure = unsure;
                boolean[] q_care = q.getCares();
                for (int i = 0; i < q_care.length; i++){
                    cares_flag[i] = cares_flag[i] | q_care[i];
                }
                urgency = Math.max(q.getUrgency(), urgency);
                Log.d("Result", "i" + Integer.toString(questionIndex) + ", a" + Boolean.toString(answer) + ", u" +  Integer.toString(urgency) + "m " +record.getTagValue());
            } catch (Exception e){

            }
        }
        getCareString(cares_flag);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTitle("問診結果");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        careList = new CareList(this);
        careList.showCareList();

        try {
            medicalCertification = (MedicalCertification) getIntent().getSerializableExtra(Utils.TAG_INTENT_CERTIFICATION);
            medicalCertification.save(this);
        } catch (Exception e) {
            medicalCertification = new MedicalCertification();
        }
        throughInterview = getIntent().getBooleanExtra(Utils.TAG_INTENT_THROUGH_INTERVIEW, false);
        boolean[] cares_flag = getIntent().getBooleanArrayExtra("CARES");
        String careString = getCareString(cares_flag);
        Log.d("CARES", careString);
        for (Care c : this.cares){
            Log.d("care required" , c.name);
        }

        loadQuestions();
        analyzeCertification();

        setListView();
        setTextView();
        setDealingBtn();

        //setDrawerLayout();

        if (throughInterview){
            if(urgency == 3){
                speechText("周りの人に協力を求め、応急手当を行ってください");
            }
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            if (throughInterview) {
                //TODO implement behavior when back key is pushed on ResultActivity from history
                new AlertDialog.Builder(ResultActivity.this)
                        .setTitle("終了")
                        .setMessage("タイトルに戻りますか")
                        .setNegativeButton("はい", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(ResultActivity.this, TitleActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        })
                        .setPositiveButton("いいえ", null)
                        .show();
            } else {
                finish();
            }
            return true;
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("medicalcertification", "request");
        if(requestCode == SUB_ACTIVITY) {
            Log.d("medicalcertification", "valid code");
            if(resultCode == RESULT_OK) {
                Log.d("medicalCertification", "is got");
                medicalCertification = (MedicalCertification) data.getSerializableExtra("CERTIFICATION");
            }
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        try {
            progressDialog.cancel();
        } catch (Exception e){
        }
    }
}
