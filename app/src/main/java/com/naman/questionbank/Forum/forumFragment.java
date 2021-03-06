package com.naman.questionbank.Forum;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.naman.questionbank.Adapters.AdaperForum;
import com.naman.questionbank.Adapters.AdapterSearchQuestion;
import com.naman.questionbank.Adapters.AdapterSearchedQuestion;
import com.naman.questionbank.R;
import com.naman.questionbank.ViewResourecActivity;
import com.naman.questionbank.discussionActivity;
import com.naman.questionbank.models.Question;
import com.naman.questionbank.profileActivity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class forumFragment extends Fragment {
    private static final String TAG = "mediafragment";
   FloatingActionButton askBtn;

    private ArrayList<Question> questionArrayList;
    private ArrayList<Question> paginatedQuestionList;
    private int mResults;
    private AdaperForum forumAdapter;
    private RecyclerView forumRv;
    private EditText searchEditText;
    private ImageView searchImage;
    private ImageView crossImage;
    private RecyclerView searchListView;
    private AdapterSearchQuestion mAdapter;
    private RelativeLayout searchRL;

    private  boolean isSignedIn;
    private AdView adView;
    private List<Question> searchedQuestion;
    public forumFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.forum_fragment, container, false);
        askBtn=view.findViewById(R.id.float_btn);
        forumRv=view.findViewById(R.id.ForumRv);

        searchEditText=view.findViewById(R.id.search);
        searchImage=view.findViewById(R.id.searchicon);
        crossImage=view.findViewById(R.id.cross);
        searchListView=view.findViewById(R.id.searchListView);
        searchRL=view.findViewById(R.id.searchRv);


        isSignedIn = FirebaseAuth.getInstance().getCurrentUser() != null;


        searchListView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        searchListView.setItemViewCacheSize(10);
        searchListView.setDrawingCacheEnabled(true);
        searchListView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_LOW);
        linearLayoutManager.setItemPrefetchEnabled(true);
        linearLayoutManager.setInitialPrefetchItemCount(20);
        searchListView.setLayoutManager(linearLayoutManager);

        forumRv.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(getContext());
        forumRv.setItemViewCacheSize(10);
        forumRv.setDrawingCacheEnabled(true);
        linearLayoutManager1.setItemPrefetchEnabled(true);
        linearLayoutManager1.setInitialPrefetchItemCount(20);
        forumRv.setLayoutManager(linearLayoutManager1);

        searchedQuestion=new ArrayList<>();
        mAdapter = new AdapterSearchQuestion(getContext(), searchedQuestion);
        mAdapter.setHasStableIds(true);
        searchListView.setAdapter(mAdapter);

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
                }else {
                    showDialogueForLogin("You have to login to ask question.");
                }
            }
        });

        crossImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchEditText.setText("");
                searchedQuestion.clear();
                searchRL.setVisibility(View.GONE);
                if (mAdapter != null) mAdapter.notifyDataSetChanged();
            }
        });

        getQuestion();

        initTextListener();

        return view;
    }

    private void getQuestion() {
        questionArrayList.clear();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child(getString(R.string.dbname_Questions))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {

                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                for (DataSnapshot snapshot1 : snapshot.getChildren()) {


                                    Question question = snapshot1.getValue(Question.class);
                                    Log.d(TAG, "onDataChange: " + question);
                                    questionArrayList.add(question);

                                }

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
    private void initTextListener() {
        searchedQuestion = new ArrayList<>();
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = searchEditText.getText().toString();
                searchForMatch(text);
            }
        });
    }

    private void searchForMatch(String keyword) {
        Log.d(TAG, "searching for a match" + keyword);
        searchedQuestion.clear();
        if (keyword.length() == 0) {
            searchRL.setVisibility(View.GONE);
            crossImage.setVisibility(View.GONE);

        } else {
            crossImage.setVisibility(View.VISIBLE);
            searchRL.setVisibility(View.VISIBLE);
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
            Query query = reference.child(getString(R.string.dbname_QuestionTags)).orderByKey()
                    .startAt(keyword).endAt(keyword + "\uf8ff");
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        for (DataSnapshot singleSnapshot : dataSnapshot1.getChildren()) {

                            Log.d(TAG, "onDataChange: g " + singleSnapshot.getKey());
                            reference.child(getString(R.string.dbname_Questions))
                                    .child(singleSnapshot.getValue().toString())

                                    .child(singleSnapshot.getKey())
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                                            Log.d(TAG, "onDataChange: g1 " + snapshot.getValue());
                                            if (!searchedQuestion.contains(snapshot.getValue(Question.class)))
                                            searchedQuestion.add(snapshot.getValue(Question.class));
                                            updateUserList();
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });

                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    private void updateUserList() {
        Log.d(TAG, "updateUserList: "+searchedQuestion);
        Collections.reverse(searchedQuestion);
        mAdapter = new AdapterSearchQuestion(getContext(), searchedQuestion);
        mAdapter.setHasStableIds(true);
        searchListView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();



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
