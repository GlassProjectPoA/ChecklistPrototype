package com.medialabamsterdam.checklistprototype;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
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
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.medialabamsterdam.checklistprototype.Adapters.CategoryCardScrollAdapter;
import com.medialabamsterdam.checklistprototype.ContainerClasses.Category;
import com.medialabamsterdam.checklistprototype.ContainerClasses.SubCategory;
import com.medialabamsterdam.checklistprototype.Utilities.Constants;
import com.medialabamsterdam.checklistprototype.Utilities.Utils;

import org.apache.http.HttpResponse;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
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

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        Intent i = getIntent();
        mCategories = i.getParcelableArrayListExtra(Constants.EXTRA_CATEGORY);
        mSubCategories = i.getParcelableArrayListExtra(Constants.EXTRA_SUBCATEGORY);
        locationIndex = i.getIntExtra(Constants.EXTRA_LOCATION, 0);
        areaCode = i.getIntExtra(Constants.EXTRA_AREA_CODE, 0);

        mCardScroller = new CardScrollView(this);
        mAdapter = new CategoryCardScrollAdapter(this, mCategories);
        mCardScroller.setAdapter(mAdapter);
        mCardScroller.activate();
        mCardScroller.setHorizontalScrollBarEnabled(false);
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
                int position = mCardScroller.getSelectedItemPosition();
                int maxPositions = mAdapter.getCount() - 1;
                int completion = 0;
                for (Category category : mCategories) {
                    if (category.isCompleted()) completion++;
                }
                AudioManager am = (AudioManager) CategoriesActivity.this.getSystemService(Context.AUDIO_SERVICE);
                switch (gesture) {
                    case TAP:
                        Log.e(TAG, "TAP called.");
                        if(isSent){
                            finish();
                        }else {
                            if (position == maxPositions) {
                                if (completion == mCategories.size() - 1) {
                                    CategoriesActivity.this.checkData();
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
                            } else {
                                if (Constants.IGNORE_INSTRUCTIONS) {
                                    CategoriesActivity.this.openRating();
                                } else {
                                    CategoriesActivity.this.openInstructions();
                                }
                                am.playSoundEffect(Sounds.TAP);
                            }
                        }
                        break;
                    case SWIPE_DOWN:
                        Log.e(TAG, "SWIPE_DOWN called.");
                        Intent result = new Intent();
                        result.putParcelableArrayListExtra(Constants.EXTRA_SUBCATEGORY, mSubCategories);
                        result.putParcelableArrayListExtra(Constants.EXTRA_CATEGORY, mCategories);
                        result.putExtra(Constants.EXTRA_LOCATION, locationIndex);
                        setResult(Activity.RESULT_OK, result);
                        finish();
                        break;
                }
                return false;
            }
        });
        return gestureDetector;
    }

    private void checkData() {
        int count = 0;
        HttpResponse response;
        CheckDataLoop: for (SubCategory sc : mSubCategories){
            if (sc.getPictureUri() == null && sc.getRating() > 1){
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

    @Override
    public boolean onGenericMotionEvent(MotionEvent event) {
        return mGestureDetector != null && mGestureDetector.onMotionEvent(event);
    }
    //endregion

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
                        mSubCategories.get(i).setRating(sc.get(j).getRating());
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
//        for (SubCategory subc : mSubCategories) {
//            Log.d(TAG, subc.toString());
//        }
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
            object.addProperty("rating", Utils.getStringFromRating(sc.getRating()));
            jsonArray.add(object);
        }

        JsonObject json = new JsonObject();
        json.addProperty("user_id", 1);
        json.addProperty("location_id", locationIndex);
        json.add("data", jsonArray);

        Log.e(TAG, json.toString());

        Ion.with(this)
                .load("http://glass.twisk-interactive.nl/checklist")
                .setJsonObjectBody(json)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        e.printStackTrace();
                        if (result != null) {
                            Log.e(TAG, result.toString());
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
        ProgressBar spinner = (ProgressBar) mCardScroller.getSelectedView().findViewById(R.id.pictureProcessBar);
        spinner.setVisibility(View.VISIBLE);
        spinner.setIndeterminateDrawable(getResources().getDrawable(R.drawable.progress_bar_green));
        mCardScroller.getSelectedView().findViewById(R.id.left_arrow).setVisibility(View.INVISIBLE);
    }


    private void openInstructions() {
        Intent intent = new Intent(this, InstructionsActivity.class);
        int position = mCardScroller.getSelectedItemPosition();
        intent.putExtra(Constants.EXTRA_POSITION, position);
        startActivity(intent);
    }

    private void openRating() {
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
