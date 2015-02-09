package com.twinly.eyebb.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.twinly.eyebb.R;

public class NotificationUtils {
	public static void sendNotification(Intent notificationIntent,
			Context context, Class<?> cls, String title, String text,
			boolean isSingle) {
		NotificationCompat.Builder builder = new NotificationCompat.Builder(
				context);

		// Set the notification contents
		builder.setSmallIcon(R.drawable.ic_launcher);
		builder.setContentTitle(title);
		builder.setContentText(text);

		// Construct a task stack
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
		// Adds the main Activity to the task stack as the parent
		stackBuilder.addParentStack(cls);
		// Push the content Intent onto the stack
		stackBuilder.addNextIntent(notificationIntent);

		int index;
		if (isSingle) {
			index = 0;
		} else {
			index = Integer.parseInt(String.valueOf(System.currentTimeMillis())
					.substring(5));
		}
		// Get a PendingIntent containing the entire back stack
		PendingIntent notificationPendingIntent = stackBuilder
				.getPendingIntent(index, PendingIntent.FLAG_CANCEL_CURRENT);
		builder.setContentIntent(notificationPendingIntent);

		Notification notification = builder.build();
		// set the notification light
		notification.defaults |= Notification.DEFAULT_LIGHTS;
		notification.flags = Notification.FLAG_AUTO_CANCEL;

		// set the notification sound and vibrate
		if (SharePrefsUtils.isSoundOn(context)) {
			notification.defaults |= Notification.DEFAULT_SOUND;
		}
		if (SharePrefsUtils.isVibrateOn(context)) {
			notification.defaults |= Notification.DEFAULT_VIBRATE;
		}

		// Get an instance of the Notification manager
		NotificationManager mNotificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		// Issue the notification
		mNotificationManager.notify(index, notification);
	}

}
