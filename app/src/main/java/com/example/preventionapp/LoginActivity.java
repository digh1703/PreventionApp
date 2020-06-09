package com.example.preventionapp;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG="SignUpActivity";
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private AppInfo appInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //중복 실행 방지
        if (!isTaskRoot()) {
            Intent intent = getIntent();
            String action = intent.getAction();
            if (intent.hasCategory("android.intent.category.LAUNCHER") && action != null && action.equals("android.intent.action.MAIN")) {
                finish();
                return;
            }
        }
        appInfo = AppInfo.getAppInfo();
        mAuth = FirebaseAuth.getInstance();
        findViewById(R.id.loginButton).setOnClickListener(onClickListener);
        findViewById(R.id.activity_login_signUpButton).setOnClickListener(onClickListener);
    }

    View.OnClickListener onClickListener= new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch(v.getId()){
                case R.id.loginButton:
                    login();
                    break;
                case R.id.activity_login_signUpButton:
                    Intent intent=new Intent(getApplicationContext(), SignUpActivity.class);
                    startActivity(intent);
                    break;
            }
        }
    };

    private void login() {
        String email = ((EditText) findViewById(R.id.emailEditText)).getText().toString();
        String password = ((EditText) findViewById(R.id.passwordEditText)).getText().toString();

        if (email.length() > 0 && password.length() > 0) {
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                final AppInfo appInfo = AppInfo.getAppInfo();
                                appInfo.setmAuth(mAuth);
                                appInfo.setUser(mAuth.getCurrentUser());

                                db = FirebaseFirestore.getInstance();
                                DocumentReference documentReference = db.collection("user").document(appInfo.getUser().getUid());

                                documentReference.get()
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                e.printStackTrace();
                                            }
                                        })
                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    DocumentSnapshot documentSnapshot = task.getResult();
                                                    User user = new User(
                                                            documentSnapshot.getString("nickname"),
                                                            documentSnapshot.getString("name"),
                                                            documentSnapshot.getString("residenceEdit"),
                                                            documentSnapshot.getString("phoneNumber"),
                                                            documentSnapshot.getString("gender")
                                                    );
                                                    if(user.getNickname().equals("")){
                                                        startToast("로그인에 실패하였습니다.");
                                                        return;
                                                    }

                                                    appInfo.getAppInfo().setUserData(user);
                                                    startToast("로그인에 성공하였습니다.");
                                                    startMainActivity();
                                                } else {
                                                    System.out.println(task.getException().toString());
                                                }
                                            }
                                        });
                            }
                        }
                    });
        }else {
            startToast("이메일 또는 비밀번호를 입력해주세요.");
        }
    }
    private void startToast(String msg){
        Toast.makeText(this, msg,
                Toast.LENGTH_SHORT).show();
    }
    private void startMainActivity(){
        Intent intent=new Intent(this,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

}
