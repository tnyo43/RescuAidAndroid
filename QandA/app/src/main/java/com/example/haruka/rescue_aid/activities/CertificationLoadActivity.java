package com.example.haruka.rescue_aid.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.example.haruka.rescue_aid.R;
import com.example.haruka.rescue_aid.utils.MedicalCertification;
import com.example.haruka.rescue_aid.utils.TempDataUtil;
import com.example.haruka.rescue_aid.utils.Utils;
import com.example.haruka.rescue_aid.views.CertificationAdapter;

import java.util.ArrayList;
import java.util.Collections;

public class CertificationLoadActivity extends OptionActivity {

    Context contextLoadDataActivity;
    ListView listView;
    ArrayAdapter<MedicalCertification> arrayAdapter;

    public void setListView(){
        String[] filenames = fileList();
        final ArrayList<MedicalCertification> medicalCertifications = new ArrayList<>();
        Log.d("certification ", String.valueOf(filenames.length));
        for (int i = 0; i < filenames.length; i++){
            final String filename = filenames[i];
            if (filename.equals("instant-run")) continue;
            Log.d("certification filename", filename);
            MedicalCertification medicalCertification = TempDataUtil.load(this, filename);
            medicalCertifications.add(medicalCertification);
            Log.d("certification ", String.valueOf(medicalCertifications.size()));
            Log.d("certification number", String.valueOf(medicalCertification.name));

        }
        Collections.sort(medicalCertifications);

        CertificationAdapter certificationAdapter = new CertificationAdapter(CertificationLoadActivity.this);

        certificationAdapter.setMedicalCertification(medicalCertifications);
        listView.setAdapter(certificationAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                MedicalCertification medicalCertification = medicalCertifications.get(position);
                Intent intent = new Intent(contextLoadDataActivity, CertificationEditActivity.class);
                intent.putExtra(Utils.TAG_INTENT_CERTIFICATION, medicalCertification);
                startActivity(intent);

            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                final EditText editView = new EditText(CertificationLoadActivity.this);
                final String[] items = {"名前変更", "削除"};
                new AlertDialog.Builder(contextLoadDataActivity)
                        .setTitle(medicalCertifications.get(position).name)
                        .setItems(items, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d("which", Integer.toString(which));
                                if (which == 0){
                                    new AlertDialog.Builder(contextLoadDataActivity)
                                            .setTitle("ファイル名を変更する")
                                            .setView(editView)
                                            .setNegativeButton("決定", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int whichButton) {
                                                    MedicalCertification medicalCertification = medicalCertifications.get(position);
                                                    medicalCertification.name = editView.getText().toString();
                                                    medicalCertification.save(contextLoadDataActivity);

                                                    Toast.makeText(contextLoadDataActivity,
                                                            "ファイル名を\"" + medicalCertification.name + "\"に変更しました",
                                                            Toast.LENGTH_LONG).show();
                                                }
                                            })
                                            .setPositiveButton("キャンセル", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int whichButton) {
                                                }
                                            })
                                            .show();
                                    editView.setText(medicalCertifications.get(position).name);
                                    editView.setSelection(editView.getText().length());
                                } else {
                                    new AlertDialog.Builder(contextLoadDataActivity).setMessage("この問診データを消去しますか").setPositiveButton("はい", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            MedicalCertification medicalCertification = medicalCertifications.get(position);
                                            deleteFile(medicalCertification.FILENAME);
                                            setListView();
                                        }
                                    }).show();
                                }
                            }
                        })
                        .show();

                return true;
            }
        });
    }

    @Override
    protected void onCreate(Bundle bundle){
        setTitle("問診履歴");
        super.onCreate(bundle);
        contextLoadDataActivity = this;

        setContentView(R.layout.activity_load_certification);
        listView = (ListView)findViewById(R.id.listview_certification);
    }

    @Override
    protected void onResume(){
        super.onResume();
        medicalCertification = null;
        setListView();

        volume = new boolean[ALL_DELETE_COMMAND.length];
    }

    void switchNext(boolean v){
        for(int i = 7; i > 0; i--){
            volume[i] = volume[i-1];
        }
        volume[0] = v;
    }

    void printBoolean(boolean[] volume){
        String v = "";
        for(boolean b : volume){
            v += b ? "T":"F";
        }
        Log.d("printVolume", v);
    }

    boolean compareCommand(){
        for(int i = 0; i < volume.length; i++){
            if(volume[i] != ALL_DELETE_COMMAND[i]){
                return false;
            }
        }
        return true;
    }

    private void deleteAll(){
        new AlertDialog.Builder(contextLoadDataActivity).setMessage("全ての問診データを消去しますか").setPositiveButton("はい", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String[] filenames = fileList();
                for(String filename : filenames) {
                    deleteFile(filename);
                }
                setListView();
            }
        }).show();
    }

    private boolean[] volume;
    final private boolean[] ALL_DELETE_COMMAND = {true, false, false, true, false, false, true, true};

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_VOLUME_UP) {
            switchNext(true);
            if (compareCommand()){
                deleteAll();
                Log.d("volume command", "completeAllDelete");
            }
            Log.d("Volume up", "done");
            printBoolean(volume);
            printBoolean(ALL_DELETE_COMMAND);
        } else if(keyCode==KeyEvent.KEYCODE_VOLUME_DOWN){
            switchNext(false);
            if (compareCommand()){
                deleteAll();
                Log.d("volume command", "completeAllDelete");
            }
            Log.d("Volume down", "done");
            printBoolean(volume);
            printBoolean(ALL_DELETE_COMMAND);
        } else if (keyCode==KeyEvent.KEYCODE_BACK){
            finish();
        }
        return false;
    }
}
