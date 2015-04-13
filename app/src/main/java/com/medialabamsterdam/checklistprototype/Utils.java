package com.medialabamsterdam.checklistprototype;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

/**
 * Created by Quintas on 13/04/2015.
 */
public class Utils {

    public static int getResourceId(Context context, String pVariableName, String pResourcename, String pPackageName)
    {
        try {
            return context.getResources().getIdentifier(pVariableName, pResourcename, pPackageName);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * Method to change the color of a word or string inside a given TextView.
     *
     * @param context the context of the TextView.
     * @param viewContainer the View of the TextView you wish to change.
     * @param viewId the Android Resources Id of the TextView to change.
     * @param stringId the Android String Id of the TextView text.
     * @param start the start of the region to change color.
     * @param end the end of the region to change color.
     * @param colorId the Android Resource Color Id.
     */
    public static void ChangeTextColor(Context context, View viewContainer, int viewId, int stringId, int start, int end, int colorId){
        TextView tv = (TextView)viewContainer.findViewById(viewId);
        tv.setText(stringId, TextView.BufferType.SPANNABLE);
        Spannable s = (Spannable)tv.getText();
        s.setSpan(new ForegroundColorSpan(Color.parseColor(context.getResources().getString(colorId))), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    /**
     * Method to change the color of a word or string inside a given TextView.
     *
     * @param context the context of the TextView.
     * @param viewContainer the View of the TextView you wish to change.
     * @param viewId the Android Resources Id of the TextView to change.
     * @param stringId the Android String Id of the TextView text.
     * @param wordToChange the word to change the color.
     * @param colorId the Android Resource Color Id.
     */
    public static void ChangeTextColor(Context context, View viewContainer, int viewId, int stringId, String wordToChange, int colorId){
        TextView tv = (TextView)viewContainer.findViewById(viewId);
        tv.setText(stringId, TextView.BufferType.SPANNABLE);
        Spannable s = (Spannable)tv.getText();
        String str = s.toString();
        int start = str.indexOf(wordToChange);
        int end = start+wordToChange.length();
        s.setSpan(new ForegroundColorSpan(Color.parseColor(context.getResources().getString(colorId))), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    }
}
