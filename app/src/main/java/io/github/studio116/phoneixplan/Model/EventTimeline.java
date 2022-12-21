package io.github.studio116.phoneixplan.Model;

import static io.github.studio116.phoneixplan.Model.ModelTypeAdapter.StringToDate;

import android.annotation.SuppressLint;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class EventTimeline {
    @SuppressLint("SimpleDateFormat")
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss' aaa Z'");

    public Date DATE = new Date();
    public String DESCRIPTION = "";

    /**
     * Write your raw JSONObject to a EventTimeline Class
     * @param JsonObj
     * */

    public void convertFromJSON(JSONObject JsonObj) throws JSONException {
        String date = JsonObj.getString("Date");
        String description = JsonObj.getString("Description");

        this.DATE = StringToDate(date);
        this.DESCRIPTION = description;
    }
}
