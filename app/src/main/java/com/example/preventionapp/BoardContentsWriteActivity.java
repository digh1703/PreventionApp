package com.example.preventionapp;

import android.content.Intent;
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
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class BoardContentsWriteActivity extends AppCompatActivity {
    androidx.appcompat.widget.Toolbar toolbar;
    private EditText titleEdit;
    private EditText contentsEdit;

    FirebaseUser user;

    FirebaseFirestore db;
    private long number;
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
        user = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();

        toolbar = (androidx.appcompat.widget.Toolbar)findViewById(R.id.toolbar);
        setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        titleEdit = (EditText)findViewById(R.id.activity_boardContentsWrite_Edit_title);
        contentsEdit = (EditText)findViewById(R.id.activity_boardContentsWrite_Edit_contents);

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

                nickname = "";
                date = Timestamp.now();
                replyNum = 0;
                recommendNum = 0;
                BoardContentsListItem item = new BoardContentsListItem(title,nickname,date,contents,replyNum,recommendNum);
                update(item);
                Intent intent = new Intent();
                intent.putExtra("item",item);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
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

    public void update(final BoardContentsListItem data) {
        if(user != null){
            db.collection("boardContents").add(data)
                    .addOnSuccessListener(new OnSuccessListener() {
                        @Override
                        public void onSuccess(Object o) {
                            Toast.makeText(getApplicationContext(), "추가 완료", Toast.LENGTH_SHORT).show();
                            BoardFragment boardFragment = new BoardFragment();
                            boardFragment.addContentsList(data);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), "추가 실패", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        else{
            //아이디가 없는 에러
        }
    }

}
