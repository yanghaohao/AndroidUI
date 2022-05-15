package com.example.colortrackview;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    private ColorTrackView colorTrackView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        colorTrackView = findViewById(R.id.tv_color_track);
    }

    public void leftClick(View view) {
        setAnimator(ColorTrackView.Orientation.LEFT2RIGHT);
    }

    public void rightClick(View view) {
        setAnimator(ColorTrackView.Orientation.RIGHT2LEFT);
    }

    public void setAnimator(ColorTrackView.Orientation orientation){
        colorTrackView.setOrientation(orientation);
        ValueAnimator valueAnimator = ObjectAnimator.ofFloat(0,1);
        valueAnimator.setDuration(2000);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float currentProgress = (float) animation.getAnimatedValue();
                colorTrackView.setColorProgress(currentProgress);
            }
        });

        valueAnimator.start();
    }
}