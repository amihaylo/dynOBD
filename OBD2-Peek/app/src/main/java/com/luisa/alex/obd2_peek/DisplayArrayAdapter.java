package com.luisa.alex.obd2_peek;

/**
 * Created by alex on 2016-11-21.
 */


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;


public class DisplayArrayAdapter extends BaseAdapter {

    private Context context = null;
    private List<String> data = null;

    public DisplayArrayAdapter(Context context, List<String> data) {
        this.context = context;
        this.data = data;
    }

    public void updateDataArray(List<String> data){
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

        String currData = data.get(position);

        if (convertView == null) {
            //if there are no previous views, then create a new view based on the custom list item layout
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_about_car, parent, false);
        }

        //populate the UI item with the data
        TextView lblTitle = (TextView) convertView.findViewById(R.id.lbl_vin_data);
        lblTitle.setText(currData);

        return convertView;
    }
}
