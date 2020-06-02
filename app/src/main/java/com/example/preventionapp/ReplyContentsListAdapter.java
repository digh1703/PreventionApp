package com.example.preventionapp;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import java.text.SimpleDateFormat;
import java.util.List;

public class ReplyContentsListAdapter extends BaseAdapter {

    private Context context;
    private List<ReplyContentsListItem> list;

    public ReplyContentsListAdapter(Context context, List<ReplyContentsListItem> list) {
        this.context = context;
        this.list = list;
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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = View.inflate(context, R.layout.activity_boardcontents_replylistitem, null);
        TextView nicknameView = (TextView) v.findViewById(R.id.activity_boardContents_replyListItem_nickname);
        TextView dateView = (TextView) v.findViewById(R.id.activity_boardContents_replyListItem_date);
        TextView contentsView = (TextView) v.findViewById(R.id.activity_boardContents_replyListItem_contents);
        TextView recommendNumView = (TextView) v.findViewById(R.id.activity_boardContents_replyListItem_recommendNum);

        nicknameView.setText(list.get(position).getNickname());
        SimpleDateFormat sdfNow = new SimpleDateFormat("yy/MM/dd HH:mm");
        String formatDate = sdfNow.format(list.get(position).getDate().toDate());
        dateView.setText(formatDate);
        contentsView.setText(list.get(position).getContents());

        recommendNumView.setText(""+list.get(position).getRecommendNum());

        return v;
    }
}
