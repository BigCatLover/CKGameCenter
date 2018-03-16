package com.jingyue.lygame.modules.rcmd.present;

import android.util.SparseArray;

import com.jingyue.lygame.bean.ShopImageBean;
import com.jingyue.lygame.bean.internal.BaseResponse;
import com.jingyue.lygame.interfaces.ApiService;
import com.jingyue.lygame.modules.rcmd.view.ShopImageView;
import com.jingyue.lygame.utils.rxJava.ProgressObserver;
import com.laoyuegou.android.lib.mvp.BaseImpl;
import com.laoyuegou.android.lib.mvp.basemvps.BaseMvpPresenter;
import com.lygame.libadapter.HttpRequestFactory;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Author       : yizhihao (Merlin)
 * Create time  : 2017-09-15 17:43
 * contact      :
 * 562536056@qq.com || yizhihao.hut@gmail.com
 */
public class ShopImagePresenter extends BaseMvpPresenter<ShopImageView> {

    private static final SparseArray<List<ShopImageBean>> imagesMap = new SparseArray<>();
    private List<String> imageUrls;

    private boolean isValueSet = false;

    public void clear() {
        imagesMap.clear();
    }

    public void setValueSet(boolean valueSet) {
        isValueSet = valueSet;
    }

    /**
     * activity fragment不能和view进行耦合
     * 他们是 1..n 的关系
     * BaseImpl是activity的抽象
     *
     * @param mView
     * @param baseImpl
     */
    public ShopImagePresenter(ShopImageView mView, BaseImpl baseImpl) {
        super(mView, baseImpl);
        imageUrls = new ArrayList<>();
    }

    public List<String> loadImageUrl(String appId) {
        imageUrls.clear();
        final List<ShopImageBean> imagelist = imagesMap.get(Integer.valueOf(appId));
        for (final ShopImageBean shopImageBean : imagelist) {
            imageUrls.addAll(shopImageBean.list);
        }
        return imageUrls;
    }

    public void loadData(final String appId) {
        if (isValueSet) return;
        final List<ShopImageBean> imagelist = imagesMap.get(Integer.valueOf(appId));
        if (imagelist != null) {
            mView.setDataOnMain(imagelist, false);
        }
        HttpRequestFactory.retrofit().create(ApiService.class)
                .obtainShopImage(appId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new ProgressObserver<BaseResponse<List<ShopImageBean>>>(baseImpl, false) {
                    @Override
                    public void onSuccess(BaseResponse<List<ShopImageBean>> response) {
                        List<ShopImageBean> result = response.realData;
                        int index = 0;
                        for (ShopImageBean shopImageBean : response.realData) {
                            shopImageBean.gameId = appId;
                            if (shopImageBean.list.size() == 2) {
                                shopImageBean = new ShopImageBean.Shop2ImageBean(shopImageBean);
                                result.set(index, shopImageBean);
                            } else if (shopImageBean.list.size() > 2) {
                                shopImageBean = new ShopImageBean.Shop3ImageBean(shopImageBean);
                                result.set(index, shopImageBean);
                            }
                            index++;
                        }
                        imagesMap.put(Integer.valueOf(appId), response.realData);
                        //网络请求时间长，可能当前页面已经结束。
                        if (mView != null) {
                            mView.setDataOnMain(response.realData, false);
                        }
                    }
                });
    }
}
