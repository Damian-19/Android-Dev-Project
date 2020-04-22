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
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
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
    String[] journeyStart, journeyEnd, departureTime;
    Spinner trainSpinner;
    Button searchStartButton;
    EditText chosenStartStationField, chosenDepartureTime;

    TrainDB trainDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_trains_relative);

        dbView = (TextView) findViewById(R.id.dbcontent);
        trainSpinner = (Spinner) findViewById(R.id.start_spinner);
        trainDB = new TrainDB(getApplicationContext());
        searchStartButton = (Button) findViewById(R.id.findStartStationsButton);
        chosenStartStationField = (EditText) findViewById(R.id.chosenStartStation);
        chosenDepartureTime = (EditText) findViewById(R.id.chosenDepartureTime);
        showFullDatabase();

        journeyEnd = trainDB.getEnd();
        departureTime = trainDB.getStartTime();
        journeyStart = trainDB.getStart();

        ArrayAdapter<String> trainAdapter = new ArrayAdapter<String>(getBaseContext(), R.layout.list_item, journeyStart);
        trainAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        trainSpinner.setAdapter(trainAdapter);


        searchStartButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isStartSearchSet()) {
                    showStartStationSearch(chosenStartStationField.getText().toString(), chosenDepartureTime.getText().toString());
                }
            }
        });



    }

    private void print(String[] content) {
        dbView.setText("");
        for (int i=0; i<content.length; i++) {
            dbView.append(content[i]+"\n");
        }
    }

    private void showFullDatabase() {
        print(trainDB.getAll());
    }

    private void showStartStationSearch(String journeyStartLocation, String journeyDepartureTime) {
        print(trainDB.getJourney(journeyStartLocation, journeyDepartureTime));
    }

    private boolean isStartSearchSet() {
        if (chosenStartStationField.getText().toString().contentEquals("") ||
                chosenDepartureTime.getText().toString().contentEquals("")) {
            Toast toast=Toast. makeText(getApplicationContext(),"Please enter start and end stations.",Toast. LENGTH_SHORT);
            return false;
        }
        return true;
    }

}