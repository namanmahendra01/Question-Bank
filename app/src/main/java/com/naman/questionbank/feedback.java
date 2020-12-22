package com.naman.questionbank;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class feedback extends AppCompatActivity {
   private EditText email;
    private EditText subject;
    private EditText message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        email=findViewById(R.id.email);
        subject=findViewById(R.id.subject);
        message=findViewById(R.id.message);
      Button  send=findViewById(R.id.send);

      send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendmail();

            }
        });


    }
    private  void sendmail(){
        String reciepentlist=email.getText().toString();
        String[] recipients=reciepentlist.split(",");
        String subjecttext=subject.getText().toString();
        String messagetext=message.getText().toString();

        Intent intent =new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_EMAIL,recipients);
        intent.putExtra(Intent.EXTRA_SUBJECT,subjecttext);
        intent.putExtra(Intent.EXTRA_TEXT,messagetext);
        intent.setType("message/rfc822");
        intent.setPackage("com.google.android.gm");
        startActivity(intent);


    }


}

