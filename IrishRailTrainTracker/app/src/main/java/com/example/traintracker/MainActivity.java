package com.example.traintracker;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {

    Button checkTrains;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_relative);


        checkTrains = (Button) findViewById(R.id.checkTrains);

        checkTrains.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent newIntent = new Intent(getApplicationContext(), CheckTrains.class);
                startActivity(newIntent);
            }
        });
    }
}
