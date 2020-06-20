package com.example.preventionapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ETFSlideStep2Fragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_etf_slide_step2, container, false);

        //2단계 화면이라면 액티비티에서 설정된 증상과 고통지수를 가져와 출력

        TextView tvSympton = v.findViewById(R.id.tv_symptom);
        tvSympton.setText(((ETFSlideActivity)getActivity()).getSympton());

        TextView tvLevel = v.findViewById(R.id.tv_level);
        tvLevel.setText("고통지수 : " + ((ETFSlideActivity)getActivity()).getLevel());

        return v;
    }
}
