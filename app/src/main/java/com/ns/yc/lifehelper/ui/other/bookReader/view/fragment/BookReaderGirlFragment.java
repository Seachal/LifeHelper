package com.ns.yc.lifehelper.ui.other.bookReader.view.fragment;

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
import com.ns.yc.lifehelper.comment.ConstantBookReader;
import com.ns.yc.lifehelper.base.mvp.BaseFragment;
import com.ns.yc.lifehelper.model.event.BookReaderSelectionEvent;
import com.ns.yc.lifehelper.ui.other.bookReader.view.activity.BookDetailDiscussionActivity;
import com.ns.yc.lifehelper.ui.other.bookReader.view.activity.BookReaderGirlActivity;
import com.ns.yc.lifehelper.ui.other.bookReader.view.adapter.ReaderGirlAdapter;
import com.ns.yc.lifehelper.ui.other.bookReader.bean.ReaderBookDiscussionList;
import com.ns.yc.lifehelper.api.http.bookReader.BookReaderModel;
import org.yczbj.ycrefreshviewlib.item.RecycleViewItemLine;
import com.ns.yc.lifehelper.utils.AppUtil;
import com.ns.yc.lifehelper.utils.EventBusUtils;
import com.ns.yc.lifehelper.utils.rxUtils.RxUtil;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.yczbj.ycrefreshviewlib.YCRefreshView;
import org.yczbj.ycrefreshviewlib.adapter.RecyclerArrayAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * ================================================
 * 作    者：杨充
 * 版    本：1.0
 * 创建日期：2017/9/27
 * 描    述：女生爱好区
 * 修订历史：
 * ================================================
 */
public class BookReaderGirlFragment extends BaseFragment {

    @BindView(R.id.recyclerView)
    YCRefreshView recyclerView;
    private BookReaderGirlActivity activity;
    private static final String TYPE = "type";
    private String mType;
    private String sort = ConstantBookReader.SortType.DEFAULT;
    private String distillate = ConstantBookReader.Distillate.ALL;
    private ReaderGirlAdapter adapter;
    protected int start = 0;
    protected int limit = 20;

    public static BookReaderGirlFragment newInstance(String param) {
        BookReaderGirlFragment fragment = new BookReaderGirlFragment();
        Bundle args = new Bundle();
        args.putString(TYPE, param);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            Bundle arguments = getArguments();
            mType = arguments.getString(TYPE);
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (BookReaderGirlActivity) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        activity = null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        EventBusUtils.unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void initCategoryList(BookReaderSelectionEvent event) {
        recyclerView.setRefreshing(true);
        sort = event.sort;
        distillate = event.distillate;
        initData();
    }

    @Override
    public int getContentView() {
        return R.layout.base_easy_recycle;
    }

    @Override
    public void initView() {
        EventBusUtils.register(this);
        initRecycleView();
    }

    @Override
    public void initListener() {
        adapter.setOnItemClickListener(new RecyclerArrayAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                if(adapter.getAllData().size()>position && position>-1){
                    Intent intent = new Intent(activity, BookDetailDiscussionActivity.class);
                    intent.putExtra("id",adapter.getAllData().get(position).get_id());
                    activity.startActivity(intent);
                }
            }
        });
    }

    @Override
    public void initData() {
        recyclerView.showProgress();
        getBookHelpList(sort, distillate, 0, limit);
    }


    private void initRecycleView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        adapter = new ReaderGirlAdapter(activity);
        recyclerView.setAdapter(adapter);
        final RecycleViewItemLine line = new RecycleViewItemLine(activity, LinearLayout.HORIZONTAL,
                SizeUtils.dp2px(1), activity.getResources().getColor(R.color.grayLine));
        recyclerView.addItemDecoration(line);
        //加载更多
        adapter.setMore(R.layout.view_recycle_more, new RecyclerArrayAdapter.OnMoreListener() {
            @Override
            public void onMoreShow() {
                if (NetworkUtils.isConnected()) {
                    if (adapter.getAllData().size() > 0) {
                        start = start + adapter.getAllData().size();
                        getBookHelpList(sort, distillate, start, limit);
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
                    start = 0;
                    getBookHelpList(sort, distillate, start, limit);
                } else {
                    recyclerView.setRefreshing(false);
                    Toast.makeText(activity, "网络不可用", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void getBookHelpList(String sort, String distillate, final int start, int limit) {
        String key = AppUtil.createCacheKey("girl-book-discussion-list", "girl", "all", sort, "all", start + "", limit + "", distillate);
        BookReaderModel model = BookReaderModel.getInstance(activity);
        Observable<ReaderBookDiscussionList> fromNetWork = model.getBookGirlList("girl", "all", sort, "all", start + "", limit + "", distillate)
                .compose(RxUtil.<ReaderBookDiscussionList>rxCacheListHelper(key));

        //依次检查disk、network
        Subscription rxSubscription = Observable.concat(RxUtil.rxCreateDiskObservable(key, ReaderBookDiscussionList.class), fromNetWork)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ReaderBookDiscussionList>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(ReaderBookDiscussionList list) {
                        boolean isRefresh = start == 0 ? true : false;
                        if(isRefresh){
                            recyclerView.setRefreshing(true);
                        }else {
                            recyclerView.setRefreshing(false);
                        }

                        if(adapter==null){
                            adapter = new ReaderGirlAdapter(activity);
                        }

                        if(start==0){
                            if(list.isOk()){
                                if(list.getPosts()!=null && list.getPosts().size()>0){
                                    recyclerView.showRecycler();
                                    adapter.clear();
                                    adapter.addAll(list.getPosts());
                                    adapter.notifyDataSetChanged();
                                    recyclerView.setRefreshing(false);
                                }else {
                                    recyclerView.setRefreshing(false);
                                }
                            }
                        }else {
                            if(list.isOk()){
                                if(list.getPosts()!=null && list.getPosts().size()>0){
                                    recyclerView.showRecycler();
                                    adapter.addAll(list.getPosts());
                                    adapter.notifyDataSetChanged();
                                }else {
                                    adapter.stopMore();
                                }
                            }
                        }
                    }
                });
    }

}
