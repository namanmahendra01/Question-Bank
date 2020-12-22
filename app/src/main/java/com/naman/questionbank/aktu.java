package com.naman.questionbank;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.facebook.ads.AudienceNetworkAds;

public class aktu extends AppCompatActivity {

    Button button1;
    public static String text6;
    public static String text4;
    public static String text5;
    private static final String TAG = "aktu";
    private AdView adView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aktu);

        AudienceNetworkAds.initialize(this);
        adView = new AdView(this, getString(R.string.placement_Id), AdSize.BANNER_HEIGHT_50);
        // Find the Ad Container
        LinearLayout adContainer = (LinearLayout) findViewById(R.id.banner_container);
        // Add the ad view to your activity layout
        adContainer.addView(adView);
        // Request an ad
        adView.loadAd();
        Spinner spinner;
        ArrayAdapter<CharSequence> adapter;

        spinner = findViewById(R.id.spinner);
        adapter = ArrayAdapter.createFromResource(this, R.array.semester, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                Toast.makeText(getBaseContext(), parent.getItemIdAtPosition(position) + " selected", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        Spinner spinner4;
        ArrayAdapter<CharSequence> adapter3;
        spinner4 = findViewById(R.id.spinner4);
        adapter3 = ArrayAdapter.createFromResource(this, R.array.year, android.R.layout.simple_spinner_item);

        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spinner4.setAdapter(adapter3);
        spinner4.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                Toast.makeText(getBaseContext(), parent.getItemIdAtPosition(position) + " selected", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

     button1= findViewById(R.id.button1);

     button1.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             String text4=((Spinner)findViewById(R.id.spinner)).getSelectedItem().toString();
             String text5=((Spinner)findViewById(R.id.spinner4)).getSelectedItem().toString();
             text6=text4.concat(text5);
             if (text4.equalsIgnoreCase("Select Semester")|| text5.equalsIgnoreCase("Select Year"))
             {
                 Toast.makeText(aktu.this,"Please select correct option",Toast.LENGTH_SHORT).show();

             }
             else {
                 Intent intent = new Intent(aktu.this, semester.class);
                 intent.putExtra("item", text6);
                 intent.putExtra("item1", text4);
                 intent.putExtra("item2", text5);
                 intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                 startActivity(intent);
             }
         }
     });


    }
    @Override
    protected void onDestroy() {
        if (adView != null) {
            adView.destroy();
        }
//        if (interstitialAd != null) {
//            interstitialAd.destroy();
//        }
        super.onDestroy();
    }
}



