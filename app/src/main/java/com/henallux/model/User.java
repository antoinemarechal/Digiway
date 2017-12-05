package com.henallux.model;

import java.io.Serializable;

public class User implements Serializable
{
    private String username;
    private String hashedPassword;

    public User(String login, String password)
    {
        setUsername(login);
        setHashedPassword(password);
    }

    public String getUsername()
    {
        return username;
    }

    private void setUsername(String username)
    {
        this.username = username;
    }

    public String getHashedPassword()
    {
        return hashedPassword;
    }

    private void setHashedPassword(String hashedPassword)
    {
        this.hashedPassword = hashedPassword;
    }
}
