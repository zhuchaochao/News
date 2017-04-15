package com.zcc.news.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by Administrator on 2017-02-26.
 */
public class HorizontalScrollViewPager extends ViewPager {
    private float startX;
    private float startY;
    public HorizontalScrollViewPager(Context context) {
        super(context);
    }

    public HorizontalScrollViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                startX=ev.getX();
                startY=ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                float endX = ev.getX();
                float endY = ev.getY();
                float distanceX = endX - startX;
                float distanceY = endY - startY;
                if(Math.abs(distanceX)>Math.abs(distanceY)){
                    //水平方向
                    //当滑到ViewPager第一个页面的时候，并且是从左往右滑
                    if(getCurrentItem()==0&&distanceX>0){
                        getParent().requestDisallowInterceptTouchEvent(false);
                    }else if (getCurrentItem()==(getAdapter().getCount()-1)&&distanceX<0){
                        getParent().requestDisallowInterceptTouchEvent(false);
                    }else{
                        getParent().requestDisallowInterceptTouchEvent(true);
                    }
                }else{
                    //垂直方向
                    //要求父视图不拦截触摸事件
                    getParent().requestDisallowInterceptTouchEvent(false);
                }
                break;
            case MotionEvent.ACTION_UP:
                break;
        }

        return super.dispatchTouchEvent(ev);
    }
}
