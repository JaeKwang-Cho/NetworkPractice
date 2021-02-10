package com.dorasima.networkpractice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MyNetworkProject extends AppCompatActivity {
    
    // 리스트뷰 구성을 위해서 필요한 데이터가 담겨 있는 ArrayList
    public static ArrayList<HashMap<String, Object>> listData = new ArrayList<HashMap<String, Object>>();
    public static RecyclerView main_list;
    TextView textView7;
    static CustomRecyclerAdapter.CustomItemClickListener customItemClickListener;

    public static Context context;

    // 서버로 부터 받아온 이미지를 담을 HashMap
    public static HashMap<String, Bitmap> imageMap = new HashMap<String, Bitmap>();
    
    // 확인할 권한 목록
    String [] permission_list ={
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_network_project);

        main_list = (RecyclerView) findViewById(R.id.main_list);
        textView7 = (TextView) findViewById(R.id.textView7);
        context = this;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permission_list, 0);
        }
        else {
            init();
        }

        // LinearLayoutManager를 세팅해주는 것을 잊지말자.
        main_list.setLayoutManager(new LinearLayoutManager(this));
        customItemClickListener = new CustomRecyclerAdapter.CustomItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Intent detail_intent = new Intent(MyNetworkProject.this,DetailActivity.class);

                //  항목 번째 해시맵을 추출한다.
                HashMap<String, Object> map = (HashMap<String, Object>)listData.get(position);
                // 글의 인덱스 번호를 가져와서 넘긴다.
                int mobile_idx = (Integer)map.get("mobile_idx");
                detail_intent.putExtra("mobile_idx",mobile_idx);
                startActivity(detail_intent);
            }
        };
        CustomRecyclerAdapter adapter = new CustomRecyclerAdapter(listData,customItemClickListener);
        // 리싸이클러 아이템 리스너는 이렇게 사용한다.
        main_list.setAdapter(adapter);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for(int result:grantResults){
            if(result== PackageManager.PERMISSION_DENIED){
                return;
            }
        }
       // init();
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


    // 데이터, 경로 초기화 메서드
    public void init(){
        String tempPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        String dirPath = tempPath+"/Android/data/"+getPackageName();

        File file = new File(dirPath);
        if(file.exists()==false){
            file.mkdir();
        }

    }
    // 메인 메뉴 구성
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu,menu);
        return true;
    }
    // 현재 액티비티가 다시 가동되면 호출되는 메서드
    @Override
    protected void onPostResume() {
        super.onPostResume();

        GetDataThread thread = new GetDataThread();
        thread.start();
    }
    // 서버에서 데이터를 받아오는 스레드이다.
    protected class GetDataThread extends Thread{
        @Override
        public void run() {
            super.run();
            OkHttpClient client = new OkHttpClient();
            Request.Builder builder = new Request.Builder();
            builder = builder.url("http://192.168.35.240:8080/TestServer/get_list.jsp");
            Request request = builder.build();

            MyCallback callback1 = new MyCallback();
            Call call = client.newCall(request);
            call.enqueue(callback1);
        }
    }
    // 서버를 받아오는 스레드에서 호출하는 콜백
    protected class MyCallback implements Callback{
        @Override
        public void onFailure(@NotNull Call call, @NotNull IOException e) {

        }
        @Override
        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
            String result = response.body().string();

            listData.clear();
            //
            try {
                JSONArray root = new JSONArray(result);

                for(int i = 0;i<root.length();i++){
                    JSONObject obj = root.getJSONObject(i);

                    int mobile_idx = obj.getInt("mobile_idx");
                    String mobile_str1 = obj.getString("mobile_str1");
                    String mobile_image = obj.getString("mobile_image");

                    HashMap<String, Object> map = new HashMap<String,Object>();
                    map.put("mobile_idx",mobile_idx);
                    map.put("mobile_str1",mobile_str1);
                    map.put("mobile_image",mobile_image );

                    listData.add(map);
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CustomRecyclerAdapter adapter = new CustomRecyclerAdapter(listData,customItemClickListener);
                        main_list.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                    }
                });
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    // 이미지 데이터를 받아오는 스레드
    public static class ImageNetworkThread extends Thread{
        String fileName;

        ImageNetworkThread(String fileName){
            this.fileName = fileName;
        }

        @Override
        public void run() {
            super.run();
            try {
                URL url = new URL("http://192.168.35.240:8080/TestServer/upload/"+fileName);

                URLConnection connection = url.openConnection();
                InputStream is = connection.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(is);

                imageMap.put(fileName,bitmap);

                runOnUI(new Runnable() {
                    @Override
                    public void run() {
                        CustomRecyclerAdapter adapter = new CustomRecyclerAdapter(listData,customItemClickListener);
                        main_list.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                    }
                });
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    // 스테틱 메서드에서 UI 변경 스레드를 호출하려면 이렇게 한다.
    public static Handler UIHandler;
    static{
        UIHandler = new Handler(Looper.getMainLooper());
    }
    public static void runOnUI(Runnable runnable){
        UIHandler.post(runnable);
    }
}