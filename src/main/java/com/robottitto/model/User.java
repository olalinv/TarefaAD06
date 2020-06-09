package com.robottitto.model;

import java.util.ArrayList;

public class User {
    private String nome;
    private String username;
    private String password;
    private ArrayList<String> follows;

    public User() {
    }

    public User(String username, String nome) {
        this.username = username;
        this.nome = nome;
    }

    public User(String username, String nome, String password) {
        this.username = username;
        this.nome = nome;
        this.password = password;
        this.follows = new ArrayList<String>();
    }

    public User(String username, String nome, String password, ArrayList<String> follows) {
        this.username = username;
        this.nome = nome;
        this.password = password;
        this.follows = follows;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public ArrayList<String> getFollows() {
        return follows;
    }

    public void setFollows(ArrayList<String> follows) {
        this.follows = follows;
    }

}
