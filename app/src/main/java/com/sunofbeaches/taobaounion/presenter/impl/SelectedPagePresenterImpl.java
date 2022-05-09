package com.sunofbeaches.taobaounion.presenter.impl;

import com.sunofbeaches.taobaounion.model.Api;
import com.sunofbeaches.taobaounion.model.domain.SelectedContent;
import com.sunofbeaches.taobaounion.model.domain.SelectedPageCategories;
import com.sunofbeaches.taobaounion.presenter.ISelectedPagePresenter;
import com.sunofbeaches.taobaounion.utils.LogUtils;
import com.sunofbeaches.taobaounion.utils.RetrofitManager;
import com.sunofbeaches.taobaounion.utils.UrlUtils;
import com.sunofbeaches.taobaounion.view.ISelectedPageCallback;

import java.net.HttpURLConnection;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SelectedPagePresenterImpl implements ISelectedPagePresenter {
    private ISelectedPageCallback mViewCallback = null;
    private final Api mApi;


    public SelectedPagePresenterImpl(){
        Retrofit retrofit = RetrofitManager.getInstance().getRetrofit();
        mApi = retrofit.create(Api.class);
    }

    @Override
    public void registerViewCallback(ISelectedPageCallback callback) {
        this.mViewCallback = callback;
    }

    @Override
    public void unRegisterViewCallback(ISelectedPageCallback callback) {
        this.mViewCallback = null;
    }

    @Override
    public void getCategories() {
        if (mViewCallback != null) {
            mViewCallback.onLoading();
        }
        //拿retrofit
        Call<SelectedPageCategories> task = mApi.getSelectedPageCategories();
        task.enqueue(new Callback<SelectedPageCategories>() {
            @Override
            public void onResponse(Call<SelectedPageCategories> call, Response<SelectedPageCategories> response) {
                int resultCode = response.code();
                LogUtils.d(this,"resultCode -- >" + resultCode);
                if (resultCode == HttpURLConnection.HTTP_OK) {
                    SelectedPageCategories result = response.body();
                    //TODO:通知UI加载更新
                    if (mViewCallback != null) {
                        mViewCallback.onCategoriesLoaded(result);
                    }

                } else {
                    onLoadedError();
                }

            }

            @Override
            public void onFailure(Call<SelectedPageCategories> call, Throwable t) {
                onLoadedError();
            }
        });
    }

    private void onLoadedError(){
        if (mViewCallback != null) {
            mViewCallback.onError();
        }
    }



    @Override
    public void getContentByCategory(SelectedPageCategories.DataBean item) {
        LogUtils.d(this,"categoryId -- >" + item.getFavorites_id());
        String targetUrl = UrlUtils.getSelectedPageContentUrl(item.getFavorites_id());
        LogUtils.d(this,"targetUrl -- >" + targetUrl);
        Call<SelectedContent> task = mApi.getSelectedPageContent(targetUrl);
        task.enqueue(new Callback<SelectedContent>() {
            @Override
            public void onResponse(Call<SelectedContent> call, Response<SelectedContent> response) {
                int resultCode = response.code();
                LogUtils.d(SelectedPagePresenterImpl.this,"getContentByCategory result code -- >" + resultCode);
                if (resultCode == HttpURLConnection.HTTP_OK) {
                    SelectedContent result = response.body();
                    if (mViewCallback != null) {
                        mViewCallback.onContentLoaded(result);
                    }
                } else {
                    onLoadedError();
                }
            }

            @Override
            public void onFailure(Call<SelectedContent> call, Throwable t) {
                onLoadedError();
            }
        });
    }

    @Override
    public void reloadContent() {
        this.getCategories();
    }
}
