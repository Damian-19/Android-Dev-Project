package ie.ul.traintracker;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class CheckTrains extends Activity {

    TextView trainsPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_trains_relative);

        trainsPage = (TextView) findViewById(R.id.trainsPage);

    }
}
