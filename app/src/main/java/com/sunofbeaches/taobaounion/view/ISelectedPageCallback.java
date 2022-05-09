package com.sunofbeaches.taobaounion.view;

import com.sunofbeaches.taobaounion.base.IBaseViewCallback;
import com.sunofbeaches.taobaounion.model.domain.SelectedContent;
import com.sunofbeaches.taobaounion.model.domain.SelectedPageCategories;

public interface ISelectedPageCallback extends IBaseViewCallback {

    /**
     * 分类内容结果
     * @param categories 分类内容
     */
    void onCategoriesLoaded(SelectedPageCategories categories);


    /**
     * 内容
     * @param content
     */
    void onContentLoaded(SelectedContent content);

}
