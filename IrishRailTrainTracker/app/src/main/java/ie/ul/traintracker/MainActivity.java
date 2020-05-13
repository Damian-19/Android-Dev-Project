package ie.ul.traintracker;
/*
 *Made by Damian Larkin, ID: 18230253
 */

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import org.w3c.dom.Text;

import java.util.Calendar;

@SuppressWarnings("deprecation")
public class MainActivity extends Activity {
    private AdView mAdView;

    Button checkTrains, addJourney;
    TextView welcomeText;
    ImageView mapImage;
    OnSharedPreferenceChangeListener prefListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_relative);

        SharedPreferences myPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());


        boolean alarm = (PendingIntent.getBroadcast(this, 0, new Intent("ALARM"), PendingIntent.FLAG_NO_CREATE) == null);

        if(alarm) {
            Intent itAlarm = new Intent("ALARM");
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, itAlarm, 0);
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.add(Calendar.SECOND, 3);
            AlarmManager alarm1 = (AlarmManager) getSystemService(ALARM_SERVICE);
            alarm1.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 60000, pendingIntent);
        }

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
                    @Override
                    public void onInitializationComplete(InitializationStatus initializationStatus) {
                    }
                });
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);


        checkTrains = findViewById(R.id.checkTrains);
        addJourney = findViewById(R.id.addJourney);
        welcomeText = findViewById(R.id.welcome_text);
        mapImage = findViewById(R.id.imageView3);
        updateFromPreferences(myPrefs);

        checkTrains.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                navigateToView(CheckTrains.class);
            }
        });

        addJourney.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                navigateToView(AddJourney.class);
            }
        });
    }

    //Update welcome text from sharedPreferences
    private void updateFromPreferences(SharedPreferences prefs) {
        String name = prefs.getString(Settings.KEY_NAME, "");
        if (!name.contentEquals("")) {
            welcomeText.setText("Welcome back, " + name + ".");
        } else {
            welcomeText.setText("Welcome back.");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    //Menu option to open the settings page
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.settings:
                navigateToView(Settings.class);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //Replaces the need for separate intents in every button listener
    public void navigateToView(Class viewName) {
        Intent intent = new Intent(getBaseContext(), viewName);
        startActivity(intent);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        //Checks sharedPreferences again for a name change
        SharedPreferences myPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        updateFromPreferences(myPrefs);
    }






}
