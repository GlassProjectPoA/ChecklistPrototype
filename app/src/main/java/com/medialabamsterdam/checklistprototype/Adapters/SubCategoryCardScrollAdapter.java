package com.medialabamsterdam.checklistprototype.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.glass.widget.CardBuilder;
import com.google.android.glass.widget.CardScrollAdapter;
import com.medialabamsterdam.checklistprototype.ContainerClasses.SubCategory;
import com.medialabamsterdam.checklistprototype.R;

import java.util.List;

/**
 * CardScrollAdapter used to display the SubCategory cards at the SubCategoryActivity.
 * <p>
 * Created by
 * Jose Carlos Quintas Junior
 * juniorquintas@gmail.com
 * on 14/04/2015.
 */
public class SubCategoryCardScrollAdapter extends CardScrollAdapter {
    private final List<SubCategory> mCards;
    private final Context mContext;
    private final String mParentCategoryName;

    /**
     * Default constructor.
     *
     * @param context            the activity's context.
     * @param subCategories      a List containing all SubCategory objects that should be depicted on
     *                           the view.
     * @param parentCategoryName the parent Category's name.
     */
    public SubCategoryCardScrollAdapter(Context context, List<SubCategory> subCategories,
                                        String parentCategoryName) {
        mCards = subCategories;
        mContext = context;
        mParentCategoryName = parentCategoryName;
        //Adds an empty SubCategory at the end in order to display the check marks card.
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
        // The current layout in order to inflate and put our own layout inside.
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View card;
        TextView tv;
        SubCategory sc = mCards.get(position);
        // If the SubCategory is the last on the array, turn it into a check_layout card.
        if (position == mCards.size() - 1) {
            //Create Check card at the end of the array
            card = inflater.inflate(R.layout.check_layout, null);
            tv = (TextView) card.findViewById(R.id.footer);
            tv.setText(R.string.tap_to_save);
            tv = (TextView) card.findViewById(R.id.title);
            tv.setText(R.string.category_finish);
            ImageView iv = (ImageView) card.findViewById(R.id.check);
            iv.setImageResource(R.drawable.check);
            iv.setColorFilter(mContext.getResources().getColor(R.color.blue));
        } else {
            // If it's not the last on the array, create the appropriate card to show.
            card = inflater.inflate(R.layout.subcategory_layout, null);
            tv = (TextView) card.findViewById(R.id.category_title);
            tv.setText(sc.getName());
            tv = (TextView) card.findViewById(R.id.category);
            tv.setText(mParentCategoryName);
            // Gets the SubCategory's grade and changes the texts and colors of the card accordingly.
            int rate = sc.getGrade();
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
        // Makes left arrow invisible if the card is the first one.
        if (position == 0) {
            tv = (TextView) card.findViewById(R.id.left_arrow);
            tv.setVisibility(View.INVISIBLE);
        }
        return card;
    }
}