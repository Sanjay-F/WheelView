package com.example.sanjay.wheelviewlib;

import android.content.Context;
import android.support.annotation.ColorInt;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * 数据bean需要完成toString方法
 */
public class WheelAdapter<T> extends RecyclerView.Adapter<WheelAdapter.tvViewHolder> {
    private List<T> mTextList;
    //字体颜色
    private int mTextColor;
    //字体padding
    private int mTextPadding;
    private static final int DEFAULT_TEXT_PADDING = 5;
    //字体大小
    float mTextSize;
    private static final int DEFAULT_TEXT_SIZE = 20;
    //上下文
    private Context mContext;
    private int selectedIndex;

    public WheelAdapter(Context context) {
        mContext = context;
        mTextColor = mContext.getResources().getColor(R.color.colorAccent);
        mTextSize = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP, DEFAULT_TEXT_SIZE, mContext.getResources().getDisplayMetrics());
        mTextPadding = ViewUtils.dp2px(mContext, DEFAULT_TEXT_PADDING);

    }

    public void setTextSize(int unit, float size) {
        mTextSize = TypedValue.applyDimension(
                unit, size, mContext.getResources().getDisplayMetrics());
    }

    public void setTextPadding(int padding) {
        mTextPadding = padding;
    }

    public void setTextColor(@ColorInt int color) {
        mTextColor = color;
    }

    public void setData(List<T> dataList) {
        mTextList = dataList;
        notifyDataSetChanged();
    }

    public void addData(List<T> dataList) {
        if (mTextList != null) {
            mTextList.addAll(dataList);
        } else {
            mTextList = dataList;
        }
        notifyDataSetChanged();
    }

    @Override
    public tvViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_date, null);
        return new tvViewHolder(view);
    }

    @Override
    public void onBindViewHolder(WheelAdapter.tvViewHolder holder, int position) {
        holder.bindData(mTextList.get(position).toString());
    }

    @Override
    public int getItemCount() {
        return mTextList == null ? 0 : mTextList.size();
    }

    class tvViewHolder extends RecyclerView.ViewHolder {
        TextView contentTv;
        private int originalTextColor;

        public tvViewHolder(View view) {
            super(view);
            contentTv = (TextView) view.findViewById(R.id.content_textview);
            originalTextColor = contentTv.getCurrentTextColor();
        }

        public void bindData(String data) {


            if (mTextSize != 0 && this.contentTv.getTextSize() != mTextSize) {
                this.contentTv.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize);
            }
            if (mTextColor != 0 && this.contentTv.getCurrentTextColor() != mTextColor) {
                this.contentTv.setTextColor(mTextColor);
            }
            if (this.contentTv.getPaddingTop() != mTextPadding) {
                this.contentTv.setPadding(0, mTextPadding, 0, mTextPadding);
            }
            if (getAdapterPosition() == selectedIndex) {
                contentTv.setTextColor(mTextColor);
            } else {
                contentTv.setTextColor(originalTextColor);
            }

            this.contentTv.setText(data);

        }
    }

    public void setSelectedIndex(int index) {
        this.selectedIndex = index;
        notifyDataSetChanged();
    }
}
