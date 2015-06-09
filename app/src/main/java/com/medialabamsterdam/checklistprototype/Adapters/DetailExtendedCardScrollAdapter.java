package com.medialabamsterdam.checklistprototype.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.glass.widget.CardBuilder;
import com.google.android.glass.widget.CardScrollAdapter;
import com.medialabamsterdam.checklistprototype.ContainerClasses.Detail;
import com.medialabamsterdam.checklistprototype.R;
import com.medialabamsterdam.checklistprototype.Utilities.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * CardScrollAdapter used to display the many Ratings descriptions at the DetailActivity.
 * <p>
 * Created by
 * Jose Carlos Quintas Junior
 * juniorquintas@gmail.com
 * on 12/05/2015.
 */
public class DetailExtendedCardScrollAdapter extends CardScrollAdapter {
    private final List<Detail> mCards;
    private final Context mContext;
    private final Detail mDetail;

    /**
     * Default constructor.
     *
     * @param context the activity's context.
     * @param detail  the Detail object to be shown.
     */
    public DetailExtendedCardScrollAdapter(Context context, Detail detail) {
        mCards = new ArrayList<>();
        mContext = context;
        mDetail = detail;
        //Creates four more cards in order to show everything we need to show.
        for (int i = 0; i < 5; i++) mCards.add(detail);
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
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View card;
        TextView tv;
        int color = 0; // Color Resource Id that was used. Saved here in order to reuse it later.
        // Inflates the card layout.
        card = inflater.inflate(R.layout.details_extended_layout, null);
        String title1;
        String title2 = null;
        String rating1;
        String rating2 = null;
        // Sets text based on language to display.
        if (Constants.LOAD_ALTERNATE_LANGUAGE) {
            title1 = mDetail.getTitle_1_nl();
            rating1 = mDetail.getRating_1_nl()[position];
            if (mDetail.getTitle_2_nl() != null) {
                title2 = mDetail.getTitle_2_nl();
                rating2 = mDetail.getRating_2_nl()[position];
            }
        } else {
            title1 = mDetail.getTitle_1();
            rating1 = mDetail.getRating_1()[position];
            if (mDetail.getTitle_2() != null) {
                title2 = mDetail.getTitle_2();
                rating2 = mDetail.getRating_2()[position];
            }
        }

        // Changes grade colors and indicators to inform the selected grade.
        switch (position) {
            case 0:
                color = mContext.getResources().getColor(R.color.green);
                tv = (TextView) card.findViewById(R.id.rating_text_aa);
                tv.setTextColor(color);
                card.findViewById(R.id.bar_aa).setVisibility(View.VISIBLE);
                break;
            case 1:
                color = mContext.getResources().getColor(R.color.green);
                tv = (TextView) card.findViewById(R.id.rating_text_a);
                tv.setTextColor(color);
                card.findViewById(R.id.bar_a).setVisibility(View.VISIBLE);
                break;
            case 2:
                color = mContext.getResources().getColor(R.color.yellow);
                tv = (TextView) card.findViewById(R.id.rating_text_b);
                tv.setTextColor(color);
                card.findViewById(R.id.bar_b).setVisibility(View.VISIBLE);
                break;
            case 3:
                color = mContext.getResources().getColor(R.color.orange);
                tv = (TextView) card.findViewById(R.id.rating_text_c);
                tv.setTextColor(color);
                card.findViewById(R.id.bar_c).setVisibility(View.VISIBLE);
                break;
            case 4:
                color = mContext.getResources().getColor(R.color.red);
                tv = (TextView) card.findViewById(R.id.rating_text_d);
                tv.setTextColor(color);
                card.findViewById(R.id.bar_d).setVisibility(View.VISIBLE);
                break;
        }

        // Displays rating and rating title 1.
        tv = (TextView) card.findViewById(R.id.title_text_1);
        tv.setText(title1);
        tv = (TextView) card.findViewById(R.id.rating_text_1);
        tv.setText(rating1);
        tv.setTextColor(color);

        // If rating2 is not null then it displays them here.
        if (title2 != null && rating2 != null) {
            tv = (TextView) card.findViewById(R.id.title_text_2);
            tv.setText(title2);
            tv = (TextView) card.findViewById(R.id.rating_text_2);
            tv.setText(rating2);
            tv.setTextColor(color);
        } else {
            // Else it will just disable the views.
            tv = (TextView) card.findViewById(R.id.title_text_2);
            tv.setVisibility(View.GONE);
            tv = (TextView) card.findViewById(R.id.rating_text_2);
            tv.setVisibility(View.GONE);
        }
        return card;
    }
}
