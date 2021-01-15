package com.naman.questionbank;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class AddResourceActivity extends AppCompatActivity {

    private String link,tag1,tag2,tag3,title;
    private EditText linkEt,t1,t2,t3,titleEt;
    private Button addBtn;
    private ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_resource);

        linkEt=findViewById(R.id.link);
        t1=findViewById(R.id.tag1);
        t2=findViewById(R.id.tag2);
        t3=findViewById(R.id.tag3);
        addBtn=findViewById(R.id.add);
        titleEt=findViewById(R.id.title);
        progressBar=findViewById(R.id.pro);



        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                checkFields();
            }
        });
    }

    private void checkFields() {
        if (linkEt.getText().equals("")||t1.getText().equals("")||t2.getText().equals("")||t3.getText().equals("")||titleEt.getText().equals("")){
            progressBar.setVisibility(View.GONE);

            Toast.makeText(this, "Please complete All the fields", Toast.LENGTH_SHORT).show();
        }else{
            checkLink();
        }

    }

    private void checkLink() {
        link=linkEt.getText().toString();
        boolean isUrl=Patterns.WEB_URL.matcher(link).matches();
        if (isUrl){
            checkTags();
        }else{
            progressBar.setVisibility(View.GONE);

            Toast.makeText(this, "Link is invaid.", Toast.LENGTH_SHORT).show();
        }

    }

    private void checkTags() {
        tag1=t1.getText().toString();
        tag2=t2.getText().toString();
        tag3=t3.getText().toString();
        if (tag1.length()>20){
            progressBar.setVisibility(View.GONE);

            t1.setError("tag size must be less than or equal to 20 characters");
        }else if(tag2.length()>20){
            progressBar.setVisibility(View.GONE);

            t2.setError("tag size must be less than or equal to 20 characters");
        }else if(tag3.length()>20){
            progressBar.setVisibility(View.GONE);

            t3.setError("tag size must be less than or equal to 20 characters");
        }else{

            checkTitle();
        }

    }

    private void checkTitle() {
        title=titleEt.getText().toString();
        if (title.length()>80){
            progressBar.setVisibility(View.GONE);

            titleEt.setError("title size must be less than or equal to 80 characters");
        }else{
            addToDatabase();
        }
    }

    private void addToDatabase() {
        DatabaseReference db= FirebaseDatabase.getInstance().getReference();
        String ri= db.push().getKey();

        Long tsLong = System.currentTimeMillis();
        String ts = tsLong.toString();

        HashMap<Object,String> hashMap= new HashMap<>();
        hashMap.put(getString(R.string.field_resource_id),ri);
        hashMap.put(getString(R.string.field_user_id), FirebaseAuth.getInstance().getCurrentUser().getUid());
        hashMap.put(getString(R.string.field_title),title);
        hashMap.put(getString(R.string.field_tags),tag1+","+tag2+","+tag3);
        hashMap.put(getString(R.string.field_link),link);
        hashMap.put(getString(R.string.field_timestamp),ts);

        db.child(getString(R.string.dbname_Resource))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(ri)
                .setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(AddResourceActivity.this, "Resource added!", Toast.LENGTH_SHORT).show();
                        Intent i=new Intent(AddResourceActivity.this,profileActivity.class);
                        startActivity(i);
                    }
                });

    }
}