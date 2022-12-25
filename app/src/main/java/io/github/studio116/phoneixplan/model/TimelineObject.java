package io.github.studio116.phoneixplan.model;

import java.util.Date;
import java.util.Objects;

public class TimelineObject {
    public enum Importance {
        LOW,
        NORMAL,
        HIGH,
        VERY_HIGH
    }

    public Importance importance;
    public String name;
    public boolean isDeadline;
    public Date timeFrom;
    public Date timeTo;
    public String description;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TimelineObject that = (TimelineObject) o;
        return importance == that.importance && Objects.equals(name, that.name) && Objects.equals(timeFrom, that.timeFrom) && Objects.equals(timeTo, that.timeTo) && Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(importance, name, timeFrom, timeTo, description);
    }
}
