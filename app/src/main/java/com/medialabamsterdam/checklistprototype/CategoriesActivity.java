package com.medialabamsterdam.checklistprototype;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.FileObserver;
import android.support.annotation.NonNull;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.android.glass.media.Sounds;
import com.google.android.glass.touchpad.Gesture;
import com.google.android.glass.touchpad.GestureDetector;
import com.google.android.glass.widget.CardScrollView;
import com.google.android.glass.widget.Slider;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.Future;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.medialabamsterdam.checklistprototype.Adapters.CategoryCardScrollAdapter;
import com.medialabamsterdam.checklistprototype.ContainerClasses.Category;
import com.medialabamsterdam.checklistprototype.ContainerClasses.SubCategory;
import com.medialabamsterdam.checklistprototype.Utilities.Constants;
import com.medialabamsterdam.checklistprototype.Utilities.Status;
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

    private CardScrollView mCardScroller;
    private GestureDetector mGestureDetector;
    private ArrayList<Category> mCategories;
    private ArrayList<SubCategory> mSubCategories;
    private CategoryCardScrollAdapter mAdapter;
    private int locationIndex;
    private int areaCode;
    private boolean isSent = false;
    private SparseIntArray _grades;
    private volatile boolean picturesReady = true;
    private volatile Status statusCurrent;
    private Slider.GracePeriod mGracePeriod;
    private final Slider.GracePeriod.Listener mGracePeriodListener =
            new Slider.GracePeriod.Listener() {

                @Override
                public void onGracePeriodEnd() {
                    // Play a SUCCESS sound to indicate the end of the grace period.
                    AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                    am.playSoundEffect(Sounds.SUCCESS);
                    if (mCategories.get(mCardScroller.getSelectedItemPosition()).isSkip()) {
                        mCategories.get(mCardScroller.getSelectedItemPosition()).setSkip(false);
                    } else {
                        mCategories.get(mCardScroller.getSelectedItemPosition()).setSkip(true);
                    }
                    mAdapter.notifyDataSetChanged();
                    checkIfCompleteOrCanSend();
                    mCardScroller.getSelectedView().findViewById(R.id.overlay).setVisibility(View.GONE);
                    mGracePeriod = null;
                }

                @Override
                public void onGracePeriodCancel() {
                    // Play a DISMISS sound to indicate the cancellation of the grace period.
                    AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                    am.playSoundEffect(Sounds.DISMISSED);
                    mCardScroller.getSelectedView().findViewById(R.id.overlay).setVisibility(View.GONE);
                    mGracePeriod = null;
                }
            };

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        //Get data from intent sent from MainActivity.
        Intent i = getIntent();
        mCategories = i.getParcelableArrayListExtra(Constants.EXTRA_CATEGORY);
        mSubCategories = i.getParcelableArrayListExtra(Constants.EXTRA_SUBCATEGORY);
        locationIndex = i.getIntExtra(Constants.EXTRA_LOCATION, 0);
        areaCode = i.getIntExtra(Constants.EXTRA_AREA_CODE, 0);
        statusCurrent = Status.CATEGORY_INCOMPLETE;

        //Regular CardScroller/Adapter procedure.
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mCardScroller = new CardScrollView(this);
        mAdapter = new CategoryCardScrollAdapter(this, mCategories, statusCurrent);
        mCardScroller.setAdapter(mAdapter);
        mCardScroller.activate();
        mCardScroller.setHorizontalScrollBarEnabled(false);
        mGestureDetector = createGestureDetector(this);
        setContentView(mCardScroller);
        checkIfCompleteOrCanSend();
    }

    //<editor-fold desc="onPause/Resume and onInstance">
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
        savedInstanceState.putParcelableArrayList(Constants.PARCELABLE_CATEGORY, mCategories);
        savedInstanceState.putParcelableArrayList(Constants.PARCELABLE_SUBCATEGORY, mSubCategories);
        savedInstanceState.putSerializable("status", statusCurrent);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        statusCurrent = (Status) savedInstanceState.get("status");
        mCategories = savedInstanceState.getParcelableArrayList(Constants.PARCELABLE_CATEGORY);
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
                int position = mCardScroller.getSelectedItemPosition();
                int maxPositions = mAdapter.getCount() - 1;
                int completion = 0;
                for (Category category : mCategories) {
                    if (category.isComplete() || category.isSkip()) completion++;
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
                                        am.playSoundEffect(Sounds.DISALLOWED);
                                    }
                                } else {
                                    int i = 0;
                                    for (Category category : mCategories) {
                                        if (!category.isComplete() || !category.isSkip()) {
                                            mCardScroller.setSelection(i);
                                            break;
                                        }
                                        i++;
                                    }
                                }
                                // If CardScroller is not at the last position it will either start
                                // SubCategoriesActivity or InstructionsActivity based on Settings.
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
                    case LONG_PRESS: //Skips the category
                        Log.e(TAG, "LONG_PRESS called.");
                        Slider mSlider = Slider.from(mCardScroller);
                        mGracePeriod = mSlider.startGracePeriod(mGracePeriodListener);
                        mCardScroller.getSelectedView().findViewById(R.id.overlay).setVisibility(View.VISIBLE);
                        TextView tv = (TextView) mCardScroller.getSelectedView().findViewById(R.id.overlaytext);
                        if (!mCategories.get(mCardScroller.getSelectedItemPosition()).isSkip()) {
                            tv.setText(R.string.skip);
                        } else {
                            tv.setText(R.string.unskip);
                        }
                        break;
                }
                return false;
            }
        });
        return gestureDetector;
    }

    @Override
    public boolean onGenericMotionEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (mGracePeriod != null) {
                mGracePeriod.cancel();
            }
        }
        return mGestureDetector != null && mGestureDetector.onMotionEvent(event);
    }
    //</editor-fold>

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == WARNING_REQUEST && resultCode == RESULT_OK) {
            savePicture(data);
        }
        if (requestCode == SUBCATEGORY_RATING_REQUEST && resultCode == RESULT_OK) {
            saveSubcategoryData(data);
        }
    }

    /**
     * This method grabs grades from the server in order to compare them with the grades from user
     * input.
     * <p>
     * This uses Json and the Ion library.
     * https://github.com/koush/ion
     */
    private void getGrades() {
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
                                checkData();
                            } else {
                                statusCurrent = Status.FAIL_CONNECT;
                                mAdapter.updateStatus(statusCurrent);
                                mAdapter.notifyDataSetChanged();
//                                checkData();
                            }
                        } else {
                            statusCurrent = Status.FAIL_CONNECT;
                            mAdapter.updateStatus(statusCurrent);
                            mAdapter.notifyDataSetChanged();
//                            checkData();
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
        statusCurrent = Status.UPLOADING;
        mAdapter.updateStatus(statusCurrent);
        mAdapter.notifyDataSetChanged();
        int count = 0;
        CheckDataLoop:
        for (SubCategory sc : mSubCategories) {
            if (_grades == null) {
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
                    prepareJson();
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
        ArrayList<SubCategory> subCategories = intent.getParcelableArrayListExtra(Constants.PARCELABLE_SUBCATEGORY);
        // Get the Category object on the intent and changes it's 'completed' variable to 'true'.
        Category category = intent.getParcelableExtra(Constants.PARCELABLE_CATEGORY);
        for (int i = 0; i < mCategories.size(); i++) {
            if (mCategories.get(i).getId() == category.getId()) {
                mCategories.get(i).setComplete(category.isComplete());
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
        checkIfCompleteOrCanSend();
    }

    /**
     * Checks if the categories have all been either graded or skipped. Then checks if there are
     * pictures still being processed before allowing the used to send the checklist to the server.
     */
    private void checkIfCompleteOrCanSend() {
        int count = 0;
        for (Category category : mCategories) {
            if (category.isComplete() || category.isSkip()) {
                count++;
                if (count == mCategories.size() - 1) {
                    statusCurrent = Status.CATEGORY_COMPLETE;
                    mAdapter.updateStatus(statusCurrent);
                    String lastPictureUri;
                    for (SubCategory sc : mSubCategories) {
                        if (sc.getPictureUri() != null) {
                            lastPictureUri = sc.getPictureUri();
                            picturesReady = false;
                            processPictureWhenReady(lastPictureUri);
                        }
                    }
                    if (statusCurrent != Status.SAVING_PICTURE && picturesReady) {
                        statusCurrent = Status.CAN_SEND;
                        mAdapter.updateStatus(statusCurrent);
                    }
                }
            }
        }
    }

    /**
     * This method prepares all the data from the checklist to send to the server.
     */
    private void prepareJson() {
        // Disables scrolling and tapping on the device, so we don't send data twice.
        mCardScroller.setFocusable(false);
        mGestureDetector = null;

        // Put all unskipped data we have to send in a JsonArray.
        new AsyncTask<Void, Void, Void>() {
            JsonObject json;

            @Override
            protected Void doInBackground(Void... voids) {
                JsonArray jsonArray = new JsonArray();
                ArrayList<Integer> categoriesToSkip = new ArrayList<>();
                for (Category category : mCategories) {
                    if (category.isSkip()) {
                        categoriesToSkip.add(category.getId());
                    }
                }
                for (SubCategory sc : mSubCategories) {
                    if (!categoriesToSkip.contains(sc.getParentId())) {
                        JsonObject object = new JsonObject();
                        object.addProperty("code", sc.getCode());
                        object.addProperty("rating", Utils.getStringFromRating(sc.getGrade()));
                        if (sc.getPictureUri() != null) {
                            object.addProperty("image", Utils.imgToString(sc.getPictureUri()));
                        }
                        jsonArray.add(object);
                    }
                }

                json = new JsonObject();
                json.addProperty("user_id", Constants.USER_ID);
                json.addProperty("location_id", locationIndex);
                json.add("data", jsonArray);

                Log.v(TAG, json.toString());
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                sendData(json);
            }
        }.execute();
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
                                // Calls statusComplete() after it receives a result as to let the user
                                // know the operation was completed.
                                statusCurrent = Status.UPLOAD_COMPLETE;
                                mAdapter.updateStatus(statusCurrent);
                            } else {
                                statusCurrent = Status.FAIL_SEND;
                                mAdapter.updateStatus(statusCurrent);
                            }
                            mCardScroller.setFocusable(true);
                            mGestureDetector = createGestureDetector(getApplicationContext());
                            isSent = true;
                        } else {
                            statusCurrent = Status.FAIL_CONNECT;
                            mAdapter.updateStatus(statusCurrent);
                            mCardScroller.setFocusable(true);
                            mGestureDetector = createGestureDetector(getApplicationContext());
                            isSent = true;
                        }
                    }
                });
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

    /**
     * Receives the path of a picture in order to watch if it was written.
     *
     * @param picturePath the path of the last picture taken.
     */
    private void processPictureWhenReady(final String picturePath) {
        final File pictureFile = new File(picturePath);

        if (pictureFile.exists()) {
            picturesReady = true;
            statusCurrent = Status.CAN_SEND;
            mAdapter.updateStatus(statusCurrent);
        } else {
            statusCurrent = Status.SAVING_PICTURE;
            mAdapter.updateStatus(statusCurrent);
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
