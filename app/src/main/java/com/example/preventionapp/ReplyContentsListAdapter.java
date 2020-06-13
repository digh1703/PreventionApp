package com.example.preventionapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import java.text.SimpleDateFormat;
import java.util.List;

public class ReplyContentsListAdapter extends BaseAdapter {

    private Context context;
    private List<ReplyContentsListItem> list;
    private AccessActivity accessActivity;
    private AppInfo appInfo;

    public ReplyContentsListAdapter(Context context, List<ReplyContentsListItem> list, AccessActivity accessActivity) {
        this.context = context;
        this.list = list;
        this.accessActivity = accessActivity;
        this.appInfo = AppInfo.getAppInfo();
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        final View v = View.inflate(context, R.layout.activity_boardcontents_replylistitem, null);
        TextView nicknameView = (TextView) v.findViewById(R.id.activity_boardContents_replyListItem_nickname);
        TextView dateView = (TextView) v.findViewById(R.id.activity_boardContents_replyListItem_date);
        TextView contentsView = (TextView) v.findViewById(R.id.activity_boardContents_replyListItem_contents);
        TextView recommendNumView = (TextView) v.findViewById(R.id.activity_boardContents_replyListItem_recommendNum);
        ImageButton btn = v.findViewById(R.id.activity_boardContents_replyListItem_btn_option);

        if( ! list.get(position).getNickname().equals(appInfo.getUserData().getNickname()) ){
            btn.setVisibility(View.GONE);
        }
        else{
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View mv) {
                    AlertDialog dialog;
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    dialog = builder.setMessage("정말 삭제하시겠습니까?")
                            .setNegativeButton("아니오", null)
                            .setPositiveButton("예", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    accessActivity.onClick(position);
                                }
                            })
                            .create();
                    dialog.show();
                }
            });
        }


        nicknameView.setText(list.get(position).getNickname());
        SimpleDateFormat sdfNow = new SimpleDateFormat("yy/MM/dd HH:mm");
        String formatDate = sdfNow.format(list.get(position).getDate().toDate());
        dateView.setText(formatDate);
        contentsView.setText(list.get(position).getContents());

        recommendNumView.setText(""+list.get(position).getRecommendNum());

        return v;
    }
}
