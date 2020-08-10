package com.example.vaziliybober.learnersreader.library;

import android.content.Context;
import android.widget.TextView;

import com.example.vaziliybober.learnersreader.helpers.MyLog;
import com.example.vaziliybober.learnersreader.helpers.MyReader;
import com.example.vaziliybober.learnersreader.helpers.SelfSaver;
import com.github.mertakdut.exception.OutOfPagesException;
import com.github.mertakdut.exception.ReadingException;

import java.util.ArrayList;

public class Book extends SelfSaver{

    private String path;
    private String title;
    private ArrayList<Chapter> chapters;
    transient private MyReader reader;
    private int numberOfPages;
    transient private Position position;
    private int textSize;


    public Book(String path, String title) {
        this.path = path;
        this.title = title;

        position = new Position();

        numberOfPages = 0;
        textSize = 17;

        this.chapters = new ArrayList<>();
    }


    public void readThrough(TextView textView) {
        while (true) {
            try {
                appendChapter(textView);
            } catch (OutOfPagesException e) {
                break;
            }
        }
    }

    public void readFirstChapter(TextView textView) {
        if (chapters.size() > 0) return;

        try {
            appendChapter(textView);
        } catch (OutOfPagesException e) {
            MyLog.warn("Book.readFirstChapter()", "Empty book ?" );
            e.printStackTrace();
        }
    }


    private void appendChapter(TextView textView) throws OutOfPagesException{
        if (reader == null) {
            reader = createReader();
        }

        String chapterText;
        try {
            chapterText = reader.getPageContent(chapters.size() + 1);


            Chapter newChapter = new Chapter(textView, chapterText);
            if (chapters.isEmpty()) {
                newChapter.setPagesNumbers(1);
            } else {
                newChapter.setPagesNumbers(chapters.get(chapters.size() - 1).getLastPageNumber() + 1);
            }

            chapters.add(newChapter);
            numberOfPages += newChapter.getNumberOfPages();
        } catch (ReadingException e) {
            MyLog.print("Couldn't read this chapter while processing the whole book");
            e.printStackTrace();

        }

    }


    public void reloadAllChapters(Context context, TextView textView) {
        numberOfPages = 0;

        int currentChapterNumber = getChapterNumberByPage(position.getPage());
        Chapter currentChapter = chapters.get(currentChapterNumber);
        int currentOffset = 0;
        for (int i = currentChapter.getFirstPageNumber(); i < position.getPage(); i++) {
            currentOffset += currentChapter.getPage(i).getContent().length();
        }


        for (int i = 0; i < chapters.size(); i++) {
            Chapter chapter = chapters.get(i);
            chapter.splitIntoPages(textView, chapter.getWholeText());
            if (i == 0) {
                chapter.setPagesNumbers(1);
            } else {
                chapter.setPagesNumbers(chapters.get(i - 1).getLastPageNumber() + 1);
            }

            numberOfPages += chapter.getNumberOfPages();
        }

        currentChapter = chapters.get(currentChapterNumber);
        int newOffset = 0;
        for (int i = currentChapter.getFirstPageNumber(); i <= currentChapter.getLastPageNumber(); i++) {
            if (currentOffset >= newOffset && currentOffset <= newOffset + currentChapter.getPage(i).getContent().length()) {
                position.setPage(i - 1);
            }
            newOffset += currentChapter.getPage(i).getContent().length();

        }

        save(context, title);
        position.save(context, title + "_position");

    }





    public Chapter getChapter(int number) {
        return chapters.get(number-1);
    }

    public String moveToChapter(int number) {
        Chapter result = getChapter(number);
        position.setPage(result.getFirstPageNumber());
        return result.getPage(position.getPage()).getContent();
    }

    public int getChapterNumberByPage(int pageNumber) {
        for (Chapter chapter : chapters) {
            if (pageNumber >= chapter.getFirstPageNumber() && pageNumber <= chapter.getLastPageNumber()) {
                return chapters.indexOf(chapter);
            }
        }

        return -1;
    }


    public String getPage(int pageNumber) {
        Chapter currentChapter = chapters.get(getChapterNumberByPage(pageNumber));

        if (currentChapter == null)
            return null;

        return currentChapter.getPage(pageNumber).getContent();
    }

    public String moveToPage(Context context, int pageNumber) {
        position.setPage(pageNumber);
        position.save(context, title + "_position");

        return getPage(pageNumber);
    }

    public String moveToNextPage(Context context) {
        return moveToPage(context, getPosition() + 1);
    }

    public String moveToPreviousPage(Context context) {
        return moveToPage(context, getPosition() - 1);
    }

    public int getNumberOfPages() {
        return numberOfPages;
    }


    public ArrayList<String> getTableOfContents() {
        ArrayList<String> result = new ArrayList<>();

        for(Chapter chapter : chapters) {
            result.add(chapter.getTitle());
        }

        return result;
    }



    private MyReader createReader() {
        MyReader reader = new MyReader();
        try {
            reader.open(path);
        } catch (ReadingException e) {
            MyLog.warn("Book.createReader()", "The book must have been already checked");
            e.printStackTrace();
            return reader;
        }

        return reader;
    }



    public String getPath() {
        return path;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getPosition() {
        return position.getPage();
    }

    public void deleteChapters() {
        chapters.clear();
    }

    public void setTextSize(int size) {
        textSize = size;
    }

    public int getTextSize() {
        return textSize;
    }

    public static Book load(Context context, String fileName) {
        Book result = (Book) SelfSaver.load(context, fileName);
        if (result != null) {
            result.position = Position.load(context, fileName + "_position");
        }
        return result;
    }





}

class Position extends SelfSaver {
    private int page = 1;

    public static Position load(Context context, String fileName) {
        return (Position) SelfSaver.load(context, fileName);
    }

    public void setPage(int pageNumber) {
        page = pageNumber;
    }

    public int getPage() {
        return page;
    }
}