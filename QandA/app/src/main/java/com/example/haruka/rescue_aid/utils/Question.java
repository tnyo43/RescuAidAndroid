package com.example.haruka.rescue_aid.utils;

import android.util.Log;

import java.io.Serializable;

public class Question implements Serializable {

    private static final long serialVersionUID = Utils.serialVersionUID_Question;

    public final static int MIN_URGENCY = Utils.MIN_URGNECY;

    // 二分木の構造
    // ノードの番号
    private int index;
    // 子の番号
    private int yesIndex, noIndex;
    // 分岐時の緊急度の付け方
    private int yesUrgency, noUrgency;
    // 分岐時の手当のリスト
    private boolean[] yesCare, noCare;
    private String question;
    private boolean answer;
    public boolean isAnswered;
    public boolean isUnsure;

    public Question(int index, String question, int yesIndex, int noIndex){
        this.index = index;
        this.yesIndex = yesIndex;
        this.noIndex = noIndex;
        this.question = question;
        this.yesUrgency = MIN_URGENCY;
        this.noUrgency = MIN_URGENCY;
        yesCare = new boolean[Utils.NUM_CARE];
        noCare = new boolean[Utils.NUM_CARE];

        answer = false;
        isAnswered = false;
        isUnsure = false;
    }

    public Question(int index, String question, int yesIndex, int noIndex, int yesUrgency, int noUrgency, boolean[] yesCare, boolean[] noCare){
        this.index = index;
        this.yesIndex = yesIndex;
        this.noIndex = noIndex;
        this.question = question;
        this.yesUrgency = yesUrgency; //Math.min(Math.max(yesUrgency, MIN_URGENCY), MAX_URGENCY);
        this.noUrgency = noUrgency; //Math.min(Math.max(noUrgency, MIN_URGENCY), MAX_URGENCY);
        this.yesCare = yesCare;
        this.noCare = noCare;

        answer = false;
        isAnswered = false;
        isUnsure = false;
    }

    public String toString(){
        String res = this.getClass().getSimpleName().toString();
        res += "  , index : " + Integer.toString(index);
        res += "  , text : " + question;
        res += "  , yes index : " + Integer.toString(yesIndex);
        res += "  , no index : " + Integer.toString(noIndex);
        res += "  , yes emergency urgency : " + Integer.toString(yesUrgency);
        res += "  , no emergency urgency : " + Integer.toString(noUrgency);
        return res;
    }

    public int getIndex(){
        return index;
    }

    public int getNextIndex(){
        if (answer == InterviewAnswers.YES){
            return getYesIndex();
        }else{
            return getNoIndex();
        }
    }

    public int getYesIndex(){
        return yesIndex;
    }

    public int getNoIndex(){
        return noIndex;
    }

    public int getYesUrgency(){
        return Math.abs(yesUrgency);
    }

    public int getNoUrgency(){
        return Math.abs(noUrgency);
    }

    // 最大値を返す
    public int getUrgency(){
        Log.d("currentquestionUrgency", "unsure : " + Boolean.toString(isUnsure));
        if(isUnsure){
            Log.d("currentquestionUrgency", getYesUrgency() + ", " + getNoUrgency());
            return Math.max(getYesUrgency(), getNoUrgency());
        } else if (answer){
            return getYesUrgency();
        } else {
            return getNoUrgency();
        }
    }

    //FIXME Compareをオーバーライド
    public boolean compareUrgency(){
        return yesUrgency > noUrgency;
    }

    public String getQuestion(){
        return question;
    }

    public void answer(boolean a){
        answer = a;
    }

    public boolean getAnswer() {
        return answer;
    }

    public String getAnswerString() {
        if (answer){
            return Utils.ANSWER_JP_YES;
        } else {
            return Utils.ANSWER_JP_NO;
        }
    }

    public boolean[] getCares(){
        if (answer){
            return yesCare;
        } else {
            return noCare;
        }
    }
}
