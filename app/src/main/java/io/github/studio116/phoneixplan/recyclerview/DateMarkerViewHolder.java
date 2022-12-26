package io.github.studio116.phoneixplan.recyclerview;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import io.github.studio116.phoneixplan.databinding.TimelineDateMarkerBinding;

public class DateMarkerViewHolder extends TimelineAdapter.TimelineViewHolder {
    private final TimelineDateMarkerBinding binding;
    public DateMarkerViewHolder(@NonNull View itemView) {
        super(itemView);
        binding = TimelineDateMarkerBinding.bind(itemView);
    }

    @Override
    void bind(TimelineAdapter.TimelineData data) {
        binding.timelineDateMarkerText.setText(((TimelineAdapter.DateMarkerData) data).date);
    }
}
