package com.zcc.news.pager;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.zcc.news.activity.MainActivity;
import com.zcc.news.base.BasePager;
import com.zcc.news.base.MenuDetaiBasePager;
import com.zcc.news.domain.NewsPagerBean;
import com.zcc.news.fragment.LeftMenuFragment;
import com.zcc.news.menudatailpager.InteracMenuDetailPager;
import com.zcc.news.menudatailpager.NewsMenuDetailPager;
import com.zcc.news.menudatailpager.PhotosMenuDetailPager;
import com.zcc.news.menudatailpager.TopicMenuDetailPager;
import com.zcc.news.utils.CacheUtil;
import com.zcc.news.utils.Constants;

import org.xutils.common.Callback;
import org.xutils.common.util.LogUtil;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import volley.VolleyManager;

/**
 * Created by Administrator on 2017-02-21.
 */
public class NewsCenterPager extends BasePager{
    private List<NewsPagerBean.DataBean> data;
    private ArrayList<MenuDetaiBasePager> menuDetaiBasePagers;
    public NewsCenterPager(Context context) {
        super(context);
    }

    @Override
    public void initData() {
        super.initData();
        LogUtil.e("新闻页面被初始化");
        ib_menu.setVisibility(View.VISIBLE);
        ib_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity mainActivity = (MainActivity) context;
                mainActivity.getSlidingMenu().toggle();
            }
        });
        String saveJson = CacheUtil.getString(context,Constants.NEWS_PAGER_URL);
        if(!TextUtils.isEmpty(saveJson)){
            processData(saveJson);
        }
        //getDataFromNet();
        getDataFromNetByVolley();
    }

    private void getDataFromNetByVolley() {
        //RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest stringRequest = new StringRequest(StringRequest.Method.GET, Constants.NEWS_PAGER_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String result) {
                LogUtil.e("使用Volley联网成功" );
                CacheUtil.putString(context,Constants.NEWS_PAGER_URL,result);
                processData(result);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                LogUtil.e("使用Volley联网失败" );
            }
        }){
            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                String parsed;
                try {

                    parsed = new String(response.data,"utf-8");
                    return Response.success(parsed, HttpHeaderParser.parseCacheHeaders(response));
                } catch (UnsupportedEncodingException var4) {
                   var4.printStackTrace();
                }


                return super.parseNetworkResponse(response);
            }
        };
        VolleyManager.getRequestQueue().add(stringRequest);
    }

    private void getDataFromNet() {
        RequestParams param = new RequestParams(Constants.NEWS_PAGER_URL);
        x.http().get(param, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
                CacheUtil.putString(context,Constants.NEWS_PAGER_URL,result);
                processData(result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                LogUtil.e("使用xUtil3联网失败" + ex.getMessage());
            }

            @Override
            public void onCancelled(CancelledException cex) {
                LogUtil.e("使用xUtil3联网取消" + cex.getMessage());
            }

            @Override
            public void onFinished() {
                LogUtil.e("使用xUtil3联网完成");
            }
        });
    }

    private void processData(String result) {
        NewsPagerBean newsPagerBean = parsedJson(result);
        MainActivity mainActivity = (MainActivity)context;
        LeftMenuFragment leftMenuFragment=mainActivity.getLeftMenuFragment();
        data = newsPagerBean.getData();
        //给左侧菜单传递数据
        menuDetaiBasePagers = new ArrayList<MenuDetaiBasePager>();
        menuDetaiBasePagers.add(new NewsMenuDetailPager(context,data.get(0)));
        menuDetaiBasePagers.add(new TopicMenuDetailPager(context,data.get(0)));
        menuDetaiBasePagers.add(new PhotosMenuDetailPager(context,data.get(2)));
        menuDetaiBasePagers.add(new InteracMenuDetailPager(context));
        leftMenuFragment.setData(data);
    }

    private NewsPagerBean parsedJson(String result) {
        return new Gson().fromJson(result,NewsPagerBean.class);
    }

    public void swicthPager(int position) {
        MenuDetaiBasePager  menuDetaiBasePager= menuDetaiBasePagers.get(position);
        View rootView = menuDetaiBasePager.rootView;
        tv_title.setText(data.get(position).getTitle());
        menuDetaiBasePager.initData();
        fl_content.removeAllViews();
        fl_content.addView(rootView);
        if (position == 2){
            ib_swich_list_grid.setVisibility(View.VISIBLE);
            ib_swich_list_grid.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PhotosMenuDetailPager photosMenuDetailPager = (PhotosMenuDetailPager) menuDetaiBasePagers.get(2);
                    photosMenuDetailPager.switchListViewAndGridView(ib_swich_list_grid);
                }
            });

        }else {
            ib_swich_list_grid.setVisibility(View.GONE);
        }
    }
}
