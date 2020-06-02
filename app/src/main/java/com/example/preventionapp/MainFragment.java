package com.example.preventionapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;

public class MainFragment extends Fragment {

    FirebaseUser user;
    private FirebaseFirestore db;

    private List<BoardContentsListItem> boardContentsList;
    ListView contentsListView;
    MainBoardContentsListAdapter adapter;

    public MainFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);

    }

    @Override
    public void onActivityCreated(Bundle b) {
        super.onActivityCreated(b);
        user = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();


        getView().findViewById(R.id.fragment_main_mapButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity().getApplicationContext(), CrimeMap.class);
                startActivity(intent);
            }
        });
        getView().findViewById(R.id.fragment_main_callButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.activity_main_fragment, new CallFragment());
                fragmentTransaction.commit();
            }
        });
        getView().findViewById(R.id.fragment_main_careButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.activity_main_fragment, new CallFragment());
                fragmentTransaction.commit();
            }
        });
        getView().findViewById(R.id.fragment_main_infoButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.activity_main_fragment, new InfoFragment());
                fragmentTransaction.commit();
            }
        });
        getView().findViewById(R.id.fragment_main_boardButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.activity_main_fragment, new BoardFragment());
                fragmentTransaction.commit();
            }
        });
        getView().findViewById(R.id.fragment_main_newsButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.activity_main_fragment, new NewsFragment());
                fragmentTransaction.commit();
            }
        });

        Button btn = (Button)getView().findViewById(R.id.activity_main_btn_moreInfo);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.activity_main_fragment, new BoardFragment());
                fragmentTransaction.commit();
            }
        });

        contentsListView = (ListView) getView().findViewById(R.id.activity_main_LV_boardContents);
        boardContentsList = new ArrayList<BoardContentsListItem>();
        adapter = new MainBoardContentsListAdapter(getView().getContext(),boardContentsList,this);
        contentsListView.setAdapter(adapter);
        BackgroundTask backgroundTask = new BackgroundTask();
        backgroundTask.execute();
    }

    //메인에서 작업이 느려져서 추가
    class BackgroundTask extends AsyncTask<Void,Void,String> {
        @Override
        protected String doInBackground(Void... Voids) {
            db.collection("boardContents")
                    .orderBy("date", Query.Direction.DESCENDING)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            boardContentsList.clear();
                            int count = 0;
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    //Log.d(TAG, document.getId() + " => " + document.getData());
                                    boardContentsList.add(new BoardContentsListItem(
                                            document.getData().get("title").toString(),
                                            document.getData().get("nickname").toString(),
                                            document.getTimestamp("date"),
                                            document.getData().get("contents").toString(),
                                            (Long) document.getData().get("replyNum"),
                                            (Long) document.getData().get("recommendNum")
                                    ));
                                    count++;
                                    if(count == 5){
                                        break;
                                    }
                                }
                                adapter.notifyDataSetChanged();
                            } else {
                                //Log.d(TAG, "Error getting documents: ", task.getException());
                            }
                        }
                    });
            return null;
        }

        @Override
        public void onProgressUpdate(Void... values){
            super.onProgressUpdate(values);
        }

    }

}
