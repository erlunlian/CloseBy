package com.example.closebyswipe;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class MessageAdapter extends BaseAdapter {

    private final Context mContext;
    private final ArrayList<Message> messages;
    private View image;
    private FirebaseDatabase mdbase;
    private DatabaseReference dbref;

    // 1
    public MessageAdapter(Context context, ArrayList<Message> messages) {
        System.out.println("constructor");
        this.mContext = context;
        this.messages = messages;
        System.out.println("constructor");
    }

    // 2
    @Override
    public int getCount() {
        return messages.size();
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
        final Message msg = messages.get(position);

        // 2
        if (convertView == null)  {
            final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            if (msg.getUserId().equals(android.os.Build.SERIAL)) {
                convertView = layoutInflater.inflate(R.layout.chatbubblesright, null);
            }
            else
                convertView = layoutInflater.inflate(R.layout.chatbubblesleft, null);
        }
        final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        if (msg.getUserId().equals(android.os.Build.SERIAL)) {
            convertView = layoutInflater.inflate(R.layout.chatbubblesright, null);
        }
        else
            convertView = layoutInflater.inflate(R.layout.chatbubblesleft, null);
        // 3
//        final ImageView imageView = (ImageView)convertView.findViewById(R.id.image_cover_art);
       // final TextView keyTextView = (TextView) convertView.findViewById((R.id.show_key));
        final TextView textmsg = (TextView) convertView.findViewById(R.id.textmessage);
        final TextView msguser = (TextView) convertView.findViewById(R.id.messageuser);

        // 4
        textmsg.setText(msg.getText());
        msguser.setText(msg.getUserId());
        System.out.println("the message  is" + msg.getText());
        System.out.println("messageuser is " + msg.getUserId());

        /**
         //imageView.setImageResource(book.getImageResource());
         nameTextView.setText(book.name);
         authorTextView.setText(book.description);
         */

        return convertView;
    }



}

