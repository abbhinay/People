package com.abhinay.people;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class signUpActivity extends AppCompatActivity {

    static final String CHAT_PREFS = "ChatPrefs";
    static final String DISPLAY_NAME_KEY = "username";
    static final String DISPLAY_EMAIL_KEY = "emailId";
    static final String DISPLAY_PASSWORD_KEY = "password";

    private AutoCompleteTextView m_signUp_username;
    private AutoCompleteTextView m_signUp_emailId;
    private EditText m_signUp_password;
    private EditText m_signUp_confirm_password;

    //Firebase instance variable to handle authentication
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        m_signUp_username = (AutoCompleteTextView) findViewById(R.id.signUp_username);
        m_signUp_emailId = (AutoCompleteTextView) findViewById(R.id.signUp_emailId);
        m_signUp_password = (EditText) findViewById(R.id.signUp_password);
        m_signUp_confirm_password = (EditText) findViewById(R.id.signUp_confirm_password);

//        m_signUp_confirm_password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
//                if (id == R.id.register_form_finished || id == EditorInfo.IME_NULL) {
//                    attemptRegistration();
//                    return true;
//                }
//                return false;
//            }
//        });

        mAuth = FirebaseAuth.getInstance();
    }

    public void signUpClick(View V){
        attemptRegistration();
    }

    public void attemptRegistration(){
        m_signUp_emailId.setError(null);
        m_signUp_password.setError(null);

        String email = m_signUp_emailId.getText().toString();
        String password = m_signUp_password.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if(!TextUtils.isEmpty(password) && !isPasswordValid(password)){
            m_signUp_password.setError("Password too short or does not match");
            focusView = m_signUp_password;
            cancel = true;
        }

        if (TextUtils.isEmpty(email)) {
            m_signUp_emailId.setError("This field is required");
            focusView = m_signUp_emailId;
            cancel = true;
        } else if (!isEmailValid(email)) {
            m_signUp_emailId.setError("This email address is invalid");
            focusView = m_signUp_emailId;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // TODO: Call create FirebaseUser() here
            createFirebaseUser();

        }

    }

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        String confirmPassword = m_signUp_confirm_password.getText().toString();
        return confirmPassword.equals(password) && password.length() > 4;
    }

    private void createFirebaseUser(){
        String email = m_signUp_emailId.getText().toString();
        String password = m_signUp_password.getText().toString();

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this,
                new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("FlashChat", "createUser onComplete: " + task.isSuccessful());

                        if(!task.isSuccessful()){
                            Log.d("FlashChat", "user creation failed");
                            showErrorDialog("Registration attempt failed");
                        } else {
                            saveDisplayName();
                            Intent intent = new Intent(signUpActivity.this, MainActivity.class);
                            finish();
                            startActivity(intent);
                        }
                    }
                });

    }

    private void saveDisplayName() {
        String displayName = m_signUp_username.getText().toString();
        String emailId = m_signUp_emailId.getText().toString();
        String password = m_signUp_password.getText().toString();
        SharedPreferences prefs = getSharedPreferences(CHAT_PREFS, 0);
        prefs.edit().putString(DISPLAY_NAME_KEY, displayName).apply();
        prefs.edit().putString(DISPLAY_EMAIL_KEY, emailId).apply();
        prefs.edit().putString(DISPLAY_PASSWORD_KEY, password).apply();
    }

    private void showErrorDialog(String message){

        new AlertDialog.Builder(this)
                .setTitle("Oops")
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();

    }
}
