package com.zcc.news.menudatailpager.tableDetailPager;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.zcc.news.R;
import com.zcc.news.activity.NewsDetailActivity;
import com.zcc.news.base.MenuDetaiBasePager;
import com.zcc.news.domain.NewsPagerBean;
import com.zcc.news.domain.TableDetailPagerBean;
import com.zcc.news.utils.CacheUtil;
import com.zcc.news.utils.Constants;
import com.zcc.news.view.RefreshListView;

import org.xutils.common.Callback;
import org.xutils.common.util.DensityUtil;
import org.xutils.common.util.LogUtil;
import org.xutils.http.RequestParams;
import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.util.List;

/**
 * Created by Administrator on 2017-02-25.
 */
public class TableDetailPager extends MenuDetaiBasePager {
    public static final String READ_ARRAY_ID = "read_array_id";
    private  ImageOptions imageOptions;
    private ViewPager viewPager;
    private TextView tv_title;
    private LinearLayout ll_point_group;
    private RefreshListView listview;
    private  String url;
    private NewsPagerBean.DataBean.ChildrenData childrenData;
    private List<TableDetailPagerBean.DataBean.TopnewsBean> topnews;
    private int prePosition;
    private List<TableDetailPagerBean.DataBean.NewsBean> news;
    private boolean isLoadMore = false;
    private String moreUrl;
    private TableDetailPagerBaseAdapter tableDetailPagerBaseAdapter;
    private MHandler mHandler;
    private boolean isDragging;

    public TableDetailPager(Context context,NewsPagerBean.DataBean.ChildrenData childrenData) {
        super(context);
        this.childrenData = childrenData;
        imageOptions = new ImageOptions.Builder()
                .setSize(DensityUtil.dip2px(100), DensityUtil.dip2px(100))
                .setRadius(DensityUtil.dip2px(5))
                        // 如果ImageView的大小不是定义为wrap_content, 不要crop.
                .setCrop(true) // 很多时候设置了合适的scaleType也不需要它.
                        // 加载中或错误图片的ScaleType
                        //.setPlaceholderScaleType(ImageView.ScaleType.MATRIX)
                .setImageScaleType(ImageView.ScaleType.CENTER_CROP)
                .setLoadingDrawableId(R.drawable.home_scroll_default)
                .setFailureDrawableId(R.drawable.home_scroll_default)
                .build();
    }

