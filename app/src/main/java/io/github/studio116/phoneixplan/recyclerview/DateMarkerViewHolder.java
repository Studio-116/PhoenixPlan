package io.github.studio116.phoneixplan.recyclerview;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import io.github.studio116.phoneixplan.R;

public class DateMarkerViewHolder extends TimelineAdapter.TimelineViewHolder {
    private final TextView date;
    public DateMarkerViewHolder(@NonNull View itemView) {
        super(itemView);
        date = itemView.findViewById(R.id.timeline_date_marker_text);
    }

    @Override
    void bind(TimelineAdapter.TimelineData data) {
        date.setText(((TimelineAdapter.DateMarkerData) data).date);
    }
}
