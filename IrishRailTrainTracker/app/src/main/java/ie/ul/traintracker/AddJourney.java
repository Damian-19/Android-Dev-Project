package ie.ul.traintracker;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.view.View;

public class AddJourney extends Activity {

    TextView journeyPage;
    TextView dbView;
    String[] journeyStart, journeyEnd, departureTime;
    Spinner trainSpinner;

    TrainDB trainDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_journey_relative);
        journeyPage = (TextView) findViewById(R.id.addJourney);

        dbView = (TextView) findViewById(R.id.dbcontent);
        trainDB = new TrainDB(getApplicationContext());
        showFullDatabase();

        journeyEnd = trainDB.getEnd();
        departureTime = trainDB.getStartTime();
        journeyStart = trainDB.getStart();
        ArrayAdapter<String> trainAdapter = new ArrayAdapter<String>(getBaseContext(), R.layout.list_item, journeyStart);
        trainAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        trainSpinner.setAdapter(trainAdapter);



    }

    private void print(String[] content) {
        dbView.setText("");
        for (int i=0; i<content.length; i++) {
            dbView.append(content[i]+"\n");
        }
    }

    private void showFullDatabase() {
        print(trainDB.getAll());
    }

}
