package ie.ul.traintracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class TrainDB{

    /*column definitions*/
    //Index column
    public static final String KEY_ID = "_id";

    public static final String KEY_JOURNEY_START = "journey_start";
    public static final String KEY_JOURNEY_END = "journey_end";
    public static final String KEY_DEPARTURE_TIME = "departure_time";

    private Context context;

    // Database open
    private ModuleDBOpenHelper moduleDBOpenHelper;

    // Constructor
    public TrainDB(Context context) {
        this.context = context;
        moduleDBOpenHelper = new ModuleDBOpenHelper(context, ModuleDBOpenHelper.DATABASE_NAME, null,
                ModuleDBOpenHelper.DATABASE_VERSION);

        //populate database
        if (getAll().length == 0) {
            this.addRow("Tralee", "Cork", "11:00");
            this.addRow("Dublin", "Limerick", "15:00");
            this.addRow("Kildare", "Cork", "11:00");
        }
    }

    // Database methods

    // Called to close the database
    public void closeDatabase() {
        moduleDBOpenHelper.close();
    }

    public void addRow(String journeyStart, String journeyEnd, String departureTime) {
        // create new row of values
        ContentValues newValues = new ContentValues();

        // assign values for each row
        newValues.put(KEY_JOURNEY_START, journeyStart);
        newValues.put(KEY_JOURNEY_END, journeyEnd);
        newValues.put(KEY_DEPARTURE_TIME, departureTime);

        // insert row into table
        SQLiteDatabase db = moduleDBOpenHelper.getWritableDatabase();
        db.insert(ModuleDBOpenHelper.DATABASE_TABLE, null, newValues);
    }

    public void deleteRow(int idNr) {
        // where clause to determine which rows to delete
        String where = KEY_ID + "=" + idNr;
        String whereArgs[] = null;

        // delete rows that match where clause
        SQLiteDatabase db = moduleDBOpenHelper.getWritableDatabase();
        db.delete(ModuleDBOpenHelper.DATABASE_TABLE, where, whereArgs);
    }

    public void deleteAll() {
        String where = null;
        String whereArgs[] = null;

        SQLiteDatabase db = moduleDBOpenHelper.getWritableDatabase();
        db.delete(ModuleDBOpenHelper.DATABASE_TABLE, where, whereArgs);
    }

    /****************************************
    * User specific database queries
     ****************************************/

    public String[] getAll() {
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
        Cursor cursor = db.query(ModuleDBOpenHelper.DATABASE_TABLE, result_columns, where, whereArgs, groupBy, having, order);

        boolean result = cursor.moveToFirst();
        while (result) {

            journeyStart = cursor.getString(cursor.getColumnIndex(KEY_JOURNEY_START));
            journeyEnd = cursor.getString(cursor.getColumnIndex(KEY_JOURNEY_END));
            departureTime = cursor.getString(cursor.getColumnIndex(KEY_DEPARTURE_TIME));

            outputArray.add(journeyStart + "(" + departureTime + ")" + " to " + journeyEnd);
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
        Cursor cursor = db.query(ModuleDBOpenHelper.DATABASE_TABLE,
                result_columns, where, whereArgs, groupBy, having, order);

        boolean result = cursor.moveToFirst();
        while (result) {
            journeyStart = cursor.getString(cursor.getColumnIndex(KEY_JOURNEY_START));

            outputArray.add(journeyStart);
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
        Cursor cursor = db.query(ModuleDBOpenHelper.DATABASE_TABLE,
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
        Cursor cursor = db.query(ModuleDBOpenHelper.DATABASE_TABLE,
                result_columns, where, whereArgs, groupBy, having, order);

        boolean result = cursor.moveToFirst();
        while (result) {
            departureTime = cursor.getString(cursor.getColumnIndex(KEY_JOURNEY_END));

            outputArray.add(departureTime);
            result = cursor.moveToNext();
        }
        return outputArray.toArray(new String[outputArray.size()]);
    }

    private static class ModuleDBOpenHelper extends SQLiteOpenHelper {
        private static final String DATABASE_NAME = "TrainDB.db";
        private static final String DATABASE_TABLE = "Trains";
        private static final int DATABASE_VERSION= 2;

        // create database
        private static final String DATABASE_CREATE = " create table " +
                DATABASE_TABLE + " (" + KEY_ID +
                " integer primary key autoincrement, " +
                KEY_JOURNEY_START + " text not null, " +
                KEY_JOURNEY_END +  "text not null, " +
                KEY_DEPARTURE_TIME + " text not null);";

        public ModuleDBOpenHelper(Context context, String name, CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        // called to create database when none exists
        @Override
        public void onCreate(SQLiteDatabase db) {db.execSQL(DATABASE_CREATE);}

        // called when database version does not match
        // version on disk updated to current version
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // log update
            Log.w("TaskDBAdapter", "Updated database to current version " +
                    oldVersion + " to " +
                    newVersion + ", which will destroy all old data");

            // update to conform to new version
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
            onCreate(db);
        }
    }
}