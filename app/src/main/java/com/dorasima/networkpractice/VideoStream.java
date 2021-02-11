package com.dorasima.networkpractice;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.MediaController;
import android.widget.VideoView;

public class VideoStream extends AppCompatActivity {

    VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_stream);

        videoView = (VideoView)findViewById(R.id.videoView);
    }

    public void showVideoBtn(View view){
        // 동영상 재생 컨트롤을 비디오 뷰에 배치한다.
        MediaController controller = new MediaController(this);
        controller.setAnchorView(videoView);

        // 동영상 주소로 Uri 개체를 만든다.
        Uri uri = Uri.parse("http://192.168.35.240:8080/MediaServer/asdf.mp4");
        // 비디오 뷰를 통제할 미디어 컨트롤러를 세팅한다.
        videoView.setMediaController(controller);
        // 재생할 동영상 주소를 세팅한다.
        videoView.setVideoURI(uri);
        // 포커스를 지정하는 것이다.
        videoView.requestFocus();
        // 비디오를 시작한다.
        videoView.start();
    }
}