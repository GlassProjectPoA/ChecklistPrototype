package com.medialabamsterdam.checklistprototype.Database;

import android.provider.BaseColumns;

/**
 * Created by
 * Jose Carlos Quintas Junior
 * juniorquintas@gmail.com
 * on 30/04/2015.
 */
public class DBContract {
    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    public DBContract() {}

    /* Inner class that defines the table contents */
    public static abstract class Area implements BaseColumns {
        public static final String TABLE_NAME = "Areas";
        public static final String COLUMN_NAME = "Name";
        public static final String COLUMN_TOP_LEFT = "Top_left";
        public static final String COLUMN_BOT_RIGHT = "Bot_right";
    }

    public static abstract class Location implements BaseColumns {
        public static final String TABLE_NAME = "Locations";
        public static final String COLUMN_NAME = "Name";
        public static final String COLUMN_TOP_LEFT = "Top_left";
        public static final String COLUMN_TOP_RIGHT = "Top_right";
        public static final String COLUMN_BOT_LEFT = "Bot_left";
        public static final String COLUMN_BOT_RIGHT = "Bot_right";
    }

    public static abstract class Category implements BaseColumns {
        public static final String TABLE_NAME = "Categories";
        public static final String COLUMN_NAME = "Name";
        public static final String COLUMN_NAME_NL = "Name_nl";
    }

    public static abstract class SubCategory implements BaseColumns {
        public static final String TABLE_NAME = "SubCategories";
        public static final String COLUMN_NAME = "Name";
        public static final String COLUMN_NAME_NL = "Name_nl";
    }

    public static abstract class Details implements BaseColumns {
        public static final String TABLE_NAME = "Details";
        public static final String COLUMN_DESCRIPTION = "Name";
        public static final String COLUMN_DETAILS = "Details";
        public static final String COLUMN_TITLE_1 = "Title_1";
        public static final String COLUMN_RATING_1 = "Rating_1";
        public static final String COLUMN_TITLE_2 = "Title_2";
        public static final String COLUMN_RATING_2 = "Rating_2";
        public static final String COLUMN_TITLE_3 = "Title_3";
        public static final String COLUMN_RATING_3 = "Rating_3";

        public static final String COLUMN_DESCRIPTION_NL = "Name_nl";
        public static final String COLUMN_DETAILS_NL = "Details_nl";
        public static final String COLUMN_TITLE_1_NL = "Title_1_nl";
        public static final String COLUMN_RATING_1_NL = "Rating_1_nl";
        public static final String COLUMN_TITLE_2_NL = "Title_2_nl";
        public static final String COLUMN_RATING_2_NL = "Rating_2_nl";
        public static final String COLUMN_TITLE_3_NL = "Title_3_nl";
        public static final String COLUMN_RATING_3_NL = "Rating_3_nl";
    }

    public static abstract class LocationsByArea implements BaseColumns {
        public static final String TABLE_NAME = "Locations_by_Area";
        public static final String COLUMN_LOCATION_ID = "Location_id";
        public static final String COLUMN_AREA_ID = "Area_id";
    }

    public static abstract class SubCatByCat implements BaseColumns {
        public static final String TABLE_NAME = "SubCategories_by_Category";
        public static final String COLUMN_CATEGORY_ID = "Category_id";
        public static final String COLUMN_SUBCATEGORY_ID = "SubCategory_id";
    }

}
