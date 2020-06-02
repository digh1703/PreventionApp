package com.example.preventionapp;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.MyViewHolder> {
    private List<NewsData> mDataset;
    private static View.OnClickListener onClickListener;
    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder {//row news 안의 요소를 찾아가는 class
        // each data item is just a string in this case
        public TextView TextView_title;
        public TextView TextView_content;
        public SimpleDraweeView ImageView_title;
        public View rootView;

        public MyViewHolder(View v) {//view를 받는다 부모가 v이고 row_news이다
            super(v);
            //자식들, length 만큼 반복해서 표시
            TextView_title=v.findViewById(R.id.TextView_title);
            TextView_content=v.findViewById(R.id.TextView_content);
            ImageView_title= (SimpleDraweeView) v.findViewById(R.id.ImageView_title);
            rootView=v;

            v.setClickable(true);
            v.setEnabled(true);
            v.setOnClickListener(onClickListener);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public NewsAdapter(List<NewsData> myDataset, Context context,View.OnClickListener onClick) {//mDataset: 각 줄마다 보여줄 값을 들고있는 원본 데이터 변수 (다양한 변수로 설정가능)
        mDataset = myDataset;
        onClickListener=onClick;
        Fresco.initialize(context);//일반적인 class에서는 context를사용하지 않는다
    }

    // Create new views (invoked by the layout manager)
    @Override
    public NewsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,//RecyvlerView의 항목화면 연결은 onCreateViewHolder
                                                       int viewType) {
        // create a new view
        LinearLayout v = (LinearLayout) LayoutInflater.from(parent.getContext())//부모 view를 setting
                .inflate(R.layout.row_news, parent, false);//각 줄별로 내용을 보여주는 레이아웃 xml이 필요:row_news 연결

        MyViewHolder vh = new MyViewHolder(v);//화면 한줄씩 해결
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {//뷰홀더가 반복되면서 onBind~함수에서 값을 세팅한다
        //onBinder가 원본데이터의 크기만큼 반복한다는 것을 힌트삼아서 뉴스를 꺼내야 한다
        NewsData news= mDataset.get(position);

        holder.TextView_title.setText(news.getTitle());
        String content=news.getContent();
        if(content !=null && content.length()>0){
            holder.TextView_content.setText(content);
        }
        else{
            holder.TextView_content.setText("-");
        }

        Uri uri = Uri.parse(news.getUrlToImage());
        holder.ImageView_title.setImageURI(uri);

        //tag-label
        holder.rootView.setTag(position);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {

        //삼항연산자
        return mDataset==null ? 0: mDataset.size();
    }

    public NewsData getNews(int position){
        return mDataset!=null ? mDataset.get(position):null;
    }
}
