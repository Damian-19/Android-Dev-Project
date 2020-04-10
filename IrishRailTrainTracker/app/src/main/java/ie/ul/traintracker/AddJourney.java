package ie.ul.traintracker;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

public class AddJourney extends Activity {

    TextView journeyPage;
    TextView dbView;
    String[] journeyStart;
    Spinner trainSpinner;

    TrainDB trainDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_journey_relative);

        dbView = (TextView) findViewById(R.id.dbcontent);
        trainDB = new TrainDB(getApplicationContext());
        showFullDatabase();

        journeyStart = trainDB.getAll();
        ArrayAdapter<String> trainAdapter = new ArrayAdapter<String>(getBaseContext(), R.layout.list_item, journeyStart);
        trainAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        trainSpinner.setAdapter(trainAdapter);


        journeyPage = (TextView) findViewById(R.id.addJourney);
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
