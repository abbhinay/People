package com.abhinay.people;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class homeActivity extends AppCompatActivity {

    TextView bunkMessageViewer;
    LinearLayout bunkInfoContainer;
    RadioButton yesButton;
    RadioButton noButton;
    LinearLayout bunkResultContainer;
    TextView positiveResult;
    TextView negativeResult;
    TextView yetToVote;
    String issue;
    FirebaseDatabase database;
    FirebaseFirestore db;

    int agree = 0, disagree = 0, neutral = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        db = FirebaseFirestore.getInstance();

        bunkMessageViewer = (TextView) findViewById(R.id.bunkMessageViewer);
        bunkInfoContainer = (LinearLayout) findViewById(R.id.bunkInfoContainer);
        yesButton = (RadioButton) findViewById(R.id.yesButton);
        noButton = (RadioButton) findViewById(R.id.noButton);
        bunkResultContainer = (LinearLayout) findViewById(R.id.bunkResultsContainer);
        positiveResult = (TextView) findViewById(R.id.positiveResult);
        negativeResult = (TextView) findViewById(R.id.negativeResult);
        yetToVote = (TextView) findViewById(R.id.yetToVote);


        db.collection("bunk")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String issueFromFirestore = document.getId();
                                Log.d("issue", issueFromFirestore);
                                if(issueFromFirestore!="No Bunk Topic"){
                                    issue = issueFromFirestore;
                                    bunkMessageViewer.setText(issueFromFirestore);
                                    bunkInfoContainer.setVisibility(View.VISIBLE);
                                }else{
                                    Log.d("issue", "issue from firestore is null");
                                }
                                //String url = document.getData().get(fileName).toString();
                                //Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                        } else {
                            Log.d("issue", "inside the else");
                        }
                    }
                });

    }


    public void onRadioButtonClicked(View v){
        boolean checked = ((RadioButton) v).isChecked();
        switch(v.getId()){
            case R.id.yesButton:
                if(checked){
                    Log.d("radio", "yes pressed");
                    uploadissueToFirestore(true);
                }
                break;

            case R.id.noButton:
                if(checked){
                    Log.d("radio", "no pressed");
                    uploadissueToFirestore(false);
                }
                break;
        }
    }

    public void showBunkStats(){

        db.collection("bunk")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                //String issueFromFirestore = document.getId();
                                  //Log.d("issue", document.getData().toString());
                                //String url = document.getData().get(fileName).toString();
                                //Log.d(TAG, document.getId() + " => " + document.getData());

                                String[] issuekeys = document.getData().keySet().toArray(new String[0]);
                                Log.d("nextissue", issuekeys[0]);
                                for(int i=0; i<issuekeys.length; i++){
                                    if(String.valueOf(document.getData().get(issuekeys[i])).equals("yes")){
                                        Log.d("nextissue", "yes");
                                        agree += 1;
                                    }else{
                                        Log.d("nextissue", "no");
                                        disagree += 1;
                                    }
                                }

                                positiveResult.setText(agree + " people agree with the bunk");
                                negativeResult.setText(disagree + " people disagree with the bunk");

                                bunkResultContainer.setVisibility(View.VISIBLE);

                                agree = 0;
                                disagree = 0;
//                                String result = String.valueOf(document.getData().get(issuekeys[0]));
//                                Log.d("nextissue", result);
//                                String value = document.getData().toString();
//                                Log.d("nextissue", value);
//                                if (value.equals("yes")) {
//                                    Log.d("nextissue", "agree");
//                                }else{
//                                    Log.d("nextissue", "disagree");
//                                }
                                //String[] issuekeys = document.getData().keySet().toArray(new String[0]);
                                //Log.d("nextissue", document.getData().keySet().toString());
//                                for(int i=0; i<issuekeys.length; i++){
//                                    if(document.getData().get(issuekeys[i]).toString()=="yes"){
//                                        agree += 1;
//                                        Log.d("nextissue", "agree");
//                                    }else{
//                                        disagree += 1;
//                                        Log.d("nextissue", "disagree");
//                                    }
//                                }

                            }
                        } else {
                            Log.d("issue", "inside the else");
                        }
                    }
                });

    }

    public void uploadissueToFirestore(boolean vote){
        Log.d("vote", vote?"yes":"no");

        SharedPreferences prefs = getSharedPreferences(signUpActivity.CHAT_PREFS, MODE_PRIVATE);
        String emailId = prefs.getString(signUpActivity.DISPLAY_EMAIL_KEY, null);

        if(issue!=null){
            DocumentReference docRef = db.collection("bunk").document(issue);
            Map data = new HashMap<>();
            data.put(emailId, vote?"yes":"no");
            docRef.set(data, SetOptions.merge());
            showBunkStats();
        }else{
            Log.d("vote", "there is no issue");
        }

    }

    public void createBunkRequestButtonPressed(View v){

        if(issue==null){

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Title");

            final EditText input = new EditText(this);

            input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
            builder.setView(input);

            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    issue = input.getText().toString();
//                Log.d("issue", issue);
                    bunkMessageViewer.setText(issue);
                    Toast.makeText(homeActivity.this, "to really upload the issue cast your vote by selecting one of the radio buttons", Toast.LENGTH_SHORT).show();
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            builder.show();

        }
        else{
            Toast.makeText(this, "sorry there is already a issue", Toast.LENGTH_SHORT).show();
       }

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
