package com.sunofbeaches.taobaounion.ui.fragment;

import android.graphics.Rect;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.sunofbeaches.taobaounion.R;
import com.sunofbeaches.taobaounion.base.BaseFragment;
import com.sunofbeaches.taobaounion.model.domain.Histories;
import com.sunofbeaches.taobaounion.model.domain.IBaseInfo;
import com.sunofbeaches.taobaounion.model.domain.SearchRecommend;
import com.sunofbeaches.taobaounion.model.domain.SearchResult;
import com.sunofbeaches.taobaounion.presenter.ISearchPresenter;
import com.sunofbeaches.taobaounion.ui.adapter.LinearItemContentAdapter;
import com.sunofbeaches.taobaounion.ui.custom.TextFlowLayout;
import com.sunofbeaches.taobaounion.utils.KeyBoardUtil;
import com.sunofbeaches.taobaounion.utils.LogUtils;
import com.sunofbeaches.taobaounion.utils.PresenterManger;
import com.sunofbeaches.taobaounion.utils.SizeUtils;
import com.sunofbeaches.taobaounion.utils.TicketUtils;
import com.sunofbeaches.taobaounion.utils.ToastUtils;
import com.sunofbeaches.taobaounion.view.ISearchPageCallback;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;


public class SearchFragment extends BaseFragment implements ISearchPageCallback, TextFlowLayout.OnFlowTextItemClickListener {

    private ISearchPresenter mSearchPresenter;

    @BindView(R.id.search_recommend_view)
    public TextFlowLayout mRecommendView;

    @BindView(R.id.search_history_view)
    public TextFlowLayout mHistoryView;

    @BindView(R.id.search_recommend_container)
    public View mRecommendContainer;

    @BindView(R.id.search_history_container)
    public View mHistoryContainer;

    @BindView(R.id.search_history_delete)
    public ImageView mHistoryDelete;

    @BindView(R.id.search_result_list)
    public RecyclerView mSearchList;

    @BindView(R.id.search_result_container)
    public TwinklingRefreshLayout mRefreshContainer;

    @BindView(R.id.search_clean_box)
    public ImageView mCleanInputBtn;

    @BindView(R.id.search_input_box)
    public EditText mSearchInputBox;

    @BindView(R.id.search_btn)
    public TextView mSearchBtn;

    private LinearItemContentAdapter mSearchResultAdapter;


    @Override
    protected int getRootViewResId() {
        return R.layout.fragment_search;
    }

