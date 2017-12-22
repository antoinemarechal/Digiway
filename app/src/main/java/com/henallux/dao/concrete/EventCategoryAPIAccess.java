package com.henallux.dao.concrete;

import com.henallux.dao.EventCategoryDataAccess;
import com.henallux.model.EventCategory;

import org.json.JSONException;
import org.json.JSONObject;

class EventCategoryAPIAccess implements EventCategoryDataAccess
{
    private final String ID_ID = "eventCategoryId";
    private final String NAME_ID = "name";

    EventCategory jsonObjectToEventCategory(JSONObject jsonEventCategory) throws JSONException
    {
        return new EventCategory(
                jsonEventCategory.getInt(ID_ID),
                jsonEventCategory.getString(NAME_ID)
        );
    }
}
