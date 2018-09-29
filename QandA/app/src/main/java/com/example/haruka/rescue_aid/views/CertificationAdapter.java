package com.example.haruka.rescue_aid.views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.haruka.rescue_aid.R;
import com.example.haruka.rescue_aid.utils.MedicalCertification;

import java.util.ArrayList;

public class CertificationAdapter  extends BaseAdapter {
    Context context;
    LayoutInflater layoutInflater = null;
    ArrayList<MedicalCertification> certificationList;

    public CertificationAdapter(Context context) {
        this.context = context;
        this.layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setMedicalCertification(ArrayList<MedicalCertification> certificationList) {
        this.certificationList = certificationList;
    }

    @Override
    public int getCount() {
        return certificationList.size();
    }

    @Override
    public Object getItem(int position) {
        return certificationList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return certificationList.get(position).number;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = layoutInflater.inflate(R.layout.list_medical_certification,parent,false);

        ((TextView)convertView.findViewById(R.id.textview_name_certification)).setText(certificationList.get(position).name);
        ((TextView)convertView.findViewById(R.id.textview_time_certification)).setText(certificationList.get(position).getStartAtJap());
        ((TextView)convertView.findViewById(R.id.textview_address_certification)).setText(certificationList.get(position).getAddress());

        return convertView;
    }
}
