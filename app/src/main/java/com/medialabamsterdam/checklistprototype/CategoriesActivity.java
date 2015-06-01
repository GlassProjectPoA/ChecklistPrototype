package com.medialabamsterdam.checklistprototype;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.FileObserver;
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

import java.io.File;
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

    private final static int STATUS_COMPLETE = 0;
    private final static int STATUS_INCOMPLETE = 1;
    private final static int STATUS_COULDNOTCONNECT = 2;
    private static final int STATUS_LOAD = 3;
    private static final int STATUS_SAVINGPICTURE = 4;
    private static final int STATUS_CANSEND = 5;

    private CardScrollView mCardScroller;
    private GestureDetector mGestureDetector;
    private ArrayList<Category> mCategories;
    private ArrayList<SubCategory> mSubCategories;
    private CategoryCardScrollAdapter mAdapter;
    private int locationIndex;
    private int areaCode;
    private boolean isSent = false;
    private SparseIntArray _grades;
    private volatile boolean picturesReady = false;

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
                                    // If _grades is not null then getGrades() has been called
                                    // before so there is no need to call it again.
                                    if (picturesReady) {
                                        if (_grades != null) {
                                            checkData();
                                        } else {
                                            getGrades();
                                        }
                                    } else {
                                        statusUpdate(STATUS_SAVINGPICTURE);
                                        am.playSoundEffect(Sounds.DISALLOWED);
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
                        Log.e(TAG, "SWIPE_DOWN called.");
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

    //region onActivityResult
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == WARNING_REQUEST && resultCode == RESULT_OK) {
            savePicture(data);
        }
        if (requestCode == SUBCATEGORY_RATING_REQUEST && resultCode == RESULT_OK) {
            saveSubcategoryData(data);
        }
    }
    //endregion

    /**
     * This method grabs grades from the server in order to compare them with the grades from user
     * input.
     * <p/>
     * This uses Json and the Ion library.
     * https://github.com/koush/ion
     */
    private void getGrades() {
        Ion.with(this)
                .load("http://glass.twisk-interactive.nl/subcategories/grades/" + locationIndex)
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
                                checkData();
                            } else {
                                statusUpdate(STATUS_COULDNOTCONNECT);
                                //TODO REMOVE
                                checkData();
                            }
                        } else {
                            statusUpdate(STATUS_COULDNOTCONNECT);
                            checkData();
                        }
                    }
                });
    }

    /**
     * Sends result back to MainActivity.
     */
    private void sendResult() {
        mCardScroller.deactivate();
        Intent result = new Intent();
        result.putParcelableArrayListExtra(Constants.EXTRA_SUBCATEGORY, mSubCategories);
        // Removes last entry on mCategories that is used to create and display the check mark.
        ArrayList<Category> fixedCategories = mCategories;
        fixedCategories.remove(fixedCategories.size() - 1);
        result.putParcelableArrayListExtra(Constants.EXTRA_CATEGORY, fixedCategories);
        result.putExtra(Constants.EXTRA_LOCATION, locationIndex);
        setResult(Activity.RESULT_OK, result);
        finish();
    }

    /**
     * Saves the picture path from the received intent.
     *
     * @param picturePathIntent Intent received from WarningActivity.
     */
    private void savePicture(Intent picturePathIntent) {
        int categoryId = picturePathIntent.getIntExtra(Constants.EXTRA_CATEGORY_ID, 0);
        int subCategoryId = picturePathIntent.getIntExtra(Constants.EXTRA_SUBCATEGORY_ID, 0);
        for (SubCategory sc : mSubCategories) {
            if (categoryId == sc.getParentId() && subCategoryId == sc.getId()) {
                sc.setPictureUri(picturePathIntent.getStringExtra(Constants.EXTRA_PICTURE));
                break;
            }
        }
        // Calls checkData() to see if any other SubCategory need a picture.
        checkData();
    }

    /**
     * This method checks if any SubCategory has a grade below the accepted. And calls
     * WarningActivity if they do.
     */
    private void checkData() {
        statusUpdate(STATUS_LOAD);
        int count = 0;
        CheckDataLoop:
        for (SubCategory sc : mSubCategories) {
            if (_grades == null){
                _grades = new SparseIntArray();
                _grades.put(1, 1);
                Log.e(TAG, "Loaded default ratings");
            }
            // If any category has a grade below accepted AND has no picture URI related to it
            // the code will call WarningActivity to prompt the user to take a picture.
            if (sc.getPictureUri() == null && sc.getGrade() > _grades.get(sc.getCode(), 1)) {
                Intent intent = new Intent(this, WarningActivity.class);
                for (Category c : mCategories) {
                    if (c.getId() == sc.getParentId()) {
                        intent.putExtra(Constants.EXTRA_CATEGORY_NAME, c.getName());
                        intent.putExtra(Constants.EXTRA_SUBCATEGORY_NAME, sc.getName());
                        intent.putExtra(Constants.EXTRA_CATEGORY_ID, c.getId());
                        intent.putExtra(Constants.EXTRA_SUBCATEGORY_ID, sc.getId());
                        startActivityForResult(intent, WARNING_REQUEST);
                        break CheckDataLoop;
                    }
                }
            } else {
                // If no SubCategory is below accepted grade, the code will call prepareJson() to send
                // the checklist to the server.
                count++;
                if (mSubCategories.size() == count) {
                    //TODO YOLO
                    prepareJson();
                    //break;
                }
            }
        }
    }

    /**
     * This method saves the results received from SubCategoryActivity.
     *
     * @param intent the data received.
     */
    private void saveSubcategoryData(Intent intent) {
        picturesReady = false;
        ArrayList<SubCategory> subCategories = intent.getParcelableArrayListExtra(Constants.PARCELABLE_SUBCATEGORY);
        // Get the Category object on the intent and changes it's 'completed' variable to 'true'.
        Category category = intent.getParcelableExtra(Constants.PARCELABLE_CATEGORY);
        for (int i = 0; i < mCategories.size(); i++) {
            if (mCategories.get(i).getId() == category.getId()) {
                    mCategories.get(i).setCompleted(category.isCompleted());
            }
        }
        // If mSubCategories is not null then it checks if the SubCategories have already been
        // graded.
        if (mSubCategories != null) {
            boolean hasInstance = false;
            for (int i = 0; i < mSubCategories.size(); i++) {
                for (int j = 0; j < subCategories.size(); j++) {
                    if (mSubCategories.get(i).getParentId() == subCategories.get(j).getParentId() &&
                            mSubCategories.get(i).getId() == subCategories.get(j).getId()) {
                        //Updates the data on the SubCategory if it was already created.
                        mSubCategories.get(i).setGrade(subCategories.get(j).getGrade());
                        hasInstance = true;
                    }
                }
            }
            if (!hasInstance) {
                // If there were no SubCategories in the ArrayList related to the received category
                // it will just add the given SubCategory object.
                for (SubCategory subCategory : subCategories) {
                    mSubCategories.add(subCategory);
                }
            }
        } else {
            // If mSubCategories is null then it will just save every SubCategory received.
            mSubCategories = subCategories;
        }
        // Updates view.
        mAdapter.notifyDataSetChanged();
        String lastPictureUri = null;
        for (SubCategory sc : subCategories){
            if (sc.getPictureUri() != null){
                lastPictureUri = sc.getPictureUri();
            }
        }
        if (lastPictureUri != null){
            processPictureWhenReady(lastPictureUri);
        } else {
            picturesReady = true;
        }
    }

    /**
     * This method sends all the data from the checklist to the server.
     * <p/>
     * This uses Json and the Ion library.
     * https://github.com/koush/ion
     */
    private void prepareJson() {
        // Disables scrolling and tapping on the device, so we don't send data twice.
        mCardScroller.setFocusable(false);
        mGestureDetector = null;

        // Put all data we have to send in a JsonArray.
        new AsyncTask<Void, Void, Void>(){
            JsonObject json;
            @Override
            protected Void doInBackground(Void... voids) {
                JsonArray jsonArray = new JsonArray();
                for (SubCategory sc : mSubCategories) {
                    JsonObject object = new JsonObject();
                    object.addProperty("code", sc.getCode());
                    object.addProperty("rating", Utils.getStringFromRating(sc.getGrade()));
                    if (sc.getPictureUri() != null) {
                        object.addProperty("image", Utils.imgToString(sc.getPictureUri()));
                    }
                    jsonArray.add(object);
                }

                json = new JsonObject();
                json.addProperty("user_id", 1);
                json.addProperty("location_id", locationIndex);
                json.add("data", jsonArray);

                Log.e(TAG, json.toString());
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                sendData(json);
            }
        }.execute();
    }

    private void sendData(JsonObject json) {
        // Send the data to the server.
        Future<JsonObject> jsonObjectFuture = Ion.with(this)
                .load("http://glass.twisk-interactive.nl/checklist")
                .setJsonObjectBody(json)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        if (e != null) e.printStackTrace();
                        if (result != null) {
                            if (result.get("status").getAsString().equals("OK")) {
                                Log.e(TAG, result.toString());
                                // Calls statusComplete() after it receives a result as to let the user
                                // know the operation was completed.
                                statusUpdate(STATUS_COMPLETE);
                            } else {
                                statusUpdate(STATUS_INCOMPLETE);
                            }
                        } else {
                            statusUpdate(STATUS_COULDNOTCONNECT);
                        }
                    }
                });
    }

    /**
     * Changes the view to let the user know the data is sent. If the user clicks again he will
     * be sent to the starting screen of the app (MainActivity).
     */
    private void statusUpdate(int updateCode) {
        int position = mCardScroller.getSelectedItemPosition();
        mCardScroller.setSelection(mCategories.size()-1);
        String title = null;
        String footer = null;
        Drawable check = null;
        boolean load = false;
        int color = -1;
        int spinnerId = -1;
        switch (updateCode) {
            case STATUS_COMPLETE:
                title = getResources().getString(R.string.complete);
                footer = getResources().getString(R.string.send_complete);
                check = getResources().getDrawable(R.drawable.check);
                color = getResources().getColor(R.color.green);
                isSent = true;
                break;
            case STATUS_INCOMPLETE:
                title = getResources().getString(R.string.incomplete);
                footer = getResources().getString(R.string.send_failed);
                check = getResources().getDrawable(R.drawable.stop);
                color = getResources().getColor(R.color.red);
                break;
            case STATUS_COULDNOTCONNECT:
                title = getResources().getString(R.string.could_not_connect);
                footer = getResources().getString(R.string.request_failed);
                check = getResources().getDrawable(R.drawable.stop);
                color = getResources().getColor(R.color.red);
                break;
            case STATUS_LOAD:
                title = getResources().getString(R.string.upload_list);
                footer = getResources().getString(R.string.please_wait);
                check = getResources().getDrawable(R.drawable.progress_bar_green);
                spinnerId = R.id.sendProgressSpinner;
                load = true;
                break;
            case STATUS_SAVINGPICTURE:
                title = getResources().getString(R.string.saving_picture);
                footer = getResources().getString(R.string.please_wait);
                check = getResources().getDrawable(R.drawable.progress_bar_yellow);
                spinnerId = R.id.pictureProgressSpinner;
                load = true;
                break;
            case STATUS_CANSEND:
                title = getResources().getString(R.string.checklist_finish);
                footer = getResources().getString(R.string.tap_to_send);
                check = getResources().getDrawable(R.drawable.upload);
                color = getResources().getColor(R.color.green);
                break;
        }
        if (load){
            TextView tv = (TextView) mCardScroller.getSelectedView().findViewById(R.id.title);
            tv.setText(title);
            tv = (TextView) mCardScroller.getSelectedView().findViewById(R.id.footer);
            tv.setText(footer);
            mCardScroller.getSelectedView().findViewById(R.id.left_arrow).setVisibility(View.INVISIBLE);
            mCardScroller.getSelectedView().findViewById(R.id.check).setVisibility(View.GONE);
            ProgressBar spinner = (ProgressBar) mCardScroller.getSelectedView()
                    .findViewById(spinnerId);
            spinner.setVisibility(View.VISIBLE);
            spinner.setIndeterminateDrawable(check);
        } else {
            TextView tv = (TextView) mCardScroller.getSelectedView().findViewById(R.id.title);
            tv.setText(title);
            tv = (TextView) mCardScroller.getSelectedView().findViewById(R.id.footer);
            tv.setText(footer);
            mCardScroller.getSelectedView().findViewById(R.id.pictureProgressSpinner).setVisibility(View.GONE);
            mCardScroller.getSelectedView().findViewById(R.id.sendProgressSpinner).setVisibility(View.GONE);
            ImageView iv = (ImageView) mCardScroller.getSelectedView().findViewById(R.id.check);
            iv.setVisibility(View.VISIBLE);
            iv.setImageDrawable(check);
            iv.setColorFilter(color);
            mGestureDetector = createGestureDetector(this);
        }
        mCardScroller.setSelection(position);
    }

    /**
     * Starts the InstructionActivity
     */
    private void startInstructions() {
        Intent intent = new Intent(this, InstructionsActivity.class);
        int position = mCardScroller.getSelectedItemPosition();
        intent.putExtra(Constants.EXTRA_POSITION, position);
        startActivity(intent);
    }

    /**
     * Starts the SubCategoryActivity
     */
    private void startSubCategories() {
        Intent intent = new Intent(this, SubCategoriesActivity.class);
        int position = mCardScroller.getSelectedItemPosition();
        ArrayList<SubCategory> subCategories = new ArrayList<>();
        // Check if mSubCategories is null. If it's not then it checks if the category has been
        // rated before, in which case it sends the previews values to SubCategoryActivity to allow
        // the user to change it.
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
        intent.putExtra(Constants.EXTRA_LOCATION, locationIndex);
        startActivityForResult(intent, SUBCATEGORY_RATING_REQUEST);
    }

    private void processPictureWhenReady(final String picturePath) {
        final File pictureFile = new File(picturePath);

        if (pictureFile.exists()) {
            picturesReady = true;
            statusUpdate(STATUS_CANSEND);
        } else {
            // The file does not exist yet. Before starting the file observer, you
            // can update your UI to let the user know that the application is
            // waiting for the picture (for example, by displaying the thumbnail
            // image and a progress indicator).

            final File parentDirectory = pictureFile.getParentFile();
            FileObserver observer = new FileObserver(parentDirectory.getPath(),
                    FileObserver.CLOSE_WRITE | FileObserver.MOVED_TO) {
                // Protect against additional pending events after CLOSE_WRITE
                // or MOVED_TO is handled.
                private boolean isFileWritten;

                @Override
                public void onEvent(int event, String path) {
                    if (!isFileWritten) {
                        // For safety, make sure that the file that was created in
                        // the directory is actually the one that we're expecting.
                        File affectedFile = new File(parentDirectory, path);
                        isFileWritten = affectedFile.equals(pictureFile);

                        if (isFileWritten) {
                            stopWatching();

                            // Now that the file is ready, recursively call
                            // processPictureWhenReady again (on the UI thread).
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    processPictureWhenReady(picturePath);
                                }
                            });
                        }
                    }
                }
            };
            observer.startWatching();
        }
    }
}
