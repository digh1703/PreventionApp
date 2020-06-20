package com.example.preventionapp;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;

public class ETFSlideActivity extends AppCompatActivity {

    private EditText etSympton;
    private EditText etLevel;

    private LinearLayout layoutInput;
    private SwipeViewPager viewPager;
    private ETFPagerAdapter etfPagerAdapter;

    private ArrayList<Fragment> etfPageList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_etf_slide);

        layoutInput = findViewById(R.id.layout_input);
        viewPager = findViewById(R.id.view_pager);
        viewPager.setOffscreenPageLimit(4);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (1 == position) {
                    //3단계화면이 선택되면 슬라이드 실행 및 뷰 페이지 이동 막기
                    ETFSlideStep3Fragment etfSlideStep3Fragment = (ETFSlideStep3Fragment)etfPageList.get(position);
                    etfSlideStep3Fragment.stopSlide();
                    etfSlideStep3Fragment.startSlide();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        //슬라이드 단계별 화면 추가
        etfPageList = new ArrayList<>();
        etfPageList.add(new ETFSlideStep2Fragment());
        etfPageList.add(new ETFSlideStep3Fragment());
        etfPageList.add(new ETFSlideStep4Fragment());
        etfPageList.add(new ETFSlideStep6Fragment());

        etSympton = findViewById(R.id.et_symptom);
        etLevel = findViewById(R.id.et_level);

        Button btnNext = findViewById(R.id.btn_next);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (TextUtils.isEmpty(etSympton.getText().toString()) || TextUtils.isEmpty(etLevel.getText().toString())) {
                    startToast("증상과 고통지수를 입력해주세요.");
                    return;
                }

                //다음 버튼을 누르면 ETF 화면 시작
                etfPagerAdapter = new ETFPagerAdapter(getSupportFragmentManager());
                viewPager.setAdapter(etfPagerAdapter);

                layoutInput.setVisibility(View.GONE);
                viewPager.setVisibility(View.VISIBLE);
            }
        });
    }

    private class ETFPagerAdapter extends FragmentStatePagerAdapter
    {
        public ETFPagerAdapter(FragmentManager fm)
        {
            super(fm);
        }

        @Override
        public Fragment getItem(int position)
        {
            return etfPageList.get(position);
        }

        @Override
        public int getCount()
        {
            return etfPageList.size();
        }
    }

    public String getSympton() {
        return etSympton.getText().toString();
    }

    public String getLevel() {
        return etLevel.getText().toString();
    }

    /**
     * 다음페이지 이동
     */
    public void nextPage() {

        if (etfPagerAdapter.getCount() - 1 > viewPager.getCurrentItem())
            viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
    }

    /**
     * 2단계 페이지 이동
     */
    public void goStep2() {
        viewPager.setCurrentItem(0);
    }

    private void startToast(String msg){
        Toast.makeText(this, msg,
                Toast.LENGTH_SHORT).show();
    }
}
