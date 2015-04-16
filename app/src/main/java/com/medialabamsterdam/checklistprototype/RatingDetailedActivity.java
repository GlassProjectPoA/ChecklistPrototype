package com.medialabamsterdam.checklistprototype;

import com.google.android.glass.media.Sounds;
import com.google.android.glass.touchpad.Gesture;
import com.google.android.glass.touchpad.GestureDetector;
import com.google.android.glass.widget.CardBuilder;
import com.google.android.glass.widget.CardScrollAdapter;
import com.google.android.glass.widget.CardScrollView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class RatingDetailedActivity extends Activity {

    public final static String EXTRA_POSITION = "com.medialabamsterdam.checklistprototype.POSITION";
    private CardScrollView mCardScroller;
    private GestureDetector mGestureDetector;
    private List<View> mCards;
    private MyCardScrollAdapter mAdapter;
    private int subCategory;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        Intent intent = getIntent();
        subCategory = intent.getIntExtra(Constants.EXTRA_POSITION, 0);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        createCards();

        mCardScroller = new CardScrollView(this);
        mAdapter = new MyCardScrollAdapter(mCards);

        mCardScroller.setAdapter(mAdapter);
        mCardScroller.activate();
        mGestureDetector = createGestureDetector(this);
        setContentView(mCardScroller);
        mCardScroller.setSelection(intent.getIntExtra(Constants.EXTRA_RATING,0)-1);
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
        gestureDetector.setBaseListener( new GestureDetector.BaseListener() {
            @Override
            public boolean onGesture(Gesture gesture) {
                if (gesture == Gesture.TAP) {
                    // Create intent to deliver some kind of result data
                    Intent result = new Intent();
                    result.putExtra(Constants.EXTRA_RATING_DETAIL, mCardScroller.getSelectedItemPosition()-1);
                    result.putExtra(Constants.EXTRA_POSITION, subCategory);
                    setResult(Activity.RESULT_OK, result);
                    AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                    am.playSoundEffect(Sounds.DISALLOWED);
                    finish();
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
                } else if (gesture == Gesture.SWIPE_DOWN){
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

    private void createCards(){
        mCards = new ArrayList<>();
        LayoutInflater inflater = LayoutInflater.from(this);
        //TODO fix this entire method.. T_T
        String[] percentage = new String[]{"", "0%", "<2%", "<5%", "<10%", "10%>"};
        String[] detail;
        if (MainActivity.LANGUAGE_ALTERNATE) {
            detail = new String[]{"NO RATING", "Geen Graffiti.", "Er zijn kleine stickers.", "Er zijn grote stickers.", "Er zijn posters of tekeningen.", "Rassistisch of aanstootgevend."};
        }else{
            detail = new String[]{"NO RATING", "No Graffiti.", "There are little stickers.", "There are big stickers.", "There are posters or drawings.", "Racist or offensive"};
        }
        for(int i = 0; i < 6; i++) {
            View card = inflater.inflate(R.layout.rating_detailed_layout, null);
            TextView tv = (TextView) card.findViewById(R.id.description_text);
            tv.setText(detail[i]);
            tv = (TextView) card.findViewById(R.id.percentage_text);
            tv.setText(percentage[i]);
            switch (i){
                case 1:
                    tv.setTextColor(getResources().getColor(R.color.green));
                    card.findViewById(R.id.bar_aa).setVisibility(View.VISIBLE);
                    tv = (TextView) card.findViewById(R.id.rating_text_aa);
                    tv.setTextColor(getResources().getColor(R.color.green));
                    break;
                case 2:
                    tv.setTextColor(getResources().getColor(R.color.green));
                    card.findViewById(R.id.bar_a).setVisibility(View.VISIBLE);
                    tv = (TextView) card.findViewById(R.id.rating_text_a);
                    tv.setTextColor(getResources().getColor(R.color.green));
                    break;
                case 3:
                    tv.setTextColor(getResources().getColor(R.color.blue));
                    card.findViewById(R.id.bar_b).setVisibility(View.VISIBLE);
                    tv = (TextView) card.findViewById(R.id.rating_text_b);
                    tv.setTextColor(getResources().getColor(R.color.blue));
                    break;
                case 4:
                    tv.setTextColor(getResources().getColor(R.color.yellow));
                    card.findViewById(R.id.bar_c).setVisibility(View.VISIBLE);
                    tv = (TextView) card.findViewById(R.id.rating_text_c);
                    tv.setTextColor(getResources().getColor(R.color.yellow));
                    break;
                case 5:
                    tv.setTextColor(getResources().getColor(R.color.red));
                    card.findViewById(R.id.bar_d).setVisibility(View.VISIBLE);
                    tv = (TextView) card.findViewById(R.id.rating_text_d);
                    tv.setTextColor(getResources().getColor(R.color.red));
                    break;
            }


            mCards.add(card);
        }
    }

    private void openInstructions() {
        Intent intent = new Intent(this, InstructionsActivity.class);
        int position = mCardScroller.getSelectedItemPosition();
        intent.putExtra(EXTRA_POSITION, position);
        startActivity(intent);
    }

}