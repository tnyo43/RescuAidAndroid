package com.example.haruka.rescue_aid.recognition_list;

import java.util.ArrayList;


public class ListSymptom {
    // FIXME たぶんvalues.stringsに入れる方がいい？
    // 「急病」に聞こえるリスト
    private static String[] ill = {
            "急病", "9秒", "10秒", "9票", "給料", "ちゅうびょう", "救急病院", "種苗", " 休業", "急病を"
    };
    // 「怪我」に聞こえるリスト
    private static String[] injury = {
            "怪我", "気が", "けが", " ケガ", "ギガ", "ティーガー", "映画", "慶賀", "経過", "兄が", "giga"
    };

    public static ArrayList<String>[] getDictionary(){
        ArrayList<String>[] Dictionay = new ArrayList[2];
        Dictionay[0] = new ArrayList<String>();
        Dictionay[1] = new ArrayList<String>();

        for(String i : ill){
            Dictionay[0].add(i);
        }
        for(String i : injury){
            Dictionay[1].add(i);
        }

        return Dictionay;
    }
}
