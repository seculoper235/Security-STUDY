package com.example.demo.Security.Config;

public class QueryState {
    public static final String SELECT_USER = "SELECT id username, password, true enabled FROM people WHERE id=?";
    public static final String SELECT_AUTH = "SELECT people_id username, role authority FROM myauthority WHERE people_id=?";
}
