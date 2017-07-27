package utilities;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by arun_i on 15-Jul-17.
 */

public class Globalpreferences {

    private static Globalpreferences globalpreferences;
    private SharedPreferences sharedPreferences;

    public static Globalpreferences getInstances(Context context){
        if(globalpreferences == null){
            globalpreferences = new Globalpreferences(context);
        }
        return globalpreferences;
    }

    private Globalpreferences(Context context){
        sharedPreferences = context.getSharedPreferences("medFriendly",Context.MODE_PRIVATE);
    }

    public void putString(String key, String value){
        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        prefsEditor.putString(key, value);
        prefsEditor.commit();
    }

    public String getString(String key) {
        if (sharedPreferences != null) {
            return sharedPreferences.getString(key, "");
        }
        return "";
    }

    public void putInt(String key, Integer value){
        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        prefsEditor.putInt(key, value);
        prefsEditor.commit();
    }


    public Integer getInt(String key) {
        if (sharedPreferences != null) {
            return sharedPreferences.getInt(key,9999999);
        }
        return 9999999;
    }
}
