package com.example.preventionapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class PreventionInfoContentsActivity extends AppCompatActivity {
    TextView title;
    TextView preview;
    TextView contents;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preventioninfocontents);

        title = (TextView)findViewById(R.id.activity_preventionInfoContents_title);
        preview = (TextView)findViewById(R.id.activity_preventionInfoContents_preview);
        contents = (TextView)findViewById(R.id.activity_preventionInfoContents_contents);

        int gp,cp;
        Intent intent = getIntent();
        gp = intent.getIntExtra("clickGroupItem",0);
        cp = intent.getIntExtra("clickChildItem",0);

        title.setText("절도");
        preview.setText("형법 제329조 (절도) 타인의 재물을 절취한 자는 6년이하의 징역 또는 1천만원이하의 벌금에 처한다.<개정 1995.12.29>\n" +
                "타인이 점유하고 있는 재물을 절취하는 죄. 절도죄가 성립하기 위해서는 고의와 불법영득의사가 있어야 한다.");
        contents.setText("<범죄에 취약한 장소 및 상황>\n"+
                "대문이 열려있는 집\n" +
                "자물쇠가 밖으로 채워져 있는 집\n" +
                "초인종을 눌러도 대답이 없거나 전화를 걸어도 받지 않는 집\n" +
                "초저녁에 불이 꺼져있는 집\n" +
                "대문·출입문에 정기 배달물(우유, 신문 등)이 쌓여 있는 집\n"+
                "\n"+
                "...");
    }
}
