package com.dorasima.networkpractice;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    // pracitce okhttp

    TextView text1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        text1 = (TextView)findViewById(R.id.textView);


    }
    // 네트워크 스레드를 발생시킨다.
    public void connectServerBtn(View view){
        NetworkThread thread = new NetworkThread();
        thread.start();
    }
    public void nextBtn(View view){

    }


    // 네트워크 처리 담당 스레드
    public class NetworkThread extends Thread{
        @Override
        public void run() {
            super.run();
            // 클라이언트 개체 생성
            OkHttpClient client = new OkHttpClient();
            // 클라이언트 개체의 정보를 세팅하는 빌더를 생성한다.
            Request.Builder builder = new Request.Builder();
            // 접속할 페이지의 주소를 세팅한다.
            builder = builder.url("http://google.com");

            // 서버로 부터 응답결과가 정상적으로 수신되면
            // 호출될 메서드를 가지고 있는 개체를 만들어야 한다.

            // 콜백을 세팅한다.
            Request request = builder.build();
            Call call = client.newCall(request);

            // 콜백을 등록한다.
            NetworkCallback callback = new NetworkCallback();
            call.enqueue(callback);
        }
   }
   // 응답결과가 수신되면 반응하는 콜백
    class NetworkCallback implements Callback{
       @Override
       // 네트워크 통신에 오류가 발생되면 호출되는 메서드
       public void onFailure(@NotNull Call call, @NotNull IOException e) {
           
       }
       @Override
       // 응답결과가 정싱적으로 수신되었을 때 호출되는 메서드
       public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
            // 응답 결과를 수신한다.
           final String result = response.body().string(); // 여기서 IOException을 발생시킨다.

           // 화면 갱신을 위해 runOnUiThread를 이용한다.
           runOnUiThread(new Runnable() {
               @Override
               public void run() {
                   text1.setText(result);
               }
           });
       }
   }
}