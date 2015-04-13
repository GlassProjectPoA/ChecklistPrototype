package com.medialabamsterdam.checklistprototype;

import com.google.android.glass.media.Sounds;
import com.google.android.glass.touchpad.Gesture;
import com.google.android.glass.touchpad.GestureDetector;
import com.google.android.glass.view.WindowUtils;
import com.google.android.glass.widget.CardBuilder;
import com.google.android.glass.widget.CardScrollAdapter;
import com.google.android.glass.widget.CardScrollView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.TextView;

import java.util.ArrayList;


public class InstructionsActivity extends Activity {

    private CardScrollView mCardScroller;
    private View mView;
    private GestureDetector mGestureDetector;
    private ArrayList<View> mCards;
    private int tapCount = 0;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mView = createInstructionsCard();

        if (tapCount==0) {
            Utils.ChangeTextColor(this, mView, R.id.instruction_text, R.string.swipe_two_to_grade, "Swipe", R.color.blue);
        }

        mCardScroller = new CardScrollView(this);
        mCardScroller.setAdapter(new CardScrollAdapter() {
            @Override
            public int getPosition(Object item) {
                return mCards.indexOf(item);
            }

            @Override
            public int getCount() {
                return mCards.size();
            }

            @Override
            public Object getItem(int position) {
                return mCards.get(position);
            }


            @Override
            public int getViewTypeCount() {
                return CardBuilder.getViewTypeCount();
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                return mView; //return mViews.get(position);
            }
        });

        mGestureDetector = createGestureDetector(this);
        setContentView(mCardScroller);
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
                if (gesture == Gesture.TAP) {
                    updateView();
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

    private void updateView(){
        tapCount++;
        if (tapCount==1){
            TextView tv = (TextView)mView.findViewById(R.id.instruction_text);
            tv.setText(R.string.tap_two_to_confirm_grade);
            Utils.ChangeTextColor(this, mView, R.id.instruction_text, R.string.tap_two_to_confirm_grade, "Tap", R.color.green);
        }
        else{
            openRating();
        }
    }

    private void openRating() {
        Intent intent = getIntent();
        int position = intent.getIntExtra(CategoriesActivity.EXTRA_POSITION, 404);
        intent = new Intent(this, RatingActivity.class);
        intent.putExtra(CategoriesActivity.EXTRA_POSITION, position);
        startActivity(intent);
    }

    private View createInstructionsCard() {//List<ChecklistTask> tasks) {
        mCards = new ArrayList<>();
        LayoutInflater inflater = LayoutInflater.from(this);
        View card = inflater.inflate(R.layout.instruction_layout, null);
        mCards.add(card);
        return mCards.get(0);
    }

}
