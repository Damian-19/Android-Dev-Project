package ie.ul.traintracker;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.legacy.content.WakefulBroadcastReceiver;

import static ie.ul.traintracker.AddJourney.CHANNEL_ID;
import static ie.ul.traintracker.AddJourney.RQS_1;
import static ie.ul.traintracker.AddJourney.TITLE_ID;
import static ie.ul.traintracker.AddJourney.CONTENT_ID;

@SuppressWarnings("deprecation")
public class AlarmReceiver extends WakefulBroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationHelper notificationHelper = new NotificationHelper(context);
        NotificationCompat.Builder builder = notificationHelper.getChannelNotification();
        notificationHelper.getManager().notify(1, builder.build());
    }
}