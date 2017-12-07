package com.henallux.dao.concrete;

import com.henallux.dao.EventDataAccess;
import com.henallux.exceptions.DataAccessException;
import com.henallux.model.Event;

import java.util.ArrayList;

public class EventAPIAccess implements EventDataAccess
{
    @Override
    public ArrayList<Event> getAllEvents() throws DataAccessException
    {
        return null;
    }
}
