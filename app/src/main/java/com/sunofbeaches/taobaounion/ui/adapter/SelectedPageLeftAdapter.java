package com.sunofbeaches.taobaounion.ui.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sunofbeaches.taobaounion.R;
import com.sunofbeaches.taobaounion.model.domain.SelectedPageCategories;

import java.util.ArrayList;
import java.util.List;

public class SelectedPageLeftAdapter extends RecyclerView.Adapter<SelectedPageLeftAdapter.InnerHolder> {

    private List<SelectedPageCategories.DataBean> mData = new ArrayList<>();

    private int mCurrentSelectedPosition = 0;
    private onLeftItemClickListener mItemClickListener = null;

    @NonNull
    @Override
    public InnerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_selected_page_left, parent, false);
        return new InnerHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull InnerHolder holder, @SuppressLint("RecyclerView") int position) {
        TextView itemTv = holder.itemView.findViewById(R.id.left_category_tv);
        if (mCurrentSelectedPosition == position) {
            itemTv.setBackgroundColor(itemTv.getResources().getColor(R.color.colorEFEEEE));
        } else {
            itemTv.setBackgroundColor(itemTv.getResources().getColor(R.color.white));
        }
        SelectedPageCategories.DataBean dataBean = mData.get(position);
        itemTv.setText(dataBean.getFavorites_title());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItemClickListener != null && mCurrentSelectedPosition != position) {
                    //修改当前选中的位置
                    mCurrentSelectedPosition = position;
                    mItemClickListener.onLeftItemClick(dataBean);
                    notifyDataSetChanged();
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    /**
     * 设置左边数据
     * @param categories
     */
    public void setData(SelectedPageCategories categories) {
        List<SelectedPageCategories.DataBean> data = categories.getData();
        if (data != null) {
            this.mData.clear();
            this.mData.addAll(data);
            notifyDataSetChanged();
        }
        if (mData.size() > 0) {
            mItemClickListener.onLeftItemClick(mData.get(mCurrentSelectedPosition));
        }


    }

    public class InnerHolder extends RecyclerView.ViewHolder {
        public InnerHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    public void setOnLeftItemClickListener(onLeftItemClickListener listener){
        this.mItemClickListener = listener;
    }

    public interface onLeftItemClickListener{
        void onLeftItemClick(SelectedPageCategories.DataBean dataBean);
    }
}
