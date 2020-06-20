package com.example.preventionapp;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

public class ETFSlideStep3Fragment extends Fragment {

    private static final int SLIDE_DELAYED = 10000;

    private Handler slideHandler;
    private SwipeViewPager viewPager;
    private ImageViewPagerAdapter imageViewPagerAdapter;
    private int currentPage = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_etf_slide_step3, container, false);

        viewPager = v.findViewById(R.id.viewpager);
        viewPager.setPagingEnabled(false);
        viewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //자동 애니메이션에서는 터치 막기
                return true;
            }
        });

        imageViewPagerAdapter = new ImageViewPagerAdapter(getActivity());
        viewPager.setAdapter(imageViewPagerAdapter);
        viewPager.addOnPageChangeListener(onPageChangeListener);

        return v;
    }

    private ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            viewPager.setPagingEnabled(false);
            currentPage = position;
            //페이지 변경시 다음페이지 타이머 실행
            startSlide();
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    /**
     * 슬라이드 에니메이션을 시작한다.
     */
    public void startSlide() {
        slideHandler = new Handler();
        slideHandler.postDelayed(slideRunnable, SLIDE_DELAYED);
    }

    /**
     * 슬라이드 에미메이션을 종료한다.
     */
    public void stopSlide() {

        if (null != slideHandler) {
            slideHandler.removeCallbacks(slideRunnable);
        }

        currentPage = 0;
        viewPager.removeOnPageChangeListener(onPageChangeListener);
        viewPager.setCurrentItem(0, false);
        viewPager.addOnPageChangeListener(onPageChangeListener);
    }

    private Runnable slideRunnable = new Runnable() {
        @Override
        public void run() {

            if (currentPage == imageViewPagerAdapter.getCount() - 1) {
                //마지막 페이지라면 다음 ETF 단계로 이동
                ((ETFSlideActivity)getActivity()).nextPage();
                stopSlide();
            }
            else {
                //마지막 페이지가 아니면 다음 페이지로
                viewPager.setCurrentItem(++currentPage);
            }
        }
    };


    public class ImageViewPagerAdapter extends PagerAdapter {

        private Context context;
        private int[] tappingImageList;

        public ImageViewPagerAdapter(Context context) {

            this.context = context;
            //Tapping 이미지 순서대로 초기화
            tappingImageList = new int[]
                    {R.drawable.tapping_1, R.drawable.tapping_2, R.drawable.tapping_3,
                            R.drawable.tapping_4, R.drawable.tapping_5, R.drawable.tapping_6,
                            R.drawable.tapping_7, R.drawable.tapping_8, R.drawable.tapping_9,
                            R.drawable.tapping_10, R.drawable.tapping_11, R.drawable.tapping_12,
                            R.drawable.tapping_13, R.drawable.tapping_14};
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.layout_tapping, null);

            ImageView imageView = view.findViewById(R.id.iv_tapping);
            imageView.setImageResource(tappingImageList[position]);

            container.addView(view);

            return view;
        }

        @Override
        public int getCount() {
            return tappingImageList.length;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
            return (view == (View) o);
        }
    }
}
