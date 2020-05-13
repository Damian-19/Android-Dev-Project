package ie.ul.traintracker;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;


public class Help extends Activity {

    TextView addJourneyHeading, addJourneyContent, addJourneyHeading1, addJourneyContent1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_relative);

        addJourneyHeading = findViewById(R.id.AddJourneyHeading);
        addJourneyContent = findViewById(R.id.AddJourneyContent);
        addJourneyHeading1 = findViewById(R.id.AddJourneyHeading1);
        addJourneyContent1 = findViewById(R.id.AddJourneyContent1);

    }
}
