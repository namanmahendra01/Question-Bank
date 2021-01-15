package com.naman.questionbank.login;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.naman.questionbank.R;

import java.util.HashMap;
import java.util.TimeZone;

public class register extends AppCompatActivity {

    private static final String TAG = "register";
    private static final int ANIMATION_DURATION = 1000;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private Context mContext;
    //widgets
    private RelativeLayout rootView;
    private ProgressBar mProgressBar;
    private EditText mUsername;
    private EditText mEmail;
    private EditText mPassword;
    private EditText mConfirmPassword;

    private Button btnregister;
    private TextView linkLogin;
    //variables
    private String username,userID;
    private String email;
    private String password;
    private String confirmPassword;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private final String append = "";

    //    SP
    Gson gson;
    SharedPreferences sp;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_signup);
        Log.d(TAG, "onCreate: started.");

//          Initialize SharedPreference variables
        sp =getSharedPreferences("Login", Context.MODE_PRIVATE);
        gson = new Gson();
        String justRegistered=sp.getString("yes",null);

        mContext = register.this;
        setupFirebaseAuth();
        initWidgets();
        init();
    }

    private void initWidgets() {
        Log.d(TAG, "initWidgets: initiliazing widgets");
        rootView = findViewById(R.id.rootView);
        mProgressBar = findViewById(R.id.Registerrequestloadingprogressbar);
        mUsername = findViewById(R.id.username);
        mEmail = findViewById(R.id.input_email);
        mPassword = findViewById(R.id.input_password);
        mConfirmPassword = findViewById(R.id.confirm_password);
        btnregister = findViewById(R.id.btn_register);
        linkLogin = findViewById(R.id.link_login);


        mContext = register.this;
        mProgressBar.setVisibility(View.GONE);



    }

    private void startRedirect(String url) {
        Intent i2 = new Intent(Intent.ACTION_VIEW);
        i2.setData(Uri.parse(url));
        startActivity(i2);
    }

    private boolean checkInputs(String email, String username, String password, String confirmPassword) {
        Log.d(TAG, "checkInputs:checking inputs for null values");

        if (username.equals("")) {
            Toast.makeText(mContext, "Empty username field", Toast.LENGTH_SHORT).show();
            mUsername.setError("Please enter a username");
            mUsername.requestFocus();
            return false;
        }

        if (username.length()>15) {
            Toast.makeText(mContext,"Username should be less than 15 characters", Toast.LENGTH_SHORT).show();
            mUsername.setError("Username should be less than 15 characters");
            mUsername.requestFocus();
            return false;
        }
        if (email.equals("")) {
            Toast.makeText(mContext, "Empty email field", Toast.LENGTH_SHORT).show();
            mEmail.setError("Please enter a email-id");
            mEmail.requestFocus();
            return false;
        }
        if (password.equals("")) {

            mPassword.setText("");
            mConfirmPassword.setText("");
            mPassword.setError("Please enter a password");
            mPassword.requestFocus();
            Toast.makeText(mContext, "Empty password field", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (confirmPassword.equals("")) {
            mConfirmPassword.setText("");
            mConfirmPassword.setError("Please enter a password");
            mConfirmPassword.requestFocus();
            Toast.makeText(mContext, "Empty confirm password field", Toast.LENGTH_SHORT).show();
            return false;
        }else
            return true;
    }

    private boolean checkValidity(String password) {
        if (password.length() < 6) {

            mPassword.setText("");
            mConfirmPassword.setText("");
            mPassword.setError("Password too short");
            mPassword.requestFocus();
            mConfirmPassword.setError("Password too short");
            mConfirmPassword.requestFocus();
            return false;
        }
        return true;
    }




    private void init() {

        rootView.setOnClickListener(v -> {
            InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Activity.INPUT_METHOD_SERVICE);
            assert imm != null;
            imm.hideSoftInputFromWindow(rootView.getWindowToken(), 0);
        });






        //registration button
        btnregister.setOnClickListener(v -> {
            email = mEmail.getText().toString();
            username = mUsername.getText().toString();
            password = mPassword.getText().toString();
            confirmPassword = mConfirmPassword.getText().toString();

            if (checkInputs(email, username, password, confirmPassword)) {
                if (checkValidity(password) || checkValidity(confirmPassword))
                    if (password.equals(confirmPassword)) {
                        mProgressBar.setVisibility(View.VISIBLE);
                        checkifuserexist(username);
                    } else {
                        mPassword.setText("");
                        mConfirmPassword.setText("");
                        mPassword.setError("Password don not match");
                        mPassword.requestFocus();
                        mConfirmPassword.setError("Password don not match");
                        mConfirmPassword.requestFocus();
                    }
            } else {

                mPassword.setText("");
                mConfirmPassword.setText("");
            }
        });


        //link login
        linkLogin.setOnClickListener(v -> {
            Log.d(TAG, "on Click:navigating to register screen");
            Intent intent = new Intent(register.this, login.class);
            startActivity(intent);
        });
    }

    private void setupFirebaseAuth() {
        Log.d(TAG, "setup FirebaseAuth: setting up firebase auth.");
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        mAuthListener = firebaseAuth -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null) {
                Log.d(TAG, "onAuthStateChanged:signed in:" + user.getUid());
                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });

            } else {
                Log.d(TAG, "onAuthStateChanged:signed_out");
            }
        };
    }

    private void checkifuserexist(final String username) {
        if (username.contains(" ")){
            Toast.makeText(mContext, "No space should be there.", Toast.LENGTH_SHORT).show();
            register.this.mUsername.setError("No space should be there.");
            register.this.mProgressBar.setVisibility(View.GONE);
        }else{
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
            Query query = reference.child(getString(R.string.dbname_username)).child(username);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (!dataSnapshot.exists()) {
                        RegisterNewEmail(email, password, mProgressBar, mEmail, mUsername, mPassword, mConfirmPassword);

                    } else {
                        Toast.makeText(mContext, "please try different username", Toast.LENGTH_SHORT).show();
                        register.this.mUsername.setError("username already exist");
                        register.this.mProgressBar.setVisibility(View.GONE);

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

    }

    public void RegisterNewEmail(final String email, String password, ProgressBar mProgressBar, EditText mEmail, EditText mUsername, EditText mPassword, EditText mConfirmPassword) {
        assert password!=null;
        assert  email!=null;
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            private static final int ANIMATION_DURATION = 1000;

            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    userID = mAuth.getCurrentUser().getUid();
                    addNewUser(email, username);
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.getException());
                    new AlertDialog.Builder(mContext)
                            .setTitle("New account cannot be created")
                            .setMessage("Try using a different email id")
                            .setPositiveButton(android.R.string.ok, (dialog, which) -> dialog.dismiss())
                            .show();
                    mProgressBar.setVisibility(View.INVISIBLE);

                    mEmail.setText("");
                    mPassword.setText("");
                    mUsername.setText("");
                    mPassword.setText("");
                    mConfirmPassword.setText("");
                }
                // ...
            }
        });
    }

    public void sendverification() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            user.sendEmailVerification().addOnCompleteListener(task -> {
                if (!task.isSuccessful()) {
                    new AlertDialog.Builder(mContext)
                            .setTitle("Sorry")
                            .setMessage("Could not send Verification Mail")
                            .setPositiveButton(android.R.string.ok, (dialog, which) -> dialog.dismiss())
                            .show();
                }else{
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("yes", "yes");
                    editor.apply();
                    Intent intent = new Intent(register.this, login.class);
                    startActivity(intent);
                    Toast.makeText(mContext, " Verification mail sent. Please verify!", Toast.LENGTH_LONG).show();
                }
            });
        }
    }


    public void addNewUser(String email, String username) {

        HashMap<Object,String> hashMap= new HashMap<>();
        hashMap.put(getString(R.string.email),email);
        hashMap.put(getString(R.string.username),username);


        DatabaseReference db= FirebaseDatabase.getInstance().getReference();
        db.child(getString(R.string.dbname_username))
                .child(username)
                .setValue(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        db.child(getString(R.string.dbname_users))
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid().toString())
                                .setValue(hashMap)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        sendverification();

                                    }
                                });
                    }
                });


    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);

    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}
