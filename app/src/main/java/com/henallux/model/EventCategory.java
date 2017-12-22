package com.henallux.model;

import java.io.Serializable;

public class EventCategory implements Serializable
{
    private int id;
    private String name;

    public EventCategory(int id, String name)
    {
        setId(id);
        setName(name);
    }

    public int getId()
    {
        return id;
    }

    private void setId(int id)
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
}
