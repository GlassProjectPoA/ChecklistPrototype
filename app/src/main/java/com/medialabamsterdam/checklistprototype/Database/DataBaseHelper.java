package com.medialabamsterdam.checklistprototype.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.medialabamsterdam.checklistprototype.ContainerClasses.Area;
import com.medialabamsterdam.checklistprototype.ContainerClasses.Category;
import com.medialabamsterdam.checklistprototype.ContainerClasses.Locations;
import com.medialabamsterdam.checklistprototype.ContainerClasses.SubCategory;
import com.medialabamsterdam.checklistprototype.R;
import com.medialabamsterdam.checklistprototype.Utilities.Constants;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by
 * Jose Carlos Quintas Junior
 * juniorquintas@gmail.com
 * on 29/04/2015.
 */
public class DataBaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "DATABASEHELPER";
    private static final String DB_NAME = "CheckListDB";
    private static final int DATABASE_VERSION = 1;
    private SQLiteDatabase myDataBase;
    private final Context mContext;

    /**
     * Constructor
     * Takes and keeps a reference of the passed context in order to access the application assets and resources.
     * @param context the activity's context.
     */
    private DataBaseHelper(Context context) {
        super(context, DB_NAME, null, DATABASE_VERSION);
        this.mContext = context;
    }

    //<editor-fold desc="DB creation and management">
    /**
     * This reads a file from the given Resource-Id and calls every line of it as a SQL-Statement
     *
     * @param context the activity's context.
     * @param resourceId e.g. R.raw.food_db
     *
     * @return Number of SQL-Statements run
     * @throws IOException
     */
    private int insertFromFile(Context context, int resourceId, SQLiteDatabase db) throws IOException {
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

    @Override
    public synchronized void close() {

        if(myDataBase != null)
            myDataBase.close();

        super.close();

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        DataBaseHelper dbHelper = new DataBaseHelper(mContext);
        try {
            int insertCount = dbHelper.insertFromFile(mContext, R.raw.checklist_db, db);
            Log.d(TAG, "Rows loaded from file= " + insertCount);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
    //</editor-fold>

    /**
     * This function is used to query data from the Area Table, returns all Areas.
     *
     * @param context the activity's context.
     * @return a List<Area>.
     */
    public static List<Area> readArea(Context context){
        List<Area> areaList = new ArrayList<>();
        SQLiteDatabase db;
        DataBaseHelper dbHelper = new DataBaseHelper(context);
        db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DBContract.Area.TABLE_NAME, null, null, null, null, null, null);

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
        db.close();
        return areaList;
    }

    /**
     * This function is used to query data from the Locations Table based on the Area_id related to a given location.
     *
     * @param context the activity's context.
     * @param areaIndex the _id of the Area that you want to query the locations.
     * @return a List<Locations>.
     */
    public static List<Locations> readLocations(Context context, int areaIndex){
        List<Locations> locationList = new ArrayList<>();
        SQLiteDatabase db;
        DataBaseHelper dbHelper = new DataBaseHelper(context);
        db = dbHelper.getReadableDatabase();
        Cursor cursor;
        String query =  "SELECT " + DBContract.Location.TABLE_NAME +"."+ DBContract.Location._ID +
                        ", " + DBContract.Location.COLUMN_NAME +
                        ", " + DBContract.Location.COLUMN_TOP_RIGHT +
                        ", " + DBContract.Location.COLUMN_TOP_LEFT +
                        ", " + DBContract.Location.COLUMN_BOT_LEFT +
                        ", " + DBContract.Location.COLUMN_BOT_RIGHT +
                        " FROM " + DBContract.Location.TABLE_NAME +
                        ", " + DBContract.LocationsByArea.TABLE_NAME +
                        " WHERE " + DBContract.LocationsByArea.COLUMN_AREA_ID +
                        " =? AND " + DBContract.LocationsByArea.COLUMN_LOCATION_ID +
                        " = " + DBContract.Location.TABLE_NAME + "." + DBContract.Location._ID;

        cursor = db.rawQuery(query, new String [] {String.valueOf(areaIndex)});

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            int i = 0;
            List<String> cursorData = new ArrayList<>();
            while (i < cursor.getColumnCount()) {
                cursorData.add(cursor.getString(i));
                i++;
            }
            Locations location = new Locations(
                    Integer.parseInt(cursorData.get(0)),
                    cursorData.get(1),
                    cursorData.get(2),
                    cursorData.get(3),
                    cursorData.get(4),
                    cursorData.get(5)
            );
            locationList.add(location);
        }
        cursor.close();
        db.close();
        return locationList;
    }

    /**
     * Write entries on the Categories_by_Location table based on the Area the Location is in.
     *
     * @param context the activity's context.
     * @param areaIndex the _id of the Area the Location is in.
     * @param locationIndex the _id of the Location the user is in.
     * @return a Category List by calling readCategory() again.
     */
    private static List<Category> writeCatByLocation(Context context, int areaIndex, int locationIndex) {
        SQLiteDatabase db;
        DataBaseHelper dbHelper = new DataBaseHelper(context);
        db = dbHelper.getWritableDatabase();
        String[] args = new String [] {String.valueOf(areaIndex)};
        String where = DBContract.CatByArea.COLUMN_AREA_ID + " =? ";
        Cursor cursor = db.query(DBContract.CatByArea.TABLE_NAME, null, where, args, null, null, null);

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            ContentValues values = new ContentValues();
            values.put(DBContract.CatByLocation.COLUMN_CATEGORY_ID, cursor.getInt(cursor.getColumnIndex(DBContract.CatByArea.COLUMN_CATEGORY_ID)));
            values.put(DBContract.CatByLocation.COLUMN_LOCATION_ID, locationIndex);
            values.put(DBContract.CatByLocation.COLUMN_REMOVE, 0);
            try {
                db.insertOrThrow(
                        DBContract.CatByLocation.TABLE_NAME,
                        null,
                        values);
            } catch (SQLiteConstraintException e) {
                Log.e("DB ERROR", e.toString());
            }
        }
        cursor.close();
        db.close();
        return readCategory(context, areaIndex, locationIndex);
    }

    /**
     * Read the entries on the Category_by_Location table. If the query returns an empty result then it calls writeCatByLocation().
     *
     * @param context the activity's context.
     * @param areaIndex the _id of the Area the Location is in.
     * @param locationIndex the _id of the Location the user is in.
     * @return a Category List.
     */
    public static List<Category> readCategory(Context context, int areaIndex, int locationIndex){
        List<Category> categoryList = new ArrayList<>();
        SQLiteDatabase db;
        DataBaseHelper dbHelper = new DataBaseHelper(context);
        db = dbHelper.getReadableDatabase();
        Cursor cursor;
        String columnName = DBContract.Category.COLUMN_NAME;
        if (Constants.LOAD_ALTERNATE_LANGUAGE){
            columnName = DBContract.Category.COLUMN_NAME_NL;
        }
        String query =  "SELECT " + DBContract.Category.TABLE_NAME +"."+ DBContract.Category._ID +
                ", " + DBContract.Category.TABLE_NAME +"."+DBContract.CatByLocation._ID +
                ", " + columnName +
                ", " + DBContract.CatByLocation.COLUMN_REMOVE +
                " FROM " + DBContract.CatByLocation.TABLE_NAME +
                ", " + DBContract.Category.TABLE_NAME +
                " WHERE " + DBContract.CatByLocation.COLUMN_LOCATION_ID +
                " =? AND " + DBContract.CatByLocation.COLUMN_CATEGORY_ID +
                " = " + DBContract.Category.TABLE_NAME + "." + DBContract.Category._ID +
                " AND " + DBContract.CatByLocation.COLUMN_REMOVE +
                " =?";

        cursor = db.rawQuery(query, new String [] {String.valueOf(locationIndex), String.valueOf(0)});

        if(cursor.getCount()==0){
            cursor.close();
            db.close();
            return writeCatByLocation(context, areaIndex, locationIndex);
        }

        Log.e(TAG, Arrays.toString(cursor.getColumnNames()));

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            int i = 0;
            List<String> cursorData = new ArrayList<>();
            while (i < cursor.getColumnCount()) {
                cursorData.add(cursor.getString(i));
                i++;
            }
            Log.e(TAG, cursorData.toString());
            Category category = new Category(
                    Integer.parseInt(cursorData.get(0)),
                    Integer.parseInt(cursorData.get(1)),
                    cursorData.get(2),
                    Boolean.parseBoolean(cursorData.get(3))
            );
            categoryList.add(category);
        }
        cursor.close();
        db.close();
        return categoryList;
    }

    private static List<SubCategory> writeSubCatByLocationCategory(Context context, int areaIndex, int locationIndex) {
        SQLiteDatabase db;
        DataBaseHelper dbHelper = new DataBaseHelper(context);
        db = dbHelper.getWritableDatabase();
        String[] args = new String [] {String.valueOf(areaIndex)};
        String where = DBContract.CatByArea.COLUMN_AREA_ID + " =? ";
        Cursor cursor = db.query(DBContract.CatByArea.TABLE_NAME, null, where, args, null, null, null);

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            ContentValues values = new ContentValues();
            values.put(DBContract.CatByLocation.COLUMN_CATEGORY_ID, cursor.getInt(cursor.getColumnIndex(DBContract.CatByArea.COLUMN_CATEGORY_ID)));
            values.put(DBContract.CatByLocation.COLUMN_LOCATION_ID, locationIndex);
            values.put(DBContract.CatByLocation.COLUMN_REMOVE, 0);
            try {
                db.insertOrThrow(
                        DBContract.CatByLocation.TABLE_NAME,
                        null,
                        values);
            } catch (SQLiteConstraintException e) {
                Log.e("DB ERROR", e.toString());
            }
        }
        cursor.close();
        db.close();
        return readSubCategory(context, areaIndex, locationIndex);
    }

    public static List<SubCategory> readSubCategory(Context context, int areaIndex, int locationIndex){
        List<SubCategory> subCategoryList = new ArrayList<>();
        SQLiteDatabase db;
        DataBaseHelper dbHelper = new DataBaseHelper(context);
        db = dbHelper.getReadableDatabase();
        Cursor cursor;
        String columnName = DBContract.SubCategory.COLUMN_NAME;
        if (Constants.LOAD_ALTERNATE_LANGUAGE){
            columnName = DBContract.SubCategory.COLUMN_NAME_NL;
        }
        String query =  "SELECT " + DBContract.SubCategory.TABLE_NAME +"."+ DBContract.SubCategory._ID +
                ", " + columnName +
                ", " + DBContract.SubCatByCatAndLoc.COLUMN_REMOVE +
                " FROM " + DBContract.CatByLocation.TABLE_NAME +
                ", " + DBContract.Category.TABLE_NAME +
                " WHERE " + DBContract.CatByLocation.COLUMN_LOCATION_ID +
                " =? AND " + DBContract.CatByLocation.COLUMN_CATEGORY_ID +
                " = " + DBContract.Category.TABLE_NAME + "." + DBContract.Category._ID;

        cursor = db.rawQuery(query, new String [] {String.valueOf(locationIndex)});

        if(cursor.getCount()==0){
            cursor.close();
            db.close();
            return writeSubCatByLocationCategory(context, areaIndex, locationIndex);
        }

        Log.e(TAG, Arrays.toString(cursor.getColumnNames()));

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            int i = 0;
            List<String> cursorData = new ArrayList<>();
            while (i < cursor.getColumnCount()) {
                cursorData.add(cursor.getString(i));
                i++;
            }
            Log.e(TAG, cursorData.toString());
            SubCategory subCategory = new SubCategory(
                    1, "1", 1, "1", 1
            );
            subCategoryList.add(subCategory);
        }
        cursor.close();
        db.close();
        return subCategoryList;
    }
}
