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
    private final DialogEditTimelineObjectBinding root;
    private final DateHandler timeFrom;
    private final DateHandler timeTo;
    public final AlertDialog dialog;

    public EditTimelineObjectDialog(Context context, Timeline timeline, TimelineObject object) {
        this.timeline = timeline;
        boolean isNew = object == null;
        this.object = !isNew ? object : new TimelineObject();

        // Setup UI
        LayoutInflater inflater = LayoutInflater.from(context);
        root = DialogEditTimelineObjectBinding.inflate(inflater, null, false);

        // Toggle Time-To Visibility
        root.objectType.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            root.objectTimeTo.setVisibility(group.getCheckedButtonId() == R.id.object_event_type ? View.VISIBLE : View.GONE);
            root.objectTimeFromText.setText(group.getCheckedButtonId() == R.id.object_event_type ? R.string.object_time_from_text : R.string.object_time_from_text_alt);
        });

        // Prefill
        if (!isNew) {
            // Name
            root.objectNameInput.setText(object.name);
            // Type
            root.objectType.check(object.isDeadline ? R.id.object_deadline_type : R.id.object_event_type);
            // Importance
            root.objectImportanceInput.setText(context.getResources().getStringArray(R.array.object_importance)[object.importance.ordinal()]);
            // Description
            root.objectDescriptionInput.setText(object.description);
        }

        // Setup Dates
        timeFrom = new DateHandler(object == null ? new Date() : object.timeFrom, root.objectTimeFromDate, root.objectTimeFromTime);
        timeTo = new DateHandler(object == null || object.isDeadline ? new Date() : object.timeTo, root.objectTimeToDate, root.objectTimeToTime);

        // Show Dialog
        dialog = new MaterialAlertDialogBuilder(context)
                .setTitle(isNew ? R.string.create_new : R.string.edit)
                .setView(root.getRoot())
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
            boolean isDeadline = root.objectType.getCheckedButtonId() != R.id.object_event_type;
            if (getText(root.objectNameInput).length() == 0) {
                // Missing Name
                root.objectName.setError(root.getRoot().getResources().getString(R.string.required));
                new MaterialAlertDialogBuilder(root.getRoot().getContext())
                        .setTitle(R.string.invalid_dialog_title)
                        .setMessage(R.string.missing_name)
                        .setNeutralButton(R.string.ok, null)
                        .show();
                return;
            } else if (!isDeadline && timeFrom.date.compareTo(timeTo.date) > 0) {
                // Invalid Date
                new MaterialAlertDialogBuilder(root.getRoot().getContext())
                        .setTitle(R.string.invalid_dialog_title)
                        .setMessage(R.string.invalid_date)
                        .setNeutralButton(R.string.ok, null)
                        .show();
                return;
            }

            // Name
            object.name = getText(root.objectNameInput);
            // Description
            object.description = getText(root.objectDescriptionInput);
            // Date
            object.timeFrom = timeFrom.date;
            object.isDeadline = isDeadline;
            object.timeTo = !object.isDeadline ? timeTo.date : null;
            // Importance
            int importanceIndex = Arrays.asList(root.getRoot().getResources().getStringArray(R.array.object_importance)).indexOf(getText(root.objectImportanceInput));
            object.importance = TimelineObject.Importance.values()[importanceIndex];

            // Save
            if (!timeline.objects.contains(object)) {
                timeline.objects.add(object);
            }
            timeline.save(root.getRoot().getContext().getApplicationContext());
        }
        // Close Dialog
        dialog.dismiss();
    }
}
