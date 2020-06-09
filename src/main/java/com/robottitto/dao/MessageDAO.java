package com.robottitto.dao;

import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Sorts;
import com.robottitto.model.Message;
import com.robottitto.model.User;
import com.robottitto.util.DBUtil;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.Date;

public class MessageDAO {

    public static ArrayList<Message> getMessages(int pageSize, int pageNumber, Bson filter) {
        ArrayList<Message> messages = new ArrayList<>();
        MongoCursor<Document> cursor = DBUtil.getMessageCollection().find(filter).sort(Sorts.descending("date")).limit(pageSize).skip(pageNumber).iterator();
        try {
            while (cursor.hasNext()) {
                Document documentMessage = cursor.next();
                Document documentUser = (Document) documentMessage.get("user");
                Message message = new Message(
                        (String) documentMessage.get("text"),
                        new User((String) documentUser.get("username"), (String) documentUser.get("nome")),
                        (Date) documentMessage.get("date"),
                        (ArrayList<String>) documentMessage.get("hashtags")
                );
                messages.add(message);
            }
        } catch (Exception exception) {
            System.out.print("Error: " + exception);
        } finally {
            cursor.close();
        }
        return messages;
    }

    public static void addMessage(Message message) {
        Document document = new Document()
                .append("text", message.getText())
                .append("user", new Document()
                        .append("nome", message.getUser().getNome())
                        .append("username", message.getUser().getUsername()))
                .append("date", message.getDate())
                .append("hashtags", message.getHashtags());
        DBUtil.getMessageCollection().insertOne(document);
        System.out.println("Engadiuse a mensaxe.\n");
    }

    public static long getMessagesCount(Bson filter) {
        return DBUtil.getMessageCollection().countDocuments(filter);
    }

}
