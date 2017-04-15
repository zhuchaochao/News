package com.zcc.news.fragment;

import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.zcc.news.R;
import com.zcc.news.activity.MainActivity;
import com.zcc.news.base.BaseFragment;
import com.zcc.news.domain.NewsPagerBean;
import com.zcc.news.pager.NewsCenterPager;
import com.zcc.news.utils.DensityUtil;

import org.xutils.common.util.LogUtil;

import java.util.List;

/**
 * Created by Administrator on 2017-02-08.
 */
public class LeftMenuFragment extends BaseFragment {
    private ListView listView;
    private List<NewsPagerBean.DataBean> data;
    private LeftMenuFragmentAdapter adapter;
    private int prePosistion;
    @Override
    public View initView() {
        listView = new ListView(context);
        listView.setPadding(0, DensityUtil.dip2px(context, 40), 0, 0);
        listView.setDividerHeight(0);
        listView.setSelector(android.R.color.transparent);
        listView.setCacheColorHint(Color.TRANSPARENT);
        listView.setOnItemClickListener(new LeftMenuFragmentOnItemClickListener());
        return listView;
    }

    @Override
    public void initData() {
        super.initData();
    }

    /**
     * 接受数据
     * @param data
     */
    public void setData(List<NewsPagerBean.DataBean> data) {
        this.data = data;
        adapter = new LeftMenuFragmentAdapter();
        listView.setAdapter(adapter);
        swicthPager(prePosistion);
    }

     class LeftMenuFragmentAdapter extends BaseAdapter {
         @Override
         public int getCount() {
             return data.size();
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
             TextView textView = (TextView) View.inflate(context, R.layout.item_leftmenu,null);
             textView.setText(data.get(position).getTitle());
             textView.setEnabled(prePosistion==position);
             return textView;
         }
     }

     class LeftMenuFragmentOnItemClickListener implements android.widget.AdapterView.OnItemClickListener {

         @Override
         public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
             prePosistion = position;
             adapter.notifyDataSetChanged();
             MainActivity mainActivity = (MainActivity)context;
             mainActivity.getSlidingMenu().toggle();
             swicthPager(prePosistion);
         }
     }

    private void swicthPager(int position) {
        MainActivity mainActivity = (MainActivity) context;
        ContentFragment contentFragment = mainActivity.getContentFragment();
        NewsCenterPager newsCenterPager = contentFragment.getNewCenterPager();
        newsCenterPager.swicthPager(position);
    }
}
