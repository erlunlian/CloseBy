
package com.example.closebyswipe;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Random;

import static android.support.v4.os.LocaleListCompat.create;

public class addChat extends AppCompatActivity {
    public static final int GET_FROM_GALLERY = 3;
    String chatID = "";
    String picString = "";

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
        final EditText radius = findViewById(R.id.radiusText);
        Intent myIntent = getIntent(); // gets the previously created intent
        final double longitude = myIntent.getDoubleExtra("longitudeKey",0); // will return "FirstKeyValue"
        final double latitude= myIntent.getDoubleExtra("latitudeKey",0);
        System.out.println("thelongitude" + longitude);
        getSupportActionBar().setTitle("Add Chat");

        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                + "0123456789"
                + "abcdefghijklmnopqrstuvxyz";

        // create StringBuffer size of AlphaNumericString
        final StringBuilder sb = new StringBuilder(20);

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
        chatID = sb.toString();

        Button upload = (Button) findViewById(R.id.uploadChat);
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(
                        new Intent(
                                Intent.ACTION_PICK,
                                android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI
                        ),
                        GET_FROM_GALLERY
                );

            }
        });



        addButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                // Checks if name description radius are added
                if (nameText.getText().toString().equals("") || descriptionText.getText().toString().equals("") || radius.getText().toString().equals("")) {
                    AlertDialog alertDialog = new AlertDialog.Builder(addChat.this).create();
                    alertDialog.setTitle("Error!");
                    alertDialog.setMessage("Please add a name, description and discovery radius for your group chat.");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                }

                else {
                    Chat chat = new Chat(nameText.getText().toString(), descriptionText.getText().toString(), longitude, latitude, sb.toString(), Integer.parseInt(radiusText.getText().toString()), picString);

                    mDatabase.child("chats").child(chatID).setValue(chat);

                    mDatabase.child("users").child(android.os.Build.SERIAL).child("userChatList").child(chatID).setValue(chat);
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

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            switch (requestCode) {

                case GET_FROM_GALLERY:
                    if (resultCode == Activity.RESULT_OK) {
                        Uri selectedImage = data.getData();
                        Bitmap bitmap = null;
                        try {
                            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                            ImageView pro = (ImageView) findViewById(R.id.chatImage);
                            pro.setImageBitmap(bitmap);
                            uploadImage(selectedImage, chatID);
                        } catch (FileNotFoundException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        break;
                    } else if (resultCode == Activity.RESULT_CANCELED) {
                        Log.e("TEST", "Selecting picture cancelled");
                    }
                    break;
            }
        } catch (Exception e) {
            Log.e("TEST", "Exception in onActivityResult : " + e.getMessage());
        }
    }
    private void uploadImage(Uri filePath, String chatID) {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();

        if(filePath != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();
            final DatabaseReference mDatabase;
            mDatabase = FirebaseDatabase.getInstance().getReference();
            picString = createString();
            StorageReference ref = storageReference.child("chatimages/" + picString);

            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Uploaded", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded "+(int)progress+"%");
                        }
                    });


        }
    }
    private String createString() {

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
        return sb.toString();
    }


}

