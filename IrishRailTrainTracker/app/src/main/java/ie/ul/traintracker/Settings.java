package ie.ul.traintracker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ToggleButton;

@SuppressWarnings("deprecation")

public class Settings extends PreferenceActivity {

    public final static String KEY_NAME = "KEY_NAME";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preference_settings);

    }

}
