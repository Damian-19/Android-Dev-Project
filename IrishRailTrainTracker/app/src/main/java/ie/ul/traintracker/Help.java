package ie.ul.traintracker;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;


public class Help extends Activity {

    TextView howAddJourneyHeading, howAddJourneyContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_relative);

        howAddJourneyHeading = (TextView) findViewById(R.id.howAddJourneyHeading);
        howAddJourneyContent = (TextView) findViewById(R.id.howAddJourneyContent);
    }
}
