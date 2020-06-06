package com.example.preventionapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class BoardContentsActivity extends AppCompatActivity {

    AppInfo appInfo;
    androidx.appcompat.widget.Toolbar toolbar;

    private List<BoardContentsListItem> boardContentsList;
    private List<ReplyContentsListItem> replyContentsList;
    private ReplyContentsListAdapter adapter;

    private TextView nicknameView;
    private TextView dateView;
    private TextView recommendView;
    private Button recommendBtn;

    private TextView titleView;
    private TextView contentsView;
    private String contentsNickname;
    private Timestamp contentsDate;

    FirebaseUser user;
    FirebaseFirestore db;

    private EditText replyEdit;
    private int number;
    private String nickname;
    private Timestamp date;
    private String replyContents;
    private long recommendNum;
    private Button replyBtn;
    private ListView replyList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boardcontents);
        setContentView(R.layout.activity_boardcontents);
        db = FirebaseFirestore.getInstance();
        appInfo = AppInfo.getAppInfo();

        toolbar = (androidx.appcompat.widget.Toolbar)findViewById(R.id.toolbar);
        setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        nicknameView = (TextView)findViewById(R.id.activity_boardContents_nickname);
        dateView = (TextView)findViewById(R.id.activity_boardContents_date);
        recommendView = (TextView)findViewById(R.id.activity_boardContents_view_recommend);
        recommendBtn = (Button)findViewById(R.id.activity_boardContents_btn_recommend);
        titleView = (TextView)findViewById(R.id.activity_boardContents_title);
        contentsView = (TextView)findViewById(R.id.activity_boardContents_contents);
        replyEdit = (EditText)findViewById(R.id.activity_boardContents_Edit_reply);
        replyBtn = (Button)findViewById(R.id.activity_boardContents_btn_reply);
        replyList = (ListView)findViewById(R.id.activity_boardContents_LV_reply);

        boardContentsList = new BoardFragment().getContentsList();
        Intent intent = getIntent();
        int position = intent.getIntExtra("position",0);

        contentsDate = boardContentsList.get(position).getDate();
        contentsNickname = boardContentsList.get(position).getNickname();
        recommendNum = boardContentsList.get(position).getRecommendNum();

        titleView.setText(boardContentsList.get(position).getTitle());
        nicknameView.setText(boardContentsList.get(position).getNickname());
        SimpleDateFormat sdfNow = new SimpleDateFormat("yy/MM/dd HH:mm");
        String formatDate = sdfNow.format(boardContentsList.get(position).getDate().toDate());
        dateView.setText(formatDate);
        recommendView.setText(String.valueOf(boardContentsList.get(position).getRecommendNum()));
        contentsView.setText(boardContentsList.get(position).getContents());

