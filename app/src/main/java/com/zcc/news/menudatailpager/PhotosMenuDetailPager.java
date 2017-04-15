package com.zcc.news.menudatailpager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.callback.AbsCallback;
import com.lzy.okhttputils.callback.StringCallback;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.zcc.news.R;
import com.zcc.news.activity.ShowImageActivity;
import com.zcc.news.base.MenuDetaiBasePager;
import com.zcc.news.domain.NewsPagerBean;
import com.zcc.news.domain.PhotosMenuDetailPagerBean;
import com.zcc.news.utils.BitmapCacheUtil;
import com.zcc.news.utils.CacheUtil;
import com.zcc.news.utils.Constants;
import com.zcc.news.utils.NetCacheUtil;

import org.xutils.common.util.LogUtil;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import okhttp3.Call;
import volley.VolleyManager;

/**
 * Created by Administrator on 2017-02-24.
 */
public class PhotosMenuDetailPager extends MenuDetaiBasePager{

    private NewsPagerBean.DataBean dataBean;
    @ViewInject(R.id.listview)
    private ListView listView;
    @ViewInject(R.id.gridview)
    private GridView gridView;
    private String url;
    private List<PhotosMenuDetailPagerBean.DataEntity.NewsEntity> news;
    private PhotosMenuDetailPageAdapter adapter;
    private boolean isSwitchListView = true;
    private BitmapCacheUtil bitmapCacheUtil;
    private Handler handler =new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case NetCacheUtil.SUCCESS:
                    int position = msg.arg1;
                    Bitmap bitmap = (Bitmap) msg.obj;
                    ImageView imageView =null;
                    if(listView.isShown()){
                        imageView = (ImageView) listView.findViewWithTag(position);
                    }else if(gridView.isShown()){
                        imageView = (ImageView) gridView.findViewWithTag(position);
                    }
                    if(imageView!=null && bitmap!=null){
                        imageView.setImageBitmap(bitmap);
                    }
                    break;
                case NetCacheUtil.FAIL:

