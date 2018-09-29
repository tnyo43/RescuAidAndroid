package com.example.haruka.rescue_aid.views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.haruka.rescue_aid.R;
import com.example.haruka.rescue_aid.utils.Care;

import java.util.ArrayList;

public class CareListAdapter extends BaseAdapter {
    Context context;
    LayoutInflater layoutInflater = null;
    ArrayList<Care> careList;

    public CareListAdapter(Context context) {
        this.context = context;
        this.layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setCareList(ArrayList<Care> careList) {
        this.careList = careList;
    }

    @Override
    public int getCount() {
        return careList.size();
    }

    @Override
    public Object getItem(int position) {
        return careList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return (long)careList.get(position).index;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = layoutInflater.inflate(R.layout.list_care_carelist,parent,false);

        Care c = careList.get(position);
        ((TextView)convertView.findViewById(R.id.textview_carelist_title)).setText(c.name);
        ((ImageView)convertView.findViewById(R.id.imageview_carelist)).setImageDrawable(c.drawable);
        return convertView;
    }
}