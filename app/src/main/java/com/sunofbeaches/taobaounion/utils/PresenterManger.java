package com.sunofbeaches.taobaounion.utils;

import com.sunofbeaches.taobaounion.presenter.ICategpryPagerPresenter;
import com.sunofbeaches.taobaounion.presenter.IHomePresenter;
import com.sunofbeaches.taobaounion.presenter.IOnSellPagePresenter;
import com.sunofbeaches.taobaounion.presenter.ISearchPresenter;
import com.sunofbeaches.taobaounion.presenter.ISelectedPagePresenter;
import com.sunofbeaches.taobaounion.presenter.ITicketPresenter;
import com.sunofbeaches.taobaounion.presenter.impl.CategoryPagePresenterImpl;
import com.sunofbeaches.taobaounion.presenter.impl.HomePresenterImpl;
import com.sunofbeaches.taobaounion.presenter.impl.OnSellPagePresenterImpl;
import com.sunofbeaches.taobaounion.presenter.impl.SearchPresenterImpl;
import com.sunofbeaches.taobaounion.presenter.impl.SelectedPagePresenterImpl;
import com.sunofbeaches.taobaounion.presenter.impl.TicketPresenterImpl;

public class PresenterManger {

    private PresenterManger(){
    }

    private static class innerClass{
        private final static PresenterManger mangerInstance= new PresenterManger();
        private final static ICategpryPagerPresenter categoryPresentereInstance = new CategoryPagePresenterImpl();
        private final static IHomePresenter homePresenterInstance = new HomePresenterImpl();
        private final static ITicketPresenter ticketPresenterInstance = new TicketPresenterImpl();
        private final static ISelectedPagePresenter selectedPagePresenter = new SelectedPagePresenterImpl();
        private final static IOnSellPagePresenter onSellPagePresenter = new OnSellPagePresenterImpl();
        private final static ISearchPresenter searchPresenter = new SearchPresenterImpl();
    }

    public static PresenterManger getMangerInstance(){
        return innerClass.mangerInstance;
    }

    public static ICategpryPagerPresenter getCategoryInstance(){
        return innerClass.categoryPresentereInstance;
    }

    public static IHomePresenter getHomeInstance(){
        return innerClass.homePresenterInstance;
    }

    public static ITicketPresenter getTicketInstance(){
        return innerClass.ticketPresenterInstance;
    }

    public static ISelectedPagePresenter getSelectedInstance(){
        return innerClass.selectedPagePresenter;
    }

    public static IOnSellPagePresenter getOnSellInstance(){
        return innerClass.onSellPagePresenter;
    }

    public static ISearchPresenter getSearchInstance(){
        return innerClass.searchPresenter;
    }



}
