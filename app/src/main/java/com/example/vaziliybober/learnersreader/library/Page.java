package com.example.vaziliybober.learnersreader.library;

import java.io.Serializable;
import java.util.ArrayList;

public class Page implements Serializable{
    private int number;
    private String content;

    public Page(String content) {
        this.content = content;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getNumber() {
        return number;
    }

    public String getContent() {
        return content;
    }

    public static ArrayList<Page> stringsToPages(ArrayList<String> strings) {
        ArrayList<Page> pages = new ArrayList<>();
        for (int i = 0; i < strings.size(); i++) {
            pages.add(new Page(strings.get(i)));
        }

        return pages;
    }
}
