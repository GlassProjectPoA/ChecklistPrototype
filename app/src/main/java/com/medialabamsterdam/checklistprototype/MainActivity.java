package com.medialabamsterdam.checklistprototype;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.location.Location;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.android.glass.media.Sounds;
import com.google.android.glass.touchpad.Gesture;
import com.google.android.glass.touchpad.GestureDetector;
import com.google.android.glass.view.WindowUtils;
import com.google.android.glass.widget.CardScrollView;
import com.medialabamsterdam.checklistprototype.Adapters.MyCardScrollAdapter;
import com.medialabamsterdam.checklistprototype.ContainerClasses.Area;
import com.medialabamsterdam.checklistprototype.ContainerClasses.Category;
import com.medialabamsterdam.checklistprototype.ContainerClasses.Locations;
import com.medialabamsterdam.checklistprototype.ContainerClasses.SubCategory;
import com.medialabamsterdam.checklistprototype.Database.DataBaseHelper;
import com.medialabamsterdam.checklistprototype.Polygon_contains_Point.Point;
import com.medialabamsterdam.checklistprototype.Polygon_contains_Point.Polygon;
import com.medialabamsterdam.checklistprototype.Utilities.Constants;
import com.medialabamsterdam.checklistprototype.Utilities.LocationUtils;
import com.medialabamsterdam.checklistprototype.Utilities.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by
 * Jose Carlos Quintas Junior
 * juniorquintas@gmail.com
 * on 01/03/2015.
 */
public class MainActivity extends Activity {

    public final static boolean OK_GLASS = false;
    private final static String TAG = "MAIN";
    private static final int CATEGORY_RATING_REQUEST = 7980;

    private CardScrollView mCardScroller;
    private GestureDetector mGestureDetector;
    private ArrayList<View> mCards;
    private LocationUtils mLocationUtils;
    private MyCardScrollAdapter mAdapter;
    private Location mActualLocation;
    private int areaIndex;
    private int areaCode;
    private int locationIndex;
    private ArrayList<Category> mCategories;
    private ArrayList<SubCategory> mSubCategories = new ArrayList<>();

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        defineLocale();

        // Defines if using voice commands or not.
        if (OK_GLASS) {
            getWindow().requestFeature(WindowUtils.FEATURE_VOICE_COMMANDS);
        }

        createLocationCard();
        handleLocationUtils();

        // Changes the color of some text in the view.
        Utils.ChangeTextColor(this, mCards.get(0), R.id.footer, R.array.tap_to_start, R.color.green);
        Utils.ChangeTextColor(this, mCards.get(0), R.id.instructions, R.array.tap_two_to_refresh, R.color.blue);

