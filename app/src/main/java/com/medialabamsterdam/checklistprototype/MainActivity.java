package com.medialabamsterdam.checklistprototype;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.location.Location;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.CountDownTimer;
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
import com.medialabamsterdam.checklistprototype.ContainerClasses.Locations;
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
    public final static String TAG = "MAIN";

    private CardScrollView mCardScroller;
    private GestureDetector mGestureDetector;
    private ArrayList<View> mCards;
    private LocationUtils mLocationUtils;
    private MyCardScrollAdapter mAdapter;
    private int areaIndex;
    private int locationIndex;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        handleLocationUtils();
        defineLocale();

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        if (OK_GLASS) {
            getWindow().requestFeature(WindowUtils.FEATURE_VOICE_COMMANDS);
        }

        createLocationCard();

        Utils.ChangeTextColor(this, mCards.get(0), R.id.footer, R.array.tap_to_start, R.color.green);
        Utils.ChangeTextColor(this, mCards.get(0), R.id.instructions, R.array.tap_two_to_refresh, R.color.blue);

        mCardScroller = new CardScrollView(this);
        mAdapter = new MyCardScrollAdapter(mCards);
        mCardScroller.setAdapter(mAdapter);
        mCardScroller.setFocusable(false);
        mGestureDetector = createGestureDetector(this);
        setContentView(mCardScroller);
        new CountDownTimer(1500, 500) {

            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                handleLocationUtils();
            }

        }.start();
    }

    private boolean handleLocationUtils() {
        if (mLocationUtils == null) {
            mLocationUtils = new LocationUtils(this);
        }
        Location mActualLocation = mLocationUtils.getLocation();
        if (mActualLocation != null) {
            Log.e("WORKS!", mActualLocation.toString());
            Point mLocationPoint = new Point((float) mActualLocation.getLatitude(), (float) mActualLocation.getLongitude());
            return findArea(mLocationPoint);
        } else {
            return false;
        }
    }

    private boolean findArea(Point point){
        List<Area> mAreas = DataBaseHelper.readArea(this);
        TextView tv = (TextView)mCards.get(0).findViewById(R.id.area_code);
        for (Area area: mAreas){

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
            if (polygon.contains(point)){
                areaIndex = area.getAreaId();
                tv.setText(area.getAreaName());
                return findLocation(point);
            }
        }
        tv.setText(R.string.unknown);
        return false;
    }

    private boolean findLocation(Point point){
        TextView tv = (TextView)mCards.get(0).findViewById(R.id.location_code);
        List<Locations> mLocations = DataBaseHelper.readLocations(this, areaIndex);
        for (Locations locations: mLocations){
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
            if (polygon.contains(point)){
                locationIndex = locations.getLocationId();
                tv.setText(locations.getLocationName());
                DataBaseHelper.readCategory(this, areaIndex, locationIndex);
                return true;
            }
        }
        tv.setText(R.string.unknown);
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCardScroller.activate();
        mLocationUtils.restart();
        handleLocationUtils();
    }

    @Override
    protected void onPause() {
        mCardScroller.deactivate();
        mLocationUtils.stop();
        super.onPause();
    }

    //region Gesture Detector
    private GestureDetector createGestureDetector(final Context context) {
        GestureDetector gestureDetector = new GestureDetector(context);

        //Create a base listener for generic gestures
        gestureDetector.setBaseListener(new GestureDetector.BaseListener() {
            @Override
            public boolean onGesture(Gesture gesture) {
                Log.e(TAG, "gesture = " + gesture);
                AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                switch (gesture) {
                    case TAP:
                        Log.e(TAG, "TAP called.");
                        openCategories();
                        am.playSoundEffect(Sounds.TAP);
                        break;
                    case TWO_LONG_PRESS:
                        boolean ok = handleLocationUtils();
                        if (ok) {
                            am.playSoundEffect(Sounds.SUCCESS);
                        } else {
                            am.playSoundEffect(Sounds.ERROR);
                        }
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

    private void openCategories() {
        Intent intent = new Intent(this, CategoriesActivity.class);
        TextView tv = (TextView) this.findViewById(R.id.location_code);
        String message = (String) tv.getText();
        intent.putExtra(Constants.EXTRA_MESSAGE, message);
        startActivity(intent);
    }

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

    private View createLocationCard() {
        mCards = new ArrayList<>();
        LayoutInflater inflater = LayoutInflater.from(this);
        View card = inflater.inflate(R.layout.location_layout, null);
        mCards.add(card);
        return mCards.get(0);
    }

}