                    break;
            }
        }
    };
    public PhotosMenuDetailPager(Context context, NewsPagerBean.DataBean dataBean) {
        super(context);
        this.dataBean = dataBean;
        this.bitmapCacheUtil = new BitmapCacheUtil(handler);
    }

    @Override
    public View initView() {
        View view = View.inflate(context, R.layout.photos_pager, null);
        x.view().inject(PhotosMenuDetailPager.this,view);
        listView.setOnItemClickListener(new MyOnItemclickListener());
        return view;
    }

    @Override
    public void initData() {
        super.initData();
        url = Constants.BASE_URL+dataBean.getUrl();
        String saveJson = CacheUtil.getString(context,url);
        if(!TextUtils.isEmpty(saveJson)){
            processData(saveJson);
        }
        //使用Volley联网请求数据
       // getDataFromNet();

        //使用OK-http请求数据
        getDataFromNetByOKhttp();
    }

    private void getDataFromNetByOKhttp() {
        OkHttpUtils.get(url).execute(new StringCallback() {
            @Override
            public void onResponse(boolean isFromCache, String result, okhttp3.Request request, @Nullable okhttp3.Response response) {
                LogUtil.e("使用okhttputils联网成功");
                CacheUtil.putString(context, url, result);
                processData(result);
            }
        });
    }

    private void getDataFromNet() {
        StringRequest stringRequest = new StringRequest(StringRequest.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String result) {
                LogUtil.e("使用Volley联网成功");
                CacheUtil.putString(context, url, result);
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

    private void processData(String json) {
        isSwitchListView = true;
        PhotosMenuDetailPagerBean bean = parsedJson(json);
        news = bean.getData().getNews();
        adapter = new PhotosMenuDetailPageAdapter();
        listView.setAdapter(adapter);
    }

    public void switchListViewAndGridView(ImageButton ib_swich_list_grid) {

        if (isSwitchListView){
            //显示GridView,隐藏ListView
            isSwitchListView = false;
            adapter = new PhotosMenuDetailPageAdapter();
            gridView.setVisibility(View.VISIBLE);
            gridView.setAdapter(adapter);
            listView.setVisibility(View.GONE);
            ib_swich_list_grid.setImageResource(R.drawable.icon_pic_list_type);
        }else {
            //显示ListView,隐藏GridView
            isSwitchListView = true;
            adapter = new PhotosMenuDetailPageAdapter();
            listView.setVisibility(View.VISIBLE);
            listView.setAdapter(adapter);
            gridView.setVisibility(View.GONE);
            ib_swich_list_grid.setImageResource(R.drawable.icon_pic_grid_type);
        }
    }

    class PhotosMenuDetailPageAdapter extends BaseAdapter{
        private DisplayImageOptions options;
        PhotosMenuDetailPageAdapter(){
            options = new DisplayImageOptions.Builder()
                    .showImageOnLoading(R.drawable.home_scroll_default)
                    .showImageForEmptyUri(R.drawable.home_scroll_default)
                    .showImageOnFail(R.drawable.home_scroll_default)
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .considerExifParams(true)
                    .bitmapConfig(Bitmap.Config.RGB_565)
                    .displayer(new RoundedBitmapDisplayer(20))
                    .build();
        }
        @Override
        public int getCount() {
            return news.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if(convertView==null){
                convertView = View.inflate(context,R.layout.item_photos_detail,null);
                viewHolder = new ViewHolder();
                viewHolder.iv_icon = (ImageView) convertView.findViewById(R.id.iv_icon);
                viewHolder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
                convertView.setTag(viewHolder);
            }else{
                viewHolder = (ViewHolder) convertView.getTag();
            }
            //使用自定义三级缓存请求图片需要ImageView设置Tag
            //viewHolder.iv_icon.setTag(position);
            viewHolder.tv_title.setText(news.get(position).getTitle());
            String imageUrl = Constants.BASE_URL+news.get(position).getSmallimage();
            //Volley的ImageLoader请求图片
            //loaderImager(viewHolder,imageUrl);
            //自定义三级缓存请求图片
//            Bitmap bitmap = bitmapCacheUtil.getBitmap(imageUrl, position);
//            if(bitmap != null){
//                viewHolder.iv_icon.setImageBitmap(bitmap);
//            }
            //使用Image-Loader 请求图片
            com.nostra13.universalimageloader.core.ImageLoader.getInstance().displayImage(imageUrl, viewHolder.iv_icon, options);
            return convertView;
        }
    }
    static class ViewHolder{
        ImageView iv_icon;
        TextView tv_title;
    }
    private PhotosMenuDetailPagerBean parsedJson(String json) {
        return new Gson().fromJson(json, PhotosMenuDetailPagerBean.class);
    }

    /**
     *
     * @param viewHolder
     * @param imageurl
     */
    private void loaderImager(final ViewHolder viewHolder, String imageurl) {

        //设置tag
        viewHolder.iv_icon.setTag(imageurl);
        ImageLoader.ImageListener imageListener = new ImageLoader.ImageListener() {
            @Override
            public void onResponse(ImageLoader.ImageContainer imageContainer, boolean b) {
                if (imageContainer!=null) {
                    if (viewHolder.iv_icon!=null) {
                        if (imageContainer.getBitmap()!=null){
                            viewHolder.iv_icon.setImageBitmap(imageContainer.getBitmap());
                        }else {
                            viewHolder.iv_icon.setImageResource(R.drawable.home_scroll_default);
                        }
                    }
                }
            }

            @Override
            public void onErrorResponse(VolleyError volleyError) {
                viewHolder.iv_icon.setImageResource(R.drawable.home_scroll_default);
            }
        };
        VolleyManager.getImageLoader().get(imageurl,imageListener);
    }


     class MyOnItemclickListener implements android.widget.AdapterView.OnItemClickListener {
         @Override
         public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
             String imageUrl = Constants.BASE_URL+news.get(position).getSmallimage();
             Intent intent = new Intent(context, ShowImageActivity.class);
             intent.putExtra("imageUrl",imageUrl);
             context.startActivity(intent);
         }
     }
}
