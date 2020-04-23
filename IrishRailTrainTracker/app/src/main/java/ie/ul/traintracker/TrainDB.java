package ie.ul.traintracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

public class TrainDB{

    /*column definitions*/
    //Index column
    public static final String KEY_ID = "_id";

    // timetable
    public static final String KEY_JOURNEY_START = "journey_start";
    public static final String KEY_JOURNEY_END = "journey_end";
    public static final String KEY_DEPARTURE_TIME = "departure_time";

    //user journeys
    public static final String KEY_CUSTOM_JOURNEY_START_LOCATION = "custom_journey_start_location";
    public static final String KEY_CUSTOM_JOURNEY_END_LOCATION = "custom_journey_end_location";
    public static final String KEY_CUSTOM_DEPARTURE_TIME = "custom_departure_time";

    private Context context;

    // Database open
    private ModuleDBOpenHelper moduleDBOpenHelper;

    // Constructor
    public TrainDB(Context context) {
        this.context = context;
        moduleDBOpenHelper = new ModuleDBOpenHelper(context, ModuleDBOpenHelper.DATABASE_NAME, null,
                ModuleDBOpenHelper.DATABASE_VERSION);

        //populate database
        if (getAllTimetable().length == 0 || getAllCustom().length == 0) {
            this.addRowTimetable("Tralee", "Cork", "11:00");
            this.addRowTimetable("Dublin", "Limerick", "15:00");
            this.addRowTimetable("Limerick", "Cork", "13:00");
            this.addRowTimetable("Newbridge", "Mallow", "10:00");
            this.addRowTimetable("Kildare", "Dublin", "16:00");
            this.addRowTimetable("Athlone", "Thurles", "11:00");
            this.addRowTimetable("Galway", "Tralee", "18:00");
            this.addRowTimetable("Killarney", "Cork", "09:00");
            this.addRowTimetable("Westport", "Sligo", "15:00");
            this.addRowTimetable("Carlow", "Waterford", "17:00");
            this.addRowCustomJourney("Tralee", "Cork", "14:00");
        }
    }

    // Database methods

    // Called to close the database
    public void closeDatabase() {
        moduleDBOpenHelper.close();
    }

    public void addRowTimetable(String journeyStart, String journeyEnd, String departureTime) {
        // create new row of values
        ContentValues newValues = new ContentValues();

        // assign values for each row
        newValues.put(KEY_JOURNEY_START, journeyStart);
        newValues.put(KEY_JOURNEY_END, journeyEnd);
        newValues.put(KEY_DEPARTURE_TIME, departureTime);

        // insert row into table
        SQLiteDatabase db = moduleDBOpenHelper.getWritableDatabase();
        db.insert(ModuleDBOpenHelper.TIMETABLE_TABLE, null, newValues);
    }

    public void addRowCustomJourney(String customJourneyStartLocation, String customJourneyEndLocation, String customDepartureTime) {
        ContentValues contentValues = new ContentValues();

        contentValues.put(KEY_CUSTOM_JOURNEY_START_LOCATION, customJourneyStartLocation);
        contentValues.put(KEY_CUSTOM_JOURNEY_END_LOCATION, customJourneyEndLocation);
        contentValues.put(KEY_CUSTOM_DEPARTURE_TIME, customDepartureTime);

        SQLiteDatabase db = moduleDBOpenHelper.getWritableDatabase();
        db.insert(ModuleDBOpenHelper.CUSTOM_JOURNEY_TABLE, null, contentValues);
    }

    public void deleteRow(int idNr) {
        // where clause to determine which rows to delete
        String where = KEY_ID + "=" + idNr;
        String whereArgs[] = null;

        // delete rows that match where clause
        SQLiteDatabase db = moduleDBOpenHelper.getWritableDatabase();
        db.delete(ModuleDBOpenHelper.TIMETABLE_TABLE, where, whereArgs);
    }

    public void deleteAll() {
        String where = null;
        String whereArgs[] = null;

        SQLiteDatabase db = moduleDBOpenHelper.getWritableDatabase();
        db.delete(ModuleDBOpenHelper.TIMETABLE_TABLE, where, whereArgs);
    }

    /****************************************
    * User specific database queries
     ****************************************/

