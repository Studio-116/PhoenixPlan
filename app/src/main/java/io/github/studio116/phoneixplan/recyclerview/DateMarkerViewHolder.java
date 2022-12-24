package io.github.studio116.phoneixplan.recyclerview;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

import io.github.studio116.phoneixplan.R;

public class DateMarkerViewHolder extends TimelineAdapter.TimelineViewHolder {
    public final TextView date;
    public DateMarkerViewHolder(@NonNull View itemView) {
        super(itemView);
        date = itemView.findViewById(R.id.timeline_date_marker_text);
    }

    @Override
    void bind(TimelineAdapter.TimelineData data) {
        DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG);
        date.setText(((TimelineAdapter.DateMarkerData) data).date.format(formatter));
    }
}
