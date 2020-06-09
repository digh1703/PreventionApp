package com.example.preventionapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.health.SystemHealthManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
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
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class BoardContentsActivity extends AppCompatActivity {

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
        int position = intent.getIntExtra("position",-1);
        if(position == -1){
            Log.d("2","no data");
        }

        this.contentsDate = boardContentsList.get(position).getDate();
        this.contentsNickname = boardContentsList.get(position).getNickname();
        this.recommendNum = boardContentsList.get(position).getRecommendNum();




        //findThisDocument(BoardContentsActivity.this.contentsNickname,BoardContentsActivity.this.contentsDate );

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

        titleView.setText(boardContentsList.get(position).getTitle());
        nicknameView.setText(boardContentsList.get(position).getNickname());
        SimpleDateFormat sdfNow = new SimpleDateFormat("yy/MM/dd HH:mm");
        String formatDate = sdfNow.format(boardContentsList.get(position).getDate().toDate());
        dateView.setText(formatDate);
        recommendView.setText(String.valueOf(boardContentsList.get(position).getRecommendNum()));
        contentsView.setText(boardContentsList.get(position).getContents());

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

        adapter = new ReplyContentsListAdapter(getApplicationContext(),replyContentsList);
        replyList.setAdapter(adapter);
        replyList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView,
                                    View itemView, int position, long id) {
                itemView.findViewById(R.id.activity_boardContents_replyListItem_btn_option)
                        .setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                AlertDialog dialog;
                                AlertDialog.Builder builder = new AlertDialog.Builder(BoardContentsActivity.this);
                                dialog = builder.setMessage("정말 삭제하시겠습니까?")
                                        .setNegativeButton("아니오", null)
                                        .setPositiveButton("예", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                            }
                                        })
                                        .create();
                                dialog.show();
                            }
                        });
            }
        });

        replyBtn = (Button) findViewById(R.id.activity_boardContents_btn_reply);
        replyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replyContents = replyEdit.getText().toString();

                if (replyContents.length() == 0) {
                    Toast.makeText(getApplicationContext(), "내용을 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

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
        int position = intent.getIntExtra("position", -1);
        if (position == -1) {
            Log.d("2", "no data");
        }
        if (writerCheck(BoardContentsActivity.this.appInfo, boardContentsList.get(position).getNickname())) {
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
                Toast.makeText(getApplicationContext(), "search", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.boardcontents_toolbar_delete:
                deleteContents();
                Intent intent = new Intent();
                intent.putExtra("update",true);
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
            System.out.println("aaa"+contentsNickname+"/"+contentsDate);
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
                                    System.out.println("a  "+documentID);
                                }
                                new CreateReplyContentsList().execute();
                            }
                            else{
                                Log.w("2", "task fail.");
                            }
                        }
                    });

            System.out.println("b  "+documentID);
            return null;
        }
    }

/*
    public void findThisDocument(String contentsNickname, Timestamp contentsDate){
        final long beforeTime = System.currentTimeMillis();
        System.out.println("aaa"+contentsNickname+"/"+contentsDate);
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
                                System.out.println("a  "+documentID);
                            }
                            long afterTime = System.currentTimeMillis(); // 코드 실행 후에 시간 받아오기
                            long secDiffTime = (afterTime - beforeTime)/1000; //두 시간에 차 계산
                            System.out.println("시간차이(m) : "+secDiffTime);
                        }
                        else{
                            Log.w("2", "task fail.");
                        }
                    }
                });
        System.out.println("b  "+documentID);
        return;
    }

 */

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


        /*
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


                            }
                        } else {
                            // Log.d(TAG, "Error getting documents: ", task.getException());
                            System.out.println("error");
                        }
                    }
                });


         */
    }


    class CreateReplyContentsList extends AsyncTask<Void,Void,String> {
        /*
        최초 덧글 리스트 생성
       createReplyContents 와 구조 동일
       덧글 생성을 date 기준으로 내림차순
        */
        private String result;

        @Override
        protected String doInBackground(Void... voids) {
            //String documentID = findThisDocument(this.contentsNickname, this.contentsDate);
            if(documentID.equals("")){
                Log.w("1","db fail");
            }
            else{
                DocumentReference ref = db.collection("boardContents").document(documentID);
                ref.collection("reply").orderBy("date", Query.Direction.DESCENDING)
                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@Nullable QuerySnapshot value,
                                                @Nullable FirebaseFirestoreException e) {
                                if (e != null) {
                                    Log.w("1", "Listen failed.", e);
                                    return;
                                }
                                replyContentsList.clear();
                                for (QueryDocumentSnapshot doc : value) {
                                    if (doc.get("nickname") != null) {
                                        replyContentsList.add(new ReplyContentsListItem(
                                                doc.getString("nickname"),
                                                doc.getTimestamp("date"),
                                                doc.getString("contents"),
                                                doc.getLong("recommendNum")
                                        ));
                                    }
                                }
                                updateList();
                            }
                        });
            }



/*
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
                                BoardContentsActivity.this.updateList();
                            } else {
                                //Log.d(TAG, "Error getting documents: ", task.getException());
                            }
                        }
                    });

 */
            return null;
        }
    }

    public void insertReplyContents(final ReplyContentsListItem data) {
        /*
        상위 컬렉션 boardContents, nickname/date 부분 검색 - 검색 받은 결과(task)를 QueryDocumentSnapshot document에 저장
        1차 결과로 선택한 게시글에 맞는 reply 컬렉션 검색 - 서버에 저장하고 동시에 클라 replyContentsList에 저장

        덧글 개수 갱신은 여러 사용자가 동시에 접근할 수 있어서 runTransaction 사용
         */
        if (this.appInfo.getUser() != null) {
            //1차 검색
            //String documentID = findThisDocument(this.contentsNickname, this.contentsDate);
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

/*
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
                                BoardContentsActivity.this.updateList();
                            } else {
                                // Log.d(TAG, "Error getting documents: ", task.getException());
                            }
                        }
                    });

 */
        }
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

            /*
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
                                Log.d("1", "Error DB ", task.getException());
                            }
                        }
                    });

             */
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



