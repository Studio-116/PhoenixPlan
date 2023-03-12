package io.github.studio116.phoenixplan.dialog;

import android.content.Context;
import android.view.View;

import androidx.appcompat.app.AlertDialog;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.UUID;

import io.github.studio116.phoenixplan.R;
import io.github.studio116.phoenixplan.model.Timeline;
import io.github.studio116.phoenixplan.model.TimelineObject;

public class ViewTimelineObjectDialog implements View.OnClickListener {
    private final Timeline timeline;
    private final TimelineObject object;
    public final AlertDialog dialog;

    public ViewTimelineObjectDialog(Context context, Timeline timeline, UUID id) {
        this.timeline = timeline;
        this.object = timeline.get(id);

        // Show Dialog
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context)
                .setTitle(object.name)
                .setPositiveButton(R.string.edit, null)
                .setNegativeButton(R.string.delete, null)
                .setNeutralButton(R.string.ok, null);
        if (object.description.length() > 0) {
            builder.setMessage(object.description);
        }
        dialog = builder.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(this);
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(this);
        dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == dialog.getButton(AlertDialog.BUTTON_POSITIVE)) {
            // Edit
            dialog.dismiss();
            new EditTimelineObjectDialog(dialog.getContext(), timeline, object.id);
        } else if (v == dialog.getButton(AlertDialog.BUTTON_NEGATIVE)) {
            // Delete
            new DeleteTimelineObjectDialog(v.getContext(), dialog, timeline, object.id);
        } else if (v == dialog.getButton(AlertDialog.BUTTON_NEUTRAL)) {
            // OK
            dialog.dismiss();
        }
    }
}
