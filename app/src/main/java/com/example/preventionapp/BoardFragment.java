package com.example.preventionapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class BoardFragment extends Fragment {

    private AppInfo appInfo;
    private ListView listview;
    private BoardContentsList boardContentsList;
    private BoardContentsListAdapter adapter;

    private FirebaseUser user;
    private FirebaseFirestore db;

    public BoardFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appInfo = AppInfo.getAppInfo();

        this.listview = (ListView) getView().findViewById(R.id.fragment_board_LV_contentsList);
        this.boardContentsList = BoardContentsList.getboardContentsList();

        this.adapter = new BoardContentsListAdapter(this.getContext(), boardContentsList, this);
        this.listview.setAdapter(adapter);
        this.listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView,
                                    View view, int position, long id) {
                Intent intent = new Intent(getActivity(), BoardContentsActivity.class);
                startActivity(intent);
            }
        });
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

        CreateContentsList createContentsList = new CreateContentsList();
        createContentsList.execute();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onStart() {
        super.onStart();
        CreateContentsList backgroundTask = new CreateContentsList();
        backgroundTask.execute();
        for(int i=0;i<boardContentsList.size();i++) {
            System.out.println(boardContentsList.get(i).getContents());
        }

    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.board_toolbar,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    //메인에서 작업이 느려져서 추가
    class CreateContentsList extends AsyncTask<Void,Void,String> {
        @Override
        protected String doInBackground(Void... Voids) {
            db.collection("boardContents")
                    .orderBy("date", Query.Direction.DESCENDING)
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
                                adapter.notifyDataSetChanged();
                            } else {
                                //Log.d(TAG, "Error getting documents: ", task.getException());
                            }
                        }
                    });
            return null;
        }
    }








    class SetList extends AsyncTask<Void,Void,String> {
        @Override
        protected String doInBackground(Void... voids) {

            return null;
        }
        @Override
        public void onPostExecute(String result){
            if (boardContentsList.size() > 0) {    //데이타가 추가, 수정되었을때
                adapter.notifyDataSetChanged();
            } else {    //뷰에 표시될 데이타가 없을때
                adapter.notifyDataSetInvalidated();
            }
            listview.setAdapter(adapter);
            return;
        }

        @Override
        public void onProgressUpdate(Void... values){
            super.onProgressUpdate(values);
        }
    }




    public List<BoardContentsListItem> getContentsList() {
        return this.boardContentsList;
    }

    public void addContentsList(BoardContentsListItem contents) {
        this.boardContentsList.add(0,contents);
    }

    public void deleteContentsList(int index) {
        this.boardContentsList.remove(index);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (resultCode == getActivity().RESULT_OK) {
                BoardContentsListItem item = data.getParcelableExtra("data");
                boardContentsList.add(0,item);
            }
        }
    }
}

