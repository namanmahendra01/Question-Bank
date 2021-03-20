package com.naman.questionbank.Adapters;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.naman.questionbank.R;
import com.naman.questionbank.ViewResourecActivity;
import com.naman.questionbank.discussionActivity;
import com.naman.questionbank.models.Question;
import com.naman.questionbank.models.Resource;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import static android.content.ContentValues.TAG;


public class AdaperForum extends RecyclerView.Adapter<AdaperForum.ViewHolder> {


    private Context mContext;
    private List<Question> questionList;

    public AdaperForum(Context mContext, List<Question> questionList) {
        this.mContext = mContext;
        this.questionList = questionList;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = null;
        view = LayoutInflater.from(mContext).inflate(R.layout.forum_item, parent, false);
        return new AdaperForum.ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int i) {


       Question question= questionList.get(i);

       holder.tag.setText(question.getTg().replace(","," "));
       holder.ques.setText(question.getQue());
       holder.views.setText(question.getV());
       holder.timestamp.setText(getDate(question.getTim()));
       getUsername(question,holder);


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(mContext, discussionActivity.class);
                i.putExtra("date",getDate(question.getTim()));
                i.putExtra("tag",holder.tag.getText());
                i.putExtra("views",holder.views.getText());
                i.putExtra("ques",holder.ques.getText());
                i.putExtra("username",holder.username.getText());
                i.putExtra("qi",question.getQi());
                i.putExtra("ui",question.getUi());

                mContext.startActivity(i);

            }
        });



    }

    private void getUsername(Question question, ViewHolder holder) {
        DatabaseReference db= FirebaseDatabase.getInstance().getReference();
        db.child(mContext.getString(R.string.dbname_users))
                .child(question.getUi())
                .child(mContext.getString(R.string.username))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        holder.username.setText(snapshot.getValue().toString());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }


    public long getItemId(int position) {
        Question question = questionList.get(position);
        return question.getQi().hashCode();
    }
    @Override
    public int getItemViewType(int position) {
        return position;
    }
    @Override
    public int getItemCount() {
        return questionList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tag,username,ques,views,timestamp;




        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tag = itemView.findViewById(R.id.tags);
            username = itemView.findViewById(R.id.username);
            ques = itemView.findViewById(R.id.question);
            views = itemView.findViewById(R.id.views);
            timestamp = itemView.findViewById(R.id.date);


        }
    }

    private String getDate(String time) {
       Long time1=Long.parseLong(time);
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time1);
        String date = DateFormat.format("dd-MM-yyyy", cal).toString();
        return date;
    }

}
