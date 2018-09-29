package com.example.haruka.rescue_aid.listener;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.haruka.rescue_aid.R;

import java.util.Date;

/**
 * SimpleCallOverlay用のListener
 */
// TODO NewDragViewListnerとの統合
public class SimpleDragViewListener implements View.OnTouchListener{

    private WindowManager windowManager;
    public static View view;
    private WindowManager.LayoutParams params;
    private LayoutInflater layoutInflater;

    static boolean big = true;
    public static String text = "";
    private int oldx;
    private int oldy;

    Date start;

    public SimpleDragViewListener(WindowManager dragView, View view, WindowManager.LayoutParams params, LayoutInflater layoutInflater) {
        this.windowManager = dragView;
        this.view = view;
        this.params = params;
        this.layoutInflater = layoutInflater;
    }

    private void makeBig(){
        windowManager.removeView(view);
        view = layoutInflater.inflate(R.layout.service_layer_old, null);

        // Viewを画面上に追加
        windowManager.addView(view, params);

        View close = ((LinearLayout)((LinearLayout)(((LinearLayout)view).getChildAt(0))).getChildAt(0)).getChildAt(1);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                windowManager.removeView(view);
                view = null;
            }
        });

        SimpleDragViewListener simpleDragViewListener = new SimpleDragViewListener(windowManager, view, params, layoutInflater);
        (view).setOnTouchListener(simpleDragViewListener);

        TextView textView = (TextView) (((LinearLayout)(((LinearLayout)view).getChildAt(0))).getChildAt(1));
        textView.setText(text);
    }

    public void setText(String text){
        this.text = text;
    }

    private void makeSmall(){
        windowManager.removeView(view);
        view = layoutInflater.inflate(R.layout.service_layer_small, null);
        windowManager.addView(view, params);
        SimpleDragViewListener simpleDragViewListener = new SimpleDragViewListener(windowManager, view, params, layoutInflater);
        (view).setOnTouchListener(simpleDragViewListener);
    }

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
                Log.d("timedayodayo", Long.toString(end.getTime() - start.getTime()));
                Log.d("timedayodayo", "jogejogjeo");
                if ((end.getTime() - start.getTime()) < 200){
                    if(big) {
                        makeSmall();
                        big = false;
                    } else {
                        makeBig();
                        big = true;
                    }
                }
        }
        oldx = x;
        oldy = y;

        return true;
    }



}
