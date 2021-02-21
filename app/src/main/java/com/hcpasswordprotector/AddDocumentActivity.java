package com.hcpasswordprotector;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.ParcelFileDescriptor;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;

public class AddDocumentActivity extends AppCompatActivity implements View.OnKeyListener {

    EditText documentTitleEditText;
    Button addPDFDocumentButton;
    Button documentDoneButton;
    ImageView documentImageView;
    TextView documentTextView;
    LinearLayout documentLinearLayout;
    RelativeLayout addPDFDocumentRelativeLayout;
    RelativeLayout seePDFDocumentRelativeLayout;
    EditText documentRequestPassEditText;
    PDFView pdfDocumentPDFView;
    ArrayList<String> arrayList;
    SQLiteDatabase sqLiteDatabase;
    Intent showDocumentIntent;
    byte[] pdfDocumentByteArray;
    static Bitmap firstImageOfTheDocumentBitmap;
    int max_PDF_Document_Length;
    ParcelFileDescriptor parcelFileDescriptor;
    PdfRenderer pdfRenderer;
    byte[] showPDFDocumentByteArray;
    byte[] showPDFDocumentByteArray1;
    byte[] showPDFDocumentByteArray2;
    ArrayList<byte[]> pdfDocumentByteArrayList;
    byte[] pdfDocumentByteArray1;
    byte[] pdfDocumentByteArray2;
    byte[] showCompletePDFDocumentByteArray;
    byte[] showPDFDocumentByteArray3;
    byte[] showPDFDocumentByteArray4;
    byte[] pdfDocumentByteArray3;
    byte[] pdfDocumentByteArray4;
    int x;
    ArrayList<byte[]> allDocumentBlobsArrayList;
    ArrayList<String> allDocumentTableNamesArrayList;

    public void addPDFDocument(View view)
    {
        if(documentTitleIsPresent()) {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("application/pdf");
            startActivityForResult(intent, 0);
        } else
        {
            Toast.makeText(AddDocumentActivity.this,"Please Add A Document Title",Toast.LENGTH_SHORT).show();
        }
    }

    public void savePDF(View view)
    {
        try {
            if (documentTitleIsPresent() && pdfDocumentByteArray.length>0 && pdfDocumentByteArray!=null) {
                createTableAndPutValuesInIt();

                new MediaPlayerClass(AddDocumentActivity.this,"save");

                PDFDocumentsList.pdfDocumentsListContext.finish();
                Intent intent = new Intent(AddDocumentActivity.this, PDFDocumentsList.class);
                startActivity(intent);
                finish();
            } else {
                throw new Exception();
            }
        } catch (Exception e)
        {
            Toast.makeText(AddDocumentActivity.this,"Some error occurred",Toast.LENGTH_SHORT).show();
        }
    }

    public void createTableAndPutValuesInIt()
    {
        initialiseArrayList();
        sqLiteDatabase.insert("documents",null,insertValuesInTheSQLiteDatabase());
        for(int i=0;i<allDocumentBlobsArrayList.size();i++)
        {
            sqLiteDatabase.insert(allDocumentTableNamesArrayList.get(i),null,insertValuesInAllDocumentBlobs(allDocumentBlobsArrayList.get(i)));
        }
    }

    public ContentValues insertValuesInTheSQLiteDatabase()
    {
        ContentValues documentContentValues=new ContentValues();
        documentContentValues.put("title",documentTitleEditText.getText().toString());
        documentContentValues.put("documentBlob",pdfDocumentByteArray);
        return documentContentValues;
    }

    public ContentValues insertValuesInAllDocumentBlobs(byte[] documentBlob)
    {
        ContentValues documentBlobContentValues=new ContentValues();
        documentBlobContentValues.put("documentBlob",documentBlob);
        return documentBlobContentValues;
    }

    public void documentForgotPassOnClick(View view)
    {
        Intent forgotDocumentPassIntent=new Intent(AddDocumentActivity.this,ForgotPassActivity.class);
        forgotDocumentPassIntent.putExtra("Forgot Pass Type","Document");
        startActivity(forgotDocumentPassIntent);
        finish();
    }

    public boolean weReceivedAnIntent()
    {
        if (showDocumentIntent.getIntExtra("Document Position",-1)!=-1)
        {
            return true;
        }
        return false;
    }

