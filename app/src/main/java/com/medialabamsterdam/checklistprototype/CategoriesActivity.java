package com.medialabamsterdam.checklistprototype;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.glass.media.Sounds;
import com.google.android.glass.touchpad.Gesture;
import com.google.android.glass.touchpad.GestureDetector;
import com.google.android.glass.widget.CardScrollView;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.Future;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.medialabamsterdam.checklistprototype.Adapters.CategoryCardScrollAdapter;
import com.medialabamsterdam.checklistprototype.ContainerClasses.Category;
import com.medialabamsterdam.checklistprototype.ContainerClasses.SubCategory;
import com.medialabamsterdam.checklistprototype.Utilities.Constants;
import com.medialabamsterdam.checklistprototype.Utilities.Utils;

import java.util.ArrayList;

/**
 * Created by
 * Jose Carlos Quintas Junior
 * juniorquintas@gmail.com
 * on 01/04/2015.
 */
public class CategoriesActivity extends Activity {

    private final static String TAG = "CATEGORIES";
    private static final int SUBCATEGORY_RATING_REQUEST = 5046;
    private final static int WARNING_REQUEST = 9574;

    private CardScrollView mCardScroller;
    private GestureDetector mGestureDetector;
    private ArrayList<Category> mCategories;
    private ArrayList<SubCategory> mSubCategories;
    private CategoryCardScrollAdapter mAdapter;
    private int locationIndex;
    private int areaCode;
    private boolean isSent = false;
    private SparseIntArray _grades;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        //Get data from intent sent from MainActivity.
        Intent i = getIntent();
        mCategories = i.getParcelableArrayListExtra(Constants.EXTRA_CATEGORY);
        mSubCategories = i.getParcelableArrayListExtra(Constants.EXTRA_SUBCATEGORY);
        locationIndex = i.getIntExtra(Constants.EXTRA_LOCATION, 0);
        areaCode = i.getIntExtra(Constants.EXTRA_AREA_CODE, 0);

