package com.abhinay.people;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private AutoCompleteTextView m_emailField;
    private EditText m_passwordField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        m_emailField = findViewById(R.id.emailIdField);
        m_passwordField = findViewById(R.id.signInPasswordField);

//        m_passwordField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
//                if (id == R.id.login || id == EditorInfo.IME_NULL) {
//                    attemptLogin();
//                    return true;
//                }
//                return false;
//            }
//        });
        Log.d("people", "inside on create");

        mAuth = FirebaseAuth.getInstance();

    }

    public void signInExistingUser(View v){
        Log.d("people", "inside on signInExistingUser");
        attemptLogin();
    }

    public void registerNewUser(View v){
        Log.d("people", "inside on registerNewUser");
        Intent myIntent = new Intent(MainActivity.this, signUpActivity.class);
        finish();
        startActivity(myIntent);
    }

    private void attemptLogin(){
        Log.d("people", "inside on attemptLogin");
        String email = m_emailField.getText().toString();
        String password = m_passwordField.getText().toString();

        if(email.equals("") || password.equals("")) return;
        Toast.makeText(this, "Login in progress...", Toast.LENGTH_SHORT).show();

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.d("people", "inside on onComplete");

                Log.d("FlashChat", "signInWithEmail() onComplete: " + task.isSuccessful());

                if (!task.isSuccessful()) {
                    Log.d("people", "inside on taskisSuccessfull");
                    Log.d("FlashChat", "Problem signing in: " + task.getException());
                    showErrorDialog("There was a problem signing in");
                } else {
                    Intent intent = new Intent(MainActivity.this, socialActivity.class);
                    finish();
                    startActivity(intent);
                }

            }
        });
    }

    private void showErrorDialog(String message) {

        new AlertDialog.Builder(this)
                .setTitle("Oops")
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}
