package com.example.preventionapp;

import androidx.fragment.app.Fragment;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import java.text.SimpleDateFormat;
import java.util.List;

public class BoardContentsListAdapter extends BaseAdapter{
    private Context context;
    private List<BoardContentsListItem> list;
    private Fragment parent;

    TextView titleView;
    TextView nicknameView;
    TextView commentNumView;
    TextView dateView;
    TextView contentsView;
    TextView recommendNumView;

    public BoardContentsListAdapter(Context context, List<BoardContentsListItem> list, Fragment parent) {
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

        View v = View.inflate(context, R.layout.fragment_board_contentslistitem, null);
        titleView = (TextView) v.findViewById(R.id.fragment_board_contentsListItem_title);
        nicknameView = (TextView) v.findViewById(R.id.fragment_board_contentsListItem_nickname);
        dateView = (TextView) v.findViewById(R.id.fragment_board_contentsListItem_date);
        contentsView = (TextView) v.findViewById(R.id.fragment_board_contentsListItem_contents);
        commentNumView = (TextView) v.findViewById(R.id.fragment_board_contentsListItem_replyNum);
        recommendNumView = (TextView) v.findViewById(R.id.fragment_board_contentsListItem_recommendNum);

        titleView.setText(list.get(position).getTitle());
        nicknameView.setText(list.get(position).getNickname());
        SimpleDateFormat sdfNow = new SimpleDateFormat("yy/MM/dd HH:mm");
        String formatDate = sdfNow.format(list.get(position).getDate().toDate());
        dateView.setText(formatDate);
        contentsView.setText(list.get(position).getContents());

        commentNumView.setText(""+list.get(position).getReplyNum());
        recommendNumView.setText(""+list.get(position).getRecommendNum());

        return v;
    }

    public void add(BoardContentsListItem item){
        list.add(0,item);
        return;
    }
}
