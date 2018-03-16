package com.jingyue.lygame.modules.search.present;

import com.jingyue.lygame.bean.SearchHotKeyBean;
import com.jingyue.lygame.bean.internal.BaseResponse;
import com.jingyue.lygame.constant.UrlConstant;
import com.jingyue.lygame.model.HotKeyRp;
import com.jingyue.lygame.modules.search.view.HotKeyView;
import com.jingyue.lygame.utils.rxJava.ProgressObserver;
import com.laoyuegou.android.lib.mvp.BaseImpl;
import com.laoyuegou.android.lib.mvp.basemvps.BaseMvpPresenter;
import com.laoyuegou.android.lib.utils.NetworkUtils;
import com.laoyuegou.android.lib.utils.RxMap;
import com.laoyuegou.android.lib.utils.Utils;

import java.util.List;

/**
 * Created by zhanglei on 2017/9/18.
 */
public class HotKeyPresent extends BaseMvpPresenter<HotKeyView> {
    private HotKeyRp hotKeyRp;

    public HotKeyPresent(HotKeyView mvpView, BaseImpl base, HotKeyRp respository) {
        super(mvpView,base);
        hotKeyRp = respository;
    }

    public void loadData(final boolean onlyGames){
        hotKeyRp.getEntry(new UrlConstant.Builder(false).floatMoblieV2().searchHotKey(),
                new RxMap<String,String>().build(),true, NetworkUtils.hasNetwork(Utils.getContext()))
                .subscribe(new ProgressObserver<BaseResponse<SearchHotKeyBean>>(baseImpl,false) {
                    @Override
                    public void onSuccess(BaseResponse<SearchHotKeyBean> response) {
                        mView.setDataOnMain(response.realData,onlyGames);
                    }
                });
    }
}
