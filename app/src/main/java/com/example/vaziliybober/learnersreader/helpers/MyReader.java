package com.example.vaziliybober.learnersreader.helpers;

import com.github.mertakdut.BookSection;
import com.github.mertakdut.Reader;
import com.github.mertakdut.exception.OutOfPagesException;
import com.github.mertakdut.exception.ReadingException;

import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

public class MyReader {
    private Reader reader;
    private BookSection bookSection;


    public MyReader() {
        reader = new Reader();
        reader.setMaxContentPerSection(1000000); // Max string length for the current page.
        //reader.setIsIncludingTextContent(true);
        reader.setIsOmittingTitleTag(true);

        bookSection = new BookSection();
    }

    public void open(String path) throws ReadingException {
        reader.setFullContent(path);
    }

    public String getPageContent(int pageIndex) throws ReadingException, OutOfPagesException{
        bookSection = reader.readSection(pageIndex);

        String sectionContent = bookSection.getSectionContent(); // Returns content as html.

        sectionContent = Jsoup.clean(sectionContent, new Whitelist().addTags("p", "h1", "h2", "h3", "h4", "h5", "h6"));
        sectionContent = sectionContent.replaceAll("</p>", "").
                replaceAll("<p>", "\t").
                replaceAll("(&nbsp;|&amp;)", "").
                replaceAll("(</h1>|</h2>|</h3>|</h4>|</h5>|</h6>)", "\n").
                replaceAll("(<h1>|<h2>|<h3>|<h4>|<h5>|<h6>)", "\n\t");
        return sectionContent;
    }

    public String getWholeContent() throws ReadingException{
        StringBuilder wholeContent = new StringBuilder();
        for (int i = 1; ; i++) {
            try {
                wholeContent.append(getPageContent(i));
            } catch (OutOfPagesException e) {
                break;
            }
        }

        return wholeContent.toString();
    }
}
