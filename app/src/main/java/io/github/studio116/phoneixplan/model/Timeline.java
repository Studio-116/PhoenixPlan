package io.github.studio116.phoneixplan.model;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.InstanceCreator;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.util.ArrayList;
import java.util.List;

import io.github.studio116.phoneixplan.Util;

public class Timeline {
    public static final String FILE_NAME = "timeline.json";
    public List<TimelineObject> objects = new ArrayList<>();

    /**
     * Saves the current timeline data
     */
    public void save(Context context) {
        Gson gson = new Gson();
        Util.write(context, FILE_NAME, gson.toJson(this, Timeline.class));
    }

    /**
     * Loads the current timeline data
     */
    public void load(Context context) {
        String data = Util.read(context, FILE_NAME);
        if (data != null) {
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(TimelineObject.class, (JsonDeserializer<TimelineObject>) (json, typeOfT, context1) -> {
                        JsonObject jsonObject = json.getAsJsonObject();
                        JsonElement typeElement = jsonObject.get("type");
                        if (typeElement == null) {
                            throw new JsonParseException("Missing \"type\" Property");
                        }
                        String type = typeElement.getAsString();
                        Class<?> clazz;
                        switch (type) {
                            case DeadlineObject.TYPE: {
                                clazz = DeadlineObject.class;
                                break;
                            }
                            case EventObject.TYPE: {
                                clazz = EventObject.class;
                                break;
                            }
                            default: {
                                throw new JsonParseException("Unsupported Timeline Object: " + type);
                            }
                        }
                        return context1.deserialize(json, clazz);
                    })
                    // https://github.com/google/gson/issues/1887#issuecomment-837223846
                    .registerTypeAdapter(Timeline.class, (InstanceCreator<?>) type -> this)
                    .create();
            try {
                gson.fromJson(data, Timeline.class);
            } catch (Exception e) {
                // Invalid Data
                reset();
            }
        } else {
            // Reset
            reset();
        }
    }

    /**
     * Resets the current timeline data
     */
    public void reset() {
        objects.clear();
    }
}
