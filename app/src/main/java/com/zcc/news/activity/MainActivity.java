package com.zcc.news.activity;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Window;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;
import com.zcc.news.R;
import com.zcc.news.fragment.ContentFragment;
import com.zcc.news.fragment.LeftMenuFragment;
import com.zcc.news.utils.DensityUtil;


public class MainActivity extends SlidingFragmentActivity {
    public static final String MAIN_CONTENT_TAG="main_content_tag";
    public static final String LEFTMENU_TAG="leftmenu_tag";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        initSlidingMenu();
        initFragment();
    }

    private void initFragment() {
        android.support.v4.app.FragmentManager fm=getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction ft =fm.beginTransaction();
        ft.replace(R.id.fl_main, new ContentFragment(), MAIN_CONTENT_TAG);
        ft.replace(R.id.fl_letfmenu, new LeftMenuFragment(), LEFTMENU_TAG);
        ft.commit();
    }

    private void initSlidingMenu() {
        //设置主页面
        setContentView(R.layout.activity_main);
        //设置左侧菜单
        setBehindContentView(R.layout.activity_leftmenu);
        //设置显示模式 左侧菜单+主页
        SlidingMenu slidingMenu=getSlidingMenu();
        slidingMenu.setMode(SlidingMenu.LEFT);
        //设置滑动模式 全屏滑动
        slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        //设置主页占据的宽度
        slidingMenu.setBehindOffset(DensityUtil.dip2px(MainActivity.this, 200));
    }
    public LeftMenuFragment getLeftMenuFragment(){
        return (LeftMenuFragment) getSupportFragmentManager().findFragmentByTag(LEFTMENU_TAG);
    }

    public ContentFragment getContentFragment() {
        return (ContentFragment) getSupportFragmentManager().findFragmentByTag(MAIN_CONTENT_TAG);
    }
}
