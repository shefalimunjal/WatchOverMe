package com.shefalimunjal.watchoverme.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shefalimunjal.watchoverme.models.Contact;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Preferences {
    private static volatile Preferences mInstance;
    private static final String EMERGENCY_CONTACTS_KEY = "user";
    private static final String USER_SEEN_TUTORIAL_KEY = "user_seen_tutorial";
    private static final String TAG = Preferences.class.getSimpleName();
    //TODO(shefalimunjal): move gson to a singleton
    private final Gson gson = new Gson();
    private SharedPreferences sharedPreferences;
    private List<Contact> emergencyContacts = null;

    public static Preferences getInstance(Context context) {
        if(mInstance == null) {
            mInstance = new Preferences(context);
        }
        return mInstance;
    }

    private Preferences(Context context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public List<Contact> getEmergencyContacts() {
        if(emergencyContacts == null){
            try {
                String json = sharedPreferences.getString(EMERGENCY_CONTACTS_KEY, null);
                Type listType = new TypeToken<ArrayList<Contact>>(){}.getType();
                emergencyContacts = gson.fromJson(json, listType);
            } catch (Exception e){
                e.printStackTrace();
            }
        }
        if (emergencyContacts == null) {
            emergencyContacts = new ArrayList<>();
        }
        return emergencyContacts;
    }

    public void addNewContact(Contact contact){
        if (contact == null) return;
        getEmergencyContacts().add(contact);
        String json = gson.toJson(emergencyContacts);
        Log.i(TAG,"json is **********************: " + json);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(EMERGENCY_CONTACTS_KEY, json);
        editor.apply();

    }

    public boolean hasUserSeenTutorial() {
        return sharedPreferences.getBoolean(USER_SEEN_TUTORIAL_KEY, false);
    }

    public void setUserSeenTutorial(boolean seen) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(USER_SEEN_TUTORIAL_KEY, seen);
        editor.apply();
    }
}