    @Override
    protected View loadRootView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.fragment_search_layout,container,false);
    }

    protected void initView(View rootView) {

        //?????????????????????
        mSearchResultAdapter = new LinearItemContentAdapter();
        mSearchList.setLayoutManager(new LinearLayoutManager(getContext()));

        mSearchList.setAdapter(mSearchResultAdapter);
        //??????????????????
        mRefreshContainer.setEnableLoadmore(true);
        mRefreshContainer.setEnableRefresh(false);
        mRefreshContainer.setEnableOverScroll(true);
        setUpState(State.SUCCESS);
        mSearchList.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.top = SizeUtils.dip2px(getContext(),1.5f);
                outRect.bottom = SizeUtils.dip2px(getContext(),1.5f);
            }
        });
    }

    @Override
    protected void initPresenter() {
        mSearchPresenter = PresenterManger.getSearchInstance();
        mSearchPresenter.registerViewCallback(this);
        //?????????????????????
        mSearchPresenter.getRecommendWords();
        mSearchPresenter.getHistories();
    }

    @Override
    protected void initListener() {
        mHistoryView.setOnFlowTextItemClickListener(this);
        mRecommendView.setOnFlowTextItemClickListener(this);
        //????????????
        mSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //?????????????????????
                //????????????????????????????????????
                if (hasInput(false)) {
                    //????????????
                    if (mSearchPresenter != null) {
                        //mSearchPresenter.doSearch(mSearchInputBox.getText().toString().trim());
                        toSearch(mSearchInputBox.getText().toString().trim());
                        KeyBoardUtil.hide(getContext(),v);
                    }
                } else {
                    //????????????
                    KeyBoardUtil.hide(getContext(),v);

                }
            }
        });
        //???????????????????????????
        mCleanInputBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSearchInputBox.setText("");
                //????????????????????????
                switchToHistoryPage();
            }
        });

        //??????????????????????????????
        mSearchInputBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //?????????????????????
                //LogUtils.d(this,"input fragment -- >" + s.toString().trim());
                //??????????????????0???????????????????????????
                //????????????

                mCleanInputBtn.setVisibility(hasInput(true) ? View.VISIBLE : View.GONE);
                mSearchBtn.setText(hasInput(false) ? "??????" : "??????");

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mSearchInputBox.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                LogUtils.d(this,"actionId -- >" + actionId);
                if (actionId == EditorInfo.IME_ACTION_SEARCH && mSearchPresenter != null) {
                    //?????????????????????????????????
                    String keyword = v.getText().toString().trim();
                    if (TextUtils.isEmpty(keyword)) {
                        return false;
                    }
                    //????????????
                    toSearch(keyword);
                    //mSearchPresenter.doSearch(keyword);
                }
                return false;
            }
        });
        mHistoryDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //????????????
                mSearchPresenter.delHistories();
            }
        });
        mRefreshContainer.setOnRefreshListener(new RefreshListenerAdapter() {
            @Override
            public void onLoadMore(TwinklingRefreshLayout refreshLayout) {
                //?????????????????????
                if (mSearchPresenter != null) {
                    mSearchPresenter.loaderMore();
                }
            }
        });

        mSearchResultAdapter.setOnListItemClickListener(new LinearItemContentAdapter.OnListItemClickListener() {
            @Override
            public void onItemClick(IBaseInfo item) {
                //????????????????????????
                TicketUtils.toTicketPage(getContext(),item);
            }
        });
    }

    /**
     * ??????????????????????????????
     */
    private void switchToHistoryPage() {
        if (mSearchPresenter != null) {
            mSearchPresenter.getHistories();
        }

        if (mRecommendView.getContentSize() != 0) {
            mRecommendContainer.setVisibility(View.VISIBLE);
        } else {
            mRecommendContainer.setVisibility(View.GONE);
        }
        //???????????????
        mRefreshContainer.setVisibility(View.GONE);

    }

    private boolean hasInput(boolean containSpace){
        if (containSpace) {
            return mSearchInputBox.getText().toString().length() > 0;
        } else {
            return mSearchInputBox.getText().toString().trim().length() > 0;
        }
    }



    @Override
    protected void onRetryClick() {
        //????????????
        if (mSearchPresenter != null) {
            mSearchPresenter.research();
        }
    }

    @Override
    protected void release() {
        super.release();
        mSearchPresenter.unRegisterViewCallback(this);
    }

    @Override
    public void onError() {
        setUpState(State.ERROR);
    }

    @Override
    public void onLoading() {
        setUpState(State.LOADING);
    }

    @Override
    public void onEmpty() {
        setUpState(State.EMPTY);
    }

    @Override
    public void onHistoriesLoaded(Histories histories) {
        LogUtils.d(this,"histories -- >" + histories);
        if (histories == null || histories.getHistories().size() == 0) {
            mHistoryContainer.setVisibility(View.GONE);
        } else {
            mHistoryView.setTextList(histories.getHistories());
            mHistoryContainer.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onHistoriesDeleted() {
        //??????????????????
        if (mSearchPresenter != null) {
            mSearchPresenter.getHistories();
        }

    }

    @Override
    public void onSearchSuccess(SearchResult result) {
        setUpState(State.SUCCESS);
        //LogUtils.d(this,"result -- > " + result);
        //??????????????????????????????
        mRecommendContainer.setVisibility(View.GONE);
        mHistoryContainer.setVisibility(View.GONE);
        //??????????????????
        mRefreshContainer.setVisibility(View.VISIBLE);
        //??????????????????
        LogUtils.d(this,"SearchResult res -- > " + result);
        try {
            mSearchResultAdapter.setData(result.getData()
                    .getTbk_dg_material_optional_response()
                    .getResult_list()
                    .getMap_data());
        } catch (Exception e){
            e.printStackTrace();
            //???????????????????????????
            setUpState(State.EMPTY);
        }


    }

    @Override
    public void onMoreLoaded(SearchResult result) {
        mRefreshContainer.finishLoadmore();
        //????????????????????????
        //???????????????????????????????????????????????????
        List<SearchResult.DataBean.TbkDgMaterialOptionalResponseBean.ResultListBean.MapDataBean> moreData
                = result.getData().getTbk_dg_material_optional_response().getResult_list().getMap_data();
        mSearchResultAdapter.addData(moreData);
        //????????????????????????????????????
        ToastUtils.showToast("????????????" + moreData.size() + "?????????");
    }

    @Override
    public void onMoreLoadedError() {
        ToastUtils.showToast("??????????????????????????????");
        mRefreshContainer.finishLoadmore();
    }

    @Override
    public void onMoreLoadedEmpty() {
        ToastUtils.showToast("??????????????????");
        mRefreshContainer.finishLoadmore();
    }

    @Override
    public void onRecommendWordsLoaded(List<SearchRecommend.DataBean> recommendWords) {
        LogUtils.d(this,"recommendWords size -- >" + recommendWords.size());
        List<String> recommendKeyWords = new ArrayList<>();
        for (SearchRecommend.DataBean item : recommendWords) {
            recommendKeyWords.add(item.getKeyword());
        }
        if (recommendWords == null || recommendKeyWords.size() == 0) {
   //         mRecommendContainer.setVisibility(View.GONE);
        } else {
            mRecommendView.setTextList(recommendKeyWords);
            mRecommendContainer.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onFlowItemClick(String text) {
        //????????????
        toSearch(text);
    }

    private void toSearch(String text){
        if (mSearchPresenter != null) {
            mSearchList.scrollToPosition(0);
            mSearchInputBox.setText(text);
            mSearchInputBox.setFocusable(true);
            mSearchInputBox.setSelection(text.length(),text.length());
            mSearchPresenter.doSearch(text);
        }
    }

}