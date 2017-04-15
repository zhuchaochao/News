package com.zcc.news.fragment;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.zcc.news.R;

import com.zcc.news.activity.MainActivity;
import com.zcc.news.base.BaseFragment;
import com.zcc.news.base.BasePager;
import com.zcc.news.pager.GovaffairPager;
import com.zcc.news.pager.HomePager;
import com.zcc.news.pager.NewsCenterPager;
import com.zcc.news.pager.SettingPager;
import com.zcc.news.pager.SmartServicePager;
import com.zcc.news.view.NoScrollViewPager;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017-02-08.
 */
public class ContentFragment extends BaseFragment {
    @ViewInject(R.id.rg_main)
    public RadioGroup rg_main;
    @ViewInject(R.id.viewpager)
    public NoScrollViewPager viewPager;
    private ArrayList<BasePager> basePagers;
    @Override
    public View initView() {
        View view =View.inflate(context,R.layout.content_fragment,null);
        x.view().inject(ContentFragment.this,view);
        isEnableSlidingMenu(SlidingMenu.TOUCHMODE_NONE);
        return view;
    }

    @Override
    public void initData() {
        super.initData();
        rg_main.check(R.id.rg_home);
        basePagers =new ArrayList<>();
        basePagers.add(new HomePager(context));
        basePagers.add(new NewsCenterPager(context));
        basePagers.add(new SmartServicePager(context));
        basePagers.add(new GovaffairPager(context));
        basePagers.add(new SettingPager(context));
        viewPager.setAdapter(new ContentFragmentViewPagerAdapter());
        viewPager.addOnPageChangeListener(new MyOnPageChangeListener());
        basePagers.get(0).initData();
        rg_main.setOnCheckedChangeListener(new MyOnCheckedChangeListener());
    }

    public NewsCenterPager getNewCenterPager() {
        return (NewsCenterPager) basePagers.get(1);
    }

    class  MyOnPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            basePagers.get(position).initData();
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }
    class ContentFragmentViewPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return basePagers.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            BasePager basePager = basePagers.get(position);
            View rootView = basePager.rootView;
            container.addView(rootView);
            return  rootView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

     class MyOnCheckedChangeListener implements RadioGroup.OnCheckedChangeListener {

         @Override
         public void onCheckedChanged(RadioGroup group, int checkedId) {
             switch (checkedId){
                 case R.id.rg_home:
                     viewPager.setCurrentItem(0,false);
                     isEnableSlidingMenu(SlidingMenu.TOUCHMODE_NONE);
                     break;
                 case R.id.rg_news:
                     viewPager.setCurrentItem(1,false);
                     isEnableSlidingMenu(SlidingMenu.TOUCHMODE_FULLSCREEN);
                     break;
                 case R.id.rg_smart:
                     viewPager.setCurrentItem(2,false);
                     isEnableSlidingMenu(SlidingMenu.TOUCHMODE_NONE);
                     break;
                 case R.id.rg_govaffair:
                     viewPager.setCurrentItem(3,false);
                     isEnableSlidingMenu(SlidingMenu.TOUCHMODE_NONE);
                     break;
                 case R.id.rg_setting:
                     viewPager.setCurrentItem(4,false);
                     isEnableSlidingMenu(SlidingMenu.TOUCHMODE_NONE);
                     break;
             }
         }
     }
    /**
     根据传人的参数设置是否让SlidingMenu可以滑动
     */
    private void isEnableSlidingMenu(int touchmodeFullscreen) {
        MainActivity mainActivity = (MainActivity) context;
        mainActivity.getSlidingMenu().setTouchModeAbove(touchmodeFullscreen);
    }
}
