package com.medialabamsterdam.checklistprototype;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

import com.google.android.glass.media.Sounds;
import com.google.android.glass.touchpad.Gesture;
import com.google.android.glass.touchpad.GestureDetector;
import com.google.android.glass.widget.CardScrollView;
import com.medialabamsterdam.checklistprototype.Adapters.SubCategoryCardScrollAdapter;
import com.medialabamsterdam.checklistprototype.ContainerClasses.Category;
import com.medialabamsterdam.checklistprototype.ContainerClasses.SubCategory;
import com.medialabamsterdam.checklistprototype.Database.DataBaseHelper;
import com.medialabamsterdam.checklistprototype.Utilities.Constants;

import java.util.ArrayList;

/**
 * Created by
 * Jose Carlos Quintas Junior
 * juniorquintas@gmail.com
 * on 01/03/2015.
 */
public class SubCategoriesActivity extends Activity {

    private final static String TAG = "SUBCATEGORIES";
    private static final int SET_RATING_DETAIL_CODE = 1652;
    private CardScrollView mCardScroller;
    private GestureDetector mGestureDetector;
    private ArrayList<SubCategory> mSubCategories;
    private SubCategoryCardScrollAdapter mAdapter;
    private Category mCategory;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        //Get data from intent sent from CategoriesActivity.
        Intent intent = getIntent();
        mCategory = intent.getParcelableExtra(Constants.PARCELABLE_CATEGORY);
        int areaCode = intent.getIntExtra(Constants.EXTRA_AREA_CODE, 0);

        //Checks if there is a SubCategory parcelable in the Intent or Bundle and loads it.
        if (intent.hasExtra(Constants.PARCELABLE_SUBCATEGORY)) {
            mSubCategories = intent.getParcelableArrayListExtra(Constants.PARCELABLE_SUBCATEGORY);
        } else if (bundle == null || !bundle.containsKey(Constants.PARCELABLE_SUBCATEGORY)) {
            mSubCategories = new ArrayList<>(DataBaseHelper.readSubCategory(this, mCategory.getId(), mCategory.getCategoryByLocationId(), areaCode));
        } else {
            mSubCategories = bundle.getParcelableArrayList(Constants.PARCELABLE_SUBCATEGORY);
        }

