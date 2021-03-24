package com.naman.questionbank;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ViewResourecActivity extends AppCompatActivity {
    private Button open,copy;
    private ImageView del,cross;
    private TextView tag1,tag2,tag3,title,linkTv;
    private String ri,t1,t2,t3,visitor;
    String userId;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_resourec);

        open= findViewById(R.id.open);
        copy= findViewById(R.id.copy);
        del= findViewById(R.id.delete);

        tag1= findViewById(R.id.tag1);
        tag2= findViewById(R.id.tag2);
        tag3= findViewById(R.id.tag3);
        title= findViewById(R.id.title);
        linkTv= findViewById(R.id.link);

        cross= findViewById(R.id.cross);

        Intent i=getIntent();
        String link=i.getStringExtra("link");
         userId=i.getStringExtra("ui");
         ri=i.getStringExtra("ri");
         t1=i.getStringExtra("t1");
         t2=i.getStringExtra("t2");
        t3=i.getStringExtra("t3");

        String ttl=i.getStringExtra("tit");
        try {

            if (!userId.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                del.setVisibility(View.GONE);

            }
        }catch (NullPointerException e){
            userId=FirebaseAuth.getInstance().getCurrentUser().getUid();
        }


        tag1.setText(t1);
        tag2.setText(t2);
        tag3.setText(t3);

        title.setText(ttl);
        linkTv.setText(link);




        cross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Uri uri = Uri.parse(link);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                  startActivity(intent);

                } catch (ActivityNotFoundException e) {
                    Toast.makeText(ViewResourecActivity.this, "Invalid Link", Toast.LENGTH_SHORT).show();
                }

            }
        });

        copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int sdk = android.os.Build.VERSION.SDK_INT;
                if (sdk < android.os.Build.VERSION_CODES.HONEYCOMB) {
                    ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    clipboard.setText(link);
                    Toast.makeText(ViewResourecActivity.this, "Link copied to clipboard!", Toast.LENGTH_SHORT).show();
                } else {
                    android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    android.content.ClipData clip = android.content.ClipData.newPlainText("link", link);
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(ViewResourecActivity.this, "Link copied to clipboard!", Toast.LENGTH_SHORT).show();

                }
            }
        });

        del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(ViewResourecActivity.this);
                builder.setTitle("Delete");
                builder.setMessage("Ae you sure you want to delete this resource?");

//                set buttons
                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteResource();

                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.create().show();

            }
        });

    }

    private void deleteResource() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child(getString(R.string.dbname_Resource))
                .child(userId)
                .child(ri)
                .removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        reference.child(getString(R.string.dbname_ResourceTags))
                                .child(t1)
                                .child(ri)
                                .removeValue()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        reference.child(getString(R.string.dbname_ResourceTags))
                                                .child(t2)
                                                .child(ri)
                                                .removeValue()
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        reference.child(getString(R.string.dbname_ResourceTags))
                                                                .child(t3)
                                                                .child(ri)
                                                                .removeValue()
                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void aVoid) {

                                                                            Toast.makeText(ViewResourecActivity.this, "Resource Deleted!", Toast.LENGTH_SHORT).show();
                                                                            Intent i = new Intent(ViewResourecActivity.this, profileActivity.class);
                                                                            startActivity(i);

                                                                    }
                                                                });
                                                    }
                                                });
                                    }
                                });

                    }
                });
    }
}