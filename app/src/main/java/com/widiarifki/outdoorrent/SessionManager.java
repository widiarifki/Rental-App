package com.widiarifki.outdoorrent;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.widiarifki.outdoorrent.model.User;

/**
 * Created by Widia Rifkianti on 17/06/2016.
 */
public class SessionManager {
    // Shared Preferences
    SharedPreferences pref;

    // Editor for Shared preferences
    SharedPreferences.Editor editor;

    // Context
    Context mContext;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Sharedpref file name
    private static final String PREF_NAME = "sharedPref";

    // All Shared Preferences Keys
    private static final String IS_LOGIN = "isLoggedIn";
    // User name (make variable public to access from outside)
    public static final String KEY_NAME = "name";
    public static final String KEY_USER_ID = "userId";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_PHONE = "phone";
    public static final String KEY_DEVICE_ID = "deviceId";

    // Constructor
    public SessionManager(Context context){
        this.mContext = context;
        pref = mContext.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void createLoginSession(User user){
        editor.putBoolean(IS_LOGIN, true);
        editor.putInt(KEY_USER_ID, user.getId());
        editor.putString(KEY_NAME, user.getName());
        editor.putString(KEY_EMAIL, user.getEmail());
        editor.putString(KEY_PHONE, user.getPhone());
        editor.putString(KEY_DEVICE_ID, user.getDeviceId());

        // commit changes
        editor.commit();
    }

    /*public HashMap<String, String> getUserDetails(){
        HashMap<String, String> user = new HashMap<String, String>();

        user.put(KEY_NAME, pref.getString(KEY_NAME, null));

        // user email id
        user.put(KEY_EMAIL, pref.getString(KEY_EMAIL, null));

        // return user
        return user;
    }*/
    public User getUserDetails(){
        User user = new User();
        int a = pref.getInt(KEY_USER_ID, 0);
        user.setId(pref.getInt(KEY_USER_ID, 0));
        user.setName(pref.getString(KEY_NAME, null));
        user.setEmail(pref.getString(KEY_EMAIL, null));
        user.setPhone(pref.getString(KEY_PHONE, null));
        user.setDeviceId(pref.getString(KEY_DEVICE_ID, null));

        return user;
    }

    public void checkLogin(){
        // Check login status
        if(!this.isLoggedIn()){
            // user is not logged in redirect him to Login Activity
            Intent i = new Intent(mContext, LoginActivity.class);
            // Closing all the Activities
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            // Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // Staring Login Activity
            mContext.startActivity(i);
        }

    }

    public void logoutUser(){
        // Clearing all data from Shared Preferences
        editor.clear();
        editor.commit();

        // After logout redirect user to Login Activity
        Intent i = new Intent(mContext, MainActivity.class);
        // Closing all the Activities
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Add new Flag to start new Activity
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Staring Login Activity
        mContext.startActivity(i);
    }

    //private boolean isLoggedIn() {
    public boolean isLoggedIn() {
        return pref.getBoolean(IS_LOGIN, false);
    }
}