        //Regular CardScroller/Adapter procedure.
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mCardScroller = new CardScrollView(this);
        mAdapter = new SubCategoryCardScrollAdapter(this, mSubCategories, mCategory.getName());
        mCardScroller.setAdapter(mAdapter);
        mCardScroller.setFocusable(false);
        mCardScroller.activate();
        mGestureDetector = createGestureDetector(this);
        setContentView(mCardScroller);
    }

    /**
     * Method used change the SubCategory grade variable.
     *
     * @param isAdd true if increasing the grade, false otherwise.
     */
    private void changeRating(boolean isAdd) {
        int position = mCardScroller.getSelectedItemPosition();
        int grade = mSubCategories.get(position).getGrade();
        // Checks if not on the border cards in order to not let the grade go over the limits.
        if (isAdd && grade <= 3) {
            mSubCategories.get(position).setGrade(grade + 1);
        } else if (!isAdd && grade >= 0) {
            mSubCategories.get(position).setGrade(grade - 1);
        }

        mAdapter.notifyDataSetChanged();
    }

    /**
     * Animates the CardScroller based on user input in order for us to use the TWO_SWIPE gesture
     * to change grades instead of changing cards.
     *
     * @param right true if animating right, false if left.
     */
    private void animateScroll(boolean right) {
        final int pos = mCardScroller.getSelectedItemPosition();
        final long time = 100;
        int size = mSubCategories.size() - 1;
        // Animates the current card to leave view to the right.
        if (right && pos < size) {
            final Animation animOutRight = new TranslateAnimation(0, -640, 0, 0);
            animOutRight.setDuration(time);
            final Animation animInRight = new TranslateAnimation(640, 0, 0, 0);
            animInRight.setDuration(time);

            // Creates an Animation Listener that when onAnimationEnd is called it loads the
            // next card from the right.
            Animation.AnimationListener al = new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    mCardScroller.setSelection(pos + 1);
                    mCardScroller.startAnimation(animInRight);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            };
            animOutRight.setAnimationListener(al);
            mCardScroller.startAnimation(animOutRight);
        // Animates the current card to leave view to the left.
        } else if (!right && pos > 0) {
            Animation animOutLeft = new TranslateAnimation(0, 640, 0, 0);
            animOutLeft.setDuration(time);
            final Animation animInLeft = new TranslateAnimation(-640, 0, 0, 0);
            animInLeft.setDuration(time);

            // Creates an Animation Listener that when onAnimationEnd is called it loads the
            // next card from the right.
            Animation.AnimationListener al = new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    mCardScroller.setSelection(pos - 1);
                    mCardScroller.startAnimation(animInLeft);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            };
            animOutLeft.setAnimationListener(al);
            mCardScroller.startAnimation(animOutLeft);
        }
    }

    /**
     * Starts the DetailsActivity.
     */
    private void startDetails() {
        Intent intent = new Intent(this, DetailsActivity.class);
        int position = mCardScroller.getSelectedItemPosition();
        int grade = mSubCategories.get(position).getGrade();
        int categoryId = mSubCategories.get(position).getParentId();
        int subCategoryId = mSubCategories.get(position).getId();
        intent.putExtra(Constants.EXTRA_POSITION, position);
        intent.putExtra(Constants.EXTRA_GRADE, grade);
        intent.putExtra(Constants.EXTRA_CATEGORY_ID, categoryId);
        intent.putExtra(Constants.EXTRA_SUBCATEGORY_ID, subCategoryId);
        startActivityForResult(intent, SET_RATING_DETAIL_CODE);
    }

    /**
     * Receives result from DetailsActivity in order to save the data changed in that activity.
     *
     * @param requestCode the request code.
     * @param resultCode the result code.
     * @param result the result intent.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent result) {
        if (requestCode == SET_RATING_DETAIL_CODE) {
            if (resultCode == RESULT_OK) {
                int position = result.getIntExtra(Constants.EXTRA_POSITION, 1);
                int rating = result.getIntExtra(Constants.EXTRA_GRADE_DETAIL, 0);
                mSubCategories.get(position).setGrade(rating);
                mCardScroller.setSelection(position);
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    /**
     * Sends result back to CategoryActivity.
     */
    private void sendResult() {
        mCardScroller.deactivate();
        Intent result = new Intent();
        mCategory.setCompleted(true);
        result.putExtra(Constants.PARCELABLE_CATEGORY, mCategory);
        // Removes last entry on mSubCategories that is used to create and display the check mark.
        ArrayList<SubCategory> fixedSubCategories = mSubCategories;
        fixedSubCategories.remove(fixedSubCategories.size() - 1);
        result.putParcelableArrayListExtra(Constants.PARCELABLE_SUBCATEGORY, mSubCategories);
        setResult(Activity.RESULT_OK, result);
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

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putParcelableArrayList(Constants.PARCELABLE_SUBCATEGORY, mSubCategories);
        savedInstanceState.putParcelable(Constants.PARCELABLE_CATEGORY, mCategory);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mCategory = savedInstanceState.getParcelable(Constants.PARCELABLE_CATEGORY);
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
                AudioManager am = (AudioManager) SubCategoriesActivity.this.getSystemService(Context.AUDIO_SERVICE);
                int position = mCardScroller.getSelectedItemPosition();
                int maxPositions = mAdapter.getCount() - 1;
                switch (gesture) {
                    case TAP:
                        Log.e(TAG, "TAP called.");
                        if (position == maxPositions) {
                            sendResult();
                            am.playSoundEffect(Sounds.DISALLOWED);
                            finish();
                        } else {
                            startDetails();
                            am.playSoundEffect(Sounds.TAP);
                        }
                        break;
                    case SWIPE_LEFT:
                        Log.e(TAG, "SWIPE_LEFT called.");
                        animateScroll(false);
                        return true;
                    case SWIPE_RIGHT:
                        Log.e(TAG, "SWIPE_RIGHT called.");
                        animateScroll(true);
                        return true;
                    case SWIPE_DOWN:
                        Log.e(TAG, "SWIPE_DOWN called.");
                        finish();
                        return true;
                    case TWO_SWIPE_LEFT:
                        Log.e(TAG, "TWO_SWIPE_LEFT called.");
                        if (position != maxPositions) {
                            changeRating(false);
                        }
                        return true;
                    case TWO_SWIPE_RIGHT:
                        Log.e(TAG, "TWO_SWIPE_RIGHT called.");
                        if (position != maxPositions) {
                            changeRating(true);
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
}