    public boolean intentFromForgotPassActivity()
    {
        if(showDocumentIntent.getBooleanExtra("Allow",false))
        {
            return true;
        }
        return false;
    }

    public void requestToShowDocument()
    {
        addPDFDocumentRelativeLayout.setVisibility(View.INVISIBLE);
        seePDFDocumentRelativeLayout.setVisibility(View.VISIBLE);
    }

    public String getAppPass()
    {
        SQLiteDatabase emergencyDetailsSQLiteDatabase=this.openOrCreateDatabase("EmergencyDetails",MODE_PRIVATE,null);
        Cursor emergencyDetailsCursor=emergencyDetailsSQLiteDatabase.rawQuery("SELECT apppass FROM emergencydetails",null);

        int apppassIndex=emergencyDetailsCursor.getColumnIndex("apppass");

        emergencyDetailsCursor.moveToFirst();

        return emergencyDetailsCursor.getString(apppassIndex);
    }

    public void initialiseArrayList()
    {
        allDocumentBlobsArrayList=new ArrayList<>();
        allDocumentTableNamesArrayList=new ArrayList<>();

        allDocumentBlobsArrayList.add(pdfDocumentByteArray1);
        allDocumentBlobsArrayList.add(pdfDocumentByteArray2);
        allDocumentBlobsArrayList.add(pdfDocumentByteArray3);
        allDocumentBlobsArrayList.add(pdfDocumentByteArray4);

        allDocumentTableNamesArrayList.add("docBlobOne");
        allDocumentTableNamesArrayList.add("docBlobTwo");
        allDocumentTableNamesArrayList.add("docBlobThree");
        allDocumentTableNamesArrayList.add("docBlobFour");
    }

