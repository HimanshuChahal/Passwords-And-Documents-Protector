package com.hcpasswordprotector;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

public class AddPasswordActivity extends AppCompatActivity implements View.OnKeyListener, AdapterView.OnItemClickListener, View.OnClickListener, TextWatcher {

    SQLiteDatabase sqLiteDatabase;

    SharedPreferences sharedPreferences;

    ListView suggestionsListView;
    ArrayList<HashMap<String,String>> suggestionsArrayList;
    SimpleAdapter suggestionsAdapter;
    EditText titleEditText;
    EditText passwordEditText;
    Button doneButton;
    RelativeLayout relativeLayout;
    TextView txt;
    EditText editText;
    TextView forgotPassTextView;
    Intent intent;
    int icon;
    ArrayList<String> companiesArrayList;

    static int[] companyIcons = {
            R.drawable.google,
            R.drawable.facebook,
            R.drawable.instaicon,
            R.drawable.twitter,
            R.drawable.microsofticon,
            R.drawable.zomato,
            R.drawable.swiggy,
            R.drawable.paytm,
            R.drawable.netflix,
            R.drawable.amazon,
            R.drawable.addicon
    };

    static String[] companyNames = {
            "Google",
            "Facebook",
            "Instagram",
            "Twitter",
            "Microsoft",
            "Zomato",
            "Swiggy",
            "Paytm",
            "Netflix",
            "Amazon",
            "Customize"
    };

    public void outsideClick(View view)
    {
        suggestionsListView.setVisibility(View.INVISIBLE);
        if(titleEditText.getVisibility()==View.VISIBLE) {
            passwordEditText.setVisibility(View.VISIBLE);
        }
    }

