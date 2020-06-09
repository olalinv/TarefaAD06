package com.robottitto.util;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoTimeoutException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.robottitto.model.Config;
import org.bson.Document;

public class DBUtil {

    private final static String USER_COLLECTION = "usuario";
    private final static String MESSAGE_COLLECTION = "mensaxe";
    private static Config config;
    private static MongoClientURI mongoClientURI;
    private static MongoClient mongoClient;
    private static MongoDatabase db;

    public static MongoClient connect(Config config) {
        setConfig(config);
        if (mongoClient == null) {
            createMongoClient();
        }
        return mongoClient;
    }

    public static void createMongoClient() {
        try {
            mongoClientURI = getMongoClientURI();
            mongoClient = new MongoClient(mongoClientURI);
            // if database doesn't exists, MongoDB will create it for you
            db = mongoClient.getDatabase(config.getDbname());
        } catch (MongoTimeoutException e) {
            System.err.println("Non foi posile conectar coa BBDD.");
            System.exit(0);
        }
    }

    public static MongoClientURI getMongoClientURI() {
        String userInfo = config.getUsername().isEmpty() ? "" : config.getUsername() + ":" + config.getPassword() + "@";
        return new MongoClientURI("mongodb://" + userInfo + config.getAddress() + ":" + config.getPort() + "/" + config.getDbname() + "?" + "retryWrites=false");
    }

    public static MongoClient getMongoClient() {
        return mongoClient;
    }

    public static void setConfig(Config config) {
        DBUtil.config = config;
    }

    public static MongoDatabase getDB() {
        return getMongoClient().getDatabase(config.getDbname());
    }

    public static MongoCollection<Document> getUserCollection() {
        return getDB().getCollection(USER_COLLECTION);
    }

    public static MongoCollection<Document> getMessageCollection() {
        return getDB().getCollection(MESSAGE_COLLECTION);
    }

}
