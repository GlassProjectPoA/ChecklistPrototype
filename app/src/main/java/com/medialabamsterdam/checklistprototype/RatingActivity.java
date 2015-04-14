package com.medialabamsterdam.checklistprototype;

import com.google.android.glass.media.Sounds;
import com.google.android.glass.touchpad.Gesture;
import com.google.android.glass.touchpad.GestureDetector;
import com.google.android.glass.widget.CardScrollView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class RatingActivity extends Activity {

    private static final String TAG = "THIS";
    private CardScrollView mCardScroller;
    private GestureDetector mGestureDetector;
    private ArrayList<SubCategory> mSubCatViews;
    private SubCategoryCardScrollAdapter mAdapter;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        createCards();

        mCardScroller = new CardScrollView(this);
        mAdapter = new SubCategoryCardScrollAdapter(this, mSubCatViews);
        mCardScroller.setAdapter(mAdapter);
        mCardScroller.setFocusable(false);
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
        gestureDetector.setBaseListener(new GestureDetector.BaseListener() {
            @Override
            public boolean onGesture(Gesture gesture) {
                Log.e(TAG, "gesture = " + gesture);
                int pos;
                switch (gesture) {
                    case TAP:
                        Log.e(TAG, "TAP called.");
                        openRatingDetailed();
                        AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                        am.playSoundEffect(Sounds.TAP);
                        break;
                    case SWIPE_LEFT:
                        Log.e(TAG, "SWIPE_LEFT called.");
                        pos = mCardScroller.getSelectedItemPosition();
                        mCardScroller.setSelection(pos-1);
                        mCardScroller.startAnimation(AnimationUtils.makeInAnimation(mCardScroller.getContext(), true));
                        return true;
                    case SWIPE_RIGHT:
                        Log.e(TAG, "SWIPE_RIGHT called.");
                        pos = mCardScroller.getSelectedItemPosition();
                        mCardScroller.setSelection(pos+1);
                        mCardScroller.startAnimation(AnimationUtils.makeInAnimation(mCardScroller.getContext(), false));
                        return true;
                    case SWIPE_DOWN:
                        Log.e(TAG, "SWIPE_DOWN called.");
                        finish();
                        return true;
                    case TWO_SWIPE_LEFT:
                        Log.e(TAG, "TWO_SWIPE_LEFT called.");
                        changeRating(false);
                        return true;
                    case TWO_SWIPE_RIGHT:
                        Log.e(TAG, "TWO_SWIPE_RIGHT called.");
                        changeRating(true);
                        return true;
                }
                return false;
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
        mSubCatViews = new ArrayList<>();
        Intent intent = getIntent();
        int position = intent.getIntExtra(CategoriesActivity.EXTRA_POSITION,404);
        LayoutInflater inflater = LayoutInflater.from(this);
        String[] problems = getResources().getStringArray(Utils.getResourceId(this, "problems_"+position, "array", getPackageName()));
        String[] categories = getResources().getStringArray(R.array.categories_list);
        int index = 0;
        for(String str : problems) {
            SubCategory subCategory = new SubCategory(position, categories[position], index, str, -1);
            index++;
            mSubCatViews.add(subCategory);
        }
        //region TODO remember to check
        View card = inflater.inflate(R.layout.summary_layout, null);
        LinearLayout ll = (LinearLayout)card.findViewById(R.id.list_container);
        LayoutInflater inflater_summary = (LayoutInflater)this.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        int i =0;
        for(String str2 : problems) {
            i++;
            //TODO Remove IF, create a scrollable list.
            if (i < 5){
                View view = inflater_summary.inflate(R.layout.summary_list_item_layout, null);
                TextView tv = (TextView) view.findViewById(R.id.summary_order);
                tv.setText(i + ". ");
                tv = (TextView) view.findViewById(R.id.summary_category);
                tv.setText(str2);
                //tv = (TextView)view.findViewById(R.id.summary_rating);
                ll.addView(view);
            }
        }
        //mSubCatViews.add(card);
        //endregion
    }

    private void changeRating(boolean add){
        int position = mCardScroller.getSelectedItemPosition();
        int rating = mSubCatViews.get(position).getCurrentRating();
        if(add && rating<=3){
            mSubCatViews.get(position).setCurrentRating(rating+1);
        }
        else if(!add && rating>=-1){
            mSubCatViews.get(position).setCurrentRating(rating-1);
        }
        mAdapter.notifyDataSetChanged();
    }

    private void openRatingDetailed() {
        Intent intent = new Intent(this, RatingDetailedActivity.class);
        int position = mCardScroller.getSelectedItemPosition();
        //intent.putExtra(EXTRA_POSITION, position);
        startActivity(intent);
    }

}