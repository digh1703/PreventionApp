package com.example.preventionapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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

import okio.Timeout;

public class SignUpActivity extends AppCompatActivity {

    private FirebaseAuth mAuth = null;
    private FirebaseFirestore db;
    private Toolbar toolbar;
    private FirebaseUser user;

    private ArrayAdapter genderAdapter;
    private Spinner genderSpinner;
    private String email;
    private String nickname;
    EditText nicknameEdit;
    private String password;
    private String passwordCheck;
    private String name;
    private String residenceEdit;
    private String phoneNumber;
    private Button nicknameCheckBtn;
    private Button emailCheckBtn;
    private Button signUpBtn;

    private Boolean nicknameCheck = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        toolbar = (androidx.appcompat.widget.Toolbar)findViewById(R.id.toolbar);
        setTitle("");

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        nicknameEdit = findViewById(R.id.nicknameEdit);
        nicknameEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                nicknameCheckBtn.setEnabled(true);
                nicknameCheckBtn.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.colorPrimary));
                nicknameCheck = false;
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        findViewById(R.id.nicknameEdit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        emailCheckBtn = (Button)findViewById(R.id.emailCheck);
        nicknameCheckBtn = (Button)findViewById(R.id.nicknameCheck);
        signUpBtn = (Button)findViewById(R.id.activity_signUp_signUpButton);

        View.OnClickListener onClickListener= new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.emailCheck:
                        System.out.println(nicknameCheck);
                        break;
                    case R.id.nicknameCheck:
                        nicknameChecking(nicknameEdit.getText().toString());
                        break;
                    case R.id.activity_signUp_signUpButton:
                        signUp();
                        break;
                }
            }
        };
        emailCheckBtn.setOnClickListener(onClickListener);
        nicknameCheckBtn.setOnClickListener(onClickListener);
        signUpBtn.setOnClickListener(onClickListener);
    }

    private int nicknameChecking(String nickname){
        DocumentReference ref;
        db = FirebaseFirestore.getInstance();

        db.collection("user").whereEqualTo("nickname",nickname).get()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                    }
                })
                .addOnCompleteListener( new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.getResult().isEmpty()){
                            startToast("사용할 수 있는 별명입니다.");
                            nicknameCheckBtn.setEnabled(false);
                            nicknameCheckBtn.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.colorGray));
                            nicknameCheck = true;
                        }
                        else{
                            startToast("사용할 수 없는 별명입니다.");
                        }
                    }
                });

        //리스너는 부른 순서대로 호출


        return 1;
    }

    private void signUp() {
        email = ((EditText) findViewById(R.id.emailEditText)).getText().toString();
        nickname = ((EditText) findViewById(R.id.nicknameEdit)).getText().toString();
        password = ((EditText) findViewById(R.id.passwordEditText)).getText().toString();
        passwordCheck = ((EditText) findViewById(R.id.passwordCheckEditText)).getText().toString();
        name = ((EditText) findViewById(R.id.nameEdit)).getText().toString();
        genderSpinner = (Spinner) findViewById(R.id.genderSpinner);
        genderAdapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.genderArray,
                android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(genderAdapter);
        residenceEdit = ((EditText) findViewById(R.id.residenceEdit)).getText().toString();
        phoneNumber = ((EditText) findViewById(R.id.phoneEdit)).getText().toString();

        if (email.length() > 0 && password.length() > 0 && passwordCheck.length() > 0 && nicknameCheck==true) {
            if (password.equals(passwordCheck)) {
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    startToast("회원가입이 성공하였습니다.");
                                    user = mAuth.getCurrentUser();
                                    db.collection("user").document(user.getUid())
                                            .set(new User(nickname, name, residenceEdit, phoneNumber, genderSpinner.getSelectedItem().toString()));
                                    finish();
                                } else {
                                    if (task.getException() != null) {
                                        startToast(task.getException().toString());
                                    }
                                }
                            }
                        });
            } else {
                startToast("비밀번호가 일치하지 않습니다.");
            }
        } else {
            startToast("이메일 또는 비밀번호를 입력해주세요.");
        }
    }
    private void startToast(String msg){
        Toast.makeText(this, msg,
                Toast.LENGTH_SHORT).show();
    }

    private void startLoginActivity(){
        Intent intent=new Intent(this,LoginActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    public void onBackPressed(){
        super.onBackPressed();
        moveTaskToBack(true);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
    }
}
