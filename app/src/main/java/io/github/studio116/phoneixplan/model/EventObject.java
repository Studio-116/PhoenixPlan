package io.github.studio116.phoneixplan.model;

import java.util.Date;
import java.util.Objects;

public class EventObject extends TimelineObject {
    public Importance importance;
    public String name;
    public Date timeFrom;
    public Date timeTo;
    public String description;

    public static final String TYPE = "event";
    @Override
    protected String getType() {
        return TYPE;
    }

    @Override
    public Date getDate() {
        return timeFrom;
    }

    @Override
    public Importance getImportance() {
        return importance;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        EventObject that = (EventObject) o;
        return importance == that.importance && Objects.equals(name, that.name) && Objects.equals(timeFrom, that.timeFrom) && Objects.equals(timeTo, that.timeTo) && Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(importance, name, timeFrom, timeTo, description);
    }
}
