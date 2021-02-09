package com.dorasima.networkpractice;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DetailActivity extends AppCompatActivity {
    ImageView imageView3;
    TextView textView4, textView5;

    int mobile_idx;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        imageView3 = (ImageView)findViewById(R.id.imageView3);
        textView4 = (TextView)findViewById(R.id.textView4);
        textView5 = (TextView)findViewById(R.id.textView5);

        // 글 번호를 가져온다.
        Intent intent= getIntent();
        mobile_idx = intent.getIntExtra("mobile_idx",0);

        GetDataThread thread = new GetDataThread();
        thread.start();
    }

    class GetDataThread extends Thread{
        @Override
        public void run() {
            super.run();

            // 클라이언트를 만들고 URL을 지정한다.
            OkHttpClient client = new OkHttpClient();
            Request.Builder builder = new Request.Builder();
            builder = builder.url("http://192.168.35.240:8080/TestServer/get_data.jsp");

            // 폼 바디를 만든다.
            FormBody.Builder fBuilder = new FormBody.Builder();
            fBuilder.add("mobile_idx",mobile_idx+"");

            FormBody body = fBuilder.build();
            builder = builder.post(body);

            // 콜백을 지정한다.
            Request request = builder.build();
            DetailCallback detailCallback = new DetailCallback();
            Call call = client.newCall(request);
            call.enqueue(detailCallback);
        }
    }
    class DetailCallback implements Callback{
        @Override
        public void onFailure(@NotNull Call call, @NotNull IOException e) {
        }
        @Override
        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
            try {
                String result = response.body().string();

                JSONObject object = null;
                object = new JSONObject(result);
                String mobile_image = object.getString("mobile_image");
                final String mobile_str1 = object.getString("mobile_str1");
                final String mobile_str2 = object.getString("mobile_str2");

                ImageNetworkThread imageNetworkThread = new ImageNetworkThread(mobile_image);
                imageNetworkThread.start();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        textView4.setText(mobile_str1);
                        textView5.setText(mobile_str2);
                    }
                });
            }
            catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
    // 이미지 데이터를 받아오는 스레드
    public class ImageNetworkThread extends Thread{
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
                final Bitmap bitmap = BitmapFactory.decodeStream(is);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        imageView3.setImageBitmap(bitmap);
                    }
                });
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}