package com.zcc.news.menudatailpager;

import android.content.Context;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.viewpagerindicator.TabPageIndicator;
import com.zcc.news.R;
import com.zcc.news.activity.MainActivity;
import com.zcc.news.base.MenuDetaiBasePager;
import com.zcc.news.domain.NewsPagerBean;
import com.zcc.news.menudatailpager.tableDetailPager.TableDetailPager;
import com.zcc.news.menudatailpager.tableDetailPager.TopicTableDetailPager;

import org.xutils.common.util.LogUtil;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017-02-24.
 */
public class TopicMenuDetailPager extends MenuDetaiBasePager{
    @ViewInject(R.id.viewpager)
    private ViewPager viewPager;
    @ViewInject(R.id.tabLayout)
    private TabLayout tabLayout;
    @ViewInject(R.id.ib_next)
    private ImageButton ib_next;
    private List<NewsPagerBean.DataBean.ChildrenData> childrenDatas;
    private ArrayList<TopicTableDetailPager> tableDetailPagers;
    public TopicMenuDetailPager(Context context, NewsPagerBean.DataBean dataBean) {
        super(context);
        this.childrenDatas = dataBean.getChildren();
    }

    @Override
    public View initView() {
        View view = View.inflate(context, R.layout.topicmenu_detail_pager,null);
        x.view().inject(TopicMenuDetailPager.this,view);
        return view;
    }

    @Override
    public void initData() {
        super.initData();
        LogUtil.e("专题详情页面被初始化");
        tableDetailPagers = new ArrayList<>();
        for(int i=0;i<childrenDatas.size();i++){
            tableDetailPagers.add(new TopicTableDetailPager(context,childrenDatas.get(i)));
        }
        viewPager.setAdapter(new NewsMenuDetailPagerAdapter());
        tabLayout.setupWithViewPager(viewPager);
        ib_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
            }
        });
        viewPager.addOnPageChangeListener(new NewsMenuDetailPagerOnPageChangeListener());
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            tab.setCustomView(getTabView(i));
        }
    }
    public View getTabView(int position){
        View view = LayoutInflater.from(context).inflate(R.layout.tab_item, null);
        TextView tv= (TextView) view.findViewById(R.id.textView);
        tv.setText(childrenDatas.get(position).getTitle());
        ImageView img = (ImageView) view.findViewById(R.id.imageView);
        img.setImageResource(R.drawable.dot_focus);
        return view;
    }

    class NewsMenuDetailPagerAdapter extends PagerAdapter {

        @Override
        public CharSequence getPageTitle(int position) {
            return childrenDatas.get(position).getTitle();
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            TopicTableDetailPager tableDetailPager = tableDetailPagers.get(position);
            View rootView = tableDetailPager.rootView;
            tableDetailPager.initData();
            container.addView(rootView);
            return rootView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getCount() {
            return tableDetailPagers.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view==object;
        }
    }

    class NewsMenuDetailPagerOnPageChangeListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            if(position==0){
                isEnableSlidingMenu(SlidingMenu.TOUCHMODE_FULLSCREEN);
            }else {
                isEnableSlidingMenu(SlidingMenu.TOUCHMODE_NONE);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }
    private void  isEnableSlidingMenu(int touchmodeFullscreen){
        MainActivity mainActivity = (MainActivity)context;
        mainActivity.getSlidingMenu().setTouchModeAbove(touchmodeFullscreen);
    }
}
