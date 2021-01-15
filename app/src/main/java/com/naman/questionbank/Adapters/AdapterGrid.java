package com.naman.questionbank.Adapters;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
import com.naman.questionbank.models.Resource;

import java.util.ArrayList;
import java.util.List;


public class AdapterGrid extends RecyclerView.Adapter<AdapterGrid.ViewHolder> {


    private Context mContext;
    private List<Resource> resourceList;

    public AdapterGrid(Context mContext, List<Resource> resourceList) {
        this.mContext = mContext;
        this.resourceList = resourceList;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = null;
            view = LayoutInflater.from(mContext).inflate(R.layout.resource_item, parent, false);
            return new AdapterGrid.ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int i) {

        Display display = ((Activity)mContext).getWindowManager().getDefaultDisplay();
        int width = display.getWidth(); // ((display.getWidth()*20)/100)
        CardView.LayoutParams parms = new CardView.LayoutParams(width/3,width/3);
        holder.image.setLayoutParams(parms);

        Resource resource = resourceList.get(i);
        String[] tagsArray = resource.getTg().split(",");
        holder.tag1.setText(tagsArray[0]);
        holder.tag2.setText(tagsArray[1]);
        holder.tag3.setText(tagsArray[2]);



    }




    public long getItemId(int position) {
        Resource resource = resourceList.get(position);
        return resource.getRi().hashCode();
    }
    @Override
    public int getItemViewType(int position) {
        return position;
    }
    @Override
    public int getItemCount() {
        return resourceList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tag1, tag2, tag3;
        ImageView image;



        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tag1 = itemView.findViewById(R.id.tag1);
            tag2 = itemView.findViewById(R.id.tag2);
            tag3 = itemView.findViewById(R.id.tag3);
            image = itemView.findViewById(R.id.image);


        }
    }



}
