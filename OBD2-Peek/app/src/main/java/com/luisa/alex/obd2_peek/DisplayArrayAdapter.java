package com.luisa.alex.obd2_peek;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by luisarojas on 2016-11-13.
 */

public class DisplayArrayAdapter extends BaseAdapter {

    private Context context = null;
    private List<OBDData> data = null;

    public DisplayArrayAdapter(Context context, List<OBDData> data) {
        this.context = context;
        this.data = data;
    }

    public void updateDataArray(List<OBDData> data){
        this.data.clear();
        this.data.addAll(data);
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        OBDData currData = data.get(position);

        if (convertView == null) {
            //if there are no previous views, then create a new view based on the custom list item layout
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.obddata_item, parent, false);
        }

        //populate the UI item with the data
        TextView lblTitle = (TextView) convertView.findViewById(R.id.lblTitle);
        lblTitle.setText(currData.getTitle());

        TextView lblValue = (TextView) convertView.findViewById(R.id.lblValue);
        lblValue.setText(String.valueOf(currData.getData()));

        return convertView;
    }
}