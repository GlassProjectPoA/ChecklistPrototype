package com.medialabamsterdam.checklistprototype;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

import com.google.android.glass.media.Sounds;
import com.google.android.glass.touchpad.Gesture;
import com.google.android.glass.touchpad.GestureDetector;
import com.google.android.glass.widget.CardScrollView;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.Future;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.medialabamsterdam.checklistprototype.Adapters.SubCategoryCardScrollAdapter;
import com.medialabamsterdam.checklistprototype.ContainerClasses.Category;
import com.medialabamsterdam.checklistprototype.ContainerClasses.SubCategory;
import com.medialabamsterdam.checklistprototype.Utilities.Constants;
import com.medialabamsterdam.checklistprototype.Utilities.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by
 * Jose Carlos Quintas Junior
 * juniorquintas@gmail.com
 * on 01/03/2015.
 */
public class SubCategoriesActivity extends Activity {

    private final static String TAG = "SUBCATEGORIES";
    private static final int RATING_DETAIL_REQUEST = 1652;
    private final static int WARNING_REQUEST = 9575;

    private CardScrollView mCardScroller;
    private GestureDetector mGestureDetector;
    private ArrayList<SubCategory> mSubCategories;
    private SubCategoryCardScrollAdapter mAdapter;
    private Category mCategory;
    private SparseIntArray _grades;
    private int locationIndex;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        mSubCategories = new ArrayList<>();

        List<SubCategory> scl = new ArrayList<>();
        scl.add(new SubCategory(1));
        scl.add(new SubCategory(2));
        scl.add(new SubCategory(3));
        scl.add(new SubCategory(4));
        scl.add(new SubCategory(5));

        mSubCategories.addAll(scl);

