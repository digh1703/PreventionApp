package com.example.preventionapp;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.ui.AppBarConfiguration;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class MainActivity extends AppCompatActivity {

    AppBarConfiguration mAppBarConfiguration;
    androidx.appcompat.widget.Toolbar toolbar;
    DrawerLayout drawer;
    NavigationView navigationView;
    private FirebaseAuth mAuth;
    FirebaseUser user;
    private FirebaseFirestore db;
    TextView headerNickname;
    TextView headerUserID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);

        user = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();
        if(user==null){
            finish();
        }


        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.activity_main_fragment, new MainFragment());
        fragmentTransaction.commit();

        toolbar = (androidx.appcompat.widget.Toolbar)findViewById(R.id.toolbar);
        setTitle("");
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);

        //네비게이션 뷰 자체를 컨트롤할때
        navigationView = findViewById(R.id.nav_view);

        //네비게이션 뷰 내의 버튼 여기서 생성
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_2, R.id.nav_3, R.id.nav_4, R.id.nav_5 , R.id.nav_6)
                .setDrawerLayout(drawer)
                .build();

        View headerView = navigationView.getHeaderView(0);

        headerNickname = (TextView) headerView.findViewById(R.id.header_userNickname);
        headerUserID = (TextView) headerView.findViewById(R.id.header_userID);

        db.collection("user").document(user.getUid())
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot d = task.getResult();
                    if(d.exists())
                        headerNickname.setText(d.getString("nickname"));
                }
            }
        });
        headerUserID.setText(user.getDisplayName());

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.toolbar_menu);

        //drawer 관련
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(
                this,
                drawer,
                toolbar,
                R.string.open,
                R.string.closed);
        drawer.addDrawerListener(actionBarDrawerToggle);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                switch (menuItem.getItemId()) {
                    case R.id.nav_0:
                        fragmentTransaction.replace(R.id.activity_main_fragment, new MainFragment());
                        fragmentTransaction.commit();
                        drawer.closeDrawer(Gravity.LEFT);
                        return true;
                    case R.id.nav_1:
                        Intent intent = new Intent(getApplicationContext(), CrimeMap.class);
                        startActivity(intent);
                        return true;
                    case R.id.nav_2:
                        fragmentTransaction.replace(R.id.activity_main_fragment, new CallFragment());
                        drawer.closeDrawer(Gravity.LEFT);
                        fragmentTransaction.commit();
                        return true;
                    case R.id.nav_3:
                        Toast.makeText(getApplicationContext(), "SelectedItem 3", Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.nav_4:
                        fragmentTransaction.replace(R.id.activity_main_fragment, new InfoFragment());
                        drawer.closeDrawer(Gravity.LEFT);
                        fragmentTransaction.commit();
                        return true;
                    case R.id.nav_5:
                        fragmentTransaction.replace(R.id.activity_main_fragment, new BoardFragment());
                        drawer.closeDrawer(Gravity.LEFT);
                        fragmentTransaction.commit();
                        return true;
                    case R.id.nav_6:
                        fragmentTransaction.replace(R.id.activity_main_fragment, new NewsFragment());
                        drawer.closeDrawer(Gravity.LEFT);
                        fragmentTransaction.commit();
                        return true;
                    case R.id.nav_7:
                        mAuth.signOut();
                        return true;
                }
                return true;
            }

        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        if(getSupportFragmentManager().findFragmentById(R.id.activity_main_fragment).getClass().equals(BoardFragment.class)){
            System.out.println("fragment found");

        }
    }

    //activity_main id.main_toolbar 에 menu , main_toolbar.xml을 더하는 과정
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_toolbar, menu);
        //xml activity_main 가 menu 객체로 변환
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.board_toolbar_search:
                Toast.makeText(getApplicationContext(), "search", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.board_toolbar_option:
                Intent intent = new Intent(getApplicationContext(),BoardContentsWriteActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    

}
