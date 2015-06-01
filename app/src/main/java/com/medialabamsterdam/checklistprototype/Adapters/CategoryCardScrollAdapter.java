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
 * CardScrollAdapter used to display the Category cards at the CategoryActivity.
 * <p/>
 * Created by
 * Jose Carlos Quintas Junior
 * juniorquintas@gmail.com
 * on 23/04/2015.
 */
public class CategoryCardScrollAdapter extends CardScrollAdapter {
    private final List<Category> mCards;
    private final Context mContext;

    /**
     * Default constructor.
     *
     * @param context    the activity's context.
     * @param categories a List containing all Category objects that should be depicted on the view.
     */
    public CategoryCardScrollAdapter(Context context, List<Category> categories) {
        mCards = categories;
        mContext = context;
        //Adds an empty Category at the end in order to display the check marks card.
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
        // The current layout in order to inflate and put our own layout inside.
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View card;
        TextView tv;
        int completion = 0;
        Category c = mCards.get(position);
        for (Category category : mCards) {
            if (category.isCompleted()) completion++;
        }
        // If the Category is the last on the array, turn it into a check_layout card.
        if (position == mCards.size() - 1) {
            //Create Check card at the end of the array
            card = inflater.inflate(R.layout.check_layout, null);

            String footer;
            String title;

            ImageView iv = (ImageView) card.findViewById(R.id.check);
            // Sets the message and color of the check mark in the card to inform the user if there
            // are any Categories he missed.
            if (completion == mCards.size() - 1) {
                iv.setColorFilter(mContext.getResources().getColor(R.color.yellow));
                footer = mContext.getResources().getString(R.string.tap_to_check);
                title = mContext.getResources().getString(R.string.checklist_finish);
            } else
            if (completion > 0) {
                iv.setColorFilter(mContext.getResources().getColor(R.color.yellow));
                footer = mContext.getResources().getString(R.string.tap_to_skip);
                title = mContext.getResources().getString(R.string.checklist_skip);
            } else {
                iv.setColorFilter(mContext.getResources().getColor(R.color.red));
                footer = mContext.getResources().getString(R.string.tap_to_not_complete);
                title = mContext.getResources().getString(R.string.checklist_not_finish);
            }
            tv = (TextView) card.findViewById(R.id.footer);
            tv.setText(footer);
            tv = (TextView) card.findViewById(R.id.title);
            tv.setText(title);
        } else {
            // If it's not the last on the array, create the appropriate card to show.
            card = inflater.inflate(R.layout.categories_layout, null);
            tv = (TextView) card.findViewById(R.id.category_title);
            tv.setText(c.getName());
            tv = (TextView) card.findViewById(R.id.order);
            tv.setText(Integer.toString(position + 1));
        }
        // Makes left arrow invisible if the card is the first one.
        if (position == 0) {
            tv = (TextView) card.findViewById(R.id.left_arrow);
            tv.setVisibility(View.INVISIBLE);
        }
        // Makes the circle that is shown in the Category card green if the Category is marked
        // as complete.
        if (c.isCompleted()) {
            LinearLayout ll = (LinearLayout) card.findViewById(R.id.bg_img_container);
            ll.setBackground(mContext.getResources().getDrawable(R.drawable.categories_background_green));
            tv = (TextView) card.findViewById(R.id.order);
            tv.setTextColor(mContext.getResources().getColor(R.color.green));
        }
        return card;
    }

}
