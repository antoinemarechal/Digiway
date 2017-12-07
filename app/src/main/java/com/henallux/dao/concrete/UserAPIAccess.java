package com.henallux.dao.concrete;

import com.henallux.dao.UserDataAccess;
import com.henallux.digiway.R;
import com.henallux.exceptions.AlreadyExistingException;
import com.henallux.exceptions.DataAccessException;
import com.henallux.model.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class UserAPIAccess implements UserDataAccess
{
    @Override
    public ArrayList<User> getAllUsers() throws DataAccessException
    {
        ArrayList<User> users;

        try
        {
            URLConnection connection = APIConnection.getInstance().connect("users");

            users = jsonStringToUserCollection(APIConnection.getInstance().readFromConnection(connection));
        }
        catch(JSONException e)
        {
            throw new DataAccessException(R.string.error_data_parsing);
        }
        catch(Exception e)
        {
            throw new DataAccessException(R.string.error_data_access);
        }

        return users;
    }

    @Override
    public User getUserWithUsername(String username) throws DataAccessException
    {
        try
        {
            HttpURLConnection connection = (HttpURLConnection) APIConnection.getInstance().connect("users/username/" + username);

            if(connection.getResponseCode() == HttpURLConnection.HTTP_NOT_FOUND)
                return null;
            else
                return jsonStringToUser(APIConnection.getInstance().readFromConnection(connection));
        }
        catch(JSONException e)
        {
            throw new DataAccessException(R.string.error_data_parsing);
        }
        catch(IOException e)
        {
            throw new DataAccessException(R.string.error_data_access);
        }
    }

    @Override
    public void addUser(User user) throws DataAccessException
    {
        try
        {
            HttpURLConnection connection = (HttpURLConnection) APIConnection.getInstance().connect("users");
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-type", "application/json");
            connection.setChunkedStreamingMode(0);

            APIConnection.getInstance().writeToConnection(connection, userToJSONString(user));

            int responseCode = connection.getResponseCode();

            if(responseCode != HttpURLConnection.HTTP_CREATED)
            {
                if(responseCode == HttpURLConnection.HTTP_CONFLICT)
                    throw new AlreadyExistingException();
                else
                    throw new DataAccessException(R.string.error_data_access);
            }
        }
        catch (JSONException e)
        {
            throw new DataAccessException(R.string.error_data_parsing);
        }
        catch (IOException e)
        {
            throw new DataAccessException(R.string.error_data_access);
        }
    }

    private ArrayList<User> jsonStringToUserCollection(String jsonString) throws JSONException
    {
        JSONArray jsonArray = new JSONArray(jsonString);
        ArrayList<User> users = new ArrayList<>();

        for(int i = 0; i < jsonArray.length(); i++)
        {
            JSONObject jsonUser = jsonArray.getJSONObject(i);

            users.add(jsonObjectToUser(jsonUser));
        }

        return users;
    }

    private User jsonStringToUser(String jsonString) throws JSONException
    {
        JSONObject jsonUser = new JSONObject(jsonString);

        return jsonObjectToUser(jsonUser);
    }

    private final String API_DATE_FORMAT = "yyyy-MM-dd";

    private final String ID_ID = "userId";
    private final String FIRST_NAME_ID = "firstName";
    private final String LAST_NAME_ID = "lastName";
    private final String BIRTHDAY_ID = "birthDate";
    private final String ADDRESS_ID = "address";
    private final String CITY_ID = "city";
    private final String ZIP_CODE_ID = "zip";
    private final String USERNAME_ID = "login";
    private final String PASSWORD_ID = "password";
    private final String IBAN_ACCOUNT_ID = "ibanAccount";
    private final String PHONE_ID_ID = "telNumber";
    private final String BALANCE_ID = "money";
    private final String RIGHTS_ID = "accessRights";

    private User jsonObjectToUser(JSONObject jsonUser) throws JSONException
    {
        Calendar birthday = Calendar.getInstance();
        DateFormat format = new SimpleDateFormat(API_DATE_FORMAT);

        try
        {
            birthday.setTime(format.parse(jsonUser.getString(BIRTHDAY_ID).substring(0, 10)));
        }
        catch (ParseException e) {}

        return new User(
                jsonUser.getLong(ID_ID),
                jsonUser.getString(FIRST_NAME_ID),
                jsonUser.getString(LAST_NAME_ID),
                birthday,
                jsonUser.getString(ADDRESS_ID),
                jsonUser.getString(CITY_ID),
                jsonUser.getString(ZIP_CODE_ID),
                jsonUser.getString(USERNAME_ID),
                jsonUser.getString(PASSWORD_ID),
                jsonUser.getString(IBAN_ACCOUNT_ID),
                jsonUser.getString(PHONE_ID_ID),
                jsonUser.getDouble(BALANCE_ID));
    }

    private String userToJSONString(User user) throws JSONException
    {
        DateFormat format = new SimpleDateFormat(API_DATE_FORMAT);

        JSONObject jsonObject = new JSONObject();

        jsonObject.accumulate(FIRST_NAME_ID, user.getFirstName());
        jsonObject.accumulate(LAST_NAME_ID, user.getLastName());
        jsonObject.accumulate(BIRTHDAY_ID, format.format(user.getBirthday().getTime()) + "T00:00:00");
        jsonObject.accumulate(ADDRESS_ID, user.getAddress());
        jsonObject.accumulate(CITY_ID, user.getCity());
        jsonObject.accumulate(ZIP_CODE_ID, user.getZipCode());
        jsonObject.accumulate(USERNAME_ID, user.getUsername());
        jsonObject.accumulate(PASSWORD_ID, user.getHashedPassword());
        jsonObject.accumulate(PHONE_ID_ID, user.getPhoneId());
        jsonObject.accumulate(RIGHTS_ID, 1);

        return jsonObject.toString();
    }
}
