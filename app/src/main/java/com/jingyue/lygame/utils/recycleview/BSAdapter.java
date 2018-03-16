package com.jingyue.lygame.utils.recycleview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jingyue.lygame.bean.GameBean;
import com.jingyue.lygame.bean.ShopImageBean;
import com.jingyue.lygame.bean.internal.BaseAdapterModel;
import com.jingyue.lygame.bean.internal.BaseItem;
import com.jingyue.lygame.constant.AppConstants;
import com.laoyuegou.android.lib.utils.LogUtils;
import com.raizlabs.android.dbflow.annotation.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Author       : yizhihao (Merlin)
 * Create time  : 2017-07-05 0:17
 * contact      :
 * 562536056@qq.com || yizhihao.hut@gmail.com
 */
public abstract class BSAdapter<M extends BaseAdapterModel, R extends BaseItem.BVH<M>> extends RecyclerView.Adapter<R> {

    public static class GlobalViewType {

        public static final int INVALID_VIEW_TYPE = 0;

        private static HashMap<Integer, BaseItem> sViewType = new HashMap<>();

        static{
            register(AppConstants.ItemType.GAME_DETAIL, new GameBean(AppConstants.ItemType.GAME_DETAIL));
            register(AppConstants.ItemType.SHOP_IMAGE_DEFAULT, new ShopImageBean());
            register(AppConstants.ItemType.SHOP_IMAGE_2, new ShopImageBean.Shop2ImageBean());
            register(AppConstants.ItemType.SHOP_IMAGE_3, new ShopImageBean.Shop3ImageBean());
            register(AppConstants.ItemType.GAME_TAG_LIST, new GameBean(AppConstants.ItemType.GAME_TAG_LIST));
            register(AppConstants.ItemType.SEARCH_GAME_LIST, new GameBean(AppConstants.ItemType.SEARCH_GAME_LIST));
            register(AppConstants.ItemType.USERLIKE_GAME_DETAIL, new GameBean(AppConstants.ItemType.USERLIKE_GAME_DETAIL));
        }

        /**
         * 注册iteminfo 如果已经存在于注册表中
         * 则将结果返回
         *
         * @param itemInfo
         * @return
         */
        public static void register(int viewType, BaseItem itemInfo) {
            if (viewType < INVALID_VIEW_TYPE || itemInfo == null) {
                return;
            }
            final BaseItem itemInfoTmp = sViewType.get(viewType);
            if (itemInfoTmp == null) {
                sViewType.put(viewType, itemInfo);
            }
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int postion);
    }

    private OnItemClickListener mItemClickListener;

    public OnItemClickListener getItemClickListener() {
        return mItemClickListener;
    }

    public void setItemClickListener(OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    @NotNull
    protected Context mContext;

    @NotNull
    protected LayoutInflater inflater;

    @NotNull
    protected List<M> mDatas;

    public BSAdapter(Context context) {
        this.mContext = context;
        this.inflater = LayoutInflater.from(mContext);
        mDatas = new ArrayList<>();
    }

    public BSAdapter(Context context, List<M> datas) {
        this.mDatas = datas;
        this.mContext = context;
        this.inflater = LayoutInflater.from(mContext);
        if (datas == null) {
            mDatas = new ArrayList<>();
        }
    }

    @Override
    public R onCreateViewHolder(ViewGroup parent, int viewType) {
        BaseItem itemInfo = GlobalViewType.sViewType.get(viewType);
        if (itemInfo == null) {
            LogUtils.e("itemInfo is null!!! view type = " + viewType);
            return null;
        }
        final View view = inflater.inflate(itemInfo.getLayout(), parent, false);
        return (R) itemInfo.onCreateViewHolder(view, viewType).setItemClickListener(mItemClickListener);
    }

    @Override
    public int getItemViewType(int position) {
        final BaseAdapterModel itemInfo = mDatas.get(position);
        return itemInfo.getItemType();
    }

    @Override
    public void onBindViewHolder(R holder, int position) {
        holder.onBindViewHolder(mDatas.get(position), position);
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    public List<M> getDatas() {
        return Collections.unmodifiableList(mDatas);
    }

    public M getItem(int position) {
        return mDatas.get(position);
    }

    public void clear() {
        mDatas.clear();
    }
    public void deleteItem(String appid){
        for(int i=0;i<mDatas.size();i++){
            if(mDatas.get(i) instanceof GameBean){
                GameBean bean = (GameBean)mDatas.get(i) ;
                if(String.valueOf(bean.id).equals(appid)){
                    mDatas.remove(i);
                    notifyDataSetChanged();
                }
            }
        }
    }
    public void addAll(List<M> datas) {
        mDatas.addAll(datas);
    }
    public void addAll(List<M> datas,boolean isRefresh) {
        if(datas == null||datas.isEmpty()){
            return;
        }
        if(isRefresh){
            mDatas.clear();
        }
        mDatas.addAll(datas);
        notifyDataSetChanged();
    }
}
