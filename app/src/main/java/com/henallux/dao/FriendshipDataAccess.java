package com.henallux.dao;

import com.henallux.exception.DataAccessException;

public interface FriendshipDataAccess
{
    public abstract void addFriendship(long senderId, long targetId) throws DataAccessException;
}
