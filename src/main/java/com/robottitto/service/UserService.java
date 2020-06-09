package com.robottitto.service;

import com.robottitto.dao.UserDAO;
import com.robottitto.model.User;
import org.bson.conversions.Bson;

import java.util.ArrayList;

public class UserService {

    public static ArrayList<User> getUsers(int pageSize, int pageNumber, Bson filter) {
        return UserDAO.getUsers(pageSize, pageNumber, filter);
    }

    public static User getUser(String username, String password) {
        return UserDAO.getUser(username, password);
    }

    public static void addUser(User user) {
        UserDAO.addUser(user);
    }

    public static void addUserToFollow(User user, String userToFollow) {
        UserDAO.addUserToFollow(user, userToFollow);
    }

    public static boolean userExists(String username) {
        return UserDAO.userExists(username);
    }

    public static long getUsersCount(Bson filter) {
        return UserDAO.getUsersCount(filter);
    }

}
