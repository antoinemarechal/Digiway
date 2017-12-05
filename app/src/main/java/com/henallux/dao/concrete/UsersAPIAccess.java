package com.henallux.dao.concrete;

import com.henallux.dao.UsersDataAccess;
import com.henallux.digiway.R;
import com.henallux.exceptions.DataAccessException;
import com.henallux.model.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class UsersAPIAccess implements UsersDataAccess
{
    @Override
    public ArrayList<User> getAllUsers() throws DataAccessException
    {
        ArrayList<User> users = new ArrayList<>();

        try
        {
            URL url = new URL("http://digiwayapi.azurewebsites.net/api/users");

            URLConnection connection = url.openConnection();

            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;

            while((line = br.readLine()) != null)
            {
                sb.append(line);
            }

            br.close();

            users = jsonToUsers(sb.toString());
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

    private ArrayList<User> jsonToUsers(String stringJSON) throws JSONException
    {
        JSONArray jsonArray = new JSONArray(stringJSON);
        ArrayList<User> users = new ArrayList<>();

        for(int i = 0; i < jsonArray.length(); i++)
        {
            JSONObject jsonUser = jsonArray.getJSONObject(i);

            users.add(new User(
                    jsonUser.getString("login"),
                    jsonUser.getString("password")
            ));
        }

        return users;
    }
}
