package com.sunofbeaches.taobaounion.base;

import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public abstract class BaseActivity extends AppCompatActivity {

    private Unbinder mBind;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResId());
//        ColorMatrix cm = new ColorMatrix();
//        cm.setSaturation(0);
//        Paint paint = new Paint();
//        paint.setColorFilter(new ColorMatrixColorFilter(cm));
//        View contentContainer = getWindow().getDecorView();
//        contentContainer.setLayerType(View.LAYER_TYPE_SOFTWARE,paint);
        mBind = ButterKnife.bind(this);
        initView();
        initEvent();
        try {
            initPresenter();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    protected abstract void initPresenter() throws PackageManager.NameNotFoundException;

    /**
     * 需要的时候覆写
     */
    protected void initEvent() {
    }

    protected abstract void initView();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBind!=null) {
            mBind.unbind();
        }
        this.release();
    }

    /**
     * 子类需要释放资源，覆盖即可
     */
    protected void release(){

    }

    protected abstract int getLayoutResId();
}
