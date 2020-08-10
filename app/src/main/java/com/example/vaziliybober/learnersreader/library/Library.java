package com.example.vaziliybober.learnersreader.library;

import android.content.Context;

import com.example.vaziliybober.learnersreader.helpers.Functions;
import com.example.vaziliybober.learnersreader.helpers.SelfSaver;

import java.io.File;
import java.util.ArrayList;

public class Library extends SelfSaver{

    private ArrayList<Book> books;

    public Library() {
        books = new ArrayList<>();
    }

    public void addBook(Book book) {
        books.add(book);
    }

    public Book getBookByTitle(String title) {
        for (Book book : books) {
            if (title.equals(book.getTitle()))
                return book;
        }

        return null;
    }

    public Book getBookByPath(String path) {
        for (Book book : books) {
            if (path.equals(book.getPath()))
                return book;
        }

        return null;
    }

    public void removeBookByTitle(String title) {
        for (Book book : books) {
            if (title.equals(book.getTitle())) {
                books.remove(book);
            }
        }
    }

    public ArrayList<String> getAllTitles() {
        ArrayList<String> result = new ArrayList<>();

        for (Book book : books) {
            result.add(book.getTitle());
        }

        return result;
    }

    public ArrayList<String> getAllPaths() {
        ArrayList<String> result = new ArrayList<>();

        for (Book book : books) {
            result.add(book.getPath());
        }

        return result;
    }

    @Override
    public void save(Context context, String fileName) {
        for (Book book : books) {
            book.deleteChapters();
        }
        super.save(context, fileName);
    }

    public static Library load(Context context, String fileName) {
        Object result = SelfSaver.load(context, fileName);
        if (result == null) return null;
        return (Library) result;
    }


    public void clearContextFiles(Context context) {
        String path = context.getFilesDir().getAbsolutePath();
        String[] names = new File(path).list();
        ArrayList<String> positionFileNames = new ArrayList<>();
        for (String title : getAllTitles()) {
            positionFileNames.add(title + "_position");
        }
        for (String name : names) {
            if (!getAllTitles().contains(name) && !name.equals("library") && !name.equals("vocabulary") && !positionFileNames.contains(name))
                Functions.deleteContextFile(context, name);
        }

    }
}
