package com.example.vaziliybober.learnersreader.helpers;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.text.Spannable;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;

import com.example.vaziliybober.learnersreader.activities.PageActivity;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.helper.HttpConnection;
import org.jsoup.nodes.Document;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpCookie;
import java.nio.file.Path;

public class Functions {

    public static String firstLine(String str, int maxLength) {
        String result = str;

        while(result.length() > 0 && (result.charAt(0) == '\n' || result.charAt(0) == '\t' || result.charAt(0) == ' '))
            result = result.substring(1);

        if (result.contains("\n")) {
            result = result.substring(0, result.indexOf("\n"));
        }

        if (result.length() > maxLength)
            result = result.substring(0, maxLength);

        return result;
    }


    public static int getWordRightBorderByOffset(CharSequence str, int offset) {
        int i;
        for (i = offset; i >= 0 && i < str.length(); i++) {
            char s = str.charAt(i);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                if (!Character.isAlphabetic(s))
                    break;
            }
        }

        return i;
    }

    public static int getWordLeftBorderByOffset(CharSequence str, int offset) {
        int i;
        for (i = offset; i >= 0 && i < str.length(); i--) {
            char s = str.charAt(i);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                if (!Character.isAlphabetic(s))
                    break;
            }
        }

        return i;
    }

    public static CharSequence getWordByOffset(CharSequence str, int offset) {
        int i = getWordRightBorderByOffset(str, offset);
        int j = getWordLeftBorderByOffset(str, offset);

        CharSequence result;
        if (i > j)
            result = str.subSequence(j + 1, i);
        else
            result = "";

        result = result.toString().replaceAll("[^a-zA-Zа-яА-Я-]", "");
        return result;
    }


    public static CharSequence getSentenceByOffset(CharSequence str, int offset) {
        int i = getSentenceRightBorderByOffset(str, offset);
        int j = getSentenceLeftBorderByOffset(str, offset);

        CharSequence result;
        if (i > j)
            result = str.subSequence(j + 1, i);
        else
            result = "";

        return result;
    }

    private static int getSentenceRightBorderByOffset(CharSequence str, int offset) {
        int i;
        for (i = offset; i >= 0 && i < str.length(); i++) {
            char s = str.charAt(i);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                if (s == '.')
                    break;
            }
        }

        return i;
    }

    private static int getSentenceLeftBorderByOffset(CharSequence str, int offset) {
        int i;
        for (i = offset; i >= 0 && i < str.length(); i--) {
            char s = str.charAt(i);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                if (s == '.')
                    break;
            }
        }

        return i;
    }


    public static String readRawTextFile(Context ctx, int resId) {
        InputStream inputStream = ctx.getResources().openRawResource(resId);

        InputStreamReader inputreader = new InputStreamReader(inputStream);
        BufferedReader buffreader = new BufferedReader(inputreader);
        String line;
        StringBuilder text = new StringBuilder();

        try {
            while ((line = buffreader.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
        } catch (IOException e) {
            return null;
        }
        return text.toString();
    }


    public static void removeBackgroundSpans(Spannable s, int textLength) {
        BackgroundColorSpan[] spans = s.getSpans(0, textLength, BackgroundColorSpan.class);
        for (BackgroundColorSpan span : spans) {
            s.removeSpan(span);
        }
    }

    public static void removeForegroundSpans(Spannable s, int textLength) {
        ForegroundColorSpan[] spans = s.getSpans(0, textLength, ForegroundColorSpan.class);
        for (ForegroundColorSpan span : spans) {
            s.removeSpan(span);
        }
    }


    public static String initialForm(final String word) {
        Document doc = null;
        String result = null;
        try {
            Connection.Response response = Jsoup.connect("http://www.learnersdictionary.com/definition/" + word).timeout(15000).ignoreHttpErrors(true).execute();
            doc = response.parse();
            result = doc.select("h1[id=ld_entries_v2_mainh][class=georgia_font box_sizing ld_xs_hidden]").text();
            //System.out.print(doc.html());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public static void deleteContextFile(Context context, String fileName) {
        String path = context.getFilesDir().getAbsolutePath();
        new File(path + File.separator + fileName).delete();
    }

    public static void renameContextFile(Context context, String from, String to) {
        String path = context.getFilesDir().getAbsolutePath();
        new File(path + File.separator + from).renameTo(new File(path + File.separator + to));
    }

}
