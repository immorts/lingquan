package com.sunofbeaches.taobaounion.view;

import com.sunofbeaches.taobaounion.base.IBaseViewCallback;
import com.sunofbeaches.taobaounion.model.domain.Categories;

public interface IHomeCallback extends IBaseViewCallback {

    void onCategoriesLoaded(Categories categories);

}
