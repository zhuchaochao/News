package com.zcc.news.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.zcc.news.R;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2017-02-27.
 */
public class RefreshListView extends ListView{
    /**
     * RefreshListView控件
     */
    private LinearLayout heanderView;
    private View ll_refresh_listview;
    private ImageView iv_refresh_arr;
    private ProgressBar pb_refresh_progresbar;
    private TextView tv_statu;
    private TextView tv_time;
    /**
     * ll_refresh_listview的高度
     */
    private int refreshListViewHeight;
    /**
     * 初始Y值
     */
    private float startY = -1;

    /**
     *下拉刷新的状态
     */
    private static final int PULLDOWNREFRESH=0;
    private static final int PULLDOWNREFRESH_RELESE = 1;
    private static final int PULLDOWNREFRESH_REFRESHING = 2;
    /**
     * 当前状态
     */
    private  int currentStatus =PULLDOWNREFRESH;
    /**
     * 箭头动画
     */
    private Animation upAnimation;
    private Animation downAnimation;
    /**
     * 加载更多视图
     */
    private View footerView;

    /**
     * 加载更多视图的高
     */
    private int footerViewHeight;
    /**
     * 是否加载更多
     */
    private boolean isLoadMore = false;
    /**
     * 顶部轮播图
     */
    private View topnewsView;
    private int listViewOnScreen = -1;

    public RefreshListView(Context context) {
        this(context, null);
    }

