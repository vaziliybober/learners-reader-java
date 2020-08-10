package com.example.vaziliybober.learnersreader.activities;

import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

public class MyActivity extends AppCompatActivity {
        protected String libraryFileName = "library";
        protected SharedPreferences sPref;
        protected String PREFERENCES_FILE_NAME = "preferences";
        protected String LAST_BOOK_PATH = "last_book_path";

        protected void savePreference(String key, String value) {
            sPref = getSharedPreferences(PREFERENCES_FILE_NAME, MODE_PRIVATE);
            SharedPreferences.Editor ed = sPref.edit();
            ed.putString(key, value);
            ed.apply();
        }

        protected String loadPreference(String key) {
            sPref = getSharedPreferences(PREFERENCES_FILE_NAME, MODE_PRIVATE);
            return sPref.getString(key, "");
        }

        protected void clearPreference(String key) {
            savePreference(key, "");
        }

    protected void setFullscreen() {
        hideAndroidBar();
        hideSupportActionBar();
    }

    protected void hideSupportActionBar() {
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.hide();
        }
    }

    protected void hideAndroidBar() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

}
