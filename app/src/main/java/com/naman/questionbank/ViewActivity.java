package com.naman.questionbank;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.facebook.ads.AudienceNetworkAds;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.InterstitialAdListener;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnDrawListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.listener.OnPageErrorListener;
import com.github.barteksc.pdfviewer.listener.OnRenderListener;
import com.github.barteksc.pdfviewer.listener.OnTapListener;
import com.krishna.fileloader.FileLoader;
import com.krishna.fileloader.listener.FileRequestListener;
import com.krishna.fileloader.pojo.FileResponse;
import com.krishna.fileloader.request.FileLoadRequest;

import java.io.File;

public class ViewActivity extends AppCompatActivity {

    private static final String TAG ="ViewActivity" ;
    PDFView pdfview;
    ProgressBar progressbar;

    private AdView adView;
    private InterstitialAd interstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);
        pdfview = findViewById(R.id.pdf_viewer);
        progressbar = findViewById(R.id.progressbar);

        AudienceNetworkAds.initialize(this);
        adView = new AdView(this, getString(R.string.placement_Id), AdSize.BANNER_HEIGHT_50);
        // Find the Ad Container
        LinearLayout adContainer = (LinearLayout) findViewById(R.id.banner_container);
        // Add the ad view to your activity layout
        adContainer.addView(adView);
        // Request an ad
        adView.loadAd();

        interstitialAd = new InterstitialAd(this, getString(R.string.Placement_ID_Interstitial));

        // Create listeners for the Interstitial Ad
        InterstitialAdListener interstitialAdListener = new InterstitialAdListener() {
            @Override
            public void onInterstitialDisplayed(Ad ad) {
                // Interstitial ad displayed callback
                Log.e(TAG, "Interstitial ad displayed.");
            }

            @Override
            public void onInterstitialDismissed(Ad ad) {
                // Interstitial dismissed callback
                Log.e(TAG, "Interstitial ad dismissed.");
            }

            @Override
            public void onError(Ad ad, AdError adError) {
                // Ad error callback
                Log.e(TAG, "Interstitial ad failed to load: " + adError.getErrorMessage());
            }

            @Override
            public void onAdLoaded(Ad ad) {
                // Interstitial ad is loaded and ready to be displayed
                Log.d(TAG, "Interstitial ad is loaded and ready to be displayed!");
                // Show the ad
            }

            @Override
            public void onAdClicked(Ad ad) {
                // Ad clicked callback
                Log.d(TAG, "Interstitial ad clicked!");
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                // Ad impression logged callback
                Log.d(TAG, "Interstitial ad impression logged!");
            }
        };

        // For auto play video ads, it's recommended to load the ad
        // at least 30 seconds before it is shown
        interstitialAd.loadAd(
                interstitialAd.buildLoadAdConfig()
                        .withAdListener(interstitialAdListener)
                        .build());

        Intent intent = getIntent();
        String link = intent.getStringExtra("link");
        String viewType = getIntent().getStringExtra("ViewType");
        if (viewType != null || !TextUtils.isEmpty(viewType)) {
            if (viewType.equals("internet")) {
                progressbar.setVisibility(View.VISIBLE);


                FileLoader.with(this)
                        .load(link)
                        .fromDirectory("PDFFiles", FileLoader.DIR_EXTERNAL_PUBLIC)
                        .asFile(new FileRequestListener<File>() {
                            @Override
                            public void onLoad(FileLoadRequest fileLoadRequest, FileResponse<File> fileResponse) {
                                progressbar.setVisibility((View.GONE));


                                File pdfFile = fileResponse.getBody();

                                pdfview.fromFile(pdfFile)
                                        .password(null)
                                        .defaultPage(0)
                                        .enableSwipe(true)
                                        .swipeHorizontal(false)
                                        .enableDoubletap(true)
                                        .onDraw(new OnDrawListener() {
                                            @Override
                                            public void onLayerDrawn(Canvas canvas, float pageWidth, float pageHeight, int displayedPage) {

                                            }
                                        }).onDrawAll(new OnDrawListener() {
                                    @Override
                                    public void onLayerDrawn(Canvas canvas, float pageWidth, float pageHeight, int displayedPage) {

                                    }
                                }).onPageError(new OnPageErrorListener() {
                                    @Override
                                    public void onPageError(int page, Throwable t) {
                                        Toast.makeText(ViewActivity.this, "Error while open page" + page, Toast.LENGTH_SHORT).show();
                                    }
                                }).onPageChange(new OnPageChangeListener() {
                                    @Override
                                    public void onPageChanged(int page, int pageCount) {

                                    }
                                }).onTap(new OnTapListener() {
                                    @Override
                                    public boolean onTap(MotionEvent e) {
                                        return true;
                                    }
                                }).onRender(new OnRenderListener() {
                                    @Override
                                    public void onInitiallyRendered(int nbPages, float pageWidth, float pageHeight) {
                                        pdfview.fitToWidth();
                                    }
                                })
                                        .enableAnnotationRendering(true)
                                        .invalidPageColor(Color.WHITE)
                                        .load();


                            }

                            @Override
                            public void onError(FileLoadRequest fileLoadRequest, Throwable throwable) {
                                Toast.makeText(ViewActivity.this, "" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                progressbar.setVisibility((View.GONE));

                            }
                        });
            }


        }
    }

    @Override
    protected void onDestroy() {
        if (adView != null) {
            adView.destroy();
        }
        if (interstitialAd != null) {
            interstitialAd.destroy();
        }
        super.onDestroy();
    }
    @Override
    public void onBackPressed() {
        if(interstitialAd != null && interstitialAd.isAdLoaded()) interstitialAd.show();
        super.onBackPressed();
    }
}
