package com.example.haruka.rescue_aid.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.util.AttributeSet;
import android.view.View;

import com.example.haruka.rescue_aid.R;

import static android.R.attr.width;
import static android.R.attr.x;
import static android.R.attr.y;
import static android.R.attr.height;

public class ResultInflate extends View {

    public String care;
    Drawable progress;

    public ResultInflate(Context context) {
        super(context);
    }

    public ResultInflate(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.ResultInflate);
        care = array.getString(R.styleable.ResultInflate_care_title);
        array.recycle();

        LayerDrawable layer = (LayerDrawable)getResources().getDrawable(R.drawable.progress);
        progress = layer.findDrawableByLayerId(android.R.id.progress);
    }

    public ResultInflate(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    protected void onDraw(Canvas canvas) {
        Drawable d = progress;
        d.setBounds(x, y, width, height);
        d.draw(canvas);
    }
}
