package com.sunofbeaches.taobaounion.utils;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.sunofbeaches.taobaounion.model.domain.IBaseInfo;
import com.sunofbeaches.taobaounion.presenter.ITicketPresenter;
import com.sunofbeaches.taobaounion.ui.activity.TicketActivity;

public class TicketUtils {

    public static void toTicketPage(Context context,IBaseInfo baseInfo){
        //处理数据
        //拿到ticketPresenter去加载数据
        String title = baseInfo.getTitle();
        String url = baseInfo.getUrl();
        if (TextUtils.isEmpty(url)) {
            url = baseInfo.getUrl();
        }
        String cover = baseInfo.getCover();
        ITicketPresenter ticketPresenter = PresenterManger.getTicketInstance();
        ticketPresenter.getTicket(title,url,cover);
        context.startActivity(new Intent(context, TicketActivity.class));
    }

}
