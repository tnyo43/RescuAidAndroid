package com.example.haruka.rescue_aid.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.haruka.rescue_aid.R;
import com.example.haruka.rescue_aid.utils.Care;
import com.example.haruka.rescue_aid.utils.CareList;
import com.example.haruka.rescue_aid.utils.MedicalCertification;
import com.example.haruka.rescue_aid.utils.Utils;
import com.example.haruka.rescue_aid.views.CareListAdapter;

import java.util.ArrayList;

public class CareChooseActivity extends OptionActivity {

    ArrayList<Care> cares;

    private void loadCare(){
        CareList careList = new CareList(this);
        cares = CareList.careList;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        setTitle("応急手当");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_care);
        ListView listView = (ListView)findViewById(R.id.listview_carelist);


        loadCare();

        ArrayList<Care> cares1 = (ArrayList<Care>)cares.clone();

        //TODO テスト用、必要ないデータはそもそも消すべき
        cares1.remove(7);
        cares1.remove(7);
        cares1.remove(7);
        cares1.remove(0);
        cares1.remove(0);

        CareListAdapter careListAdapter = new CareListAdapter(this);
        careListAdapter.setCareList(cares1);
        listView.setAdapter(careListAdapter);

        final Intent intent = new Intent(this, ExplainActivity.class);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Care care = cares.get(position+2);
                intent.putExtra("CARE_XML", care.getXml());
                startActivity(intent);
            }
        });


        try{
            medicalCertification = (MedicalCertification)getIntent().getSerializableExtra(Utils.TAG_INTENT_CERTIFICATION);
        } catch (Exception e){

        }
    }
}
