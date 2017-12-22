package com.henallux.dao;

import com.henallux.exception.DataAccessException;
import com.henallux.model.User;

import java.util.ArrayList;

public interface UserDataAccess
{
    public abstract ArrayList<User> getAllUsers() throws DataAccessException;

    public abstract User getUserWithUsername(String username) throws DataAccessException;

    public abstract void addUser(User user) throws DataAccessException;
}
