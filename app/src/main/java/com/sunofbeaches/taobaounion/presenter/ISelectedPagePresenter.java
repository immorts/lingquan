package com.sunofbeaches.taobaounion.presenter;

import com.sunofbeaches.taobaounion.base.IBasePresenter;
import com.sunofbeaches.taobaounion.model.domain.SelectedPageCategories;
import com.sunofbeaches.taobaounion.view.ISelectedPageCallback;

public interface ISelectedPagePresenter extends IBasePresenter<ISelectedPageCallback> {

    /**
     * 获取分类
     */
    void getCategories();

    /**
     * 根据分类获取分类内容
     * @param item
     */
    void getContentByCategory(SelectedPageCategories.DataBean item);

    /**
     * 重新加载内容
     */
    void reloadContent();

}
