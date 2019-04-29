package com.example.closebyswipe;

public class Message {
    private String senderID;
    private User sender;
    private String text;

    public Message() {
        sender = new User();
    }

    public Message(User sendUser, String msg, String userId) {
        System.out.println("User id in message constructor is " + userId);
        sender = new User(sendUser);
        text = msg;
        senderID = userId;
    }
    public String getText() {
        return text;
    }

    public String getUserId() {
        return senderID;
    }

    public void setUserId(String id) {
        senderID = id;
    }
    public void setText(String setTxt) {
        text = setTxt;
    }

}
