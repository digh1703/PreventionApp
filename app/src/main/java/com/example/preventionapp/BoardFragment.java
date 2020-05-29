package com.example.preventionapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class BoardFragment extends Fragment {

    private ListView listview;
    private List<BoardContentsListItem> contentsList;
    private BoardContentsListAdapter adapter;

    private FirebaseUser user;
    private FirebaseFirestore db;

    public BoardFragment() {
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.board_toolbar,menu);
        super.onCreateOptionsMenu(menu, inflater);
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

        user = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();
        listview = (ListView) getView().findViewById(R.id.fragment_board_LV_contentsList);
        contentsList = new ArrayList<BoardContentsListItem>();

        BackgroundTask backgroundTask = new BackgroundTask();
        backgroundTask.execute();

        adapter = new BoardContentsListAdapter(this.getContext(), contentsList, this);
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView,
                                    View view, int position, long id) {
                Intent intent = new Intent(getActivity(), BoardContentsActivity.class);
                intent.putExtra("nickname",contentsList.get(position).getNickname());
                intent.putExtra("date",contentsList.get(position).getDate());
                intent.putExtra("title",contentsList.get(position).getTitle());
                intent.putExtra("contents",contentsList.get(position).getContents());
                intent.putExtra("recommendNum",contentsList.get(position).getRecommendNum());
                startActivity(intent);
                adapter.notifyDataSetChanged();
            }
        });
    }


    //메인에서 작업이 느려져서 추가
    class BackgroundTask extends AsyncTask<Void,Void,String> {
        @Override
        protected String doInBackground(Void... Voids) {
            db.collection("boardContents")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            contentsList.clear();
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    //Log.d(TAG, document.getId() + " => " + document.getData());
                                    contentsList.add(new BoardContentsListItem(
                                            document.getData().get("title").toString(),
                                            document.getData().get("nickname").toString(),
                                            document.getData().get("date").toString(),
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

        @Override
        public void onProgressUpdate(Void... values){
            super.onProgressUpdate(values);
        }

    }
}

