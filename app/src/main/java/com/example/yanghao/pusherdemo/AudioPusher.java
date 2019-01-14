package com.example.yanghao.pusherdemo;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

public class AudioPusher extends Pusher{

    private static final String TAG = AudioPusher.class.getSimpleName();
    private AudioParams mAudioParams;
    private AudioRecord mAudioRecord;
    private boolean isPushing = false;
    private int minBufferSize;
    private PushNative mPushNative;
    public AudioPusher(AudioParams mAudioParams, PushNative pushNative) {
        this.mAudioParams = mAudioParams;
        this.mPushNative = pushNative;
        int channelConfig = mAudioParams.getChannel() == 1 ? AudioFormat.CHANNEL_IN_MONO : AudioFormat.CHANNEL_IN_STEREO;
        minBufferSize = AudioRecord.getMinBufferSize(mAudioParams.getSampleRateInHz(), channelConfig, AudioFormat.ENCODING_PCM_16BIT);
        mAudioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,mAudioParams.getSampleRateInHz()
                ,channelConfig,AudioFormat.ENCODING_PCM_16BIT,minBufferSize);
    }

    @Override
    public void startPusher() {
        isPushing = true;
        new Thread(new AudioRecordTask()).start();
    }

    @Override
    public void stopPusher() {
        isPushing = false;
        mAudioRecord.stop();
    }

    @Override
    public void release() {
        if (mAudioRecord!=null){
            mAudioRecord.release();
            mAudioRecord = null;
        }
    }

    class AudioRecordTask implements Runnable{

        @Override
        public void run() {
            while (isPushing){
                byte[] buffer = new byte[minBufferSize];
                int read = mAudioRecord.read(buffer, 0, buffer.length);
                if (read>0){
                    mPushNative.fireAudio(buffer, read);
                }
            }
        }
    }
}
