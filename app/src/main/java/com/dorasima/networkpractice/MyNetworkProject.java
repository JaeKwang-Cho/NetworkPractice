package com.dorasima.networkpractice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class MyNetworkProject extends AppCompatActivity implements CustomRecyclerAdapter.OnItemClickListener {

    @Override
    public void onItemClick(View v, int position) {
        Log.d("test","clicked "+position);
    }

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
        // 리싸이클러 아이템 리스너는 이렇게 사용한다.
        adapter.setOnItemClickListener(new CustomRecyclerAdapter.OnItemClickListener(){
            @Override
            public void onItemClick(View v, int position) {
                Intent detail_intent = new Intent(MyNetworkProject.this,DetailActivity.class);
                startActivity(detail_intent);
            }
        });
        main_list.setAdapter(adapter);
        // adapter.notifyDataSetChanged();
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        switch (id){
            case R.id.menu_write:
                Intent write_intent= new Intent(this, WriteActivty.class);
                startActivity(write_intent);
                break;
            case R.id.menu_reload:
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
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
    // 메인 메뉴 구성
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu,menu);
        return true;
    }

}