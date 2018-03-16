package com.jingyue.lygame.bean.internal;

import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView;
import android.view.View;


import com.jingyue.lygame.utils.recycleview.BSAdapter;
import com.raizlabs.android.dbflow.annotation.ColumnIgnore;

import java.io.Serializable;

/**
 * Author       : yizhihao (Merlin)
 * Create time  : 2017-07-05 1:48
 * contact      :
 * 562536056@qq.com || yizhihao.hut@gmail.com
 * list view model item 基类布局
 */
public abstract class BaseItem implements Serializable {

    public BaseItem(int itemType) {
        this.itemType = itemType;
        BSAdapter.GlobalViewType.register(itemType, this);
    }

    public BaseItem() {
        BSAdapter.GlobalViewType.register(itemType, this);
    }

    @ColumnIgnore
    public int itemType = 0;

    public int getItemType(){
        return itemType;
    }

    /**
     * @return 获取对应view 的布局
     */
    @LayoutRes
    public abstract int getLayout();

    /**
     * 不同的实体可能对应相同的布局，当出现相同布局的实体的时候
     * 覆写view type偏移量
     *
     * @return
     */
    public int getViewTypeOffset() {
        return 0;
    }

    /**
     * 因为view model和vh是 1...n的关系
     * 加上base adapter不知道vh的类型无法实例化
     * 所以将初始化vh的操作委托给view model
     *
     * @param parent   此处传递root view而传递context是为了减少view model中的业务逻辑与context的耦合
     * @param viewType
     * @return
     */
    public abstract BVH onCreateViewHolder(View parent, int viewType);

    public static abstract class BVH<M extends BaseAdapterModel> extends RecyclerView.ViewHolder {

        protected BSAdapter.OnItemClickListener mItemClickListener;

        public BVH<M> setItemClickListener(final BSAdapter.OnItemClickListener itemClickListener) {
            this.mItemClickListener = itemClickListener;
            if (this.mItemClickListener == itemClickListener) {
                return this;
            }
            if (itemClickListener != null) {
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mItemClickListener.onItemClick(v, getAdapterPosition());
                    }
                });
            }
            return this;
        }

        public BVH(View itemView) {
            super(itemView);
        }

        /**
         * 每个vh的有自己对应的view xml
         * 所以对于bind view的操作各不相同
         * 所以将bind view 操作委托给vh自己去处理
         *
         * @param itemInfo
         */
        public abstract void onBindViewHolder(M itemInfo, int position);
    }

    @Override
    public String toString() {
        return "BaseItem{" +
                "class " + getClass().getSimpleName() +
                "view type id= " + getLayout() + "," + getViewTypeOffset() +
                '}';
    }
}
