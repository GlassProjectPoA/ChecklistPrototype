package com.medialabamsterdam.checklistprototype.Database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.medialabamsterdam.checklistprototype.ContainerClasses.Area;
import com.medialabamsterdam.checklistprototype.R;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Quintas on 29/04/2015.
 */
public class DataBaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "DATABASEHELPER";
    //The Android's default system path of your application database.
    private static String DB_PATH = "/data/data/com.medialabamsterdam.checklistprototype/databases/";
    private static String DB_NAME = "CheckListDB";
    public static final int DATABASE_VERSION = 2;
    private SQLiteDatabase myDataBase;
    private final Context mContext;

    /**
     * Constructor
     * Takes and keeps a reference of the passed context in order to access to the application assets and resources.
     * @param context
     */
    public DataBaseHelper(Context context) {
        super(context, DB_NAME, null, DATABASE_VERSION);
        this.mContext = context;
    }

    /**
     * This reads a file from the given Resource-Id and calls every line of it as a SQL-Statement
     *
     * @param context
     *
     * @param resourceId
     *  e.g. R.raw.food_db
     *
     * @return Number of SQL-Statements run
     * @throws IOException
     */
    public int insertFromFile(Context context, int resourceId, SQLiteDatabase db) throws IOException {
        // Resetting Counter
        int result = 0;

        // Open the resource
        InputStream insertsStream = context.getResources().openRawResource(resourceId);
        BufferedReader insertReader = new BufferedReader(new InputStreamReader(insertsStream));

        // Iterate through lines (assuming each insert has its own line and theres no other stuff)
        while (insertReader.ready()) {
            Log.d(TAG, "Came here " + result);
            String insertStmt = insertReader.readLine();
            db.execSQL(insertStmt);
            result++;
            Log.d(TAG, "Rows loaded from file= " + result);
        }
        insertReader.close();

        // returning number of inserted rows
        return result;
    }

    //<editor-fold desc="Description">
    /**
     * Creates a empty database on the system and rewrites it with your own database.
     * */
    public void createDataBase() throws IOException {
        Log.d(TAG, "BEEN HERE");
        boolean dbExist = checkDataBase();

        if(dbExist){
//            do nothing - database already exist
        }else{

//            By calling this method an empty database will be created into the default system path
//            of your application so we are gonna be able to overwrite that database with our database.
            this.getReadableDatabase();

            try {

                copyDataBase();

            } catch (IOException e) {

                throw new Error("Error copying database");

            }
        }
        Log.d(TAG, DB_PATH);
    }

    /**
     * Check if the database already exist to avoid re-copying the file each time you open the application.
     * @return true if it exists, false if it doesn't
     */
    private boolean checkDataBase(){

        SQLiteDatabase checkDB = null;

        try{
            String myPath = DB_PATH + DB_NAME;
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);

        }catch(SQLiteException e){

            //database does't exist yet.

        }

        if(checkDB != null){

            checkDB.close();

        }

        return checkDB != null;
    }

    /**
     * Copies your database from your local assets-folder to the just created empty database in the
     * system folder, from where it can be accessed and handled.
     * This is done by transfering bytestream.
     * */
    private void copyDataBase() throws IOException{

        //Open your local db as the input stream
        InputStream myInput = mContext.getAssets().open(DB_NAME);
        Log.d(TAG, myInput.toString() + "=====" + myInput.available());

        // Path to the just created empty db
        String outFileName = DB_PATH + DB_NAME;

        //Open the empty db as the output stream
        OutputStream myOutput = new FileOutputStream(outFileName);

        //transfer bytes from the inputfile to the outputfile
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer))>0){
            myOutput.write(buffer, 0, length);
        }

        //Close the streams
        myOutput.flush();
        myOutput.close();
        myInput.close();

    }

    public void openDataBase() throws SQLException {

        //Open the database
        String myPath = DB_PATH + DB_NAME;
        myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);

    }
    //</editor-fold>

    @Override
    public synchronized void close() {

        if(myDataBase != null)
            myDataBase.close();

        super.close();

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        DataBaseHelper dbHelper = new DataBaseHelper(mContext);
        try {
            int insertCount = dbHelper.insertFromFile(mContext, R.raw.checklist_db, db);
            Log.d(TAG, "Rows loaded from file= " + insertCount);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

/*    public static void ReadTableWhereOrder(Context context, String table,  String[] columns, String where, String orderBy){
        SQLiteDatabase db = new DataBaseHelper(context).getReadableDatabase();
        Cursor cur = db.query(table, columns, where, null, null, null, orderBy);
    }*/

    public static ArrayList<Area> readArea(Context context){
        ArrayList<Area> areaList = new ArrayList<>();
        SQLiteDatabase db;
        DataBaseHelper dbHelper = new DataBaseHelper(context);
        db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query(DataBaseContract.AreaEntry.TABLE_NAME, null, null, null, null, null, null);

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            int i =0;
            List<String> cursorData = new ArrayList<>();
            while (i< cursor.getColumnCount()) {
                cursorData.add(cursor.getString(i));
                i++;
            }
            Area area = new Area(
                    Integer.parseInt(cursorData.get(0)),
                    cursorData.get(1),
                    cursorData.get(2),
                    cursorData.get(3)
            );
            areaList.add(area);
        }
        cursor.close();
        for (Area a: areaList)
        {
            Log.e(TAG, a.toString());
        }
        return areaList;
    }

    // Add your public helper methods to access and get content from the database.
    // You could return cursors by doing "return myDataBase.query(....)" so it'd be easy
    // to you to create adapters for your views.

}
