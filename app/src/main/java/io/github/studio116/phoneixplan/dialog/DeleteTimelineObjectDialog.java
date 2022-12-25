package io.github.studio116.phoneixplan.dialog;

import android.content.Context;
import android.view.View;

import androidx.appcompat.app.AlertDialog;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import io.github.studio116.phoneixplan.R;
import io.github.studio116.phoneixplan.model.Timeline;
import io.github.studio116.phoneixplan.model.TimelineObject;

public class DeleteTimelineObjectDialog implements View.OnClickListener {
    private final Timeline timeline;
    private final TimelineObject object;
    public final AlertDialog parentDialog;
    public final AlertDialog dialog;

    public DeleteTimelineObjectDialog(Context context, AlertDialog parentDialog, Timeline timeline, TimelineObject object) {
        this.timeline = timeline;
        this.object = object;
        this.parentDialog = parentDialog;

        // Show Dialog
        dialog = new MaterialAlertDialogBuilder(context)
                .setMessage(R.string.are_you_sure_dialog)
                .setPositiveButton(R.string.delete, null)
                .setNegativeButton(R.string.cancel, null)
                .show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(this);
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == dialog.getButton(AlertDialog.BUTTON_POSITIVE)) {
            // Delete
            timeline.objects.remove(object);
            timeline.save(dialog.getContext().getApplicationContext());
            // Close Parent Dialog
            if (parentDialog.isShowing()) {
                parentDialog.dismiss();
            }
        }
        // Close Dialog
        dialog.dismiss();
    }
}
