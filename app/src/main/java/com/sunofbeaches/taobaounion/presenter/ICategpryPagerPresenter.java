package com.sunofbeaches.taobaounion.presenter;

import com.sunofbeaches.taobaounion.base.IBasePresenter;
import com.sunofbeaches.taobaounion.view.ICategoryPagerCallback;

public interface ICategpryPagerPresenter extends IBasePresenter<ICategoryPagerCallback> {
    /**
     * 数据分类Id去获取内容
     * @param categoryId
     */
    void getContentByCategoryId(int categoryId);


    void loaderMore(int categoryId);

    void reload(int categoryId);

}
