package com.abhinay.people;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class socialActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social);
    }

    public void goToChat(View v){
        Intent myIntent = new Intent(socialActivity.this, chatActivity.class);
        finish();
        startActivity(myIntent);
    }

    public void development(View v){
        Toast mToastMessage;
        mToastMessage = Toast.makeText(this, "this feature is in development", Toast.LENGTH_SHORT);
        mToastMessage.show();
    }
}
