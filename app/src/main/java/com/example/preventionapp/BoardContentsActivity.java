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

import java.util.ArrayList;
import java.util.List;

public class BoardContentsActivity extends AppCompatActivity {

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
    private String contentsDate;

    FirebaseUser user;
    FirebaseFirestore db;

    private EditText replyEdit;
    private int number;
    private String nickname;
    private Timestamp date;
    private String replyContents;
    private int recommendNum;
    private Button replyBtn;
    private ListView replyList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boardcontents);
        user = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();

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

        boardContentsList = new ArrayList<BoardContentsListItem>();
        Intent intent = getIntent();
        contentsDate = intent.getStringExtra("date");
        contentsNickname = intent.getStringExtra("nickname");

        titleView.setText(intent.getStringExtra("title"));
        nicknameView.setText(intent.getStringExtra("nickname"));
        dateView.setText(intent.getStringExtra("date"));
        recommendView.setText(intent.getStringExtra("recommendNum"));
        contentsView.setText(intent.getStringExtra("contents"));

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
    protected void onStart() {
        super.onStart();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.board_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
            case R.id.board_toolbar_search:
                Toast.makeText(getApplicationContext(), "search", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.board_toolbar_option:

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
                                System.out.println("good");
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

    public void createReplyContents(final String contentsNickname, String contentsDate, final ReplyContentsListItem data) {
        if(user != null){
            db.collection("boardContents")
                    .whereEqualTo("nickname", contentsNickname)
                    .whereEqualTo("date",contentsDate)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    db.collection("boardContents").document(document.getId()).
                                            collection("reply").add(data);
                                }
                            } else {
                               // Log.d(TAG, "Error getting documents: ", task.getException());
                            }
                        }
                    });
        }
    }

    class CreateReplyContentsList extends AsyncTask<Void,Void,String> {
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
                                                            document.getData().get("nickname").toString(),
                                                            (Timestamp) document.getData().get("date"),
                                                            document.getData().get("contents").toString(),
                                                            (Long) document.getData().get("recommendNum")
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



