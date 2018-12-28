package com.ns.yc.lifehelper.ui.other.bookReader.view.activity;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.ns.yc.lifehelper.R;
import com.ns.yc.lifehelper.base.mvp.BaseActivity;
import com.ns.yc.lifehelper.base.adapter.BasePagerAdapter;
import com.ns.yc.lifehelper.ui.other.bookReader.view.fragment.SubTopRankFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;

/**
 * ================================================
 * 作    者：杨充
 * 版    本：1.0
 * 创建日期：2017/9/19
 * 描    述：小说阅读器追书最热榜排行版
 * 修订历史：
 * ================================================
 */
public class SubTopRankActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.ll_title_menu)
    FrameLayout llTitleMenu;
    @BindView(R.id.toolbar_title)
    TextView toolbarTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tab_layout)
    TabLayout tabLayout;
    @BindView(R.id.vp_content)
    ViewPager vpContent;
    private ArrayList<Fragment> mFragments = new ArrayList<>();
    private List<String> titles;
    private String id;
    private String monthRank;
    private String totalRank;
    private String title;

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public int getContentView() {
        return R.layout.base_tab_view;
    }

    @Override
    public void initView() {
        initToolBar();
        initIntentData();
        initFragmentList();
        initViewPagerAndTab();
    }


    private void initToolBar() {
        toolbarTitle.setText("追书最热榜");
    }


    private void initIntentData() {
        if(getIntent()!=null){
            id = getIntent().getStringExtra("id");
            monthRank = getIntent().getStringExtra("monthRank");
            totalRank = getIntent().getStringExtra("totalRank");
            title = getIntent().getStringExtra("title").split(" ")[0];
        }
        toolbarTitle.setText(title);
    }

    @Override
    public void initListener() {
        llTitleMenu.setOnClickListener(this);
    }

    @Override
    public void initData() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ll_title_menu:
                finish();
                break;
        }
    }

    private void initFragmentList() {
        titles = Arrays.asList(getResources().getStringArray(R.array.sub_rank_tabs));
        mFragments.add(SubTopRankFragment.newInstance(id));
        mFragments.add(SubTopRankFragment.newInstance(monthRank));
        mFragments.add(SubTopRankFragment.newInstance(totalRank));
    }

    private void initViewPagerAndTab() {
        /**
         * 注意使用的是：getChildFragmentManager，
         * 这样setOffscreenPageLimit()就可以添加上，保留相邻2个实例，切换时不会卡
         * 但会内存溢出，在显示时加载数据
         */
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        BasePagerAdapter myAdapter = new BasePagerAdapter(supportFragmentManager, mFragments, titles);
        vpContent.setAdapter(myAdapter);
        // 左右预加载页面的个数
        vpContent.setOffscreenPageLimit(3);
        myAdapter.notifyDataSetChanged();
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        tabLayout.setupWithViewPager(vpContent);
    }


}
