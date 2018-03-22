package me.peterjiang.testfinal;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Map;

/**
 * Created by PeterJiang on 10/6/16.
 */

public class EventAdapter extends ArrayAdapter {
    List<Map<String, String>> listdata;
    Context context;
    Location currentLocation;

    public EventAdapter(Context context, List<Map<String, String>> listdata, Location location) {
        super(context, R.layout.event_item, listdata);
        this.context = context;
        this.listdata = listdata;
        this.currentLocation = location;
    }

    public double getDistance(double lat1, double lon1, double lat2, double lon2) {
        float[] results = new float[1];
        Location.distanceBetween(lat1, lon1, lat2, lon2, results);
        return results[0];
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        convertView = inflater.inflate(R.layout.event_item, parent, false);
        TextView TaskName = (TextView) convertView.findViewById(R.id.textListView);
        TextView TaskDescription = (TextView) convertView.findViewById(R.id.textListView2);
        TextView TaskDistance = (TextView) convertView.findViewById(R.id.txt_event_distance);
        double distance = getDistance(currentLocation.getLatitude(), currentLocation.getLongitude(), Double.parseDouble(listdata.get(position).get("latitude")), Double.parseDouble(listdata.get(position).get("longitude")));
        distance = Math.floor(distance * 100) / 100;
        ImageView imageview = (ImageView) convertView.findViewById(R.id.imageView3);
        TaskName.setText(listdata.get(position).get("EventName"));
        TaskDistance.setText(String.format("%.2f miles", distance/1600));
        TaskDescription.setText(listdata.get(position).get("EventDate"));
//        Picasso.with(context).load("http://lorempixel.com/30/30/abstract/").into(imageview);
        int imagenum = position + 998;
        Picasso.with(context).load("https://unsplash.it/40/40/?image="+imagenum ).resize(35, 35).into(imageview);

        return convertView;
    }

}