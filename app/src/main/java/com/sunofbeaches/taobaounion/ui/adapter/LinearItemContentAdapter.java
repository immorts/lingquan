package com.sunofbeaches.taobaounion.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.sunofbeaches.taobaounion.R;
import com.sunofbeaches.taobaounion.model.domain.IBaseInfo;
import com.sunofbeaches.taobaounion.model.domain.ILinearItemInfo;
import com.sunofbeaches.taobaounion.utils.UrlUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LinearItemContentAdapter extends RecyclerView.Adapter<LinearItemContentAdapter.InnerHolder> {

    List<ILinearItemInfo> mData = new ArrayList<>();
    private OnListItemClickListener mItemClickListener = null;

    @NonNull
    @Override
    public LinearItemContentAdapter.InnerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        LogUtils.d(this,"onCreateViewHolder");
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_home_pager_content,parent,false);
        return new InnerHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull LinearItemContentAdapter.InnerHolder holder, @SuppressLint("RecyclerView") int position) {
//        LogUtils.d(this,"onBindViewHolder" + position);
        ILinearItemInfo dataBean = mData.get(position);
        //设置数据
        holder.setData(dataBean);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItemClickListener != null) {
                    ILinearItemInfo item = mData.get(position);
                    mItemClickListener.onItemClick(item);
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void setData(List<? extends ILinearItemInfo> contents) {
        mData.clear();
        mData.addAll(contents);
        notifyDataSetChanged();
    }

    public void addData(List<? extends ILinearItemInfo> contents) {
        //添加之前拿到原来的size
        int oldSize = mData.size();
        mData.addAll(contents);
        //更新UI
        notifyItemRangeChanged(oldSize,contents.size());

    }

    public class InnerHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.goods_title)
        public TextView title;

        @BindView(R.id.goods_off_prise)
        public TextView offPriseTV;

        @BindView(R.id.goods_cover)
        public ImageView cover;

        @BindView(R.id.goods_after_off_prise)
        public TextView finalPriseTV;

        @BindView(R.id.goods_original_prise)
        public TextView originalPriseTV;

        @BindView(R.id.goods_sell_count)
        public TextView sellCountTV;

        public InnerHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }

        public void setData(ILinearItemInfo dataBean) {
            Context context = itemView.getContext();

            title.setText(dataBean.getTitle());
//            LogUtils.d(this,"url == > " + dataBean.getPict_url());
//            ViewGroup.LayoutParams layoutParams = cover.getLayoutParams();
//            int width = layoutParams.width;
//            int height = layoutParams.height;
//            int coverSize = (Math.max(width, height)) / 2;
            String coverPath = UrlUtils.getCoverPath(dataBean.getCover());
            Glide.with(context).load(coverPath).into(cover);

            String finalPrise = dataBean.getFinalPrise();
            long couponAmount = dataBean.getCouponAmount();
            float resultPrise = Float.parseFloat(finalPrise) - couponAmount;
            finalPriseTV.setText(String.format("%.2f",resultPrise));
            offPriseTV.setText(String.format(context.getString(R.string.text_goods_off_prise),dataBean.getCouponAmount()));
            originalPriseTV.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            originalPriseTV.setText(String.format(context.getString(R.string.text_goods_original_prise),finalPrise));
            sellCountTV.setText(String.format(context.getString(R.string.text_goods_sell_count),dataBean.getVolume()));
        }
    }

    public void setOnListItemClickListener(OnListItemClickListener listener){
        this.mItemClickListener = listener;
    }



    public interface OnListItemClickListener {
        void onItemClick(IBaseInfo item);
    }

}
