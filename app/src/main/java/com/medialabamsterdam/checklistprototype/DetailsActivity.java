package com.medialabamsterdam.checklistprototype;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.WindowManager;

import com.google.android.glass.media.Sounds;
import com.google.android.glass.touchpad.Gesture;
import com.google.android.glass.touchpad.GestureDetector;
import com.google.android.glass.widget.CardScrollView;
import com.medialabamsterdam.checklistprototype.Adapters.DetailCardScrollAdapter;
import com.medialabamsterdam.checklistprototype.Adapters.DetailExtendedCardScrollAdapter;
import com.medialabamsterdam.checklistprototype.ContainerClasses.Detail;
import com.medialabamsterdam.checklistprototype.Database.DataBaseHelper;
import com.medialabamsterdam.checklistprototype.Utilities.Constants;

/**
 * Created by
 * Jose Carlos Quintas Junior
 * juniorquintas@gmail.com
 * on 01/03/2015.
 */
public class DetailsActivity extends Activity {

    private final static String TAG = "DETAILS";
    private CardScrollView mCardScroller;
    private GestureDetector mGestureDetector;
    private DetailCardScrollAdapter mDetailedAdapter;
    private DetailExtendedCardScrollAdapter mDetailedExtendedAdapter;
    private int mSubCategoryPosition;
    private Detail mDetail;
    private boolean extended = false;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        //Get data from intent sent from SubCategoriesActivity.
        Intent intent = getIntent();
        mSubCategoryPosition = intent.getIntExtra(Constants.EXTRA_POSITION, 0);
        int categoryId = intent.getIntExtra(Constants.EXTRA_CATEGORY_ID, 0);
        int subCategoryId = intent.getIntExtra(Constants.EXTRA_SUBCATEGORY_ID, 0);

        //Loads details from Database.
        mDetail = DataBaseHelper.readDetails(this, categoryId, subCategoryId);

        //Regular CardScroller/Adapter procedure.
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mCardScroller = new CardScrollView(this);
        mDetailedAdapter = new DetailCardScrollAdapter(this, mDetail);
        mDetailedExtendedAdapter = new DetailExtendedCardScrollAdapter(this, mDetail);
        mCardScroller.setAdapter(mDetailedAdapter);
        mCardScroller.activate();
        mGestureDetector = createGestureDetector(this);
        setContentView(mCardScroller);
        mCardScroller.setSelection(intent.getIntExtra(Constants.EXTRA_GRADE, 0));
    }

    /**
     * Sends Activity Result back to SubCategoryActivity
     * Is called when user TAP.
     */
    private void sendResult() {
        Intent result = new Intent();
        result.putExtra(Constants.EXTRA_GRADE_DETAIL, mCardScroller.getSelectedItemPosition());
        result.putExtra(Constants.EXTRA_POSITION, mSubCategoryPosition);
        DetailsActivity.this.setResult(Activity.RESULT_OK, result);
        DetailsActivity.this.finish();
    }

    /**
     * Changes the adapter so it displays two kinds of data sets (needed to show all the details).
     * Is called when user SWIPE_UP.
     */
    private void changeAdapter() {
        mCardScroller.deactivate();
        if (!extended) {
            mCardScroller.setAdapter(mDetailedExtendedAdapter);
            extended = true;
        } else {
            mCardScroller.setAdapter(mDetailedAdapter);
            extended = false;
        }
        mCardScroller.activate();
    }

    //region Boring Stuff
    @Override
    protected void onResume() {
        super.onResume();
        mCardScroller.activate();
    }

    @Override
    protected void onPause() {
        mCardScroller.deactivate();
        super.onPause();
    }
    //endregion

    //region Gesture Detector
    private GestureDetector createGestureDetector(final Context context) {
        GestureDetector gestureDetector = new GestureDetector(context);

        //Create a base listener for generic gestures
        gestureDetector.setBaseListener(new GestureDetector.BaseListener() {
            @Override
            public boolean onGesture(Gesture gesture) {
                AudioManager am = (AudioManager) DetailsActivity.this.getSystemService(Context.AUDIO_SERVICE);
                switch (gesture) {
                    case TAP:
                        Log.e(TAG, "TAP called.");
                        sendResult();
                        am.playSoundEffect(Sounds.DISALLOWED);
                        break;
                    case SWIPE_UP:
                        Log.e(TAG, "SWIPE_UP called.");
                        changeAdapter();
                        break;
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
}