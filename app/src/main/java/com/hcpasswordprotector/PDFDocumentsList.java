package com.hcpasswordprotector;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

public class PDFDocumentsList extends AppCompatActivity implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener,View.OnKeyListener {

    ArrayList<String> arrayList;
    ArrayList<byte[]> documentBlobsArrayList;
    ArrayList<String> documentTitlesArrayList;
    ListView documentsListView;
    EditText documentsEnterPassEditText;
    RelativeLayout documentsRelativeLayout;
    SQLiteDatabase sqliteDatabase;
    Cursor cursor;
    int deleteDocumentListViewIndex;
    int lengthOfTheLayout;
    static PDFDocumentsList pdfDocumentsListContext;
    ArrayList<byte[]> documentBlobsArrayList1;
    ArrayList<byte[]> documentBlobsArrayList2;
    ArrayList<byte[]> documentBlobsArrayList3;
    ArrayList<byte[]> documentBlobsArrayList4;
    ArrayList<ArrayList<byte[]>> allDocumentBlobsArrayList;
    ArrayList<String> allDocumentTableNamesArrayList;
    static SharedPreferences documentIndexSharedPreferences;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater=this.getMenuInflater();
        menuInflater.inflate(R.menu.documents_main_menu,menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);

        Intent addDocumentIntent=new Intent(PDFDocumentsList.this,AddDocumentActivity.class);
        startActivity(addDocumentIntent);

