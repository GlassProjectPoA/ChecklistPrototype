package com.medialabamsterdam.checklistprototype;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.FileObserver;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.glass.content.Intents;
import com.google.android.glass.media.Sounds;
import com.google.android.glass.touchpad.Gesture;
import com.google.android.glass.touchpad.GestureDetector;
import com.google.android.glass.view.WindowUtils;
import com.google.android.glass.widget.CardScrollView;
import com.medialabamsterdam.checklistprototype.Adapters.MyCardScrollAdapter;
import com.medialabamsterdam.checklistprototype.Utilities.Constants;

import java.io.File;
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
    private ProgressBar spinner;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        if (OK_GLASS) {
            getWindow().requestFeature(WindowUtils.FEATURE_VOICE_COMMANDS);
        }

        createWarningCard();

        mCardScroller = new CardScrollView(this);
        mAdapter = new MyCardScrollAdapter(mCards);
        mCardScroller.setAdapter(mAdapter);
        mCardScroller.setFocusable(false);
        mCardScroller.setHorizontalScrollBarEnabled(false);
        mGestureDetector = createGestureDetector(this);
        setContentView(mCardScroller);
        spinner = (ProgressBar)mCards.get(1).findViewById(R.id.pictureProcessBar);
    }

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

    //region Gesture Detector
    private GestureDetector createGestureDetector(final Context context) {
        GestureDetector gestureDetector = new GestureDetector(context);

        //Create a base listener for generic gestures
        gestureDetector.setBaseListener(new GestureDetector.BaseListener() {
            @Override
            public boolean onGesture(Gesture gesture) {
                Log.e(TAG, "gesture = " + gesture);
                AudioManager am = (AudioManager) WarningActivity.this.getSystemService(Context.AUDIO_SERVICE);
                switch (gesture) {
                    case TAP:
                        Log.e(TAG, "TAP called.");
                        WarningActivity.this.takePicture();
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
    //endregion

    private void takePicture() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, TAKE_PICTURE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == TAKE_PICTURE_REQUEST && resultCode == RESULT_OK) {
            String thumbnailPath = data.getStringExtra(Intents.EXTRA_THUMBNAIL_FILE_PATH);
            String picturePath = data.getStringExtra(Intents.EXTRA_PICTURE_FILE_PATH);

            processPictureWhenReady(picturePath);
            // TODO: Show the thumbnail to the user while the full picture is being
            // processed.
            if (spinner.getVisibility() == View.GONE) {
                mCardScroller.activate();
                mCards.get(1).findViewById(R.id.left_arrow).setVisibility(View.INVISIBLE);
                mCards.get(1).findViewById(R.id.check).setVisibility(View.GONE);
                TextView tv = (TextView) mCards.get(1).findViewById(R.id.title);
                tv.setText(R.string.saving_picture);
                tv = (TextView) mCards.get(1).findViewById(R.id.footer);
                tv.setText(R.string.please_wait);
                spinner.setVisibility(View.VISIBLE);
                mCardScroller.setSelection(1);
                mAdapter.notifyDataSetChanged();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void processPictureWhenReady(final String picturePath) {
        final File pictureFile = new File(picturePath);

        if (pictureFile.exists()) {
            // The picture is ready; process it.
            Intent result = new Intent();
            result.putExtra(Constants.EXTRA_PICTURE, picturePath);
            setResult(Activity.RESULT_OK, result);
            finish();
        } else {
            // The file does not exist yet. Before starting the file observer, you
            // can update your UI to let the user know that the application is
            // waiting for the picture (for example, by displaying the thumbnail
            // image and a progress indicator).

            final File parentDirectory = pictureFile.getParentFile();
            FileObserver observer = new FileObserver(parentDirectory.getPath(), FileObserver.CLOSE_WRITE | FileObserver.MOVED_TO) {
                private boolean isFileWritten;

                @Override
                public void onEvent(int event, String path) {
                    if (!isFileWritten) {
                        File affectedFile = new File(parentDirectory, path);
                        isFileWritten = affectedFile.equals(pictureFile);

                        if (isFileWritten) {
                            stopWatching();
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

    private void createWarningCard() {
        mCards = new ArrayList<>();
        LayoutInflater inflater = LayoutInflater.from(this);
        View card = inflater.inflate(R.layout.warning_layout, null);
        mCards.add(card);
        card = inflater.inflate(R.layout.check_layout, null);
        mCards.add(card);
    }

}
