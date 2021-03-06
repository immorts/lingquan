package com.sunofbeaches.taobaounion.presenter;

import com.sunofbeaches.taobaounion.base.IBasePresenter;
import com.sunofbeaches.taobaounion.view.IOnSellPageCallback;

public interface IOnSellPagePresenter extends IBasePresenter<IOnSellPageCallback> {

    /**
     * 加载特惠内容
     */
    void getOnSellContent();

    /**
     * 重新加载内容
     * @Call 网络不佳的时候调用
     */
    void reLoad();

    /**
     * 加载更多特惠内容
     */
    void loaderMore();

}
