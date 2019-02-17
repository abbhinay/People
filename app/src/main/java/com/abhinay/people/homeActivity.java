package com.abhinay.people;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class homeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
    }

    public void goToChat(View v){
        Intent chatIntent = new Intent(homeActivity.this, chatActivity.class);
        finish();
        startActivity(chatIntent);
    }

    public void goToDocument(View v){
        Intent docIntent = new Intent(homeActivity.this, document.class);
        finish();
        startActivity(docIntent);
    }

    public void development(View v){
        Toast mToastMessage;
        mToastMessage = Toast.makeText(this, "this feature is in development", Toast.LENGTH_SHORT);
        mToastMessage.show();
    }
}
