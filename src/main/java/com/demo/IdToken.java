package com.demo;

import com.google.gson.Gson;

public class IdToken {
    String sub;
    String name;
    String nickname;
    String preferred_username;
    String email;
    Boolean email_verified;
    public String toString() {
        return new Gson().toJson(this);
    }
}
