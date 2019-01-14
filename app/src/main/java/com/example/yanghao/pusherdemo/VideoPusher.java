package com.example.yanghao.pusherdemo;

import android.hardware.Camera;
import android.view.SurfaceHolder;

import java.io.IOException;
import java.nio.Buffer;
import java.util.IllegalFormatCodePointException;

public class VideoPusher extends Pusher implements SurfaceHolder.Callback, Camera.PreviewCallback {

    private SurfaceHolder mSurfaceHolder;
    private byte[] buffers;
    private Camera mCamera;
    private VideoParams mVideoParams;
    private boolean isPushing = false;
    private PushNative mPushNative;
    public VideoPusher(SurfaceHolder surfaceHolder, VideoParams videoParams, PushNative pushNative) {
        this.mSurfaceHolder = surfaceHolder;
        this.mVideoParams = videoParams;
        this.mPushNative = pushNative;
        surfaceHolder.addCallback(this);
    }

    @Override
    public void startPusher() {
        isPushing = true;
    }

    @Override
    public void stopPusher() {
        isPushing = false;
    }

    @Override
    public void release() {
        stopPreview();
    }

    private void startPreView(){
        //surfaceView初始化完成，开始进行相机预览
        try {
            mCamera = Camera.open(mVideoParams.getCameraId());

            //取出预览数据
            buffers = new byte[mVideoParams.getHeight()*mVideoParams.getWidth()*4];
            mCamera.addCallbackBuffer(buffers);
            mCamera.setPreviewCallbackWithBuffer(this);


            mCamera.setPreviewDisplay(mSurfaceHolder);
            mCamera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        startPreView();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

    }

    private void stopPreview() {
        if (mCamera!=null){
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    public void switchCamera(){
        if (mVideoParams.getCameraId() == Camera.CameraInfo.CAMERA_FACING_BACK){
            mVideoParams.setCameraId(Camera.CameraInfo.CAMERA_FACING_FRONT);
        }else {
            mVideoParams.setCameraId(Camera.CameraInfo.CAMERA_FACING_BACK);
        }

        stopPreview();
        startPreView();
    }

    @Override
    public void onPreviewFrame(byte[] bytes, Camera camera) {
        if (mCamera!=null) {
            mCamera.addCallbackBuffer(buffers);
        }
        if (isPushing){
            mPushNative.fireVideo(bytes );
        }
    }
}
