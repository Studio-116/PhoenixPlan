package io.github.studio116.phoneixplan.recyclerview;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.text.DateFormat;

import io.github.studio116.phoneixplan.R;
import io.github.studio116.phoneixplan.Util;
import io.github.studio116.phoneixplan.model.DeadlineObject;
import io.github.studio116.phoneixplan.model.EventObject;

public class TimelineObjectViewHolder extends TimelineAdapter.TimelineViewHolder {
    public final View importance;
    public final TextView name;
    public final TextView date;
    public TimelineObjectViewHolder(@NonNull View itemView) {
        super(itemView);
        importance = itemView.findViewById(R.id.timeline_object_importance_color_bar);
        name = itemView.findViewById(R.id.timeline_object_name);
        date = itemView.findViewById(R.id.timeline_object_date);
    }

    @Override
    void bind(TimelineAdapter.TimelineData data) {
        TimelineAdapter.TimelineObjectData fullData = (TimelineAdapter.TimelineObjectData) data;
        int importanceColor;
        switch (fullData.object.getImportance()) {
            case LOW: {
                importanceColor = R.color.importance_low;
                break;
            }
            case NORMAL: {
                importanceColor = R.color.importance_normal;
                break;
            }
            case HIGH: {
                importanceColor = R.color.importance_high;
                break;
            }
            case VERY_HIGH: {
                importanceColor = R.color.importance_very_high;
                break;
            }
            default: {
                throw new UnsupportedOperationException();
            }
        }
        importance.setBackgroundColor(importance.getResources().getColor(importanceColor));
        name.setText(fullData.object.getName());
        DateFormat formatter = DateFormat.getTimeInstance(DateFormat.SHORT);
        if (fullData.object instanceof DeadlineObject) {
            date.setText(formatter.format(((DeadlineObject) fullData.object).time));
        } else {
            String timeFrom = formatter.format(((EventObject) fullData.object).timeFrom);
            DateFormat formatter2 = formatter;
            if (Util.isNotSameDay(((EventObject) fullData.object).timeFrom, ((EventObject) fullData.object).timeTo)) {
                formatter2 = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT);
            }
            String timeTo = formatter2.format(((EventObject) fullData.object).timeTo);
            date.setText(String.format(date.getResources().getString(R.string.timeline_object_event_time_format), timeFrom, timeTo));
        }
    }
}
