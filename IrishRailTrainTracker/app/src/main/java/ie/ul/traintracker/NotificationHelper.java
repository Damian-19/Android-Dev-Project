package ie.ul.traintracker;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;

import androidx.core.app.NotificationCompat;
import androidx.work.impl.model.Preference;

import static ie.ul.traintracker.AddJourney.TITLE_ID;
import static ie.ul.traintracker.AddJourney.CONTENT_ID;

@SuppressWarnings("deprecation")
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
        Intent intent = new Intent(this, AddJourney.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        final SharedPreferences myPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        if (myPrefs.contains("KEY_NAME")) {
            TITLE_ID = myPrefs.getString("KEY_NAME", "NAME_ERROR") + ", Your Journey Reminder";
        } else {
            TITLE_ID = "Your Journey Reminder";
        }

        return new NotificationCompat.Builder(getApplicationContext(), channelID)
                .setContentTitle(TITLE_ID)
                .setContentText(CONTENT_ID)
                .setSmallIcon(R.mipmap.ic_notification)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
    }

    // Used to cancel the alarm and clear formatted date & time
    public void cancelAlarm() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent, 0);

        alarmManager.cancel(pendingIntent);
        SharedPreferences.Editor prefEditor = PreferenceManager.getDefaultSharedPreferences(getBaseContext()).edit();
        prefEditor.remove("KEY_FORMATTED_DATE");
        prefEditor.remove("KEY_FORMATTED_TIME");
        prefEditor.apply();
    }
}