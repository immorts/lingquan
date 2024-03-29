package com.sunofbeaches.taobaounion.ui.adapter;

import android.text.TextUtils;
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
import com.sunofbeaches.taobaounion.model.domain.SelectedContent;
import com.sunofbeaches.taobaounion.utils.Constants;
import com.sunofbeaches.taobaounion.utils.LogUtils;
import com.sunofbeaches.taobaounion.utils.UrlUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SelectedPageContentAdapter extends RecyclerView.Adapter<SelectedPageContentAdapter.InnerHolder> {
    List<SelectedContent.DataBean.TbkDgOptimusMaterialResponseBean.ResultListBean.MapDataBean> mData= new ArrayList<>();
    private onSelectedPageContentItemCLickListener mContentItemClickListener = null;

    @NonNull
    @Override
    public InnerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_selected_page_content, parent, false);
        return new InnerHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull InnerHolder holder, int position) {
        SelectedContent.DataBean.TbkDgOptimusMaterialResponseBean.ResultListBean.MapDataBean itamData = mData.get(position);
        holder.setData(itamData);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mContentItemClickListener != null) {
                    mContentItemClickListener.onContentItamClick(itamData);
                }
            }
        });

    }


    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void setData(SelectedContent content) {
        if (content.getCode() == Constants.SUCCESS_CODE) {
            List<SelectedContent.DataBean.TbkDgOptimusMaterialResponseBean.ResultListBean.MapDataBean> map_data =
                    content.getData().getTbk_dg_optimus_material_response().getResult_list().getMap_data();
            this.mData.clear();
            this.mData.addAll(map_data);
            notifyDataSetChanged();
        }
    }

    public class InnerHolder extends RecyclerView.ViewHolder {

        @BindView((R.id.selected_cover))
        public ImageView cover;

        @BindView((R.id.selected_off_prise))
        public TextView offPriseTv;

        @BindView((R.id.selected_title))
        public TextView title;

        @BindView((R.id.selected_buy_btn))
        public TextView buyBtn;

        @BindView((R.id.selected_original_prise))
        public TextView originalPriseTv;





        public InnerHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);

        }

        public void setData(SelectedContent.DataBean.TbkDgOptimusMaterialResponseBean.ResultListBean.MapDataBean itemData) {
            title.setText(itemData.getTitle());
            String pict_url = itemData.getPict_url();
            String coverPath = UrlUtils.getCoverPath(pict_url);
            LogUtils.d(this,"coverPath -- >" + coverPath);
            Glide.with(itemView.getContext()).load(coverPath).into(cover);

            if (TextUtils.isEmpty(itemData.getCoupon_click_url())) {
                originalPriseTv.setText("晚啦，没有优惠券了");
                buyBtn.setVisibility(View.GONE);
            } else {
                originalPriseTv.setText("原价：" + itemData.getZk_final_price());

            }

            if (TextUtils.isEmpty(itemData.getCoupon_info())) {
                offPriseTv.setVisibility(View.GONE);
            } else {
                offPriseTv.setVisibility(View.VISIBLE);
                offPriseTv.setText(itemData.getCoupon_info());
            }

        }
    }

    public void setOnSelectedPageContentItemCLickListener(onSelectedPageContentItemCLickListener listener){
        this.mContentItemClickListener = listener;
    }


    public interface onSelectedPageContentItemCLickListener{
        void onContentItamClick(IBaseInfo item);
    }

}
