package com.dorasima.networkpractice;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;

// 리스트의 어뎁터
public class CustomRecyclerAdapter extends RecyclerView.Adapter<CustomRecyclerAdapter.ViewHolder> {
    private ArrayList<HashMap<String, Object>> mDataList = null;
    private CustomItemClickListener listener;

    public interface CustomItemClickListener{
        void onItemClick(View v, int position);
    }

    // ViewHolder에서 OnclickListener를 implement 한다.
    public class ViewHolder extends RecyclerView.ViewHolder{
        protected final ImageView imageView;
        protected final TextView textView3;

        ViewHolder(View view) {
            super(view);
            imageView = (ImageView) view.findViewById(R.id.imageView);
            textView3 = (TextView) view.findViewById(R.id.textView3);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                // onClick을 오버라이딩하는데
                public void onClick(View view) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        if (listener != null) {
                            // 내부 인터페이스의 onItemClick을 호출한다.
                            listener.onItemClick(view, pos);
                        }
                    }
                }
            });
        }
    }
    // 내부 리스너를 적용하는 함수
    public void setOnItemClickListener(CustomItemClickListener listener){
        this.listener = listener;
    }
    public CustomRecyclerAdapter(ArrayList<HashMap<String,Object>> list){
        mDataList = list;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_of_list, parent, false);
        ViewHolder viewHolder= new ViewHolder(view);

        return viewHolder;
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HashMap<String, Object> map = mDataList.get(position);

        String mobile_img = (String)map.get("mobile_image");
        String mobile_str1 = (String)map.get("mobile_str1");

        // Image가 담긴 HashMap에서 이미지를 추출한다.
        Bitmap bitmap = MyNetworkProject.imageMap.get(mobile_img);
        if( bitmap==null){
            // 이미지 데이터가 없을때 가져오는 스레드를 만든다.
            MyNetworkProject.ImageNetworkThread thread = new MyNetworkProject.ImageNetworkThread(mobile_img);
            thread.start();
        }else{
            holder.imageView.setImageBitmap(bitmap);
        }

        //holder.imageView.setImageResource(mobile_img);
        holder.textView3.setText(mobile_str1);
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

}
