package com.example.haruka.rescue_aid.listener;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.haruka.rescue_aid.R;
import com.example.haruka.rescue_aid.utils.Utils;

import java.util.ArrayList;
import java.util.Date;

/**
 * NewCallOverlay用のListener
 */
public class NewDragViewListener implements View.OnTouchListener{

    private WindowManager windowManager;
    public static View view;
    public static TextView textView;
    public static TableLayout tableLayout;
    private WindowManager.LayoutParams params;
    private LayoutInflater layoutInflater;
    private static Context context;


    static boolean big = true;
    public static String text = "";
    public static boolean call7119 = false;
    public static ArrayList<String[]> list;
    private int oldx;
    private int oldy;

    Date start;

    public NewDragViewListener(WindowManager dragView, View view, WindowManager.LayoutParams params, LayoutInflater layoutInflater) {
        this.windowManager = dragView;
        this.view = view;
        this.params = params;
        this.layoutInflater = layoutInflater;
    }

    public static void setContext(Context context){
        NewDragViewListener.context = context;
    }

    private void makeBig(){
        windowManager.removeView(view);
        view = layoutInflater.inflate(R.layout.service_layout, null);

        // Viewを画面上に追加
        windowManager.addView(view, params);
        Log.d("LayoutContens", Integer.toString(((LinearLayout)((LinearLayout)((LinearLayout)view).getChildAt(0)).getChildAt(0)).getChildCount()));
        View close = ((LinearLayout)((LinearLayout)(((LinearLayout)view).getChildAt(0))).getChildAt(0)).getChildAt(1);


        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                windowManager.removeView(view);
                view = null;
            }
        });
        NewDragViewListener dragViewListener = new NewDragViewListener(windowManager, view, params, layoutInflater);
        NewDragViewListener.setContext(context);
        (view).setOnTouchListener(dragViewListener);
        dragViewListener.setText(text);

        tableLayout = (TableLayout) (((LinearLayout)(((LinearLayout)view).getChildAt(0))).getChildAt(2));
        Log.d("LayoutContens", Integer.toString(tableLayout.getChildCount()));
        Log.d("LayoutContenslistsize", Integer.toString(list.size()));

        setTable(list);
        textView = (TextView) (((LinearLayout)(((LinearLayout)view).getChildAt(0))).getChildAt(3));
        textView.setText(text);

        //FIXME #付きだと発信できないので対処を考えないといけない
        if (call7119){
            textView = (TextView) (((LinearLayout)(((LinearLayout)view).getChildAt(0))).getChildAt(1));
            textView.setText("\"#7119\"に発信してください");
        }
    }

    public static void setTable(ArrayList<String[]> list){
        NewDragViewListener.list = list;
        Log.e("errorr", Integer.toString(list.size()));

        try {
            for (String[] question : list) {

                TableRow tableRow = new TableRow(context);
                TableRow _tableRow = new TableRow(context); //FIXME マージンを作るようにやってるけど、ちゃんと設定で作れ
                TextView textview = new TextView(context);
                textview.setText("・");
                textview.setTextSize(15);
                tableRow.addView(textview);
                for (int i = 0; i < 2; i++){
                    String val = question[i];
                    textview = new TextView(context);
                    textview.setMaxWidth(340);
                    textview.setTextSize(15);
                    if (i == 1){
                        if (val.equals(Utils.ANSWER_JP_YES)){
                            textview.setTextColor(context.getResources().getColor(R.color.yes));
                        } else if (val.equals(Utils.ANSWER_JP_NO)){
                        } else {
                            textview.setTextColor(context.getResources().getColor(R.color.unsure));
                        }
                        val = "　" + val;
                        textview.setTextColor(context.getResources().getColor(R.color.no));
                    } else {
                        textview.setTextColor(context.getResources().getColor(R.color.black));
                    }
                    textview.setText(val);

                    tableRow.addView(textview);

                    TextView _textView = new TextView(context);
                    _textView.setText(" ");
                    _textView.setMaxHeight(17);
                    _tableRow.addView(_textView);
                }

                tableLayout.addView(tableRow);
                tableLayout.addView(_tableRow);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }


    public void setText(String text){
        this.text = text;
    }

    // 最小化
    private void makeSmall(){
        windowManager.removeView(view);
        view = layoutInflater.inflate(R.layout.service_layer_small, null);
        windowManager.addView(view, params);
        NewDragViewListener dragViewListener = new NewDragViewListener(windowManager, view, params, layoutInflater);
        (view).setOnTouchListener(dragViewListener);
    }

    // ドラッグ、タップの処理
    @Override
    public boolean onTouch(View view, MotionEvent event) {
        int x = (int) event.getRawX();
        int y = (int) event.getRawY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                start = new Date();
                break;

            case MotionEvent.ACTION_MOVE:
                params.x += (x-oldx);
                params.y += (y-oldy);

                windowManager.updateViewLayout(view, params);
                break;
            case MotionEvent.ACTION_UP:
                Date end = new Date();
                Log.d("timeday", Long.toString(end.getTime() - start.getTime()));
                if ((end.getTime() - start.getTime()) < 200){
                    if(big) {
                        makeSmall();
                        Log.d("timeday", "small");
                        big = false;
                    } else {
                        makeBig();
                        Log.d("timeday", "big");
                        big = true;
                    }
                }
        }
        oldx = x;
        oldy = y;

        return true;
    }



}
