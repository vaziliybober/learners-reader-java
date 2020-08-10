package com.example.vaziliybober.learnersreader.helpers;

import android.content.Context;
import android.util.Log;

import com.example.vaziliybober.learnersreader.library.Book;

import java.io.File;
import java.util.ArrayList;

public class MyLog {

    static public void print(String str) {
        Log.i("MyLog", str);
    }

    static public void warn(String where, String comment) {
        print("Unexpected behaviour in " + where + ". " + comment);
    }

    static public void printBookTableOfContents(Book book) {
        ArrayList<String> titles = book.getTableOfContents();

        for (int i = 0; i < titles.size(); i++) {
            print(i+1 + ". " + titles.get(i));
        }
    }

    static public void printContextFiles(Context context) {
        File file = context.getFilesDir();
        for (String name : file.list()) {
            print(name);
        }
    }
}