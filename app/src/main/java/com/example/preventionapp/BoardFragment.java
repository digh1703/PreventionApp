package com.example.preventionapp;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;

import androidx.annotation.Nullable;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class BoardFragment extends Fragment {

    private AppInfo appInfo;
    private ListView contentsList;
    private BoardContentsList boardContentsList;
    private ArrayList<BoardContentsListItem> searchContentsList;
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


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db = FirebaseFirestore.getInstance();
        this.contentsList = (ListView) getView().findViewById(R.id.fragment_board_LV_contentsList);
        this.boardContentsList = BoardContentsList.getboardContentsList();
        this.searchContentsList = new ArrayList<BoardContentsListItem>();
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
        final SearchView searchView = (SearchView) menu.findItem(R.id.board_toolbar_search).getActionView();
        searchView.setIconifiedByDefault(true); // Do not iconify the widget; expand it by default
        searchView.setQueryHint("게시글을 검색합니다.");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener(){
            //쿼리(검색창에 넣은 내용)을 보낼때 동작
            //search를 위한 리스트를 새로 만들고 그 안에 검색된 원본 리스트를 넣은 후 search 리스트를 보여준다
            @Override
            public boolean onQueryTextSubmit(String s) {
                if(s.length() != 0){
                    BoardFragment.this.searchContentsList.clear();
                    for(int i=0;i<BoardFragment.this.boardContentsList.size();i++){
                        if (BoardFragment.this.boardContentsList.get(i).getNickname().toLowerCase().contains(s)
                                || BoardFragment.this.boardContentsList.get(i).getTitle().toLowerCase().contains(s)
                                || BoardFragment.this.boardContentsList.get(i).getContents().toLowerCase().contains(s))
                        {
                            // 검색된 원본 데이터(boardContentsList)를 리스트에 추가한다.
                            BoardFragment.this.searchContentsList.add(boardContentsList.get(i));
                        }
                    }
                    BoardFragment.this.adapter.setList(BoardFragment.this.searchContentsList);
                    updateList();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
        //종료(x버튼) 누를 시 동작
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                searchView.setQuery(null,false);
                BoardFragment.this.adapter.setList(BoardFragment.this.boardContentsList);
                updateList();
                return false;
            }
        });
    }

    /*
    search 기능은 searchView로 생성
    option(글 작성)은 onOptionsItemSelected로 아이템 선택 설정시 기능 작성
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.board_toolbar_option:
                Intent intent = new Intent(this.getContext(),BoardContentsWriteActivity.class);
                startActivityForResult(intent,REQUEST_UPDATE);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /*
    CreateContentsList - 검색 후 결과를 boardContentsList에 add
    updateList - UI 스레드 접근을 위해 만든 메서드(어댑터 처리)
     */
    class CreateContentsList extends AsyncTask<Void,Void,String> {
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
                                publishProgress();
                            } else {
                                //Log.d(TAG, "Error getting documents: ", task.getException());
                            }
                        }
                    });
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            try{
                BoardFragment.this.updateList();
            }
            catch (Exception e){
                e.printStackTrace();
            }
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

    //intent로 다른 액티비티 전환 후 돌아올 때 받는 데이터 관리(startActivityForResult 처리)
    //boardContentsWriteActivity(글 작성) / boardContentsActivity(글 삭제+수정) 처리 후 각 액티비티는 updateListRequest 를 보낸다.
    //updateListRequest true 라면 현재 생성된 리스트를 지우고 서버에서 갱신된 리스트를 받아온다.
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Activity.RESULT_OK) {
            if (resultCode == REQUEST_UPDATE) {
                Boolean updateCheck = data.getBooleanExtra("updateListRequest",false);
                //boardContentsActivity 에서 넘어오는 정보 확인
                //업데이트(글 작성+삭제)가 이루어지지 않았다면
                if(updateCheck) {
                    new CreateContentsList().execute();
                }
            }
        }
    }

}

