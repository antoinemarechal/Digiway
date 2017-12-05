package com.henallux.dao;

import com.henallux.exceptions.DataAccessException;
import com.henallux.model.User;

import java.util.ArrayList;

public interface UsersDataAccess
{
    public abstract ArrayList<User> getAllUsers() throws DataAccessException;
}
