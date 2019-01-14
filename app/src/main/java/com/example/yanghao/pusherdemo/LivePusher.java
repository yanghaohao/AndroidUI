package com.example.yanghao.pusherdemo;

import android.hardware.Camera;
import android.view.SurfaceHolder;

public class LivePusher implements SurfaceHolder.Callback {

    private  SurfaceHolder surfaceHolder;
    private VideoPusher videoPusher;
    private AudioPusher audioPusher;
    private PushNative pushNative;
    public LivePusher(SurfaceHolder holder) {
        this.surfaceHolder = holder;
        holder.addCallback(this);
        prepare();
    }

    /**
     * 初始化推流器
     */
    private void prepare(){
        pushNative = new PushNative();
        //实例化音视频
        VideoParams videoParams = new VideoParams(480,320, Camera.CameraInfo.CAMERA_FACING_BACK);
        videoPusher = new VideoPusher(surfaceHolder,videoParams,pushNative);

        AudioParams audioParams = new AudioParams();
        audioPusher = new AudioPusher(audioParams,pushNative);
    }

    public void switchCamera(){
        videoPusher.switchCamera();
    }

    public void startPusher(String url){
        videoPusher.startPusher();
        audioPusher.startPusher();
        pushNative.startPush(url);
    }

    public void stopPusher(){
        videoPusher.stopPusher();
        audioPusher.stopPusher();
        pushNative.stopPush();
    }

    public void release(){
        videoPusher.release();
        audioPusher.release();
        pushNative.release();
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        stopPusher();
        release();
    }
}
