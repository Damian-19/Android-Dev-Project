package ie.ul.traintracker;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.SimpleTimeZone;
import java.util.TimeZone;

@SuppressWarnings("deprecation")

public class AddJourney extends FragmentActivity {

    public static final String CHANNEL_ID = "1001";
    final Calendar calendar = Calendar.getInstance();


    TextView trainsPage, notificationPrompt, tableView, timePicked;
    EditText addCustomStartLocation, addCustomEndLocation;
    TimePickerDialog timePickerDialog;
    DatePickerDialog datePickerDialog;
    String[] customJourney;
    TimePicker timePicker;
    Button notificationButton, setDate, addCustomJourney, timePickerButton, datePickerButton;
    public static String TITLE_ID = "Title";
    public static String CONTENT_ID = "Content";
    final static int RQS_1 = 1;
    private Integer delID;

    TrainDB trainDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_journey_relative);

        SharedPreferences myPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        updateFromPreferences(myPrefs);

        trainDB = new TrainDB(getApplicationContext());
        addCustomStartLocation = (EditText) findViewById(R.id.addCustomJourneyStartInput);
        addCustomEndLocation = (EditText) findViewById(R.id.addCustomJourneyEndInput);
        notificationButton = (Button) findViewById(R.id.notification_button);
        notificationPrompt = (TextView) findViewById(R.id.notificationPrompt);
        tableView = (TextView) findViewById(R.id.tableView);

        /*datePickerButton = (Button) findViewById(R.id.datePickerButton);
        final String[] dateFormatted = new String[1];
        datePickerButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(AddJourney.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                calendar.set(Calendar.YEAR, year);
                                calendar.set(Calendar.MONTH, month);
                                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                datePickerButton.setText("Departure Date: " + addLeadingZero(dayOfMonth) + "/" + addLeadingZero(month) + "/" + year);
                                int date = (dayOfMonth + month + year);
                                SimpleDateFormat format = new SimpleDateFormat("DD/MM/YYYY");
                                dateFormatted[0] = format.format(date);
                            }
                        }, year, month, dayOfMonth);
                datePickerDialog.show();
            }
        });*/

        timePickerButton = (Button) findViewById(R.id.timePickerButton);
        final String[] formatted = new String[1];
        final String[] dateFormatted = new String[1];
        timePickerButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(AddJourney.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                calendar.set(Calendar.MINUTE, minute);
                                calendar.set(Calendar.SECOND, 0);
                                //timePickerButton.setText("Departure Time: " + addLeadingZero(hourOfDay) + ":" + addLeadingZero(minute));
                                int time  = (minute * 60 + (hourOfDay -1) * 60 * 60) * 1000;
                                SimpleDateFormat format = new SimpleDateFormat("HH:mm");
                                formatted[0] = format.format(time);

                                startAlarm(calendar);
                            }
                        }, hour, minute, true);
                timePickerDialog.show();


                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(AddJourney.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                calendar.set(Calendar.YEAR, year);
                                calendar.set(Calendar.MONTH, month);
                                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                //datePickerButton.setText("Departure Date: " + addLeadingZero(dayOfMonth) + "/" + addLeadingZero(month) + "/" + year);
                                int date = (dayOfMonth + month + year);
                                SimpleDateFormat format = new SimpleDateFormat("DD/MM/YYYY");
                                dateFormatted[0] = format.format(date);
                                //startAlarm(calendar);
                            }
                        }, year, month, dayOfMonth);
                datePickerDialog.show();
            }
        });




        addCustomJourney = (Button) findViewById(R.id.addCustomJourneyButton);
        addCustomJourney.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if the stations have been set
                if (isAddJourneySet()) {
                    // Check if the departure time has been set
                    if (formatted[0] != null && dateFormatted[0] != null) {
                        addJourney(convert(addCustomStartLocation.getText().toString()), convert(addCustomEndLocation.getText().toString()), dateFormatted[0], formatted[0]);
                        showFullTable();
                        //buildSpinner();
                    } else {
                        Toast toast = Toast.makeText(getApplicationContext(), "Please fill out all fields", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }
            }
        });

        notificationButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                sendNotification();
            }
        });


        setDate = (Button) findViewById(R.id.setDate);
        setDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment timePicker = new TimePickerFragment();
                timePicker.show(getSupportFragmentManager(), "time picker");
            }
        });

        Button buttonCancelAlarm = findViewById(R.id.button_cancel);
        buttonCancelAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelAlarm();
            }
        });

        setDate.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment dialogFragment = new TimePickerFragment();
                dialogFragment.show(getSupportFragmentManager(), "timePicker");
            }
        });

        Button buttonCancel = findViewById(R.id.button_cancel);
        buttonCancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelAlarm();
            }
        });

        showFullTable();
    }

    private void updateTimeText(Calendar calendar) {
        String timeText = "Alarm set for: ";
        timeText += DateFormat.getTimeInstance(DateFormat.SHORT).format(calendar.getTime());

        timePickerButton.setText(timeText);
    }

    private void startAlarm(Calendar calendar) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(AddJourney.this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent, 0);

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }

    private void cancelAlarm() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent, 0);

        alarmManager.cancel(pendingIntent);
        timePickerButton.setText("Pick Departure Time");
    }

    private void updateFromPreferences(SharedPreferences prefs) {
        String name = prefs.getString(Settings.KEY_NAME, "");
        if (!name.contentEquals("")) {
            TITLE_ID = (name + ", your journey reminder");
        } else {
            TITLE_ID = ("Journey Reminder");
        }
    }

    // creates the notification channel for newer versions of Android
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

    // builds & sends notification
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

    private void print(String[] content) {
        tableView.setText("");
        for (int i = 0; i < content.length; i++) {
            tableView.append(content[i] + "\n");
        }
    }

    private void showFullTable() {
        print(trainDB.getAllCustom());
    }

    // checks if all fields are filled out before adding to the database
    private boolean isAddJourneySet() {
        if (addCustomStartLocation.getText().toString().contentEquals("") ||
                addCustomEndLocation.getText().toString().contentEquals("")) {
            Toast toast = Toast.makeText(getApplicationContext(), "Please enter start and end stations", Toast.LENGTH_SHORT);
            toast.show();
            return false;
        }
        return true;
    }

    // adds the journey using the database method
    private void addJourney(String journeyStart, String journeyEnd, String dateFormatted, String formatted) {
        trainDB.addRowCustomJourney(journeyStart, journeyEnd, dateFormatted,  formatted);
    }

    private void deleteJourney(int index) {
        trainDB.deleteRowTimetable(index);
    }

    // Adds a leading zero if less than 10
    public String addLeadingZero(int input) {
        if (input >= 10) {
            return String.valueOf(input);
        } else {
            return "0" + String.valueOf(input);
        }
    }

    // Converts first letter of input to uppercase if is lowercase
    // Adapted from www.geeksforgeeks.org/java-program-convert-first-character-uppercase-sentence/
    static String convert( String input) {
        char ch[] = input.toCharArray();
        for (int i=0; i<input.length(); i++) {
            if (i == 0 && ch[i] != ' ' || ch[i] != ' ' && ch[i - 1] == ' ') {
                if (ch[i] >= 'a' && ch[i] <= 'z') {
                    ch[i] = (char)(ch[i] - 'a' + 'A');
                }
            } else if (ch[i] >= 'A' && ch[i] <= 'Z') {
                ch[i] = (char)(ch[i] + 'a' - 'A');
            }
        }
        String string = new String(ch);
        return string;
    }
}


