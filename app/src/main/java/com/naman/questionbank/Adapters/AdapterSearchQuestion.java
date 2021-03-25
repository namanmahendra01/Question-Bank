package com.naman.questionbank.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
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
import com.naman.questionbank.models.Comment;
import com.naman.questionbank.models.Question;
import com.naman.questionbank.models.Resource;
import com.naman.questionbank.profileActivity;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import static android.content.ContentValues.TAG;


public class AdapterSearchQuestion extends RecyclerView.Adapter<AdapterSearchQuestion.ViewHolder> {

    private final Context mContext;

    private final List<Question>  question;

    public AdapterSearchQuestion(Context mContext, List<Question> question) {
        this.mContext = mContext;
        this.question=question;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.forum_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {


        holder.tag.setText(question.get(position).getTg().replace(","," "));
        holder.ques.setText(question.get(position).getQue());
        holder.views.setText(question.get(position).getV());
        holder.timestamp.setText(getDate(question.get(position).getTim()));
        getUsername(question.get(position),holder);



        holder.itemView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent i = new Intent(mContext, discussionActivity.class);
                i.putExtra("date",getDate(question.get(position).getTim()));
                i.putExtra("tag",holder.tag.getText());
                i.putExtra("views",holder.views.getText());
                i.putExtra("ques",holder.ques.getText());
                i.putExtra("username",holder.username.getText());
                i.putExtra("qi",question.get(position).getQi());
                i.putExtra("ui",question.get(position).getUi());
                mContext.startActivity(i);



            }
        });

        holder.username.setOnClickListener(v -> {
            Intent i1 = new Intent(mContext, profileActivity.class);
            i1.putExtra("visitor", "true");
            i1.putExtra("ui", question.get(position).getUi());
            mContext.startActivity(i1);
        });

    }

    @Override
    public long getItemId(int position) {
        Question question1 = question.get(position);
        return question1.getTim().hashCode();
    }

    @Override
    public int getItemCount() {
        return question.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tag,username,ques,views,timestamp;


        public ViewHolder(@NonNull View convertView) {
            super(convertView);
           tag = (TextView)convertView.findViewById(R.id.tags);
           username = (TextView)convertView.findViewById(R.id.username);
            ques = (TextView)convertView.findViewById(R.id.question);
            views = (TextView)convertView.findViewById(R.id.views);
             timestamp = (TextView)convertView.findViewById(R.id.date);

        }
    }
    private String getDate(String time) {
        Long time1=Long.parseLong(time);
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time1);
        String date = DateFormat.format("dd-MM-yyyy", cal).toString();
        return date;
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
}
