package com.example.preventionapp;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import java.util.List;

public class MainBoardContentsListAdapter extends BaseAdapter{
    private Context context;
    private List<BoardContentsListItem> list;
    private Fragment parent;

    TextView titleView;
    TextView contentsView;

    public MainBoardContentsListAdapter(Context context, List<BoardContentsListItem> list, Fragment parent) {
        this.context = context;
        this.list = list;
        this.parent = parent;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    //xml 파일과 연결
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String contents;

        View v = View.inflate(context, R.layout.fragment_main_contentslistitem, null);
        titleView = (TextView) v.findViewById(R.id.fragment_main_contentsListItem_title);
        contentsView = (TextView) v.findViewById(R.id.fragment_main_contentsListItem_contents);

        titleView.setText(list.get(position).getTitle());
        contentsView.setText(list.get(position).getContents());

        return v;
    }

    public void add(BoardContentsListItem item){
        list.add(0,item);
        return;
    }
}
