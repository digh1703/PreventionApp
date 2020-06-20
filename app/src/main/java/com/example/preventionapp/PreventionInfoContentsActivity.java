package com.example.preventionapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class PreventionInfoContentsActivity extends AppCompatActivity {
    androidx.appcompat.widget.Toolbar toolbar;

    TextView title;
    TextView preview;
    TextView contents;
    AppInfo appInfo;
    FirebaseFirestore db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preventioninfocontents);
        int gp,cp;
        appInfo = AppInfo.getAppInfo();
        db = FirebaseFirestore.getInstance();

        toolbar = (androidx.appcompat.widget.Toolbar)findViewById(R.id.toolbar);
        setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        title = (TextView)findViewById(R.id.activity_preventionInfoContents_title);
        preview = (TextView)findViewById(R.id.activity_preventionInfoContents_preview);
        preview.setMovementMethod(new ScrollingMovementMethod());
        preview.setTextSize(13);
        contents = (TextView)findViewById(R.id.activity_preventionInfoContents_contents);
        contents.setMovementMethod(new ScrollingMovementMethod());
        contents.setTextSize(16);

        Intent intent = getIntent();
        gp = intent.getIntExtra("clickGroupItem",0);
        cp = intent.getIntExtra("clickChildItem",0);

        //문서 번호 (그룹번호+1)+소그룹번호, 100부터 시작해서 101,102,103...
        DocumentReference ref = db.collection("preventionInfo").document(String.valueOf((gp+1)*100+cp));
        if(ref != null){
            ref.get()
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            e.printStackTrace();
                        }
                    })
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            DocumentSnapshot document = task.getResult();
                            title.setText(document.getString("title"));
                            preview.setText(document.getString("preview").replace("\\n","\n"));
                            contents.setText(document.getString("contents").replace("\\n","\n"));
                        }
                    });
        }
        else{
            title.setText("title");
            preview.setText("preview");
            contents.setText("contents");
        }

    }

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
}
