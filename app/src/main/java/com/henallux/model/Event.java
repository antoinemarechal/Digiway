package com.henallux.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Event implements Serializable
{
    private long id;
    private String name;
    private String address;
    private String city;
    private String zipCode;
    private Calendar date;
    private double ticketPrice;
    private String description;

    private EventCategory category;
    private List<PointOfInterest> pointsOfInterest;

    public Event(long id, String name, String address, String city, String zipCode, Calendar date, double ticketPrice, String description, EventCategory category)
    {
        setId(id);
        setName(name);
        setAddress(address);
        setCity(city);
        setZipCode(zipCode);
        setDate(date);
        setTicketPrice(ticketPrice);
        setDescription(description);
        setCategory(category);

        pointsOfInterest = new ArrayList<>();
    }

    private void sortPointsOfInterest()
    {
        Collections.sort(pointsOfInterest, new Comparator<PointOfInterest>() {
            @Override
            public int compare(PointOfInterest poi1, PointOfInterest poi2)
            {
                return poi1.getCategory().compareTo(poi2.getCategory());
            }
        });
    }

    public long getId()
    {
        return id;
    }

    private void setId(long id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    private void setName(String name)
    {
        this.name = name;
    }

    public String getAddress()
    {
        return address;
    }

    private void setAddress(String address)
    {
        this.address = address;
    }

    public String getCity()
    {
        return city;
    }

    private void setCity(String city)
    {
        this.city = city;
    }

    public String getZipCode()
    {
        return zipCode;
    }

    private void setZipCode(String zipCode)
    {
        this.zipCode = zipCode;
    }

    public Calendar getDate()
    {
        return date;
    }

    private void setDate(Calendar date)
    {
        this.date = date;
    }

    public double getTicketPrice()
    {
        return ticketPrice;
    }

    private void setTicketPrice(double ticketPrice)
    {
        this.ticketPrice = ticketPrice;
    }

    public String getDescription()
    {
        return description;
    }

    private void setDescription(String description)
    {
        this.description = description;
    }

    public EventCategory getCategory()
    {
        return category;
    }

    private void setCategory(EventCategory category)
    {
        this.category = category;
    }

    public List<PointOfInterest> getPointsOfInterest()
    {
        return this.pointsOfInterest;
    }

    public void addPointOfInterest(PointOfInterest pointOfInterest)
    {
        this.pointsOfInterest.add(pointOfInterest);

        sortPointsOfInterest();
    }

    public void addPointOfInterest(ArrayList<PointOfInterest> pointsOfInterest)
    {
        this.pointsOfInterest.addAll(pointsOfInterest);

        sortPointsOfInterest();
    }
}
