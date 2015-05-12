package com.medialabamsterdam.checklistprototype.Utilities;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.medialabamsterdam.checklistprototype.Polygon_contains_Point.Point;

/**
 * Created by
 * Jose Carlos Quintas Junior
 * juniorquintas@gmail.com
 * on 13/04/2015.
 */
public class Utils {

    /**
     * Utility class to convert a formatted string to an array.
     *
     * @param string    String to convert.
     * @param separator String to look for when splitting the string.
     * @return String[], containing the separated strings.
     */
    public static String[] stringToArray(String string, String separator) {
        if (string == null) return null;
        if (string.contains(separator)) {
            //string = string.replace("[", "").replace("]","");
            return string.split(separator);
        } else {
            Log.e("StringToArray", "String '" + string + "' does not contain " + separator);
            return new String[]{"Unable to decode string"};
        }
    }

    /**
     * @param context       the context of the Resource you want to retrieve the Id.
     * @param pVariableName the name property of the Resource you want to retrieve the Id.
     * @param pResourcename the type of Resource you want to retrieve. (Ex: "string", "array", "layout")
     * @param pPackageName  the package the Resource belongs to. (Use getPackageName() in most cases)
     * @return the ID of the given resource. Returns -1 if Resource not found.
     */
    public static int getResourceId(Context context, String pVariableName, String pResourcename, String pPackageName) {
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
     * @param context       the context of the TextView.
     * @param viewContainer the View of the TextView you wish to change.
     * @param viewId        the Android Resources Id of the TextView to change.
     * @param stringId      the Android String Id of the TextView text.
     * @param start         the start of the region to change color.
     * @param end           the end of the region to change color.
     * @param colorId       the Android Resource Color Id.
     */
    public static void ChangeTextColor(Context context, View viewContainer, int viewId, int stringId, int start, int end, int colorId) {
        try {
            TextView tv = (TextView) viewContainer.findViewById(viewId);
            tv.setText(stringId, TextView.BufferType.SPANNABLE);
            Spannable s = (Spannable) tv.getText();
            s.setSpan(new ForegroundColorSpan(Color.parseColor(context.getResources().getString(colorId))), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Method to change the color of a word or string inside a given TextView.
     *
     * @param context       the context of the TextView.
     * @param viewContainer the View of the TextView you wish to change.
     * @param viewId        the Android Resources Id of the TextView to change.
     * @param stringId      the Android String Id of the TextView text.
     * @param wordToChange  the word to change the color.
     * @param colorId       the Android Resource Color Id.
     */
    public static void ChangeTextColor(Context context, View viewContainer, int viewId, int stringId, String wordToChange, int colorId) {
        try {
            TextView tv = (TextView) viewContainer.findViewById(viewId);
            tv.setText(stringId, TextView.BufferType.SPANNABLE);
            Spannable s = (Spannable) tv.getText();
            String str = s.toString();
            int start = str.indexOf(wordToChange);
            int end = start + wordToChange.length();
            s.setSpan(new ForegroundColorSpan(Color.parseColor(context.getResources().getString(colorId))), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Method to change the color of a word or string inside a given TextView.
     *
     * @param context       the context of the TextView.
     * @param viewContainer the View of the TextView you wish to change.
     * @param viewId        the Android Resources Id of the TextView to change.
     * @param stringId      the Android String-Array Id of the TextView text containing the phrase on the first item and the "string" to be changed on the second item.
     * @param colorId       the Android Resource Color Id.
     */
    public static void ChangeTextColor(Context context, View viewContainer, int viewId, int stringId, int colorId) {
        try {
            String[] str_array = context.getResources().getStringArray(stringId);
            TextView tv = (TextView) viewContainer.findViewById(viewId);
            tv.setText(str_array[0], TextView.BufferType.SPANNABLE);
            Spannable s = (Spannable) tv.getText();
            String str = s.toString();
            int start = str.indexOf(str_array[1]);
            int end = start + str_array[1].length();
            s.setSpan(new ForegroundColorSpan(Color.parseColor(context.getResources().getString(colorId))), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Method to transform a "Location" string into a Point.
     *
     * @param string a String formatted as "52.3588929,4.9081412"
     * @return a Point object.
     */
    public static Point stringToPoint (String string){
        String[] strArray = string.split(",");
        return new Point(Double.parseDouble(strArray[0]), Double.parseDouble(strArray[1]));
    }

}
