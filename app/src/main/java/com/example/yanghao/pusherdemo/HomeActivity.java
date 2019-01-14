package com.example.yanghao.pusherdemo;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;

public class HomeActivity extends Activity {

    private SurfaceView mSvVideo;
    private LivePusher livePusher;
    private final String URL = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mSvVideo = findViewById(R.id.sv_video);

        Button button = findViewById(R.id.btn_start_video);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startLive(view);
            }
        });
        Button btn = findViewById(R.id.btn_switch_video);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkCamera();
            }
        });

        getPermission();
    }

    private void startLive(View view){
        Button button = (Button) view;
        if (button.getText().toString().equals("开始直播")){
            livePusher.startPusher(URL);
            button.setText("停止直播");
        }else {
            livePusher.stopPusher();
            button.setText("开始直播");
        }
    }

    public void checkCamera(){
        livePusher.switchCamera();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case 100:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //相机预览图像
                    getPermission();
                } else {
                    // 没有获取到权限，做特殊处理

                }
                break;
        }
    }

    private void getPermission(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.INTERNET)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            // 申请一个（或多个）权限，并提供用于回调返回的获取码（用户定义）
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA, Manifest.permission.INTERNET, Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.RECORD_AUDIO}, 100);
        }else{
            //相机预览图像
            livePusher = new LivePusher(mSvVideo.getHolder());
        }
    }
}
