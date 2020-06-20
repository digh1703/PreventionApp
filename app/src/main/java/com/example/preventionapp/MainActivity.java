package com.example.preventionapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
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
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
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
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

/*
 mainActivity
 네비게이션 바 설정 - 각 버튼 별 반응
 메뉴 main_toolbar 사용
 */

@SuppressWarnings("unused")
public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private androidx.appcompat.widget.Toolbar toolbar;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private AppInfo appInfo;

    private FirebaseFirestore db;
    private TextView headerNickname;
    private TextView headerUserID;

    //private final int REQUEST_UPDATE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);
        db = FirebaseFirestore.getInstance();

        appInfo = AppInfo.getAppInfo();

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
                R.id.nav_0, R.id.nav_1,R.id.nav_2, R.id.nav_3, R.id.nav_4, R.id.nav_5 , R.id.nav_6, R.id.nav_7)
                .setDrawerLayout(drawer)
                .build();

        View headerView = navigationView.getHeaderView(0);

        headerNickname = (TextView) headerView.findViewById(R.id.header_userNickname);
        headerUserID = (TextView) headerView.findViewById(R.id.header_userID);
        headerNickname.setText(this.appInfo.getUserData().getNickname());
        headerUserID.setText(this.appInfo.getUser().getEmail());

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
                        fragmentTransaction.replace(R.id.activity_main_fragment, new CrimeMapMenuFragment());
                        fragmentTransaction.commit();
                        drawer.closeDrawer(Gravity.LEFT);
                        return true;
                    case R.id.nav_2:
                        fragmentTransaction.replace(R.id.activity_main_fragment, new CallFragment());
                        drawer.closeDrawer(Gravity.LEFT);
                        fragmentTransaction.commit();
                        return true;
                    case R.id.nav_3:
                        fragmentTransaction.replace(R.id.activity_main_fragment, new CareMenuFragment());
                        drawer.closeDrawer(Gravity.LEFT);
                        fragmentTransaction.commit();
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
                        appInfo.getmAuth().signOut();
                        appInfo.clear();
                        finish();
                        return true;
                }
                return true;
            }

        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    //activity_main id.main_toolbar 에 menu , main_toolbar.xml을 더하는 과정
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_toolbar, menu);
        //xml activity_main 가 menu 객체로 변환
        return true;
    }

    public void fragmentChange(Fragment fragment, boolean isBackStack) {

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.replace(R.id.activity_main_fragment, fragment);
        drawer.closeDrawer(Gravity.LEFT);

        if (isBackStack) {
            fragmentTransaction.addToBackStack(null);
        }

        fragmentTransaction.commit();
    }
/*
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.board_toolbar_search:
                System.out.println("검색중");
                //onSearchRequested();
                return true;
            case R.id.board_toolbar_option:
                Intent intent = new Intent(getApplicationContext(),BoardContentsWriteActivity.class);
                startActivityForResult(intent,REQUEST_UPDATE);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
*/
/*
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK){
            if(requestCode == REQUEST_UPDATE){
                if(getSupportFragmentManager().findFragmentById(R.id.activity_main_fragment).getClass().equals(BoardFragment.class)){
                    Boolean updateCheck = data.getBooleanExtra("update",false);
                    //boardContentsWriteActivity 에서 넘어오는 정보 확인
                    //업데이트(글쓰기)가 이루어지지 않았다면
                    if(updateCheck){
                        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.activity_main_fragment);
                        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                        ft.detach(fragment).attach(fragment).commit();
                    }
                }
            }
        }
    }

 */
}
