package io.github.studio116.phoenixplan.recyclerview;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import java.text.DateFormat;

import io.github.studio116.phoenixplan.R;
import io.github.studio116.phoenixplan.Util;
import io.github.studio116.phoenixplan.databinding.TimelineObjectBinding;
import io.github.studio116.phoenixplan.dialog.ViewTimelineObjectDialog;
import io.github.studio116.phoenixplan.model.Timeline;
import io.github.studio116.phoenixplan.model.TimelineObject;

public class TimelineObjectViewHolder extends TimelineAdapter.TimelineViewHolder implements View.OnClickListener {
    private final TimelineObjectBinding binding;
    private final Timeline timeline;
    private TimelineObject object = null;
    public TimelineObjectViewHolder(@NonNull View itemView, Timeline timeline) {
        super(itemView);
        binding = TimelineObjectBinding.bind(itemView);
        this.timeline = timeline;
    }

    private static DateFormat getTimeFormatter() {
        return DateFormat.getTimeInstance(DateFormat.SHORT);
    }
    private static DateFormat getDateTimeFormatter() {
        return DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT);
    }

    @Override
    void bind(TimelineAdapter.TimelineData data) {
        TimelineAdapter.TimelineObjectData fullData = (TimelineAdapter.TimelineObjectData) data;
        int importanceColor;
        switch (fullData.object.importance) {
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
        binding.timelineObjectImportanceColorBar.setBackgroundColor(ContextCompat.getColor(binding.getRoot().getContext(), importanceColor));
        binding.timelineObjectName.setText(fullData.object.name);
        DateFormat formatter = fullData.underCurrentDayHeader ? getTimeFormatter() : getDateTimeFormatter();
        if (fullData.object.isDeadline) {
            binding.timelineObjectDate.setText(formatter.format(fullData.object.timeFrom));
        } else {
            String timeFrom = formatter.format(fullData.object.timeFrom);
            DateFormat formatter2 = Util.isNotSameDay(fullData.object.timeFrom, fullData.object.timeTo) ? getDateTimeFormatter() : formatter;
            String timeTo = formatter2.format(fullData.object.timeTo);
            binding.timelineObjectDate.setText(binding.getRoot().getResources().getString(R.string.timeline_object_event_time_format, timeFrom, timeTo));
        }
        object = fullData.object;
        binding.cardView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (object != null) {
            new ViewTimelineObjectDialog(binding.getRoot().getContext(), timeline, object.id);
        }
    }
}
