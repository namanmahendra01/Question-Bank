package com.naman.questionbank;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.naman.questionbank.Adapters.AdapterComment;
import com.naman.questionbank.models.Comment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import static android.content.ContentValues.TAG;

public class discussionActivity extends AppCompatActivity {
    TextView tag,username,ques,views,timestamp;
    String quesId;
    RecyclerView commentRv;
    private ArrayList<Comment> comments;
    private ArrayList<String> commentID;
    private static final String TAG = "CommentActivity";

    private AdapterComment adapterComment;



    private EditText mComment;
    String userId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discussion);
        mComment = findViewById(R.id.comment);

        tag = findViewById(R.id.tags);
        username = findViewById(R.id.username);
        ques = findViewById(R.id.question);
        views = findViewById(R.id.views);
        timestamp = findViewById(R.id.date);

        Intent i = getIntent();
        tag.setText(i.getStringExtra("tag"));
        views.setText(i.getStringExtra("views"));
        timestamp.setText(i.getStringExtra("date"));
        ques.setText(i.getStringExtra("ques"));
        username.setText(i.getStringExtra("username"));
        quesId=i.getStringExtra("qi");
        userId=i.getStringExtra("ui");
        Log.d(TAG, "onCreate: "+quesId);


        commentRv = findViewById(R.id.recyclerComment);
        commentRv.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        commentRv.setItemViewCacheSize(9);
        commentRv.setDrawingCacheEnabled(true);
        commentRv.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_LOW);
        linearLayoutManager.setItemPrefetchEnabled(true);
        linearLayoutManager.setInitialPrefetchItemCount(20);
        commentRv.setLayoutManager(linearLayoutManager);

        comments = new ArrayList<>();
        commentID = new ArrayList<>();
        adapterComment = new AdapterComment(this, comments, commentID, quesId, userId);
        adapterComment.setHasStableIds(true);
        commentRv.setAdapter(adapterComment);

        getComments(quesId, userId);
        if (!FirebaseAuth.getInstance().getCurrentUser().getUid().equals(userId)) {
            increaseViews();
        }

        ImageView mCheckMark = findViewById(R.id.checkMark);
        mCheckMark.setOnClickListener(v -> {
            if (!mComment.getText().toString().equals("")) {
                addNewComment(mComment.getText().toString());
                mComment.setText("");
                closeKeyboard();
            } else Toast.makeText(discussionActivity.this, "C'mon..Give a Shoutout", Toast.LENGTH_SHORT).show();

        });

        ImageView mBackArrow = findViewById(R.id.backarrow);
        ImageView shareBtn = findViewById(R.id.share);


        mBackArrow.setOnClickListener(v -> finish());
        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(discussionActivity.this, profileActivity.class);
                i.putExtra("toShare","true");
                i.putExtra("qi",quesId);
                startActivity(i);
            }
        });



    }

    private void increaseViews() {
        int x=Integer.parseInt(views.getText().toString());
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        db.child(getString(R.string.dbname_Questions))
                .child(userId)
                .child(quesId)
                .child(getString(R.string.field_views))
                .setValue(String.valueOf(x+1));

    }

    private String getTim() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH);
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));
        return sdf.format(new Date());
    }

    private void closeKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
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
                .setValue(comment);

    }
    private void getComments(String photoId, String userId) {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        db.child(getString(R.string.dbname_Discussion))
                .child(quesId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        comments.clear();
                        for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                            Comment comment = snapshot1.getValue(Comment.class);
                            comments.add(comment);
                            commentID.add(snapshot1.getKey());
                        }
                        adapterComment.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}