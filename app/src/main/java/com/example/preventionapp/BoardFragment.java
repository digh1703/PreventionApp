package com.example.preventionapp;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class BoardFragment extends Fragment {

    private AppInfo appInfo;
    private ListView contentsList;
    private BoardContentsList boardContentsList;
    private BoardContentsListAdapter adapter;

    private FirebaseFirestore db;

    private final int REQUEST_UPDATE = 1;

    public BoardFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.appInfo = AppInfo.getAppInfo();
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_board, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onActivityCreated(Bundle b) {
        super.onActivityCreated(b);

        db = FirebaseFirestore.getInstance();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.contentsList = (ListView) getView().findViewById(R.id.fragment_board_LV_contentsList);
        this.boardContentsList = BoardContentsList.getboardContentsList();
        new CreateContentsList().execute();

        this.adapter = new BoardContentsListAdapter(this.getContext(), boardContentsList, this);
        this.contentsList.setAdapter(adapter);
        this.contentsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView,
                                    View view, int position, long id) {
                Intent intent = new Intent(getActivity(), BoardContentsActivity.class);
                intent.putExtra("position",position);
                startActivityForResult(intent,REQUEST_UPDATE);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        new CreateContentsList().execute();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.board_toolbar,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    //메인에서 작업이 느려져서 추가, 백그라운드에서 작업하는 것
    class CreateContentsList extends AsyncTask<Void,Void,String> {
        String result;
        @Override
        protected String doInBackground(Void... Voids) {
            CollectionReference ref = db.collection("boardContents");
            ref.orderBy("date", Query.Direction.DESCENDING)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            BoardFragment.this.boardContentsList.clear();
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    //Log.d(TAG, document.getId() + " => " + document.getData());
                                    BoardFragment.this.boardContentsList.add(new BoardContentsListItem(
                                            document.getData().get("title").toString(),
                                            document.getData().get("nickname").toString(),
                                            document.getTimestamp("date"),
                                            document.getData().get("contents").toString(),
                                            (Long) document.getData().get("replyNum"),
                                            (Long) document.getData().get("recommendNum")
                                    ));
                                }
                                BoardFragment.this.updateList();
                            } else {
                                //Log.d(TAG, "Error getting documents: ", task.getException());
                            }
                        }
                    });
            return null;
        }
    }

    void updateList(){
        final Handler handler = new Handler();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            BoardFragment.this.adapter.notifyDataSetChanged();
                        }
                    });
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Activity.RESULT_OK) {
            if (resultCode == REQUEST_UPDATE) {
                Boolean updateCheck = data.getBooleanExtra("update",false);
                //boardContentsActivity 에서 넘어오는 정보 확인
                //업데이트(글삭제)가 이루어지지 않았다면
                if(updateCheck) {
                    new CreateContentsList().execute();
                }
            }
        }
    }

}

