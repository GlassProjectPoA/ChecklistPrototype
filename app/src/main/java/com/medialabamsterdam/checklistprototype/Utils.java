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

    public static void ChangeTextColor(Context context, View viewContainer, int viewId, int stringId, int start, int end, int colorId){
        TextView tv = (TextView)viewContainer.findViewById(viewId);
        tv.setText(stringId, TextView.BufferType.SPANNABLE);
        Spannable s = (Spannable)tv.getText();
        s.setSpan(new ForegroundColorSpan(Color.parseColor(context.getResources().getString(colorId))), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

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
