package com.example.vaziliybober.learnersreader.vocabulary;

import android.content.Context;

import com.example.vaziliybober.learnersreader.helpers.SelfSaver;

import java.util.ArrayList;

public class Vocabulary extends SelfSaver{
    private ArrayList<Word> words;

    public static ArrayList<String> wordsToRemove = new ArrayList<>();

    public Vocabulary() {
        words = new ArrayList<>();
    }

    public void newTranslation(String w, String context) {
        for (Word word : words) {
            if (word.getWord().equals(w)) {
                if (!word.getContexts().contains(context)) {
                    word.addContext(context);
                }
                return;
            }
        }

        Word word = new Word(w);
        word.addContext(context);
        words.add(word);
    }

    public ArrayList<String> getAllWords() {
        ArrayList<String> result = new ArrayList<>();

        for (int i = words.size() - 1; i >= 0; i--) {
            result.add(words.get(i).getWord());
        }

        return result;
    }

    public Word getWordByString(String w) {
        for (Word word : words) {
            if (word.getWord().equals(w)) {
                return word;
            }
        }

        return null;
    }


    public void removeWord(String w) {
        Word word = getWordByString(w);
        words.remove(word);
    }


    public static Vocabulary load(Context context, String fileName) {
        return (Vocabulary) SelfSaver.load(context, fileName);
    }

    public void removeWords(Context context) {
        for (String w : wordsToRemove) {
            Word word = getWordByString(w);
            words.remove(word);
            wordsToRemove.remove(w);
        }

        save(context, "vocabulary");
    }
}