    @Override
    public View initView() {
        View view = View.inflate(context, R.layout.tabledetail_pager, null);
        listview = (RefreshListView ) view.findViewById(R.id.listview);
        View topnewsView = View.inflate(context, R.layout.topnews, null);
        viewPager = (ViewPager) topnewsView.findViewById(R.id.viewpager);
        tv_title = (TextView) topnewsView.findViewById(R.id.tv_title);
        ll_point_group = (LinearLayout) topnewsView.findViewById(R.id.ll_point_group);
        listview.addTopNewsView(topnewsView);
        listview.setOnRefreshListener(new MyOnRefreshListener());
        listview.setOnItemClickListener(new mOnItemClickListener());
        return view;
    }
    class  mOnItemClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            int relPosition = position-1;
            TableDetailPagerBean.DataBean.NewsBean newsBean = news.get(relPosition);
            Toast.makeText(context,newsBean.getId()+","+newsBean.getTitle(),Toast.LENGTH_SHORT).show();
            String idArray = CacheUtil.getString(context, READ_ARRAY_ID);
            if(!idArray.contains(newsBean.getId()+"")){
                idArray = idArray+newsBean.getId()+",";
                CacheUtil.putString(context,READ_ARRAY_ID,idArray);
                tableDetailPagerBaseAdapter.notifyDataSetChanged();
            }
            Intent intent = new Intent(context, NewsDetailActivity.class);
            intent.putExtra("url",Constants.BASE_URL+newsBean.getUrl());
            context.startActivity(intent);
        }
    }
    @Override
    public void initData() {
        super.initData();
        this.url = Constants.BASE_URL+childrenData.getUrl();
        String saveJson = CacheUtil.getString(context, url);
        if(!TextUtils.isEmpty(saveJson)){
            pocessData(saveJson);
        }
        getDataFromNet();

    }

    private void getDataFromNet() {
        RequestParams param = new RequestParams(url);
        param.setConnectTimeout(4000);
        x.http().get(param, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                CacheUtil.putString(context, url, result);
                pocessData(result);
                listview.onFinishRefresh(true);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                listview.onFinishRefresh(false);
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    private void pocessData(String result) {
        TableDetailPagerBean tableDetailPagerBean = parsedJson(result);

        news  = tableDetailPagerBean.getData().getNews();
        moreUrl ="";
        if(TextUtils.isEmpty(tableDetailPagerBean.getData().getMore())){
            moreUrl="";
        }else{
            moreUrl =Constants.BASE_URL+tableDetailPagerBean.getData().getMore();
        }

        if(!isLoadMore){
            topnews = tableDetailPagerBean.getData().getTopnews();
            //不是上拉刷新加载更多
            viewPager.setAdapter(new TableDetailPagerPagerAdapter());
            createPoint();
            viewPager.addOnPageChangeListener(new TableDetailPagerOnPageChangeListener());
            tv_title.setText(topnews.get(0).getTitle());
            ll_point_group.getChildAt(0).setEnabled(true);
            tableDetailPagerBaseAdapter = new TableDetailPagerBaseAdapter();
            listview.setAdapter(tableDetailPagerBaseAdapter);
            if(mHandler==null){
                mHandler = new MHandler();
            }
            mHandler.removeCallbacksAndMessages(null);
            mHandler.postDelayed(new MRunnable(),3000);
        }else{
            isLoadMore = false;
            List<TableDetailPagerBean.DataBean.NewsBean> moreNews = tableDetailPagerBean.getData().getNews();
            news.addAll(moreNews);
            tableDetailPagerBaseAdapter.notifyDataSetChanged();
        }

    }
    class MHandler extends  Handler{
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int item = (viewPager.getCurrentItem()+1)%topnews.size();
            viewPager.setCurrentItem(item);
            mHandler.postDelayed(new MRunnable(), 3000);
        }
    }
    class MRunnable implements Runnable{

        @Override
        public void run() {
            mHandler.sendEmptyMessage(0);
        }
    }
    private void createPoint() {
        ll_point_group.removeAllViews();//移除所有的红点
        for (int i = 0; i < topnews.size(); i++) {

            ImageView imageView = new ImageView(context);
            //设置背景选择器
            imageView.setBackgroundResource(R.drawable.point_selector);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(DensityUtil.dip2px(5),DensityUtil.dip2px(5));
            imageView.setEnabled(false);
            if(i!=0){
                params.leftMargin = DensityUtil.dip2px(8);
            }


            imageView.setLayoutParams(params);

            ll_point_group.addView(imageView);
        }
    }

    class  TableDetailPagerPagerAdapter extends PagerAdapter{
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageView imageView =new ImageView(context);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            String url = topnews.get(position).getTopimage();
            x.image().bind(imageView, Constants.BASE_URL + url,imageOptions);
            container.addView(imageView);
            imageView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()){
                        case MotionEvent.ACTION_DOWN://按下
                            LogUtil.e("按下");
                            //是把消息队列所有的消息和回调移除
                            mHandler.removeCallbacksAndMessages(null);
                            break;
                        case MotionEvent.ACTION_UP://离开
                            LogUtil.e("离开");
                            //是把消息队列所有的消息和回调移除
                            mHandler.removeCallbacksAndMessages(null);
                            mHandler.postDelayed(new MRunnable(), 3000);
                            break;
                    }
                    return true;
                }
            });
