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
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.medialabamsterdam.checklistprototype.Adapters.SubCategoryCardScrollAdapter;
import com.medialabamsterdam.checklistprototype.ContainerClasses.Category;
import com.medialabamsterdam.checklistprototype.ContainerClasses.SubCategory;
import com.medialabamsterdam.checklistprototype.Database.DataBaseHelper;
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

        //Get data from intent sent from CategoriesActivity.
        Intent intent = getIntent();
        mCategory = intent.getParcelableExtra(Constants.PARCELABLE_CATEGORY);
        locationIndex = intent.getIntExtra(Constants.EXTRA_LOCATION, 0);
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
        getGrades(false);
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
                            if (_grades != null) {
                                checkData();
                            } else {
                                getGrades(true);
                            }
                            am.playSoundEffect(Sounds.DISALLOWED);
                        } else {
                            startDetails();
                            am.playSoundEffect(Sounds.TAP);
                        }
                        return true;
                    case SWIPE_LEFT: // Display next subcategory
                        Log.e(TAG, "SWIPE_LEFT called.");
                        animateScroll(false);
                        return true;
                    case SWIPE_RIGHT: // Display previous subcategory
                        Log.e(TAG, "SWIPE_RIGHT called.");
                        animateScroll(true);
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

    @Override
    public boolean onGenericMotionEvent(MotionEvent event) {
        return mGestureDetector != null && mGestureDetector.onMotionEvent(event);
    }
    //</editor-fold>

    /**
     * This method grabs grades from the server in order to compare them with the grades from user
     * input.
     * <p>
     * This uses Json and the Ion library.
     * https://github.com/koush/ion
     */
    private void getGrades(final boolean sendResult) {
        Ion.with(this)
                .load(Constants.WEB_SERVICE_URL + "subcategories/grades/" + locationIndex)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        if (e != null) e.printStackTrace();
                        if (result != null) {
                            // Saves all acceptable grades from given location in _grades.
                            if (result.has("grades")) {
                                Log.e(TAG, result.toString());
                                JsonArray jsonArray = result.getAsJsonArray("grades");
                                _grades = new SparseIntArray();
                                for (int i = 0; i < jsonArray.size(); i++) {
                                    int code = jsonArray.get(i).getAsJsonObject().get("code").getAsInt();
                                    int grade = Utils.getRatingFromString(jsonArray.get(i)
                                            .getAsJsonObject().get("accepted_grade").getAsString());
                                    _grades.put(code, grade);
                                }
                                // Calls the checkData() method to determine if any grade needs a picture.
                                if (sendResult) {
                                    checkData();
                                }
                            } else {
                                if (sendResult) {
                                    if(locationIndex == 1289){
                                        checkData();
                                    }
                                }
                                //statusUpdate(FAIL_CONNECT);
                                //TODO FIX MESSAGE
                            }
                        } else {
                            if (sendResult) {
                                if(locationIndex == 1289){
                                    checkData();
                                }
                            }
                            //statusUpdate(FAIL_CONNECT);
                            // TODO FIX MESSAGE
                        }
                    }
                });
    }

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
}