    public void initialise()
    {
        documentTitleEditText=findViewById(R.id.documentTitleEditText);
        addPDFDocumentButton=findViewById(R.id.addPDFDocumentButton);
        documentDoneButton=findViewById(R.id.documentDoneButton);
        documentImageView=findViewById(R.id.documentImageView);
        documentTextView=findViewById(R.id.documentTextView);
        documentLinearLayout=findViewById(R.id.documentLinearLayout);
        addPDFDocumentRelativeLayout=findViewById(R.id.addPDFDocumentRelativeLayout);
        seePDFDocumentRelativeLayout=findViewById(R.id.seePDFDocumentRelativeLayout);
        documentRequestPassEditText=findViewById(R.id.documentRequestPassEditText);
        pdfDocumentPDFView=findViewById(R.id.pdfDocumentPDFView);
        arrayList=new ArrayList<>();
        pdfDocumentByteArrayList=new ArrayList<>();
        max_PDF_Document_Length=2096720;
        x=0;
        sqLiteDatabase=this.openOrCreateDatabase("Documents",MODE_PRIVATE,null);

        showDocumentIntent=getIntent();

        documentRequestPassEditText.setOnKeyListener(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_document);

        setTitle("Add A PDF Document");

        initialise();

        if(weReceivedAnIntent())
        {
            setTitle("Your Document");
            requestToShowDocument();
        }

        if(intentFromForgotPassActivity())
        {
            setTitle("Your Document");
            showDocument();
            createThePDFDocumentFromTheBitmapStrings(PDFDocumentsList.documentIndexSharedPreferences.getInt("DocumentIndex",-1));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==0 && resultCode==RESULT_OK && data!=null)
        {
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP)
            {

                try {

                    parcelFileDescriptor = AddDocumentActivity.this.getContentResolver().openFileDescriptor(data.getData(),"r");
                    pdfRenderer = new PdfRenderer(parcelFileDescriptor);

                    ParcelFileDescriptor parcelFileDescriptor1=AddDocumentActivity.this.getContentResolver().openFileDescriptor(data.getData(),"r");

                    BufferedInputStream inputStream=new BufferedInputStream(new FileInputStream(parcelFileDescriptor1.getFileDescriptor()));
                    int pdfDocumentData=inputStream.read();
                    x=0;
                    while(pdfDocumentData!=-1)
                    {
                        x++;
                        pdfDocumentData=inputStream.read();
                    }

                    workAfterGettingResultOfThePDFDocument();

                } catch (Exception e)
                {
                    Toast.makeText(AddDocumentActivity.this,"Some error occurred",Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            } else
            {
                Toast.makeText(AddDocumentActivity.this,"Your ANDROID Version Is Less Than LOLLIPOP Version. Please Update :)",Toast.LENGTH_LONG).show();
            }
        }

    }

    public void workAfterGettingResultOfThePDFDocument()
    {
        if(max_PDF_Document_Length*5>(x-1000)) {
            CreatePDFBytes createPDFBytes=new CreatePDFBytes();
            pdfDocumentByteArrayList=createPDFBytes.doInBackground(parcelFileDescriptor);
            createPDFBytes.applyFirstImageToDocumentImageView(pdfRenderer);

            pdfDocumentByteArray=pdfDocumentByteArrayList.get(0);
            pdfDocumentByteArray1=pdfDocumentByteArrayList.get(1);
            pdfDocumentByteArray2=pdfDocumentByteArrayList.get(2);
            pdfDocumentByteArray3=pdfDocumentByteArrayList.get(3);
            pdfDocumentByteArray4=pdfDocumentByteArrayList.get(4);

            if(firstImageOfTheDocumentBitmap!=null)
            {
                documentImageView.setImageBitmap(firstImageOfTheDocumentBitmap);
                addPDFDocumentButton.setVisibility(View.INVISIBLE);
                documentLinearLayout.setVisibility(View.VISIBLE);
            }

            if (documentTitleEditText.getText().toString() != null) {
                documentTextView.setText(documentTitleEditText.getText().toString());
            }

        } else
        {
            Toast.makeText(AddDocumentActivity.this,"Size Of The PDF Document Is Too Large. Please Select Another PDF Document.",Toast.LENGTH_LONG).show();
        }
    }

    public void createThePDFDocumentFromTheBitmapStrings(int documentIdFromTheTable)
    {
        SQLiteDatabase documentsSQLiteDatabase=this.openOrCreateDatabase("Documents",MODE_PRIVATE,null);

        documentTitleEditText.setText(getTheTitleFromTheDocumentSQLiteDatabase(documentsSQLiteDatabase, documentIdFromTheTable));
        documentTitleEditText.setSelection(documentTitleEditText.getText().length());
        documentTitleEditText.setClickable(false);
        documentTitleEditText.setKeyListener(null);

        createPDFDocumentFromTheDocumentBlobs(documentsSQLiteDatabase, documentIdFromTheTable);
    }

    public String getTheTitleFromTheDocumentSQLiteDatabase(SQLiteDatabase documentsSQLiteDatabase, int documentIdFromTheTable)
    {
        Cursor documentsCursor=documentsSQLiteDatabase.rawQuery("SELECT title FROM documents WHERE id="+documentIdFromTheTable,null);

        int titleIndex=documentsCursor.getColumnIndex("title");

        documentsCursor.moveToFirst();

        return documentsCursor.getString(titleIndex);
    }

    public void createPDFDocumentFromTheDocumentBlobs(SQLiteDatabase documentsSQLiteDatabase, int documentIdFromTheTable)
    {
        try {
            Cursor documentsCursor = documentsSQLiteDatabase.rawQuery("SELECT documentBlob FROM documents WHERE id=" + documentIdFromTheTable, null);

            int documentBlobIndex = documentsCursor.getColumnIndex("documentBlob");

            Cursor docBlobOneCursor=documentsSQLiteDatabase.rawQuery("SELECT documentBlob FROM docBlobOne WHERE id="+documentIdFromTheTable,null);

            int docBlobOneDocumentBlobIndex=docBlobOneCursor.getColumnIndex("documentBlob");

            Cursor docBlobTwoCursor=documentsSQLiteDatabase.rawQuery("SELECT documentBlob FROM docBlobTwo WHERE id="+ documentIdFromTheTable,null);

            int docBlobTwoDocumentBlobIndex=docBlobTwoCursor.getColumnIndex("documentBlob");

            Cursor docBlobThreeCursor=documentsSQLiteDatabase.rawQuery("SELECT documentBlob FROM docBlobThree WHERE id="+documentIdFromTheTable,null);

            int docBlobThreeDocumentBlobIndex=docBlobThreeCursor.getColumnIndex("documentBlob");

            Cursor docBlobFourCursor=documentsSQLiteDatabase.rawQuery("SELECT documentBlob FROM docBlobFour WHERE id="+documentIdFromTheTable,null);

            int docBlobFourDocumentBlobIndex=docBlobFourCursor.getColumnIndex("documentBlob");

            documentsCursor.moveToFirst();
            docBlobOneCursor.moveToFirst();
            docBlobTwoCursor.moveToFirst();
            docBlobThreeCursor.moveToFirst();
            docBlobFourCursor.moveToFirst();

            showPDFDocumentByteArray=documentsCursor.getBlob(documentBlobIndex);

            showPDFDocumentByteArray1=docBlobOneCursor.getBlob(docBlobOneDocumentBlobIndex);

            showPDFDocumentByteArray2=docBlobTwoCursor.getBlob(docBlobTwoDocumentBlobIndex);

            showPDFDocumentByteArray3=docBlobThreeCursor.getBlob(docBlobThreeDocumentBlobIndex);

            showPDFDocumentByteArray4=docBlobFourCursor.getBlob(docBlobFourDocumentBlobIndex);

            showCompletePDFDocumentByteArray=ByteBuffer.allocate(showPDFDocumentByteArray.length+showPDFDocumentByteArray1.length+showPDFDocumentByteArray2.length)
                    .put(showPDFDocumentByteArray)
                    .put(showPDFDocumentByteArray1)
                    .put(showPDFDocumentByteArray2)
                    .put(showPDFDocumentByteArray3)
                    .put(showPDFDocumentByteArray4)
                    .array();

        } catch(Exception e)
        {
            e.printStackTrace();
            Toast.makeText(AddDocumentActivity.this,"Some error occurred",Toast.LENGTH_SHORT).show();
        }

        createPDFDocumentPages();
    }

    public void createPDFDocumentPages()
    {
        try {

            final ProgressDialog progressDialog=new ProgressDialog(AddDocumentActivity.this);
            progressDialog.setTitle("Loading Your PDF Document");
            progressDialog.setCancelable(false);
            progressDialog.show();

            pdfDocumentPDFView.fromBytes(showCompletePDFDocumentByteArray).onLoad(new OnLoadCompleteListener() {
                @Override
                public void loadComplete(int nbPages) {
                    progressDialog.dismiss();
                }
            }).load();
            pdfDocumentPDFView.setVisibility(View.VISIBLE);

        } catch (Exception e)
        {
            Toast.makeText(AddDocumentActivity.this,"Some error occurred",Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    public boolean documentTitleIsPresent()
    {
        if(!documentTitleEditText.getText().toString().equals("") && documentTitleEditText.getText().toString()!=null)
        {
            return true;
        }
        return false;
    }

    public void onPermissionToSeeTheDocument()
    {
        new CountDownTimer(500,500)
        {

            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                hideKeyBoard();
                showDocument();
                createThePDFDocumentFromTheBitmapStrings(showDocumentIntent.getIntExtra("Document Position",-1));
            }
        }.start();
    }

    public void showDocument()
    {
        addPDFDocumentRelativeLayout.setVisibility(View.VISIBLE);
        documentTitleEditText.setGravity(Gravity.CENTER);
        seePDFDocumentRelativeLayout.setVisibility(View.INVISIBLE);
        documentDoneButton.setVisibility(View.INVISIBLE);
        addPDFDocumentButton.setVisibility(View.INVISIBLE);
    }

    public void hideKeyBoard()
    {
        InputMethodManager inputMethodManager=(InputMethodManager) this.getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(documentRequestPassEditText.getWindowToken(),0);
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {

        if(keyCode==KeyEvent.KEYCODE_ENTER && event.getAction()==KeyEvent.ACTION_DOWN)
        {
            if(documentRequestPassEditText.getText().toString().equals(getAppPass()))
            {
                onPermissionToSeeTheDocument();
            } else
            {
                Toast.makeText(AddDocumentActivity.this,"Wrong Password! Please Try Again",Toast.LENGTH_SHORT).show();
            }
            return true;
        }

        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sqLiteDatabase.close();
    }
}
