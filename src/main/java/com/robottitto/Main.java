package com.robottitto;

import com.mongodb.client.model.Filters;
import com.robottitto.model.Config;
import com.robottitto.model.Message;
import com.robottitto.model.User;
import com.robottitto.service.MessageService;
import com.robottitto.service.UserService;
import com.robottitto.util.DBUtil;
import com.robottitto.util.JsonUtil;
import org.bson.BsonDocument;
import org.bson.conversions.Bson;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Main {

    private static final String CONFIG_JSON = "config.json";
    private static final int PAGE_SIZE = 5;
    private static Scanner scanner = new Scanner(System.in);
    private static User currentUser = null;
    private static int userStatus = 0;

    public static void main(String[] args) throws IOException {
        Config config = JsonUtil.readConfig(CONFIG_JSON);
        DBUtil.connect(config);
        showMenu();
    }

    private static void showMenu() {
        int option1 = -1;
        int option2 = -1;
        while (true) {
            try {
                if (userStatus == 0) {
                    showMenuOptions();
                    option1 = Integer.parseInt(scanner.nextLine());
                }
                switch (option1) {
                    case 1:
                        showForm("register");
                        break;
                    case 2:
                        if (userStatus == 0) {
                            showForm("login");
                        } else {
                            showMenuOptions();
                            option2 = Integer.parseInt(scanner.nextLine());
                            switch (option2) {
                                case 1:
                                    showMessages(new BsonDocument());
                                    break;
                                case 2:
                                    if (!currentUser.getFollows().isEmpty()) {
                                        ArrayList<Bson> filters = new ArrayList<>();
                                        for (String user : currentUser.getFollows()) {
                                            filters.add(Filters.eq("user.username", user));
                                        }
                                        Bson filter = Filters.or(filters);
                                        showMessages(filter);
                                    } else {
                                        System.err.println("Non segue a ningún usuario.\n");
                                    }
                                    break;
                                case 3:
                                    System.out.println("Introduza o hashtag: ");
                                    String hashtag = scanner.nextLine();
                                    hashtag = hashtag.replace("#", "");
                                    Bson filter = Filters.eq("hashtags", hashtag);
                                    showMessages(filter);
                                    break;
                                case 4:
                                    addMessage();
                                    break;
                                case 5:
                                    System.out.println("Introduza o alias: ");
                                    String alias = scanner.nextLine();
                                    ArrayList<Bson> filters = new ArrayList<>();
                                    filters.add(Filters.not(Filters.eq("username", currentUser.getUsername())));
                                    filters.add(Filters.regex("username", Pattern.compile(alias)));
                                    filter = Filters.and(filters);
                                    findUsers(filter);
                                    break;
                                case 0:
                                    System.exit(0);
                                    break;
                                default:
                                    System.out.println("Non existe a opción introducida.");
                                    break;
                            }
                        }

                        break;
                    case 0:
                        System.exit(0);
                        break;
                    default:
                        System.out.println("Opción descoñecida.");
                        break;
                }
            } catch (NumberFormatException e) {
                System.err.println("\nOpción descoñecida.\n");
            } catch (Exception e) {
                if (e.getLocalizedMessage() != null) {
                    System.err.println(e.getLocalizedMessage());
                }
            }
        }
    }

    private static void showMenuOptions() {
        System.out.println("Opcións dispoñibles: ");
        switch (userStatus) {
            case 0:
                // User is not logged in
                System.out.println("[1] Rexistrarse");
                System.out.println("[2] Login");
                break;
            case 1:
                // User is logged in
                System.out.println("[1] Ver tódalas mensaxes");
                System.out.println("[2] Ver mensaxes de usuarios que sigo");
                System.out.println("[3] Buscar por hashtag");
                System.out.println("[4] Escribir unha mensaxe");
                System.out.println("[5] Buscar usuarios");
                break;
        }
        System.out.println("[0] Saír da aplicación");
        System.out.println("Elixa unha opción: ");
    }

    private static void showForm(String type) throws Exception {
        switch (type) {
            case "register":
                System.out.println("Introduza o alias: ");
                String username = scanner.nextLine();
                if (username.isEmpty()) {
                    throw new Exception("O alias é obrigatorio.");
                }
                if (UserService.userExists(username)) {
                    throw new Exception("Ese alias xa existe.");
                }
                System.out.println("Introduza o nome: ");
                String nome = scanner.nextLine();
                if (nome.isEmpty()) {
                    throw new Exception("O nome é obrigatorio.");
                }
                System.out.println("Introduza o contrasinal: ");
                String password = scanner.nextLine();
                if (password.isEmpty()) {
                    throw new Exception("O contrasinal é obrigatorio.");
                } else {
                    User user = new User(username, nome, password);
                    UserService.addUser(user);
                }
                break;
            case "login":
                System.out.println("Introduza o alias: ");
                username = scanner.nextLine();
                System.out.println("Introduza o contrasinal: ");
                password = scanner.nextLine();
                if (!username.isEmpty() && !password.isEmpty()) {
                    currentUser = UserService.getUser(username, password);
                    if (currentUser != null) {
                        userStatus = 1;
                        System.out.println("Benvid@ " + currentUser.getNome());
                    } else {
                        throw new Exception("Non se atopou o usuario.");
                    }
                } else {
                    throw new Exception("O alias e o contrasinal son obrigatorios.");
                }
                break;
        }
    }

    private static void findUsers(Bson filter) throws Exception {
        long usersCount = UserService.getUsersCount(filter);
        if (usersCount == 0) {
            System.out.println("Non hai usuarios.");
            return;
        }
        for (int i = 0; i < usersCount; i += PAGE_SIZE) {
            System.out.printf("%-10s %-10s %-10s\n", "Alias", "Nome", "Seguidor");
            System.out.printf("----------------------------------------------------------------------------------------\n");
            ArrayList<User> users = UserService.getUsers(PAGE_SIZE, i, filter);
            for (User user : users) {
                System.out.printf("%-10s %-10s %-10s\n", user.getUsername(), user.getNome(), currentUser.getFollows().contains(user.getUsername()) ? "Sí" : "Non");
            }
            if (usersCount > (i + PAGE_SIZE)) {
                System.out.println("Mostrando " + (i + 1) + " a " + (i + PAGE_SIZE) + " de " + usersCount + " usuarios.");
                System.out.println("Pulse Intro para ver máis usuarios...");
                scanner.nextLine();
            } else {
                System.out.println("Mostrando " + (i + 1) + " a " + usersCount + " de " + usersCount + " usuarios.");
                System.out.println("Non hai máis usuarios.");
            }
        }
        System.out.println("Introduza o alias do usuario para seguir: ");
        String alias = scanner.nextLine();
        if (alias.isEmpty()) {
            throw new Exception("O alias é obrigatorio.");
        } else if (alias.equals(currentUser.getUsername())) {
            throw new Exception("Non se pode seguir a si mesmo.");
        } else if (currentUser.getFollows().contains(alias)) {
            throw new Exception("Xa segue a ese usuario.");
        } else if (!UserService.userExists(alias)) {
            throw new Exception("Non se atopa ese usuario.");
        } else {
            UserService.addUserToFollow(currentUser, alias);
            currentUser = UserService.getUser(currentUser.getUsername(), null);
        }
    }

    private static void showMessages(Bson filter) {
        long messagesCount = MessageService.getMessagesCount(filter);
        if (messagesCount == 0) {
            System.out.println("Non hai mensaxes.");
            return;
        }
        for (int i = 0; i < messagesCount; i += PAGE_SIZE) {
            System.out.printf("%-20s %-10s %-15s %-25s\n", "Data", "Alias", "Nome", "Texto");
            System.out.printf("----------------------------------------------------------------------------------------\n");
            ArrayList<Message> messages = MessageService.getMessages(PAGE_SIZE, i, filter);
            for (Message message : messages) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                String messageDate = dateFormat.format(message.getDate());
                System.out.printf("%-20s %-10s %-15s %-25s\n", messageDate, message.getUser().getUsername(), message.getUser().getNome(), message.getText());
            }
            if (messagesCount > (i + PAGE_SIZE)) {
                System.out.println("Mostrando " + (i + 1) + " a " + (i + PAGE_SIZE) + " de " + messagesCount + " mensaxes.");
                System.out.println("Pulse Intro para ver máis mensaxes...");
                scanner.nextLine();
            } else {
                System.out.println("Mostrando " + (i + 1) + " a " + messagesCount + " de " + messagesCount + " mensaxes.");
                System.out.println("Non hai máis mensaxes.");
            }
        }
    }

    private static void addMessage() throws Exception {
        System.out.println("Introduza a mensaxe: ");
        String text = scanner.nextLine();
        if (text.isEmpty()) {
            throw new Exception("A mensaxe é obrigatoria.");
        }
        Message message = new Message(text, currentUser);
        MessageService.addMessage(message);
    }

}
