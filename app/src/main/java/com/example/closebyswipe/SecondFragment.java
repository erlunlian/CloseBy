package com.example.closebyswipe;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;
import static android.os.SystemClock.sleep;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SecondFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SecondFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SecondFragment extends Fragment {

    MapView mMapView;
    private GoogleMap googleMap;
    double longitude;
    double latitude;
    private FirebaseDatabase mdbase;
    private DatabaseReference dbref;
    private List<Chat> chats;
    View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mdbase = FirebaseDatabase.getInstance();
        dbref = mdbase.getReference();
        chats = new ArrayList<Chat>();
        //      userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        rootView = inflater.inflate(R.layout.fragment_second, container, false);

        //TODO: Only add user if not present
        final User user = new User(android.os.Build.SERIAL);

        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("test.png");


        ImageView image = (ImageView)rootView.findViewById(R.id.imageView);
        ImageButton im = (ImageButton) rootView.findViewById(R.id.imageButton);
        GlideApp.with(getActivity().getApplicationContext() /* context */)
                .load(storageReference)
                .into(im);

        im.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Profile.class);
                startActivity(intent);

            }
        });
        dbref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User u = dataSnapshot.child("users").child(android.os.Build.SERIAL).getValue(User.class);

                if (u == null) {
                    dbref.child("users").child(android.os.Build.SERIAL).setValue(user);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaserror) {}

        });


        dbref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get data

                ArrayList<Chat> chatList = new ArrayList<Chat>();
                long size = dataSnapshot.child("chats").getChildrenCount();
                Iterable<DataSnapshot> chats2 = dataSnapshot.child("chats").getChildren();
                for (DataSnapshot chat: chats2) {
                    chats.add(chat.getValue(Chat.class));
                }
                System.out.println("chats are " + chats);




                // Update UI elements here...

            }

            @Override
            public void onCancelled(DatabaseError databaserror) {
            }
        });

sleep(1000);
        mMapView = (MapView) rootView.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED) {
            MyLocation.LocationResult locationResult = new MyLocation.LocationResult(){
                @Override
                public void gotLocation(Location location){
                    longitude = location.getLongitude();
                    latitude = location.getLatitude();
                    mapReady(latitude, longitude);
                }
            };
            MyLocation myLocation = new MyLocation();
            myLocation.getLocation(getActivity(), locationResult);
        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[] {
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION },
                    1);
        }

        FloatingActionButton refresh = rootView.findViewById(R.id.refresh);

        refresh.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                System.out.println("hello, refreshing");

                getChatPins();
            }
        });

        FloatingActionButton fab = rootView.findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent myIntent = new Intent(getActivity(), addChat.class);
                myIntent.putExtra("longitudeKey",longitude);
                myIntent.putExtra("latitudeKey",latitude);
                // myIntent.putExtra("chatList", (Serializable) mItems);

                System.out.println("longitude and latitude here"+ longitude + latitude);
                startActivity(myIntent);
            }
        });
        mMapView.onResume(); // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }
    public static SecondFragment newInstance(String text) {

        SecondFragment f = new SecondFragment();
        Bundle b = new Bundle();
        b.putString("msg", text);

        f.setArguments(b);

        return f;
    }


    public void mapReady(final double latit, final double longit) {

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(final GoogleMap mMap) {
                googleMap = mMap;
                googleMap.setMyLocationEnabled(true);

                UiSettings mapUiSettings = mMap.getUiSettings();
                mapUiSettings.setAllGesturesEnabled(false);

                final int zoomLevel = 18;

                SeekBar zoomBar = (SeekBar) getView().findViewById(R.id.seekBar);
                zoomBar.setMax(zoomLevel);
                zoomBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {}
                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {}
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int zoomValue = zoomLevel - progress;
                mMap.animateCamera(CameraUpdateFactory.zoomTo(zoomValue));
                System.out.println("zoomval = " + zoomValue + "\n" + "zoomBarProgress = " + progress);

                }
                });

                // For showing a move to my location button

                // For dropping a marker at a point on the Map
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
                SharedPreferences.Editor editor = sp.edit();
                editor.putFloat("latitude", (float)latit);
                editor.putFloat("longitude", (float)longit);

                editor.commit();
                LatLng currentLocation = new LatLng(latit, longit);
                // googleMap.addMarker(new MarkerOptions().position(currentLocation).title("Marker Title").snippet("Marker Description"));
                System.out.print("the size of mitems is "+ chats.size());
                getChatPins();
                // For zooming automatically to the location of the marker
                CameraPosition cameraPosition = new CameraPosition.Builder().target(currentLocation).zoom(zoomLevel).build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(Marker marker) {
                        Intent intent = new Intent(getActivity(), chatView.class);
                        Chat joinedChat = (Chat)marker.getTag();
                        String key = joinedChat.getKey();
                        //Chat joinedChat = dataSnapshot.child("chats").child(key).getValue(Chat.class);
                        dbref.child("users").child(android.os.Build.SERIAL).child("userChatList").child(key).setValue(joinedChat);

                        intent.putExtra("KEY", key);

                        //intent.put("USER", u);
                        startActivity(intent);
                    }
                });

            }
        });
    }

    public void getChatPins() {
        for (int i = 0; i < chats.size(); i++) {
            Chat c = chats.get(i);
            System.out.println("Chat name is: " + c.getName());
            System.out.println("latitude2 is " + c.getLatitude());
            System.out.println("longitude2 is " + c.getLongitude());

            if (c.getRadius() > calculateDistance(c.getLongitude(),c.getLatitude())) {
                LatLng chatLocation = new LatLng(c.getLatitude(), c.getLongitude());
                Marker marker = googleMap.addMarker(new MarkerOptions().position(chatLocation).title(c.getName()).snippet(c.getDescription()));
                marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.pin));
                marker.setTag(c);
            }
        }
    }

    public double calculateDistance(double lon1, double lat1) {

        DecimalFormat df = new DecimalFormat("#.##");

        double lon2 = longitude;
        double lat2 = latitude;

        Location locationA = new Location("point A");

        locationA.setLatitude(lat1);
        locationA.setLongitude(lon1);

        Location locationB = new Location("point B");

        locationB.setLatitude(lat2);
        locationB.setLongitude(lon2);

        double distance = locationA.distanceTo(locationB);

        return distance * 0.000621371;
    }

}