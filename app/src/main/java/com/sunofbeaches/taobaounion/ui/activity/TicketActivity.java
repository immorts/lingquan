package com.sunofbeaches.taobaounion.ui.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sunofbeaches.taobaounion.R;
import com.sunofbeaches.taobaounion.base.BaseActivity;
import com.sunofbeaches.taobaounion.model.domain.TicketResult;
import com.sunofbeaches.taobaounion.presenter.ITicketPresenter;
import com.sunofbeaches.taobaounion.utils.LogUtils;
import com.sunofbeaches.taobaounion.utils.PresenterManger;
import com.sunofbeaches.taobaounion.utils.ToastUtils;
import com.sunofbeaches.taobaounion.utils.UrlUtils;
import com.sunofbeaches.taobaounion.view.ITicketPagerCallback;

import butterknife.BindView;

public class TicketActivity extends BaseActivity implements ITicketPagerCallback {

    private ITicketPresenter mTicketPresenter;

    private boolean mHasTaobaoApp = false;

    @BindView(R.id.ticket_cover)
    public ImageView mCover;

    @BindView(R.id.ticket_code)
    public EditText mTicketCode;

    @BindView(R.id.ticket_copy_or_open_btn)
    public TextView mOpenOrCopyBtn;

    @BindView(R.id.ticket_back_press)
    public ImageView backPress;

    @BindView(R.id.ticket_cover_loading)
    public View loadingView;

    @BindView(R.id.ticket_load_retry)
    public View retryLoadText;


    @Override
    protected void initPresenter(){
        mTicketPresenter = PresenterManger.getTicketInstance();
        if (mTicketPresenter!=null) {
            mTicketPresenter.registerViewCallback(this);
        }
        //判断是否有安装淘宝
        //淘宝的包名：com.taobao.taobao
        //检查是否安装有淘宝应用
        PackageManager pm = getPackageManager();
        try {
            PackageInfo packageInfo = pm.getPackageInfo("com.taobao.taobao", PackageManager.MATCH_UNINSTALLED_PACKAGES);
            mHasTaobaoApp = packageInfo != null;
        } catch (PackageManager.NameNotFoundException e){
            e.printStackTrace();
            mHasTaobaoApp = false;
        }
        LogUtils.d(this,"mHasTaobaoApp -- > " + mHasTaobaoApp);
        mOpenOrCopyBtn.setText(mHasTaobaoApp ? "打开淘宝领券" : "复制淘口令");

    }

    @Override
    protected void release() {
        if (mTicketPresenter != null) {
            mTicketPresenter.unRegisterViewCallback(this);
        }
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initEvent() {
        backPress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mOpenOrCopyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //复制淘口令
                //拿到内容
                String ticketCode = mTicketCode.getText().toString().trim();
                ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                //复制到粘贴板
                ClipData clipData = ClipData.newPlainText("sob_taobao_ticket_code", ticketCode);

                cm.setPrimaryClip(clipData);
                //判断有没有淘宝
                if (mHasTaobaoApp) {
                    //如果有就打开淘宝
                    Intent taobaoIntent = new Intent();
//                    taobaoIntent.setAction("android.intent.action.MAIN");
//                    taobaoIntent.addCategory("android.intent.category.LAUNCHER");
                    ComponentName componentName = new ComponentName("com.taobao.taobao","com.taobao.tao.TBMainActivity");
                    taobaoIntent.setComponent(componentName);

                    startActivity(taobaoIntent);
                } else{
                    //没有就显示复制成功'
                    ToastUtils.showToast("已经复制，粘贴分享，或打开淘宝");
                }

            }
        });
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_ticket;
    }

    @Override
    public void onError() {
        if (loadingView != null) {
            loadingView.setVisibility(View.GONE);
        }
        if (retryLoadText != null) {
            retryLoadText.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onLoading() {
        if (retryLoadText != null) {
            retryLoadText.setVisibility(View.GONE);
        }
        if (loadingView != null) {
            loadingView.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onEmpty() {

    }

    @Override
    public void onTicketLoaded(String cover, TicketResult result) {

        //LogUtils.d(this,"cover -- >" + cover + "result -- >" + result);
        if (mCover != null && !TextUtils.isEmpty(cover)) {
            ViewGroup.LayoutParams layoutParams = mCover.getLayoutParams();
            int targetWidth = layoutParams.width / 2;
            String coverPath = UrlUtils.getCoverPath(cover);
            LogUtils.d(this,"coverPath -- > " + coverPath);
            Glide.with(this).load(coverPath).into(mCover);
        }
        if (TextUtils.isEmpty(cover)) {
            mCover.setImageResource(R.mipmap.home_normal);
        }


 //       LogUtils.d(this,"result -- >" + result + "result.getData().getTbk_tpwd_create_response() -- > "+result.getData().getTbk_tpwd_create_response().getData().getModel());
        //设置一下code
        if(result != null && result.getData().getTbk_tpwd_create_response() != null){
            mTicketCode.setText(result.getData().getTbk_tpwd_create_response().getData().getModel().substring(2,15));
        }
        if (loadingView != null) {
            loadingView.setVisibility(View.GONE);
        }


    }
}