        //Regular CardScroller/Adapter procedure.
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mCardScroller = new CardScrollView(this);
        mAdapter = new MyCardScrollAdapter(mCards);
        mCardScroller.setAdapter(mAdapter);
        mCardScroller.setFocusable(false);
        mGestureDetector = createGestureDetector(this);
        setContentView(mCardScroller);
    }

    //region onResume/Pause onInstance
    @Override
    protected void onResume() {
        super.onResume();
        mCardScroller.activate();
        mLocationUtils.restart();
    }

    @Override
    protected void onPause() {
        mCardScroller.deactivate();
        mLocationUtils.stop();
        super.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putParcelableArrayList(Constants.PARCELABLE_CATEGORY, mCategories);
        savedInstanceState.putParcelableArrayList(Constants.PARCELABLE_SUBCATEGORY, mSubCategories);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mCategories = savedInstanceState.getParcelableArrayList(Constants.PARCELABLE_CATEGORY);
        mSubCategories = savedInstanceState.getParcelableArrayList(Constants.PARCELABLE_SUBCATEGORY);
    }
    //endregion

    //region Gesture Detector
    private GestureDetector createGestureDetector(final Context context) {
        GestureDetector gestureDetector = new GestureDetector(context);

        //Create a base listener for generic gestures
        gestureDetector.setBaseListener(new GestureDetector.BaseListener() {
            @Override
            public boolean onGesture(Gesture gesture) {
                AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                switch (gesture) {
                    case TAP:
                        Log.e(TAG, "TAP called.");
                        if (mActualLocation != null) {
                            openCategories();
                            am.playSoundEffect(Sounds.TAP);
                        } else {
                            am.playSoundEffect(Sounds.DISALLOWED);
                        }
                        break;
                    case TWO_LONG_PRESS:
                        am.playSoundEffect(handleLocationUtils());
                        return true;
                }
                return false;
            }
        });
        return gestureDetector;
    }

    @Override
    public boolean onGenericMotionEvent(MotionEvent event) {
        return mGestureDetector != null && mGestureDetector.onMotionEvent(event);
    }
    //endregion

    //region onActivityResult
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CATEGORY_RATING_REQUEST && resultCode == RESULT_OK) {
            handleLocationUtils();
            if (data.getIntExtra(Constants.EXTRA_LOCATION, 0) == locationIndex) {
                mCategories = data.getParcelableArrayListExtra(Constants.EXTRA_CATEGORY);
                mSubCategories = data.getParcelableArrayListExtra(Constants.EXTRA_SUBCATEGORY);
            }
        }
    }
    //endregion

    /**
     * Handles LocationUtils, starting it then querying for location updates and then stopping it
     * when we receive a location.
     *
     * @return returns an int related to a sound effect in order to give sound feedback to user.
     */
    private int handleLocationUtils() {
        if (mLocationUtils == null) {
            mLocationUtils = new LocationUtils(this);
        }
        mActualLocation = mLocationUtils.getLocation();
        if (mActualLocation != null) {
            Log.e(TAG, mActualLocation.toString());
            // Creates a point to check if actual location is inside an Area.
            Point mLocationPoint = new Point((float) mActualLocation.getLatitude(),
                    (float) mActualLocation.getLongitude());
            mLocationUtils.stop();
            // Calls the findArea() method in order to check which area are we in.
            return findArea(mLocationPoint);
        } else {
            //TODO remove test location
            Log.e(TAG, " \nCouldn't find location.\nUsing test Location");
/*          HVBO location*/
            mActualLocation = new Location("");
            mActualLocation.setLatitude(52.381234);
            mActualLocation.setLongitude(4.895540);/**/
/*          63vv location
            mActualLocation = new Location("");
            mActualLocation.setLatitude(52.388266);
            mActualLocation.setLongitude(4.820238);/**/
            Point mLocationPoint = new Point((float) mActualLocation.getLatitude(),
                    (float) mActualLocation.getLongitude());
            return findArea(mLocationPoint);
//            return Sounds.ERROR;
        }
    }

    /**
     * Searches the internal database in order to find if we are inside an Area.
     *
     * @param point out current location in a Point Object.
     * @return returns an int related to a sound effect in order to give sound feedback to user.
     */
    private int findArea(Point point) {
        List<Area> mAreas = DataBaseHelper.readArea(this);
        TextView tv = (TextView) mCards.get(0).findViewById(R.id.area_code);
        for (Area area : mAreas) {

            Point topLeft = Utils.stringToPoint(area.getTopLeft());
            Point botRight = Utils.stringToPoint(area.getBotRight());
            Point topRight = new Point(topLeft.x, botRight.y);
            Point botLeft = new Point(botRight.x, topLeft.y);

            Polygon polygon = Polygon.Builder()
                    .addVertex(topRight)
                    .addVertex(topLeft)
                    .addVertex(botLeft)
                    .addVertex(botRight)
                    .build();
            // If we are inside an Area then it updates areaIndex, areaCode values as well as the
            // Area text on the screen.
            if (polygon.contains(point)) {
                areaIndex = area.getId();
                areaCode = area.getCode();
                tv.setText(area.getName());
                // It then calls findLocation() method to find in which location are we, based on
                // which area we are.
                return findLocation(point);
            }
        }
        // Sets Area text to "?" if not inside any recorded area.
        tv.setText(R.string.unknown);
        return Sounds.ERROR;
    }

    /**
     * Searches the internal database in order to find if we are inside a Location.
     *
     * @param point out current location in a Point Object.
     * @return returns an int related to a sound effect in order to give sound feedback to user.
     */
    private int findLocation(Point point) {
        TextView tv = (TextView) mCards.get(0).findViewById(R.id.location_code);
        List<Locations> mLocations = DataBaseHelper.readLocations(this, areaIndex);
        for (Locations locations : mLocations) {

            Point topRight = Utils.stringToPoint(locations.getTopRight());
            Point topLeft = Utils.stringToPoint(locations.getTopLeft());
            Point botLeft = Utils.stringToPoint(locations.getBotLeft());
            Point botRight = Utils.stringToPoint(locations.getBotRight());

            Polygon polygon = Polygon.Builder()
                    .addVertex(topRight)
                    .addVertex(topLeft)
                    .addVertex(botLeft)
                    .addVertex(botRight)
                    .build();
            // If we are inside a Location then it updates locationIndex value as well as the
            // Location text on the screen.
            if (polygon.contains(point)) {
                locationIndex = locations.getLocationId();
                tv.setText(locations.getName());
                // Loads the Categories based on the given location.
                mCategories = new ArrayList<>(DataBaseHelper.readCategory(this, areaIndex, locationIndex));
                return Sounds.SUCCESS;
            }
        }
        // Sets Location text to "?" if not inside any recorded location.
        tv.setText(R.string.unknown);
        return Sounds.ERROR;
    }

    /**
     * Starts the CategoriesActivity.
     */
    private void openCategories() {
        Intent intent = new Intent(this, CategoriesActivity.class);
        intent.putParcelableArrayListExtra(Constants.EXTRA_CATEGORY, mCategories);
        intent.putParcelableArrayListExtra(Constants.EXTRA_SUBCATEGORY, mSubCategories);
        intent.putExtra(Constants.EXTRA_LOCATION, locationIndex);
        intent.putExtra(Constants.EXTRA_AREA_CODE, areaCode);
        startActivityForResult(intent, CATEGORY_RATING_REQUEST);
    }

    /**
     * Defines the locale based on Constants "Configuration".
     * Language can be changed to Dutch(nl) by changing LOAD_ALTERNATE_LANGUAGE to true.
     */
    private void defineLocale() {
        if (Constants.LOAD_ALTERNATE_LANGUAGE) {
            Locale locale = new Locale(Constants.ALTERNATE_LANGUAGE);
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.locale = locale;
            getBaseContext().getResources().updateConfiguration(config,
                    getBaseContext().getResources().getDisplayMetrics());
        }
    }

    /**
     * Creates the card view.
     */
    private void createLocationCard() {
        mCards = new ArrayList<>();
        LayoutInflater inflater = LayoutInflater.from(this);
        View card = inflater.inflate(R.layout.location_layout, null);
        mCards.add(card);
    }
}