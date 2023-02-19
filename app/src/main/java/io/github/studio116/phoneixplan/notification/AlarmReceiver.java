package io.github.studio116.phoneixplan.notification;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.annotation.StringRes;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.TaskStackBuilder;
import androidx.core.content.ContextCompat;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

import io.github.studio116.phoneixplan.MainActivity;
import io.github.studio116.phoneixplan.R;
import io.github.studio116.phoneixplan.model.Timeline;
import io.github.studio116.phoneixplan.model.TimelineObject;

public class AlarmReceiver extends BroadcastReceiver {
    enum Message {
        DEADLINE_1_HOUR(R.string.notifications_deadline_1_hour), DEADLINE_30_MINUTES(R.string.notifications_deadline_30_minutes), DEADLINE_5_MINUTES(R.string.notifications_deadline_5_minutes), DEADLINE_NOW(R.string.notifications_deadline_now), EVENT_1_HOUR(R.string.notifications_event_1_hour), EVENT_30_MINUTES(R.string.notifications_event_30_minutes), EVENT_5_MINUTES(R.string.notifications_event_5_minutes), EVENT_NOW(R.string.notifications_event_now), EVENT_ENDED(R.string.notifications_event_ended);

        @StringRes
        private final int id;

        Message(int id) {
            this.id = id;
        }
    }

    enum Mode {
        SEND, SEND_PERSISTENT, CANCEL
    }

    static class Config implements Serializable {
        public final UUID id;
        public final Message message;
        public final Mode mode;

        Config(UUID id, Message message, Mode mode) {
            this.id = id;
            this.message = message;
            this.mode = mode;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            Config config = (Config) o;
            return id.equals(config.id) && message == config.message && mode == config.mode;
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, message, mode);
        }

        public int notificationID() {
            return Objects.hash(id, message);
        }
    }

    static final String CONFIG_EXTRA = "io.github.studio116.phoneixplan.CONFIG_EXTRA";
    private static final String CHANNEL_ID = "io.github.studio116.phoneixplan.CHANNEL_ID";

    @Override
    public void onReceive(Context context, Intent intent) {
        // Load Data From Intent
        Config config = (Config) intent.getSerializableExtra(CONFIG_EXTRA);
        if (config == null) {
            return;
        }

        // Cancel
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        if (config.mode == Mode.CANCEL) {
            notificationManager.cancel(config.notificationID());
            return;
        }

        // Load Timeline Object
        Timeline timeline = new Timeline();
        timeline.load(context.getApplicationContext());
        TimelineObject object = timeline.get(config.id);
        if (object == null) {
            return;
        }

        // Create Notification Channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, context.getString(R.string.notifications_channel_name), NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        // Create Notification
        Intent launchIntent = new Intent(context, MainActivity.class);
        launchIntent.putExtra(MainActivity.ARGUMENT_EXTRA, config.id);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntentWithParentStack(launchIntent);
        int flags = PendingIntent.FLAG_UPDATE_CURRENT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            flags |= PendingIntent.FLAG_IMMUTABLE;
        }
        PendingIntent pendingIntent = stackBuilder.getPendingIntent((int) System.currentTimeMillis(), flags);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context.getApplicationContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_baseline_calendar_today_24)
                .setContentTitle(object.name)
                .setContentText(context.getString(config.message.id))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(config.mode == Mode.SEND)
                .setAllowSystemGeneratedContextualActions(false)
                .setOngoing(config.mode == Mode.SEND_PERSISTENT);

        // Show Notification
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU || ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            notificationManager.notify(config.notificationID(), builder.build());
        }
    }
}
