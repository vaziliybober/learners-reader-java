package com.example.vaziliybober.learnersreader.activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.vaziliybober.learnersreader.R;
import com.example.vaziliybober.learnersreader.helpers.Functions;
import com.example.vaziliybober.learnersreader.helpers.MyLog;
import com.example.vaziliybober.learnersreader.helpers.RealPathUtil;
import com.example.vaziliybober.learnersreader.library.Book;
import com.example.vaziliybober.learnersreader.library.Library;

import java.io.File;
import java.util.ArrayList;

public class LibraryActivity extends MyActivity {

    private Button addBookButton;
    private ListView libraryListView;
    private Library library;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);

        clearPreference(LAST_BOOK_PATH);

        hideSupportActionBar();
        requestPermissions();


        library = Library.load(this, libraryFileName);
        if (library == null) library = new Library();

        library.clearContextFiles(this);

        initViews();
    }

    private void initViews() {
        addBookButton = (Button) findViewById(R.id.addBookButton);
        initAddBookButton();
        libraryListView = (ListView) findViewById(R.id.libraryListView);
        initLibraryListView();
    }

    private void initAddBookButton() {

        addBookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent().setType("*/*").setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select a file"), 123);
            }
        });
    }

    private void initLibraryListView() {
        ArrayList<String> bookTitles = library.getAllTitles();

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, bookTitles);
        libraryListView.setAdapter(adapter);

        libraryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String title = (String) libraryListView.getItemAtPosition(position);
                savePreference(LAST_BOOK_PATH, library.getBookByTitle(title).getPath());
                Intent intent = new Intent(LibraryActivity.this, PageActivity.class);
                intent.putExtra("from", "LibraryActivity");
                //MyLog.print("Book selected. Loading PageActivity from LibraryActivity");
                startActivity(intent);
            }
        });

        registerForContextMenu(libraryListView);

    }

    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo){
        menu.add(0, v.getId(), 0, "Edit");
        menu.add(0, v.getId(), 0, "Delete");
    }

    public boolean onContextItemSelected(MenuItem item){
        AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int position = ((AdapterView.AdapterContextMenuInfo)menuInfo).position;
        String title = (String) libraryListView.getItemAtPosition(position);

        if(item.getTitle()=="Edit") {
            showTitleDialog(library.getBookByTitle(title).getPath(), true);
        }
        if(item.getTitle()=="Delete") {
            showDeleteDialog(title);
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==123 && resultCode==RESULT_OK) {

            MyLog.print("New book added");

            Uri selectedFile = data.getData(); //The uri with the location of the file
            String realPath = RealPathUtil.getRealPath(this, selectedFile);
            savePreference(LAST_BOOK_PATH, realPath);
            if (!realPath.endsWith(".epub")) {
                Toast.makeText(this, "Not an epub file", Toast.LENGTH_SHORT).show();
                return;
            }

            if (selectedFile != null) {
                showTitleDialog(realPath, false);
            }

        }
    }

    private void showDeleteDialog(final String title) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("");
        alert.setMessage("Are you sure you want to delete this book?");

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                library.removeBookByTitle(title);
                library.save(LibraryActivity.this, libraryFileName);
                Functions.deleteContextFile(LibraryActivity.this, title);
                Functions.deleteContextFile(LibraryActivity.this, title + "_position");
                initLibraryListView();
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

            }
        });

        alert.show();
    }

    private void showTitleDialog(final String realPath, final boolean edit) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("");
        alert.setMessage("Enter the book name");

        final EditText input = new EditText(this);
        String defaultTitle = new File(realPath).getName();
        if (!edit)
            input.setText(defaultTitle);
        else input.setText(library.getBookByPath(realPath).getTitle());
        input.selectAll();
        alert.setView(input);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String title = input.getText().toString();
                if (!edit) {
                    if (!library.getAllPaths().contains(realPath)) {
                        Book newBook = new Book(realPath, title);
                        library.addBook(newBook);
                        library.save(LibraryActivity.this, libraryFileName);
                        initLibraryListView();
                    }
                } else {
                    String oldTitle = library.getBookByPath(realPath).getTitle();
                    library.getBookByPath(realPath).setTitle(title);
                    Functions.renameContextFile(LibraryActivity.this, oldTitle, title);
                    Functions.renameContextFile(LibraryActivity.this, oldTitle + "_position", title + "_position");
                    library.save(LibraryActivity.this, libraryFileName);
                    initLibraryListView();
                }

            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

            }
        });

        alert.show();
    }



    private void requestPermissions() {
        boolean permissionGranted = ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        if(!permissionGranted) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 200);
        }

        permissionGranted = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        if(!permissionGranted) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 200);
        }
    }

    @Override
    public void onBackPressed() {
        if (getIntent().getStringExtra("from").equals("MainActivity")) {
            //MyLog.print("Exit from LibraryActivity");
            finishAffinity();
        } else {
            super.onBackPressed();
        }
    }
}