        //Regular CardScroller/Adapter procedure.
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mCardScroller = new CardScrollView(this);
        mAdapter = new SubCategoryCardScrollAdapter(this, mSubCategories, "yolo");
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
        startActivityForResult(intent, RATING_DETAIL_REQUEST);
    }

    /**
     * Receives result from DetailsActivity in order to save the data changed in that activity.
     *
     * @param requestCode the request code.
     * @param resultCode  the result code.
     * @param data        the result intent.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == WARNING_REQUEST && resultCode == RESULT_OK) {
            savePicture(data);
        }
        if (requestCode == RATING_DETAIL_REQUEST && resultCode == RESULT_OK) {
            saveDetailData(data);
        }
    }

    /**
     * Saves the picture path from the received intent.
     *
     * @param picturePathData Intent received from WarningActivity.
     */
    private void savePicture(Intent picturePathData) {
        int categoryId = picturePathData.getIntExtra(Constants.EXTRA_CATEGORY_ID, 0);
        int subCategoryId = picturePathData.getIntExtra(Constants.EXTRA_SUBCATEGORY_ID, 0);
        for (SubCategory sc : mSubCategories) {
            if (categoryId == sc.getParentId() && subCategoryId == sc.getId()) {
                sc.setPictureUri(picturePathData.getStringExtra(Constants.EXTRA_PICTURE));
                break;
            }
        }
        // Calls checkData() to see if any other SubCategory need a picture.
        checkData();
    }

    /**
     * Saves the grade from the received intent.
     *
     * @param detailData Intent received from DetailsActivity.
     */
    private void saveDetailData(Intent detailData) {
        int position = detailData.getIntExtra(Constants.EXTRA_POSITION, 1);
        int rating = detailData.getIntExtra(Constants.EXTRA_GRADE_DETAIL, 0);
        mSubCategories.get(position).setGrade(rating);
        mCardScroller.setSelection(position);
        mAdapter.notifyDataSetChanged();
    }

    /**
     * Sends result back to CategoryActivity.
     */
    private void sendResult() {
        mCardScroller.deactivate();
        Intent result = new Intent();
        mCategory.setComplete(true);
        result.putExtra(Constants.PARCELABLE_CATEGORY, mCategory);
        // Removes last entry on mSubCategories that is used to create and display the check mark.
        ArrayList<SubCategory> fixedSubCategories = mSubCategories;
        fixedSubCategories.remove(fixedSubCategories.size() - 1);
        result.putParcelableArrayListExtra(Constants.PARCELABLE_SUBCATEGORY, mSubCategories);
        setResult(Activity.RESULT_OK, result);
        finish();
    }

    //<editor-fold desc="onResume / onPause">
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
    protected void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        savedInstanceState.putParcelableArrayList(Constants.PARCELABLE_SUBCATEGORY, mSubCategories);
        savedInstanceState.putParcelable(Constants.PARCELABLE_CATEGORY, mCategory);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mCategory = savedInstanceState.getParcelable(Constants.PARCELABLE_CATEGORY);
        mSubCategories = savedInstanceState.getParcelableArrayList(Constants.PARCELABLE_SUBCATEGORY);
    }
    //</editor-fold>

    //<editor-fold desc="Gesture Detector">
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
                        Log.e(TAG, "TAP called."); // If user in last card, saves data.
                        // Start DetailsActivity otherwise.
                        if (position == maxPositions) {
                            goToStart();
                            am.playSoundEffect(Sounds.DISALLOWED);
                        } else {
                            prepareJson();
                            animateScroll(true);
                        }
                        return true;
                    case SWIPE_DOWN: // Finish activity and send data back to parent
                        Log.e(TAG, "SWIPE_DOWN called.");
                        finish();
                        return true;
                    case TWO_SWIPE_LEFT: // Changes grade of current SubCategory
                        Log.e(TAG, "TWO_SWIPE_LEFT called.");
                        if (position != maxPositions) {
                            changeRating(false);
                        }
                        return true;
                    case TWO_SWIPE_RIGHT: // Changes grade of current SubCategory
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

    private void goToStart() {
        mCardScroller.deactivate();
        Intent result = new Intent();
        setResult(Activity.RESULT_OK, result);
        finish();
    }

    @Override
    public boolean onGenericMotionEvent(MotionEvent event) {
        return mGestureDetector != null && mGestureDetector.onMotionEvent(event);
    }
    //</editor-fold>

    /**
     * This method checks if any SubCategory has a grade below the accepted. And calls
     * WarningActivity if they do.
     */
    private void checkData() {
        int count = 0;
        if (_grades == null) {
            _grades = new SparseIntArray();
            _grades.put(1, 1);
            Log.e(TAG, "Loaded default ratings");
        }
        for (SubCategory sc : mSubCategories) {
            // If any SubCategory has a grade below accepted AND has no picture URI related to it
            // the code will call WarningActivity to prompt the user to take a picture.
            if (sc.getName() == null) {
                count++;
            } else if (sc.getPictureUri() == null && sc.getGrade() > _grades.get(sc.getCode(), 1)) {
                Intent intent = new Intent(this, WarningActivity.class);
                intent.putExtra(Constants.EXTRA_SUBCATEGORY_NAME, sc.getName());
                intent.putExtra(Constants.EXTRA_CATEGORY_ID, sc.getParentId());
                intent.putExtra(Constants.EXTRA_SUBCATEGORY_ID, sc.getId());
                startActivityForResult(intent, WARNING_REQUEST);
                break;
            } else {
                // If no SubCategory is below accepted grade, the code will call sendData() to send
                // the checklist to the server.
                count++;
            }
        }
        if (mSubCategories.size() == count) {
            sendResult();
        }
    }

    /**
     * This method prepares all the data from the checklist to send to the server.
     */
    private void prepareJson() {
        // Disables scrolling and tapping on the device, so we don't send data twice.
        int position = mCardScroller.getSelectedItemPosition();
        int grade = mSubCategories.get(position).getGrade();
        JsonObject json = new JsonObject();

        json = new JsonObject();
        json.addProperty("id", position);
        json.addProperty("grade", Utils.getStringFromRating(grade));

        Log.v(TAG, json.toString());
        sendData(json);
    }

    /**
     * This method sends a JsonObject to the server. It also notifies the UI of the results.
     * <p>
     * This uses Json and the Ion library.
     * https://github.com/koush/ion
     *
     * @param json object to be sent
     */
    private void sendData(JsonObject json) {
        // Send the data to the server.
        Future<JsonObject> jsonObjectFuture = Ion.with(this)
                .load(Constants.WEB_SERVICE_URL + "checklist")
                .setJsonObjectBody(json)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        if (e != null) e.printStackTrace();
                        if (result != null) {
                            if (result.get("status").getAsString().equals("OK")) {
                                Log.e(TAG, result.toString());
                            }
                        }
                    }
                });
    }
}