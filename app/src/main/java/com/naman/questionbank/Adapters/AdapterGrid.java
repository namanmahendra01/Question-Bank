package com.naman.questionbank.Adapters;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.google.gson.Gson;
import com.naman.questionbank.R;
import com.naman.questionbank.ViewResourecActivity;
import com.naman.questionbank.models.Resource;
import com.naman.questionbank.profileActivity;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;


public class AdapterGrid extends RecyclerView.Adapter<AdapterGrid.ViewHolder> {


    private Context mContext;
    private List<Resource> resourceList;
    private  boolean fromShare;
    private  long lastItem=12 ;
    private boolean ifSelected;

    public AdapterGrid(Context mContext, List<Resource> resourceList,boolean fromShare) {
        this.mContext = mContext;
        this.resourceList = resourceList;
        this.fromShare=fromShare;
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

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (fromShare){
                    Log.d(TAG, "onClick: jkj"+holder.getItemId());

                    if (lastItem!=(holder.getItemId())) {
                        if(!ifSelected){
                            ifSelected=true;
                            lastItem = holder.getItemId();
                            holder.image.setBackgroundColor(mContext.getResources().getColor(R.color.transparentBlue));
                            SharedPreferences sp = mContext.getSharedPreferences("shared preferences", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor=sp.edit();
                            editor.putString("ri",resource.getRi());
                            editor.putString("ui",resource.getUi());
                            editor.apply();
                        }

                    }else{
                        lastItem = -1;
                        ifSelected=false;
                        SharedPreferences sp = mContext.getSharedPreferences("shared preferences", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor=sp.edit();
                        editor.putString("ri",null);
                        editor.putString("ui",null);
                        editor.apply();
                        holder.image.setBackgroundColor(mContext.getResources().getColor(R.color.white));

                    }

                }else {
                    Log.d(TAG, "onBindViewHolder: "+fromShare);

                    Intent i = new Intent(mContext, ViewResourecActivity.class);
                    i.putExtra("link", resource.getLk());
                    i.putExtra("ui", resource.getUi());
                    i.putExtra("ri", resource.getRi());
                    i.putExtra("t1", tagsArray[0]);
                    i.putExtra("t2", tagsArray[1]);
                    i.putExtra("t3", tagsArray[2]);
                    i.putExtra("tit", resource.getTtl());
                    mContext.startActivity(i);
                }

            }
        });



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
