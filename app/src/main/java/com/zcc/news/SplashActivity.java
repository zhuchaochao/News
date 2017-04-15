package com.zcc.news;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.RelativeLayout;

import com.zcc.news.activity.GuideActivity;
import com.zcc.news.activity.MainActivity;
import com.zcc.news.utils.CacheUtil;

public class SplashActivity extends Activity {
    /**
     * 静态常量
     */
    public static final String START_MAIN = "start_main";
    private  RelativeLayout relativeLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        //渐变动画，缩放动画，旋转动画
        AlphaAnimation alphaAnimation=new AlphaAnimation(0,1);
        //alphaAnimation.setDuration(500);
        alphaAnimation.setFillAfter(true);
        ScaleAnimation scaleAnimation=new ScaleAnimation(0,1,0,1, ScaleAnimation.RELATIVE_TO_SELF,0.5f,ScaleAnimation.RELATIVE_TO_SELF,0.5f);
        //scaleAnimation.setDuration(500);
        scaleAnimation.setFillAfter(true);
        RotateAnimation rotateAnimation=new RotateAnimation(0,360,RotateAnimation.RELATIVE_TO_SELF,0.5f,RotateAnimation.RELATIVE_TO_SELF,0.5f);
        //rotateAnimation.setDuration(500);
        rotateAnimation.setFillAfter(true);
        AnimationSet animationSet=new AnimationSet(false);
        animationSet.addAnimation(alphaAnimation);
        animationSet.addAnimation(scaleAnimation);
        animationSet.addAnimation(rotateAnimation);
        animationSet.setDuration(3000);
        relativeLayout = (RelativeLayout) findViewById(R.id.rl_splash_root);
        relativeLayout.setAnimation(animationSet);
        animationSet.setAnimationListener(new MyAnimationListener());
    }
    class MyAnimationListener implements Animation.AnimationListener {
        @Override
        public void onAnimationStart(Animation animation) {


        }

        @Override
        public void onAnimationEnd(Animation animation) {
            boolean isStartMain= CacheUtil.getBoolean(SplashActivity.this,START_MAIN);
            Intent intent;
            if(isStartMain){
                //如果进入过主页面，直接进入主页面
                intent = new Intent(SplashActivity.this,MainActivity.class);
            }else{
                //没有进入过主页面，进入引导页
                intent = new Intent(SplashActivity.this,GuideActivity.class);
            }
            startActivity(intent);
            finish();
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    }
}
