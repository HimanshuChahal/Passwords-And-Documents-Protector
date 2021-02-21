package com.hcpasswordprotector;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class UserDetailsActivity extends AppCompatActivity {

    SQLiteDatabase sqLiteDatabase;

    EditText yourName;
    EditText yourId;
    EditText appPass;
    EditText confirmAppPass;

    public void submitDetails(View view)
    {

        if(yourName.getText().toString().equals("") ||
                yourId.getText().toString().equals("") ||
                appPass.getText().toString().equals("") ||
                confirmAppPass.getText().toString().equals("") ||
                !(appPass.getText().toString().equals(confirmAppPass.getText().toString())))
        {
            Toast.makeText(UserDetailsActivity.this,"Please Enter valid Details",Toast.LENGTH_SHORT).show();
        }
        else {
            try {
                sqLiteDatabase.execSQL("INSERT INTO emergencydetails(name, yourid, apppass) VALUES('" + yourName.getText().toString() + "', '" + yourId.getText().toString() + "', '" + appPass.getText().toString() + "')");
                Toast.makeText(UserDetailsActivity.this, "Successful", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
            }


            Intent intent = new Intent(UserDetailsActivity.this, PasswordListActivity.class);
            startActivity(intent);
            finish();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);

        setTitle("Emergency Details");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.BLACK));
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        LayoutInflater layoutInflater=(LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
        View actionBarTextView=layoutInflater.inflate(R.layout.action_bar_layout, null);
        getSupportActionBar().setCustomView(actionBarTextView);

        sqLiteDatabase=this.openOrCreateDatabase("EmergencyDetails", MODE_PRIVATE, null);

        yourName=findViewById(R.id.yourName);
        yourId=findViewById(R.id.yourId);
        appPass=findViewById(R.id.appPass);
        confirmAppPass=findViewById(R.id.confirmAppPass);
    }
}
