package com.henallux.model;

import java.io.Serializable;

public class PointOfInterest implements Serializable
{
    private long id;
    private String label;
    private double latitude;
    private double longitude;
    private String category;

    public PointOfInterest(long id, String label, double latitude, double longitude, String category)
    {
        setId(id);
        setLabel(label);
        setLatitude(latitude);
        setLongitude(longitude);
        setCategory(category);
    }

    public long getId()
    {
        return id;
    }

    private void setId(long id)
    {
        this.id = id;
    }

    public String getLabel()
    {
        return this.label;
    }

    private void setLabel(String label)
    {
        this.label = label;
    }

    public double getLatitude()
    {
        return this.latitude;
    }

    private void setLatitude(double latitude)
    {
        this.latitude = latitude;
    }

    public double getLongitude()
    {
        return this.longitude;
    }

    private void setLongitude(double longitude)
    {
        this.longitude = longitude;
    }

    public String getCategory()
    {
        return category;
    }

    private void setCategory(String category)
    {
        this.category = category;
    }
}
