package com.jingyue.lygame.modules.rcmd.present;

import com.jingyue.lygame.bean.internal.BaseResponse;
import com.jingyue.lygame.bean.GameBean;
import com.jingyue.lygame.constant.UrlConstant;
import com.jingyue.lygame.events.TagChangeEvent;
import com.jingyue.lygame.model.GameDetailRp;
import com.jingyue.lygame.model.TagRp;
import com.jingyue.lygame.modules.rcmd.view.GameView;
import com.jingyue.lygame.utils.rxJava.BaseObserver;
import com.jingyue.lygame.utils.rxJava.ProgressObserver;
import com.laoyuegou.android.lib.mvp.BaseImpl;
import com.laoyuegou.android.lib.mvp.basemvps.BaseMvpPresenter;
import com.laoyuegou.android.lib.utils.NetworkUtils;
import com.laoyuegou.android.lib.utils.RxMap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Action;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Author       : yizhihao (Merlin)
 * Create time  : 2017-09-03 16:12
 * contact      :
 * 562536056@qq.com || yizhihao.hut@gmail.com
 */
public class GameDetailPresent extends BaseMvpPresenter<GameView> {

    private GameDetailRp mGameRespository;
    private TagRp mTagRp;

    public GameDetailPresent(GameView mvpView, BaseImpl base, GameDetailRp respository) {
        super(mvpView, base);
        mGameRespository = respository;
    }

    private List<GameBean.Tag> tags;

    public void onTagChangeEvent(List<GameBean.Tag> orginData,TagChangeEvent event){
        if(tags == null){
            tags = orginData;
        }
        switch (event.opertation){
            case TagChangeEvent.OP_SELECT:
                tags.add(event.tag);
                break;
            case TagChangeEvent.OP_UNSELECT:
                if(tags.contains(event.tag)){
                    tags.remove(event.tag);
                }
                break;
        }
    }

    public void reloadTag(final String appId) {
        if (mTagRp == null) {
            mTagRp = new TagRp();
        }
        Observable.create(new ObservableOnSubscribe<List<GameBean.Tag>>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<List<GameBean.Tag>> e) throws Exception {
                List<GameBean.Tag> tags = mTagRp.loadTag(Integer.valueOf(appId));
                if (tags != null) {
                    List<GameBean.Tag> result = new ArrayList<GameBean.Tag>();
                    for (final GameBean.Tag tag : tags) {
                        if (tag.isDefault() || tag.isSelected()) {
                            result.add(tag);
                        }
                    }
                    e.onNext(result);
                    return;
                }
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<List<GameBean.Tag>>(baseImpl.name()) {
                    @Override
                    public void onNext(@NonNull List<GameBean.Tag> tags) {
                        super.onNext(tags);
                        if(mView != null){
                            mView.onTagReload(tags);
                        }
                    }
                });

    }

    private String url = new UrlConstant.Builder(false).floatMoblieV2().gameDetail();

    public void loadData(String appId) {
        mGameRespository.getEntry(url
                , new RxMap().put("app_id", appId).build()
                , true
                , NetworkUtils.hasNetwork(baseImpl.getContext()))
                .subscribe(new ProgressObserver<BaseResponse<GameBean>>(baseImpl, true) {
                    @Override
                    public void onSuccess(BaseResponse<GameBean> response) {
                        mView.setDataOnMain(response.realData, false);
                    }
                });
    }

}

