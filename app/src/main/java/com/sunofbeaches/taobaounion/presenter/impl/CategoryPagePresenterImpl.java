package com.sunofbeaches.taobaounion.presenter.impl;

import com.sunofbeaches.taobaounion.model.Api;
import com.sunofbeaches.taobaounion.model.domain.HomePagerContent;
import com.sunofbeaches.taobaounion.presenter.ICategpryPagerPresenter;
import com.sunofbeaches.taobaounion.utils.LogUtils;
import com.sunofbeaches.taobaounion.utils.RetrofitManager;
import com.sunofbeaches.taobaounion.utils.UrlUtils;
import com.sunofbeaches.taobaounion.view.ICategoryPagerCallback;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class CategoryPagePresenterImpl implements ICategpryPagerPresenter {

    private Map<Integer,Integer> pagesInfo = new HashMap<>();

    public static final int DEFAULT_PAGE = 1;
    private Integer mCurrentPage;


    @Override
    public void getContentByCategoryId(int categoryId) {
        for (ICategoryPagerCallback callback : callbacks) {
            if (callback.getCategoryId() == categoryId) {
                callback.onLoading();
            }
        }
        //根据分类Id加载内容，

        Integer targetPage = pagesInfo.get(categoryId);
        if (targetPage == null) {
            targetPage = DEFAULT_PAGE;
            pagesInfo.put(categoryId,DEFAULT_PAGE);
        }
        Call<HomePagerContent> task = createTask(categoryId, targetPage);
        task.enqueue(new Callback<HomePagerContent>() {
            @Override
            public void onResponse(Call<HomePagerContent> call, Response<HomePagerContent> response) {
                LogUtils.d(this,"current thread ========= > " + Thread.currentThread().getName());
                int code = response.code();
                LogUtils.d(CategoryPagePresenterImpl.this,"code -- > " + code);
                if(code == HttpURLConnection.HTTP_OK){
                    HomePagerContent pagerContent = response.body();
                    LogUtils.d(CategoryPagePresenterImpl.this,"pageContent -- >" + pagerContent);
                    //把数据给更新ui
                    handleHomePageContentResult(pagerContent,categoryId);
                } else {
                    //TODO:
                    handleNetworkError(categoryId);
                }
            }

            @Override
            public void onFailure(Call<HomePagerContent> call, Throwable t) {
                LogUtils.d(CategoryPagePresenterImpl.this,"on Failure -- > " + t.toString());
                handleNetworkError(categoryId);
            }
        });

    }

    private Call<HomePagerContent> createTask(int categoryId, Integer targetPage) {
        String homePagerUrl = UrlUtils.createHomePagerUrl(categoryId, targetPage);
        LogUtils.d(this,"home pager url -- > " + homePagerUrl);
        Retrofit retrofit = RetrofitManager.getInstance().getRetrofit();
        Api api = retrofit.create(Api.class);
        Call<HomePagerContent> task = api.getHomePagerContent(homePagerUrl);
        return task;
    }

    private void handleNetworkError(int categoryId) {
        for(ICategoryPagerCallback callback : callbacks){
            if (callback.getCategoryId() == categoryId) {
                callback.onError();
            }
        }
    }

    private void handleHomePageContentResult(HomePagerContent pagerContent, int categoryId) {
        //通知ui层更新数据
        List<HomePagerContent.DataBean> data = pagerContent.getData();
        for(ICategoryPagerCallback callback : callbacks){
            if (callback.getCategoryId() == categoryId) {
                if (pagerContent == null || pagerContent.getData().size() == 0) {
                    callback.onEmpty();
                } else {
                    List<HomePagerContent.DataBean> looperData = data.subList(data.size() - 5, data.size());
                    callback.onLooperListLoaded(looperData);
                    callback.onContentLoaded(pagerContent.getData());
                }
            }
        }
    }

    @Override
    public void loaderMore(int categoryId) {
        //加载更多的数据
        //1.拿到更多的页面
        mCurrentPage = this.pagesInfo.get(categoryId);
        if (pagesInfo == null) {
            mCurrentPage = 1;
        }
        //2.页码++
        mCurrentPage++;
        //3.加载数据
        Call<HomePagerContent> task = createTask(categoryId, mCurrentPage);
        //4。处理数据结果
        task.enqueue(new Callback<HomePagerContent>() {
            @Override
            public void onResponse(Call<HomePagerContent> call, Response<HomePagerContent> response) {
                //结果
                int code = response.code();
                LogUtils.d(CategoryPagePresenterImpl.this,"result code -- >" + code);
                if (code == HttpURLConnection.HTTP_OK) {
                    HomePagerContent result = response.body();
                    LogUtils.d(CategoryPagePresenterImpl.this,"result -- >" + result.toString());
                    handleLoaderResult(result,categoryId);
                } else {
                    handleLoaderMoreError(categoryId);
                }
            }

            @Override
            public void onFailure(Call<HomePagerContent> call, Throwable t) {
                //请求失败
                LogUtils.d(CategoryPagePresenterImpl.this,t.toString());
                handleLoaderMoreError(categoryId);
            }
        });
    }

    private void handleLoaderResult(HomePagerContent result, int categoryId) {
        for (ICategoryPagerCallback callback : callbacks) {
            if (callback.getCategoryId() == categoryId) {
                if (result == null || result.getData().size() == 0) {
                    callback.onLoaderMoreEmpty();
                } else {
                    callback.onLoaderMoreLoaded(result.getData());
                }
            }
        }
    }

    private void handleLoaderMoreError(int categoryId) {
        mCurrentPage--;
        pagesInfo.put(categoryId,mCurrentPage);
        for (ICategoryPagerCallback callback : callbacks) {
            if (callback.getCategoryId() == categoryId) {
                callback.onLoaderMoreError();
            }
        }
    }

    @Override
    public void reload(int categoryId) {

    }

    private List<ICategoryPagerCallback> callbacks = new ArrayList<>();

    @Override
    public void registerViewCallback(ICategoryPagerCallback callback) {
        if (!callbacks.contains(callback)) {
            callbacks.add(callback);
        }

    }

    @Override
    public void unRegisterViewCallback(ICategoryPagerCallback callback) {

    }
}
