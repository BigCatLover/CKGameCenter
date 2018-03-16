package com.jingyue.lygame.modules.search;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jingyue.lygame.BaseActivity;
import com.jingyue.lygame.R;
import com.jingyue.lygame.bean.GameBean;
import com.jingyue.lygame.bean.SearchHotKeyBean;
import com.jingyue.lygame.bean.SearchLocalWordsBean;
import com.jingyue.lygame.clickaction.SearchAction;
import com.jingyue.lygame.model.HotKeyRp;
import com.jingyue.lygame.model.SearchRp;
import com.jingyue.lygame.model.SearchWordsListRp;
import com.jingyue.lygame.modules.rcmd.TagGameListActivity;
import com.jingyue.lygame.modules.rcmd.adapter.TagGameListAdapter;
import com.jingyue.lygame.modules.search.present.HotKeyPresent;
import com.jingyue.lygame.modules.search.present.SearchPresent;
import com.jingyue.lygame.modules.search.view.HotKeyView;
import com.jingyue.lygame.modules.search.view.SearchView;
import com.jingyue.lygame.utils.SearchHistoryUtils;
import com.jingyue.lygame.utils.recycleview.ItemDividerDecoration;
import com.jingyue.lygame.widget.BaseLinearLayoutManager;
import com.jingyue.lygame.widget.RecycleViewDivider;
import com.jingyue.lygame.widget.tagflow.FlowLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;

import butterknife.BindView;
import butterknife.OnClick;
import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler2;
import in.srain.cube.views.ptr.PtrFrameLayout;

public class SearchActivity extends BaseActivity implements HotKeyView, SearchView {

