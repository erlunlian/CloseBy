package com.example.closebyswipe;

import java.util.ArrayList;

public class User {

        private String mId;
        private ArrayList<Chat> userChatList;

        public User (User u) {
            userChatList = u.getChatList();
            mId = u.getId();
        }
        public User() {
            userChatList = new ArrayList<Chat>();

        }
        public User(String id) {
            userChatList = new ArrayList<Chat>();
            mId = id;
            // empty constructor
        }


        public String getId() {
            return mId;
        }

        public ArrayList<Chat> getChatList() {
            return userChatList;
        }

        public void addChat(Chat chat) {
            chat.addUser(this);
            userChatList.add(chat);
        }

        public void setId(String id) {
            mId = id;
        }
}
