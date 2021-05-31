package com.example.demo.Security;

public class QueryState {
    public static final String SELECT_USER = "SELECT username, password, true enabled FROM people WHERE id=?";
    public static final String SELECT_AUTH = "SELECT roles authority FROM people WHERE id=?";
}