    @BindView(R.id.back_left)
    ImageView backLeft;
    @BindView(R.id.et_search)
    EditText etSearch;
    @BindView(R.id.delete)
    ImageView delete;
    @BindView(R.id.search_ll)
    TextView searchLl;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.ll_searchResult)
    RelativeLayout llSearchResult;
    @BindView(R.id.nodata_iv)
    ImageView nodataIv;
    @BindView(R.id.ll_nodata)
    RelativeLayout llNodata;
    @BindView(R.id.wordtipList)
    RecyclerView wordtipList;
    @BindView(R.id.ll_searchwordtip)
    RelativeLayout llSearchwordtip;
    @BindView(R.id.historyList)
    RecyclerView historyList;
    @BindView(R.id.delete_history)
    TextView deleteHistory;
    @BindView(R.id.ll_history)
    RelativeLayout llHistory;
    @BindView(R.id.dot1)
    ImageView dot1;
    @BindView(R.id.hot_search)
    RelativeLayout hotSearch;
    @BindView(R.id.recy_hotsearch)
    RecyclerView recyHotsearch;
    @BindView(R.id.dot3)
    ImageView dot3;
    @BindView(R.id.hot_label_ll)
    RelativeLayout hotLabelLl;
    @BindView(R.id.hot_tab_context)
    FlowLayout hotTabContext;
    @BindView(R.id.ll_searchUI)
    RelativeLayout llSearchUI;
    @BindView(R.id.ptrRefresh)
    PtrClassicFrameLayout ptrRefresh;
    @BindView(R.id.change)
    TextView change;
    @BindView(R.id.ll_content)
    FrameLayout llContent;

    private HotKeyPresent hotKeyPresent;
    private SearchLocalWordsBean searchWordsList;//热词
    private static final int HINT_RAND = 10;
    public static final int SHOW_TIP = 1;
    public static final int SHOW_SEARCH_RESULT = 2;
    public static String searchKey = "";
    private TipWordsAdapter tipAdapter;
    private SearchPresent searchPresent;
    private List<GameBean> searchResult = new ArrayList<>();//搜索结果列表
    private SimpleDateFormat mFormat;
    private static final int HISTORY_MAX = 10;
    private List<String> mHistoryDatas = new ArrayList<>();//历史搜索记录列表
    private SearchHisAdaper hisAdaper;
    private SearchHotKeyAdapter hotKeyAdapter;
    private TagGameListAdapter mListAdapter;
    private int currentPage = 1;
    private final static int DEFAULT_LIMIT_NUM = 7;
    private boolean backFlag = false;
    private boolean isSearch = false;
    private Random rand;
    private SearchHotKeyBean hotKeyBean;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == SHOW_TIP) {
                updateTipList();
            } else if (msg.what == SHOW_SEARCH_RESULT) {
                searchKey = String.valueOf(msg.obj);
                llHistory.setVisibility(View.GONE);
                gotoSearch();
            }

        }
    };
    private boolean isPull = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        add(hotKeyPresent = new HotKeyPresent(this, this, new HotKeyRp()));
        hotKeyPresent.loadData(false);
        currentPage = 1;
        ptrRefresh.setPtrHandler(new PtrDefaultHandler2() {
            @Override
            public boolean checkCanDoLoadMore(PtrFrameLayout frame, View content, View footer) {
                return !isPull && super.checkCanDoLoadMore(frame, content, footer);
            }

            @Override
            public void onLoadMoreBegin(PtrFrameLayout frame) {
                currentPage++;
                isPull = true;
                loadData(false, false);
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                currentPage = 1;
                isPull = false;
                loadData(true, false);
            }
        });
        setupUI();
    }

    private void setupUI() {
        rand = new Random();
        searchWordsList = SearchWordsListRp.getInstance().getData();
        String searchGame = "";
        if (searchWordsList != null) {
            if (searchWordsList.gameName.size() < 10 && searchWordsList.gameName.size() > 0) {
                searchGame = searchWordsList.gameName.get(rand.nextInt(searchWordsList.gameName.size()));
            } else {
                searchGame = searchWordsList.gameName.get(rand.nextInt(HINT_RAND));
            }
        }
        etSearch.setHint(getString(R.string.a_0138, searchGame));
        //历史记录
        hisAdaper = new SearchHisAdaper(this, mHandler);
        historyList.setLayoutManager(new BaseLinearLayoutManager(this));
        historyList.addItemDecoration(new RecycleViewDivider(this, GridLayoutManager.HORIZONTAL, 1,
                ResourcesCompat.getColor(getResources(), R.color.bg_common, getTheme()), false));
        historyList.setAdapter(hisAdaper);
        //热词
        recyHotsearch.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        hotKeyAdapter = new SearchHotKeyAdapter(this, mHandler);
        recyHotsearch.addItemDecoration(new RecycleViewDivider(this, GridLayoutManager.HORIZONTAL, 1,
                ResourcesCompat.getColor(getResources(), R.color.bg_common, getTheme()), false));
        recyHotsearch.addItemDecoration(new RecycleViewDivider(this, GridLayoutManager.VERTICAL, 1,
                ResourcesCompat.getColor(getResources(), R.color.bg_common, getTheme()), 20, true));
        recyHotsearch.setAdapter(hotKeyAdapter);
        //搜索结果
        recyclerView.setLayoutManager(new BaseLinearLayoutManager(this));
        recyclerView.addItemDecoration(new ItemDividerDecoration(this, ItemDividerDecoration.VERTICAL_LIST));
        recyclerView.setAdapter(mListAdapter = new TagGameListAdapter(this));
        //提示列表
        wordtipList.setLayoutManager(new BaseLinearLayoutManager(this));
        tipAdapter = new TipWordsAdapter();
        tipAdapter.setOnClickListener(new TipWordsAdapter.ClickListener() {
            @Override
            public void search(String search) {
                searchKey = search;
                gotoSearch();
            }
        });
        wordtipList.addItemDecoration(new RecycleViewDivider(this, GridLayoutManager.HORIZONTAL, 1,
                ResourcesCompat.getColor(getResources(), R.color.bg_common, getTheme()), false));
        wordtipList.setAdapter(tipAdapter);

        etSearch.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
//                    etSearch.setHint("");
                    if (etSearch.getText().toString().trim().length() > 0) {
                        llSearchUI.setVisibility(View.GONE);
                        llHistory.setVisibility(View.GONE);
                        llSearchwordtip.setVisibility(View.VISIBLE);
                        llSearchResult.setVisibility(View.GONE);
                        llNodata.setVisibility(View.GONE);
                        delete.setVisibility(View.VISIBLE);
                        mHandler.sendEmptyMessageDelayed(SHOW_TIP, 300);
                    } else {
                        showHistory();
                        delete.setVisibility(View.GONE);
                    }
                } else {
                    delete.setVisibility(View.GONE);
                }
            }
        });

        etSearch.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                etSearch.setCursorVisible(true);
                if (etSearch.getText().toString().trim().length() > 0) {
                    delete.setVisibility(View.VISIBLE);
                }
                return false;
            }
        });

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    etSearch.setHint("");
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        if (etSearch.isCursorVisible()) {
                            delete.setVisibility(View.VISIBLE);
                        } else {
                            delete.setVisibility(View.GONE);
                        }
                    }
                } else {
                    delete.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                //文字变动 ， 有未发出的搜索请求，应取消
                if (mHandler.hasMessages(SHOW_TIP)) {
                    mHandler.removeMessages(SHOW_TIP);
                }
                //如果为空 直接显示搜索历史
                if (TextUtils.isEmpty(s)) {
                    showHistory();
                } else {//否则延迟300ms开始搜索
                    llSearchUI.setVisibility(View.GONE);
                    llSearchwordtip.setVisibility(View.VISIBLE);
                    llSearchResult.setVisibility(View.GONE);
                    llNodata.setVisibility(View.GONE);
                    if (!isSearch) {
                        mHandler.sendEmptyMessageDelayed(SHOW_TIP, 300); //自动搜索功能
                    }
                }
            }
        });
        wordtipList.setLayoutManager(new BaseLinearLayoutManager(this));
    }

    private void updateTipList() {
        String key = etSearch.getText().toString().trim();
        if (key.isEmpty()) {
            return;
        }
        backFlag = true;
        llHistory.setVisibility(View.GONE);
        llSearchUI.setVisibility(View.GONE);
        llNodata.setVisibility(View.GONE);
        llSearchwordtip.setVisibility(View.VISIBLE);
        llSearchResult.setVisibility(View.GONE);

        List<String> mFilter = new ArrayList<>();
        for (String gamename : searchWordsList.gameName) {
            if (gamename.indexOf(key) != -1 && !gamename.equals(key)) {
                mFilter.add(gamename);
            }
        }
        for (String company : searchWordsList.companyName) {
            if (company.indexOf(key) != -1 && !company.equals(key)) {
                mFilter.add(company);
            }
        }
        for (String keywords : searchWordsList.keywordsName) {
            if (keywords.indexOf(key) != -1 && !keywords.equals(key)) {
                mFilter.add(keywords);
            }
        }
        for (String type : searchWordsList.typeName) {
            if (type.indexOf(key) != -1 && !type.equals(key)) {
                mFilter.add(type);
            }
        }

        if (mFilter.size() > 10) {
            List<String> newFilter = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                newFilter.add(mFilter.get(i));
            }
            tipAdapter.setDatas(newFilter);
        } else if (mFilter.size() > 0) {
            tipAdapter.setDatas(mFilter);
        } else {
            llSearchwordtip.setVisibility(View.GONE);
        }

        wordtipList.setAdapter(tipAdapter);
    }

    private void gotoSearch() {
        currentPage = 1;
        backFlag = true;
        isSearch = true;
        //隐藏键盘
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(etSearch.getWindowToken(), 0);

        if (searchKey.isEmpty()) {
            return;
        }
        etSearch.setText(searchKey);
        etSearch.setSelection(searchKey.length());
        etSearch.setCursorVisible(false);
        if (searchPresent == null) {
            searchPresent = new SearchPresent(this, this, new SearchRp());
            add(searchPresent);
        }
        loadData(true, true);
    }

    @Override
    public void onBackPressed() {
        if (backFlag) {
            backFlag = false;
            llSearchUI.setVisibility(View.VISIBLE);
            llHistory.setVisibility(View.GONE);
            llSearchwordtip.setVisibility(View.GONE);
            llSearchResult.setVisibility(View.GONE);
            llNodata.setVisibility(View.GONE);
            if (etSearch.getText().toString().trim().length() < 1) {
                String searchGame = "";
                if (searchWordsList.gameName.size() < 10 && searchWordsList.gameName.size() > 0) {
                    searchGame = searchWordsList.gameName.get(rand.nextInt(searchWordsList.gameName.size()));
                } else {
                    searchGame = searchWordsList.gameName.get(rand.nextInt(HINT_RAND));
                }
                etSearch.setHint(getString(R.string.a_0138, searchGame));
            }
            return;
        }
        super.onBackPressed();
    }

    //加载搜索游戏列表
    private void loadData(boolean isRefresh, boolean needProgress) {
        //搜索埋点
        recordPageEvent(new SearchAction(searchKey));
        searchPresent.loadData(searchKey, currentPage, DEFAULT_LIMIT_NUM, isRefresh, needProgress);
    }

    private void showHistory() {
        backFlag = true;
        Map<String, String> historys = (Map<String, String>) SearchHistoryUtils.getAll(this);
        mHistoryDatas.clear();
        llSearchUI.setVisibility(View.GONE);
        llSearchwordtip.setVisibility(View.GONE);
        llSearchResult.setVisibility(View.GONE);
        llNodata.setVisibility(View.GONE);
        if (historys.size() > 0) {
            llHistory.setVisibility(View.VISIBLE);
            Object[] keys = historys.keySet().toArray();
            Arrays.sort(keys);
            int keyLeng = keys.length;
            //这里计算 如果历史记录条数是大于 可以显示的最大条数，则用最大条数做循环条件，防止历史记录条数-最大条数为负值，数组越界
            int hisLeng = keyLeng > HISTORY_MAX ? HISTORY_MAX : keyLeng;
            for (int i = 1; i <= hisLeng; i++) {
                mHistoryDatas.add(historys.get(keys[keyLeng - i]));
            }
            hisAdaper.setDatas(mHistoryDatas);
        } else {
            llHistory.setVisibility(View.GONE);
            llSearchUI.setVisibility(View.VISIBLE);
        }
    }

    //清楚历史记录
    private void clearsearchHistory() {
        SearchHistoryUtils.clear(this);
        mHistoryDatas.clear();
        llHistory.setVisibility(View.GONE);
        llSearchUI.setVisibility(View.VISIBLE);
        backFlag = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        delMoreSearchHistory();
    }

    private void setLabelUI(boolean changeGames) {
        if (!changeGames) {
            //热门标签
            hotTabContext.removeAllViews();
            for (final SearchHotKeyBean.HotKeyTag tag1 : hotKeyBean.tags) {
                TextView tagtext = (TextView) LayoutInflater.from(this).inflate(R.layout.common_tag_txv, hotTabContext, false);
                tagtext.setText(tag1.name);
                tagtext.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        TagGameListActivity.open(SearchActivity.this, tag1.id, tag1.name);

                    }
                });
                hotTabContext.addView(tagtext);
            }
        }
        //搜索热词
        hotKeyAdapter.setDatas(hotKeyBean.games);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_search;
    }

    @Override
    public void setDataOnMain(SearchHotKeyBean bean, boolean onlyGames) {
        hotKeyBean = bean;
        setLabelUI(onlyGames);
    }

    @OnClick({R.id.back_left, R.id.search_ll, R.id.delete, R.id.delete_history, R.id.change})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.delete_history:
                clearsearchHistory();
                break;
            case R.id.back_left:
                finish();
                break;
            case R.id.search_ll:
                searchKey = etSearch.getText().toString().trim();
                if (searchKey.isEmpty()) {
                    if (etSearch.getHint().toString().trim().length() > 0) {
                        String[] s = etSearch.getHint().toString().trim().split("：");
                        searchKey = s[1];
                    }
                }
                gotoSearch();
                break;
            case R.id.delete:
                etSearch.setText("");
                break;
            case R.id.change:
                hotKeyPresent.loadData(true);
                break;
        }
    }

    public static void start(Context context) {
        Intent starter = new Intent(context, SearchActivity.class);
        context.startActivity(starter);
    }

    @Override
    public void setDataOnMain(List<GameBean> beans, boolean isRefresh) {
        searchResult = beans;
        isSearch = false;
        ptrRefresh.refreshComplete();
        showSearchResultUI(isRefresh);
    }

    @Override
    public void noMore() {
        if (ptrRefresh != null) {
            ptrRefresh.refreshComplete();
        }
    }

    private void showSearchResultUI(boolean isRefresh) {
        if (delete == null) {
            return;
        }
        delete.setVisibility(View.GONE);
        saveSearchHistory(searchKey);
        llHistory.setVisibility(View.GONE);
        llSearchUI.setVisibility(View.GONE);
        llSearchwordtip.setVisibility(View.GONE);
        if (searchResult == null || searchResult.isEmpty()) {
            if (isRefresh) {
                llNodata.setVisibility(View.VISIBLE);
                llSearchResult.setVisibility(View.GONE);
            }
        } else {
            llNodata.setVisibility(View.GONE);
            llSearchResult.setVisibility(View.VISIBLE);
        }
        mListAdapter.addAll(searchResult, isRefresh);
//
    }

    //保存到历史记录
    private void saveSearchHistory(String keyWords) {
        Map<String, String> historys = (Map<String, String>) SearchHistoryUtils.getAll(this);
        for (Map.Entry<String, String> entry : historys.entrySet()) {
            if (keyWords.equals(entry.getValue())) {
                SearchHistoryUtils.remove(this, entry.getKey());
            }
        }
        SearchHistoryUtils.put(this, mFormat.format(new Date()), keyWords);
    }

    //删除多余历史记录
    private void delMoreSearchHistory() {
        Map<String, String> hisAll = (Map<String, String>) SearchHistoryUtils.getAll(this);
        if (hisAll.size() > HISTORY_MAX) {
            //将key排序升序
            Object[] keys = hisAll.keySet().toArray();
            Arrays.sort(keys);
            for (int i = keys.length - HISTORY_MAX - 1; i > -1; i--) {
                SearchHistoryUtils.remove(this, (String) keys[i]);
            }
        }
    }
}
