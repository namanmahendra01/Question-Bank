package com.naman.questionbank;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.naman.questionbank.Adapters.AdapterGrid;
import com.naman.questionbank.login.login;
import com.naman.questionbank.models.Resource;

import java.util.ArrayList;

public class profileActivity extends AppCompatActivity {
    private static final String TAG = "profile";
    private TextView username, visitor, res;
    private Button add;
    private RecyclerView resourceRv;
    ArrayList<Resource> resourceArrayList;
    private AdapterGrid adapterGrid;
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

        getUsername();
        getVisitors();
        if (x==0) {
            getResource();
        }

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(profileActivity.this,AddResourceActivity.class);
                startActivity(i);
            }
        });


        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Intent i = new Intent(this, login.class);
            i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(i);
        }


    }

    private void getVisitors() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child(getString(R.string.dbname_users))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
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
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
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


                            adapterGrid = new AdapterGrid(profileActivity.this, resourceArrayList);
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
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
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
        x++;
            getResource();

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        x++;
            getResource();

    }
}