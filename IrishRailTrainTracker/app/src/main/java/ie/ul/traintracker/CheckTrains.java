package ie.ul.traintracker;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.Calendar;

@SuppressWarnings("deprecation")
public class CheckTrains extends Activity {

    public static final String CHANNEL_ID = "1001";
    private static final String EXTRA_NOTIFICATION_ID = "2";

    TextView trainsPage, notificationPrompt;
    TimePicker timePicker;
    TimePickerDialog timePickerDialog;
    Button notificationButton, setTime;
    String title;
    OnSharedPreferenceChangeListener prefListener;
    CharSequence testTitle = "Journey Reminder";
    CharSequence testContent = "You have a journey today";
    public static String TITLE_ID = "Title";
    public static String CONTENT_ID = "Content";
    final static int RQS_1 = 1;
    final Calendar myCalendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_trains_relative);

        SharedPreferences myPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        updateFromPreferences(myPrefs);

        //Notification
        createNotificationChannel();

        trainsPage = (TextView) findViewById(R.id.trainsPage);
        notificationButton = (Button) findViewById(R.id.notification_button);
        notificationPrompt = (TextView) findViewById(R.id.notificationPrompt);
        setTime = (Button) findViewById(R.id.setDate);

        setTime.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                notificationPrompt.setText("");
                openTimePickerDialog(false);
            }
        });

                notificationButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        sendNotification();
                        Toast toast = Toast.makeText(getApplicationContext(), "Notification Sent", Toast.LENGTH_SHORT);
                    }
                });

    }

    private void openTimePickerDialog(boolean is24r) {
        Calendar calendar = Calendar.getInstance();

        timePickerDialog = new TimePickerDialog(
                this,
                onTimeSetListener,
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                is24r);
        timePickerDialog.setTitle("Set Journey Departure Time");
        timePickerDialog.show();
    }

    OnTimeSetListener onTimeSetListener = new OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            Calendar calNow = Calendar.getInstance();
            Calendar calSet = (Calendar) calNow.clone();

            calSet.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calSet.set(Calendar.MINUTE, minute);
            calSet.set(Calendar.SECOND, 0);
            calSet.set(Calendar.MILLISECOND, 0);

            if(calSet.compareTo(calNow) <= 0) {
                calSet.add(Calendar.DATE, 1);
            }
            setAlarm(calSet);
        }};

    private void setAlarm(Calendar targetCal) {
        notificationPrompt.setText(
                "\n\n***\n"
                + "Your journey has been added, departing at " + targetCal.getTime() + "\n"
                + "***\n");

        Intent intent = new Intent(getBaseContext(), AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getBaseContext(), RQS_1, intent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, targetCal.getTimeInMillis(), pendingIntent);
    }

    private void updateFromPreferences(SharedPreferences prefs) {
        String name = prefs.getString(Settings.KEY_NAME, "");
        if (!name.contentEquals("")) {
            TITLE_ID = (name + ", your journey reminder");
        } else {
            TITLE_ID = ("Journey Reminder");
        }
    }

    /*public static void scheduleNotification(Context context, long time) {
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra("title", TITLE_ID);
        intent.putExtra("text", CONTENT_ID);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        //Schedule notification
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, time, pendingIntent);
    }

    public static void cancelNotification(Context context) {
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra("title", TITLE_ID);
        intent.putExtra("content", CONTENT_ID);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        //Cancel notification
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }


    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        }
    };

    public void setDate(View view) {
        new DatePickerDialog(
                CheckTrains.this, date,
                myCalendar.get(Calendar.YEAR),
                myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)
        ).show();
    }*/



    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            //Register channel with system
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void sendNotification() {

        Intent intent = new Intent(this, CheckTrains.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);


        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.logo)
                .setContentTitle(TITLE_ID)
                .setContentText(CONTENT_ID)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setCategory(NotificationCompat.CATEGORY_REMINDER)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(1, builder.build());
    }


}
