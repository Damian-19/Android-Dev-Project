package ie.ul.traintracker;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

@SuppressWarnings("deprecation")

public class Settings extends PreferenceActivity {

    ToggleButton darkMode;

    public final static String KEY_NAME = "KEY_NAME";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preference_settings);

        ToggleButton toggleButton = (ToggleButton) findViewById(R.id.dark_mode);
        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                } else {

                }
            }
        });



    }

}
