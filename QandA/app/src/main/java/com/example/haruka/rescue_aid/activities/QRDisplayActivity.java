package com.example.haruka.rescue_aid.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.haruka.rescue_aid.R;
import com.example.haruka.rescue_aid.utils.MedicalCertification;
import com.example.haruka.rescue_aid.utils.Utils;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

// QRコードを表示
public class QRDisplayActivity extends LocationActivity {

    private Context context;
    private boolean throughInterview;
    private Switch qrSwitch;
    private String qrID;
    private boolean done, require;

    private LoaderCallbacks<Bitmap> callbacks = new LoaderCallbacks<Bitmap>() {
        @Override
        public Loader<Bitmap> onCreateLoader(int id, Bundle bundle) {
            EncodeTaskLoader loader = new EncodeTaskLoader(
                    getApplicationContext(), bundle.getString("contents"));
            loader.forceLoad();
            return loader;
        }

        @Override
        public void onLoaderReset(Loader<Bitmap> loader) {
        }

        @Override
        public void onLoadFinished(Loader<Bitmap> loader, Bitmap bitmap) {
            getSupportLoaderManager().destroyLoader(0);
            if (bitmap == null) {
                Toast.makeText(getApplicationContext(), "Error.", Toast.LENGTH_SHORT).show();
            } else {
                ImageView imageView = (ImageView) findViewById(R.id.result_view);
                imageView.setImageBitmap(bitmap);
                medicalCertification.showRecords();
            }
        }
    };

    private void sendRecord(final int num){
        if (num < 0){
            return;
        }

        // Herokuのアプリケーションにデータを投げる
        (new Thread(new Runnable() {
            @Override
            public void run(){
                try {
                    String buffer = "";
                    HttpURLConnection con = null;
                    URL url = new URL("https://qa-server.herokuapp.com/post");
                    con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("POST");
                    con.setInstanceFollowRedirects(false);
                    con.setRequestProperty("Accept-Language", "jp");
                    con.setDoOutput(true);
                    con.setDoInput(true);
                    con.setRequestProperty("Content-Type", "application/json; charset=utf-8");
                    OutputStream os = con.getOutputStream();
                    PrintStream ps = new PrintStream(os);
                    ps.print(medicalCertification.getJSON());
                    ps.close();
                    Log.d("medicalCertification", medicalCertification.getJSON().toString());
                    BufferedReader reader =
                            new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));

                    buffer = reader.readLine();
                    qrID = buffer;

                    done = true;
                    Log.d("HTTP RESULT", "require is "+ Boolean.toString(require));
                    if (require){
                        qrSwitch.setChecked(true);
                        showQR("https://qa-server.herokuapp.com/certification/" + qrID);
                        ((TextView) findViewById(R.id.textview_qr_mode)).setText("一般のQRコードリーダーで読み取る");
                    }
                    Log.d("medicalcertification", buffer);

                    JSONArray jsonArray = new JSONArray(buffer);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        Log.d("HTTP REQ", jsonObject.getString("name"));
                    }
                    con.disconnect();
                    return;
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (ProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        sendRecord(num-1);
                    }
                }, 500);

            }
        })).start();
    }

    public static class EncodeTaskLoader extends AsyncTaskLoader<Bitmap> {
        private String mContents;

        public EncodeTaskLoader(Context context, String contents) {
            super(context);
            mContents = contents;
        }

        @Override
        public Bitmap loadInBackground() {
            try {
                return encode(mContents);
            } catch (Exception e) {
                return null;
            }
        }

        private Bitmap encode(String contents) throws Exception {
            QRCodeWriter writer = new QRCodeWriter();
            BitMatrix bm = null;
            bm = writer.encode(mContents, BarcodeFormat.QR_CODE, 300, 300);

            int width = bm.getWidth();
            int height = bm.getHeight();
            int[] pixels = new int[width * height];
            for (int y = 0; y < height; y++) {
                int offset = y * width;
                for (int x = 0; x < width; x++) {
                    pixels[offset + x] = bm.get(x, y) ? Color.BLACK : Color.WHITE;
                }
            }
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
            return bitmap;
        }
    }

    private void showQR(String text){
        Bundle bundle = new Bundle();
        bundle.putString("contents", text);
        getSupportLoaderManager().initLoader(0, bundle, callbacks);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTitle("QRコード");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_qr);

        context = this;

        Button b1, b2;
        b1 = (Button)findViewById(R.id.btn_qr_show_certification);
        b1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                Intent intent = new Intent(context, CertificationActivity.class);
                intent.putExtra(Utils.TAG_INTENT_CERTIFICATION, medicalCertification);
                intent.putExtra(Utils.TAG_INTENT_THROUGH_INTERVIEW, throughInterview);
                startActivity(intent);
                finish();
            }
        });
        b2 = (Button)findViewById(R.id.btn_qr_show_result);
        b2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                Intent intent = new Intent(context, ResultActivity.class);
                intent.putExtra(Utils.TAG_INTENT_CERTIFICATION, medicalCertification);
                intent.putExtra(Utils.TAG_INTENT_THROUGH_INTERVIEW, throughInterview);
                startActivity(intent);
                finish();
            }
        });
        qrSwitch = (Switch)findViewById(R.id.switch_qr_mode);
        qrSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(!isChecked){ // 別端末のアプリで表示するよう
                    showQR(medicalCertification.toString());
                    ((TextView)findViewById(R.id.textview_qr_mode)).setText("救&援のQRコードリーダーで読み取る");
                } else { // Webアプリでみるよう
                    if (qrID == null){
                        if (done) {
                            Toast.makeText(QRDisplayActivity.this, "データ転送に失敗しました\n\"救&援\"のQRコードリーダで読み取ってください", Toast.LENGTH_SHORT).show();
                            qrSwitch.setChecked(false);
                        } else {
                            require = true;
                            Toast.makeText(QRDisplayActivity.this, "しばらくお待ち下さい", Toast.LENGTH_SHORT).show();
                            qrSwitch.setChecked(false);
                        }
                    } else {
                        showQR("https://qa-server.herokuapp.com/certification/" + qrID);
                        ((TextView) findViewById(R.id.textview_qr_mode)).setText("一般のQRコードリーダーで読み取る");
                    }
                }
            }
        });


        throughInterview = getIntent().getBooleanExtra("THROUGH_INTERVIEW", false);
        sendRecord(5);
        done = false;
        require = false;
    }

    @Override
    protected void onResume(){
        super.onResume();
        Intent intent = getIntent();
        medicalCertification = (MedicalCertification) intent.getSerializableExtra(Utils.TAG_INTENT_CERTIFICATION);
        String mResult = medicalCertification.toString();
        showQR(mResult);
    }

    @Override
    public Dialog onCreateDialog(int id) {
        switch (id) {

            case 0:
                return new AlertDialog.Builder(QRDisplayActivity.this)
                        .setMessage("スタート画面に戻りますか？\n今回の問診データは\n「タイトル」→「問診履歴」\nから見れます")
                        .setCancelable(false)
                        .setPositiveButton("戻る", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                finish();
                            }
                        })
                        .setNegativeButton("戻らない", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                            }
                        })
                        .create();
        }
        return null;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            finish();
            return true;
        }
        return false;
    }
}