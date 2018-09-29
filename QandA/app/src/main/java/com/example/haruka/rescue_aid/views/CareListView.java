package com.example.haruka.rescue_aid.views;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

public class CareListView extends ListView implements View.OnClickListener {

    public CareListView(Context context){
        super(context);
    }

    public CareListView(Context context , AttributeSet attrs){
        super(context , attrs);
    }

    @Override
    public boolean performItemClick(View view, int position, long id) {
        return super.performItemClick(view, position, id);
    }

    public void onClick(View view) {
        Object o = view.getTag();
        Log.d("object", o.toString());
        int pos = (Integer)o;
        this.performItemClick(view, pos, view.getId());
    }
}
