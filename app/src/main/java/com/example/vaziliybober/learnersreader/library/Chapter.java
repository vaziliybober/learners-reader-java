package com.example.vaziliybober.learnersreader.library;

import android.widget.TextView;

import com.example.vaziliybober.learnersreader.helpers.Functions;
import com.example.vaziliybober.learnersreader.helpers.Paginator;

import java.io.Serializable;
import java.util.ArrayList;

public class Chapter implements Serializable{

    private String title;
    private ArrayList<Page> pages;
    private String wholeText;

    private int firstPageNumber;
    private int lastPageNumber;




    public Chapter(TextView textView, String wholeText) {
        title = Functions.firstLine(wholeText, 35);
        pages = new ArrayList<>();
        this.wholeText = wholeText;

        splitIntoPages(textView, wholeText);
    }

    public void splitIntoPages(TextView textView, String text) {
        Paginator paginator = new Paginator(text,
                textView.getWidth(),
                textView.getHeight(),
                textView.getPaint(),
                textView.getLineSpacingMultiplier(),
                textView.getLineSpacingExtra(),
                textView.getIncludeFontPadding());



        pages = Page.stringsToPages(paginator.getPages());
    }



    public int getNumberOfPages() {
        return pages.size();
    }


    public Page getPage(int pageNumber) {
        for (Page page : pages) {
            if (pageNumber == page.getNumber()) {
                return page;
            }
        }

        return null;
    }

    public String getTitle() {
        return title;
    }

    public void setPagesNumbers(int firstPageNumber) {
        this.firstPageNumber = firstPageNumber;
        this.lastPageNumber = firstPageNumber + pages.size() - 1;

        for (int i = 0, j = firstPageNumber; j <= this.lastPageNumber; i++, j++) {
            pages.get(i).setNumber(j);
        }
    }

    public String getWholeText() {
//        String result = "";
//        for (Page page : pages) {
//            result += page.getContent();
//        }
//
//        return result;
        return wholeText;
    }


    public int getFirstPageNumber() {
        return firstPageNumber;
    }

    public int getLastPageNumber() {
        return lastPageNumber;
    }

    public void deletePages() {
        pages.clear();
    }
}
