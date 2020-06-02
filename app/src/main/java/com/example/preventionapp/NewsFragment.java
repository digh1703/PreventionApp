package com.example.preventionapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

//ReCycler View 1.라이브러리다운 2. RecyclerView컴포넌트 붙이기 3.액티비티와 연결
public class NewsFragment extends Fragment {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private String[] mDataset={"1","2"};

    RequestQueue queue;

    @Nullable
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        //recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        //layoutManager = new LinearLayoutManager(getActivity());
        //recyclerView.setLayoutManager(layoutManager);



        //queue = Volley.newRequestQueue(getActivity());//requestqueue를 초기화 Volley한테 네트워크 연결 request요청
        //getNews();//뉴스 정보를 받아온다

        //1.화면이 로딩 -> 뉴스 정보를 받아온다
        //2.정보 -> 어댑터 넘겨준다
        //3.어댑터-> 셋팅
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_news, container, false);
        recyclerView = (RecyclerView)v.findViewById(R.id.my_recycler_view);
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);



        queue = Volley.newRequestQueue(getActivity());//requestqueue를 초기화 Volley한테 네트워크 연결 request요청
        getNews();//뉴스 정보를 받아온다

        return v;
    }

    public void getNews(){//json를 활용해 정보를 받아오는 함수


        String url ="https://newsapi.org/v2/everything?qintitle=%22%EB%B2%94%ED%96%89%22OR%22%ED%8F%AD%ED%96%89%22OR%22%EA%B0%95%EA%B0%84%22OR%22%EC%A0%88%EB%8F%84%22OR%22%EA%B5%AC%EC%86%8D%22OR%22%EA%B2%80%EA%B1%B0%22&sortby=publishedat&apiKey=81fce0774dc242a09cba4d1e1557fdfc";
        // 입력한 주소에서 string형태 응답 요청
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //뉴스 정보가 response라는 변수로 들어온다
                        Log.d("NEWS",response);

                        //접근방법: json>articles 키값의 배열 value>배열 항목마다 title... 확인
                        try {

                            JSONObject jsonObj=new JSONObject(response);//try catch문으로 json형태로 바꿔준다

                            JSONArray arrayArticles =jsonObj.getJSONArray("articles");

                            //response->>NewsData Class에 분류
                            List<NewsData> news=new ArrayList<>();//NewsData list인 news 를 선언

                            for(int i=0, j=arrayArticles.length(); i<j; i++){
                                JSONObject obj=arrayArticles.getJSONObject(i);

                                Log.d("NEWS",obj.toString());

                                NewsData newsData=new NewsData();
                                newsData.setTitle(obj.getString("title"));//Value값을 받아온다
                                newsData.setUrlToImage(obj.getString("urlToImage"));
                                newsData.setContent(obj.getString("description"));
                                newsData.setUrl(obj.getString("url"));
                                news.add(newsData);
                            }

                            // specify an adapter (see also next example)
                            mAdapter = new NewsAdapter(news,getActivity(), new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Object obj=v.getTag();
                                    if(obj !=null) {
                                        int position = (int)obj;
                                        String url=((NewsAdapter)mAdapter).getNews(position).getUrl();
                                        Intent intent;
                                        intent =new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                                        startActivity(intent);
                                    }
                                }
                            });//액티비티가 원본 데이터를 어댑터로 넘겨주고 어댑터는 이것을 활용
                            recyclerView.setAdapter(mAdapter);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        //response->>NewsData Class에 분류


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        //요청을 추가한다.
        queue.add(stringRequest);//queue가 알아서 처리한다.

    }
}
