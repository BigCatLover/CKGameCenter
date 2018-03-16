package com.jingyue.lygame.modules.rcmd.adapter;


import android.support.v4.view.PagerAdapter;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.jingyue.lygame.R;
import com.jingyue.lygame.bean.ShopImageBean;
import com.lygame.libadapter.ImageLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Author       : yizhihao (Merlin)
 * Create time  : 2017-09-24 19:00
 * contact      :
 * 562536056@qq.com || yizhihao.hut@gmail.com
 */
public class BigGalleryAdapter extends PagerAdapter {

    private SparseArray<ImageView> mViews = new SparseArray<>();
    private final List<String> mBeanList = new ArrayList<>();

    @Override
    public int getCount() {
        return mBeanList.size();
    }

    public BigGalleryAdapter() {
    }

    public void resetData(List<ShopImageBean> beanList) {
        mBeanList.clear();
        for (final ShopImageBean shopImageBean : beanList) {
            mBeanList.addAll(shopImageBean.list);
        }
    }

    public int getFirstIndex(String url) {
        return mBeanList.indexOf(url);
    }

    public String getItem(int position) {
        try {
            return mBeanList.get(position);
        } catch (IndexOutOfBoundsException e) {
            return "";
        }
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ImageView view = mViews.get(position);
        final String bean = mBeanList.get(position);
        if (view == null) {
            view = makeView(container, position);
            mViews.put(position, view);
        }
        container.addView(view);
        ImageLoader.getInstance().showImage(ImageLoader
                .getDefaultSourceOptions(view, bean,R.mipmap.ic_load_err_land));
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(mViews.get(position));
    }

    private ImageView makeView(ViewGroup container, int position) {
        final ImageView imageView = (ImageView) LayoutInflater.from(
                container.getContext()).inflate(R.layout.rcmd_view_big_image_item,
                container,
                false);
        return imageView;
    }

}
