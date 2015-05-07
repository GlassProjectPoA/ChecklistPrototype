package com.medialabamsterdam.checklistprototype;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;import android.media.AudioManager;
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
import com.medialabamsterdam.checklistprototype.ContainerClasses.SubCategory;
import com.medialabamsterdam.checklistprototype.Utilities.Constants;
import com.medialabamsterdam.checklistprototype.Utilities.Utils;

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
    private ArrayList<SubCategory> mSubCatViews;
    private SubCategoryCardScrollAdapter mAdapter;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        if (bundle == null || !bundle.containsKey(Constants.PARCELABLE_SUBCATEGORY)) {
            createCards();
        } else {
            mSubCatViews = bundle.getParcelableArrayList(Constants.PARCELABLE_SUBCATEGORY);
        }

        mCardScroller = new CardScrollView(this);
        mAdapter = new SubCategoryCardScrollAdapter(this, mSubCatViews);
        mCardScroller.setAdapter(mAdapter);
        mCardScroller.setFocusable(false);
        mCardScroller.activate();
        mGestureDetector = createGestureDetector(this);
        setContentView(mCardScroller);
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
                Log.e(TAG, "gesture = " + gesture);
                int position = mCardScroller.getSelectedItemPosition();
                int maxPositions = mAdapter.getCount() - 1;
                switch (gesture) {
                    case TAP:
                        Log.e(TAG, "TAP called.");
                        if (position == maxPositions) {
                            //do nothing
                        } else {
                            SubCategoriesActivity.this.openRatingDetailed();
                            AudioManager am = (AudioManager) SubCategoriesActivity.this.getSystemService(Context.AUDIO_SERVICE);
                            am.playSoundEffect(Sounds.TAP);
                        }
                        break;
                    case SWIPE_LEFT:
                        Log.e(TAG, "SWIPE_LEFT called.");
                        SubCategoriesActivity.this.animateScroll(false);
                        return true;
                    case SWIPE_RIGHT:
                        Log.e(TAG, "SWIPE_RIGHT called.");
                        SubCategoriesActivity.this.animateScroll(true);
                        return true;
                    case SWIPE_DOWN:
                        Log.e(TAG, "SWIPE_DOWN called.");
                        SubCategoriesActivity.this.finish();
                        return true;
                    case TWO_SWIPE_LEFT:
                        Log.e(TAG, "TWO_SWIPE_LEFT called.");
                        if (position == maxPositions) {
                            //do nothing
                        } else {
                            SubCategoriesActivity.this.changeRating(false);
                        }
                        return true;
                    case TWO_SWIPE_RIGHT:
                        Log.e(TAG, "TWO_SWIPE_RIGHT called.");
                        if (position == maxPositions) {
                            //do nothing
                        } else {
                            SubCategoriesActivity.this.changeRating(true);
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

    private void createCards() {
        mSubCatViews = new ArrayList<>();
        Intent intent = getIntent();
        int position = intent.getIntExtra(Constants.EXTRA_POSITION, 404);
        String[] problems = getResources().getStringArray(Utils.getResourceId(this, "problems_" + position, "array", getPackageName()));
        String[] categories = getResources().getStringArray(R.array.categories_list);
        int index = 0;
        for (String str : problems) {
            SubCategory subCategory = new SubCategory(position, categories[position], index, str, -1);
            index++;
            mSubCatViews.add(subCategory);
        }
    }

    private void changeRating(boolean right) {
        int position = mCardScroller.getSelectedItemPosition();
        int rating = mSubCatViews.get(position).getCurrentRating();
        if (right && rating <= 3) {
            mSubCatViews.get(position).setCurrentRating(rating + 1);
        } else if (!right && rating >= 0) {
            mSubCatViews.get(position).setCurrentRating(rating - 1);
        }

        mAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putParcelableArrayList(Constants.PARCELABLE_SUBCATEGORY, mSubCatViews);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    private void animateScroll(boolean right) {
        final int pos = mCardScroller.getSelectedItemPosition();
        final long time = 100;
        int size = mSubCatViews.size() - 1;
        if (right && pos < size) {
            final Animation animOutRight = new TranslateAnimation(0, -640, 0, 0);
            animOutRight.setDuration(time);
            final Animation animInRight = new TranslateAnimation(640, 0, 0, 0);
            animInRight.setDuration(time);

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
        } else if (!right && pos > 0) {
            Animation animOutLeft = new TranslateAnimation(0, 640, 0, 0);
            animOutLeft.setDuration(time);
            final Animation animInLeft = new TranslateAnimation(-640, 0, 0, 0);
            animInLeft.setDuration(time);

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

    private void openRatingDetailed() {
        Intent intent = new Intent(this, SubCategoriesDetailedActivity.class);
        int position = mCardScroller.getSelectedItemPosition();
        int rating = mSubCatViews.get(position).getCurrentRating();
        intent.putExtra(Constants.EXTRA_POSITION, position);
        intent.putExtra(Constants.EXTRA_RATING, rating);
        startActivityForResult(intent, SET_RATING_DETAIL_CODE);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SET_RATING_DETAIL_CODE) {
            if (resultCode == RESULT_OK) {
                int position = data.getIntExtra(Constants.EXTRA_POSITION, 1);
                int rating = data.getIntExtra(Constants.EXTRA_RATING_DETAIL, 0);
                mSubCatViews.get(position).setCurrentRating(rating);
                mCardScroller.setSelection(position);
                mAdapter.notifyDataSetChanged();
            }
        }
    }

}