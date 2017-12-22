package com.henallux.dao.concrete;

import com.henallux.dao.PointOfInterestDataAccess;
import com.henallux.exception.DataAccessException;
import com.henallux.model.PointOfInterest;
import com.henallux.yetee.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLConnection;
import java.util.ArrayList;

public class PointOfInterestAPIAccess implements PointOfInterestDataAccess
{
    private final String ID_ID = "pointOfInterestId";
    private final String NAME_ID = "name";
    private final String DESCRIPTION_ID = "description";
    private final String LATITUDE_ID = "latitude";
    private final String LONGITUDE_ID = "longitude";

    @Override
    public ArrayList<PointOfInterest> getPointsOfInterest(long eventId) throws DataAccessException
    {
        try
        {
            URLConnection connection = APIConnection.getInstance().connect("pointsofinterest/eventid/" + eventId);

            return jsonStringToPointOfInterestCollection(APIConnection.getInstance().readFromConnection(connection));
        }
        catch(JSONException e)
        {
            throw new DataAccessException(R.string.error_data_parsing);
        }
        catch(Exception e)
        {
            throw new DataAccessException(R.string.error_data_access);
        }
    }

    private ArrayList<PointOfInterest> jsonStringToPointOfInterestCollection(String jsonString) throws JSONException
    {
        JSONArray jsonArray = new JSONArray(jsonString);
        ArrayList<PointOfInterest> pointsOfInterest = new ArrayList<>();

        for(int i = 0; i < jsonArray.length(); i++)
        {
            JSONObject jsonPointOfInterest = jsonArray.getJSONObject(i);

            pointsOfInterest.add(jsonObjectToPointOfInterest(jsonPointOfInterest));
        }

        return pointsOfInterest;
    }

    private PointOfInterest jsonObjectToPointOfInterest(JSONObject jsonPointOfInterest) throws JSONException
    {
        String[] splittedName = jsonPointOfInterest.getString(NAME_ID).split(" - ");

        return new PointOfInterest(
                jsonPointOfInterest.getLong(ID_ID),
                splittedName.length > 1 ? splittedName[1] : splittedName[0],
                jsonPointOfInterest.getDouble(LATITUDE_ID),
                jsonPointOfInterest.getDouble(LONGITUDE_ID),
                splittedName.length > 1 ? splittedName[0] : null
        );
    }
}
