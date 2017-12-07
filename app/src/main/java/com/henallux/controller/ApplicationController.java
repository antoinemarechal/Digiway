package com.henallux.controller;

import com.henallux.dao.concrete.UserAPIAccess;
import com.henallux.exceptions.DataAccessException;
import com.henallux.model.User;

import java.util.ArrayList;

public class ApplicationController
{
    public ArrayList<User> getAllUsers() throws DataAccessException
    {
        ArrayList<User> users = new UserAPIAccess().getAllUsers();

        return users == null ? new ArrayList<User>() : users;
    }

    public User getUserWithUsername(String username) throws DataAccessException
    {
        return new UserAPIAccess().getUserWithUsername(username);
    }

    public void registerUser(User user) throws DataAccessException
    {
        new UserAPIAccess().addUser(user);
    }
}
