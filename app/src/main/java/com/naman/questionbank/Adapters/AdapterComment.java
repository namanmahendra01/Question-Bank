package com.naman.questionbank.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Typeface;
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
import com.naman.questionbank.models.Comment;
import com.naman.questionbank.models.Resource;
import com.naman.questionbank.profileActivity;


import java.util.List;
import java.util.Objects;

import static android.content.ContentValues.TAG;


public class AdapterComment extends RecyclerView.Adapter<AdapterComment.ViewHolder> {

    private final Context mContext;
    private final List<Comment> comments;
    private final List<String> commentId;
    private final String quesId;
    private final String mUserId;

    public AdapterComment(Context mContext, List<Comment> comments, List<String> commentId, String quesId, String mUserId) {
        this.mContext = mContext;
        this.comments = comments;
        this.commentId = commentId;
        this.quesId = quesId;
        this.mUserId = mUserId;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_commets, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder,  final int i) {
        Comment comment = comments.get(i);
        int currentPosition=i;

        if (comment.getC().contains(",&&,"))
        {
            String[] cmntArray = comment.getC().split(",&&,");
            holder.ui=cmntArray[0];
            holder.ri=cmntArray[1];
            holder.comment.setText("Resource Shared");
            holder.comment.setTextSize(24);
            holder.comment.setPaintFlags(holder.comment.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);
            holder.comment.setTextColor(mContext.getResources().getColor(R.color.deeppurple));
            holder.comment.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD_ITALIC));

        }else{
            holder.comment.setText(comment.getC());

        }


        holder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (comment.getC().contains(",&&,")){
                    Log.d(TAG, "onClick: "+holder.ui+holder.ri);
                    DatabaseReference db= FirebaseDatabase.getInstance().getReference();
                    db.child(mContext.getString(R.string.dbname_Resource))
                            .child(holder.ui)
                            .child(holder.ri)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()) {
                                        Resource resource = snapshot.getValue(Resource.class);
                                        String[] tagsArray = resource.getTg().split(",");
                                        Intent i = new Intent(mContext, ViewResourecActivity.class);
                                        i.putExtra("link", resource.getLk());
                                        i.putExtra("ui", resource.getUi());
                                        i.putExtra("ri", resource.getRi());
                                        i.putExtra("t1", tagsArray[0]);
                                        i.putExtra("t2", tagsArray[1]);
                                        i.putExtra("t3", tagsArray[2]);
                                        i.putExtra("tit", resource.getTtl());
                                        i.putExtra("ui", comment.getUi());


                                        mContext.startActivity(i);
                                    }else{
                                        Toast.makeText(mContext, "Resource does not exit.", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                }
            }
        });
        holder.timestamp.setText(comment.getTim().substring(0, 10));

        getUserdetail(comment.getUi(), holder.username);

        PopupMenu popup = new PopupMenu(mContext, holder.editButton);
        popup.getMenuInflater().inflate(R.menu.post_comment, popup.getMenu());
        if (!comment.getUi().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
            holder.editButton.setVisibility(View.GONE);
        else {
            holder.editButton.setOnClickListener(v -> popup.show());
            popup.setOnMenuItemClickListener(item -> {
                if (item.getTitle().equals("Delete Comment")) {
                    DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
                    myRef.child(mContext.getString(R.string.dbname_Discussion))
                            .child(quesId)
                            .child(commentId.get(i))
                            .removeValue()
                            .addOnCompleteListener(task -> {
                                if (i == comments.size() || i == commentId.size()) {
                                    Toast.makeText(mContext, "Error", Toast.LENGTH_SHORT).show();
                                } else {
                                    comments.remove(currentPosition);
                                    commentId.remove(currentPosition);
                                    Log.d(TAG, "onBindViewHolder: "+currentPosition+"   "+i);
                                   AdapterComment.this. notifyItemRemoved(i);
                                    Toast.makeText(mContext, "Deleted Successfully!", Toast.LENGTH_SHORT).show();

                                }
                            })
                            .addOnFailureListener(e -> Toast.makeText(mContext, "Unsuccessful", Toast.LENGTH_SHORT).show());

                }
                return true;
            });
        }
        holder.username.setOnClickListener(v -> {
            Intent i1 = new Intent(mContext, profileActivity.class);
            i1.putExtra("visitor", "true");
            i1.putExtra("ui", comment.getUi());
            mContext.startActivity(i1);
        });


    }

    private void getUserdetail(String user_id, TextView username) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child(mContext.getString(R.string.dbname_users))
                .child(user_id)
                .child(mContext.getString(R.string.username));
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                username.setText(dataSnapshot.getValue().toString());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    @Override
    public long getItemId(int position) {
        return commentId.get(position).hashCode();
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView comment, username, timestamp;
        ImageButton editButton;
        String ui,ri;

        public ViewHolder(@NonNull View convertView) {
            super(convertView);
            comment = convertView.findViewById(R.id.addcomment);
            username = convertView.findViewById(R.id.comment_username);
            timestamp = convertView.findViewById(R.id.comment_time_posted);
            editButton = convertView.findViewById(R.id.edit);
        }
    }
}
