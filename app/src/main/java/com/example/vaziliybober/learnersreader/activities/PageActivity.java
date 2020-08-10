package com.example.vaziliybober.learnersreader.activities;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.text.Spannable;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.vaziliybober.learnersreader.R;
import com.example.vaziliybober.learnersreader.helpers.MyLog;
import com.example.vaziliybober.learnersreader.helpers.MyWebViewClient;
import com.example.vaziliybober.learnersreader.helpers.UniversalListener;
import com.example.vaziliybober.learnersreader.library.Book;
import com.example.vaziliybober.learnersreader.helpers.Functions;
import com.example.vaziliybober.learnersreader.library.Library;
import com.example.vaziliybober.learnersreader.vocabulary.Vocabulary;

import java.util.ArrayList;

public class PageActivity extends MyActivity {
    private Book book;

    private TextView textView;
    private TextView pageTextView;
    private WebView webView;
    private ListView chaptersListView;
    private String currentWord;
    private String currentSentence;
    private Vocabulary vocabulary;

    private boolean wholeBookLoaded = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page);

        MyLog.printContextFiles(this);

        setFullscreen();

        chaptersListView = (ListView) findViewById(R.id.chaptersListView);
        chaptersListView.setVisibility(View.GONE);

        initWebView();
        initVocabulary();

        initBook();

        initPageTextView();
        initTextView();

        registerForContextMenu(pageTextView);
    }

    private void initVocabulary() {
        vocabulary = Vocabulary.load(this, "vocabulary");
        if (vocabulary == null) {
            vocabulary = new Vocabulary();
        }
    }

    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo){
        menu.add(0, v.getId(), 0, "Go to page");
        menu.add(0, v.getId(), 0, "Go to chapter");
        menu.add(0, v.getId(), 0, "Change font size");
        menu.add(0, v.getId(), 0, "Vocabulary");
    }

    public boolean onContextItemSelected(MenuItem item){
        if(item.getTitle()=="Go to page") {
            if (wholeBookLoaded)
                showGoToPageDialog();
        }
        if(item.getTitle()=="Go to chapter") {
            if (wholeBookLoaded)
                chaptersListView.setVisibility(View.VISIBLE);
        }
        if(item.getTitle()=="Change font size") {
            if (wholeBookLoaded)
                showChangerFontSizeDialog();
        }
        if(item.getTitle()=="Vocabulary") {
            Intent intent = new Intent(this, VocabularyActivity.class);
            intent.putExtra("from", "PageActivity");
            startActivity(intent);
        }

        return true;
    }

    private void showChangerFontSizeDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("");
        alert.setMessage("Enter the text size (from 10 to 30). Now it is " + book.getTextSize());

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        alert.setView(input);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                int size = Integer.valueOf(input.getText().toString());
                if (size < 10 || size > 30)
                    return;

                textView.setTextSize(size);
                book.setTextSize(size);
                book.reloadAllChapters(PageActivity.this, textView);
                updateTextView(book.moveToNextPage(PageActivity.this));
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

            }
        });

        alert.show();
    }

    private void showGoToPageDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("");
        alert.setMessage("Enter the page number");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        alert.setView(input);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                int number = Integer.valueOf(input.getText().toString());
                if (number <= 0 || number > book.getNumberOfPages())
                    return;

                updateTextView(book.moveToPage(PageActivity.this, number));
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

            }
        });

        alert.show();
    }

    private void initBook() {
        String bookPath = loadPreference(LAST_BOOK_PATH);
        Library library = Library.load(this, libraryFileName);

        textView = (TextView) findViewById(R.id.textView);


        if (library != null) {
            library.clearContextFiles(this);
            String bookTitle = library.getBookByPath(bookPath).getTitle();
            book = Book.load(this, bookTitle);

            if (book == null) {
                MyLog.print("First opening of the book");
                book = new Book(bookPath, bookTitle);
                wholeBookLoaded = false;
            } else {
                MyLog.print("Not first opening of the book");
                initChaptersListView();
            }

        } else {
            MyLog.warn("PageActivity.onCreate()", "The library must have been saved!");
        }
    }


    class LoadPaginationData extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            book.readThrough(textView);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            wholeBookLoaded = true;
            book.save(PageActivity.this, book.getTitle());
            updateTextView(textView.getText().toString());
            initChaptersListView();
            super.onPostExecute(result);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initTextView() {

        textView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (!wholeBookLoaded) {
                    book.readFirstChapter(textView);
                    new LoadPaginationData().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }

                textView.setTextSize(book.getTextSize());
                MyLog.print("hiiiiiiiiiiiiiii");

                updateTextView(book.moveToPage(PageActivity.this, book.getPosition()));

                textView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });


        textView.setOnTouchListener(new UniversalListener(this) {
            private boolean swipeDone = false;
            private int currentWordStart = 0, currentWordEnd = 0;
            @Override
            public void onSwipeLeft() {
                swipeDone = true;
                updateTextView(book.moveToNextPage(PageActivity.this));
            }


            @Override
            public void onSwipeRight() {
                swipeDone = true;
                updateTextView(book.moveToPreviousPage(PageActivity.this));
            }

            @Override
            public void onSwipeUp() {
                removeSpans();
                swipeDone = true;
            }

            @Override
            public void onSwipeDown() {
                removeSpans();
                swipeDone = true;
            }


            @Override
            public void onTouch(MotionEvent e) {

                if (e.getAction() == MotionEvent.ACTION_UP) {
                    if (swipeDone) {
                        swipeDone = false;
                        return;
                    }

                    new LoadInitialForm().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

                    if (currentWord != null) {
                        webView.loadUrl("http://www.learnersdictionary.com/definition/" + currentWord);
                        webView.setVisibility(View.VISIBLE);
                    }

                    return;
                }

                int offset = textView.getOffsetForPosition(e.getX(), e.getY());
                currentWordStart = Functions.getWordLeftBorderByOffset(textView.getText(), offset);
                currentWordEnd = Functions.getWordRightBorderByOffset(textView.getText(), offset);


                if (currentWordEnd > currentWordStart) {
                    currentWord = Functions.getWordByOffset(textView.getText(), offset).toString();
                    currentSentence = Functions.getSentenceByOffset(textView.getText(), offset).toString();

                    Spannable s = (Spannable) textView.getText();

                    removeSpans();

                    s.setSpan(new BackgroundColorSpan(Color.RED), currentWordStart + 1, currentWordEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    s.setSpan(new ForegroundColorSpan(Color.WHITE), currentWordStart + 1, currentWordEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }
        });
    }



    class LoadInitialForm extends AsyncTask<Void, Void, Void> {
        private String initialForm;

        @Override
        protected Void doInBackground(Void... params) {
            vocabulary = Vocabulary.load(PageActivity.this, "vocabulary");
            if (vocabulary == null)
                vocabulary = new Vocabulary();

            initialForm = Functions.initialForm(currentWord);
            MyLog.print("->" + initialForm + "<-");
            if (initialForm == null || initialForm.isEmpty() || initialForm.equals("null"))
                return null;
            vocabulary.newTranslation(initialForm, currentSentence);
            vocabulary.save(PageActivity.this, "vocabulary");
            return null;
        }
    }



    private void removeSpans() {
        Spannable s = (Spannable) textView.getText();

        Functions.removeBackgroundSpans(s, textView.getText().length());
        Functions.removeForegroundSpans(s, textView.getText().length());
    }


    private void updateTextView(String text) {
        if (text == null) {
            if (book.getPosition() <= 0) {
                book.moveToNextPage(this);
            } else {
                book.moveToPreviousPage(this);
            }
            return;
        }
        textView.setText(text, TextView.BufferType.SPANNABLE);

        String totalNumberOfPages;
        if (wholeBookLoaded) {
            totalNumberOfPages = Integer.toString(book.getNumberOfPages());
        } else {
            totalNumberOfPages = "?";
        }
        pageTextView.setText(Integer.toString(book.getPosition()) + "/" + totalNumberOfPages);
    }

    @Override
    public void onBackPressed() {
        if (chaptersListView.getVisibility() == View.VISIBLE) {
            chaptersListView.setVisibility(View.GONE);
            return;
        }

        if (webView.getVisibility() == View.VISIBLE) {
            if (webView.canGoBack()) {
                webView.goBack();

            } else {
                webView.setVisibility(View.GONE);
                webView.loadUrl("about:blank");
            }
        } else {
            Intent intent = new Intent(this, LibraryActivity.class);
            intent.putExtra("from", "MainActivity");
            startActivity(intent);
        }

    }


    private void initChaptersListView() {

        ArrayList<String> chapters = book.getTableOfContents();

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, chapters);
        chaptersListView.setAdapter(adapter);

        chaptersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                textView.setText(book.moveToChapter(position + 1), TextView.BufferType.SPANNABLE);
                updateTextView(textView.getText().toString());
                onBackPressed();
            }
        });
    }


    private void initWebView() {
        webView = (WebView) findViewById(R.id.webView);
        MyWebViewClient myWebViewClient = new MyWebViewClient();
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setVisibility(View.GONE);
        webView.setWebViewClient(myWebViewClient);
    }

    private void initPageTextView() {
        pageTextView = (TextView) findViewById(R.id.pageTextView);
    }
}


