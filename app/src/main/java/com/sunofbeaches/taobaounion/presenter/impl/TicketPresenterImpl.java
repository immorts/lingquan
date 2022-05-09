package com.sunofbeaches.taobaounion.presenter.impl;

import com.sunofbeaches.taobaounion.model.Api;
import com.sunofbeaches.taobaounion.model.domain.TicketParams;
import com.sunofbeaches.taobaounion.model.domain.TicketResult;
import com.sunofbeaches.taobaounion.presenter.ITicketPresenter;
import com.sunofbeaches.taobaounion.utils.LogUtils;
import com.sunofbeaches.taobaounion.utils.RetrofitManager;
import com.sunofbeaches.taobaounion.utils.UrlUtils;
import com.sunofbeaches.taobaounion.view.ITicketPagerCallback;

import java.net.HttpURLConnection;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class TicketPresenterImpl implements ITicketPresenter {

    private ITicketPagerCallback mViewCallback = null;
    private String mCover;
    private TicketResult mTicketResult;

    enum LoadState {
        LOADING,SUCCESS,ERROR,NONE
    }

    private LoadState mCurrentState = LoadState.NONE;

    /**
     * 去获取淘宝口令
     * @param title
     * @param url
     * @param cover
     */
    @Override
    public void getTicket(String title, String url, String cover) {

        this.onTicketLoading();
        LogUtils.d(this,"title -- >" + title);
        LogUtils.d(this,"url -- >" + url);
        LogUtils.d(this,"cover -- >" + cover);
        mCover = cover;
        String ticketUrl = UrlUtils.getTicketUrl(url);
        Retrofit retrofit = RetrofitManager.getInstance().getRetrofit();
        Api api = retrofit.create(Api.class);
        TicketParams ticketParams = new TicketParams(ticketUrl,title);
        Call<TicketResult> task = api.getTicket(ticketParams);
        task.enqueue(new Callback<TicketResult>() {
            @Override
            public void onResponse(Call<TicketResult> call, Response<TicketResult> response) {
                int code = response.code();
                LogUtils.d(this,"result code -- > " + code);
                if (code == HttpURLConnection.HTTP_OK) {
                    //请求成功
                    mTicketResult = response.body();
                    LogUtils.d(this,"result -- >" + mTicketResult.toString());
                    //通知UI更新
                    onLoadTicketSuccess();
                } else {
                    //请求失败
                    onLoadedTicketError();

                }

            }

            @Override
            public void onFailure(Call<TicketResult> call, Throwable t) {
                onLoadedTicketError();
            }
        });

    }

    private void onLoadTicketSuccess(){
        if (mViewCallback != null) {
            mViewCallback.onTicketLoaded(mCover, mTicketResult);
        } else {
            mCurrentState = LoadState.SUCCESS;
        }
    }

    private void onLoadedTicketError(){
        if (mViewCallback!=null) {
            mViewCallback.onError();
        } else {
            mCurrentState = LoadState.ERROR;
        }
    }


    @Override
    public void registerViewCallback(ITicketPagerCallback callback) {
        this.mViewCallback = callback;
        if(mCurrentState != LoadState.NONE){
            //说明状态已经改变了，需要更新UI
            if(mCurrentState == LoadState.SUCCESS){
                onLoadTicketSuccess();
            } else if(mCurrentState == LoadState.ERROR){
                onLoadedTicketError();
            } else if(mCurrentState == LoadState.LOADING){
                onTicketLoading();
            }
        }
    }

    private void onTicketLoading() {
        if (mViewCallback != null) {
            mViewCallback.onLoading();
        } else {
            mCurrentState = LoadState.LOADING;
        }
    }

    @Override
    public void unRegisterViewCallback(ITicketPagerCallback callback) {
        this.mViewCallback = null;
    }

}
