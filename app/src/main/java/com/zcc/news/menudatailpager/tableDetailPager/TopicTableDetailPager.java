package com.zcc.news.menudatailpager.tableDetailPager;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.zcc.news.R;
import com.zcc.news.base.MenuDetaiBasePager;
import com.zcc.news.domain.NewsPagerBean;
import com.zcc.news.domain.TableDetailPagerBean;
import com.zcc.news.utils.CacheUtil;
import com.zcc.news.utils.Constants;

import org.xutils.common.Callback;
import org.xutils.common.util.DensityUtil;
import org.xutils.http.RequestParams;
import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.util.List;

/**
 * Created by Administrator on 2017-02-25.
 */
public class TopicTableDetailPager extends MenuDetaiBasePager {
    private  ImageOptions imageOptions;
    private ViewPager viewPager;
    private TextView tv_title;
    private LinearLayout ll_point_group;
    private PullToRefreshListView pullToRefreshListView;
    private  String url;
    private NewsPagerBean.DataBean.ChildrenData childrenData;
    private List<TableDetailPagerBean.DataBean.TopnewsBean> topnews;
    private int prePosition;
    private List<TableDetailPagerBean.DataBean.NewsBean> news;
    private boolean isLoadMore = false;
    private String moreUrl;
    private TableDetailPagerBaseAdapter tableDetailPagerBaseAdapter;
    private ListView listView;

    public TopicTableDetailPager(Context context, NewsPagerBean.DataBean.ChildrenData childrenData) {
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
        View view = View.inflate(context, R.layout.topictabledetail_pager, null);
        pullToRefreshListView = (PullToRefreshListView ) view.findViewById(R.id.pull_refresh_list);
        View topnewsView = View.inflate(context, R.layout.topictopnews, null);
        viewPager = (ViewPager) topnewsView.findViewById(R.id.viewpager);
        tv_title = (TextView) topnewsView.findViewById(R.id.tv_title);
        ll_point_group = (LinearLayout) topnewsView.findViewById(R.id.ll_point_group);
        listView = pullToRefreshListView.getRefreshableView();
        listView.addHeaderView(topnewsView);
        pullToRefreshListView.setOnRefreshListener(new mOnRefreshListener2());
        return view;
    }
    class mOnRefreshListener2 implements PullToRefreshBase.OnRefreshListener2 {

        @Override
        public void onPullDownToRefresh(PullToRefreshBase refreshView) {
            getDataFromNet();
        }

        @Override
        public void onPullUpToRefresh(PullToRefreshBase refreshView) {
            if(TextUtils.isEmpty(moreUrl)){
                 Toast.makeText(context,"没有更多了",Toast.LENGTH_SHORT).show();
             }else{
                 getMoreDataFromNet();
             }
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
                pullToRefreshListView.onRefreshComplete();
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                pullToRefreshListView.onRefreshComplete();
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
            pullToRefreshListView.setAdapter(tableDetailPagerBaseAdapter);
        }else{
            isLoadMore = false;
            List<TableDetailPagerBean.DataBean.NewsBean> moreNews = tableDetailPagerBean.getData().getNews();
            news.addAll(moreNews);
            tableDetailPagerBaseAdapter.notifyDataSetChanged();
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
            x.image().bind(viewHolder.iv_icon,imageUrl);
//            Glide.with(context)
//                    .load(imageUrl)
//                    .diskCacheStrategy(DiskCacheStrategy.ALL)
//                    .placeholder(R.drawable.news_pic_default)
//                    .error(R.drawable.news_pic_default)
//                    .into(viewHolder.iv_icon);
            viewHolder.tv_title.setText(newsData.getTitle());
            viewHolder.tv_time.setText(newsData.getPubdate());
            return convertView;
        }
        class  ViewHolder{
            ImageView iv_icon;
            TextView tv_title;
            TextView tv_time;
        }
    }

//     class MyOnRefreshListener implements RefreshListView.OnRefreshListener {
//         @Override
//         public void onPullDownRefresh() {
//             getDataFromNet();
//         }
//
//         @Override
//         public void onLoadMore() {
//             isLoadMore = true;
//             if(TextUtils.isEmpty(moreUrl)){
//                 Toast.makeText(context,"没有更多了",Toast.LENGTH_SHORT).show();
//             }else{
//                 getMoreDataFromNet();
//             }
//         }
//     }

    private void getMoreDataFromNet() {
        RequestParams param = new RequestParams(moreUrl);
        param.setConnectTimeout(4000);
        x.http().get(param, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                isLoadMore = true;
                pocessData(result);
                pullToRefreshListView.onRefreshComplete();
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                pullToRefreshListView.onRefreshComplete();
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
