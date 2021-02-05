package com.dorasima.networkpractice;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.TextureView;
import android.view.View;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HttpBasicClient extends AppCompatActivity {

    TextView text2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_http_basic_client);

        text2 = (TextView)findViewById(R.id.textView2);
    }

    public void connectServerBtn(View view){
        HttpNetworkThread thread = new HttpNetworkThread();
        thread.start();
    }

    private class HttpNetworkThread extends Thread{
        @Override
        public void run() {
            super.run();
            try{
                OkHttpClient client = new OkHttpClient();
                Request.Builder builder = new Request.Builder();
                builder = builder.url("http://192.168.35.240:8080/BasicServer_war_exploded/");

                // FormBody를 만들어서 보내면 Post 방식 그냥 보내면 Get방식이다.
                FormBody.Builder formBuilder = new FormBody.Builder();
                // 서버로 보낼 데이터를 세팅
                formBuilder.add("data1","문자열1");
                formBuilder.add("data2","문자열2");
                formBuilder.add("data3","문자열3");
                formBuilder.add("data3","문자열4");
                
                FormBody body = formBuilder.build();
                
                // POST 방식으로 요청하기 위해 세팅
                builder = builder.post(body);

                Request request = builder.build();

                Call call = client.newCall(request);
                //call.execute(); 는 콜백이 없을 때 사용한다.
                // 데이터를 수신 받을때 콜백이 쓰인다.
                HttpNetworkCallback callback = new HttpNetworkCallback();
                call.enqueue(callback);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    private class HttpNetworkCallback implements Callback{
        // 웹에서 데이터를 받을 때 콜백함수가 이용된다.
        @Override
        public void onFailure(@NotNull Call call, @NotNull IOException e) {

        }

        @Override
        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
            // 서버가 전달한 데이터를 추출한다.
            final String result = response.body().string();
            // json 양식으로 되어있으므로,
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        // JSONObject로 받는다.
                        JSONObject obj = new JSONObject(result);

                        int value1 = obj.getInt("value1");
                        double value2 = obj.getDouble("value2");
                        String value3 = obj.getString("value3");

                        text2.setText("value1: "+value1);
                        text2.append("\nvalue2: "+value2);
                        text2.append("\nvalue3: "+value3);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });
        }
    }

}