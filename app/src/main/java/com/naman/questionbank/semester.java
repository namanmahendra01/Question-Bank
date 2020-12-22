package com.naman.questionbank;

import android.Manifest;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.facebook.ads.AudienceNetworkAds;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.BaseMultiplePermissionsListener;

import java.util.Arrays;
import java.util.List;
import java.util.Map;




public class semester extends AppCompatActivity {

    Button button;
    FirebaseFirestore firestore;
    Button button1;
    public static String value;
    public static String text;
    public static String text2;
    public static String text3;
    public static String text8;
    private static final String TAG = "semester";
    private AdView adView;


   @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_semester);


        FirebaseApp.initializeApp(this);
        firestore=FirebaseFirestore.getInstance();


       AudienceNetworkAds.initialize(this);
       adView = new AdView(this, getString(R.string.placement_Id), AdSize.BANNER_HEIGHT_50);
       // Find the Ad Container
       LinearLayout adContainer = (LinearLayout) findViewById(R.id.banner_container);
       // Add the ad view to your activity layout
       adContainer.addView(adView);
       // Request an ad
       adView.loadAd();
  Dexter.withActivity(this)
                .withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new BaseMultiplePermissionsListener(){
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        super.onPermissionsChecked(report);
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        super.onPermissionRationaleShouldBeShown(permissions, token);
                    }
                })
                .check();


        Intent intent = getIntent();
        String item= intent.getStringExtra("item");
        Spinner spinner3= findViewById(R.id.spinner3);
        int id=this.getResources().getIdentifier(item,"array",this.getPackageName());

            String[] subject = getResources().getStringArray(id);
            Arrays.sort(subject);
            ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, subject);
            adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner3.setAdapter(adapter2);
            spinner3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    Toast.makeText(getBaseContext(), parent.getItemIdAtPosition(position) + " selected", Toast.LENGTH_SHORT).show();
                }


                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });


        button=findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                text8 = "aktu1";
                String text3 = ((Spinner) findViewById(R.id.spinner3)).getSelectedItem().toString();
                if (text3.equalsIgnoreCase("")) {
                    Toast.makeText(semester.this, "Sorry!PDF is not available right now", Toast.LENGTH_SHORT).show();

                } else {
                    Intent intent = getIntent();
                    String text = intent.getStringExtra("item1");
                    String text2 = intent.getStringExtra("item2");

                    DocumentReference call = firestore.collection(text8).document(text);
                    call.collection(text2).document(text3)
                            .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                            @Override
                                                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                                if (documentSnapshot.exists()) {
                                                                    Map<String, Object> map = documentSnapshot.getData();
                                                                    if (map.size() != 0) {

                                                                        POJO x = documentSnapshot.toObject(POJO.class);
                                                                        value = x.getValue();
                                                                        Intent intent = new Intent(semester.this, ViewActivity.class);
                                                                        intent.putExtra("ViewType", "internet");
                                                                        intent.putExtra("link", value);
                                                                        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                                                                        startActivity(intent);
                                                                    } else {
                                                                        Toast.makeText(semester.this, "Sorry!PDF is not available right now,We will try to make it available in future", Toast.LENGTH_SHORT).show();
                                                                    }
                                                                } else {
                                                                    Toast.makeText(semester.this, "Sorry!PDF is not available right now,We will try to make it available in future", Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        }
                    ).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(semester.this, "success", Toast.LENGTH_SHORT).show();

                        }
                    });
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
