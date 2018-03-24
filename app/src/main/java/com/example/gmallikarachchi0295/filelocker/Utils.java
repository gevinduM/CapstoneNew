package com.example.gmallikarachchi0295.filelocker;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by gmallikarachchi0295 on 3/24/2018.
 */

public class Utils {

    static void saveStringsInSP(Context ctx, String key, String value){

        SharedPreferences.Editor editor = ctx.getSharedPreferences("SP", Activity.MODE_PRIVATE).edit();
        editor.putString(key,value);
        editor.apply();
    }

    static String getStringFromSP(Context ctx, String key){

        SharedPreferences sharedPreferences = ctx.getSharedPreferences("SP", Activity.MODE_PRIVATE);
        return sharedPreferences.getString(key,null);

    }
}
