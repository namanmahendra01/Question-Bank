package com.naman.questionbank.Forum;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.facebook.ads.AudienceNetworkAds;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.naman.questionbank.Adapters.AdaperForum;
import com.naman.questionbank.MainActivity;
import com.naman.questionbank.R;
import com.naman.questionbank.ViewResourecActivity;
import com.naman.questionbank.models.Question;
import com.naman.questionbank.profileActivity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class askedForumFragment extends Fragment {
    private static final String TAG = "mediafragment";
    FloatingActionButton askBtn;

    private ArrayList<Question> questionArrayList;
    private ArrayList<Question> paginatedQuestionList;
    private int mResults;
    private AdaperForum forumAdapter;
    private RecyclerView forumRv;
    private AdView adView;
    private boolean isSignedIn;
    public askedForumFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.asked_forum_fragment, container, false);
        askBtn=view.findViewById(R.id.float_btn);
        forumRv=view.findViewById(R.id.ForumRv);

        isSignedIn = FirebaseAuth.getInstance().getCurrentUser() != null;


        forumRv.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        forumRv.setItemViewCacheSize(10);
        forumRv.setDrawingCacheEnabled(true);
        forumRv.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_LOW);
        linearLayoutManager.setItemPrefetchEnabled(true);
        linearLayoutManager.setInitialPrefetchItemCount(20);
        forumRv.setLayoutManager(linearLayoutManager);

        questionArrayList = new ArrayList<>();


        AudienceNetworkAds.initialize(getContext());
        adView = new AdView(getContext(), getString(R.string.placement_Id), AdSize.BANNER_HEIGHT_50);
        // Find the Ad Container
        LinearLayout adContainer = (LinearLayout) view.findViewById(R.id.banner_container);
        // Add the ad view to your activity layout
        adContainer.addView(adView);
        // Request an ad
        adView.loadAd();

        forumRv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!recyclerView.canScrollVertically(1) && newState == RecyclerView.SCROLL_STATE_IDLE) {
                    displayMoreCard();

                }
            }
        });


        askBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isSignedIn) {
                    Intent i = new Intent(getContext(), AskQuestionActivity.class);
                    startActivity(i);
                }else{
                    showDialogueForLogin("You have to login to ask question.");
                }
            }
        });



        if (isSignedIn) {
            getQuestion();
        }



        return view;
    }
    private void showDialogueForLogin(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Login to Continue");
        builder.setMessage(message);
//                set buttons
        builder.setPositiveButton("Log in", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent c = new Intent(getContext(), com.naman.questionbank.login.login.class);
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
    private void getQuestion() {
        questionArrayList.clear();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child(getString(R.string.dbname_Questions))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {

                                for (DataSnapshot snapshot1 : dataSnapshot.getChildren()) {
                                    Question question = snapshot1.getValue(Question.class);
                                    Log.d(TAG, "onDataChange: " + question);
                                    questionArrayList.add(question);

                            }

                            displayCard();

                        }else{
                            displayCard();

                        }
                    }


                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }

    private void displayCard() {
        Log.d(TAG, "display first 10 contest");



        paginatedQuestionList = new ArrayList<>();
        if (questionArrayList != null&&questionArrayList.size()!=0) {
            Collections.sort(questionArrayList, new Comparator<Question>() {
                @Override
                public int compare(Question o1, Question o2) {
                    return o2.getTim().compareTo(o1.getTim());
                }
            });

            try {


                int iteration = questionArrayList.size();
                if (iteration > 5) {
                    iteration = 5;
                }
                mResults = 5;
                for (int i = 0; i < iteration; i++) {
                    paginatedQuestionList.add(questionArrayList.get(i));
                }
                Log.d(TAG, "contest: sss" + paginatedQuestionList.size());
                forumAdapter = new AdaperForum(getContext(), paginatedQuestionList);
                forumAdapter.setHasStableIds(true);
                forumRv.setAdapter(forumAdapter);

            } catch (NullPointerException e) {
                Log.e(TAG, "Null pointer exception" + e.getMessage());

            } catch (IndexOutOfBoundsException e) {
                Log.e(TAG, "index out of bound" + e.getMessage());

            }

        }else {

        }
    }

    public void displayMoreCard() {
        Log.d(TAG, "display next 10 contest");

        try {
            if (questionArrayList.size() > mResults && questionArrayList.size() > 0) {

                int iterations;
                if (questionArrayList.size() > (mResults + 10)) {
                    Log.d(TAG, "display next 20 contest");
                    iterations = 10;
                } else {
                    Log.d(TAG, "display less tha 20 contest");
                    iterations = questionArrayList.size() - mResults;
                }
                for (int i = mResults; i < mResults + iterations; i++) {
                    paginatedQuestionList.add(questionArrayList.get(i));

                }
                forumRv.post(new Runnable() {
                    @Override
                    public void run() {
                        forumAdapter.notifyItemRangeInserted(mResults,iterations);
                    }
                });
                mResults = mResults + iterations;


            }else{

            }

        } catch (NullPointerException e) {
            Log.e(TAG, "Null pointer exception" + e.getMessage());

        } catch (IndexOutOfBoundsException e) {
            Log.e(TAG, "index out of bound" + e.getMessage());

        }

    }
    @Override
    public void onDestroy() {
        if (adView != null) {
            adView.destroy();
        }
//        if (interstitialAd != null) {
//            interstitialAd.destroy();
//        }
        super.onDestroy();
    }
}
