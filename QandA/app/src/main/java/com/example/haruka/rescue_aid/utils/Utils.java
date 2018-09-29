package com.example.haruka.rescue_aid.utils;

import android.graphics.drawable.Drawable;
import android.util.Log;

import com.example.haruka.rescue_aid.R;

public class Utils {
    //FIXME 定数をstringにしたいがstringは呼べない。吉田先生に相談
    public final static String SCENARIOS_ILL = "ill/ill_1003.csv";

    public final static String SCENARIOS_INJURY = "injury/kega_1003.csv";

    public final static String LIST_CARE = "carelist_v00.csv";

    public final static String TAG_CARE = "Care";
    public final static String TAG_END = "END";

    public final static String TAG_INTENT_CERTIFICATION = "CERTIFICATION";
    public final static String TAG_INTENT_THROUGH_INTERVIEW = "THROUGH_INTERVIEW";

    public static final long serialVersionUID_MedicalCertification = 1L;
    public static final long serialVersionUID_Question = 2L;

    public static final String ANSWER_SHORT_YES = "Y";
    public static final String ANSWER_SHORT_NO = "N";
    public static final String ANSWER_SHORT_UNSURE = "U";
    public static final String ANSWER_JP_YES = "はい";
    public static final String ANSWER_JP_NO = "いいえ";
    public static final String ANSWER_JP_UNSURE = "わからない";

    public final static int MIN_URGNECY = 1, MAX_URGENCY = 3;
    public static int[] URGENCY_COLORS = {
            0,
            R.color.urgency1,
            R.color.urgency2,
            R.color.urgency3,
    };
    public static String[] URGENCY_WARNING = {"", "大きな問題はありません", "医療機関の受診が必要です", "緊急度が高いです"};
    public final static int NUM_CARE = 7;

    public static String getAnswerString(Question q){
        String answer = (q.getAnswer()) ? "Y":"N";
        if(q.isUnsure){
            answer += "U";
        }
        return answer;
    }
    public static String getAnswerString(String value){
        if (value.substring(1).equals("U")){
            return ANSWER_JP_UNSURE;
        } else {
            if (value.equals(ANSWER_SHORT_YES)){
                return ANSWER_JP_YES;
            } else {
                return ANSWER_JP_NO;
            }
        }
    }

    public static boolean getAnswerBoolean(String answer){
        String a = answer.substring(0, 1);
        return "Y".equals(a);
    }

    public static boolean getUnsureBoolean(String answer){
        return answer.length() == 2;
    }

    public static String getScenario(int scenarioID){
        switch (scenarioID){
            case 0:
                Log.d("return", SCENARIOS_ILL);
                return SCENARIOS_ILL;
            case 1:
                Log.d("return", SCENARIOS_INJURY);
                return SCENARIOS_INJURY;
            default:
                return SCENARIOS_ILL;
        }
    }

    public static int getXMLID(String xml){
        Log.d("careXML", xml);
        if ("care_aed".equals(xml)){
            return R.xml.care_aed;
        } else if ("care_chest_compression".equals(xml)){
            return R.xml.care_chest_compression;
        } else if ("care_bleed_stopping".equals(xml)) {
            return R.xml.care_bleed_stopping;
        } else if ("care_airway_foreign_body_removal".equals(xml)) {
            return R.xml.care_airway_foreign_body_removal;
        } else if ("care_heatstroke".equals(xml)) {
            return R.xml.care_heatstroke;
        } else if ("care_fracture".equals(xml)){
            return R.xml.care_fracture;
        } else {
            return R.xml.care_recovery_position;
        }
    }

    public static String getDMSLocation(double degree){
        degree += 1.0/3600/2;
        int d = (int)degree;
        int m = (int)((degree - d) * 60);
        int s = (int)((degree - d - m/60.0)*3600);

        return Integer.toString(d) + "度" + Integer.toString(m) + "分" + Integer.toString(s) + "秒";
    }
}
