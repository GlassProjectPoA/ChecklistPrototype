package com.medialabamsterdam.checklistprototype.Utilities;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.text.Spannable;
import android.text.style.ForegroundColorSpan;
import android.util.Base64;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.TextView;

import com.medialabamsterdam.checklistprototype.Polygon_contains_Point.Point;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

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
    public static Point stringToPoint(String string) {
        String[] strArray = string.split(",");
        return new Point(Double.parseDouble(strArray[0]), Double.parseDouble(strArray[1]));
    }

    /**
     * Method to get a String out of the "int" ratings.
     * 0 = "AA"
     * 1 = "A"
     * 2 = "B"
     * 3 = "C"
     * 4 = "D"
     *
     * @param rating an int 0-4
     * @return a String
     */
    public static String getStringFromRating(int rating) {
        SparseArray<String> _rates = new SparseArray<>();
        _rates.put(0, "AA");
        _rates.put(1, "A");
        _rates.put(2, "B");
        _rates.put(3, "C");
        _rates.put(4, "D");
        return _rates.get(rating);
    }

    /**
     * Method to get an int out of the "String" ratings.
     * "AA" = 0
     * "A" = 1
     * "B" = 2
     * "C" = 3
     * "D" = 4
     *
     * @param rating a String with the rating letter ('A+' should be depicted as 'AA')
     * @return an int
     */
    public static int getRatingFromString(String rating) {
        Map<String, Integer> _rates = new HashMap<>();
        _rates.put("AA", 0);
        _rates.put("A", 1);
        _rates.put("B", 2);
        _rates.put("C", 3);
        _rates.put("D", 4);
        return _rates.get(rating);
    }

    /**
     * Converts an image to a Base64 string in order send it through JSON.
     *
     * @param path the system path to the picture.
     * @return the picture converted into a Base64 String.
     */
    public static String imgToString(String path) {

        BitmapFactory.Options options0 = new BitmapFactory.Options();
        options0.inSampleSize = 2;
        // options.inJustDecodeBounds = true;
        options0.inScaled = false;
        options0.inDither = false;
        options0.inPreferredConfig = Bitmap.Config.ARGB_8888;

        Bitmap bmp = BitmapFactory.decodeFile(path);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        bmp.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);

        byte[] imageBytes0 = byteArrayOutputStream.toByteArray();

        return Base64.encodeToString(imageBytes0, Base64.DEFAULT);
    }
}