/*
        contentsDate = (Timestamp)intent.getSerializableExtra("date");
        contentsNickname = intent.getStringExtra("nickname");
        recommendNum = intent.getLongExtra("recommendNum",0);

        titleView.setText(intent.getStringExtra("title"));
        nicknameView.setText(intent.getStringExtra("nickname"));
        dateView.setText(intent.getStringExtra("date"));
        recommendView.setText(String.valueOf(recommendNum));
        contentsView.setText(intent.getStringExtra("contents"));

 */
        recommendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recommendBtnAction();
            }
        });

        replyContentsList = new ArrayList<ReplyContentsListItem>();
        CreateReplyContentsList createReplyContentsList = new CreateReplyContentsList();
        createReplyContentsList.execute();

        titleView = (TextView) findViewById(R.id.activity_boardContents_title);
        nicknameView = (TextView) findViewById(R.id.activity_boardContents_nickname);
        dateView = (TextView) findViewById(R.id.activity_boardContents_date);
        contentsView = (TextView) findViewById(R.id.activity_boardContents_contents);

        adapter = new ReplyContentsListAdapter(getApplicationContext(),replyContentsList);
        replyList.setAdapter(adapter);

        replyBtn = (Button)findViewById(R.id.activity_boardContents_btn_reply);
        replyBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                replyContents = replyEdit.getText().toString();

                if(replyContents.length() == 0){
                    Toast.makeText(getApplicationContext(), "내용을 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                nickname = "";
                date = Timestamp.now();
                recommendNum = 0;
                createReplyContents(contentsNickname, contentsDate,new ReplyContentsListItem(nickname,date,replyContents,recommendNum));
            }
        });
    }

        @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.boardcontents_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
            case R.id.boardcontents_toolbar_modify:
                Toast.makeText(getApplicationContext(), "search", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.boardcontents_toolbar_delete:
                deleteContents();
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void recommendBtnAction(){
        final DocumentReference[] sfDocRef = new DocumentReference[1];
        db.collection("boardContents")
                .whereEqualTo("nickname", contentsNickname)
                .whereEqualTo("date",contentsDate)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                sfDocRef[0] = db.collection("boardContents").document(document.getId());

                                db.runTransaction(new Transaction.Function<Long>() {
                                    @Override
                                    public Long apply(Transaction transaction) throws FirebaseFirestoreException {
                                        DocumentSnapshot snapshot = transaction.get(sfDocRef[0]);
                                        long newPopulation = snapshot.getLong("recommendNum") + 1;
                                        if (newPopulation <= 99) {
                                            transaction.update(sfDocRef[0], "recommendNum", newPopulation);
                                            return newPopulation;
                                        } else {
                                            throw new FirebaseFirestoreException("Population too high",
                                                    FirebaseFirestoreException.Code.ABORTED);
                                        }
                                    }
                                }).addOnSuccessListener(new OnSuccessListener<Long>() {
                                    @Override
                                    public void onSuccess(Long result) {
                                        recommendView.setText(result.toString());
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        //Log.w(TAG, "Transaction failure.", e);
                                    }
                                });
                            }
                        } else {
                            // Log.d(TAG, "Error getting documents: ", task.getException());
                            System.out.println("error");
                        }
                    }
                });

    }

    public void createReplyContents(final String contentsNickname, Timestamp contentsDate, final ReplyContentsListItem data) {
        /*
        상위 컬렉션 boardContents, nickname/date 부분 검색 - 검색 받은 결과(task)를 QueryDocumentSnapshot document에 저장
        1차 결과로 선택한 게시글에 맞는 reply 컬렉션 검색 - 서버에 저장하고 동시에 클라 replyContentsList에 저장

        덧글 개수 갱신은 여러 사용자가 동시에 접근할 수 있어서 runTransaction 사용
         */
        if (user != null) {
            //1차 검색
            db.collection("boardContents")
                    .whereEqualTo("nickname", contentsNickname)
                    .whereEqualTo("date", contentsDate)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    //2차 검색
                                    db.collection("boardContents").document(document.getId()).
                                            collection("reply").add(data);
                                    replyContentsList.add(0, new ReplyContentsListItem(
                                            data.getNickname(),
                                            data.getDate(),
                                            data.getContents(),
                                            data.getRecommendNum()
                                    ));
                                    final DocumentReference sfDocRef = db.collection("boardContents").document(document.getId());
                                    //덧글 갯수 갱신
                                    db.runTransaction(new Transaction.Function<Long>() {
                                        @Override
                                        public Long apply(Transaction transaction) throws FirebaseFirestoreException {
                                            DocumentSnapshot snapshot = transaction.get(sfDocRef);
                                            long newPopulation = snapshot.getLong("replyNum") + 1;
                                            if (newPopulation <= 99) {
                                                transaction.update(sfDocRef, "replyNum", newPopulation);
                                                return newPopulation;
                                            } else {
                                                throw new FirebaseFirestoreException("Population too high",
                                                        FirebaseFirestoreException.Code.ABORTED);
                                            }
                                        }
                                    });
                                }
                                adapter.notifyDataSetChanged();
                                replyList.setAdapter(adapter);
                            } else {
                                // Log.d(TAG, "Error getting documents: ", task.getException());
                            }
                        }
                    });
        }
    }

    public void deleteContents() {
        /*
        createReplyContents 와 구조 동일
        하위 컬렉션(reply) 삭제후 상위 컬렉션(boardContents) 삭제
         */
        if (user != null) {
            db.collection("boardContents")
                    .whereEqualTo("nickname", contentsNickname)
                    .whereEqualTo("date", contentsDate)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    //하위 컬렉션(reply) 삭제후 상위 컬렉션(boardContents) 삭제
                                    db.collection("boardContents").document(document.getId()).
                                            collection("reply").document()
                                            .delete()
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    e.printStackTrace();
                                                }
                                            });
                                    db.collection("boardContents").document(document.getId())
                                            .delete()
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    e.printStackTrace();
                                                }
                                            });
                                }
                            } else {
                                // Log.d(TAG, "Error getting documents: ", task.getException());
                            }
                        }
                    });
        }
    }

    class CreateReplyContentsList extends AsyncTask<Void,Void,String> {
        /*
        최초 덧글 리스트 생성
       createReplyContents 와 구조 동일
       덧글 생성을 date 기준으로 내림차순
        */
        @Override
        protected String doInBackground(Void... voids) {
            db.collection("boardContents")
                    .whereEqualTo("nickname", contentsNickname)
                    .whereEqualTo("date",contentsDate)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                replyContentsList.clear();
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    DocumentReference ref = db.collection("boardContents").document(document.getId());
                                    ref.collection("reply").orderBy("date", Query.Direction.DESCENDING)
                                            .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {
                                                for (QueryDocumentSnapshot document : task.getResult()) {
                                                    replyContentsList.add(new ReplyContentsListItem(
                                                            document.getString("nickname"),
                                                            document.getTimestamp("date"),
                                                            document.getString("contents"),
                                                            document.getLong("recommendNum")
                                                    ));
                                                }
                                            }
                                        }
                                    });
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



