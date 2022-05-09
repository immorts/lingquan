package com.sunofbeaches.taobaounion.presenter.impl;

import com.sunofbeaches.taobaounion.model.Api;
import com.sunofbeaches.taobaounion.model.domain.Histories;
import com.sunofbeaches.taobaounion.model.domain.SearchRecommend;
import com.sunofbeaches.taobaounion.model.domain.SearchResult;
import com.sunofbeaches.taobaounion.presenter.ISearchPresenter;
import com.sunofbeaches.taobaounion.utils.JsonCacheUtils;
import com.sunofbeaches.taobaounion.utils.LogUtils;
import com.sunofbeaches.taobaounion.utils.RetrofitManager;
import com.sunofbeaches.taobaounion.view.ISearchPageCallback;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SearchPresenterImpl implements ISearchPresenter {

    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_HISTORIES_SIZE = 10;
    private final Api mApi;
    private ISearchPageCallback mSearchPageCallback = null;

    /**
     * 搜索的当前页码
     */
    private int mCurrentPage = DEFAULT_PAGE;
    private String mCurrentKeyWord = null;
    private JsonCacheUtils mJsonCacheUtils = null;


    public SearchPresenterImpl(){
        RetrofitManager instance = RetrofitManager.getInstance();
        Retrofit retrofit = instance.getRetrofit();
        mApi = retrofit.create(Api.class);
        mJsonCacheUtils = JsonCacheUtils.getInstance();
    }


    @Override
    public void registerViewCallback(ISearchPageCallback callback) {
        this.mSearchPageCallback = callback;
    }

    @Override
    public void unRegisterViewCallback(ISearchPageCallback callback) {
        this.mSearchPageCallback = null;
    }

    @Override
    public void getHistories() {
        Histories histories = mJsonCacheUtils.getValue(KEY_HISTORIES,Histories.class);
        if (mSearchPageCallback != null ) {
            mSearchPageCallback.onHistoriesLoaded(histories);
        }
    }

    @Override
    public void delHistories() {
        mJsonCacheUtils.delCache(KEY_HISTORIES);
        if (mSearchPageCallback != null) {
            mSearchPageCallback.onHistoriesDeleted();
        }

    }

    public static final String KEY_HISTORIES = "key_histories";


    private int mHistoriesMaxSize = DEFAULT_HISTORIES_SIZE;

    /**
     * 添加历史记录
     * @param history
     */
    private void saveHistories(String history){
        Histories histories = mJsonCacheUtils.getValue(KEY_HISTORIES,Histories.class);
        //如果已经存在了，就干掉然后再添加
        List<String> historiesList = null;
        if (histories != null && histories.getHistories() != null) {
            historiesList = histories.getHistories();
            if (historiesList.contains(history)) {
                historiesList.remove(history);
            }
        }
        //去重完成
        //处理没有数据的情况
        if (historiesList == null) {
            historiesList = new ArrayList<>();
        }
        if(histories == null){
            histories = new Histories();
            histories.setHistories(historiesList);
        }
        //对个数进行限制
        if (historiesList.size() > mHistoriesMaxSize) {
            historiesList = historiesList.subList(0,mHistoriesMaxSize);
        }
        //添加记录
        historiesList.add(history);
        //保存记录
        mJsonCacheUtils.saveCache(KEY_HISTORIES,histories);

    }

    @Override
    public void doSearch(String keyword) {
        if(mCurrentKeyWord == null || !mCurrentKeyWord.equals(keyword)){
            this.saveHistories(keyword);
            this.mCurrentKeyWord = keyword;
        }
        //更新UI状态
        if (mSearchPageCallback != null) {
            mSearchPageCallback.onLoading();
        }
        Call<SearchResult> task = mApi.doRearch(mCurrentPage, keyword);
        task.enqueue(new Callback<SearchResult>() {
            @Override
            public void onResponse(Call<SearchResult> call, Response<SearchResult> response) {
                int code = response.code();
                LogUtils.d(this,"do search result code -- >" + code);
                if (code == HttpURLConnection.HTTP_OK) {
                    handleSearchresult(response.body());
                } else {
                    onError();
                }

            }

            @Override
            public void onFailure(Call<SearchResult> call, Throwable t) {
                t.printStackTrace();
                onError();
            }
        });
    }

    private void onError() {
        if (mSearchPageCallback != null) {
            mSearchPageCallback.onError();
        }
    }

    private void handleSearchresult(SearchResult body) {
        if (mSearchPageCallback != null) {
            if (isResultEmpty(body)) {
                //数据为空
                mSearchPageCallback.onEmpty();
            } else {
                mSearchPageCallback.onSearchSuccess(body);
            }
        }

    }

    private boolean isResultEmpty(SearchResult result){
        try{
            return result == null || result.getData().getTbk_dg_material_optional_response().getResult_list().getMap_data().size() == 0;
        } catch (Exception e){
            return false;
        }
    }


    @Override
    public void research() {
        if (mCurrentKeyWord != null) {
            mSearchPageCallback.onEmpty();
        } else {
            //可以重新搜索
            this.doSearch(mCurrentKeyWord);
        }
    }

    @Override
    public void loaderMore() {
        mCurrentPage++;
        //进行搜索
        if (mCurrentKeyWord == null) {
            if (mSearchPageCallback != null) {
                mSearchPageCallback.onEmpty();
            }
        } else {
            //做搜索的事情
            doSearchMore();
        }
    }

    private void doSearchMore() {
        Call<SearchResult> task = mApi.doRearch(mCurrentPage, mCurrentKeyWord);
        task.enqueue(new Callback<SearchResult>() {
            @Override
            public void onResponse(Call<SearchResult> call, Response<SearchResult> response) {
                int code = response.code();
                LogUtils.d(this,"do search more result code -- >" + code);
                if (code == HttpURLConnection.HTTP_OK) {
                    handleMoreSearchresult(response.body());
                } else {
                    onLoadedMoreError();
                }

            }

            @Override
            public void onFailure(Call<SearchResult> call, Throwable t) {
                t.printStackTrace();
                onLoadedMoreError();
            }
        });
    }

    /**
     * 处理加载更多的结果
     * @param body
     */
    private void handleMoreSearchresult(SearchResult body) {
        if (mSearchPageCallback != null) {
            if (isResultEmpty(body)) {
                //数据为空
                mSearchPageCallback.onMoreLoadedEmpty();
            } else {
                mSearchPageCallback.onMoreLoaded(body);
            }
        }
    }


    /**
     * 加载更多内容失败
     */
    private void onLoadedMoreError() {
        mCurrentPage--;
        if (mSearchPageCallback != null) {
            mSearchPageCallback.onMoreLoadedError();
        }
    }

    @Override
    public void getRecommendWords() {
        Call<SearchRecommend> task = mApi.getRecommendWords();
        task.enqueue(new Callback<SearchRecommend>() {
            @Override
            public void onResponse(Call<SearchRecommend> call, Response<SearchRecommend> response) {
                int code = response.code();
                LogUtils.d(this,"getRecommend result code -- > " + code);
                if (code == HttpURLConnection.HTTP_OK) {
                    //处理结果
                    if (mSearchPageCallback != null) {
                        mSearchPageCallback.onRecommendWordsLoaded(response.body().getData());
                    }

                } else {

                }
            }

            @Override
            public void onFailure(Call<SearchRecommend> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }
}
