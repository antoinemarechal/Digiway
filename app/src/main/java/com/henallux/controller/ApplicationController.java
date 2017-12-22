package com.henallux.controller;

import com.henallux.dao.concrete.EventAPIAccess;
import com.henallux.dao.concrete.FriendshipAPIAccess;
import com.henallux.dao.concrete.UserAPIAccess;
import com.henallux.exception.DataAccessException;
import com.henallux.model.Event;
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

    public ArrayList<Event> getAllEvents() throws DataAccessException
    {
        return new EventAPIAccess().getAllEvents();
    }

    public void addFriendRequest(long senderId, long targetId) throws DataAccessException
    {
        new FriendshipAPIAccess().addFriendship(senderId, targetId);
    }
}
