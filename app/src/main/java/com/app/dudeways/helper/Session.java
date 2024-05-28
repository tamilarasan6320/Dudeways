package com.app.dudeways.helper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.app.dudeways.Activity.SplashscreenActivity;


public class Session {
    public static final String PREFER_NAME = "smartgram";
    final int PRIVATE_MODE = 0;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _activity;


    private SharedPreferences sharedPreferences;



    public Session(Context activity) {
        try {
            this._activity = activity;
            pref = _activity.getSharedPreferences(PREFER_NAME, PRIVATE_MODE);
            sharedPreferences = _activity.getSharedPreferences("user_session", Context.MODE_PRIVATE);
            editor = pref.edit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public void setData(String id, String val) {
        editor.putString(id, val);
        editor.commit();
    }

    public void setBoolean(String id, boolean val) {
        editor.putBoolean(id, val);
        editor.commit();
    }

    public String getData(String id) {
        return pref.getString(id, "");
    }


    public void logoutUser(Activity activity) {
        Intent i = new Intent(activity, SplashscreenActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(i);
        activity.finish();
        editor.clear();
        editor.commit();

        new Session(_activity).setBoolean("is_logged_in", false);

    }
    public boolean getBoolean(String id) {
        return pref.getBoolean(id, false);
    }


    public void clearData() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }


}