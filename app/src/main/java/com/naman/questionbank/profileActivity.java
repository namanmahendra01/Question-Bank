package com.naman.questionbank;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.naman.questionbank.Adapters.AdapterGrid;
import com.naman.questionbank.login.login;
import com.naman.questionbank.models.Comment;
import com.naman.questionbank.models.Resource;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class profileActivity extends AppCompatActivity {
    private static final String TAG = "profile";
    private TextView username, visitor, res,selectTv;
    private Button add;
    private RelativeLayout upper;
    private RecyclerView resourceRv;
    ArrayList<Resource> resourceArrayList;
    String userId="";
    private AdapterGrid adapterGrid;
    boolean ifFromShare=false;
    String fromShare=" ",quesId,anotherPerson ;

    int x=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        username = findViewById(R.id.username);
        visitor = findViewById(R.id.visitor);
        res = findViewById(R.id.res);
        add = findViewById(R.id.add);
        resourceRv = findViewById(R.id.Rv);
        upper = findViewById(R.id.info);
        selectTv = findViewById(R.id.selectText);

        Intent j = getIntent();
        userId=j.getStringExtra("ui");
        if (userId==null){
            userId=FirebaseAuth.getInstance().getCurrentUser().getUid();
        }else{
            if (!userId.equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
            add.setVisibility(View.GONE);
        }

        fromShare=j.getStringExtra("toShare");
        quesId=j.getStringExtra("qi");
        if (fromShare!=null) {
            if (fromShare.equals("true")) {
                ifFromShare = true;
                upper.setVisibility(View.GONE);
                selectTv.setVisibility(View.VISIBLE);
                add.setText("Share");

            }
        }

        getUsername();
        getVisitors();
        if (x==0) {
            getResource();
        }

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: ss");
                if (fromShare==null) {
                        Intent i = new Intent(profileActivity.this, AddResourceActivity.class);
                        startActivity(i);
                }else{
                    Log.d(TAG, "onClick: ss2");


                    SharedPreferences sp = profileActivity.this.getSharedPreferences("shared preferences", Context.MODE_PRIVATE);
                    String ui =sp.getString("ui",null);
                    String ri =sp.getString("ri",null);
                    if (ui==null){
                        Toast.makeText(profileActivity.this, "Nothing is selected", Toast.LENGTH_SHORT).show();
                    }else{
                        AlertDialog.Builder builder = new AlertDialog.Builder(profileActivity.this);
                        builder.setTitle("Share");
                        builder.setMessage("Share selected Resource?");
//                set buttons
                        builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                addNewComment(ui+",&&,"+ri);

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

                }
            }
        });


        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Intent i = new Intent(this, login.class);
            i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(i);
        }


    }
    private void addNewComment(String newComment) {
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
        String commentID = myRef.push().getKey();
        Comment comment = new Comment();
        comment.setC(newComment);
        comment.setTim(getTim());
        comment.setUi(FirebaseAuth.getInstance().getCurrentUser().getUid());
        myRef.child(getString(R.string.dbname_Discussion))
                .child(quesId)
                .child(commentID)
                .setValue(comment)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        finish();
                    }
                });

    }
    private String getTim() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH);
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));
        return sdf.format(new Date());
    }
    private void getVisitors() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child(getString(R.string.dbname_users))
                .child(userId)
                .child(getString(R.string.visitors))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            visitor.setText("visitors : "+snapshot.getValue().toString());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void getResource() {
        x++;
        resourceArrayList = new ArrayList<>();
        ArrayList<Resource> resourceArrayList1 = new ArrayList<>();


        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child(getString(R.string.dbname_Resource))
                .child(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            res.setText("Resource Provided : " + dataSnapshot.getChildrenCount());
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                Log.d(TAG, "onDataChange: 1");
                                Resource resource = snapshot.getValue(Resource.class);
                                resourceArrayList1.add(resource);
                            }
                            resourceArrayList.addAll(resourceArrayList1);

                            resourceRv.setHasFixedSize(true);
                            GridLayoutManager linearLayoutManager = new GridLayoutManager(profileActivity.this, 3);
                            resourceRv.setDrawingCacheEnabled(true);
                            resourceRv.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_LOW);
                            linearLayoutManager.setItemPrefetchEnabled(true);
                            linearLayoutManager.setInitialPrefetchItemCount(20);
                            resourceRv.setLayoutManager(linearLayoutManager);


                            adapterGrid = new AdapterGrid(profileActivity.this, resourceArrayList,ifFromShare);
                            adapterGrid.setHasStableIds(true);
                            resourceRv.setAdapter(adapterGrid);

                        }else{
                            res.setText("Resource Provided : 0" );

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void getUsername() {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        db.child(getString(R.string.dbname_users))
                .child(userId)
                .child(getString(R.string.username))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        username.setText(snapshot.getValue().toString());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: 11"+x);
        if(x==0){
            x++;
            getResource();
        }

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onResume: 12" +x);

        if(x==0){
            x++;
            getResource();
        }


    }
}