    public void savePassword(View view)
    {
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS passwords(id INTEGER PRIMARY KEY, title VARCHAR, actualpassword VARCHAR, picture INTEGER)");
        if(!titleEditText.getText().toString().equals("") && !passwordEditText.getText().toString().equals("")) {
            for(int i=0;i<companyNames.length;i++)
            {
                if(companyNames[i].equals(titleEditText.getText().toString()))
                {
                    icon=companyIcons[i];
                    break;
                }
                else
                {
                    icon=R.drawable.addicon;
                }
            }
            sqLiteDatabase.execSQL("INSERT INTO passwords(title, actualpassword, picture) VALUES('" + titleEditText.getText().toString() + "', '" + passwordEditText.getText().toString() + "', "+icon+")");
            new MediaPlayerClass(AddPasswordActivity.this,"save");
            PasswordListActivity.passwordListActivity.finish();
            Intent intent=new Intent(AddPasswordActivity.this,PasswordListActivity.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(AddPasswordActivity.this, "Enter appropriate values in title/password fields", Toast.LENGTH_SHORT).show();
        }
    }

    public void titleOnClick(View view)
    {
        suggestionsListView.setVisibility(View.VISIBLE);
        passwordEditText.setVisibility(View.INVISIBLE);
        if(suggestionsArrayList.isEmpty())
        {
            passwordEditText.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_password);

        sqLiteDatabase=this.openOrCreateDatabase("Passwords",MODE_PRIVATE,null);

        sharedPreferences=getSharedPreferences("idOfTheSavedPassword",MODE_PRIVATE);

        suggestionsListView=findViewById(R.id.suggestionsListView);
        suggestionsArrayList=new ArrayList<>();
        titleEditText=findViewById(R.id.titleEditText);
        passwordEditText=findViewById(R.id.passwordEditText);
        doneButton=findViewById(R.id.doneButton);
        relativeLayout=findViewById(R.id.relativeLayout);
        forgotPassTextView=findViewById(R.id.forgotPassTextView);
        companiesArrayList=new ArrayList<>();

        for(int i=0;i<companyIcons.length;i++)
        {
            HashMap<String, String> hashMap=new HashMap<>();
            hashMap.put("CompanyName", companyNames[i]);
            hashMap.put("CompanyIcon", Integer.toString(companyIcons[i]));
            suggestionsArrayList.add(hashMap);
        }

        String[] from={"CompanyName", "CompanyIcon"};

        int[] to={R.id.nameTextView,R.id.imageImageView};

        suggestionsAdapter=new SimpleAdapter(AddPasswordActivity.this,suggestionsArrayList,R.layout.addimagetolistlayout,from,to);

        suggestionsListView.setAdapter(suggestionsAdapter);

        suggestionsListView.setOnItemClickListener(this);

        forgotPassTextView.setOnClickListener(this);

        titleEditText.addTextChangedListener(this);

        intent=getIntent();

        if(intent.getBooleanExtra("Allow",false))
        {
            setTitle("Your Password");
            VisitYourPassword visitYourPassword=new VisitYourPassword();
            visitYourPassword.permitted(sharedPreferences.getInt("id",-1));
            passwordEditText.setVisibility(View.VISIBLE);
            suggestionsListView.setVisibility(View.INVISIBLE);
            hideKeyBoard();
        }
        else {
            if (intent.getIntExtra("ID", -1) == -1) {
                setTitle("Add your password");
                suggestionsListView.setVisibility(View.VISIBLE);
                passwordEditText.setOnKeyListener(this);
            } else {
                setTitle("Your password");
                new VisitYourPassword();

                SQLiteDatabase database = this.openOrCreateDatabase("EmergencyDetails", MODE_PRIVATE, null);
                final Cursor cursor = database.rawQuery("SELECT apppass FROM emergencydetails", null);
                final int apppassIndex = cursor.getColumnIndex("apppass");

                cursor.moveToFirst();

                editText.setOnKeyListener(this);
            }
        }

    }

    public void hideKeyBoard()
    {
        InputMethodManager inputMethodManager=(InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(titleEditText.getWindowToken(),0);
    }

    public String getAppPass()
    {
        SQLiteDatabase emergencyDetailsSQLiteDatabase=this.openOrCreateDatabase("EmergencyDetails",MODE_PRIVATE,null);
        Cursor emergencyDetailsCursor=emergencyDetailsSQLiteDatabase.rawQuery("SELECT apppass FROM emergencydetails",null);

        int apppassIndex=emergencyDetailsCursor.getColumnIndex("apppass");

        emergencyDetailsCursor.moveToFirst();

        return emergencyDetailsCursor.getString(apppassIndex);
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {

        if(keyCode==KeyEvent.KEYCODE_ENTER && event.getAction()==KeyEvent.ACTION_DOWN)
        {
            hideKeyBoard();
            if(v==editText)
            {
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (editText.getText().toString().equals(getAppPass())) {
                        new CountDownTimer(500, 1000) {

                            @Override
                            public void onTick(long millisUntilFinished) {

                            }

                            @Override
                            public void onFinish() {
                                VisitYourPassword visit = new VisitYourPassword();
                                visit.permitted(intent.getIntExtra("ID",-1));
                            }
                        }.start();

                        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                        inputMethodManager.hideSoftInputFromWindow(titleEditText.getWindowToken(), 0);
                    } else {
                        Toast.makeText(AddPasswordActivity.this, "Wrong Password! Please Try Again", Toast.LENGTH_SHORT).show();
                        editText.setText("");
                    }
                } else if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
                    AddPasswordActivity.this.finish();
                }
                return true;
            }
            }

        return false;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(!companyNames[position].equals("Customize")) {
            if(companiesArrayList.size()>0 && companiesArrayList!=null) {
                titleEditText.setText(companiesArrayList.get(position));
            } else
            {
                titleEditText.setText(companyNames[position]);
            }
            titleEditText.setSelection(titleEditText.getText().length());
        }
        else
        {
            titleEditText.setText("");
        }
        suggestionsListView.setVisibility(View.INVISIBLE);
        passwordEditText.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.forgotPassTextView)
        {
            sharedPreferences.edit().putInt("id",intent.getIntExtra("ID", -1)).apply();
            Intent intent1=new Intent(AddPasswordActivity.this,ForgotPassActivity.class);
            startActivity(intent1);
            finish();
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if(intent.getIntExtra("ID",-1)==-1) {
            suggestionsArrayList.clear();
            suggestionsListView.setVisibility(View.VISIBLE);
            companiesArrayList.clear();
            suggestionsAdapter.notifyDataSetChanged();
            for (int i = 0; i < companyIcons.length; i++) {
                if(s.length()<=companyNames[i].length()) {
                    if (s.toString().toLowerCase().equals(companyNames[i].substring(0, s.length()).toLowerCase())) {
                        addValuesAfterTextChangeToArrayList(i);
                    }
                }
                if(suggestionsArrayList.isEmpty())
                {
                    passwordEditText.setVisibility(View.VISIBLE);
                } else
                {
                    passwordEditText.setVisibility(View.INVISIBLE);
                }
            }
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    public void addValuesAfterTextChangeToArrayList(int i)
    {
        HashMap<String,String> hashMap=new HashMap<>();
        hashMap.put("CompanyName",companyNames[i]);
        hashMap.put("CompanyIcon",Integer.toString(companyIcons[i]));
        suggestionsArrayList.add(hashMap);
        companiesArrayList.add(companyNames[i]);
        suggestionsAdapter.notifyDataSetChanged();
    }

    public class VisitYourPassword
    {
        public VisitYourPassword()
        {
            suggestionsListView.setVisibility(View.INVISIBLE);
            passwordEditText.setVisibility(View.INVISIBLE);
            titleEditText.setVisibility(View.INVISIBLE);
            doneButton.setVisibility(View.INVISIBLE);
            txt=findViewById(R.id.txt);
            txt.setVisibility(View.VISIBLE);
            editText=findViewById(R.id.requestPassEditText);
            editText.setVisibility(View.VISIBLE);
            forgotPassTextView.setVisibility(View.VISIBLE);
        }

        public void permitted(int whichID)
        {
            passwordEditText.setVisibility(View.VISIBLE);
            titleEditText.setVisibility(View.VISIBLE);
            titleEditText.setClickable(false);
            titleEditText.setKeyListener(null);
            passwordEditText.setKeyListener(null);
            editText.setVisibility(View.INVISIBLE);
            txt.setVisibility(View.INVISIBLE);
            forgotPassTextView.setVisibility(View.INVISIBLE);

            TextView msg=new TextView(AddPasswordActivity.this);
            msg.setText("You cannot alter this password");
            RelativeLayout.LayoutParams lp3=new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
            );
            lp3.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            lp3.addRule(RelativeLayout.CENTER_HORIZONTAL);
            lp3.bottomMargin=200;
            msg.setLayoutParams(lp3);
            msg.setTextColor(Color.BLUE);
            relativeLayout.addView(msg);

            Cursor c = sqLiteDatabase.rawQuery("SELECT title,actualpassword FROM passwords WHERE id=" + whichID, null);
            int titleIndex = c.getColumnIndex("title");
            int actualPasswordIndex = c.getColumnIndex("actualpassword");

            c.moveToFirst();

            titleEditText.setText(c.getString(titleIndex));
            passwordEditText.setText(c.getString(actualPasswordIndex));
            sharedPreferences.edit().clear().apply();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        sqLiteDatabase.close();
    }
}
