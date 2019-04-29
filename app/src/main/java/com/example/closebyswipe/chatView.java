package com.example.closebyswipe;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import static android.os.SystemClock.sleep;

public class chatView extends AppCompatActivity {

    private FirebaseDatabase mdbase;
    private DatabaseReference dbref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_view);
        ArrayList<Message> g = new ArrayList<Message>();
        getAndShowMessages(g);
        mdbase = FirebaseDatabase.getInstance();
        dbref = mdbase.getReference();

        Intent intent = getIntent();
        final String key = intent.getExtras().getString("KEY");
        //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        dbref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get data

                User u = dataSnapshot.child("users").child(android.os.Build.SERIAL).getValue(User.class);
                String chatName = (String)dataSnapshot.child("chats").child(key).child("name").getValue();
                getSupportActionBar().setTitle(chatName);

                long size = dataSnapshot.child("chats").child(key).child("messageList").getChildrenCount();

                clickStuff(u, key, size);

                ArrayList<Message> yourMessageList = new ArrayList<Message>();

                Iterable<DataSnapshot> messageList = dataSnapshot.child("chats").child(key).child("messageList").getChildren();

                for (DataSnapshot message: messageList) {
                    yourMessageList.add(message.getValue(Message.class));

                }

                for(int i = 0; i < yourMessageList.size(); i++) {
                    System.out.println("For loop message user id" + yourMessageList.get(i).getUserId());
                }

                System.out.println("message list"+yourMessageList.size());
                System.out.println("Data change user id " + u.getId());

                getAndShowMessages(yourMessageList);
                // Update UI elements here...

            }

            @Override
            public void onCancelled(DatabaseError databaserror) {
            }
        });

    }

    private void getAndShowMessages(final ArrayList<Message> messages) {
        System.out.println("here it is" + messages.size());
        final ListView listView = (ListView) findViewById(R.id.messageListView);
        List<String> your_list = new ArrayList<String>();

        for(int i = 0; i < messages.size(); i++) {
            your_list.add(messages.get(i).getText());
        }
        final MessageAdapter messageAdapter = new MessageAdapter(this, messages);
        //ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
       //         this, android.R.layout.simple_list_item_1,your_list
       // ) ;
        listView.setAdapter(messageAdapter);
        listView.post(new Runnable() {
            @Override
            public void run() {
                // Select the last row so it will scroll into view...
                listView.setSelection(messages.size() - 1);
            }
        });
    }

    private void clickStuff(final User u, final String key, final long size) {
        Button send = findViewById(R.id.sendMessage);
        final EditText descriptionText = findViewById(R.id.writeMessage);

        send.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                System.out.println("User ID is " + u.getId());


                System.out.println("button clicked");
                //User u = new User("5");


                String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                        + "0123456789"
                        + "abcdefghijklmnopqrstuvxyz";

                // create StringBuffer size of AlphaNumericString
                StringBuilder sb = new StringBuilder(20);

                for (int i = 0; i < 20; i++) {

                    // generate a random number between
                    // 0 to AlphaNumericString variable length
                    int index
                            = (int) (AlphaNumericString.length()
                            * Math.random());

                    // add Character one by one in end of sb
                    sb.append(AlphaNumericString
                            .charAt(index));
                }
                Message m = new Message(u, descriptionText.getText().toString(), u.getId());
                int x = (int)size;
                dbref.child("chats").child(key).child("messageList").child(Integer.toString(x)).setValue(m);
                descriptionText.setText("");


            }

        });


    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.settings_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_name) {
            //startActivity(new Intent(this, SecondFragment.class));
            this.finish();
        }

        return super.onOptionsItemSelected(item);
    }

}
