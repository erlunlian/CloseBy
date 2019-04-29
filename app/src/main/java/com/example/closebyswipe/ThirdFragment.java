package com.example.closebyswipe;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static android.provider.AlarmClock.EXTRA_MESSAGE;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ThirdFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ThirdFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ThirdFragment extends Fragment {
    private FirebaseDatabase mdbase;
    private DatabaseReference dbref;
    private ArrayList<Chat> mItems;
    View v;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_third, container, false);

        mdbase = FirebaseDatabase.getInstance();
        dbref = mdbase.getReference();

        System.out.println("Before database");


        // Get reference to firebase location where the data is stored
        dbref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get data
                final User u = dataSnapshot.child("users").child(android.os.Build.SERIAL).getValue(User.class);

                System.out.println("accessing database");

                ArrayList<Chat> chatList = new ArrayList<Chat>();

                Iterable<DataSnapshot> yourChatList = dataSnapshot.child("users").child(android.os.Build.SERIAL).child("userChatList").getChildren();
                for (DataSnapshot chat: yourChatList) {
                    chatList.add(chat.getValue(Chat.class));
                }

                ListView listView = (ListView) v.findViewById(R.id.chatList);

                final ChatListAdapter chatListAdapter = new ChatListAdapter(getContext(), chatList);
                listView.setAdapter(chatListAdapter);

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        TextView textView = (TextView) view.findViewById(R.id.show_key);

                        Intent intent = new Intent(getActivity(), chatView.class);
                        String key = textView.getText().toString();
                        intent.putExtra("KEY", key);

                        startActivity(intent);

                    }
                });



                // Update UI elements here...

            }

            @Override
            public void onCancelled(DatabaseError databaserror) {}
        });

        return v;
    }




    public static ThirdFragment newInstance(String text) {

        ThirdFragment f = new ThirdFragment();
        Bundle b = new Bundle();
        b.putString("msg", text);

        f.setArguments(b);

        return f;
    }
}
