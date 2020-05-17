package com.example.preventionapp;

import android.app.Activity;
import android.os.Bundle;
import android.view.Display;
import android.widget.ExpandableListView;

import com.example.preventationapp.R;

import java.util.ArrayList;

public class DropdownTest extends Activity {
    private ExpandableListView listView;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preventioninfo);
        Display newDisplay = getWindowManager().getDefaultDisplay();
        int width = newDisplay.getWidth();

        ArrayList<myGroup> DataList = new ArrayList<myGroup>();
        listView = (ExpandableListView)findViewById(R.id.activity_preventioninfo_LV_list);
        myGroup temp = new myGroup("한글");
        temp.child.add("ㄱ");
        temp.child.add("ㄴ");
        temp.child.add("ㄷ");
        DataList.add(temp);
        temp = new myGroup("영어");
        temp.child.add("a");
        temp.child.add("b");
        temp.child.add("c");
        DataList.add(temp);
        temp = new myGroup("숫자");
        temp.child.add("1");
        temp.child.add("2");
        temp.child.add("3");
        DataList.add(temp);

        DropdownAdapter adapter = new DropdownAdapter(
                getApplicationContext(),
                R.layout.activity_preventioninfo_grouprow,
                R.layout.activity_preventioninfo_groupchildrow,
                DataList);
        listView.setIndicatorBounds(width-50, width); //이 코드를 지우면 화살표 위치가 바뀐다.
        listView.setAdapter(adapter);
    }
}