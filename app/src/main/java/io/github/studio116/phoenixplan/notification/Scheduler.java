package io.github.studio116.phoenixplan.notification;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.AlarmManagerCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.Calendar;
import java.util.Date;

import io.github.studio116.phoenixplan.model.Timeline;
import io.github.studio116.phoenixplan.model.TimelineObject;

public class Scheduler {
    private static void schedule(Context context, PendingIntent alarmIntent, Date time, boolean cancel) {
        // Schedule
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (cancel) {
            alarmManager.cancel(alarmIntent);
        } else {
            AlarmManagerCompat.setExactAndAllowWhileIdle(alarmManager, AlarmManager.RTC_WAKEUP, time.getTime(), alarmIntent);
        }
    }

    private static void schedule(Context context, TimelineObject object, Date time, AlarmReceiver.Message message, boolean cancel, boolean persistent) {
        // Build Config
        AlarmReceiver.Config config = new AlarmReceiver.Config(object.id, message, persistent ? AlarmReceiver.Mode.SEND_PERSISTENT : AlarmReceiver.Mode.SEND);

        // Cancel Old Notification
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.cancel(config.notificationID());

        // Check If Starting Point Is In The Past
        Date now = new Date();
        if (time.compareTo(now) < 0) {
            if (persistent) {
                // Persistent notifications should be displayed even if their starting point is in the past.
                // But not if their ending point is in the past.
                if (object.timeTo.compareTo(now) < 0) {
                    return;
                }
            } else {
                // Skip
                return;
            }
        }
        // PendingIntent Flags
        int flags = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            flags |= PendingIntent.FLAG_IMMUTABLE;
        }
        // Schedule Notification
        {
            // Create Intent
            Intent intent = new Intent(context, AlarmReceiver.class);
            intent.putExtra(AlarmReceiver.CONFIG_EXTRA, config);
            // Create PendingIntent
            PendingIntent alarmIntent = PendingIntent.getBroadcast(context, config.hashCode(), intent, flags);
            // Schedule
            schedule(context, alarmIntent, time, cancel);
        }
        // Cancel Persistent Notification
        if (persistent) {
            // Create Intent
            Intent intent = new Intent(context, AlarmReceiver.class);
            config = new AlarmReceiver.Config(object.id, message, AlarmReceiver.Mode.CANCEL);
            intent.putExtra(AlarmReceiver.CONFIG_EXTRA, config);
            // Create PendingIntent
            PendingIntent alarmIntent = PendingIntent.getBroadcast(context, config.hashCode(), intent, flags);
            // Schedule
            schedule(context, alarmIntent, object.timeTo, cancel);
        }
    }

    public static void schedule(Context context, TimelineObject object, boolean cancel) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(object.timeFrom);
        calendar.add(Calendar.HOUR, -1);
        if (object.isDeadline) {
            // Deadline
            schedule(context, object, calendar.getTime(), AlarmReceiver.Message.DEADLINE_1_HOUR, cancel, false);
            calendar.add(Calendar.MINUTE, 30);
            schedule(context, object, calendar.getTime(), AlarmReceiver.Message.DEADLINE_30_MINUTES, cancel, false);
            calendar.add(Calendar.MINUTE, 25);
            schedule(context, object, calendar.getTime(), AlarmReceiver.Message.DEADLINE_5_MINUTES, cancel, false);
            calendar.add(Calendar.MINUTE, 5);
            schedule(context, object, calendar.getTime(), AlarmReceiver.Message.DEADLINE_NOW, cancel, false);
        } else {
            // Event
            schedule(context, object, calendar.getTime(), AlarmReceiver.Message.EVENT_1_HOUR, cancel, false);
            calendar.add(Calendar.MINUTE, 30);
            schedule(context, object, calendar.getTime(), AlarmReceiver.Message.EVENT_30_MINUTES, cancel, false);
            calendar.add(Calendar.MINUTE, 25);
            schedule(context, object, calendar.getTime(), AlarmReceiver.Message.EVENT_5_MINUTES, cancel, false);
            calendar.add(Calendar.MINUTE, 5);
            schedule(context, object, calendar.getTime(), AlarmReceiver.Message.EVENT_NOW, cancel, true);
            schedule(context, object, object.timeTo, AlarmReceiver.Message.EVENT_ENDED, cancel, false);
        }
    }

    public static void update(Context context) {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.cancelAll();
        Timeline timeline = new Timeline();
        timeline.load(context.getApplicationContext());
        for (TimelineObject object : timeline.objects) {
            Scheduler.schedule(context, object, false);
        }
    }
}
