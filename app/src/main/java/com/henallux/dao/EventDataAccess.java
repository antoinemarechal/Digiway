package com.henallux.dao;

import com.henallux.exception.DataAccessException;
import com.henallux.model.Event;

import java.util.ArrayList;

public interface EventDataAccess
{
    public abstract ArrayList<Event> getAllEvents() throws DataAccessException;
}
