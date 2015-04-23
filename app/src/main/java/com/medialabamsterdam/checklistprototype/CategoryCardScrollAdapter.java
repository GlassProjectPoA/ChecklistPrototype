package com.medialabamsterdam.checklistprototype;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.glass.widget.CardBuilder;
import com.google.android.glass.widget.CardScrollAdapter;

import java.util.List;

/**
 * Created by Quintas on 23/04/2015.
 */
public class CategoryCardScrollAdapter extends CardScrollAdapter {
    private List<Category> mCards;
    private Context mContext;

    public CategoryCardScrollAdapter(Context context, List<Category> views) {
        mCards = views;
        mContext = context;
        mCards.add(new Category());
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
        Category c = mCards.get(position);
        if (position == mCards.size() - 1) {
            card = inflater.inflate(R.layout.card_layout, null);
            tv = (TextView) card.findViewById(R.id.footer);
            tv.setText(R.string.tap_to_send);
            card.findViewById(R.id.layout).setBackgroundColor(mContext.getResources().getColor(R.color.green));
        } else {
            card = inflater.inflate(R.layout.categories_layout, null);
            tv = (TextView) card.findViewById(R.id.category_title);
            tv.setText(c.getCategoryName());
            tv = (TextView) card.findViewById(R.id.order);
            tv.setText(Integer.toString(position + 1));
        }
        if (position == 0) {
            tv = (TextView) card.findViewById(R.id.left_arrow);
            tv.setTextColor(mContext.getResources().getColor(R.color.gray_dark));
        }
        return card;
    }

}
