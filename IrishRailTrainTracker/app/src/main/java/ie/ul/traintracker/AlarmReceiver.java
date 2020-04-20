package ie.ul.traintracker;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import static ie.ul.traintracker.CheckTrains.CHANNEL_ID;
import static ie.ul.traintracker.CheckTrains.RQS_1;
import static ie.ul.traintracker.CheckTrains.TITLE_ID;
import static ie.ul.traintracker.CheckTrains.CONTENT_ID;

@SuppressWarnings("deprecation")
public class AlarmReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        //Build notification
        /*Notification notification = new NotificationCompat.Builder(context)
                .setContentTitle(TITLE_ID)
                .setContentText(CONTENT_ID)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .build();*/


        //Send notification
        //NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        //manager.notify(RQS_1, notification);
    }

   /* public void sendNotification() {


        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.logo)
                .setContentTitle(TITLE_ID)
                .setContentText(CONTENT_ID)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setFullScreenIntent(fullScreenPendingIntent, true)
                .setCategory(NotificationCompat.CATEGORY_REMINDER)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(1, builder.build());
    }*/
}