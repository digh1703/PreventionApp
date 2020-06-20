package com.example.preventionapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class CareMenuFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_caremenu, container, false);

        Button etfStart = rootView.findViewById(R.id.etf_start);
        Button hospitalSearch = rootView.findViewById(R.id.hospital_search);

        etfStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ETFSlideActivity.class);
                startActivity(intent);
            }
        });

        hospitalSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).fragmentChange(new HospitalMapFragment(), true);
            }
        });

        return rootView;
    }
}
