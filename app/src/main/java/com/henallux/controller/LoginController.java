package com.henallux.controller;

import com.henallux.dao.concrete.UsersAPIAccess;
import com.henallux.exceptions.DataAccessException;
import com.henallux.model.User;

import java.util.ArrayList;

public class LoginController
{
    public ArrayList<User> getAllUsers() throws DataAccessException
    {
        ArrayList<User> users = new UsersAPIAccess().getAllUsers();

        return users == null ? new ArrayList<User>() : users;
    }
}
