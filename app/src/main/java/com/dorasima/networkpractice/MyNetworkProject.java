package com.dorasima.networkpractice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class MyNetworkProject extends AppCompatActivity {

    // 리스트뷰 구성을 위해서 필요한 데이터가 담겨 있는 ArrayList
    ArrayList<HashMap<String, Object>> listData = new ArrayList<HashMap<String, Object>>();
    RecyclerView main_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_network_project);

        main_list = (RecyclerView)findViewById(R.id.main_list);

        init();
        // LinearLayoutManager를 세팅해주는 것을 잊지말자.
        main_list.setLayoutManager(new LinearLayoutManager(this));
        CustomRecyclerAdapter adapter = new CustomRecyclerAdapter(listData);
        main_list.setAdapter(adapter);
        // adapter.notifyDataSetChanged();
    }
    // 데이터 초기화 메서드
    public void init(){
        HashMap<String,Object> map1= new HashMap<String,Object>();
        HashMap<String,Object> map2= new HashMap<String,Object>();
        HashMap<String,Object> map3= new HashMap<String,Object>();

        map1.put("mobile_image", android.R.drawable.ic_delete);
        map1.put("mobile_str1","항목1");

        map2.put("mobile_image", android.R.drawable.ic_menu_upload);
        map2.put("mobile_str1","항목2");

        map3.put("mobile_image", android.R.drawable.ic_menu_camera);
        map3.put("mobile_str1","항목3");

        listData.add(map1);
        listData.add(map2);
        listData.add(map3);
        // 나중에 지울 것이다.
    }
    // 리스트의 어뎁터
    public class CustomRecyclerAdapter extends RecyclerView.Adapter<CustomRecyclerAdapter.ViewHolder>{
        private ArrayList<HashMap<String, Object>> mDataList = null;

        public class ViewHolder extends RecyclerView.ViewHolder{
            protected final ImageView imageView;
            protected final TextView textView3;

            ViewHolder(View view){
                super(view);

                imageView = (ImageView)view.findViewById(R.id.imageView);
                textView3 = (TextView)view.findViewById(R.id.textView3);
            }
        }
        public CustomRecyclerAdapter(ArrayList<HashMap<String,Object>> list){
            mDataList = list;
        }
        public CustomRecyclerAdapter(){
            mDataList = null;
        }
        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_of_list,parent,false);
            ViewHolder viewHolder= new ViewHolder(view);

            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            HashMap<String, Object> map = listData.get(position);

            int mobile_img = (Integer)map.get("mobile_image");
            String mobile_str1 = (String)map.get("mobile_str1");

            holder.imageView.setImageResource(mobile_img);
            holder.textView3.setText(mobile_str1);
        }

        @Override
        public int getItemCount() {
            return mDataList.size();
        }
    }

    // 메인 메뉴 구성
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu,menu);
        return true;
    }
}