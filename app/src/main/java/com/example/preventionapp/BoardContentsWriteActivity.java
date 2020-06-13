package com.example.preventionapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;

/*
 BoardContentsWriteActivity
 public void insertContents(final BoardContentsListItem data)
 public void updateContents(String documentID, final String contents)

 이 액티비티는 두 번 활용 (작성 / 수정)
 modifyCheck 를 통해 이 액티비티로 들어오는 intent 데이터를 받고 작성인지 수정인지 확인
 BoardFragment -> 메뉴(글 작성) -> onCreate 점검 - insertContents
 BoardContentsActivity -> 메뉴(글 수정) -> modify, true -> onCreate 점검 - updateContents
 */

public class BoardContentsWriteActivity extends AppCompatActivity {
    androidx.appcompat.widget.Toolbar toolbar;
    private EditText titleEdit;
    private EditText contentsEdit;

    private AppInfo appInfo;
    private BoardContentsList boardContentsList;

    FirebaseFirestore db;
    private String title;
    private String nickname;
    private Timestamp date;
    private String contents;
    private long replyNum;
    private long recommendNum;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boardcontentswrite);

        appInfo = AppInfo.getAppInfo();
        boardContentsList = BoardContentsList.getboardContentsList();
        if(appInfo.getmAuth() != null){
            db = FirebaseFirestore.getInstance();
        }
        else{
            finish();
        }

        toolbar = (androidx.appcompat.widget.Toolbar)findViewById(R.id.toolbar);
        setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        titleEdit = (EditText)findViewById(R.id.activity_boardContentsWrite_Edit_title);
        contentsEdit = (EditText)findViewById(R.id.activity_boardContentsWrite_Edit_contents);

        final Intent intent = getIntent();
        boolean modifyCheck = intent.getBooleanExtra("modify",false);
        if(modifyCheck){
            final int position = intent.getIntExtra("position",-1);
            if(position == -1){
                return;
                //범위를 벗어난 리스트 에러
            }
            //제목은 수정 불가능 처리
            titleEdit.setFocusable(false);
            titleEdit.setClickable(false);
            titleEdit.setText(this.boardContentsList.get(position).getTitle());
            contentsEdit.setText(this.boardContentsList.get(position).getContents());
            Button writeBtn = (Button)findViewById(R.id.activity_boardContentsWrite_btn_write);
            writeBtn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    contents = contentsEdit.getText().toString();

                    if(contents.length() ==0){
                        Toast.makeText(getApplicationContext(), "내용을 입력해주세요.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    updateContents(intent.getStringExtra("documentID"),contents);
                    finish();
                }
            });
        }
        else{
            Button writeBtn = (Button)findViewById(R.id.activity_boardContentsWrite_btn_write);
            writeBtn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    title = titleEdit.getText().toString();
                    contents = contentsEdit.getText().toString();

                    if(title.length()==0){
                        Toast.makeText(getApplicationContext(), "제목을 입력해주세요.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if(contents.length() ==0){
                        Toast.makeText(getApplicationContext(), "내용을 입력해주세요.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    nickname = BoardContentsWriteActivity.this.appInfo.getUserData().getNickname();
                    date = Timestamp.now();
                    replyNum = 0;
                    recommendNum = 0;
                    BoardContentsListItem item = new BoardContentsListItem(title,nickname,date,contents,replyNum,recommendNum);
                    insertContents(item);
                    Intent intent = new Intent();
                    intent.putExtra("update",true);
                    setResult(Activity.RESULT_OK,intent);
                    finish();
                }
            });
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void insertContents(final BoardContentsListItem data) {
        if(appInfo.getUser() != null){
            db.collection("boardContents").add(data)
                    .addOnSuccessListener(new OnSuccessListener() {
                        @Override
                        public void onSuccess(Object o) {
                            Toast.makeText(getApplicationContext(), "작성 완료", Toast.LENGTH_SHORT).show();
                            //BoardContentsWriteActivity.this.boardContentsList.add(data);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), "작성 실패", Toast.LENGTH_SHORT).show();
                        }
                    });

        }
        else{
           Log.d("1","user access fail");
        }
    }

    public void updateContents(String documentID, final String contents){
        if(appInfo.getUser() != null){
            db.collection("boardContents").document(documentID)
                    .update("contents",contents)
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            e.printStackTrace();
                        }
                    });
        }
    }
}