        return true;
    }

    public void removeTheDocumentFromTheList(View view)
    {
        if(allowToRemoveTheDocumentFromTheList())
        {
            new DocumentsProcessing().doInBackground();
            dropAllTheTables();
            createTableAfterDocumentDeletion();
            arrayList=documentTitlesArrayList;
            addDocumentTitlesToTheListView();
            new MediaPlayerClass(PDFDocumentsList.this,"delete");
            hideKeyBoard();
            new CountDownTimer(200,200)
            {

                @Override
                public void onTick(long millisUntilFinished) {

                }

                @Override
                public void onFinish() {

                    documentDeletionLayoutProcessing();
                    Toast.makeText(PDFDocumentsList.this,"Successfully deleted",Toast.LENGTH_SHORT).show();

                }
            }.start();
        } else
        {
            Toast.makeText(PDFDocumentsList.this,"It was a wrong Password",Toast.LENGTH_SHORT).show();
        }
    }

    public void dropAllTheTables()
    {
        sqliteDatabase.execSQL("DROP TABLE IF EXISTS documents");
        sqliteDatabase.execSQL("DROP TABLE IF EXISTS docBlobOne");
        sqliteDatabase.execSQL("DROP TABLE IF EXISTS docBlobTwo");
        sqliteDatabase.execSQL("DROP TABLE IF EXISTS docBlobThree");
        sqliteDatabase.execSQL("DROP TABLE IF EXISTS docBlobFour");
    }

    public void createTableAfterDocumentDeletion()
    {
        try {
            sqliteDatabase.execSQL("CREATE TABLE IF NOT EXISTS documents(id INTEGER PRIMARY KEY, title VARCHAR, documentBlob BLOB)");
            sqliteDatabase.execSQL("CREATE TABLE IF NOT EXISTS docBlobOne(id INTEGER PRIMARY KEY, documentBlob BLOB)");
            sqliteDatabase.execSQL("CREATE TABLE IF NOT EXISTS docBlobTwo(id INTEGER PRIMARY KEY, documentBlob BLOB)");
            sqliteDatabase.execSQL("CREATE TABLE IF NOT EXISTS docBlobThree(id INTEGER PRIMARY KEY, documentBlob BLOB)");
            sqliteDatabase.execSQL("CREATE TABLE IF NOT EXISTS docBlobFour(id INTEGER PRIMARY KEY, documentBlob BLOB)");

            for (int i = 0; i < documentBlobsArrayList.size(); i++) {
                sqliteDatabase.insert("documents", null, addValuesToTableAfterDocumentDeletion(documentTitlesArrayList.get(i), documentBlobsArrayList.get(i)));
                for (int j = 1; j < allDocumentBlobsArrayList.size(); j++) {
                    sqliteDatabase.insert(allDocumentTableNamesArrayList.get(j), null, addValuesToTheDocBlobTables(allDocumentBlobsArrayList.get(j).get(i)));
                }
            }
        } catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public ContentValues addValuesToTableAfterDocumentDeletion(String documentTitle, byte[] documentBlob)
    {
        ContentValues documentsContentValues=new ContentValues();
        documentsContentValues.put("title",documentTitle);
        documentsContentValues.put("documentBlob",documentBlob);
        return documentsContentValues;
    }

    public ContentValues addValuesToTheDocBlobTables(byte[] documentBlob)
    {
        ContentValues docBlobOneContentValues=new ContentValues();
        docBlobOneContentValues.put("documentBlob",documentBlob);
        return docBlobOneContentValues;
    }

    public void initialise()
    {
        arrayList=new ArrayList<>();
        documentsListView=findViewById(R.id.documentsListView);
        documentsEnterPassEditText=findViewById(R.id.documentsEnterPassEditText);
        documentsRelativeLayout=findViewById(R.id.documentsRelativeLayout);
        documentBlobsArrayList=new ArrayList<>();
        documentTitlesArrayList=new ArrayList<>();
        pdfDocumentsListContext=PDFDocumentsList.this;
        documentBlobsArrayList1=new ArrayList<>();
        documentBlobsArrayList2=new ArrayList<>();
        documentBlobsArrayList3=new ArrayList<>();
        documentBlobsArrayList4=new ArrayList<>();
        sqliteDatabase=this.openOrCreateDatabase("Documents",MODE_PRIVATE,null);

        documentsListView.setOnItemClickListener(this);
        documentsListView.setOnItemLongClickListener(this);

        documentsEnterPassEditText.setOnKeyListener(this);
    }

    public void createTableAndCursor()
    {
        sqliteDatabase.execSQL("CREATE TABLE IF NOT EXISTS documents(id INTEGER PRIMARY KEY, title VARCHAR, documentBlob BLOB)");
        sqliteDatabase.execSQL("CREATE TABLE IF NOT EXISTS docBlobOne(id INTEGER PRIMARY KEY, documentBlob BLOB)");
        sqliteDatabase.execSQL("CREATE TABLE IF NOT EXISTS docBlobTwo(id INTEGER PRIMARY KEY, documentBlob BLOB)");
        sqliteDatabase.execSQL("CREATE TABLE IF NOT EXISTS docBlobThree(id INTEGER PRIMARY KEY, documentBlob BLOB)");
        sqliteDatabase.execSQL("CREATE TABLE IF NOT EXISTS docBlobFour(id INTEGER PRIMARY KEY, documentBlob BLOB)");
        cursor=sqliteDatabase.rawQuery("SELECT title FROM documents",null);
        putAllTheTitlesInTheArrayList();
    }

    public void putAllTheTitlesInTheArrayList()
    {
        try {
            if (documentsTableIsNotEmpty()) {
                int titleIndex = cursor.getColumnIndex("title");

                cursor.moveToFirst();

                while (!cursor.isAfterLast()) {
                    arrayList.add(cursor.getString(titleIndex));
                    cursor.moveToNext();
                }

                addDocumentTitlesToTheListView();

            } else {
                documentsListView.setVisibility(View.INVISIBLE);
                TextView textView = new TextView(PDFDocumentsList.this);
                textView.setTextSize(25);
                textView.setText("You have not saved any PDF documents yet");
                textView.setGravity(Gravity.CENTER);
                RelativeLayout documentsCompleteRelativeLayout = findViewById(R.id.documentsCompleteRelativeLayout);
                documentsCompleteRelativeLayout.addView(textView);
            }
        } catch (Exception e)
        {
            Toast.makeText(PDFDocumentsList.this,"Some error occurred",Toast.LENGTH_SHORT).show();
        }
    }

    public void addDocumentTitlesToTheListView()
    {
        ArrayList<HashMap<String,String>> addItemsArrayList=new ArrayList<>();
        for(int i=0;i<arrayList.size();i++)
        {
            HashMap<String,String> hashMap=new HashMap<>();
            hashMap.put("title",arrayList.get(i));
            hashMap.put("image",String.valueOf(R.drawable.goldenstar));
            addItemsArrayList.add(hashMap);
        }
        String[] from={"title","image"};
        int[] to={R.id.nameTextView,R.id.imageImageView};

        SimpleAdapter simpleAdapter=new SimpleAdapter(PDFDocumentsList.this,addItemsArrayList,R.layout.addimagetolistlayout,from,to);
        documentsListView.setAdapter(simpleAdapter);
    }

    public boolean documentsTableIsNotEmpty()
    {
        if(cursor.getCount()==0)
        {
            return false;
        }

        return true;
    }

    public boolean allowToRemoveTheDocumentFromTheList()
    {
        try
        {
            SQLiteDatabase emergencyDetailsSQLiteDatabase=this.openOrCreateDatabase("EmergencyDetails",MODE_PRIVATE,null);
            Cursor emergencyDetailsCursor=emergencyDetailsSQLiteDatabase.rawQuery("SELECT apppass FROM emergencydetails",null);

            int apppassIndex=emergencyDetailsCursor.getColumnIndex("apppass");

            emergencyDetailsCursor.moveToFirst();

            if(emergencyDetailsCursor.getString(apppassIndex).equals(documentsEnterPassEditText.getText().toString()))
            {
                return true;
            } else
            {
                return false;
            }

        } catch (Exception e)
        {
            return false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdfdocuments_list);

        setTitle("Your Saved PDF Documents");

        initialise();
        createTableAndCursor();
    }

    public void hideKeyBoard()
    {
        InputMethodManager inputMethodManager=(InputMethodManager) this.getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(documentsRelativeLayout.getWindowToken(),0);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        try
        {
            documentIndexSharedPreferences=this.getSharedPreferences("DocumentIndexSharedPreferences",MODE_PRIVATE);
            documentIndexSharedPreferences.edit().putInt("DocumentIndex",position+1).apply();
            Intent intent=new Intent(PDFDocumentsList.this,AddDocumentActivity.class);
            intent.putExtra("Document Position",position+1);
            startActivity(intent);
        } catch (Exception e)
        {
            Toast.makeText(PDFDocumentsList.this,"Some error occurred",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

        try
        {
            new AlertDialog.Builder(PDFDocumentsList.this)
                    .setIcon(android.R.drawable.ic_delete)
                    .setTitle("Delete this password")
                    .setMessage("Are you sure?")
                    .setPositiveButton("Delete",new DialogInterface.OnClickListener(){

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            layoutAnimationForListViewItemLongClick(position);
                        }
                    })
                    .setNegativeButton("No",null)
                    .show();
        } catch (Exception e)
        {
            Toast.makeText(PDFDocumentsList.this,"Some error occurred",Toast.LENGTH_SHORT).show();
        }

        return true;
    }

    public void layoutAnimationForListViewItemLongClick(int position)
    {
        deleteDocumentListViewIndex=position+1;
        documentsListView.setVisibility(View.INVISIBLE);
        documentsRelativeLayout.setVisibility(View.VISIBLE);
        lengthOfTheLayout=documentsListView.getHeight()/2+documentsRelativeLayout.getHeight()/2+100;
        documentsRelativeLayout.animate().translationYBy(lengthOfTheLayout).setDuration(800).setListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);

                documentsRelativeLayout.animate().translationYBy(-200).setDuration(200).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);

                    }
                }).start();

            }
        }).start();
    }

    public void initialiseArrayLists()
    {
        allDocumentBlobsArrayList=new ArrayList<>();
        allDocumentTableNamesArrayList=new ArrayList<>();

        allDocumentBlobsArrayList.add(documentBlobsArrayList);
        allDocumentBlobsArrayList.add(documentBlobsArrayList1);
        allDocumentBlobsArrayList.add(documentBlobsArrayList2);
        allDocumentBlobsArrayList.add(documentBlobsArrayList3);
        allDocumentBlobsArrayList.add(documentBlobsArrayList4);

        allDocumentTableNamesArrayList.add("documents");
        allDocumentTableNamesArrayList.add("docBlobOne");
        allDocumentTableNamesArrayList.add("docBlobTwo");
        allDocumentTableNamesArrayList.add("docBlobThree");
        allDocumentTableNamesArrayList.add("docBlobFour");
    }

    public void documentDeletionLayoutProcessing()
    {
        documentsListView.setVisibility(View.VISIBLE);
        documentsRelativeLayout.setVisibility(View.INVISIBLE);
        documentsRelativeLayout.animate().translationYBy(-lengthOfTheLayout+200).start();
        documentsEnterPassEditText.setText("");
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {

        if(keyCode==KeyEvent.KEYCODE_BACK && event.getAction()==KeyEvent.ACTION_DOWN)
        {
            documentDeletionLayoutProcessing();
            return true;
        }

        return false;
    }

    class DocumentsProcessing extends AsyncTask<Void, Void, Void>
    {

        public void getAllDocumentBlobs(int deleteDocumentListViewIndex, String documentTableNameString, ArrayList<byte[]> documentBlobArrayList)
        {
            try {
                documentBlobArrayList.clear();

                Cursor documentBlobsCursor = sqliteDatabase.rawQuery("SELECT documentBlob FROM "+documentTableNameString, null);
                int documentBlobsIndex = documentBlobsCursor.getColumnIndex("documentBlob");

                documentBlobsCursor.moveToFirst();

                while (!documentBlobsCursor.isAfterLast()) {
                    documentBlobArrayList.add(documentBlobsCursor.getBlob(documentBlobsIndex));
                    documentBlobsCursor.moveToNext();
                }

                documentBlobArrayList.remove(deleteDocumentListViewIndex - 1);

            } catch (Exception e)
            {

            }
        }

        public void getAllDocumentTitles(int deleteDocumentListViewIndex)
        {
            try {
                documentTitlesArrayList.clear();

                Cursor documentTitlesCursor = sqliteDatabase.rawQuery("SELECT title FROM documents", null);
                int documentTitlesIndex = documentTitlesCursor.getColumnIndex("title");

                documentTitlesCursor.moveToFirst();

                while (!documentTitlesCursor.isAfterLast()) {
                    documentTitlesArrayList.add(documentTitlesCursor.getString(documentTitlesIndex));
                    documentTitlesCursor.moveToNext();
                }

                documentTitlesArrayList.remove(deleteDocumentListViewIndex - 1);
            } catch (Exception e)
            {

            }
        }

        @Override
        protected Void doInBackground(Void... voids) {
            initialiseArrayLists();
            getAllDocumentTitles(deleteDocumentListViewIndex);
            for(int i=0;i<allDocumentBlobsArrayList.size();i++) {
                getAllDocumentBlobs(deleteDocumentListViewIndex, allDocumentTableNamesArrayList.get(i),allDocumentBlobsArrayList.get(i));
            }
            return null;
        }
    }

}
