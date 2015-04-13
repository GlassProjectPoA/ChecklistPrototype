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
import android.media.AudioManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class RatingActivity extends Activity {

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
        gestureDetector.setBaseListener( new GestureDetector.BaseListener() {
            @Override
            public boolean onGesture(Gesture gesture) {
                if (gesture == Gesture.TAP) {
//                    openInstructions();
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
        Intent intent = getIntent();
        int position = intent.getIntExtra(CategoriesActivity.EXTRA_POSITION,404);
        LayoutInflater inflater = LayoutInflater.from(this);
        String[] strs = getResources().getStringArray(Utils.getResourceId(this, "problems_"+position, "array", getPackageName()));
        for(String str : strs) {
            View card = inflater.inflate(R.layout.rating_layout, null);
            TextView tv = (TextView)card.findViewById(R.id.rating_title);
            tv.setText(str);
            String[] n = getResources().getStringArray(R.array.categories_list);
            tv = (TextView)card.findViewById(R.id.category);
            tv.setText(n[position]);
            mCards.add(card);
        }
//        View card = inflater.inflate(R.layout.summary_layout, null);
//        LayoutInflater inflater_summary = LayoutInflater.fro
    }

    private class MyCardScrollAdapter extends CardScrollAdapter {
        private List<View> mViews;

        public MyCardScrollAdapter(List<View> views) {
            mViews = views;
        }

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
            return mViews.get(position);
        }

    }

//    private void openInstructions() {
//        Intent intent = new Intent(this, InstructionsActivity.class);
//        int position = mCardScroller.getSelectedItemPosition();
//        intent.putExtra(EXTRA_POSITION, position);
//        startActivity(intent);
//    }

}