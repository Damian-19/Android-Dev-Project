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
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
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
import androidx.work.impl.model.Preference;

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

public class AddJourney extends Activity {

    public static final String CHANNEL_ID = "1001";
    final Calendar calendar = Calendar.getInstance();


    TextView tableView;
    EditText addCustomStartLocation, addCustomEndLocation;
    Spinner reminderSpinner;
    TimePickerDialog timePickerDialog;
    DatePickerDialog datePickerDialog;
    String[] customJourney;
    TimePicker timePicker;
    Button notificationButton, setDate, addCustomJourney, timePickerButton;

    private static final String KEY_SAVED_TIME = "saved_time";
    private static final String KEY_SAVED_DATE = "saved_date";
    public static String TITLE_ID = "Journey Reminder";
    public static String CONTENT_ID = "Tap to see your journeys";
    final static int RQS_1 = 1;
    private Integer delID;
    private Integer isAlarmSet = 0;
    private Integer timeBefore = null;
    private Integer timeSet = null;
    Integer savedPosition;
    public String timeFormatted;
    public String dateFormatted;

    TrainDB trainDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_journey_relative);

        trainDB = new TrainDB(getApplicationContext());
        addCustomStartLocation = findViewById(R.id.addCustomJourneyStartInput);
        addCustomEndLocation = findViewById(R.id.addCustomJourneyEndInput);
        tableView = findViewById(R.id.tableView);

        final SharedPreferences myPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        updateFromPreferences(myPrefs);


        final Editor prefEditor = PreferenceManager.getDefaultSharedPreferences(getBaseContext()).edit();
        showFullTable();
        if (tableView.getText().toString().isEmpty()) {
            tableView.setText("No journeys yet!");
        }

        reminderSpinner = findViewById(R.id.reminderSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.reminderSpinnerArray, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        reminderSpinner.setAdapter(adapter);
        if (myPrefs.contains("KEY_SPINNER_SELECTED")) {
            savedPosition = myPrefs.getInt("KEY_SPINNER_SELECTED", 0);
            reminderSpinner.setSelection(savedPosition);
        }
        reminderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (timeFormatted == null || dateFormatted == null) {
                    timeBefore = (int) parent.getItemIdAtPosition(position);
                } else if (timeFormatted != null && dateFormatted != null){
                    reminderSpinner.setSelection(0);
                    Toast toast = Toast.makeText(getApplicationContext(), "You have already set a reminder. Selection not set", Toast.LENGTH_LONG);
                    toast.show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                timeBefore = null;

            }
        });


        timePickerButton = findViewById(R.id.timePickerButton);
        checkAlarmSet();
        timePickerButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (timeFormatted == null && dateFormatted == null) {
                    if (timeBefore == 0) {
                        timeBefore = null;
                        timeSet = null;
                    } else if (timeBefore == 1) {
                        timeSet = 10;
                    } else if (timeBefore == 2) {
                        timeSet = 20;
                    } else if (timeBefore == 3) {
                        timeSet = 30;
                    } else if (timeBefore == 4) {
                        timeSet = 45;
                    } else if (timeBefore == 5) {
                        timeSet = 60;
                    } else {
                        timeSet = 30;
                    }
                    if (timeBefore != null || timeSet != null) {

                        //time picker
                        final Calendar calendar = Calendar.getInstance();
                        int hour = calendar.get(Calendar.HOUR_OF_DAY);
                        int minute = calendar.get(Calendar.MINUTE);
                        final int year = calendar.get(Calendar.YEAR);
                        final int month = calendar.get(Calendar.MONTH);
                        final int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

                        TimePickerDialog timePickerDialog = new TimePickerDialog(AddJourney.this,
                                new TimePickerDialog.OnTimeSetListener() {
                                    @Override
                                    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                        calendar.set(Calendar.MINUTE, minute);
                                        calendar.set(Calendar.SECOND, 0);



                                        //if (timeSet != null) {
                                            timePickerButton.setText("Alarm Set: " + dayOfMonth + "/" + month + "/" + year + " @ " + addLeadingZero(hourOfDay) + ":" + addLeadingZero(minute));
                                            int time = (minute * 60 + (hourOfDay - 1) * 60 * 60) * 1000;
                                            SimpleDateFormat format = new SimpleDateFormat("HH:mm");
                                            timeFormatted = format.format(time);
                                            prefEditor.putString("KEY_FORMATTED_TIME", timeFormatted);
                                            prefEditor.apply();

                                            startAlarm(calendar);
                                        /*} else {
                                            Toast toast = Toast.makeText(getApplicationContext(), "Please select how far in advance you would like your reminder", Toast.LENGTH_LONG);
                                            toast.show();
                                        }*/
                                    }
                                }, hour, minute, true);
                        timePickerDialog.show();

                        // date picker

                        DatePickerDialog datePickerDialog = new DatePickerDialog(AddJourney.this,
                                new DatePickerDialog.OnDateSetListener() {
                                    @Override
                                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                        calendar.set(Calendar.YEAR, year);
                                        calendar.set(Calendar.MONTH, month);
                                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                        int date = (dayOfMonth + month + year);
                                        SimpleDateFormat format = new SimpleDateFormat("DD/MM/YYYY");
                                        dateFormatted = dayOfMonth + "/" + month + "/" + year;
                                        prefEditor.putString("KEY_FORMATTED_DATE", dateFormatted);
                                        prefEditor.apply();
                                    }
                                }, year, month, dayOfMonth);
                        datePickerDialog.show();
                    } else if (timeBefore == null || timeSet == null) {
                        Toast toast = Toast.makeText(getApplicationContext(), "Please select how far in advance you would like your reminder", Toast.LENGTH_LONG);
                        toast.show();
                    }
            } else {
                    timeFormatted = myPrefs.getString("KEY_FORMATTED_TIME", "TIME_ERROR");
                    dateFormatted = myPrefs.getString("KEY_FORMATTED_DATE", "DATE_ERROR");
                    timePickerButton.setText("Alarm Set: " + dateFormatted + " @ " + timeFormatted);
                    Toast toast = Toast.makeText(getApplicationContext(), "Only one alarm may be set at a time. This can be reset from the menu", Toast.LENGTH_LONG);
                    toast.show();
                }
                }
        });




        addCustomJourney = findViewById(R.id.addCustomJourneyButton);
        addCustomJourney.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                    // Check if the stations have been set
                    if (isAddJourneySet()) {
                        // Check if the departure time has been set
                        if (timeFormatted != null && dateFormatted != null) {
                            addJourney(convert(addCustomStartLocation.getText().toString()), convert(addCustomEndLocation.getText().toString()), dateFormatted, timeFormatted);
                            showFullTable();
                            //buildSpinner();
                        } else {
                            Toast toast = Toast.makeText(getApplicationContext(), "Please fill in all fields", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    }
            }

        });

    }


    /****************
     * END onCreate()
     *****************/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.custom_menu, menu);
        return true;
    }

    //Menu option to open the settings page
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.cancel_alarm:
                cancelAlarm();
                reminderSpinner.setSelection(0);
                Editor prefEditor = PreferenceManager.getDefaultSharedPreferences(getBaseContext()).edit();
                prefEditor.putInt("KEY_SPINNER_SELECTED", 0);
                prefEditor.apply();
                return true;

            case R.id.remove_all_custom:
                deleteAll();
                showFullTable();
                return true;

            case R.id.settings:
                navigateToView(Settings.class);
                return true;

            case R.id.help:
                navigateToView(Help.class);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void checkAlarmSet() {
        final SharedPreferences myPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        if (myPrefs.contains("KEY_FORMATTED_TIME") && myPrefs.contains("KEY_FORMATTED_DATE")) {
            String time = myPrefs.getString("KEY_FORMATTED_TIME", "TIME_ERROR");
            String date = myPrefs.getString("KEY_FORMATTED_DATE", "DATE_ERROR");
            timePickerButton.setText("Alarm Set: " + date + " @ " + time);
            isAlarmSet = 1;
            Toast toast = Toast.makeText(getApplicationContext(), "You already have an alarm set", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    // Used to schedule the alarm intent for the date & time selected
    private void startAlarm(Calendar calendar) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(AddJourney.this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent, 0);
        Editor prefEditor = PreferenceManager.getDefaultSharedPreferences(getBaseContext()).edit();

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, (calendar.getTimeInMillis() - timeSet * 60 * 1000), pendingIntent);
        prefEditor.putInt("KEY_SPINNER_SELECTED", timeBefore);
        prefEditor.apply();
    }

    // Used to cancel the alarm and clear formatted date & time
    public void cancelAlarm() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent, 0);

        alarmManager.cancel(pendingIntent);
        Editor prefEditor = PreferenceManager.getDefaultSharedPreferences(getBaseContext()).edit();
        dateFormatted = null;
        prefEditor.remove("KEY_FORMATTED_DATE");
        timeFormatted = null;
        prefEditor.remove("KEY_FORMATTED_TIME");
        prefEditor.apply();
        timePickerButton.setText("Set Departure Date & Time");
    }

    // Checks if a name is saved in the sharedPreferences and updates the variable TITLE_ID
    private void updateFromPreferences(SharedPreferences prefs) {
        String name = prefs.getString(Settings.KEY_NAME, "");
        if (!name.contentEquals("")) {
            TITLE_ID = (name + ", your journey reminder");
        } else {
            TITLE_ID = ("Journey Reminder");
        }
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
        if (addCustomStartLocation.getText().toString().contentEquals("")) {
            Toast toast = Toast.makeText(getApplicationContext(), "Please enter a start station", Toast.LENGTH_SHORT);
            toast.show();
            return false;
        } else if (addCustomEndLocation.getText().toString().contentEquals("")) {
            Toast toast = Toast.makeText(getApplicationContext(), "Please enter an end station", Toast.LENGTH_SHORT);
            toast.show();
            return false;
        }
        return true;
    }

    // adds the journey using the database method
    private void addJourney(String journeyStart, String journeyEnd, String dateFormatted, String timeFormatted) {
        trainDB.addRowCustomJourney(journeyStart, journeyEnd, dateFormatted,  timeFormatted);
    }

    private void deleteAll() {
        trainDB.deleteAllCustom();
    }

    // Adds a leading zero if less than 10
    public String addLeadingZero(int input) {
        if (input >= 10) {
            return String.valueOf(input);
        } else {
            return "0" + input;
        }
    }

    // Converts first letter of input to uppercase if is lowercase
    // Adapted from www.geeksforgeeks.org/java-program-convert-first-character-uppercase-sentence/
    static String convert( String input) {
        char[] ch = input.toCharArray();
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

    public void navigateToView(Class viewName) {
        Intent intent = new Intent(getBaseContext(), viewName);
        startActivity(intent);
    }
}


