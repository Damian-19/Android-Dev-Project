package ie.ul.traintracker;
/*
 *Made by Damian Larkin, ID: 18230253
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

public class MainActivity extends Activity {
    private AdView mAdView;

    Button checkTrains, addJourney;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_relative);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
                    @Override
                    public void onInitializationComplete(InitializationStatus initializationStatus) {
                    }
                });
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);


        checkTrains = (Button) findViewById(R.id.checkTrains);
        addJourney = (Button) findViewById(R.id.addJourney);

        checkTrains.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent checkTrainsIntent = new Intent(getApplicationContext(), CheckTrains.class);
                startActivity(checkTrainsIntent);
            }
        });

        addJourney.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent addJourneyIntent = new Intent(getApplicationContext(), AddJourney.class);
                startActivity(addJourneyIntent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.settings:
                Intent settingsIntent = new Intent(getBaseContext(), ie.ul.traintracker.Settings.class);
                startActivity(settingsIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
