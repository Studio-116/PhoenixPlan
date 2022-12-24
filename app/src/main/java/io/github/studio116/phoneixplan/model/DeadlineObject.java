package io.github.studio116.phoneixplan.model;

import java.util.Date;
import java.util.Objects;

public class DeadlineObject extends TimelineObject {
    public Importance importance;
    public String name;
    public Date time;
    public String description;

    public static final String TYPE = "deadline";
    @Override
    protected String getType() {
        return TYPE;
    }

    @Override
    public Date getDate() {
        return time;
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
        DeadlineObject that = (DeadlineObject) o;
        return importance == that.importance && Objects.equals(name, that.name) && Objects.equals(time, that.time) && Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(importance, name, time, description);
    }
}
