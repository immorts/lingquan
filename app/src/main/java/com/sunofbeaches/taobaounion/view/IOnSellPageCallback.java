package com.sunofbeaches.taobaounion.view;

import com.sunofbeaches.taobaounion.base.IBaseViewCallback;
import com.sunofbeaches.taobaounion.model.domain.OnSellContent;

public interface IOnSellPageCallback extends IBaseViewCallback {

    /**
     * 特惠内容
     * @param result
     */
    void onContentLoadedSuccess(OnSellContent result);

    /**
     * 加载更多的内容
     */
    void onMoreLoaded(OnSellContent moreResult);

    /**
     * 加载更多失败
     */
    void onMoreLoadedError();

    /**
     * 没有更多内容
     */
    void onMoreLoadedEmpty();

}
