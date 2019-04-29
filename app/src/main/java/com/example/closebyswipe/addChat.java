
package com.example.closebyswipe;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.nio.charset.Charset;
import java.util.Random;

import static android.support.v4.os.LocaleListCompat.create;

public class addChat extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final DatabaseReference mDatabase;
        mDatabase = FirebaseDatabase.getInstance().getReference();
        setContentView(R.layout.activity_add_chat);
        final EditText nameText = findViewById(R.id.nameText);
        final EditText descriptionText = findViewById(R.id.descriptionText);
        final EditText radiusText = findViewById(R.id.radiusText);

        final Button addButton = findViewById(R.id.addButton);
        Intent myIntent = getIntent(); // gets the previously created intent
        final double longitude = myIntent.getDoubleExtra("longitudeKey",0); // will return "FirstKeyValue"
        final double latitude= myIntent.getDoubleExtra("latitudeKey",0);
        System.out.println("thelongitude" + longitude);
        getSupportActionBar().setTitle("Add Chat");

        addButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                        + "0123456789"
                        + "abcdefghijklmnopqrstuvxyz";

                // create StringBuffer size of AlphaNumericString
                StringBuilder sb = new StringBuilder(20);

                for (int i = 0; i < 20; i++) {

                    // generate a random number between
                    // 0 to AlphaNumericString variable length
                    int index
                            = (int)(AlphaNumericString.length()
                            * Math.random());

                    // add Character one by one in end of sb
                    sb.append(AlphaNumericString
                            .charAt(index));
                }

                if (nameText.getText().toString().equals("") || descriptionText.getText().toString().equals("")) {
                    AlertDialog alertDialog = new AlertDialog.Builder(addChat.this).create();
                    alertDialog.setTitle("Error!");
                    alertDialog.setMessage("Please add a name and description for the group chat.");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                }

                else {
                    Chat chat = new Chat(nameText.getText().toString(), descriptionText.getText().toString(), longitude, latitude, sb.toString(), Integer.parseInt(radiusText.getText().toString()));

                    mDatabase.child("chats").child(sb.toString()).setValue(chat);
                    mDatabase.child("users").child(android.os.Build.SERIAL).child("userChatList").child(sb.toString()).setValue(chat);
                    System.out.println("chat created");
                    //Intent myIntent = new Intent(this, ExplorePage.class);
                    //myIntent.putExtra("chatsKey",longitude);
                    // myIntent.putExtra("latitudeKey",latitude);
                    System.out.println("longitude and latitude here" + longitude + latitude);
                    finish();
                }

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

