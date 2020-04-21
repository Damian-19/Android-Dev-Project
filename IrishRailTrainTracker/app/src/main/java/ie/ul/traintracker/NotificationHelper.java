package ie.ul.traintracker;

import android.annotation.TargetApi;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import static ie.ul.traintracker.CheckTrains.TITLE_ID;
import static ie.ul.traintracker.CheckTrains.CONTENT_ID;


public class NotificationHelper extends ContextWrapper {
    public static final String channelID = "channelID";
    public static final String channelName = "Journey Reminders";


    private NotificationManager mManager;

    public NotificationHelper(Context base) {
        super(base);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel();
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void createChannel() {
        NotificationChannel channel = new NotificationChannel(channelID, channelName, NotificationManager.IMPORTANCE_HIGH);

        getManager().createNotificationChannel(channel);
    }

    public NotificationManager getManager() {
        if (mManager == null) {
            mManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }

        return mManager;
    }

    public NotificationCompat.Builder getChannelNotification() {
        return new NotificationCompat.Builder(getApplicationContext(), channelID)
                .setContentTitle(TITLE_ID)
                .setContentText(CONTENT_ID)
                .setSmallIcon(R.drawable.logo)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);
    }
}