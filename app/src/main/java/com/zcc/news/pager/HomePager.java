package com.zcc.news.pager;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.zcc.news.base.BasePager;

import org.xutils.common.util.LogUtil;

/**
 * Created by Administrator on 2017-02-21.
 */
public class HomePager extends BasePager{

    public HomePager(Context context) {
        super(context);
    }

    @Override
    public void initData() {
        super.initData();
        LogUtil.e("主页面被初始化");
        tv_title.setText("主页");
        TextView textView=new TextView(context);
        textView.setText("我是主页面");
        textView.setTextColor(Color.RED);
        textView.setTextSize(30);
        textView.setGravity(Gravity.CENTER);
        ib_menu.setVisibility(View.GONE);
        fl_content.addView(textView);
    }
}
