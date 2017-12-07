package com.henallux.model;

import java.io.Serializable;
import java.util.Calendar;

public class User implements Serializable
{
    private long id;
    private String firstName;
    private String lastName;
    private Calendar birthday;
    private String address;
    private String city;
    private String zipCode;
    private String username;
    private String hashedPassword;

    private String ibanAccount;
    private String phoneId;
    private double balance;

    public User(String firstName, String lastName, Calendar birthday, String streetNumber, String streetName, String city, String zipCode, String country, String username, String password, String phoneId)
    {
        setFirstName(firstName);
        setLastName(lastName);
        setBirthday(birthday);
        setAddress(streetNumber + ", " + streetName);
        setCity(city);
        setZipCode(zipCode);
        setUsername(username);
        setHashedPassword(password);
        setPhoneId(phoneId);
    }

    public User(long id, String firstName, String lastName, Calendar birthday, String address, String city, String zipCode, String username, String password, String ibanAccount, String phoneId, double balance)
    {
        setId(id);
        setFirstName(firstName);
        setLastName(lastName);
        setBirthday(birthday);
        setAddress(address);
        setCity(city);
        setZipCode(zipCode);
        setUsername(username);
        setHashedPassword(password);
        setIbanAccount(ibanAccount);
        setPhoneId(phoneId);
        setBalance(balance);
    }

    public long getId()
    {
        return id;
    }

    private void setId(long id)
    {
        this.id = id;
    }

    public String getFirstName()
    {
        return firstName;
    }

    private void setFirstName(String firstName)
    {
        this.firstName = firstName;
    }

    public String getLastName()
    {
        return lastName;
    }

    private void setLastName(String lastName)
    {
        this.lastName = lastName;
    }

    public Calendar getBirthday()
    {
        return birthday;
    }

    private void setBirthday(Calendar birthday)
    {
        this.birthday = birthday;
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

    public String getUsername()
    {
        return username;
    }

    private void setUsername(String username)
    {
        this.username = username;
    }

    public String getHashedPassword()
    {
        return hashedPassword;
    }

    private void setHashedPassword(String hashedPassword)
    {
        this.hashedPassword = hashedPassword;
    }

    public String getIbanAccount()
    {
        return ibanAccount;
    }

    private void setIbanAccount(String ibanAccount)
    {
        this.ibanAccount = ibanAccount;
    }

    public String getPhoneId()
    {
        return phoneId;
    }

    private void setPhoneId(String phoneId)
    {
        this.phoneId = phoneId;
    }

    public double getBalance()
    {
        return balance;
    }

    private void setBalance(double balance)
    {
        this.balance = balance;
    }
}
