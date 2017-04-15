package com.zcc.news.menudatailpager;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.zcc.news.base.MenuDetaiBasePager;

/**
 * Created by Administrator on 2017-02-24.
 */
public class InteracMenuDetailPager extends MenuDetaiBasePager{
    private TextView textView;

    public InteracMenuDetailPager(Context context) {
        super(context);
    }

    @Override
    public View initView() {
        textView=new TextView(context);
        textView.setTextColor(Color.RED);
        textView.setTextSize(30);
        textView.setGravity(Gravity.CENTER);
        return textView;
    }

    @Override
    public void initData() {
        super.initData();
        textView.setText("我是互动详情面");
    }
}
