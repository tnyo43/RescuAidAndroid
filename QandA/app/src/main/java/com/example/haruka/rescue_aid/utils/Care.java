package com.example.haruka.rescue_aid.utils;

import android.graphics.drawable.Drawable;

public class Care {

    public int index;
    public String name;
    public String xml;
    public String description;
    public String buttonText;
    public static final String NULL_XML = "";
    public Drawable drawable;

    public Care(int index, String name, String xml){
        this.index = index;
        this.name = name;
        this.xml = xml;
        this.description = "";
    }

    public void setDescription(String text){
        description = text;
    }


    public void setButtonText(String text){
        buttonText = text;
    }

    // xmlファイルを読み込み
    // 処置名とファイルの対応づけ
    public String getXml(){
        if (xml.equals(NULL_XML)){
            return null;
        } else {
            return xml;
        }
    }

    public void setDrawable(Drawable drawable){
        this.drawable = drawable;
    }

    public Drawable getDrawable(){
        return drawable;
    }
}
