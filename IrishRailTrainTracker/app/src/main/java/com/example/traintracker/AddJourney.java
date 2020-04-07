package com.example.traintracker;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class AddJourney extends Activity {

    TextView journeyPage;

    TrainDB trainDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_journey_relative);

        trainDB = new TrainDB(getApplicationContext());


        journeyPage = (TextView) findViewById(R.id.addJourney);
    }

}
