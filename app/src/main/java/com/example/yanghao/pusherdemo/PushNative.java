package com.example.yanghao.pusherdemo;

public class PushNative {


    public native void startPush(String url);

    public native void stopPush();

    public native void release();

    /**
     *  设置视频参数
     * @param height
     * @param width
     * @param bitrate
     * @param fps
     */
    public native void setVideoOptions(int height,int width,int bitrate,int fps);

    /**
     *  设置音频参数
     * @param sampleRateInHz
     * @param channel
     */
    public native void setAudioOptions(int sampleRateInHz,int channel);
    /**
     * 视频推流
     */
    public native void fireVideo(byte[] data);

    /**
     * 音频推流
     */
    public native void fireAudio(byte[] data,int len);
}
