package com.medialabamsterdam.checklistprototype;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.glass.widget.CardBuilder;
import com.google.android.glass.widget.CardScrollAdapter;

import java.util.List;

/**
 * Created by Quintas on 14/04/2015.
 */
public class SubCategoryCardScrollAdapter extends CardScrollAdapter {
    private List<SubCategory> mCards;
    private Context mContext;

    public SubCategoryCardScrollAdapter(Context context, List<SubCategory> views) {
        mCards = views;
        mContext = context;
        mCards.add(new SubCategory());
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
        SubCategory sc = mCards.get(position);
        if (position == mCards.size() - 1) {
            card = inflater.inflate(R.layout.check_layout, null);
            tv = (TextView) card.findViewById(R.id.footer);
            tv.setText(R.string.tap_to_save);
            tv = (TextView) card.findViewById(R.id.title);
            tv.setText(R.string.category_finish);
            ImageView iv = (ImageView)card.findViewById(R.id.check);
            iv.setImageResource(R.drawable.check_blue);
        } else {
            card = inflater.inflate(R.layout.rating_layout, null);
            tv = (TextView) card.findViewById(R.id.category_title);
            tv.setText(sc.getThisSubCategoryName());
            tv = (TextView) card.findViewById(R.id.category);
            tv.setText(sc.getParentCategoryName());
            int rate = sc.getCurrentRating();
            switch (rate) {
                case -1:
                    break;
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
        }

        /*if(position == 0 && mCards.size() == 1){
            tv = (TextView) card.findViewById(R.id.left_arrow);
            tv.setTextColor(mContext.getResources().getColor(R.color.gray_dark));
            tv = (TextView) card.findViewById(R.id.right_arrow);
            tv.setTextColor(mContext.getResources().getColor(R.color.gray_dark));
        } else*/
        if (position == 0) {
            tv = (TextView) card.findViewById(R.id.left_arrow);
            tv.setTextColor(mContext.getResources().getColor(R.color.gray_dark));
        } /*else if (position == mCards.size()-1){
            tv = (TextView) card.findViewById(R.id.right_arrow);
            tv.setTextColor(mContext.getResources().getColor(R.color.gray_dark));
        }*/

        return card;
    }

}