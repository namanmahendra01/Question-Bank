package com.naman.questionbank;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;

import android.os.Bundle;
import android.util.Log;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.facebook.ads.AudienceNetworkAds;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.naman.questionbank.Forum.forumActivity;


public class MainActivity extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "Main";
    private CardView aktupaper, iscpaper, cbsepaper, advancepaper, jeepaper, ndapaper, neetpaper, cdspaper, gatepaper, iprepaper, imainpaper, cmainpaper, cprepaper;
    private DrawerLayout drawer;
    Button draw;
    private TextView login, username,enterForum;
    private AdView adView;
    MenuItem signIn;
    boolean isSignedIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        login = findViewById(R.id.login);
        username = findViewById(R.id.username);
        enterForum = findViewById(R.id.enterForum);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);


        // get menu from navigationView
        Menu menu = navigationView.getMenu();

        // find MenuItem you want to change
        signIn = menu.findItem(R.id.logout);


        isSignedIn = FirebaseAuth.getInstance().getCurrentUser() != null;
        if (!isSignedIn) {
            login.setVisibility(View.VISIBLE);
            username.setVisibility(View.GONE);
            setText("Log in");
        } else {
            login.setVisibility(View.GONE);
            username.setVisibility(View.VISIBLE);
            setText("Log out");
            getUsername();
        }

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, com.naman.questionbank.login.login.class);
                startActivity(i);
            }
        });

        enterForum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isSignedIn) {
                    Intent i = new Intent(MainActivity.this, forumActivity.class);
                    startActivity(i);
                }else{
                    showDialogueForLogin("You have to login to enter into forum.");
                }
            }
        });
        username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, profileActivity.class);
                startActivity(i);
            }
        });

        AudienceNetworkAds.initialize(this);
        adView = new AdView(this, getString(R.string.placement_Id), AdSize.BANNER_HEIGHT_50);
        // Find the Ad Container
        LinearLayout adContainer = (LinearLayout) findViewById(R.id.banner_container);
        // Add the ad view to your activity layout
        adContainer.addView(adView);
        // Request an ad
        adView.loadAd();

        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.Open, R.string.Close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        aktupaper = findViewById(R.id.aktu);
        advancepaper = findViewById(R.id.advance);
        iscpaper = findViewById(R.id.isc);
        cbsepaper = findViewById(R.id.cbse);
        jeepaper = findViewById(R.id.jee);
        ndapaper = findViewById(R.id.nda);
        neetpaper = findViewById(R.id.neet);
        cdspaper = findViewById(R.id.cds);
        gatepaper = findViewById(R.id.gate);
        iprepaper = findViewById(R.id.iespre);
        imainpaper = findViewById(R.id.iesmain);
        cmainpaper = findViewById(R.id.main);
        cprepaper = findViewById(R.id.pre);
        aktupaper.setOnClickListener(this);
        iscpaper.setOnClickListener(this);
        cbsepaper.setOnClickListener(this);
        advancepaper.setOnClickListener(this);
        jeepaper.setOnClickListener(this);
        ndapaper.setOnClickListener(this);
        cdspaper.setOnClickListener(this);
        neetpaper.setOnClickListener(this);
        iprepaper.setOnClickListener(this);
        imainpaper.setOnClickListener(this);
        cprepaper.setOnClickListener(this);
        cmainpaper.setOnClickListener(this);
        gatepaper.setOnClickListener(this);


    }

    private void showDialogueForLogin(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Login to Continue");
        builder.setMessage(message);
//                set buttons
        builder.setPositiveButton("Log in", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent c = new Intent(MainActivity.this, com.naman.questionbank.login.login.class);
                startActivity(c);

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    private void setText(String log_out) {

        // set new title to the MenuItem
        signIn.setTitle(log_out);
    }

    private void getUsername() {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        db.child(getString(R.string.dbname_users))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(getString(R.string.username))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        username.setText(snapshot.getValue().toString());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }


    @Override
    public void onClick(View v) {

        Intent i;

        switch (v.getId()) {

            case R.id.aktu:
                i = new Intent(this, aktu.class);
                i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(i);
                break;

            case R.id.isc:
                i = new Intent(this, isc.class);
                i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(i);
                break;

            case R.id.cbse:
                i = new Intent(this, cbse.class);
                i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(i);
                break;
            case R.id.advance:
                i = new Intent(this, advance.class);
                i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(i);
                break;
            case R.id.jee:
                i = new Intent(this, jee.class);
                i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(i);
                break;
            case R.id.nda:
                i = new Intent(this, nda.class);
                i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(i);
                break;

            case R.id.cds:
                i = new Intent(this, cds.class);
                i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(i);
                break;
            case R.id.neet:
                i = new Intent(this, neet.class);
                i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(i);
                break;
            case R.id.main:
                i = new Intent(this, cmain.class);
                i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(i);
                break;
            case R.id.pre:
                i = new Intent(this, cpre.class);
                i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(i);
                break;
            case R.id.iesmain:
                i = new Intent(this, imain.class);
                i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(i);
                break;

            case R.id.iespre:
                i = new Intent(this, ipre.class);
                i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(i);
                break;
            case R.id.gate:
                i = new Intent(this, gate.class);
                i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(i);
                break;
            default:
                break;


        }
    }

    private void signOut() {
        Intent intent = new Intent(MainActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        FirebaseAuth.getInstance().signOut();
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        drawer = findViewById(R.id.drawer_layout);

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        Intent i;
        switch (menuItem.getItemId()) {
            case R.id.profile:
                if (isSignedIn) {
                    i = new Intent(this, profileActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(i);
                    break;
                } else {

                   showDialogueForLogin("You have to login to go on Profile page.");
                    break;

                }

            case R.id.fb:

                i = new Intent(this, feedback.class);
                i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(i);
                break;
            case R.id.ql:

                i = new Intent(this, questionbanklist.class);
                i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(i);
                break;
            case R.id.ad:

                i = new Intent(this, aboutdeveleper.class);
                i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(i);
                break;
            case R.id.logout:
                Log.d(TAG, "onNavigationItemSelected: "+menuItem.getTitle());

                if (!menuItem.getTitle().equals("Log out")) {
                    Intent c = new Intent(MainActivity.this, com.naman.questionbank.login.login.class);
                    startActivity(c);
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("Log Out");
                    builder.setMessage("Ae you sure you want to Log Out?");
//                set buttons
                    builder.setPositiveButton("Log Out", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            signOut();

                        }
                    });
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.create().show();
                }

                break;
            case R.id.hp:
                i = new Intent(this, help.class);
                i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(i);
                break;
            case R.id.rate:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=PackageName")));
                break;
            case R.id.share:
                final String appPackageName = getApplicationContext().getPackageName();
                String applink = "";
                try {
                    applink = "https://play.google.com/store/apps/details?id=" + appPackageName;
                } catch (android.content.ActivityNotFoundException anfe) {
                    applink = "https://play.google.com/store/apps/details?id=" + appPackageName;
                }
                i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                String shareBody = applink;
                String shareSub = "Your subject here";
                i.putExtra(Intent.EXTRA_SUBJECT, shareSub);
                i.putExtra(Intent.EXTRA_TEXT, shareBody);
                i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(Intent.createChooser(i, "Share using"));
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
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