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


public class tripArrayAdapter extends BaseAdapter {

    private Context context = null;
    private List<Trip> data = null;

    public tripArrayAdapter(Context context, List<Trip> data) {
        this.context = context;
        this.data = data;
    }

    public void updateDataArray(List<Trip> data){
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

        if (convertView == null) {
            //if there are no previous views, then create a new view based on the custom list item layout
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_past_trips, parent, false);
        }

        if (data.size() > 0) {

            //Obtain all the data from the current Trip
            Trip currData = data.get(position);

            String date = currData.getDate();
            String origin = currData.getOrigin();
            String destination = currData.getDestination();

            //populate the UI item with the data
            TextView lbl_date = (TextView) convertView.findViewById(R.id.edit_trip_date);
            TextView lbl_origin = (TextView) convertView.findViewById(R.id.edit_trip_origin);
            TextView lbl_destination = (TextView) convertView.findViewById(R.id.edit_trip_destination);

            //Set all the individual elements to be displayed
            lbl_date.setText(date);
            lbl_origin.setText(origin);
            lbl_destination.setText(destination);

            return convertView;
        } else {

            return null;
        }
    }
}
