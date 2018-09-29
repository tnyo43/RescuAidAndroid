package com.example.haruka.rescue_aid.views;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.haruka.rescue_aid.R;
import com.example.haruka.rescue_aid.utils.Care;

import java.util.ArrayList;

public class CareAdapter extends BaseAdapter {
    Context context;
    LayoutInflater layoutInflater = null;
    ArrayList<Care> careList;

    public CareAdapter(Context context) {
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

    public void call119(){
        Intent intent;
        Uri uri = Uri.parse("tel:119");
        intent = new Intent(Intent.ACTION_DIAL,uri);
        context.startActivity(intent);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        convertView = layoutInflater.inflate(R.layout.list_care,parent,false);
        final CareListView listView = (CareListView)parent;

        final Care c = careList.get(position);
        ((TextView)convertView.findViewById(R.id.textview_care_title)).setText(c.name);
        ((TextView)convertView.findViewById(R.id.textview_care_description)).setText(c.description);
        ((Button)convertView.findViewById(R.id.btn_explain_care)).setText(c.buttonText);
        convertView.findViewById(R.id.btn_explain_care).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listView.performItemClick(v, position, 0);
            }
        });

        return convertView;
    }
}