    public RefreshListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RefreshListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initHeaderView(context);
        initAnimation();
        initFooterView(context);
    }

    private void initFooterView(Context context) {
        footerView = View.inflate(context, R.layout.refreshlistview_footer,null);
        footerView.measure(0, 0);
        footerViewHeight = footerView.getMeasuredHeight();
        footerView.setPadding(0, -footerViewHeight, 0, 0);
        addFooterView(footerView);
        setOnScrollListener(new mOnScrollListener());
    }

    public void addTopNewsView(View topnewsView) {
        if(topnewsView !=null){
            this.topnewsView = topnewsView;
            heanderView.addView(this.topnewsView);
        }
    }

    class mOnScrollListener implements OnScrollListener{

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            if(scrollState==OnScrollListener.SCROLL_STATE_IDLE||scrollState==OnScrollListener.SCROLL_STATE_FLING){
                if(getLastVisiblePosition() == getCount()-1){
                    footerView.setPadding(8,8,8,8);
                    isLoadMore = true;
                    if(mOnRefreshListener !=null){
                        mOnRefreshListener.onLoadMore();
                    }
                }
            }
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

        }
    }
    private void initAnimation() {
        upAnimation = new RotateAnimation(0,-180,RotateAnimation.RELATIVE_TO_SELF,0.5f,RotateAnimation.RELATIVE_TO_SELF,0.5f);
        upAnimation.setDuration(500);
        upAnimation.setFillAfter(true);

        downAnimation = new RotateAnimation(-180,-360,RotateAnimation.RELATIVE_TO_SELF,0.5f,RotateAnimation.RELATIVE_TO_SELF,0.5f);
        downAnimation.setDuration(500);
        downAnimation.setFillAfter(true);
    }

    private void initHeaderView(Context context){
        heanderView = (LinearLayout) View.inflate(context, R.layout.refreshlistview_heander,null);
        ll_refresh_listview = heanderView.findViewById(R.id.ll_refresh_listview);
        iv_refresh_arr = (ImageView) heanderView.findViewById(R.id.iv_refresh_arr);
        pb_refresh_progresbar = (ProgressBar) heanderView.findViewById(R.id.pb_refresh_progresbar);
        tv_statu = (TextView) heanderView.findViewById(R.id.tv_statu);
        tv_time = (TextView) heanderView.findViewById(R.id.tv_time);
        ll_refresh_listview.measure(0, 0);
        refreshListViewHeight = ll_refresh_listview.getMeasuredHeight();
        ll_refresh_listview.setPadding(0, -refreshListViewHeight, 0, 0);
        addHeaderView(heanderView);
    }
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                startY = ev.getY();
                break;

            case MotionEvent.ACTION_MOVE:
                if(startY==-1){
                    startY = ev.getY();
                }
                boolean isDisplayTopNews = isDisplayTopNews();
                if(!isDisplayTopNews){
                    break;
                }
                if(currentStatus == PULLDOWNREFRESH_REFRESHING){
                    break;
                }
                float endY =ev.getY();
                float distanceY = endY - startY;
                if(distanceY>0){
                    int paddingHeight = (int) (-refreshListViewHeight+distanceY);
                    ll_refresh_listview.setPadding(0,paddingHeight,0,0);
                    if(paddingHeight<0&&currentStatus != PULLDOWNREFRESH){
                        //设置为下拉刷新状态
                        currentStatus = PULLDOWNREFRESH;

                        //更新状态
                        refreshViewState();
                    }else if(paddingHeight>0&&currentStatus != PULLDOWNREFRESH_RELESE){
                        //设置为松手刷新状态
                        currentStatus = PULLDOWNREFRESH_RELESE;

                        //更新状态
                        refreshViewState();
                    }
                }
                break;

            case MotionEvent.ACTION_UP:
                startY = -1;
                if(currentStatus == PULLDOWNREFRESH ){
                    //下拉刷新
                    ll_refresh_listview.setPadding(0,-refreshListViewHeight,0,0);
                }else if(currentStatus == PULLDOWNREFRESH_RELESE){
                    //松手刷新
                    //设置为正在刷新状态
                    currentStatus = PULLDOWNREFRESH_REFRESHING;
                    //更新状态
                    refreshViewState();
                    ll_refresh_listview.setPadding(0,0,0,0);
                    //回调接口
                    if( mOnRefreshListener != null){
                        this.mOnRefreshListener.onPullDownRefresh();
                    }
                }
                break;
        }
        return super.onTouchEvent(ev);
    }

    private boolean isDisplayTopNews() {
        if(topnewsView!=null){
            int[] location = new int[2];
            if(listViewOnScreen==-1){

                getLocationOnScreen(location);
                listViewOnScreen =location[1];
            }
            topnewsView.getLocationOnScreen(location);
            int topnewsViewOnScreen = location[1];
            return topnewsViewOnScreen>=listViewOnScreen;
        }else {
            return true;
        }

    }

    private void refreshViewState() {
        switch (currentStatus){
            case PULLDOWNREFRESH:
                iv_refresh_arr.startAnimation(downAnimation);
                tv_statu.setText("下拉刷新...");
                break;
            case PULLDOWNREFRESH_RELESE:
                iv_refresh_arr.startAnimation(upAnimation);
                tv_statu.setText("松手刷新...");
                break;
            case PULLDOWNREFRESH_REFRESHING:
                tv_statu.setText("正在刷新...");
                iv_refresh_arr.setVisibility(View.GONE);
                pb_refresh_progresbar.setVisibility(View.VISIBLE);
                iv_refresh_arr.clearAnimation();
                break;
        }
    }

    /**
     * 刷新完成，成功更新时间，失败不更新时间
     * @param success
     */
    public void onFinishRefresh(boolean success) {
        if(isLoadMore){
            isLoadMore = false;
            footerView.setPadding(0,-footerViewHeight,0,0);
        }else{
            currentStatus = PULLDOWNREFRESH;
            tv_statu.setText("下拉刷新...");
            iv_refresh_arr.setVisibility(View.VISIBLE);
            pb_refresh_progresbar.setVisibility(View.GONE);
            iv_refresh_arr.clearAnimation();
            ll_refresh_listview.setPadding(0,-refreshListViewHeight,0,0);
            if(success) {
                tv_time.setText("上次更新时间：" + getSystemTime());
            }
        }

    }

    private String getSystemTime() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
        return simpleDateFormat.format(new Date());
    }

    public interface OnRefreshListener{
        /**
         * 当下拉刷新时回调这个方法
         */
        public void onPullDownRefresh();

        public void onLoadMore();
    }
    private OnRefreshListener mOnRefreshListener;
    public OnRefreshListener setOnRefreshListener(OnRefreshListener l){
        this.mOnRefreshListener = l;
        return  this.mOnRefreshListener;
    }
}
