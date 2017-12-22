package com.henallux.dao.concrete;

import com.henallux.dao.EventDataAccess;
import com.henallux.exception.DataAccessException;
import com.henallux.model.Event;
import com.henallux.model.PointOfInterest;
import com.henallux.yetee.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLConnection;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;

public class EventAPIAccess implements EventDataAccess
{
    private final String ID_ID = "eventId";
    private final String NAME_ID = "name";
    private final String ADDRESS_ID = "address";
    private final String CITY_ID = "city";
    private final String ZIP_CODE_ID = "zip";
    private final String DATE_ID = "eventDate";
    private final String PRICE_ID = "ticketPrice";
    private final String DESCRIPTION_ID = "description";
    private final String CATEGORY_ID = "eventCategory";

    private EventCategoryAPIAccess eventCategoryAPIAccess;
    private PointOfInterestAPIAccess pointOfInterestAPIAccess;

    @Override
    public ArrayList<Event> getAllEvents() throws DataAccessException
    {
        eventCategoryAPIAccess = new EventCategoryAPIAccess();
        pointOfInterestAPIAccess = new PointOfInterestAPIAccess();

        ArrayList<Event> events;

        try
        {
            URLConnection connection = APIConnection.getInstance().connect("events");

            events = jsonStringToEventCollection(APIConnection.getInstance().readFromConnection(connection));

            Collections.sort(events, new Comparator<Event>() {
                @Override
                public int compare(Event event1, Event event2)
                {
                    return event1.getCategory().getId() - event2.getCategory().getId();
                }
            });
        }
        catch(JSONException e)
        {
            throw new DataAccessException(R.string.error_data_parsing);
        }
        catch(Exception e)
        {
            throw new DataAccessException(R.string.error_data_access);
        }

        return events;
    }

    private ArrayList<Event> jsonStringToEventCollection(String jsonString) throws JSONException, DataAccessException
    {
        JSONArray jsonArray = new JSONArray(jsonString);
        ArrayList<Event> events = new ArrayList<>();

        for(int i = 0; i < jsonArray.length(); i++)
        {
            JSONObject jsonEvent = jsonArray.getJSONObject(i);

            events.add(jsonObjectToEvent(jsonEvent));
        }

        return events;
    }

    private Event jsonObjectToEvent(JSONObject jsonEvent) throws JSONException, DataAccessException
    {
        Calendar date = Calendar.getInstance();
        DateFormat format = new SimpleDateFormat(APIConnection.API_DATE_FORMAT);

        try
        {
            date.setTime(format.parse(jsonEvent.getString(DATE_ID).substring(0, 10)));
        }
        catch (ParseException e) {}

        Event event = new Event(
                jsonEvent.getLong(ID_ID),
                jsonEvent.getString(NAME_ID),
                jsonEvent.getString(ADDRESS_ID),
                jsonEvent.getString(CITY_ID),
                jsonEvent.getString(ZIP_CODE_ID),
                date,
                jsonEvent.getDouble(PRICE_ID),
                jsonEvent.getString(DESCRIPTION_ID),
                eventCategoryAPIAccess.jsonObjectToEventCategory(jsonEvent.getJSONObject(CATEGORY_ID)));

        ArrayList<PointOfInterest> pointsOfInterest = pointOfInterestAPIAccess.getPointsOfInterest(event.getId());

        event.addPointOfInterest(pointsOfInterest);

        return event;
    }
}
