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
import com.medialabamsterdam.checklistprototype.Adapters.MyCardScrollAdapter;
import com.medialabamsterdam.checklistprototype.Utilities.Constants;
import com.medialabamsterdam.checklistprototype.Utilities.Utils;

import java.util.ArrayList;

/**
 * Created by
 * Jose Carlos Quintas Junior
 * juniorquintas@gmail.com
 * on 01/04/2015.
 *
 * THIS CLASS ISN'T REALLY USED ANYMORE
 */
public class InstructionsActivity extends Activity {
    private final static String TAG = "INSTRUCTIONS";
    private CardScrollView mCardScroller;
    private View mView;
    private GestureDetector mGestureDetector;
    private ArrayList<View> mCards;
    private int tapCount = 0;
    private MyCardScrollAdapter mAdapter;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mView = createInstructionsCard();

        if (tapCount == 0) {
            Utils.ChangeTextColor(this, mView, R.id.instruction_text, R.string.swipe_two_to_grade, "Swipe", R.color.blue);
        }

        mCardScroller = new CardScrollView(this);
        mAdapter = new MyCardScrollAdapter(mCards);
        mCardScroller.setAdapter(mAdapter);

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
                Log.e(TAG, "gesture = " + gesture);
                AudioManager am = (AudioManager) InstructionsActivity.this.getSystemService(Context.AUDIO_SERVICE);
                switch (gesture) {
                    case TAP:
                        // Create intent to deliver some kind of result data
                        InstructionsActivity.this.updateView();
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

    private void updateView() {
        tapCount++;
        if (tapCount == 1) {
            TextView tv = (TextView) mView.findViewById(R.id.instruction_text);
            tv.setText(R.string.tap_two_to_confirm_grade);
            Utils.ChangeTextColor(this, mView, R.id.instruction_text, R.string.tap_two_to_confirm_grade, "Tap", R.color.green);
        } else {
            openRating();
        }
    }

    private void openRating() {
        Intent intent = getIntent();
        int position = intent.getIntExtra(Constants.EXTRA_POSITION, 404);
        intent = new Intent(this, SubCategoriesActivity.class);
        intent.putExtra(Constants.EXTRA_POSITION, position);
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
