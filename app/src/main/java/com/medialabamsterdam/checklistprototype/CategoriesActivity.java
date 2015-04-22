package com.medialabamsterdam.checklistprototype;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.android.glass.media.Sounds;
import com.google.android.glass.touchpad.Gesture;
import com.google.android.glass.touchpad.GestureDetector;
import com.google.android.glass.widget.CardScrollView;

import java.util.ArrayList;
import java.util.List;

public class CategoriesActivity extends Activity {


    private CardScrollView mCardScroller;
    private GestureDetector mGestureDetector;
    private List<View> mCards;
    private MyCardScrollAdapter mAdapter;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        createCards();

        mCardScroller = new CardScrollView(this);
        mAdapter = new MyCardScrollAdapter(mCards);
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
                if (gesture == Gesture.TAP) {
                    if (Constants.IGNORE_INSTRUCTIONS) {
                        openRating();
                    } else {
                        openInstructions();
                    }
                    AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                    am.playSoundEffect(Sounds.TAP);
                    return true;
                } else if (gesture == Gesture.TWO_TAP) {
                    // do something on two finger tap
                    return true;
                } else if (gesture == Gesture.SWIPE_RIGHT) {
                    // do something on right (forward) swipe
                    return true;
                } else if (gesture == Gesture.SWIPE_LEFT) {
                    // do something on left (backwards) swipe
                    return true;
                } else if (gesture == Gesture.SWIPE_DOWN) {
                    finish();
                }
                return false;
            }
        });

        gestureDetector.setFingerListener(new GestureDetector.FingerListener() {
            @Override
            public void onFingerCountChanged(int previousCount, int currentCount) {
                // do something on finger count changes
            }
        });

        gestureDetector.setScrollListener(new GestureDetector.ScrollListener() {
            @Override
            public boolean onScroll(float displacement, float delta, float velocity) {
                // do something on scrolling
                return true;
            }
        });

        return gestureDetector;
    }

    @Override
    public boolean onGenericMotionEvent(MotionEvent event) {
        if (mGestureDetector != null) {
            return mGestureDetector.onMotionEvent(event);
        }
        return false;
    }
    //endregion

    private void createCards() {
        mCards = new ArrayList<>();
        LayoutInflater inflater = LayoutInflater.from(this);
        String[] strs = getResources().getStringArray(R.array.categories_list);
        int i = 0;
        for (String str : strs) {
            i++;
            View card = inflater.inflate(R.layout.categories_layout, null);
            TextView tv = (TextView) card.findViewById(R.id.rating_title);
            tv.setText(str);
            tv = (TextView) card.findViewById(R.id.order);
            tv.setText(Integer.toString(i));
            mCards.add(card);
            if(i == 1 && strs.length == 1){
                tv = (TextView) card.findViewById(R.id.left_arrow);
                tv.setTextColor(getResources().getColor(R.color.gray_dark));
                tv = (TextView) card.findViewById(R.id.right_arrow);
                tv.setTextColor(getResources().getColor(R.color.gray_dark));
            } else if(i == 1){
                tv = (TextView) card.findViewById(R.id.left_arrow);
                tv.setTextColor(getResources().getColor(R.color.gray_dark));
            } else if (strs.length == i){
                tv = (TextView) card.findViewById(R.id.right_arrow);
                tv.setTextColor(getResources().getColor(R.color.gray_dark));
            }
        }
    }

    private void openInstructions() {
        Intent intent = new Intent(this, InstructionsActivity.class);
        int position = mCardScroller.getSelectedItemPosition();
        intent.putExtra(Constants.EXTRA_POSITION, position);
        startActivity(intent);
    }

    private void openRating() {
        Intent intent = new Intent(this, SubCategoryActivity.class);
        int position = mCardScroller.getSelectedItemPosition();
        intent.putExtra(Constants.EXTRA_POSITION, position);
        startActivity(intent);
    }

}
