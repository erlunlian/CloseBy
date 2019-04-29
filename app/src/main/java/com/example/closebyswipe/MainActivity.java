package com.example.closebyswipe;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.app.ActionBar;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;


import com.example.closebyswipe.FirstFragment;
import com.example.closebyswipe.R;
import com.example.closebyswipe.SecondFragment;
import com.example.closebyswipe.ThirdFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.google.firebase.FirebaseApp;

import static android.os.SystemClock.sleep;

public class MainActivity extends AppCompatActivity {

    private LocationManager locationManager;
    private Toolbar myToolbar;
    private int pageOn = 1;
    double longitude;
    double latitude;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FirebaseApp.initializeApp(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
         myToolbar = (Toolbar) findViewById(R.id.toolbar5);
        setSupportActionBar(myToolbar);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED) {
        } else {
            ActivityCompat.requestPermissions(this, new String[] {
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION },
                    1);
        }


        ViewPager pager = (ViewPager) findViewById(R.id.viewPager);
        pager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
        pager.setCurrentItem(1);
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {
                ImageButton tbut = (ImageButton) findViewById(R.id.toolbarButton);
                switch (position) {
                    case 0:
                        myToolbar.setTitle("Explore");
                        tbut.setBackgroundResource(R.drawable.plus);

                        pageOn = 0;
                        break;
                    case 1:
                        myToolbar.setTitle("Map View");
                        pageOn = 1;
                        tbut.setBackgroundResource(R.drawable.settings);

                        break;
                    case 2:
                        myToolbar.setTitle("Chats");
                        pageOn = 2;
                        tbut.setBackgroundResource(R.drawable.plus);
                        break;
                }
            }
            @Override
            public void onPageScrollStateChanged(int state) {}
        });
        toolbarButton();



    }

    private void toolbarButton() {
        ImageButton tbut = (ImageButton) findViewById(R.id.toolbarButton);
        tbut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (pageOn) {
                    case 0:
                        addChat();
                        break;
                    case 1:
                        break;
                    case 2:
                        addChat();
                        break;
                }
            }
        });


    }

    private class MyPagerAdapter extends FragmentPagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int pos) {
            switch(pos) {

                case 0:
                    return FirstFragment.newInstance("FirstFragment, Instance 1");
                case 1:
                    return SecondFragment.newInstance("SecondFragment, Instance 1");
                case 2: return ThirdFragment.newInstance("ThirdFragment, Instance 1");
                default: return ThirdFragment.newInstance("ThirdFragment, Default");
            }
        }

        @Override
        public int getCount() {
            return 3;
        }
    }

    private void addChat() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED) {
            MyLocation.LocationResult locationResult = new MyLocation.LocationResult(){
                @Override
                public void gotLocation(Location location){
                    longitude = location.getLongitude();
                    latitude = location.getLatitude();
                    System.out.println("longitude is "+longitude);
                    System.out.println("latitude is "+latitude);
                }
            };
            MyLocation myLocation = new MyLocation();
            myLocation.getLocation(this, locationResult);
        } else {
            ActivityCompat.requestPermissions(this, new String[] {
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION },
                    1);
        }


        Intent myIntent = new Intent(this, addChat.class);
        myIntent.putExtra("longitudeKey",longitude);
        myIntent.putExtra("latitudeKey",latitude);
        // myIntent.putExtra("chatList", (Serializable) mItems);

        System.out.println("longitude and latitude here"+ longitude + latitude);
        startActivity(myIntent);

    }
}
