package com.example.preventionapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public class CallFragment extends Fragment {
    private String items[] = {"예", "아니요"};
    public CallFragment() {
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_call, container, false);
    }
    @Override
    public void onActivityCreated(Bundle b) {
        super.onActivityCreated(b);

        Button call = (Button)getView().findViewById(R.id.fragment_call_call);
        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog dialog;
                AlertDialog.Builder builder = new AlertDialog.Builder(CallFragment.this.getActivity());
                dialog = builder.setMessage("112 상활실로 연결됩니다\n"+ "통화하시겠습니까?\n" +
                        "(장난 전화 시 처벌을 받습니다.)")
                        .setNegativeButton("아니오",null)
                        .setPositiveButton("예",new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //String tel = "tel : 01092497712";
                                //startActivity(new Intent("android.intent.action.CALL", Uri.parse(tel)));
                            }
                        })
                        .create();
                dialog.show();
            }
        });
        Button message = (Button)getView().findViewById(R.id.message);
    }
}
