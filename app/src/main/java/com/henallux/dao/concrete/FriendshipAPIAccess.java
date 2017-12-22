package com.henallux.dao.concrete;

import com.henallux.dao.FriendshipDataAccess;
import com.henallux.exception.AlreadyExistingException;
import com.henallux.exception.DataAccessException;
import com.henallux.yetee.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;

public class FriendshipAPIAccess implements FriendshipDataAccess
{
    private final String ID_ID = "friendshipId";
    private final String SENDER_ID = "user";
    private final String TARGET_ID = "friend";
    private final String AWAITING_ID = "isAwaiting";

    @Override
    public void addFriendship(long senderId, long targetId) throws DataAccessException
    {
        try
        {
            HttpURLConnection connection = (HttpURLConnection) APIConnection.getInstance().connect("friendship");
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-type", "application/json");
            connection.setChunkedStreamingMode(0);

            APIConnection.getInstance().writeToConnection(connection, friendRequestDataToJSONString(senderId, targetId));

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

    private String friendRequestDataToJSONString(long senderId, long targetId) throws JSONException
    {
        JSONObject jsonObject = new JSONObject();

        jsonObject.accumulate(SENDER_ID, senderId);
        jsonObject.accumulate(TARGET_ID, targetId);

        return jsonObject.toString();
    }
}
