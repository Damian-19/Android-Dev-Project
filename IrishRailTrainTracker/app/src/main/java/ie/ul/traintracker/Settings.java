package ie.ul.traintracker;

import android.os.Bundle;
import android.preference.PreferenceActivity;
@SuppressWarnings("deprecation")

public class Settings extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preference_settings);

    }

}
