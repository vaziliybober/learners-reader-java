package com.example.vaziliybober.learnersreader.helpers;

import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;

import java.util.ArrayList;
import java.util.List;

public class Paginator {
    private final boolean mIncludePad;
    private final int mWidth;
    private final int mHeight;
    private final float mSpacingMult;
    private final float mSpacingAdd;
    private final String mText;
    private final TextPaint mPaint;
    private final List<String> mPages;

    public Paginator(String text, int pageW, int pageH, TextPaint paint, float spacingMult, float spacingAdd, boolean inclidePad) {
        this.mText = text;
        this.mWidth = pageW;
        this.mHeight = pageH;
        this.mPaint = paint;
        this.mSpacingMult = spacingMult;
        this.mSpacingAdd = spacingAdd;
        this.mIncludePad = inclidePad;
        this.mPages = new ArrayList<String>();

        layout();
    }

    private void layout() {
        final StaticLayout layout = new StaticLayout(mText, mPaint, mWidth, Layout.Alignment.ALIGN_NORMAL, mSpacingMult, mSpacingAdd, mIncludePad);

        final int lines = layout.getLineCount();
        final String text = layout.getText().toString();
        int startOffset = 0;
        int height = mHeight;

        for (int i = 0; i < lines; i++) {
            if (height < layout.getLineBottom(i)) {
                // When the layout height has been exceeded
                addPage(text.substring(startOffset, layout.getLineStart(i)));
                startOffset = layout.getLineStart(i);
                height = layout.getLineTop(i) + mHeight;
            }

            if (i == lines - 1) {
                // Put the rest of the text into the last page
                addPage(text.substring(startOffset, layout.getLineEnd(i)));
                return;
            }
        }
    }

    private void addPage(String text) {
        mPages.add(text);
    }

    public int size() {
        return mPages.size();
    }

    public CharSequence get(int index) {
        return (index >= 0 && index < mPages.size()) ? mPages.get(index) : null;
    }

    public int getPagesNumber() {
        return mPages.size();
    }

    public ArrayList<String> getPages() {
        return (ArrayList<String>) mPages;
    }
}
