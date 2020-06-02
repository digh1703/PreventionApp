package com.example.preventionapp;

import android.content.Context;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

public class InfoFragment extends Fragment {
    private ExpandableListView listView;

    public InfoFragment(){

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_preventioninfo, container, false);

    }

    @Override
    public void onActivityCreated(Bundle b) {
        super.onActivityCreated(b);

        Display newDisplay = getActivity().getWindowManager().getDefaultDisplay();
        int width = newDisplay.getWidth();

        ArrayList<myGroup> DataList = new ArrayList<myGroup>();
        listView = (ExpandableListView)getView().findViewById(R.id.fragment_preventioninfo_LV_list);
        myGroup temp = new myGroup("폭력");
        temp.child.add("폭력의 정의");
        temp.child.add("폭력에 휘말렸을 때");
        temp.child.add("주의사항");
        temp.child.add("피해자 지원 사이트 안내");
        DataList.add(temp);
        temp = new myGroup("절도");
        temp.child.add("단순절도");
        temp.child.add("소매치기");
        temp.child.add("빈집털이");
        temp.child.add("특수절도");
        temp.child.add("해외에서 도난당할 시");
        temp.child.add("분실품 찾는 사이트 안내");
        DataList.add(temp);
        temp = new myGroup("성폭력");
        temp.child.add("성폭력의 정의");
        temp.child.add("성폭력의 범주");
        temp.child.add("성폭력에 당했을 때");
        temp.child.add("주변인이 피해를 입었을 때");
        temp.child.add("피해자 지원 사이트 안내");
        DataList.add(temp);
        temp = new myGroup("강도");
        temp.child.add("강도");
        DataList.add(temp);

        DropdownAdapter adapter = new DropdownAdapter(
                getContext(),
                R.layout.fragment_preventioninfo_grouprow,
                R.layout.fragment_preventioninfo_groupchildrow,
                DataList);
        listView.setIndicatorBounds(width-50, width); //이 코드를 지우면 화살표 위치가 바뀐다.
        listView.setAdapter(adapter);

    }
}
