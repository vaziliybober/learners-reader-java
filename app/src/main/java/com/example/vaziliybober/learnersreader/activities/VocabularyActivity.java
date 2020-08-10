package com.example.vaziliybober.learnersreader.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.vaziliybober.learnersreader.R;
import com.example.vaziliybober.learnersreader.helpers.Functions;
import com.example.vaziliybober.learnersreader.helpers.MyLog;
import com.example.vaziliybober.learnersreader.library.Book;
import com.example.vaziliybober.learnersreader.vocabulary.Vocabulary;
import com.example.vaziliybober.learnersreader.vocabulary.Word;

import java.io.File;
import java.util.ArrayList;

public class VocabularyActivity extends MyActivity {

    Vocabulary vocabulary;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vocabulary);

        setFullscreen();

        MyLog.printContextFiles(this);


        initVocabulary();
        initListView();

        registerForContextMenu(listView);

    }


    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo){
        menu.add(0, v.getId(), 0, "Edit");
        menu.add(0, v.getId(), 0, "Delete");
    }

    public boolean onContextItemSelected(MenuItem item){
        AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int position = ((AdapterView.AdapterContextMenuInfo)menuInfo).position;
        String word = (String) listView.getItemAtPosition(position);

        if(item.getTitle()=="Edit") {
            showEditDialog(word);
        }
        if(item.getTitle()=="Delete") {
            showDeleteDialog(word);
        }
        return true;
    }


    private void showEditDialog(final String w) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("");
        alert.setMessage("Enter the right word");

        final EditText input = new EditText(this);
        alert.setView(input);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String newWord = input.getText().toString();
                Word word = vocabulary.getWordByString(w);
                if (!vocabulary.getAllWords().contains(newWord))
                    word.setWord(newWord);
                else {
                    for (String context : word.getContexts())
                        vocabulary.newTranslation(newWord, context);
                    vocabulary.removeWord(word.getWord());
                }
                vocabulary.save(VocabularyActivity.this, "vocabulary");
                initListView();
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });

        alert.show();
    }


    private void showDeleteDialog(final String wordToRemove) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("");
        alert.setMessage("Are you sure you want to delete this word?");

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                vocabulary.removeWord(wordToRemove);
                vocabulary.save(VocabularyActivity.this, "vocabulary");
                initListView();
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });

        alert.show();
    }



    private void initVocabulary() {
        vocabulary = Vocabulary.load(this, "vocabulary");
        if (vocabulary == null) {
            vocabulary = new Vocabulary();
        }
    }

    private void initListView() {
        listView = (ListView) findViewById(R.id.listView);

        final ArrayList<String> words = vocabulary.getAllWords();

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, words);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(VocabularyActivity.this, WordActivity.class);
                intent.putExtra("from", "VocabularyActivity");
                intent.putExtra("word", words.get(position));
                startActivity(intent);
            }
        });
    }
}
