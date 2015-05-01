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
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.glass.media.Sounds;
import com.google.android.glass.touchpad.Gesture;
import com.google.android.glass.touchpad.GestureDetector;
import com.google.android.glass.widget.CardScrollView;
import com.medialabamsterdam.checklistprototype.Adapters.CategoryCardScrollAdapter;
import com.medialabamsterdam.checklistprototype.ContainerClasses.Category;
import com.medialabamsterdam.checklistprototype.Utilities.Constants;

import java.util.ArrayList;

/**
 * Created by
 * Jose Carlos Quintas Junior
 * juniorquintas@gmail.com
 * on 01/04/2015.
 */
public class CategoriesActivity extends Activity {

    public final static String TAG = "CATEGORIES";
    private final static int WARNING_REQUEST = 9574;

    private CardScrollView mCardScroller;
    private GestureDetector mGestureDetector;
    private ArrayList<Category> mCategoryViews;
    private CategoryCardScrollAdapter mAdapter;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        createCards();

        mCardScroller = new CardScrollView(this);
        mAdapter = new CategoryCardScrollAdapter(this, mCategoryViews);
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
                Log.e(TAG, "gesture = " + gesture);
                int position = mCardScroller.getSelectedItemPosition();
                int maxPositions = mAdapter.getCount() - 1;
                AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                switch (gesture) {
                    case TAP:
                        Log.e(TAG, "TAP called.");
                        if (position == maxPositions) {
                            checkData();
                        } else {
                            if (Constants.IGNORE_INSTRUCTIONS) {
                                openRating();
                            } else {
                                openInstructions();
                            }
                            am.playSoundEffect(Sounds.TAP);
                        }
                        break;
                }
                return false;
            }
        });
        return gestureDetector;
    }

    private void checkData() {
        //TODO Check ratings if there is any that is below B, start intent.
        Intent intent = new Intent(this, WarningActivity.class);
        startActivityForResult(intent, WARNING_REQUEST);
        sendData(null);
    }

    @Override
    public boolean onGenericMotionEvent(MotionEvent event) {
        return mGestureDetector != null && mGestureDetector.onMotionEvent(event);
    }
    //endregion

    private void createCards() {
        mCategoryViews = new ArrayList<>();
        String[] strs = getResources().getStringArray(R.array.categories_list);
        int index = 0;
        for (String str : strs) {
            Category category = new Category(index, str);
            index++;
            mCategoryViews.add(category);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if (requestCode == WARNING_REQUEST && resultCode == RESULT_OK) {
            sendData(data.getStringExtra(Constants.EXTRA_PICTURE));
        }
    }

    private void sendData(String picturePath) {
        if(picturePath == null){
            //TODO send data without picture
            Log.d(TAG, "null");
        }else{
            //TODO send data with picture
            TextView tv = (TextView)mCardScroller.getSelectedView().findViewById(R.id.title);
            tv.setText(R.string.upload_list);
            tv = (TextView)mCardScroller.getSelectedView().findViewById(R.id.footer);
            tv.setText(R.string.please_wait);
            mCardScroller.getSelectedView().findViewById(R.id.check).setVisibility(View.GONE);
            ProgressBar spinner = (ProgressBar) mCardScroller.getSelectedView().findViewById(R.id.pictureProcessBar);
            spinner.setVisibility(View.VISIBLE);
            spinner.setIndeterminateDrawable(getResources().getDrawable(R.drawable.progress_bar_green));
            mCardScroller.getSelectedView().findViewById(R.id.left_arrow).setVisibility(View.INVISIBLE);
            Log.d(TAG, picturePath);
        }
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
        intent.putExtra(Constants.EXTRA_POSITION, position);
        startActivity(intent);
    }

}