        //Regular CardScroller/Adapter procedure.
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mCardScroller = new CardScrollView(this);
        mAdapter = new CategoryCardScrollAdapter(this, mCategories);
        mCardScroller.setAdapter(mAdapter);
        mCardScroller.activate();
        mCardScroller.setHorizontalScrollBarEnabled(false);
        mGestureDetector = createGestureDetector(this);
        setContentView(mCardScroller);
    }

    //region onPause/Resume and onInstance
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
                int position = mCardScroller.getSelectedItemPosition();
                int maxPositions = mAdapter.getCount() - 1;
                int completion = 0;
                for (Category category : mCategories) {
                    if (category.isCompleted()) completion++;
                }
                AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                switch (gesture) {
                    case TAP:
                        Log.e(TAG, "TAP called.");
                        // Checks if data was sent to the server already, in which case it will
                        // finish the Activity
                        if (isSent) {
                            finish();
                        } else {
                            // Checks if the CardScroller was at the last position when the user
                            // tapped and if the user TAP it will either send the data to the
                            // server or send the user to one of the Categories he didn't grade.
                            if (position == maxPositions) {
                                if (completion == mCategories.size() - 1) {
                                    // If _grades is not null then getGrades() has been called before
                                    // so there is no need to call it again.
                                    if (_grades != null) {
                                        checkData();
                                    } else {
                                        getGrades();
                                    }
                                } else {
                                    int i = 0;
                                    for (Category category : mCategories) {
                                        if (!category.isCompleted()) {
                                            mCardScroller.setSelection(i);
                                            break;
                                        }
                                        i++;
                                    }
                                }
                            // If CardScroller is not at the last position it will either start
                            // SubCategoriesActiviry or InstructionsActivity based on Settings.
                            } else {
                                if (Constants.IGNORE_INSTRUCTIONS) {
                                    startSubCategories();
                                } else {
                                    startInstructions();
                                }
                                am.playSoundEffect(Sounds.TAP);
                            }
                        }
                        break;
                    case SWIPE_DOWN:
                        sendResult();
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

    /**
     * This method grabs grades from the server in order to compare them with the grades from user
     * input and prompt the user to take a picture in case they are lower than what they should be.
     *
     * This uses Json and the Ion library.
     * https://github.com/koush/ion
     */
    private void getGrades(){
        Ion.with(this)
                //TODO change the 866 at the end to match locationId
                .load("http://glass.twisk-interactive.nl/subcategories/grades/866")
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        if (e != null) e.printStackTrace();
                        if (result != null) {
                            // Saves all acceptable grades from given location in _grades.
                            // Use the SubCategory.getCode() in order to extract the according
                            // acceptable grade from _grades.
                            Log.e(TAG, result.toString());
                            JsonArray jsonArray = result.getAsJsonArray("grades");
                            _grades = new SparseIntArray();
                            for (int i = 0; i < jsonArray.size() ; i++) {
                                int code = jsonArray.get(i).getAsJsonObject().get("code").getAsInt();
                                int grade = Utils.getRatingFromString(jsonArray.get(i)
                                        .getAsJsonObject().get("accepted_grade").getAsString());
                                _grades.put(code, grade);
                            }
                            // Calls the checkData() method to determine if any grade needs a picture.
                            checkData();
                        }
                    }
                });

    }

    private void sendResult() {
        Log.e(TAG, "SWIPE_DOWN called.");
        Intent result = new Intent();
        result.putParcelableArrayListExtra(Constants.EXTRA_SUBCATEGORY, mSubCategories);
        result.putParcelableArrayListExtra(Constants.EXTRA_CATEGORY, mCategories);
        result.putExtra(Constants.EXTRA_LOCATION, locationIndex);
        setResult(Activity.RESULT_OK, result);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == WARNING_REQUEST && resultCode == RESULT_OK) {
            savePicture(data);
        }
        if (requestCode == SUBCATEGORY_RATING_REQUEST && resultCode == RESULT_OK) {
            saveData(data);
        }
    }

    private void savePicture(Intent data) {
        int categoryId = data.getIntExtra(Constants.EXTRA_CATEGORY_ID, 0);
        int subCategoryId = data.getIntExtra(Constants.EXTRA_SUBCATEGORY_ID, 0);
        for (SubCategory sc : mSubCategories){
            if (categoryId == sc.getParentId() && subCategoryId == sc.getId()){
                sc.setPictureUri(data.getStringExtra(Constants.EXTRA_PICTURE));
                break;
            }
        }
        checkData();
    }


    private void checkData() {
        int count = 0;
        CheckDataLoop: for (SubCategory sc : mSubCategories){
            if (sc.getPictureUri() == null && sc.getGrade() > _grades.get(sc.getCode())){
                Intent intent = new Intent(this, WarningActivity.class);
                for (Category c : mCategories){
                    if (c.getId() == sc.getParentId()){
                        intent.putExtra(Constants.EXTRA_CATEGORY_NAME, c.getName());
                        intent.putExtra(Constants.EXTRA_SUBCATEGORY_NAME, sc.getName());
                        intent.putExtra(Constants.EXTRA_CATEGORY_ID, c.getId());
                        intent.putExtra(Constants.EXTRA_SUBCATEGORY_ID, sc.getId());
                        startActivityForResult(intent, WARNING_REQUEST);
                        break CheckDataLoop;
                    }
                }
            } else {
                count++;
                if (mSubCategories.size() == count){
                    sendData();
                }
            }
        }
    }

    private void saveData(Intent data){
        ArrayList<SubCategory> sc = data.getParcelableArrayListExtra(Constants.PARCELABLE_SUBCATEGORY);
        sc.remove(sc.size() - 1);
        Category c = data.getParcelableExtra(Constants.PARCELABLE_CATEGORY);
        for (int i = 0; i < mCategories.size(); i++) {
            if (mCategories.get(i).getId() == c.getId()) {
                mCategories.get(i).setCompleted(c.isCompleted());
            }
        }

        if (mSubCategories != null) {
            boolean hasInstance = false;
            for (int i = 0; i < mSubCategories.size(); i++) {
                for (int j = 0; j < sc.size(); j++) {
                    if (mSubCategories.get(i).getParentId() == sc.get(j).getParentId() &&
                            mSubCategories.get(i).getId() == sc.get(j).getId()) {
                        mSubCategories.get(i).setGrade(sc.get(j).getGrade());
                        hasInstance = true;
                    }
                }
            }
            if (!hasInstance) {
                for (SubCategory subCategory : sc) {
                    mSubCategories.add(subCategory);
                }
            }
        } else {
            mSubCategories = sc;
        }
        mAdapter.notifyDataSetChanged();
    }

    private void sendData() {
        startLoader();
        mCardScroller.setFocusable(false);
        mGestureDetector = null;

        JsonArray jsonArray = new JsonArray();
        for (SubCategory sc : mSubCategories) {
            JsonObject object = new JsonObject();
            object.addProperty("code", sc.getCode());
            object.addProperty("rating", Utils.getStringFromRating(sc.getGrade()));
            jsonArray.add(object);
        }

        JsonObject json = new JsonObject();
        json.addProperty("user_id", 1);
        //TODO change location_id value to locationIndex
        json.addProperty("location_id", 866);
        json.add("data", jsonArray);

        Log.e(TAG, json.toString());

        Future<JsonObject> jsonObjectFuture = Ion.with(this)
                .load("http://glass.twisk-interactive.nl/checklist")
                .setJsonObjectBody(json)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        if (e != null) e.printStackTrace();
                        if (result != null){
                            Log.e(TAG, result.toString());
                            statusComplete();
                        }
                    }
                });
    }

    private void startLoader() {
        TextView tv = (TextView) mCardScroller.getSelectedView().findViewById(R.id.title);
        tv.setText(R.string.upload_list);
        tv = (TextView) mCardScroller.getSelectedView().findViewById(R.id.footer);
        tv.setText(R.string.please_wait);
        mCardScroller.getSelectedView().findViewById(R.id.check).setVisibility(View.GONE);
        ProgressBar spinner = (ProgressBar) mCardScroller.getSelectedView()
                .findViewById(R.id.pictureProcessBar);
        spinner.setVisibility(View.VISIBLE);
        spinner.setIndeterminateDrawable(getResources().getDrawable(R.drawable.progress_bar_green));
        mCardScroller.getSelectedView().findViewById(R.id.left_arrow).setVisibility(View.INVISIBLE);
    }

    private void statusComplete() {
        TextView tv = (TextView) mCardScroller.getSelectedView().findViewById(R.id.title);
        tv.setText(R.string.complete);
        tv = (TextView) mCardScroller.getSelectedView().findViewById(R.id.footer);
        tv.setText(R.string.send_complete);
        mCardScroller.getSelectedView().findViewById(R.id.pictureProcessBar).setVisibility(View.GONE);
        ImageView iv = (ImageView)mCardScroller.getSelectedView().findViewById(R.id.check);
        iv.setVisibility(View.VISIBLE);
        iv.setImageResource(R.drawable.check);
        iv.setColorFilter(getResources().getColor(R.color.green));
        mGestureDetector = createGestureDetector(this);
        isSent = true;
    }


    private void startInstructions() {
        Intent intent = new Intent(this, InstructionsActivity.class);
        int position = mCardScroller.getSelectedItemPosition();
        intent.putExtra(Constants.EXTRA_POSITION, position);
        startActivity(intent);
    }

    private void startSubCategories() {
        Intent intent = new Intent(this, SubCategoriesActivity.class);
        int position = mCardScroller.getSelectedItemPosition();
        ArrayList<SubCategory> subCategories = new ArrayList<>();
        if (mSubCategories != null) {
            for (SubCategory sc : mSubCategories) {
                if (sc.getParentId() == mCategories.get(position).getId()) {
                    subCategories.add(sc);
                }
            }
            if (subCategories.size() != 0) {
                intent.putParcelableArrayListExtra(Constants.PARCELABLE_SUBCATEGORY, subCategories);
            }
        }
        intent.putExtra(Constants.PARCELABLE_CATEGORY, mCategories.get(position));
        intent.putExtra(Constants.EXTRA_POSITION, position);
        intent.putExtra(Constants.EXTRA_AREA_CODE, areaCode);
        startActivityForResult(intent, SUBCATEGORY_RATING_REQUEST);
    }
}
