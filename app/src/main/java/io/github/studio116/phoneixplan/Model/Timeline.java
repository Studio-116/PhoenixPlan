package io.github.studio116.phoneixplan.Model;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

public class Timeline {
    private JSONObject Data = new JSONObject(); // Your timeline data

    public Timeline() {

    }

    /**
     * Creates an calender event
     * @param Name
     * @param EventVal
     * */
    public void createEvent(String Name, EventTimeline EventVal) throws JSONException {
        JSONObject EventJson = new JSONObject();
        EventJson.put("Date", EventVal.DATE.toString());
        EventJson.put("Description", EventVal.Description);

        Data.put(Name, EventJson);
    }

    /**
     * Saves the current timeline data
     * @param AppContext
     * */
    public void SaveToFile(Context AppContext) {
        SaveTypeAdapter.write(AppContext,"timeline.json", this.Data);
    }
}
