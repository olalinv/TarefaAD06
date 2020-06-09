package com.robottitto.dao;

import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Sorts;
import com.robottitto.model.User;
import com.robottitto.util.DBUtil;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;

public class UserDAO {

    public static ArrayList<User> getUsers(int pageSize, int pageNumber, Bson filter) {
        ArrayList<User> users = new ArrayList<>();
        MongoCursor<Document> cursor = DBUtil.getUserCollection().find(filter).sort(Sorts.ascending("username")).limit(pageSize).skip(pageNumber).iterator();
        try {
            while (cursor.hasNext()) {
                Document documentMessage = cursor.next();
                User user = new User(
                        (String) documentMessage.get("username"),
                        (String) documentMessage.get("nome")
                );
                users.add(user);
            }
        } catch (Exception exception) {
            System.out.print("Error: " + exception);
        } finally {
            cursor.close();
        }
        return users;
    }

    public static User getUser(String username, String password) {
        User user = null;
        Document document = new Document().append("username", username);
        if (password != null) {
            document.append("password", password);
        }
        Document result = DBUtil.getUserCollection().find(document).first();
        if (result != null) {
            user = new User(
                    (String) result.get("username"),
                    (String) result.get("nome"),
                    (String) result.get("password"),
                    (ArrayList<String>) result.get("follows")
            );
        }
        return user;
    }

    public static void addUser(User user) {
        Document document = new Document()
                .append("username", user.getUsername())
                .append("nome", user.getNome())
                .append("password", user.getPassword())
                .append("follows", user.getFollows());
        if (!userExists(user.getUsername())) {
            DBUtil.getUserCollection().insertOne(document);
            System.out.println("Engadiuse o usuario.\n");
        } else {
            System.err.println("O alias xa existe. Elixa outro.\n");
        }
    }

    public static void addUserToFollow(User user, String userToFollow) {
        if (userExists(userToFollow)) {
            DBUtil.getUserCollection().updateOne(
                    new Document("username", user.getUsername()),
                    new Document("$push", new Document("follows", userToFollow)));
            System.out.println("Agora segues a " + userToFollow + ".\n");
        } else {
            System.err.println("Non se atopa o usuario.\n");
        }
    }

    public static boolean userExists(String username) {
        Document document = new Document().append("username", username);
        Document result = DBUtil.getUserCollection().find(document).first();
        return result != null;
    }

    public static long getUsersCount(Bson filter) {
        return DBUtil.getUserCollection().countDocuments(filter);
    }

}
