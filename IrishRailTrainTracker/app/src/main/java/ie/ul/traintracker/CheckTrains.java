package ie.ul.traintracker;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
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
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
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
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

@SuppressWarnings("deprecation")
public class CheckTrains extends Activity {

    TextView dbView;
    String savedStartStation, savedEndStation;
    Button searchStartButton, showFullTimetable;
    EditText chosenStartStationField, chosenDepartureStation;

    TrainDB trainDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_trains_relative);

        dbView = findViewById(R.id.dbcontent);
        trainDB = new TrainDB(getApplicationContext());
        searchStartButton = findViewById(R.id.findStartStationsButton);
        chosenStartStationField = findViewById(R.id.chosenStartStation);
        chosenDepartureStation = findViewById(R.id.chosenDepartureStation);
        showFullTimetable = findViewById(R.id.showFullDatabase);

        final SharedPreferences myPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        if (myPrefs.contains("KEY_START_STATION_SEARCH") && myPrefs.contains("KEY_END_STATION_SEARCH")) {
            readInput();
        }


        searchStartButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isStartSearchSet()) {
                    showStartStationSearch(chosenStartStationField.getText().toString(), chosenDepartureStation.getText().toString());
                    saveInput();
                }
                if (dbView.getText().toString().isEmpty()) {
                    Toast toast = Toast.makeText(getApplicationContext(), "No journeys found", Toast.LENGTH_SHORT);
                    toast.show();
                }
                // close the virtual keyboard
                try  {
                    InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                } catch (Exception e) {

                }
            }
        });

        showFullTimetable.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showFullTable();
            }
        });



    }

    private void print(String[] content) {
        dbView.setText("");
        for (int i=0; i<content.length; i++) {
            dbView.append(content[i]+"\n");
        }
    }

    private void showFullTable() {
        print(trainDB.getAllTimetable());
    }

    private void saveInput() {
        final Editor prefEditor = PreferenceManager.getDefaultSharedPreferences(getBaseContext()).edit();
        prefEditor.putString("KEY_START_STATION_SEARCH", chosenStartStationField.getText().toString());
        prefEditor.putString("KEY_END_STATION_SEARCH", chosenDepartureStation.getText().toString());
        prefEditor.apply();
    }

    private void readInput() {
        final SharedPreferences myPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        savedStartStation = myPrefs.getString("KEY_START_STATION_SEARCH", "START_ERROR");
        savedEndStation = myPrefs.getString("KEY_END_STATION_SEARCH", "END_ERROR");
        chosenStartStationField.setText(savedStartStation);
        chosenDepartureStation.setText(savedEndStation);
    }

    private void showStartStationSearch(String journeyStartLocation, String journeyDepartureTime) {
        print(trainDB.getJourney(journeyStartLocation, journeyDepartureTime));
    }

    private boolean isStartSearchSet() {
        if (chosenStartStationField.getText().toString().contentEquals("") ||
                chosenDepartureStation.getText().toString().contentEquals("")) {
            Toast toast = Toast.makeText(getApplicationContext(),"Please enter start and end stations.",Toast.LENGTH_SHORT);
            toast.show();
            return false;
        }
        return true;
    }

}