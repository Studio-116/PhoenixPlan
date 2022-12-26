package io.github.studio116.phoneixplan.dialog;

import android.content.Context;
import android.content.ContextWrapper;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.timepicker.MaterialTimePicker;

import java.text.DateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import io.github.studio116.phoneixplan.R;
import io.github.studio116.phoneixplan.databinding.DialogEditTimelineObjectBinding;
import io.github.studio116.phoneixplan.model.Timeline;
import io.github.studio116.phoneixplan.model.TimelineObject;

public class EditTimelineObjectDialog implements View.OnClickListener {
    private static class DateHandler implements View.OnClickListener {
        private Date date;

        private final MaterialButton dateButton;
        private final MaterialButton timeButton;

        private DateHandler(Date date, MaterialButton dateButton, MaterialButton timeButton) {
            this.date = date;
            this.dateButton = dateButton;
            dateButton.setOnClickListener(this);
            this.timeButton = timeButton;
            timeButton.setOnClickListener(this);
            update();
        }

        private void update() {
            DateFormat formatter = DateFormat.getDateInstance(DateFormat.SHORT);
            dateButton.setText(formatter.format(date));
            formatter = DateFormat.getTimeInstance(DateFormat.SHORT);
            timeButton.setText(formatter.format(date));
        }

        // Get Activity From Context, Needed For Starting Fragments
        private static AppCompatActivity getActivityFromContext(Context context){
            while (context instanceof ContextWrapper) {
                if (context instanceof AppCompatActivity) {
                    return (AppCompatActivity) context;
                }
                context = ((ContextWrapper)context).getBaseContext();
            }
            throw new UnsupportedOperationException();
        }

        @Override
        public void onClick(View v) {
            if (v == dateButton) {
                // New Date
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                MaterialDatePicker<Long> picker = MaterialDatePicker.Builder.datePicker()
                        .setTitleText(R.string.pick_date)
                        .setSelection(calendar.getTimeInMillis())
                        .build();
                picker.addOnPositiveButtonClickListener(v1 -> {
                    Long selection = picker.getSelection();
                    if (selection != null) {
                        Calendar utcCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                        Date selectedDate = new Date(selection);
                        utcCalendar.setTime(selectedDate);
                        int year = utcCalendar.get(Calendar.YEAR);
                        int month = utcCalendar.get(Calendar.MONTH);
                        int day = utcCalendar.get(Calendar.DAY_OF_MONTH);
                        calendar.setTime(date);
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, month);
                        calendar.set(Calendar.DAY_OF_MONTH, day);
                        date = calendar.getTime();
                        // Update
                        update();
                    }
                });
                picker.show(getActivityFromContext(v.getContext()).getSupportFragmentManager(), picker.toString());
            } else if (v == timeButton) {
                // New Time
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                MaterialTimePicker picker = new MaterialTimePicker.Builder()
                        .setTitleText(R.string.pick_time)
                        .setInputMode(MaterialTimePicker.INPUT_MODE_CLOCK)
                        .setHour(calendar.get(Calendar.HOUR_OF_DAY))
                        .setMinute(calendar.get(Calendar.MINUTE))
                        .build();
                picker.addOnPositiveButtonClickListener(v1 -> {
                    calendar.setTime(date);
                    calendar.set(Calendar.HOUR_OF_DAY, picker.getHour());
                    calendar.set(Calendar.MINUTE, picker.getMinute());
                    date = calendar.getTime();
                    // Update
                    update();
                });
                picker.show(getActivityFromContext(v.getContext()).getSupportFragmentManager(), picker.toString());
            }
        }
    }

    private final Timeline timeline;
    private final TimelineObject object;
    private final DialogEditTimelineObjectBinding binding;
    private final DateHandler timeFrom;
    private final DateHandler timeTo;
    public final AlertDialog dialog;

    public EditTimelineObjectDialog(Context context, Timeline timeline, int id) {
        this.timeline = timeline;
        boolean isNew = id == -1;
        this.object = !isNew ? timeline.get(id) : new TimelineObject();

        // Setup UI
        LayoutInflater inflater = LayoutInflater.from(context);
        binding = DialogEditTimelineObjectBinding.inflate(inflater, null, false);

        // Toggle Time-To Visibility
        binding.objectType.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            binding.objectTimeTo.setVisibility(group.getCheckedButtonId() == R.id.object_event_type ? View.VISIBLE : View.GONE);
            binding.objectTimeFromText.setText(group.getCheckedButtonId() == R.id.object_event_type ? R.string.object_time_from_text : R.string.object_time_from_text_alt);
        });

        // Prefill
        if (!isNew) {
            // Name
            binding.objectNameInput.setText(object.name);
            // Type
            binding.objectType.check(object.isDeadline ? R.id.object_deadline_type : R.id.object_event_type);
            // Importance
            int importance = object.importance.ordinal();
            binding.objectImportanceInput.setText(context.getResources().getStringArray(R.array.object_importance)[importance], false);
            // Description
            binding.objectDescriptionInput.setText(object.description);
        }

        // Setup Dates
        timeFrom = new DateHandler(isNew ? new Date() : object.timeFrom, binding.objectTimeFromDate, binding.objectTimeFromTime);
        timeTo = new DateHandler(isNew || object.isDeadline ? new Date() : object.timeTo, binding.objectTimeToDate, binding.objectTimeToTime);

        // Show Dialog
        dialog = new MaterialAlertDialogBuilder(context)
                .setTitle(isNew ? R.string.create_new : R.string.edit)
                .setView(binding.getRoot())
                .setPositiveButton(isNew ? R.string.create : R.string.save, null)
                .setNegativeButton(R.string.cancel, null)
                .show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(this);
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(this);
    }

    private String getText(EditText edit) {
        Editable text = edit.getText();
        return text != null ? text.toString() : "";
    }

    @Override
    public void onClick(View v) {
        if (v == dialog.getButton(AlertDialog.BUTTON_POSITIVE)) {
            // Create/Save

            // Check Validity
            boolean isDeadline = binding.objectType.getCheckedButtonId() != R.id.object_event_type;
            if (getText(binding.objectNameInput).length() == 0) {
                // Missing Name
                binding.objectName.setError(binding.getRoot().getResources().getString(R.string.required_error));
                new MaterialAlertDialogBuilder(binding.getRoot().getContext())
                        .setMessage(R.string.missing_name)
                        .setNeutralButton(R.string.ok, null)
                        .show();
                return;
            } else if (!isDeadline && timeFrom.date.compareTo(timeTo.date) > 0) {
                // Invalid Date
                new MaterialAlertDialogBuilder(binding.getRoot().getContext())
                        .setMessage(R.string.invalid_date)
                        .setNeutralButton(R.string.ok, null)
                        .show();
                return;
            }

            // Name
            object.name = getText(binding.objectNameInput);
            // Description
            object.description = getText(binding.objectDescriptionInput);
            // Date
            object.timeFrom = timeFrom.date;
            object.isDeadline = isDeadline;
            object.timeTo = !object.isDeadline ? timeTo.date : null;
            // Importance
            int importanceIndex = Arrays.asList(binding.getRoot().getResources().getStringArray(R.array.object_importance)).indexOf(getText(binding.objectImportanceInput));
            object.importance = TimelineObject.Importance.values()[importanceIndex];

            // Save
            if (!timeline.objects.contains(object)) {
                timeline.objects.add(object);
            }
            timeline.save(binding.getRoot().getContext().getApplicationContext());
        }
        // Close Dialog
        dialog.dismiss();
    }
}
