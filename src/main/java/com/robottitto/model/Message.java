package com.robottitto.model;

import com.robottitto.model.User;

import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Message {
    private String text;
    private User user;
    private Date date;
    private ArrayList<String> hashtags;

    public Message() {
    }

    public Message(String text, User user) {
        this.text = text;
        this.user = user;
        this.date = new Date();
        this.hashtags = extractHashTags();
    }

    public Message(String text, User user, Date date, ArrayList<String> hashtags) {
        this.text = text;
        this.user = user;
        this.date = date;
        this.hashtags = hashtags;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public ArrayList<String> getHashtags() {
        return hashtags;
    }

    public void setHashtags(ArrayList<String> hashtags) {
        this.hashtags = hashtags;
    }

    public ArrayList<String> extractHashTags() {
        ArrayList<String> hashes = new ArrayList<>();
        Pattern pattern = Pattern.compile("(#.[^\\s]+)");
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            hashes.add(matcher.group().substring(1));
        }
        return hashes;
    }

}
