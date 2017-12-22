package com.henallux.dao;

import com.henallux.exception.DataAccessException;
import com.henallux.model.PointOfInterest;

import java.util.ArrayList;

public interface PointOfInterestDataAccess
{
    public abstract ArrayList<PointOfInterest> getPointsOfInterest(long eventId) throws DataAccessException;
}
