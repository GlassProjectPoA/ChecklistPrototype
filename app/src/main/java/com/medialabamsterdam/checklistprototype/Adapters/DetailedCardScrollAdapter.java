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

import java.util.ArrayList;
import java.util.List;

/**
 * Created by
 * Jose Carlos Quintas Junior
 * juniorquintas@gmail.com
 * on 12/05/2015.
 */
public class DetailedCardScrollAdapter extends CardScrollAdapter {
    private final List<Detail> mCards;
    private final Context mContext;
    private final Detail mDetail;

    public DetailedCardScrollAdapter(Context context, Detail views) {
        mCards = new ArrayList<>();
        mContext = context;
        mDetail = views;
        for (int i = 0; i < 5; i++) mCards.add(views);
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
        card = inflater.inflate(R.layout.details_layout, null);
        tv = (TextView) card.findViewById(R.id.description_text);
        tv.setText(mDetail.getDescription()[position]);
        tv = (TextView) card.findViewById(R.id.detail_text);
        tv.setText(mDetail.getDetails());
        switch (position) {
            case 0:
                tv = (TextView) card.findViewById(R.id.rating_text_aa);
                tv.setTextColor(mContext.getResources().getColor(R.color.green));
                card.findViewById(R.id.bar_aa).setVisibility(View.VISIBLE);
                break;
            case 1:
                tv = (TextView) card.findViewById(R.id.rating_text_a);
                tv.setTextColor(mContext.getResources().getColor(R.color.green));
                card.findViewById(R.id.bar_a).setVisibility(View.VISIBLE);
                break;
            case 2:
                tv = (TextView) card.findViewById(R.id.rating_text_b);
                tv.setTextColor(mContext.getResources().getColor(R.color.yellow));
                card.findViewById(R.id.bar_b).setVisibility(View.VISIBLE);
                break;
            case 3:
                tv = (TextView) card.findViewById(R.id.rating_text_c);
                tv.setTextColor(mContext.getResources().getColor(R.color.orange));
                card.findViewById(R.id.bar_c).setVisibility(View.VISIBLE);
                break;
            case 4:
                tv = (TextView) card.findViewById(R.id.rating_text_d);
                tv.setTextColor(mContext.getResources().getColor(R.color.red));
                card.findViewById(R.id.bar_d).setVisibility(View.VISIBLE);
                break;
        }
        return card;
    }
}
