package com.naman.questionbank;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.facebook.ads.AudienceNetworkAds;


public class help extends AppCompatActivity {
Button button3;
    private static final String TAG = "help";
    private AdView adView;


   @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

       AudienceNetworkAds.initialize(this);
       adView = new AdView(this, getString(R.string.placement_Id), AdSize.BANNER_HEIGHT_50);
       // Find the Ad Container
       LinearLayout adContainer = (LinearLayout) findViewById(R.id.banner_container);
       // Add the ad view to your activity layout
       adContainer.addView(adView);
       // Request an ad
       adView.loadAd();

  button3= findViewById(R.id.feed);

        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(help.this, feedback.class);

                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);

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