    public String[] getAllTimetable() {
        ArrayList<String> outputArray = new ArrayList<String>();
        String[] result_columns = new String[]{
                KEY_JOURNEY_START, KEY_JOURNEY_END, KEY_DEPARTURE_TIME};

        String journeyStart, journeyEnd, departureTime;
        String where = null;
        String whereArgs[] = null;
        String groupBy = null;
        String having = null;
        String order = null;

        SQLiteDatabase db = moduleDBOpenHelper.getWritableDatabase();
        Cursor cursor = db.query(ModuleDBOpenHelper.TIMETABLE_TABLE, result_columns, where, whereArgs, groupBy, having, order);

        boolean result = cursor.moveToFirst();
        while (result) {

            journeyStart = cursor.getString(cursor.getColumnIndex(KEY_JOURNEY_START));
            journeyEnd = cursor.getString(cursor.getColumnIndex(KEY_JOURNEY_END));
            departureTime = cursor.getString(cursor.getColumnIndex(KEY_DEPARTURE_TIME));

            outputArray.add(departureTime + " " + "from" + " " + journeyStart + " " + "to" + " " + journeyEnd);
            result = cursor.moveToNext();
        }
        return outputArray.toArray(new String[outputArray.size()]);
    }

    public String[] getAllCustom() {
        ArrayList<String> outputArray = new ArrayList<String>();
        String[] result_columns = new String[]{
                KEY_CUSTOM_JOURNEY_START_LOCATION, KEY_CUSTOM_JOURNEY_END_LOCATION, KEY_CUSTOM_DEPARTURE_TIME};

        String customJourneyStart, customJourneyEnd, customDepartureTime;
        String where = null;
        String whereArgs[] = null;
        String groupBy = null;
        String having = null;
        String order = null;

        SQLiteDatabase db = moduleDBOpenHelper.getWritableDatabase();
        Cursor cursor = db.query(ModuleDBOpenHelper.CUSTOM_JOURNEY_TABLE, result_columns, where, whereArgs, groupBy, having, order);

        boolean result = cursor.moveToFirst();
        while (result) {

            customJourneyStart = cursor.getString(cursor.getColumnIndex(KEY_CUSTOM_JOURNEY_START_LOCATION));
            customJourneyEnd = cursor.getString(cursor.getColumnIndex(KEY_CUSTOM_JOURNEY_END_LOCATION));
            customDepartureTime = cursor.getString(cursor.getColumnIndex(KEY_CUSTOM_DEPARTURE_TIME));

            outputArray.add(customDepartureTime + " " + "from" + " " + customJourneyStart + " " + "to" + " " + customJourneyEnd);
            result = cursor.moveToNext();
        }
        return outputArray.toArray(new String[outputArray.size()]);
    }

    public String[] getStart() {

        ArrayList<String> outputArray = new ArrayList<String>();
        String[] result_columns = new String[] {
                KEY_JOURNEY_START
        };

        String journeyStart;
        String where = null;
        String whereArgs[] = null;
        String groupBy = null;
        String having = null;
        String order = null;

        SQLiteDatabase db = moduleDBOpenHelper.getWritableDatabase();
        Cursor cursor = db.query(ModuleDBOpenHelper.TIMETABLE_TABLE,
                result_columns, where, whereArgs, groupBy, having, order);

        boolean result = cursor.moveToFirst();
        while (result) {
            journeyStart = cursor.getString(cursor.getColumnIndex(KEY_JOURNEY_START));

            outputArray.add(journeyStart);
            result = cursor.moveToNext();
        }
        return outputArray.toArray(new String[outputArray.size()]);


    }

    public String[] getJourney(String journeyStartLocation, String journeyDepartureTime) {
        ArrayList<String> outputArray = new ArrayList<String>();
        String[] result_columns = new String[]{
                KEY_DEPARTURE_TIME, KEY_JOURNEY_END, KEY_JOURNEY_START};

        String journeyStart, journeyEnd, departureTime;

        String where = KEY_JOURNEY_START + "= ? AND " + KEY_JOURNEY_END + "= ?";
        String whereArgs[] = {journeyStartLocation.toString(), journeyDepartureTime.toString()};
        String groupBy = null;
        String having = null;
        String order = null;

        SQLiteDatabase db = moduleDBOpenHelper.getWritableDatabase();
        Cursor cursor = db.query(ModuleDBOpenHelper.TIMETABLE_TABLE,
                result_columns, where, whereArgs, groupBy, having, order);

        boolean result = cursor.moveToFirst();
        while (result) {
            journeyStart = cursor.getString(cursor.getColumnIndex(KEY_JOURNEY_START));
            journeyEnd = cursor.getString(cursor.getColumnIndex(KEY_JOURNEY_END));
            departureTime = cursor.getString(cursor.getColumnIndex(KEY_DEPARTURE_TIME));

            outputArray.add(departureTime + " " + "from" + " " + journeyStart + " " + "to" + " " + journeyEnd);
            result = cursor.moveToNext();
        }
        return outputArray.toArray(new String[outputArray.size()]);

    }

