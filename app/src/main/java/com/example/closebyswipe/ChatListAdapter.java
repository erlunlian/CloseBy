package com.example.closebyswipe;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;


public class ChatListAdapter extends BaseAdapter {

    private final Context mContext;
    private final ArrayList<Chat> chats;
    private View image;
    private double curLong;
    private double curLat;

    // 1
    public ChatListAdapter(Context context, ArrayList<Chat> chats) {
        System.out.println("constructor");
        this.mContext = context;
        this.chats = chats;
        System.out.println("constructor");
    }

    // 2
    @Override
    public int getCount() {
        return chats.size();
    }

    // 3
    @Override
    public long getItemId(int position) {
        return 0;
    }

    // 4
    @Override
    public Object getItem(int position) {
        return null;
    }

    // 5
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // 1
        final Chat thechat = chats.get(position);
        // 2
        if (convertView == null) {
            final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            convertView = layoutInflater.inflate(R.layout.layoutchat, null);
        }

        double longitude = thechat.getLongitude();
        double latitude = thechat.getLatitude();

/*
        if (ContextCompat.checkSelfPermission(this.mContext, android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this.mContext, android.Manifest.permission.ACCESS_COARSE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED) {
            final View finalConvertView = convertView;
            MyLocation.LocationResult locationResult = new MyLocation.LocationResult(){
                @Override
                public void gotLocation(Location location){
                    curLong = location.getLongitude();
                    curLat = location.getLatitude();
                    populateChat(curLong, curLat, finalConvertView, thechat);
                }
            };
            MyLocation myLocation = new MyLocation();
            myLocation.getLocation(this.mContext, locationResult);

        } else {
            ActivityCompat.requestPermissions((Activity) this.mContext, new String[] {
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION },
                    1);
        }

*/
        final View finalConvertView = convertView;

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this.mContext);
        curLong = sp.getFloat("longitude", 0);
        curLat = sp.getFloat("latitude", 0);
        populateChat(curLong, curLat, finalConvertView, thechat);

        /**
        //imageView.setImageResource(book.getImageResource());
        nameTextView.setText(book.name);
        authorTextView.setText(book.description);
         */

        return convertView;
    }

    public void populateChat(double curLong, double curLat, View convertView, Chat thechat) {
        final TextView keyTextView = (TextView) convertView.findViewById((R.id.show_key));
        final TextView nameTextView = (TextView) convertView.findViewById(R.id.textview_book_name);
        final TextView authorTextView = (TextView) convertView.findViewById(R.id.textview_book_author);
        final TextView distance = (TextView) convertView.findViewById(R.id.distance);

        // 4
        nameTextView.setText(thechat.getName());
        authorTextView.setText(thechat.getDescription());
        keyTextView.setText(thechat.getKey());
        distance.setText(calculateDistance(thechat.getLongitude(), thechat.getLatitude()) + " miles away");
    }

    public String calculateDistance(double lon1, double lat1) {

        DecimalFormat df = new DecimalFormat("#.##");

        double lon2 = curLong;
        double lat2 = curLat;

        Location locationA = new Location("point A");

        locationA.setLatitude(lat1);
        locationA.setLongitude(lon1);

        Location locationB = new Location("point B");

        locationB.setLatitude(lat2);
        locationB.setLongitude(lon2);

        double distance = locationA.distanceTo(locationB);

        return df.format(distance * 0.000621371);
    }



}

