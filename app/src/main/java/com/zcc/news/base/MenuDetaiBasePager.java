package com.zcc.news.base;

import android.content.Context;
        import android.view.View;

/**
 * Created by Administrator on 2017-02-24.
 */
public abstract class MenuDetaiBasePager {
    public final Context context;
    public View rootView;
    public MenuDetaiBasePager(Context context){
        this.context = context;
        this.rootView = initView();
    }
    public abstract View initView();
    public  void initData(){};
}
