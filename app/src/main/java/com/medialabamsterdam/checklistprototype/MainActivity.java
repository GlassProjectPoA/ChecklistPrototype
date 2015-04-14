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
        import android.content.res.Configuration;
        import android.media.AudioManager;
        import android.os.Bundle;
        import android.view.LayoutInflater;
        import android.view.Menu;
        import android.view.MotionEvent;
        import android.view.View;
        import android.view.ViewGroup;
        import android.view.Window;
        import android.view.WindowManager;
        import android.widget.TextView;

        import java.util.ArrayList;
        import java.util.Locale;

public class MainActivity extends Activity {

    public final static String EXTRA_MESSAGE = "com.medialabamsterdam.checklistprototype.MESSAGE";
    private final static String LANGUAGE_TO_LOAD = "nl";
    private final static boolean LANGUAGE_ALTERNATE = false;
    private final static boolean OK_GLASS = false;

    private CardScrollView mCardScroller;
    private View mView;
    private GestureDetector mGestureDetector;
    private ArrayList<View> mCards;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        if(LANGUAGE_ALTERNATE) {
            Locale locale = new Locale(LANGUAGE_TO_LOAD);
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.locale = locale;
            getBaseContext().getResources().updateConfiguration(config,
                    getBaseContext().getResources().getDisplayMetrics());
        }

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        if(OK_GLASS) {
            getWindow().requestFeature(WindowUtils.FEATURE_VOICE_COMMANDS);
        }

        mView = createLocationCard();
        Utils.ChangeTextColor(this, mView, R.id.footer, R.array.tap_to_start, R.color.green);
        Utils.ChangeTextColor(this, mView, R.id.instructions, R.array.tap_two_to_refresh, R.color.blue);

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
        gestureDetector.setBaseListener( new GestureDetector.BaseListener() {
            @Override
            public boolean onGesture(Gesture gesture) {
                if (gesture == Gesture.TAP) {
                    openCategories();
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


    private void openCategories() {
        Intent intent = new Intent(this, CategoriesActivity.class);
        TextView tv = (TextView)this.findViewById(R.id.location_code);
        String message = (String) tv.getText();
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }

    private View createLocationCard(){//List<ChecklistTask> tasks) {
        mCards = new ArrayList<>();
        LayoutInflater inflater = LayoutInflater.from(this);
        View card = inflater.inflate(R.layout.location_layout, null);
        mCards.add(card);
        return mCards.get(0);
    }

}
