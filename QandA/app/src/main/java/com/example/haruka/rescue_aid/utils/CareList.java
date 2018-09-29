package com.example.haruka.rescue_aid.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;


public class CareList {
    //FIXME stringにおきたいけど、そうするとfinalにならないので対処法
    private final String _carelist = "care/carelist_v00.csv";
    private AssetManager assetManager;
    public static ArrayList<Care> careList;
    public static int CARE_NUM;

    public CareList(Context context){
        assetManager = context.getResources().getAssets();
        careList = new ArrayList<>();

        try {
            InputStream is = assetManager.open(_carelist);
            InputStreamReader inputStreamReader = new InputStreamReader(is);
            BufferedReader bufferReader = new BufferedReader(inputStreamReader);

            String line = "";
            while ((line = bufferReader.readLine()) != null) {
                StringTokenizer st = new StringTokenizer(line, ",");
                Log.i("Carelist", "String Tokenizer is made : " + line);

                String _index = st.nextToken();
                int index = Integer.parseInt(_index);
                String name = st.nextToken();
                String xml = "";
                try {
                    xml = st.nextToken();
                } catch (NoSuchElementException ne) {
                    Log.e("Carelist", ne.toString());
                    xml = Care.NULL_XML;
                }
                Log.d("Carelist", xml);

                String description = st.nextToken();
                String buttonText = st.nextToken();

                Care c = new Care(index, name, xml);
                c.setDescription(description);
                c.setButtonText(buttonText);
                careList.add(c);

                try {
                    String drawableFilename = st.nextToken();
                    Log.d("drawable filename", drawableFilename);
                    Drawable drawable = Drawable.createFromStream(assetManager.open(drawableFilename.trim()), null);
                    c.setDrawable(drawable);
                } catch (Exception e){
                    Drawable drawable = Drawable.createFromStream(assetManager.open("icon.png"),null);
                    c.setDrawable(drawable);
                    Log.e("creating Carelist", e.toString());
                }
            }

            is.close();
        } catch (Exception e){
            Log.e("Carelist", e.toString());
            careList = new ArrayList<>();
        }

        CARE_NUM = careList.size();
    }

    public static Care getCare(int index){
        return careList.get(index);
    }

    public void showCareList(){
        for(Care c : careList){
            Log.d("care" + Integer.toString(c.index), c.name + ", " + c.xml);
        }
    }
}
