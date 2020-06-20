package com.example.preventionapp;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/*
BoardContentsActivity
class FindThisDocument
현재 문서 이름 확인

public void recommendBtnAction
문서 내의 추천 버튼 관리

public void modifyContents()
현재 문서 이름(db접근을 위해) / 위치(리스트 정보 가져오기 위해) / modify 체크
를 intent를 통해 BoardContentsWriteActivity 쪽으로 보낸다.

public void deleteContents()

class CreateReplyContentsList
댓글 리스트 생성, 처음 액티비티가 만들어질때/댓글을 insert,delete 할때마다 불린다.

public void insertReplyContents(final ReplyContentsListItem data)

public void deleteReplyContents(int position) {

+DB 탐색은 닉네임과 문서 작성 시간으로 탐색
 */

public class BoardContentsActivity extends AppCompatActivity implements AccessActivity{

    AppInfo appInfo;
    androidx.appcompat.widget.Toolbar toolbar;

    private BoardContentsList boardContentsList;
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

    FirebaseFirestore db;
    private String documentID = new String();
    private int position;

    private EditText replyEdit;
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
        boardContentsList = BoardContentsList.getboardContentsList();
        replyContentsList = new ArrayList<ReplyContentsListItem>();

        Intent intent = getIntent();
        this.position = intent.getIntExtra("position",-1);
        if(this.position == -1){
            Log.d("2","no data");
            //이전화면으로 이동
        }

        this.contentsDate = boardContentsList.get(this.position).getDate();
        this.contentsNickname = boardContentsList.get(this.position).getNickname();
        this.recommendNum = boardContentsList.get(this.position).getRecommendNum();

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

        titleView.setText(boardContentsList.get(this.position).getTitle());
        nicknameView.setText(boardContentsList.get(this.position).getNickname());
        SimpleDateFormat sdfNow = new SimpleDateFormat("yy/MM/dd HH:mm");
        String formatDate = sdfNow.format(boardContentsList.get(this.position).getDate().toDate());
        dateView.setText(formatDate);
        recommendView.setText(String.valueOf(boardContentsList.get(this.position).getRecommendNum()));
        contentsView.setText(boardContentsList.get(this.position).getContents());

        recommendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recommendBtnAction();
            }
        });


        titleView = (TextView) findViewById(R.id.activity_boardContents_title);
        nicknameView = (TextView) findViewById(R.id.activity_boardContents_nickname);
        dateView = (TextView) findViewById(R.id.activity_boardContents_date);
        contentsView = (TextView) findViewById(R.id.activity_boardContents_contents);

        new FindThisDocument().execute();

        adapter = new ReplyContentsListAdapter(this,replyContentsList,this);
        replyList.setAdapter(adapter);

        replyBtn = (Button) findViewById(R.id.activity_boardContents_btn_reply);
        replyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replyContents = replyEdit.getText().toString();

                if (replyContents.length() == 0) {
                    Toast.makeText(BoardContentsActivity.this, "내용을 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                System.out.println("2:"+ getApplicationContext());
                nickname = appInfo.getUserData().getNickname();
                date = Timestamp.now();
                recommendNum = 0;
                Thread insertReplyContentsThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        insertReplyContents(new ReplyContentsListItem(
                                nickname,
                                date,
                                replyContents,
                                recommendNum));
                    }
                });
                try{
                    insertReplyContentsThread.start();
                    insertReplyContentsThread.join();
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        Intent intent = getIntent();
        if (writerCheck(BoardContentsActivity.this.appInfo, boardContentsList.get(this.position).getNickname())) {
            menuInflater.inflate(R.menu.boardcontents_toolbar, menu); // 삭제 가능
        } else {
            menuInflater.inflate(R.menu.boardcontents_toolbar, menu);
            MenuItem item = menu.findItem(R.id.boardcontents_toolbar_delete);
            MenuItem item2 = menu.findItem(R.id.boardcontents_toolbar_modify);
            item.setVisible(false);
            item2.setVisible(false);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
            case R.id.boardcontents_toolbar_modify:
                modifyContents();
                return true;
            case R.id.boardcontents_toolbar_delete:
                deleteContents();
                Intent intent = new Intent();
                intent.putExtra("updateListRequest",true);
                setResult(Activity.RESULT_OK,intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    class FindThisDocument extends AsyncTask<Void,Void,String> {
        @Override
        protected String doInBackground(Void... voids) {
            CollectionReference ref = db.collection("boardContents");

            ref.whereEqualTo("nickname", contentsNickname)
                    .whereEqualTo("date", contentsDate)
                    .get()
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w("11", "db fail.", e);
                            e.printStackTrace();
                        }
                    })
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    documentID = documentID.concat(document.getId());
                                    System.out.println("1:"+ getApplicationContext());
                                }
                                new CreateReplyContentsList().execute();
                            }
                            else{
                                Log.w("2", "task fail.");
                            }
                        }
                    });

            return null;
        }
    }

    public void recommendBtnAction(){
        //String documentID = findThisDocument(this.contentsNickname, this.contentsDate);
        if(documentID.equals("")){
            Log.w("1","db fail");
            return;
        }
        final DocumentReference ref = db.collection("boardContents").document(documentID);
        if(ref != null){
            db.runTransaction(new Transaction.Function<Long>() {
                @Override
                public Long apply(Transaction transaction) throws FirebaseFirestoreException {
                    DocumentSnapshot snapshot = transaction.get(ref);
                    long newPopulation = snapshot.getLong("recommendNum") + 1;
                    if (newPopulation <= 99) {
                        transaction.update(ref, "recommendNum", newPopulation);
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
                    Log.w("1", "Transaction ss");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.w("1", "Transaction failure.", e);
                }
            });
        }
    }

    public void modifyContents(){
        Intent intent = new Intent(this, BoardContentsWriteActivity.class);
        intent.putExtra("documentID",this.documentID);
        intent.putExtra("position",this.position);
        intent.putExtra("modify",true);
        startActivity(intent);
        finish();
    }

    public void deleteContents() {
        /*
        createReplyContents 와 구조 동일
        하위 컬렉션(reply) 삭제후 상위 컬렉션(boardContents) 삭제
         */
        if (this.appInfo.getUser() != null) {
            //String documentID = findThisDocument(this.contentsNickname, this.contentsDate);
            if(documentID.equals("")){
                Log.w("1","db fail");
                return;
            }
            final DocumentReference ref = db.collection("boardContents").document(documentID);

            ref.collection("reply").document()
                    .delete()
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            e.printStackTrace();
                        }
                    });
            ref.delete()
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            e.printStackTrace();
                        }
                    });
        }
    }

    class CreateReplyContentsList extends AsyncTask<Void,Void,String> {
         /*
        상위 컬렉션 boardContents, nickname/date 부분 검색 - 검색 받은 결과(task)를 QueryDocumentSnapshot document에 저장
        1차 결과로 선택한 게시글에 맞는 reply 컬렉션 검색 - 서버에 저장하고 동시에 클라 replyContentsList에 저장

        덧글 개수 갱신은 여러 사용자가 동시에 접근할 수 있어서 runTransaction 사용
         */
        @Override
        protected String doInBackground(Void... voids) {
            if(documentID.equals("")){
                Log.w("1","db fail");
            }
            else {
                DocumentReference ref = db.collection("boardContents").document(documentID);
                ref.collection("reply").orderBy("date", Query.Direction.DESCENDING)
                        .get()
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                e.printStackTrace();
                            }
                        })
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if(task.isSuccessful()){
                                    replyContentsList.clear();
                                    for (QueryDocumentSnapshot replyDocument : task.getResult()) {
                                        if (replyDocument.get("nickname") != null) {
                                            replyContentsList.add(new ReplyContentsListItem(
                                                    replyDocument.getString("nickname"),
                                                    replyDocument.getTimestamp("date"),
                                                    replyDocument.getString("contents"),
                                                    replyDocument.getLong("recommendNum")
                                            ));
                                        }
                                    }
                                    publishProgress();
                                }
                            }
                        });
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            try{
                BoardContentsActivity.this.updateList();
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public void insertReplyContents(final ReplyContentsListItem data) {
        /*
        최초 덧글 리스트 생성
       createReplyContents 와 구조 동일
       덧글 생성을 date 기준으로 내림차순
        */
        if (this.appInfo.getUser() != null) {
            //1차 검색
            if(documentID.equals("")){
                Log.w("1","db fail");
                return;
            }
            final DocumentReference ref = db.collection("boardContents").document(documentID);
            ref.collection("reply").add(data)
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w("1", "db failed.", e);
                            return;
                        }
                    })
                    .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            new CreateReplyContentsList().execute();
                        }
                    });
            //덧글 갯수 갱신

            db.runTransaction(new Transaction.Function<Long>() {
                @Override
                public Long apply(Transaction transaction) throws FirebaseFirestoreException {
                    DocumentSnapshot snapshot = transaction.get(ref);
                    long newPopulation = snapshot.getLong("replyNum") + 1;
                    if (newPopulation <= 99) {
                        transaction.update(ref, "replyNum", newPopulation);
                        return newPopulation;
                    } else {
                        throw new FirebaseFirestoreException("Population too high",
                                FirebaseFirestoreException.Code.ABORTED);
                    }
                }
            });
        }
    }

    //AccessActivity를 이용해 replyContentsListadapter 연결하려고 추가
    @Override
    public void onClick(int position) {
        deleteReplyContents(position);
    }

    public void deleteReplyContents(int position) {
        if (this.appInfo.getUser() != null) {
            if(documentID.equals("")){
                Log.w("1","db fail");
                return;
            }
            final DocumentReference ref = db.collection("boardContents").document(documentID);

            ref.collection("reply")
                    .whereEqualTo("nickname", this.replyContentsList.get(position).getNickname())
                    .whereEqualTo("date", this.replyContentsList.get(position).getDate())
                    .get()
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            e.printStackTrace();
                        }
                    })
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful()){
                                for(QueryDocumentSnapshot replyDocument : task.getResult()){
                                    ref.collection("reply").document(replyDocument.getId())
                                            .delete()
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    e.printStackTrace();
                                                }
                                            });

                                    System.out.println(replyDocument.getId());
                                    new CreateReplyContentsList().execute();
                                }
                            }
                        }
                    });
        }
    }

    private Boolean writerCheck(AppInfo appInfo, String nickname){
        if(appInfo.getUserData().getNickname().equals(nickname) ){
            return true;
        }
        else{
            return false;
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
                            BoardContentsActivity.this.adapter.notifyDataSetChanged();
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
}



