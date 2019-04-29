package com.example.closebyswipe;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;


public class FirstFragment extends Fragment {

    private FirebaseDatabase mdbase;
    private DatabaseReference dbref;
    private ArrayList<Chat> mItems;
    View v;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_first, container, false);


        mdbase = FirebaseDatabase.getInstance();
        dbref = mdbase.getReference();

        // Get reference to firebase location where the data is stored
        dbref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                // Get data

                ArrayList<Chat> chatList = new ArrayList<Chat>();
                long size = dataSnapshot.child("chats").getChildrenCount();
                Iterable<DataSnapshot> chats = dataSnapshot.child("chats").getChildren();
                for (DataSnapshot chat: chats) {
                    Chat c = chat.getValue(Chat.class);
                    if (c.getRadius() > calculateDistance(c.getLongitude(),c.getLatitude())) {
                        chatList.add(chat.getValue(Chat.class));
                    }
                }
                System.out.println("chats are " + chatList);
                GridView gridView = (GridView) v.findViewById(R.id.gridview);

                ChatsAdapter chatsAdapter = new ChatsAdapter(getContext(), chatList);
                gridView.setAdapter(chatsAdapter);
                gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        TextView textView = (TextView) view.findViewById(R.id.show_key);
                        Intent intent = new Intent(getActivity(), chatView.class);
                        String key = textView.getText().toString();
                        Chat joinedChat = dataSnapshot.child("chats").child(key).getValue(Chat.class);
                        dbref.child("users").child(android.os.Build.SERIAL).child("userChatList").child(key).setValue(joinedChat);

                        intent.putExtra("KEY", key);

                        //intent.put("USER", u);
                        startActivity(intent);

                    }
                });


                // Update UI elements here...

            }

            @Override
            public void onCancelled(DatabaseError databaserror) {
            }
        });

        return v;
    }

    public static FirstFragment newInstance(String text) {

        FirstFragment f = new FirstFragment();
        Bundle b = new Bundle();
        b.putString("msg", text);

        f.setArguments(b);

        return f;
    }
    public double calculateDistance(double lon1, double lat1) {

        DecimalFormat df = new DecimalFormat("#.##");
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        double lon2 = sp.getFloat("longitude", 0);
        double lat2 = sp.getFloat("latitude", 0);


        Location locationA = new Location("point A");

        locationA.setLatitude(lat1);
        locationA.setLongitude(lon1);

        Location locationB = new Location("point B");

        locationB.setLatitude(lat2);
        locationB.setLongitude(lon2);

        double distance = locationA.distanceTo(locationB);
        System.out.println("distance is "+distance);
        return distance * 0.000621371;
    }

}
