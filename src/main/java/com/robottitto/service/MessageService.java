package com.robottitto.service;

import com.robottitto.dao.MessageDAO;
import com.robottitto.model.Message;
import org.bson.conversions.Bson;

import java.util.ArrayList;

public class MessageService {

    public static ArrayList<Message> getMessages(int pageSize, int pageNumber, Bson filter) {
        return MessageDAO.getMessages(pageSize, pageNumber, filter);
    }

    public static void addMessage(Message message) {
        MessageDAO.addMessage(message);
    }

    public static long getMessagesCount(Bson filter) {
        return MessageDAO.getMessagesCount(filter);
    }

}
