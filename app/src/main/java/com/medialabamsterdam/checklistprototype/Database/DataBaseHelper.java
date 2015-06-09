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
import com.medialabamsterdam.checklistprototype.ContainerClasses.Detail;
import com.medialabamsterdam.checklistprototype.ContainerClasses.Locations;
import com.medialabamsterdam.checklistprototype.ContainerClasses.SubCategory;
import com.medialabamsterdam.checklistprototype.R;
import com.medialabamsterdam.checklistprototype.Utilities.Constants;
import com.medialabamsterdam.checklistprototype.Utilities.Utils;

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
    private final Context mContext;
    private SQLiteDatabase myDataBase;

    /**
     * Constructor
     * Takes and keeps a reference of the passed context in order to access the application assets
     * and resources.
     *
     * @param context the activity's context.
     */
    private DataBaseHelper(Context context) {
        super(context, DB_NAME, null, DATABASE_VERSION);
        this.mContext = context;
    }

    /**
     * This function is used to query data from the Area Table, returns all Areas.
     *
     * @param context the activity's context.
     * @return a List<Area>.
     */
    public static List<Area> readArea(Context context) {
        List<Area> areaList = new ArrayList<>();
        SQLiteDatabase db;
        DataBaseHelper dbHelper = new DataBaseHelper(context);
        db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DBContract.Area.TABLE_NAME, null, null, null, null, null, null);

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            int i = 0;
            List<String> cursorData = new ArrayList<>();
            while (i < cursor.getColumnCount()) {
                cursorData.add(cursor.getString(i));
                i++;
            }
            Area area = new Area(
                    Integer.parseInt(cursorData.get(0)),
                    cursorData.get(1),
                    cursorData.get(2),
                    cursorData.get(3),
                    Integer.parseInt(cursorData.get(4))
            );
            areaList.add(area);
        }
        cursor.close();
        db.close();
        return areaList;
    }

    /**
     * This function is used to query data from the Locations Table based on the Area_id related to
     * a given location.
     *
     * @param context   the activity's context.
     * @param areaIndex the _id of the Area that you want to query the locations.
     * @return a List<Locations>.
     */
    public static List<Locations> readLocations(Context context, int areaIndex) {
        List<Locations> locationList = new ArrayList<>();
        SQLiteDatabase db;
        DataBaseHelper dbHelper = new DataBaseHelper(context);
        db = dbHelper.getReadableDatabase();
        Cursor cursor;
        String query = "SELECT " + DBContract.Location.TABLE_NAME + "." + DBContract.Location._ID +
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

        cursor = db.rawQuery(query, new String[]{String.valueOf(areaIndex)});

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
     * @param context       the activity's context.
     * @param areaIndex     the _id of the Area the Location is in.
     * @param locationIndex the _id of the Location the user is in.
     * @return a Category List by calling readCategory() again.
     */
    private static List<Category> writeCatByLocation(Context context, int areaIndex, int locationIndex) {
        SQLiteDatabase db;
        DataBaseHelper dbHelper = new DataBaseHelper(context);
        db = dbHelper.getWritableDatabase();
        String[] args = new String[]{String.valueOf(areaIndex)};
        String where = DBContract.CatByArea.COLUMN_AREA_ID + " =? ";
        Cursor cursor = db.query(DBContract.CatByArea.TABLE_NAME, null, where, args, null, null, null);

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            ContentValues values = new ContentValues();
            values.put(DBContract.CatByLocation.COLUMN_CATEGORY_ID,
                    cursor.getInt(cursor.getColumnIndex(DBContract.CatByArea.COLUMN_CATEGORY_ID)));
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
     * Read the entries on the Category_by_Location table. If the query returns an empty result
     * then it calls writeCatByLocation().
     *
     * @param context       the activity's context.
     * @param areaIndex     the _id of the Area the Location is in.
     * @param locationIndex the _id of the Location the user is in.
     * @return a Category List.
     */
    public static List<Category> readCategory(Context context, int areaIndex, int locationIndex) {
        List<Category> categoryList = new ArrayList<>();
        SQLiteDatabase db;
        DataBaseHelper dbHelper = new DataBaseHelper(context);
        db = dbHelper.getReadableDatabase();
        Cursor cursor;
        String columnName = DBContract.Category.COLUMN_NAME;
        if (Constants.LOAD_ALTERNATE_LANGUAGE) {
            columnName = DBContract.Category.COLUMN_NAME_NL;
        }
        String query = "SELECT " + DBContract.Category.TABLE_NAME + "." + DBContract.Category._ID +
                ", " + DBContract.CatByLocation.TABLE_NAME + "." + DBContract.CatByLocation._ID +
                ", " + columnName +
                ", " + DBContract.CatByLocation.COLUMN_REMOVE +
                " FROM " + DBContract.Category.TABLE_NAME +
                ", " + DBContract.CatByLocation.TABLE_NAME +
                " WHERE " + DBContract.CatByLocation.COLUMN_CATEGORY_ID +
                " = " + DBContract.Category.TABLE_NAME + "." + DBContract.Category._ID +
                " AND " + DBContract.CatByLocation.COLUMN_LOCATION_ID +
                " =? AND " + DBContract.CatByLocation.COLUMN_REMOVE +
                " =? ORDER BY " + DBContract.Category.TABLE_NAME + "." + DBContract.Category._ID +
                ", " + DBContract.CatByLocation.TABLE_NAME + "." + DBContract.CatByLocation._ID;

        cursor = db.rawQuery(query, new String[]{String.valueOf(locationIndex), String.valueOf(0)});

        if (cursor.getCount() == 0) {
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
                    Integer.parseInt(cursorData.get(3)) != 0
            );
            categoryList.add(category);
        }
        cursor.close();
        db.close();
        return categoryList;
    }

    /**
     * Write entries on the SubCategories_by_Location_And_Categories table based on the Category
     * and Location the SubCategory is in.
     *
     * @param context              the activity's context.
     * @param categoryID           the ID of the Category to use.
     * @param categoryByLocationId the ID of the categoryByLocation stored on the Category.class.
     * @return a SubCategory list by calling readSubCategory() again.
     */
    private static List<SubCategory> writeSubCatByLocationAndCategory(
            Context context, int categoryID, int categoryByLocationId, int areaCode) {
        SQLiteDatabase db;
        DataBaseHelper dbHelper = new DataBaseHelper(context);
        db = dbHelper.getWritableDatabase();
        String[] args = new String[]{String.valueOf(categoryID)};
        String where = DBContract.SubCatByCat.COLUMN_CATEGORY_ID + " =? ";
        Cursor cursor = db.query(DBContract.SubCatByCat.TABLE_NAME, null, where, args, null, null, null);

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            ContentValues values = new ContentValues();
            values.put(DBContract.SubCatByCatAndLoc.COLUMN_SUBCATEGORY_ID, cursor.getInt(
                    cursor.getColumnIndex(DBContract.SubCatByCat.COLUMN_SUBCATEGORY_ID)));
            values.put(DBContract.SubCatByCatAndLoc.COLUMN_CATEGORYBYLOCATION_ID,
                    categoryByLocationId);
            values.put(DBContract.SubCatByCatAndLoc.COLUMN_REMOVE, 0);
            try {
                db.insertOrThrow(
                        DBContract.SubCatByCatAndLoc.TABLE_NAME,
                        null,
                        values);
            } catch (SQLiteConstraintException e) {
                Log.e("DB ERROR", e.toString());
            }
        }
        cursor.close();
        db.close();
        return readSubCategory(context, categoryID, categoryByLocationId, areaCode);
    }

    /**
     * Read the entries on the SubCategories_by_Location_And_Categories table. If the query
     * returns an empty result then it calls writeSubCatByLocationAndCategory().
     *
     * @param context              the activity's context.
     * @param categoryID           the ID of the Category to use.
     * @param categoryByLocationId the ID of the categoryByLocation stored on the Category.class.
     * @return a SubCategory list.
     */
    public static List<SubCategory> readSubCategory(Context context, int categoryID,
                                                    int categoryByLocationId, int areaCode) {
        List<SubCategory> subCategoryList = new ArrayList<>();
        SQLiteDatabase db;
        DataBaseHelper dbHelper = new DataBaseHelper(context);
        db = dbHelper.getReadableDatabase();
        Cursor cursor;
        String columnName = DBContract.SubCategory.COLUMN_NAME;
        if (Constants.LOAD_ALTERNATE_LANGUAGE) {
            columnName = DBContract.SubCategory.COLUMN_NAME_NL;
        }
        String query = "SELECT " + DBContract.SubCategory.TABLE_NAME + "." + DBContract.SubCategory._ID +
                ", " + columnName +
                ", " + DBContract.SubCatByCatAndLoc.COLUMN_REMOVE +
                ", " + DBContract.SubCatByCat.TABLE_NAME + "." + DBContract.SubCatByCat.COLUMN_CODE +
                " FROM " + DBContract.SubCatByCatAndLoc.TABLE_NAME +
                ", " + DBContract.SubCategory.TABLE_NAME +
                ", " + DBContract.SubCatByCat.TABLE_NAME +
                " WHERE " + DBContract.SubCatByCatAndLoc.COLUMN_CATEGORYBYLOCATION_ID +
                " =? AND " + DBContract.SubCatByCat.COLUMN_CATEGORY_ID +
                " =? AND " + DBContract.SubCatByCat.TABLE_NAME + "."
                + DBContract.SubCatByCat.COLUMN_SUBCATEGORY_ID +
                " = " + DBContract.SubCategory.TABLE_NAME + "." + DBContract.SubCategory._ID +
                " AND " + DBContract.SubCatByCatAndLoc.TABLE_NAME + "."
                + DBContract.SubCatByCatAndLoc.COLUMN_SUBCATEGORY_ID +
                " = " + DBContract.SubCategory.TABLE_NAME + "." + DBContract.SubCategory._ID;

        cursor = db.rawQuery(query, new String[]{String.valueOf(categoryByLocationId),
                String.valueOf(categoryID)});

        if (cursor.getCount() == 0) {
            cursor.close();
            db.close();
            return writeSubCatByLocationAndCategory(context, categoryID, categoryByLocationId, areaCode);
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
                    categoryID,
                    Integer.parseInt(cursorData.get(0)),
                    cursorData.get(1),
                    Integer.parseInt(cursorData.get(2)) != 0,
                    Integer.parseInt(areaCode + cursorData.get(3))
            );
            subCategoryList.add(subCategory);
            Log.e(TAG, subCategory.toString());
        }
        cursor.close();
        db.close();
        for (SubCategory sc : subCategoryList) {
            if (sc.isRemove()) {
                subCategoryList.remove(sc);
            }
        }
        return subCategoryList;
    }

    /**
     * Reads the entries on the Details table where Category_id and SubCategory_id equals the given
     * respective parameters.
     *
     * @param context       the activity's context.
     * @param categoryID    the Category ID to be used in que query.
     * @param subCategoryID the SubCategory ID to be used in the query.
     * @return returns a Detail object.
     */
    public static Detail readDetails(Context context, int categoryID, int subCategoryID) {
        Detail detail = new Detail();
        SQLiteDatabase db;
        DataBaseHelper dbHelper = new DataBaseHelper(context);
        db = dbHelper.getReadableDatabase();
        Cursor cursor;
        int subCatByCat = -1;

        String[] args = new String[]{String.valueOf(categoryID), String.valueOf(subCategoryID)};
        String where = DBContract.SubCatByCat.COLUMN_CATEGORY_ID + " =? AND "
                + DBContract.SubCatByCat.COLUMN_SUBCATEGORY_ID + " =? ";

        cursor = db.query(DBContract.SubCatByCat.TABLE_NAME, null, where, args, null, null, null);

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            subCatByCat = cursor.getInt(cursor.getColumnIndex(DBContract.SubCatByCat._ID));
        }
        cursor.close();

        String query = "SELECT " + DBContract.Details.TABLE_NAME + "." + DBContract.Details._ID +
                ", " + DBContract.Details.COLUMN_DESCRIPTION +
                ", " + DBContract.Details.COLUMN_DESCRIPTION_NL +
                ", " + DBContract.Details.COLUMN_DETAILS +
                ", " + DBContract.Details.COLUMN_DETAILS_NL +
                ", " + DBContract.Details.COLUMN_TITLE_1 +
                ", " + DBContract.Details.COLUMN_TITLE_1_NL +
                ", " + DBContract.Details.COLUMN_RATING_1 +
                ", " + DBContract.Details.COLUMN_RATING_1_NL +
                ", " + DBContract.Details.COLUMN_TITLE_2 +
                ", " + DBContract.Details.COLUMN_TITLE_2_NL +
                ", " + DBContract.Details.COLUMN_RATING_2 +
                ", " + DBContract.Details.COLUMN_RATING_2_NL +
                ", " + DBContract.Details.COLUMN_TITLE_3 +
                ", " + DBContract.Details.COLUMN_TITLE_3_NL +
                ", " + DBContract.Details.COLUMN_RATING_3 +
                ", " + DBContract.Details.COLUMN_RATING_3_NL +
                " FROM " + DBContract.Details.TABLE_NAME +
                ", " + DBContract.DetailsBySubCat.TABLE_NAME +
                " WHERE " + DBContract.DetailsBySubCat.TABLE_NAME + "."
                + DBContract.DetailsBySubCat.COLUMN_SUBCATEGORYBYCATEGORY_ID +
                " =? AND " + DBContract.Details.TABLE_NAME + "." + DBContract.Details._ID +
                " = " + DBContract.DetailsBySubCat.TABLE_NAME + "."
                + DBContract.DetailsBySubCat.COLUMN_DETAIL_ID;

        cursor = db.rawQuery(query, new String[]{String.valueOf(subCatByCat)});

        if (cursor.getCount() == 0) {
            cursor.close();
            db.close();
            return null;
        }

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            int i = 0;
            List<String> cursorData = new ArrayList<>();
            while (i < cursor.getColumnCount()) {
                cursorData.add(cursor.getString(i));
                i++;
            }
            detail = new Detail(
                    Integer.parseInt(cursorData.get(0)),
                    Utils.stringToArray(cursorData.get(1), "_,,_"),
                    Utils.stringToArray(cursorData.get(2), "_,,_"),
                    cursorData.get(3),
                    cursorData.get(4),
                    cursorData.get(5),
                    cursorData.get(6),
                    Utils.stringToArray(cursorData.get(7), "_,,_"),
                    Utils.stringToArray(cursorData.get(8), "_,,_"),
                    cursorData.get(9),
                    cursorData.get(10),
                    Utils.stringToArray(cursorData.get(11), "_,,_"),
                    Utils.stringToArray(cursorData.get(12), "_,,_"),
                    cursorData.get(13),
                    cursorData.get(14),
                    Utils.stringToArray(cursorData.get(15), "_,,_"),
                    Utils.stringToArray(cursorData.get(16), "_,,_")
            );
        }
        cursor.close();
        db.close();
        return detail;
    }

    /**
     * This reads a file from the given Resource-Id and calls every line of it as a SQL-Statement
     *
     * @param context    the activity's context.
     * @param resourceId e.g. R.raw.food_db
     * @return Number of SQL-Statements run
     * @throws IOException
     */
    private int insertFromFile(Context context, int resourceId, SQLiteDatabase db) throws IOException {
        // Resetting Counter
        int result = 0;

        // Open the resource
        InputStream insertsStream = context.getResources().openRawResource(resourceId);
        BufferedReader insertReader = new BufferedReader(new InputStreamReader(insertsStream));

        // Iterate through lines (assuming each insert has its own line and there's no other stuff)
        while (insertReader.ready()) {
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
        if (myDataBase != null)
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
}