    public String[] getEnd() {

        ArrayList<String> outputArray = new ArrayList<String>();
        String[] result_columns = new String[] {
                KEY_JOURNEY_END
        };

        String journeyEnd;
        String where = null;
        String whereArgs[] = null;
        String groupBy = null;
        String having = null;
        String order = null;

        SQLiteDatabase db = moduleDBOpenHelper.getWritableDatabase();
        Cursor cursor = db.query(ModuleDBOpenHelper.TIMETABLE_TABLE,
                result_columns, where, whereArgs, groupBy, having, order);

        boolean result = cursor.moveToFirst();
        while (result) {
            journeyEnd = cursor.getString(cursor.getColumnIndex(KEY_JOURNEY_END));

            outputArray.add(journeyEnd);
            result = cursor.moveToNext();
        }
        return outputArray.toArray(new String[outputArray.size()]);
    }

    public String[] getStartTime() {

        ArrayList<String> outputArray = new ArrayList<String>();
        String[] result_columns = new String[] {
                KEY_DEPARTURE_TIME
        };

        String departureTime;
        String where = null;
        String whereArgs[] = null;
        String groupBy = null;
        String having = null;
        String order = null;

        SQLiteDatabase db = moduleDBOpenHelper.getWritableDatabase();
        Cursor cursor = db.query(ModuleDBOpenHelper.TIMETABLE_TABLE,
                result_columns, where, whereArgs, groupBy, having, order);

        boolean result = cursor.moveToFirst();
        while (result) {
            departureTime = cursor.getString(cursor.getColumnIndex(KEY_DEPARTURE_TIME));

            outputArray.add(departureTime);
            result = cursor.moveToNext();
        }
        return outputArray.toArray(new String[outputArray.size()]);
    }

    private static class ModuleDBOpenHelper extends SQLiteOpenHelper {
        private static final String DATABASE_NAME = "TrainDB.db";
        private static final String TIMETABLE_TABLE = "Trains";
        private static final String CUSTOM_JOURNEY_TABLE = "Journeys";
        private static final int DATABASE_VERSION= 9;

        // create database
        private static final String TIMETABLE_CREATE = "create table " +
                TIMETABLE_TABLE + " (" + KEY_ID +
                " integer primary key autoincrement, " +
                KEY_JOURNEY_START + " text not null collate nocase, " +
                KEY_JOURNEY_END +  " text not null collate nocase, " +
                KEY_DEPARTURE_TIME + " text not null);";

        private static final String CUSTOM_JOURNEY_CREATE = "create table " +
                CUSTOM_JOURNEY_TABLE + " (" + KEY_ID +
                " integer primary key autoincrement, " +
                KEY_CUSTOM_JOURNEY_START_LOCATION + " text not null collate nocase, " +
                KEY_CUSTOM_JOURNEY_END_LOCATION + " text not null collate nocase, " +
                KEY_CUSTOM_DEPARTURE_TIME + " text);";


        public ModuleDBOpenHelper(Context context, String name, CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        // called to create database when none exists
        @Override
        public void onCreate(SQLiteDatabase db) {

            db.execSQL(TIMETABLE_CREATE);
            db.execSQL(CUSTOM_JOURNEY_CREATE);
        }

        // called when database version does not match
        // version on disk updated to current version
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // log update
            Log.w("TaskDBAdapter", "Updated database to current version " +
                    oldVersion + " to " +
                    newVersion + ", which will destroy all old data");

            // update to conform to new version
            db.execSQL("DROP TABLE IF EXISTS " + TIMETABLE_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + CUSTOM_JOURNEY_TABLE);
            onCreate(db);
        }
    }
}
