package io.github.studio116.phoneixplan.recyclerview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import io.github.studio116.phoneixplan.R;
import io.github.studio116.phoneixplan.Util;
import io.github.studio116.phoneixplan.model.Timeline;
import io.github.studio116.phoneixplan.model.TimelineObject;

public class TimelineAdapter extends ListAdapter<TimelineAdapter.TimelineData, TimelineAdapter.TimelineViewHolder> {
    abstract static class TimelineViewHolder extends RecyclerView.ViewHolder {
        public TimelineViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        abstract void bind(TimelineData data);
    }

    public final Timeline timeline;
    public TimelineAdapter(Timeline timeline) {
        super(new DiffUtil.ItemCallback<TimelineData>() {
            @Override
            public boolean areItemsTheSame(@NonNull TimelineData oldItem, @NonNull TimelineData newItem) {
                return oldItem.is(newItem);
            }

            @Override
            public boolean areContentsTheSame(@NonNull TimelineData oldItem, @NonNull TimelineData newItem) {
                return oldItem.equals(newItem);
            }
        });
        this.timeline = timeline;
        rebuild();
    }

    interface TimelineData {
        boolean equals(Object o);
        boolean is(Object o);
    }
    static class TimelineObjectData implements TimelineData {
        public final TimelineObject object;
        public TimelineObjectData(TimelineObject object) {
            this.object = object;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            TimelineObjectData that = (TimelineObjectData) o;
            return Objects.equals(object, that.object);
        }

        @Override
        public boolean is(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            TimelineObjectData that = (TimelineObjectData) o;
            return object == that.object;
        }

        @Override
        public int hashCode() {
            return Objects.hash(object);
        }
    }
    static class DateMarkerData implements TimelineData {
        public final LocalDate date;
        public DateMarkerData(LocalDate date) {
            this.date = date;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            DateMarkerData that = (DateMarkerData) o;
            return Objects.equals(date, that.date);
        }

        @Override
        public boolean is(Object o) {
            return equals(o);
        }

        @Override
        public int hashCode() {
            return Objects.hash(date);
        }
    }
    public void rebuild() {
        List<TimelineData> data = new ArrayList<>();
        List<TimelineObject> objects = new ArrayList<>(timeline.objects);
        Collections.sort(objects, Comparator.comparing(TimelineObject::getDate));
        TimelineObject last = null;
        for (TimelineObject object : objects) {
            if (last == null || Util.isNotSameDay(last.getDate(), object.getDate())) {
                DateMarkerData dateMarker = new DateMarkerData(object.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
                data.add(dateMarker);
            }
            TimelineObjectData objectData = new TimelineObjectData(object);
            data.add(objectData);
            last = object;
        }
        submitList(data);
    }

    @NonNull
    @Override
    public TimelineAdapter.TimelineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        if (viewType == 0) {
            // Timeline Object
            return new TimelineObjectViewHolder(inflater.inflate(R.layout.timeline_object, parent, false));
        } else {
            // Date Marker
            return new DateMarkerViewHolder(inflater.inflate(R.layout.timeline_date_marker, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull TimelineAdapter.TimelineViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position) instanceof DateMarkerData ? 1 : 0;
    }
}
