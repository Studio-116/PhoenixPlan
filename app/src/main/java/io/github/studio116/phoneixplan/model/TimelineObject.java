package io.github.studio116.phoneixplan.model;

import androidx.annotation.Keep;

import java.util.Date;

public abstract class TimelineObject {
    public enum Importance {
        LOW,
        NORMAL,
        HIGH,
        VERY_HIGH
    }

    protected abstract String getType();
    @Keep
    public final String type = getType();

    public abstract Date getDate();
    public abstract Importance getImportance();
    public abstract String getName();
}
