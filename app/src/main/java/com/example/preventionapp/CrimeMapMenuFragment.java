package com.example.preventionapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class CrimeMapMenuFragment extends Fragment {

    @Nullable
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.activity_crimemap_menu, container, false);
        Button murder = view.findViewById(R.id.murder);
        Button rape = view.findViewById(R.id.rape);
        Button robbery=view.findViewById(R.id.robbery);
        Button larceny = view.findViewById(R.id.larceny);
        Button violence=view.findViewById(R.id.violence);
        murder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(),CrimeMapMurderActivity.class);
                intent.putExtra("select",0);
                startActivity(intent);
            }
        });

        robbery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(), CrimeMapMurderActivity.class);
                intent.putExtra("select",1);
                startActivity(intent);
            }
        });

        rape.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(), CrimeMapMurderActivity.class);
                intent.putExtra("select",2);
                startActivity(intent);
            }
        });


        larceny.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(), CrimeMapMurderActivity.class);
                intent.putExtra("select",3);
                startActivity(intent);
            }
        });

        violence.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(), CrimeMapMurderActivity.class);
                intent.putExtra("select",4);
                startActivity(intent);
            }
        });

        return view;
    }
}
