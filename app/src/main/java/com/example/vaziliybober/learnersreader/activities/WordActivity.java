package com.example.vaziliybober.learnersreader.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.vaziliybober.learnersreader.R;
import com.example.vaziliybober.learnersreader.helpers.MyLog;
import com.example.vaziliybober.learnersreader.helpers.MyWebViewClient;
import com.example.vaziliybober.learnersreader.library.Library;
import com.example.vaziliybober.learnersreader.vocabulary.Vocabulary;
import com.example.vaziliybober.learnersreader.vocabulary.Word;

import java.util.ArrayList;

public class WordActivity extends MyActivity {

    private ListView listView;
    private Button translateButton;
    private TextView textView;
    private WebView webView;
    private Word word;
    private Vocabulary vocabulary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word);

        setFullscreen();

        initWebView();
        initWord();
        initTextView();
        initListView();
        initTranslateButton();

        registerForContextMenu(listView);
    }


    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo){
        menu.add(0, v.getId(), 0, "Delete");
    }

    public boolean onContextItemSelected(MenuItem item){
        AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int position = ((AdapterView.AdapterContextMenuInfo)menuInfo).position;
        String word = (String) listView.getItemAtPosition(position);

        if(item.getTitle()=="Delete") {
            showDeleteDialog(word);
        }
        return true;
    }

    private void showDeleteDialog(final String contextToRemove) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("");
        alert.setMessage("Are you sure you want to delete this word?");

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                word.removeContext(contextToRemove);
                vocabulary.save(WordActivity.this, "vocabulary");
                initListView();
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });

        alert.show();
    }

    private void initWord() {
        vocabulary = Vocabulary.load(this, "vocabulary");
        word = vocabulary.getWordByString(getIntent().getStringExtra("word"));
        MyLog.print(word.getWord());
    }

    private void initTextView() {
        textView = (TextView) findViewById(R.id.textView);
        textView.setText(word.getWord());
    }

    private void initTranslateButton() {
        translateButton = (Button) findViewById(R.id.translateButton);

        translateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webView.loadUrl("http://www.learnersdictionary.com/definition/" + word.getWord());
                webView.setVisibility(View.VISIBLE);
                translateButton.setVisibility(View.GONE);
            }
        });
    }

    private void initListView() {
        listView = (ListView) findViewById(R.id.listView);

        final ArrayList<String> contexts = word.getContexts();

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, contexts);
        listView.setAdapter(adapter);
    }

    private void initWebView() {
        webView = (WebView) findViewById(R.id.webView);
        MyWebViewClient myWebViewClient = new MyWebViewClient();
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setVisibility(View.GONE);
        webView.setWebViewClient(myWebViewClient);
    }


    @Override
    public void onBackPressed() {
        if (webView.getVisibility() == View.VISIBLE) {
            if (webView.canGoBack()) {
                webView.goBack();

            } else {
                webView.setVisibility(View.GONE);
                webView.loadUrl("about:blank");
                translateButton.setVisibility(View.VISIBLE);
            }
        } else {
            super.onBackPressed();
        }

    }
}
