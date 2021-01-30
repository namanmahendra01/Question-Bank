package com.naman.questionbank.Forum;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.naman.questionbank.AddResourceActivity;
import com.naman.questionbank.R;
import com.naman.questionbank.profileActivity;

import java.util.HashMap;

public class AskQuestionActivity extends AppCompatActivity {

    private RadioGroup ifQues;
    private RadioButton ifQuesBtn;
    boolean ifResource = false;
    private String ques, tag1, tag2, tag3;
    private EditText quesET, t1, t2, t3;
    private Button addBtn;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ask_question);
        ifQues = findViewById(R.id.grp);
        ifQuesBtn = findViewById(R.id.grp1);

        quesET = findViewById(R.id.ques);
        t1 = findViewById(R.id.tag1);
        t2 = findViewById(R.id.tag2);
        t3 = findViewById(R.id.tag3);
        addBtn = findViewById(R.id.add);
        progressBar = findViewById(R.id.pro);

        ifQues.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                ifResource = radioGroup.getCheckedRadioButtonId() == ifQuesBtn.getId();
            }
        });


        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                checkFields();
            }
        });
    }

    private void checkFields() {
        if (quesET.getText().equals("") || t1.getText().equals("") || t2.getText().equals("") || t3.getText().equals("")) {
            progressBar.setVisibility(View.GONE);

            Toast.makeText(this, "Please complete All the fields", Toast.LENGTH_SHORT).show();
        } else {
            checkTags();
        }

    }
    private void checkTags() {
        tag1 = t1.getText().toString().toLowerCase();
        tag2 = t2.getText().toString().toLowerCase();
        tag3 = t3.getText().toString().toLowerCase();
        if (tag1.length() > 20) {
            progressBar.setVisibility(View.GONE);

            t1.setError("tag size must be less than or equal to 20 characters");
        } else if (tag2.length() > 20) {
            progressBar.setVisibility(View.GONE);

            t2.setError("tag size must be less than or equal to 20 characters");
        } else if (tag3.length() > 20) {
            progressBar.setVisibility(View.GONE);

            t3.setError("tag size must be less than or equal to 20 characters");
        } else {
            addToDatabase();
        }

    }


    private void addToDatabase() {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        String qi = db.push().getKey();

        Long tsLong = System.currentTimeMillis();
        String ts = tsLong.toString();

        HashMap<Object, String> hashMap = new HashMap<>();
        hashMap.put(getString(R.string.field_question_id), qi);
        hashMap.put(getString(R.string.field_user_id), FirebaseAuth.getInstance().getCurrentUser().getUid());
        hashMap.put(getString(R.string.field_question), quesET.getText().toString());
        hashMap.put(getString(R.string.field_tags), tag1 + "," + tag2 + "," + tag3);
        hashMap.put(getString(R.string.field_views), "0");
        hashMap.put(getString(R.string.field_timestamp), ts);

        db.child(getString(R.string.dbname_Questions))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(qi)
                .setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        addTags(getString(R.string.dbname_QuestionTags),qi,db);

                    }
                });

    }

    private void addTags(String string, String qi, DatabaseReference db) {
        db.child(string)
                .child(tag1)
                .child(qi)
                .setValue(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        db.child(string)
                                .child(tag2)
                                .child(qi)
                                .setValue(FirebaseAuth.getInstance().getCurrentUser().getUid())                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                db.child(string)
                                        .child(tag3)
                                        .child(qi)
                                        .setValue(FirebaseAuth.getInstance().getCurrentUser().getUid())                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        if (ifResource){
                                            ifResource=false;
                                            addTags(getString(R.string.dbname_ResourceBasedQuestionsTags),qi,db);

                                        }else {
                                            Toast.makeText(AskQuestionActivity.this, "Question published!", Toast.LENGTH_SHORT).show();
                                            Intent i = new Intent(AskQuestionActivity.this, forumActivity.class);
                                            startActivity(i);
                                        }
                                    }
                                });
                            }
                        });
                    }
                });
    }


}