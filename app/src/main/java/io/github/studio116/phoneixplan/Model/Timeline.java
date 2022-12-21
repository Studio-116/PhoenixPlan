package io.github.studio116.phoneixplan.Model;

import static io.github.studio116.phoneixplan.Model.ModelTypeAdapter.DateToString;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class Timeline {
    private JSONObject Data; // Your timeline data

    public Timeline() {
        this.Data = new JSONObject();
    }

    /**
     * Handles file timeline data
     * */
    public Timeline(String JsonData) throws JSONException {
        this.Data = new JSONObject(JsonData);
    }

    /**
     * Creates an calender event
     * @param Name
     * @param EventVal
     * */
    public void createEvent(String Name, EventTimeline EventVal) throws JSONException {
        JSONObject EventJson = new JSONObject();
        EventJson.put("Date", DateToString(EventVal.DATE));
        EventJson.put("Description", EventVal.DESCRIPTION);

        this.Data.put(Name, EventJson);
    }

    /**
     * Gets an calender event
     * @param Name
     * */

    public EventTimeline getEvent(String Name) {
        EventTimeline eventTimeline = new EventTimeline();
        try {
            eventTimeline.convertFromJSON(this.Data.getJSONObject(Name));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return eventTimeline;
    }

    /**
     * Saves the current timeline data
     * @param AppContext
     * */
    public void SaveToFile(Context AppContext) {
        ModelTypeAdapter.write(AppContext,"timeline.json", this.Data);
    }
}
