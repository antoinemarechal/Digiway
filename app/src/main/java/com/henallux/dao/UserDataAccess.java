package com.henallux.dao;

import com.henallux.exceptions.DataAccessException;
import com.henallux.model.User;

import java.util.ArrayList;

public interface UserDataAccess
{
    public abstract ArrayList<User> getAllUsers() throws DataAccessException;

    User getUserWithUsername(String username) throws DataAccessException;

    public abstract void addUser(User user) throws DataAccessException;
}
