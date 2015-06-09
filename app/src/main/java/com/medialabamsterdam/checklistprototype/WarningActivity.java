package com.medialabamsterdam.checklistprototype;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.android.glass.content.Intents;
import com.google.android.glass.media.Sounds;
import com.google.android.glass.touchpad.Gesture;
import com.google.android.glass.touchpad.GestureDetector;
import com.google.android.glass.view.WindowUtils;
import com.google.android.glass.widget.CardScrollView;
import com.medialabamsterdam.checklistprototype.Adapters.MyCardScrollAdapter;
import com.medialabamsterdam.checklistprototype.Utilities.Constants;

import java.util.ArrayList;

/**
 * Created by
 * Jose Carlos Quintas Junior
 * juniorquintas@gmail.com
 * on 01/03/2015.
 */
public class WarningActivity extends Activity {

    private final static boolean OK_GLASS = true;
    private final static String TAG = "WARNING";
    private final static int TAKE_PICTURE_REQUEST = 6169;

    private CardScrollView mCardScroller;
    private GestureDetector mGestureDetector;
    private ArrayList<View> mCards;
    private MyCardScrollAdapter mAdapter;
    private int categoryId;
    private int subCategoryId;
    private String categoryName;
    private String subCategoryName;


    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        // Enables voice commands.
        if (OK_GLASS) {
            getWindow().requestFeature(WindowUtils.FEATURE_VOICE_COMMANDS);
        }

        //Get data from intent sent from CategoryActivity.
        Intent intent = getIntent();
        categoryId = intent.getIntExtra(Constants.EXTRA_CATEGORY_ID, 0);
        subCategoryId = intent.getIntExtra(Constants.EXTRA_SUBCATEGORY_ID, 0);
        if (intent.hasExtra(Constants.EXTRA_CATEGORY_NAME)) {
            categoryName = intent.getStringExtra(Constants.EXTRA_CATEGORY_NAME);
        } else {
            categoryName = "SubCategory -";
        }
        subCategoryName = intent.getStringExtra(Constants.EXTRA_SUBCATEGORY_NAME);

        //Regular CardScroller/Adapter procedure.
        createWarningCard();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mCardScroller = new CardScrollView(this);
        mAdapter = new MyCardScrollAdapter(mCards);
        mCardScroller.setAdapter(mAdapter);
        mCardScroller.setFocusable(false);
        mCardScroller.setHorizontalScrollBarEnabled(false);
        mGestureDetector = createGestureDetector(this);
        setContentView(mCardScroller);
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
    //</editor-fold>

    //<editor-fold desc="Gesture Detector">
    private GestureDetector createGestureDetector(final Context context) {
        GestureDetector gestureDetector = new GestureDetector(context);

        //Create a base listener for generic gestures
        gestureDetector.setBaseListener(new GestureDetector.BaseListener() {
            @Override
            public boolean onGesture(Gesture gesture) {
                Log.e(TAG, "gesture = " + gesture);
                AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                switch (gesture) {
                    case TAP:
                        Log.e(TAG, "TAP called.");
                        takePicture();
                        am.playSoundEffect(Sounds.TAP);
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
    //</editor-fold>

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == TAKE_PICTURE_REQUEST && resultCode == RESULT_OK) {
            sendResult(data);
        }
    }

    /**
     * Starts intent to take a picture with the glass' default program.
     */
    private void takePicture() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, TAKE_PICTURE_REQUEST);
    }

    /**
     * Sends result from the takePicture intent back to CategoryActivity.
     *
     * @param picturePathIntent the intent received from takePicture containing the picture path.
     */
    private void sendResult(Intent picturePathIntent) {
//        String thumbnailPath = picturePathIntent.getStringExtra(Intents.EXTRA_THUMBNAIL_FILE_PATH);
        String picturePath = picturePathIntent.getStringExtra(Intents.EXTRA_PICTURE_FILE_PATH);
        Intent result = new Intent();
        result.putExtra(Constants.EXTRA_PICTURE, picturePath);
        result.putExtra(Constants.EXTRA_CATEGORY_ID, categoryId);
        result.putExtra(Constants.EXTRA_SUBCATEGORY_ID, subCategoryId);
        setResult(Activity.RESULT_OK, result);
        finish();
    }

    /**
     * Creates the card view.
     */
    private void createWarningCard() {
        mCards = new ArrayList<>();
        LayoutInflater inflater = LayoutInflater.from(this);
        View card = inflater.inflate(R.layout.warning_layout, null);
        TextView tv = (TextView) card.findViewById(R.id.category_text);
        tv.setText(categoryName);
        tv = (TextView) card.findViewById(R.id.subcategory_text);
        tv.setText(subCategoryName);
        mCards.add(card);
    }

}
