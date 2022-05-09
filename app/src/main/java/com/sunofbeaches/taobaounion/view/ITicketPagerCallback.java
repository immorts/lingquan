package com.sunofbeaches.taobaounion.view;

import com.sunofbeaches.taobaounion.base.IBaseViewCallback;
import com.sunofbeaches.taobaounion.model.domain.TicketResult;

public interface ITicketPagerCallback extends IBaseViewCallback {

    /**
     * 淘宝口令加载结果
     * @param cover
     * @param result
     */
    void onTicketLoaded(String cover, TicketResult result);

}
