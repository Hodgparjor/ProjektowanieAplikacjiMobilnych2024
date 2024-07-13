package com.example.todo;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

public class SharedPreferencesManager {
    private static final String SHARED_PREFERENCES_FILE = "ToDoSharedPreferences";
    private static final String SHOW_COMPLETED = "show_completed";
    private static final String NOTIFICATION_TIME = "notification_time";
    private static final String DISPLAYED_CATEGORIES = "displayed_categories";

    private SharedPreferences preferences;

    public SharedPreferencesManager(Context context) {
        preferences = context.getSharedPreferences(SHARED_PREFERENCES_FILE, Context.MODE_PRIVATE);
    }

    public void putShowCompleted(boolean showCompleted) {
        preferences.edit().putBoolean(SHOW_COMPLETED, showCompleted).apply();
    }

    public boolean getShowCompleted() {
        return preferences.getBoolean(SHOW_COMPLETED, true);
    }

    public void putSelectedCategories(Set<String> selectedCategories) {
        preferences.edit().putStringSet(DISPLAYED_CATEGORIES, selectedCategories).apply();
    }

    public Set<String> getSelectedCategories() {
        return preferences.getStringSet(DISPLAYED_CATEGORIES, new HashSet<>());
    }

    public void putNotificationTime(int time) {
        preferences.edit().putInt(NOTIFICATION_TIME, time).apply();
    }

    public int getNotificationTime() {
        return preferences.getInt(NOTIFICATION_TIME, 15);
    }

}
