package com.zcc.news.base;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.zcc.news.R;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

/**
 * Created by Administrator on 2017-02-20.
 */
public class BasePager {
    public final Context context;

    public TextView tv_title;
    public ImageButton ib_menu;
    public FrameLayout fl_content;
    public View rootView;
    public ImageButton ib_swich_list_grid;
    public BasePager(Context context){
        this.context=context;
        rootView = initView();
    }

    private View initView() {
        View view =View.inflate(context,R.layout.base_pager,null);
        tv_title = (TextView) view.findViewById(R.id.tv_title);
        ib_menu = (ImageButton) view.findViewById(R.id.ib_menu);
        fl_content = (FrameLayout) view.findViewById(R.id.fl_content);
        ib_swich_list_grid = (ImageButton) view.findViewById(R.id.ib_swich_list_grid);
        return view;
    }
    public void initData(){}
}
