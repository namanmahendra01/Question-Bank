package com.naman.questionbank.Forum;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;


import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.naman.questionbank.Adapters.SectionPagerAdapter;
import com.naman.questionbank.R;
import com.naman.questionbank.login.login;

public class forumActivity extends AppCompatActivity {


    private static final String TAG = "forum";
    public TabLayout tablayout;
    private static final int ACTIVITY_NUM = 0;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private ViewPager mViewPager;
    private FrameLayout mFramelayoutl;
    private RelativeLayout mRelativeLayout;
    LinearLayout prom;
    Context context = forumActivity.this;
    String mUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forum);

        Log.d(TAG, "onCreate:starting.");
        mAuth = FirebaseAuth.getInstance();
        mViewPager = (ViewPager) findViewById(R.id.viewpager_container);
        mFramelayoutl = (FrameLayout) findViewById(R.id.container);
        mRelativeLayout = (RelativeLayout) findViewById(R.id.relLayoutParent);


        checkCurrentuser(mAuth.getCurrentUser());
        setupFirebaseAuth();

        setupViewPager();


    }

    @Override
    protected void onResume() {
        checkCurrentuser(mAuth.getCurrentUser());
        super.onResume();
    }


    //    *********************FIREBASE***************************
    private void checkCurrentuser(FirebaseUser user) {
        if (user == null) {
            Intent intent = new Intent(forumActivity.this, login.class);
            startActivity(intent);
        }


    }


    private void setupFirebaseAuth() {
        Log.d(TAG, "setup FirebaseAuth: setting up firebase auth.");

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                checkCurrentuser(user);
                if (user != null) {

                    Log.d(TAG, "onAuthStateChanged:signed in:" + user.getUid());
                } else {
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    Log.d(TAG, "onAuthStateChanged: navigating to login");
                    SharedPreferences settings = getSharedPreferences("shared preferences", Context.MODE_PRIVATE);
                    new android.app.AlertDialog.Builder(context)
                            .setTitle("No user logon found")
                            .setMessage("We will be logging u out. \n Please try to log in again")
                            .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                                Intent intent = new Intent(context, login.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                settings.edit().clear().apply();
                                if (mAuthListener != null)
                                    mAuth.removeAuthStateListener(mAuthListener);
                                startActivity(intent);
                            })
                            .show();
                }
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        mViewPager.setCurrentItem(0);
        checkCurrentuser(mAuth.getCurrentUser());


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    //   ************************FIREBASE****************************


    //    for adding 3 tabs -media,home,message
    private void setupViewPager() {
        SectionPagerAdapter adapter = new SectionPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new forumFragment());
        adapter.addFragment(new askedForumFragment());

        mViewPager.setAdapter(adapter);

        tablayout = (TabLayout) findViewById(R.id.tabs);
        tablayout.setupWithViewPager(mViewPager);
//        for giving icon to them
        tablayout.getTabAt(0).setText("Forum");
        tablayout.getTabAt(1).setText("Asked");
    }





}
