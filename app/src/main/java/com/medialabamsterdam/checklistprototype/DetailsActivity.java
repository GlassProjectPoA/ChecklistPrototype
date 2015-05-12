package com.medialabamsterdam.checklistprototype;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.WindowManager;

import com.google.android.glass.media.Sounds;
import com.google.android.glass.touchpad.Gesture;
import com.google.android.glass.touchpad.GestureDetector;
import com.google.android.glass.widget.CardScrollView;
import com.medialabamsterdam.checklistprototype.Adapters.DetailedCardScrollAdapter;
import com.medialabamsterdam.checklistprototype.Adapters.DetailedExtendedCardScrollAdapter;
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

    private final static String TAG = "DETAILED";
    private CardScrollView mCardScroller;
    private GestureDetector mGestureDetector;
    private DetailedCardScrollAdapter mDetailedAdapter;
    private DetailedExtendedCardScrollAdapter mDetailedExtendedAdapter;
    private int mSubCategory;
    private Detail mDetail;
    private boolean extended = false;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        Intent intent = getIntent();
        mSubCategory = intent.getIntExtra(Constants.EXTRA_POSITION, 0);

        mDetail = DataBaseHelper.readDetails(this, intent.getIntExtra(Constants.EXTRA_CATEGORY_ID, 0), intent.getIntExtra(Constants.EXTRA_SUBCATEGORY_ID, 0));

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mCardScroller = new CardScrollView(this);
        mDetailedAdapter = new DetailedCardScrollAdapter(this, mDetail);
        mDetailedExtendedAdapter = new DetailedExtendedCardScrollAdapter(this, mDetail);

        mCardScroller.setAdapter(mDetailedAdapter);
        mCardScroller.activate();
        mGestureDetector = createGestureDetector(this);
        setContentView(mCardScroller);
        mCardScroller.setSelection(intent.getIntExtra(Constants.EXTRA_RATING, 0));
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
                        // Create intent to deliver some kind of result data
                        Intent result = new Intent();
                        result.putExtra(Constants.EXTRA_RATING_DETAIL, mCardScroller.getSelectedItemPosition());
                        result.putExtra(Constants.EXTRA_POSITION, mSubCategory);
                        DetailsActivity.this.setResult(Activity.RESULT_OK, result);
                        am.playSoundEffect(Sounds.DISALLOWED);
                        DetailsActivity.this.finish();
                        break;
                    case SWIPE_UP:
                        mCardScroller.deactivate();
                        if (!extended) {
                            mCardScroller.setAdapter(mDetailedExtendedAdapter);
                            extended = true;
                        } else {
                            mCardScroller.setAdapter(mDetailedAdapter);
                            extended = false;
                        }
                        mCardScroller.activate();
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