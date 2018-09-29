package com.example.haruka.rescue_aid.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.haruka.rescue_aid.R;
import com.example.haruka.rescue_aid.utils.MedicalCertification;
import com.example.haruka.rescue_aid.utils.TempDataUtil;
import com.example.haruka.rescue_aid.utils.Utils;

public class CertificationEditActivity extends OptionActivity {

    Button button1, button2, button3, button4;
    Intent intent1, intent2, intent3;
    EditText filenameEditText;
    Button filenameButton;
    Context context;
    InputMethodManager inputMethodManager;
    private LinearLayout linearLayout;

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        inputMethodManager.hideSoftInputFromWindow(linearLayout.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        linearLayout.requestFocus();

        return true;
    }

    @Override
    protected void onCreate(Bundle bundle){
        setTitle("問診履歴");
        super.onCreate(bundle);
        setContentView(R.layout.activity_edit_certification);
        medicalCertification = (MedicalCertification)getIntent().getSerializableExtra(Utils.TAG_INTENT_CERTIFICATION);

        button1 = (Button)findViewById(R.id.btn_edit_qr);
        button2 = (Button)findViewById(R.id.btn_edit_show);
        button3 = (Button)findViewById(R.id.btn_edit_result);
        button4 = (Button)findViewById(R.id.btn_edit_delete);
        filenameEditText = (EditText)findViewById(R.id.edit_edit_filename);
        filenameButton = (Button)findViewById(R.id.btn_edit_filename);

        linearLayout = (LinearLayout)findViewById(R.id.linearlayout_edit_certification);
        inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);

        filenameEditText.setText(medicalCertification.name);
        filenameEditText.setSelection(filenameEditText.getText().length());
        filenameEditText.clearFocus();

        intent1 = new Intent(this, QRDisplayActivity.class);
        intent2 = new Intent(this, CertificationActivity.class);
        intent3 = new Intent(this, ResultActivity.class);

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                intent1.putExtra(Utils.TAG_INTENT_CERTIFICATION, medicalCertification);
                startActivity(intent1);
            }
        });
        //button1.setText(getString(R.string.gotoQR));
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                intent2.putExtra(Utils.TAG_INTENT_CERTIFICATION, medicalCertification);
                startActivity(intent2);
            }
        });
        //button2.setText(getString(R.string.gotoCertification));
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                intent3.putExtra(Utils.TAG_INTENT_CERTIFICATION, medicalCertification);
                startActivity(intent3);
            }
        });
        //button3.setText(getString(R.string.gotoResult));
        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(CertificationEditActivity.this)
                        .setMessage("このデータを消去します\nよろしいですか")
                        .setPositiveButton("いいえ", null)
                        .setNegativeButton("はい", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteFile(medicalCertification.FILENAME);
                                finish();
                            }})
                        .show();
            }
        });
        filenameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String filename = filenameEditText.getText().toString();
                medicalCertification.name = filename;
                Log.d("filename", filename);
                TempDataUtil.store(CertificationEditActivity.this, medicalCertification);
                Toast.makeText(context,
                        "ファイル名を\"" + medicalCertification.name + "\"に変更しました",
                        Toast.LENGTH_LONG).show();
                linearLayout.requestFocus();
                inputMethodManager.hideSoftInputFromWindow(linearLayout.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        });

        context = this;

    }

    @Override
    protected void onPause(){
        super.onPause();

        progressDialog.dismiss();
    }
}
