package com.hcpasswordprotector;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.method.PasswordTransformationMethod;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

public class PasswordListActivity extends AppCompatActivity implements View.OnKeyListener {

    SQLiteDatabase sqLiteDatabase;
    Cursor cursor;
    ListView passwordListView;
    ArrayList<HashMap<String, String>> passwordArrayList;
    SimpleAdapter passwordAdapter;
    ArrayList<String> reloadTitleArrayList;
    ArrayList<String> reloadActualPasswordArrayList;
    ArrayList<Integer> reloadPictureArrayList;
    RelativeLayout relativeLayout;
    TextView enterPassTextView;
    EditText enterPassEditText;
    Button deleteButton;
    static PasswordListActivity passwordListActivity;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.main_menu,menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);

        switch(item.getItemId())
        {
            case R.id.addPassword: Intent addPasswordIntent=new Intent(PasswordListActivity.this,AddPasswordActivity.class);
                                   startActivity(addPasswordIntent);
                                   break;
            case R.id.documents: Intent documentsIntent=new Intent(PasswordListActivity.this,PDFDocumentsList.class);
                                 startActivity(documentsIntent);
                                 finish();
        }

        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_list);

        setTitle("Your Saved Passwords");

        passwordListView=findViewById(R.id.passwordListView);
        passwordArrayList=new ArrayList<>();
        reloadTitleArrayList=new ArrayList<>();
        reloadActualPasswordArrayList=new ArrayList<>();
        reloadPictureArrayList=new ArrayList<>();
        enterPassTextView=findViewById(R.id.enterPassTextView);
        enterPassEditText=findViewById(R.id.enterPassEditText);
        deleteButton=findViewById(R.id.deleteButton);
        relativeLayout=findViewById(R.id.relLayout);
        passwordListActivity=this;

        sqLiteDatabase=this.openOrCreateDatabase("Passwords", MODE_PRIVATE, null);

        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS passwords(id INTEGER PRIMARY KEY, title VARCHAR, actualpassword VARCHAR, picture INTEGER)");
        cursor=sqLiteDatabase.rawQuery("SELECT title,picture,actualpassword FROM passwords", null);

        int titleIndex=cursor.getColumnIndex("title");
        int actualPasswordIndex=cursor.getColumnIndex("actualpassword");

        cursor.moveToFirst();

        if(MainActivity.showTutorialSharedPreferences.getBoolean("show tutorial",false) && (passwordArrayList.size()==0 || passwordArrayList==null)) {
            tutorialGuide();
        }

        while(!cursor.isAfterLast())
        {
            HashMap<String,String> hashMap=new HashMap<>();
            hashMap.put("title",cursor.getString(titleIndex));
            for(int i=0;i<AddPasswordActivity.companyIcons.length;i++)
            {
                if(cursor.getString(titleIndex).equals(AddPasswordActivity.companyNames[i]))
                {
                    hashMap.put("picture",String.valueOf(AddPasswordActivity.companyIcons[i]));
                    break;
                } else
                {
                    hashMap.put("picture",String.valueOf(R.drawable.addicon));
                }
            }
            passwordArrayList.add(hashMap);
            reloadTitleArrayList.add(cursor.getString(titleIndex));
            reloadPictureArrayList.add(Integer.parseInt(hashMap.get("picture")));
            reloadActualPasswordArrayList.add(cursor.getString(actualPasswordIndex));
            cursor.moveToNext();
        }

        String[] from={"title","picture"};

        int[] to={R.id.nameTextView,R.id.imageImageView};

        passwordAdapter=new SimpleAdapter(PasswordListActivity.this,passwordArrayList,R.layout.addimagetolistlayout,from,to);
        passwordListView.setAdapter(passwordAdapter);

        if(passwordArrayList.size()==0)
        {
            passwordListView.setVisibility(ListView.INVISIBLE);
            TextView textView=new TextView(PasswordListActivity.this);
            textView.setText("You have not saved any passwords yet");
            textView.setTextSize(25);
            textView.setGravity(Gravity.CENTER);
            RelativeLayout relLayout=findViewById(R.id.relLayout);
            relLayout.addView(textView);
        } else
        {
            passwordListView.setVisibility(View.VISIBLE);
        }

        passwordListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(PasswordListActivity.this,AddPasswordActivity.class);
                intent.putExtra("ID",position+1);
                startActivity(intent);
            }
        });

        passwordListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                new AlertDialog.Builder(PasswordListActivity.this)
                        .setIcon(android.R.drawable.ic_delete)
                        .setTitle("Delete this password")
                        .setMessage("Are you sure?")
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                new CountDownTimer(700,1000) {
                                    @Override
                                    public void onTick(long millisUntilFinished) {

                                    }

                                    @Override
                                    public void onFinish() {
                                        enterPassTextView.setVisibility(View.VISIBLE);
                                        enterPassTextView.animate().translationYBy(500).setDuration(800).start();
                                        enterPassEditText.setVisibility(View.VISIBLE);
                                        enterPassEditText.animate().setStartDelay(400).translationYBy(700).setDuration(800).start();
                                        passwordListView.setVisibility(View.INVISIBLE);
                                        deleteButton.setVisibility(View.VISIBLE);
                                        deleteButton.animate().setStartDelay(800).translationYBy(1000).setDuration(800).start();
                                        enterPassEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                                        SQLiteDatabase database=PasswordListActivity.this.openOrCreateDatabase("EmergencyDetails", MODE_PRIVATE,null);
                                        final Cursor c=database.rawQuery("SELECT apppass FROM emergencydetails",null);

                                        final int apppassIndex=c.getColumnIndex("apppass");

                                        c.moveToFirst();

                                        deleteButton.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                if(enterPassEditText.getText().toString().equals(c.getString(apppassIndex)))
                                                {
                                                    passwordArrayList.remove(position);
                                                    passwordAdapter.notifyDataSetChanged();
                                                    reloadTitleArrayList.remove(position);
                                                    reloadActualPasswordArrayList.remove(position);
                                                    reloadPictureArrayList.remove(position);
                                                    sqLiteDatabase.execSQL("DROP TABLE passwords");
                                                    sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS passwords(id INTEGER PRIMARY KEY, title VARCHAR, actualpassword VARCHAR, picture INTEGER)");
                                                    for(int i=0;i<passwordArrayList.size();i++) {
                                                        sqLiteDatabase.execSQL("INSERT INTO passwords(title, actualpassword,picture) VALUES('" + reloadTitleArrayList.get(i) + "', '"+reloadActualPasswordArrayList.get(i)+"', '"+reloadPictureArrayList.get(i)+"')");
                                                    }
                                                    enterPassTextView.setVisibility(View.INVISIBLE);
                                                    enterPassEditText.setVisibility(View.INVISIBLE);
                                                    passwordListView.setVisibility(View.VISIBLE);
                                                    deleteButton.setVisibility(View.INVISIBLE);
                                                    enterPassEditText.setText("");
                                                    hideKeyboard();
                                                    reLocateDeletingViews();
                                                    new MediaPlayerClass(PasswordListActivity.this,"delete");
                                                    Toast.makeText(PasswordListActivity.this,"Successfully Deleted",Toast.LENGTH_SHORT).show();
                                                }
                                                else
                                                {
                                                    Toast.makeText(PasswordListActivity.this,"It was a wrong Password",Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    }
                                }.start();
                            }
                        }).setNegativeButton("No", null)
                        .show();
                return true;
            }
        });

        enterPassEditText.setOnKeyListener(this);
        deleteButton.setOnKeyListener(this);

    }

    public void reLocateDeletingViews()
    {
        enterPassTextView.animate().translationYBy(-500).start();
        enterPassEditText.animate().translationYBy(-700).start();
        deleteButton.animate().translationYBy(-1000).start();
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_ENTER && event.getAction()==KeyEvent.ACTION_DOWN)
        {
            hideKeyboard();
        }
        else if(keyCode==KeyEvent.KEYCODE_BACK && event.getAction()==KeyEvent.ACTION_DOWN)
        {
            new CountDownTimer(500,1000)
            {
                @Override
                public void onTick(long millisUntilFinished) {

                }

                @Override
                public void onFinish() {
                    enterPassTextView.setVisibility(View.INVISIBLE);
                    enterPassEditText.setVisibility(View.INVISIBLE);
                    passwordListView.setVisibility(View.VISIBLE);
                    deleteButton.setVisibility(View.INVISIBLE);
                    enterPassEditText.setText("");
                    reLocateDeletingViews();
                    hideKeyboard();
                }
            }.start();
        }
        return true;
    }

    public void hideKeyboard()
    {
        InputMethodManager inputMethodManager=(InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(enterPassTextView.getWindowToken(),0);
    }

    public void tutorialGuide()
    {
        int height = 250;
        int width = 250;
        ImageView myAppGuideImageView = new ImageView(PasswordListActivity.this);
        myAppGuideImageView.setImageResource(R.drawable.myandroidguide);
        myAppGuideImageView.setLayoutParams(new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT
        ));
        ImageView upwardPointingImageView = new ImageView(PasswordListActivity.this);
        upwardPointingImageView.setImageResource(R.drawable.upwardpointing);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(width - 100, height - 100);
        lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        upwardPointingImageView.setLayoutParams(lp);
        animation(upwardPointingImageView);
        relativeLayout.addView(myAppGuideImageView);
        relativeLayout.addView(upwardPointingImageView);
    }

    public void animation(final View view)
    {
        view.animate().translationY(200).setDuration(500).setListener(new AnimatorListenerAdapter(){
            @Override
            public void onAnimationEnd(Animator animation) {
                repeatAnimation(view);
            }
        }).start();
    }

    public void repeatAnimation(final View view)
    {
        view.animate().translationYBy(-200).setDuration(500).setListener(new AnimatorListenerAdapter(){
            @Override
            public void onAnimationEnd(Animator animation) {
                animation(view);
            }
        }).start();
    }

}
