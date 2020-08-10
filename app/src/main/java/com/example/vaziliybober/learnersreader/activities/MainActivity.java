package com.example.vaziliybober.learnersreader.activities;

import android.content.Intent;
import android.os.Bundle;

import com.example.vaziliybober.learnersreader.R;
import com.example.vaziliybober.learnersreader.helpers.MyLog;

public class MainActivity extends MyActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setFullscreen();

        //clearPreference(LAST_BOOK_PATH);
        String lastBookPath = loadPreference(LAST_BOOK_PATH);

        Intent intent;

        if (lastBookPath.isEmpty()) {
            intent = new Intent(this, LibraryActivity.class);
            MyLog.print("No last book path saved. Goint to LibraryActivity from MainActivity");
        } else {
            intent = new Intent(this, PageActivity.class);
            MyLog.print("Last book path found. Goint to PageActivity from MainActivity");
        }

        intent.putExtra("from", "MainActivity");
        startActivity(intent);
    }
}
