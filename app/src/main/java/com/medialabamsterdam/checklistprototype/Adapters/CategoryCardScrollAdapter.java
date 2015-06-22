package com.medialabamsterdam.checklistprototype.Adapters;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.glass.widget.CardBuilder;
import com.google.android.glass.widget.CardScrollAdapter;
import com.medialabamsterdam.checklistprototype.ContainerClasses.Category;
import com.medialabamsterdam.checklistprototype.R;
import com.medialabamsterdam.checklistprototype.Utilities.Status;

import java.util.List;

/**
 * CardScrollAdapter used to display the Category cards at the CategoryActivity.
 * <p>
 * Created by
 * Jose Carlos Quintas Junior
 * juniorquintas@gmail.com
 * on 23/04/2015.
 */
public class CategoryCardScrollAdapter extends CardScrollAdapter {
    private final List<Category> mCards;
    private final Context mContext;
    private Status mStatus;

    /**
     * Default constructor.
     *
     * @param context    the activity's context.
     * @param categories a List containing all Category objects that should be depicted on the view.
     */
    public CategoryCardScrollAdapter(Context context, List<Category> categories, Status status) {
        super();
        this.mCards = categories;
        this.mContext = context;
        this.mStatus = status;
        //Adds an empty Category at the end in order to display the check marks card.
        this.mCards.add(new Category());
    }

    /**
     * This method is called from an Activity in order to update the contents of the last card as
     * to give feedback to the user.
     *
     * @param status one of the values contained in the Status enum.
     */
    public void updateStatus(Status status) {
        this.mStatus = status;
        this.notifyDataSetChanged();
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

        Category c = mCards.get(position);

        // If the Category is the last on the array, turn it into a check_layout card.
        if (position == mCards.size() - 1) {
            //Create Check card at the end of the array
            card = inflater.inflate(R.layout.check_layout, null);

            String title = null;
            String footer = null;
            Drawable check = null;
            boolean load = false;
            boolean hideArrow = false;
            int color = -1;

            switch (mStatus) {
                case UPLOAD_COMPLETE:
                    title = mContext.getResources().getString(R.string.complete);
                    footer = mContext.getResources().getString(R.string.send_complete);
                    check = mContext.getResources().getDrawable(R.drawable.check);
                    color = mContext.getResources().getColor(R.color.green);
                    hideArrow = true;
                    break;
                case FAIL_SEND:
                    title = mContext.getResources().getString(R.string.incomplete);
                    footer = mContext.getResources().getString(R.string.send_failed);
                    check = mContext.getResources().getDrawable(R.drawable.stop);
                    color = mContext.getResources().getColor(R.color.red);
                    hideArrow = true;
                    break;
                case FAIL_CONNECT:
                    title = mContext.getResources().getString(R.string.could_not_connect);
                    footer = mContext.getResources().getString(R.string.request_failed);
                    check = mContext.getResources().getDrawable(R.drawable.stop);
                    color = mContext.getResources().getColor(R.color.red);
                    hideArrow = true;
                    break;
                case UPLOADING:
                    title = mContext.getResources().getString(R.string.upload_list);
                    footer = mContext.getResources().getString(R.string.please_wait);
                    color = mContext.getResources().getColor(R.color.green);
                    load = true;
                    hideArrow = true;
                    break;
                case SAVING_PICTURE:
                    title = mContext.getResources().getString(R.string.saving_picture);
                    footer = mContext.getResources().getString(R.string.please_wait);
                    color = mContext.getResources().getColor(R.color.yellow);
                    load = true;
                    hideArrow = true;
                    break;
                case CAN_SEND:
                    title = mContext.getResources().getString(R.string.checklist_finish);
                    footer = mContext.getResources().getString(R.string.tap_to_send);
                    check = mContext.getResources().getDrawable(R.drawable.upload);
                    color = mContext.getResources().getColor(R.color.green);
                    break;
                case CATEGORY_COMPLETE:
                    title = mContext.getResources().getString(R.string.checklist_finish);
                    footer = mContext.getResources().getString(R.string.tap_to_check);
                    check = mContext.getResources().getDrawable(R.drawable.upload);
                    color = mContext.getResources().getColor(R.color.yellow);
                    break;
                case CATEGORY_INCOMPLETE:
                    title = mContext.getResources().getString(R.string.checklist_not_finish);
                    footer = mContext.getResources().getString(R.string.tap_to_not_complete);
                    check = mContext.getResources().getDrawable(R.drawable.upload);
                    color = mContext.getResources().getColor(R.color.red);
                    break;
            }
            if (load) {
                card.findViewById(R.id.check).setVisibility(View.GONE);
                ProgressBar spinner = (ProgressBar) card.findViewById(R.id.progressBar_check);
                spinner.setVisibility(View.VISIBLE);
                spinner.getIndeterminateDrawable().mutate().setColorFilter(color, PorterDuff.Mode.MULTIPLY);
            } else {
                card.findViewById(R.id.progressBar_check).setVisibility(View.GONE);
                ImageView iv = (ImageView) card.findViewById(R.id.check);
                iv.setVisibility(View.VISIBLE);
                iv.setImageDrawable(check);
                iv.setColorFilter(color);
            }
            if (hideArrow) {
                card.findViewById(R.id.left_arrow).setVisibility(View.INVISIBLE);
            }
            tv = (TextView) card.findViewById(R.id.title);
            tv.setText(title);
            tv = (TextView) card.findViewById(R.id.footer);
            tv.setText(footer);
        } else {
            // If it's not the last on the array, create the appropriate card to show.
            card = inflater.inflate(R.layout.categories_layout, null);
            tv = (TextView) card.findViewById(R.id.category_title);
            tv.setText(c.getName());
            tv = (TextView) card.findViewById(R.id.order);
            tv.setText(Integer.toString(position + 1));

            // Makes the circle that is shown in the Category card green if the Category is marked
            // as complete.
            String footer;
            ImageView iv;
            Drawable statusDrawable;
            int color;

            if (c.isComplete()) {
                statusDrawable = mContext.getResources().getDrawable(R.drawable.category_complete);
                color = mContext.getResources().getColor(R.color.green);
                footer = mContext.getResources().getString(R.string.category_complete);
            } else if (c.isSkip()) {
                statusDrawable = mContext.getResources().getDrawable(R.drawable.category_default);
                color = mContext.getResources().getColor(R.color.yellow);
                footer = mContext.getResources().getString(R.string.category_skipped);
            } else {
                statusDrawable = mContext.getResources().getDrawable(R.drawable.category_default);
                color = mContext.getResources().getColor(R.color.white);
                footer = mContext.getResources().getString(R.string.tap_to_grade);
            }
            iv = (ImageView) card.findViewById(R.id.category_status);
            iv.setImageDrawable(statusDrawable);
            iv.setColorFilter(color);
            tv = (TextView) card.findViewById(R.id.order);
            tv.setTextColor(color);
            tv = (TextView) card.findViewById(R.id.footer);
            tv.setText(footer);
        }
        // Makes left arrow invisible if the card is the first one.
        if (position == 0) {
            tv = (TextView) card.findViewById(R.id.left_arrow);
            tv.setVisibility(View.INVISIBLE);
        }
        return card;
    }

}
