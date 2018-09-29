package com.example.haruka.rescue_aid.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.haruka.rescue_aid.R;

public class SettingActivity extends AppCompatActivity {

    SharedPreferences sharedPref;
    TextView readTextView;
    EditText writeEditText;
    Button readButton;
    Button writeButton;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        findViews();
        setListeners();

        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        SharedPreferences pref = getSharedPreferences("preference_test", MODE_PRIVATE);
        int useId = pref.getInt("id", 10001);
        Log.d("preference test id", Integer.toString(useId));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.settings:
                startActivity(new Intent(this, MyPreferenceActivity.class));
                return true;
        }
        return false;
    }

    protected void findViews(){
        readTextView = (TextView)findViewById(R.id.text_setting);
        writeEditText = (EditText)findViewById(R.id.edit_setting);
        readButton = (Button)findViewById(R.id.btn_setting_read);
        writeButton = (Button)findViewById(R.id.btn_setting_write);
    }

    protected void setListeners(){
        readButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                readTextView.setText(sharedPref.getString("data1", "") + sharedPref.getString("data2", ""));
            }
        });

        writeButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("data1", writeEditText.getText().toString() + "hogehoge");
                editor.putString("data2", writeEditText.getText().toString() + "hoo" + writeEditText.getText().toString() + "bar");
                editor.commit();
            }
        });
    }
}
