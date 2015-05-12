package com.medialabamsterdam.checklistprototype.Adapters;

import android.content.Context;
import android.util.Log;
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
 * Created by
 * Jose Carlos Quintas Junior
 * juniorquintas@gmail.com
 * on 12/05/2015.
 */
public class DetailedExtendedCardScrollAdapter extends CardScrollAdapter {
    private final List<Detail> mCards;
    private final Context mContext;
    private final Detail mDetail;

    public DetailedExtendedCardScrollAdapter(Context context, Detail views) {
        mCards = new ArrayList<>();
        mContext = context;
        mDetail = views;
        for (int i = 0; i < 5; i++) mCards.add(views);
        Log.d("Check = ", "" + mCards.toString());
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
        int color = 0;
        card = inflater.inflate(R.layout.details_extended_layout, null);

        String title1;
        String title2 = null;
        String rating1;
        String rating2 = null;

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

        tv = (TextView) card.findViewById(R.id.title_text_1);
        tv.setText(title1);
        tv = (TextView) card.findViewById(R.id.rating_text_1);
        tv.setText(rating1);
        tv.setTextColor(color);

        if (title2 != null && rating2 != null) {
            tv = (TextView) card.findViewById(R.id.title_text_2);
            tv.setText(title2);
            tv = (TextView) card.findViewById(R.id.rating_text_2);
            tv.setText(rating2);
            tv.setTextColor(color);
        } else {
            tv = (TextView) card.findViewById(R.id.title_text_2);
            tv.setVisibility(View.GONE);
            tv = (TextView) card.findViewById(R.id.rating_text_2);
            tv.setVisibility(View.GONE);
        }
        return card;
    }
}
