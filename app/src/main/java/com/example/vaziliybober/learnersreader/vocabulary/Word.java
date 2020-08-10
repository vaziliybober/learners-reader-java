package com.example.vaziliybober.learnersreader.vocabulary;

import java.io.Serializable;
import java.util.ArrayList;

public class Word implements Serializable{
    private String word;
    private ArrayList<String> contexts;

    public Word(String word) {
        this.word = word;
        contexts = new ArrayList<>();
    }

    public String getWord() {
        return word;
    }
    public void setWord(String w) {
        word = w;
    }

    public void removeContext(String context) {
        contexts.remove(context);
    }

    public void addContext(String context) {
        contexts.add(context);
    }

    public ArrayList<String> getContexts() {
        return contexts;
    }
}
