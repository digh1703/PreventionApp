package com.example.preventionapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.preventationapp.R;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preventioninfo);
/*
        if(FirebaseAuth.getInstance().getCurrentUser()==null){
            startSignUpActivity();
        }
        findViewById(R.id.loginButton).setOnClickListener(onClickListener);
*/
    }



    View.OnClickListener onClickListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch(v.getId()){
                case R.id.loginButton:
                    FirebaseAuth.getInstance().signOut();
                    startSignUpActivity();
                    break;
            }

        }
    };

    private void startSignUpActivity(){
        Intent intent=new Intent(this,SignupActivity.class);
        startActivity(intent);
    }
}
