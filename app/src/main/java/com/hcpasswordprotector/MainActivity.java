package com.hcpasswordprotector;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    Cursor c;
    ImageView protectImageView;
    ImageView myAppGuideImageView;
    TextView protectTextView;
    TextView myAppGuideTextView;
    int i;
    boolean finishTheActivity;
    static SharedPreferences showTutorialSharedPreferences;

    public void initialise()
    {
        protectImageView=findViewById(R.id.protectImageView);
        myAppGuideImageView=findViewById(R.id.myAppGuideImageView);
        protectTextView=findViewById(R.id.protectTextView);
        myAppGuideTextView=findViewById(R.id.myAppGuideTextView);
        i=0;
        finishTheActivity=false;
        showTutorialSharedPreferences=this.getSharedPreferences("ShowTutorial",MODE_PRIVATE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().hide();
        SQLiteDatabase sqLiteDatabase=this.openOrCreateDatabase("EmergencyDetails", MODE_PRIVATE, null);

        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS emergencydetails(id INTEGER PRIMARY KEY, name VARCHAR, yourid VARCHAR, apppass VARCHAR)");

        c=sqLiteDatabase.rawQuery("SELECT * FROM emergencydetails",null);

        c.moveToFirst();

        initialise();

        if(c.getCount()==0)
        {
            showTutorialSharedPreferences.edit().putBoolean("show tutorial",true).apply();
        } else
        {
            showTutorialSharedPreferences.edit().putBoolean("show tutorial",false).apply();
        }

        myAppGuideTextView.animate().scaleY(0);

        new CountDownTimer(7000,1000)
        {
            @Override
            public void onTick(long millisUntilFinished) {
                if(showTutorialSharedPreferences.getBoolean("show tutorial",false)) {
                    if (i == 3) {
                        myAppGuideImageView.setVisibility(View.VISIBLE);
                        myAppGuideTextView.setVisibility(View.VISIBLE);
                        myAppGuideTextView.animate().scaleY(1).setDuration(500).start();
                    }
                    i++;
                }
            }

            @Override
            public void onFinish() {
                finishTheActivity=true;
                if(c.getCount()==0) {
                    Intent intent = new Intent(MainActivity.this, UserDetailsActivity.class);
                    startActivity(intent);
                    finish();
                }
                else
                {
                    Intent intent = new Intent(MainActivity.this, PasswordListActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        }.start();

    }

    @Override
    public void finish() {
        if(finishTheActivity) {
            super.finish();
        }
    }
}
