package com.example.closebyswipe;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.GridView;

import java.util.ArrayList;
import java.util.List;

public class ExplorePage extends AppCompatActivity {
    private ArrayList<Chat> mItemsL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore_page);
        GridView gridView = (GridView)findViewById(R.id.gridview);
        Intent i = getIntent();
        mItemsL = (ArrayList<Chat>) i.getSerializableExtra("chatList");
        ChatsAdapter chatsAdapter = new ChatsAdapter(this, mItemsL);
        gridView.setAdapter(chatsAdapter);


    }
}
