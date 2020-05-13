package ie.ul.traintracker;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.method.ScrollingMovementMethod;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;

@SuppressWarnings("deprecation")

public class AddJourney extends Activity {

    public static final String CHANNEL_ID = "1001";
    final Calendar calendar = Calendar.getInstance();


    TextView tableView;
    EditText addCustomStartLocation, addCustomEndLocation;
    Spinner reminderSpinner;
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
        tableView.setMovementMethod(new ScrollingMovementMethod()); // allow scrolling the textView

        final SharedPreferences myPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        updateFromPreferences(myPrefs); // check and update user name


        final Editor prefEditor = PreferenceManager.getDefaultSharedPreferences(getBaseContext()).edit();
        showFullTable(); // display all user made journeys
        if (tableView.getText().toString().isEmpty()) { // check if user journey table is empty (by checking if the textView it populates is empty)
            tableView.setText("No journeys yet!");
        }

        reminderSpinner = findViewById(R.id.reminderSpinner); // reminder spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.reminderSpinnerArray, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        reminderSpinner.setAdapter(adapter);
        if (myPrefs.contains("KEY_SPINNER_SELECTED")) { // check if spinner position has been saved
            savedPosition = myPrefs.getInt("KEY_SPINNER_SELECTED", 0); // get position if was saved
            reminderSpinner.setSelection(savedPosition); // set spinner to saved position
        }
        reminderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (timeFormatted == null || dateFormatted == null) { // check time or date has not been set
                    timeBefore = (int) parent.getItemIdAtPosition(position); //get id of spinner position
                } else if (timeFormatted != null && dateFormatted != null){
                    reminderSpinner.setSelection(0); // reset spinner
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
                if (timeFormatted == null && dateFormatted == null) { // check time and date have not been set
                    if (timeBefore == 0) { // check if spinner has been selected
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
                        timeSet = 30; // default selection just incase
                    }
                    if (timeBefore != null || timeSet != null) { // check reminder spinner has been set

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

                                            timePickerButton.setText("Alarm Set: " + dayOfMonth + "/" + month + "/" + year + " @ " + addLeadingZero(hourOfDay) + ":" + addLeadingZero(minute));
                                            int time = (minute * 60 + (hourOfDay - 1) * 60 * 60) * 1000;
                                            SimpleDateFormat format = new SimpleDateFormat("HH:mm");
                                            timeFormatted = format.format(time); // format time for database
                                            prefEditor.putString("KEY_FORMATTED_TIME", timeFormatted); // saved selected time
                                            prefEditor.apply();

                                            startAlarm(calendar); // start alarm manager
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
                                        dateFormatted = dayOfMonth + "/" + month + "/" + year; // format date for database
                                        prefEditor.putString("KEY_FORMATTED_DATE", dateFormatted); // saved selected date
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




        addCustomJourney = findViewById(R.id.addCustomJourneyButton); // button to add your journey
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
        getMenuInflater().inflate(R.menu.custom_menu, menu); // create menu
        return true;
    }

    //Menu option to open the settings page
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.cancel_alarm: // cancel alarm menu option
                cancelAlarm();
                reminderSpinner.setSelection(0);
                Editor prefEditor = PreferenceManager.getDefaultSharedPreferences(getBaseContext()).edit();
                prefEditor.putInt("KEY_SPINNER_SELECTED", 0);
                prefEditor.apply();
                return true;

            case R.id.remove_all_custom: // delete table menu option
                deleteAll();
                showFullTable();
                return true;

            case R.id.settings: // open settings page
                navigateToView(Settings.class);
                return true;

            case R.id.help: // open help page
                navigateToView(Help.class);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void checkAlarmSet() { // check if an alarm has been set
        final SharedPreferences myPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        if (myPrefs.contains("KEY_FORMATTED_TIME") && myPrefs.contains("KEY_FORMATTED_DATE")) { // check if time and date has been saved
            String time = myPrefs.getString("KEY_FORMATTED_TIME", "TIME_ERROR");
            String date = myPrefs.getString("KEY_FORMATTED_DATE", "DATE_ERROR");
            timePickerButton.setText("Alarm Set: " + date + " @ " + time); // set time & date button to saved values
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

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, (calendar.getTimeInMillis() - timeSet * 60 * 1000), pendingIntent); // schedules intent to fire notification
        prefEditor.putInt("KEY_SPINNER_SELECTED", timeBefore); // saves the spinner position
        prefEditor.apply();
    }

    // Used to cancel the alarm and clear formatted date & time
    public void cancelAlarm() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent, 0);

        alarmManager.cancel(pendingIntent); // cancel alarm intent
        Editor prefEditor = PreferenceManager.getDefaultSharedPreferences(getBaseContext()).edit();
        dateFormatted = null;
        prefEditor.remove("KEY_FORMATTED_DATE"); // clear saved date
        timeFormatted = null;
        prefEditor.remove("KEY_FORMATTED_TIME"); // clear saved time
        prefEditor.apply();
        timePickerButton.setText("Set Departure Date & Time"); // reset button
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

    // deletes the custom journeys table
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


