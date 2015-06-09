package com.medialabamsterdam.checklistprototype.Adapters;

import android.view.View;
import android.view.ViewGroup;

import com.google.android.glass.widget.CardBuilder;
import com.google.android.glass.widget.CardScrollAdapter;

import java.util.List;

/**
 * Generic CardScrollAdapter to use on regular views.
 * <p>
 * Created by
 * Jose Carlos Quintas Junior
 * juniorquintas@gmail.com
 * on 14/04/2015.
 */
public class MyCardScrollAdapter extends CardScrollAdapter {
    private final List<View> mViews;

    public MyCardScrollAdapter(List<View> views) {
        mViews = views;
    }

    @Override
    public int getPosition(Object item) {
        return mViews.indexOf(item);
    }

    @Override
    public int getCount() {
        return mViews.size();
    }

    @Override
    public Object getItem(int position) {
        return mViews.get(position);
    }


    @Override
    public int getViewTypeCount() {
        return CardBuilder.getViewTypeCount();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return mViews.get(position);
    }

}