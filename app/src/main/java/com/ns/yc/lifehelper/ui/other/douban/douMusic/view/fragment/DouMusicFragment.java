package com.ns.yc.lifehelper.ui.other.douban.douMusic.view.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.blankj.utilcode.util.NetworkUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.ns.yc.lifehelper.R;
import com.ns.yc.lifehelper.base.mvp.BaseLazyFragment;
import com.ns.yc.lifehelper.ui.other.douban.douMusic.view.DouMusicActivity;
import com.ns.yc.lifehelper.ui.other.douban.douMusic.view.activity.DouMusicDetailActivity;
import com.ns.yc.lifehelper.ui.other.douban.douMusic.view.adapter.DouMusicAdapter;
import com.ns.yc.lifehelper.ui.other.douban.douMusic.bean.DouMusicBean;
import com.ns.yc.lifehelper.api.http.douban.DouMusicModel;
import org.yczbj.ycrefreshviewlib.item.RecycleViewItemLine;

import org.yczbj.ycrefreshviewlib.YCRefreshView;
import org.yczbj.ycrefreshviewlib.adapter.RecyclerArrayAdapter;

import butterknife.BindView;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


/**
 * ================================================
 * 作    者：杨充
 * 版    本：1.0
 * 创建日期：2017/8/22
 * 描    述：豆瓣音乐页面
 * 修订历史：
 * ================================================
 */
public class DouMusicFragment extends BaseLazyFragment {


    private static final String TYPE = "";
    @BindView(R.id.recyclerView)
    YCRefreshView recyclerView;
    private DouMusicActivity activity;
    private String mType;
    private DouMusicAdapter adapter;

    public static DouMusicFragment newInstance(String param1) {
        DouMusicFragment fragment = new DouMusicFragment();
        Bundle args = new Bundle();
        args.putString(TYPE, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (DouMusicActivity) context;

    }

    @Override
    public void onDetach() {
        super.onDetach();
        activity = null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mType = getArguments().getString(TYPE);
        }
    }


    @Override
    public int getContentView() {
        return R.layout.base_easy_recycle;
    }

    @Override
    public void initView() {
        initRecycleView();
    }

    @Override
    public void initListener() {
        adapter.setOnItemClickListener(new RecyclerArrayAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(activity, DouMusicDetailActivity.class);
                intent.putExtra("id",adapter.getAllData().get(position).getId());
                intent.putExtra("title",adapter.getAllData().get(position).getTitle());
                intent.putExtra("image",adapter.getAllData().get(position).getImage());
                intent.putExtra("average",adapter.getAllData().get(position).getRating().getAverage());
                intent.putExtra("numRaters",String.valueOf(adapter.getAllData().get(position).getRating().getNumRaters()));
                intent.putExtra("alt_title",adapter.getAllData().get(position).getAlt_title());
                startActivity(intent);
            }
        });
    }

    @Override
    public void initData() {

    }


    @Override
    public void onLazyLoad() {
        recyclerView.showProgress();
        getDouMusic(mType , 0 , 30);
    }


    private void initRecycleView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        final RecycleViewItemLine line = new RecycleViewItemLine(activity, LinearLayout.HORIZONTAL,
                SizeUtils.px2dp(6), getResources().getColor(R.color.colorWhite));
        //recyclerView.addItemDecoration(line);
        adapter = new DouMusicAdapter(activity);
        recyclerView.setAdapter(adapter);
        AddHeader();
        adapter.setMore(R.layout.view_recycle_more, new RecyclerArrayAdapter.OnMoreListener() {
            @Override
            public void onMoreShow() {
                if (NetworkUtils.isConnected()) {
                    if (adapter.getAllData().size() > 0) {
                        getDouMusic(mType, adapter.getAllData().size(), adapter.getAllData().size() + 21);
                    } else {
                        adapter.pauseMore();
                    }
                } else {
                    adapter.pauseMore();
                    Toast.makeText(activity, "网络不可用", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onMoreClick() {

            }
        });

        //设置没有数据
        adapter.setNoMore(R.layout.view_recycle_no_more, new RecyclerArrayAdapter.OnNoMoreListener() {
            @Override
            public void onNoMoreShow() {
                if (NetworkUtils.isConnected()) {
                    adapter.resumeMore();
                } else {
                    Toast.makeText(activity, "网络不可用", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNoMoreClick() {
                if (NetworkUtils.isConnected()) {
                    adapter.resumeMore();
                } else {
                    Toast.makeText(activity, "网络不可用", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //设置错误
        adapter.setError(R.layout.view_recycle_error, new RecyclerArrayAdapter.OnErrorListener() {
            @Override
            public void onErrorShow() {
                adapter.resumeMore();
            }

            @Override
            public void onErrorClick() {
                adapter.resumeMore();
            }
        });

        //刷新
        recyclerView.setRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (NetworkUtils.isConnected()) {
                    getDouMusic(mType , 0 , 30);
                } else {
                    recyclerView.setRefreshing(false);
                    Toast.makeText(activity, "网络不可用", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * 添加头部布局
     */
    private void AddHeader() {

    }

    private void getDouMusic(String mType , final int start, int count) {
        DouMusicModel model = new DouMusicModel(activity);
        model.getMusic(mType,start,count)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<DouMusicBean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        recyclerView.showError();
                        recyclerView.setErrorView(R.layout.view_custom_empty_data);
                    }

                    @Override
                    public void onNext(DouMusicBean musicBean) {
                        if(adapter==null){
                            adapter = new DouMusicAdapter(activity);
                        }

                        if(start==0){
                            if(musicBean!=null && musicBean.getMusics()!=null && musicBean.getMusics().size()>0){
                                adapter.clear();
                                adapter.addAll(musicBean.getMusics());
                                adapter.notifyDataSetChanged();
                            } else {
                                recyclerView.showEmpty();
                                recyclerView.setEmptyView(R.layout.view_custom_empty_data);
                            }
                        } else {
                            if(musicBean!=null && musicBean.getMusics()!=null && musicBean.getMusics().size()>0){
                                adapter.addAll(musicBean.getMusics());
                                adapter.notifyDataSetChanged();
                            } else {
                                adapter.stopMore();
                            }
                        }
                    }
                });
    }


}
