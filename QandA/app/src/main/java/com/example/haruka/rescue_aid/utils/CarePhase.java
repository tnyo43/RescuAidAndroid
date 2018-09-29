package com.example.haruka.rescue_aid.utils;

import android.graphics.drawable.Drawable;

/**
 * 処置の画像と文字とボタンだけを持つ
 * 後の処理はExplainCareに任せる
 */
public class CarePhase {

    int id;

    public String text;
    public String button;
    public String button2;
    public Drawable drawable;
    public int duration;

    public CarePhase(){
        id = 0;
        button = "";
    }

    public String getText(){
        return text;
    }

    public Drawable getImage(){
        return drawable;
    }
}