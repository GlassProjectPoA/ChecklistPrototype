package com.medialabamsterdam.checklistprototype.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.glass.widget.CardBuilder;
import com.google.android.glass.widget.CardScrollAdapter;
import com.medialabamsterdam.checklistprototype.ContainerClasses.Category;
import com.medialabamsterdam.checklistprototype.R;

import java.util.List;

/**
 * Created by
 * Jose Carlos Quintas Junior
 * juniorquintas@gmail.com
 * on 23/04/2015.
 */
public class CategoryCardScrollAdapter extends CardScrollAdapter {
    private final List<Category> mCards;
    private final Context mContext;

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
        int completion = 0;
        Category c = mCards.get(position);
        for (Category category : mCards) {
            if (category.isCompleted()) completion++;
        }
        if (position == mCards.size() - 1) {
            //Create Check card at the end of the array
            card = inflater.inflate(R.layout.check_layout, null);
            ImageView iv = (ImageView) card.findViewById(R.id.check);
            if (completion == mCards.size() - 1) {
                iv.setColorFilter(mContext.getResources().getColor(R.color.green));
                tv = (TextView) card.findViewById(R.id.footer);
                tv.setText(R.string.tap_to_send);
                tv = (TextView) card.findViewById(R.id.title);
                tv.setText(R.string.checklist_finish);
            } else {
                iv.setColorFilter(mContext.getResources().getColor(R.color.red));
                tv = (TextView) card.findViewById(R.id.footer);
                tv.setText(R.string.tap_to_not_complete);
                tv = (TextView) card.findViewById(R.id.title);
                tv.setText(R.string.checklist_not_finish);
            }
        } else {
            card = inflater.inflate(R.layout.categories_layout, null);
            tv = (TextView) card.findViewById(R.id.category_title);
            tv.setText(c.getName());
            tv = (TextView) card.findViewById(R.id.order);
            tv.setText(Integer.toString(position + 1));
        }
        if (position == 0) {
            tv = (TextView) card.findViewById(R.id.left_arrow);
            tv.setTextColor(mContext.getResources().getColor(R.color.gray_dark));
        }
        if (c.isCompleted()) {
            LinearLayout ll = (LinearLayout) card.findViewById(R.id.bg_img_container);
            ll.setBackground(mContext.getResources().getDrawable(R.drawable.categories_background_green));
            tv = (TextView) card.findViewById(R.id.order);
            tv.setTextColor(mContext.getResources().getColor(R.color.green));
        }
        return card;
    }

}