//            x.image().loadDrawable(Constants.BASE_URL + url, imageOptions, new Callback.CommonCallback<Drawable>() {
//                @Override
//                public void onSuccess(Drawable result) {
//                    LogUtil.e("调取图片成功啦--->"+result);
//                    imageView.setImageDrawable(result);
//                }
//
//                @Override
//                public void onError(Throwable ex, boolean isOnCallback) {
//                    LogUtil.e("调取图片失败--->"+ex.getMessage());
//                    imageView.setBackgroundResource(R.drawable.news_pic_default);
//                }
//
//                @Override
//                public void onCancelled(CancelledException cex) {
//                    imageView.setBackgroundResource(R.drawable.news_pic_default);
//                }
//
//                @Override
//                public void onFinished() {
//
//                }
//            });
            return imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getCount() {
            return topnews.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view==object;
        }
    }
    private TableDetailPagerBean parsedJson(String result) {
        return new Gson().fromJson(result,TableDetailPagerBean.class);
    }

     class TableDetailPagerOnPageChangeListener implements ViewPager.OnPageChangeListener {
         @Override
         public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

         }
         @Override
         public void onPageSelected(int position) {
             tv_title.setText(topnews.get(position).getTitle());
             for(int i=0;i<ll_point_group.getChildCount();i++){
                 ll_point_group.getChildAt(i).setEnabled(false);
             }
             ll_point_group.getChildAt(position).setEnabled(true);
         }
         @Override
         public void onPageScrollStateChanged(int state) {
             if(state ==ViewPager.SCROLL_STATE_DRAGGING){//拖拽
                 isDragging = true;
                 LogUtil.e("拖拽");
                 //拖拽要移除消息
                 mHandler.removeCallbacksAndMessages(null);
             }else if(state ==ViewPager.SCROLL_STATE_SETTLING&&isDragging){//惯性
                 //发消息
                 LogUtil.e("惯性");
                 isDragging = false;
                 mHandler.removeCallbacksAndMessages(null);
                 mHandler.postDelayed(new MRunnable(),3000);

             }else if(state ==ViewPager.SCROLL_STATE_IDLE&&isDragging){//静止状态
                 //发消息
                 LogUtil.e("静止状态");
                 isDragging = false;
                 mHandler.removeCallbacksAndMessages(null);
                 mHandler.postDelayed(new MRunnable(),3000);
             }

         }
     }

    class TableDetailPagerBaseAdapter extends BaseAdapter {
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
                convertView = View.inflate(context,R.layout.listitem_detail_pager,null);
                viewHolder = new ViewHolder();
                viewHolder.iv_icon = (ImageView) convertView.findViewById(R.id.iv_icon);
                viewHolder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
                viewHolder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
                convertView.setTag(viewHolder);
            }else{
                viewHolder = (ViewHolder) convertView.getTag();
            }
            TableDetailPagerBean.DataBean.NewsBean newsData = news.get(position);
            String imageUrl = Constants.BASE_URL+newsData.getListimage();
            //x.image().bind(viewHolder.iv_icon,imageUrl);
            Glide.with(context)
                    .load(imageUrl)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.news_pic_default)
                    .error(R.drawable.news_pic_default)
                    .into(viewHolder.iv_icon);
            viewHolder.tv_title.setText(newsData.getTitle());
            viewHolder.tv_time.setText(newsData.getPubdate());
            String array = CacheUtil.getString(context,READ_ARRAY_ID);
            if(array.contains(newsData.getId()+"")){
                viewHolder.tv_title.setTextColor(Color.GRAY);
            }else{
                viewHolder.tv_title.setTextColor(Color.BLACK);
            }
            return convertView;
        }
        class  ViewHolder{
            ImageView iv_icon;
            TextView tv_title;
            TextView tv_time;
        }
    }

     class MyOnRefreshListener implements RefreshListView.OnRefreshListener {
         @Override
         public void onPullDownRefresh() {
             getDataFromNet();
         }

         @Override
         public void onLoadMore() {
             isLoadMore = true;
             if(TextUtils.isEmpty(moreUrl)){
                 Toast.makeText(context,"没有更多了",Toast.LENGTH_SHORT).show();
             }else{
                 getMoreDataFromNet();
             }
         }
     }

    private void getMoreDataFromNet() {
        RequestParams param = new RequestParams(moreUrl);
        param.setConnectTimeout(4000);
        x.http().get(param, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                listview.onFinishRefresh(false);
                isLoadMore = true;
                pocessData(result);

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                listview.onFinishRefresh(false);
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }
}
