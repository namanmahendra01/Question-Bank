package com.naman.questionbank.Adapters;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.naman.questionbank.R;
import com.naman.questionbank.discussionActivity;
import com.naman.questionbank.models.Question;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class AdapterSearchedQuestion extends ArrayAdapter<Question> {

    private static final String TAG = "UserListAdapter";

    private LayoutInflater mInflater;
    private List<Question> question= null;
    private int layoutResources;
    private Context mContext;

    public AdapterSearchedQuestion(@NonNull Context context, int resource, @NonNull List<Question> objects) {
        super(context, resource, objects);
        mContext=context;
        mInflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layoutResources=resource;
        this.question=objects;
    }
    private static class ViewHolder{
        TextView tag,username,ques,views,timestamp;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final ViewHolder holder;
        if(convertView==null){
            convertView=mInflater.inflate(layoutResources,parent,false);
            holder=new ViewHolder();


            holder. tag = (TextView)convertView.findViewById(R.id.tags);
            holder. username = (TextView)convertView.findViewById(R.id.username);
            holder.ques = (TextView)convertView.findViewById(R.id.question);
            holder.views = (TextView)convertView.findViewById(R.id.views);
            holder. timestamp = (TextView)convertView.findViewById(R.id.date);

            convertView.setTag(holder);
        }else{
            holder = (ViewHolder)convertView.getTag();
        }

        holder.tag.setText(question.get(position).getTg().replace(","," "));
        holder.ques.setText(question.get(position).getQue());
        holder.views.setText(question.get(position).getV());
        holder.timestamp.setText(getDate(question.get(position).getTim()));
        getUsername(question.get(position),holder);


        convertView.setOnClickListener(new View.OnClickListener() {

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


        return convertView;
    }
    private String getDate(String time) {
        Long time1=Long.parseLong(time);
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time1);
        String date = DateFormat.format("dd-MM-yyyy", cal).toString();
        return date;
    }
    private void getUsername(Question question,ViewHolder holder) {
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
