package com.hcpasswordprotector;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

public class ForgotPassActivity extends AppCompatActivity implements View.OnKeyListener {

    SQLiteDatabase sqLiteDatabase;
    Cursor cursor;

    EditText specifiedNameEditText;
    EditText specifiedIdEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_pass);

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.BLACK));
        setTitle("Your Details");

        sqLiteDatabase=this.openOrCreateDatabase("EmergencyDetails",MODE_PRIVATE,null);
        cursor=sqLiteDatabase.rawQuery("SELECT name,yourid FROM emergencydetails",null);

        specifiedNameEditText=findViewById(R.id.specifiedNameEditText);
        specifiedIdEditText=findViewById(R.id.specifiedIdEditText);

        specifiedIdEditText.setOnKeyListener(this);
    }

    public boolean allowToSeeThePassOrDoc(int youridIndex, int nameIndex)
    {
        if(cursor.getString(nameIndex).equals(specifiedNameEditText.getText().toString()) && cursor.getString(youridIndex).equals(specifiedIdEditText.getText().toString()))
        {
            return true;
        }
        return false;
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_ENTER && event.getAction()== KeyEvent.ACTION_DOWN)
        {
            InputMethodManager inputMethodManager=(InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(specifiedNameEditText.getWindowToken(),0);

            int nameIndex=cursor.getColumnIndex("name");
            int youridIndex=cursor.getColumnIndex("yourid");
            cursor.moveToFirst();

            Intent recievedIntent=getIntent();

            if(allowToSeeThePassOrDoc(youridIndex, nameIndex))
            {
                if("Document".equals(recievedIntent.getStringExtra("Forgot Pass Type")))
                {
                    Intent allowToSeeTheDocumentIntent=new Intent(ForgotPassActivity.this,AddDocumentActivity.class);
                    allowToSeeTheDocumentIntent.putExtra("Allow",true);
                    startActivity(allowToSeeTheDocumentIntent);
                } else {
                    Intent intent = new Intent(ForgotPassActivity.this, AddPasswordActivity.class);
                    intent.putExtra("Allow", true);
                    startActivity(intent);
                }
                finish();
            }
            else
            {
                Toast.makeText(ForgotPassActivity.this,"Invalid Name/Id",Toast.LENGTH_SHORT).show();
            }
        }
        else if (keyCode==KeyEvent.KEYCODE_BACK && event.getAction()==KeyEvent.ACTION_DOWN)
        {
            finish();
        }
        return true;
    }
}
