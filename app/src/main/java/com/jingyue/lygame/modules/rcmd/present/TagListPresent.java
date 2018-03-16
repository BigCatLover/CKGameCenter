package com.jingyue.lygame.modules.rcmd.present;

import android.support.annotation.NonNull;

import com.jingyue.lygame.bean.GameBean;
import com.jingyue.lygame.bean.GameBean_Table;
import com.jingyue.lygame.bean.GameBean_Tag;
import com.jingyue.lygame.bean.GameBean_Tag_Table;
import com.jingyue.lygame.bean.internal.BaseResponse;
import com.jingyue.lygame.constant.AppConstants;
import com.jingyue.lygame.interfaces.ApiService;
import com.jingyue.lygame.modules.rcmd.view.TagListView;
import com.jingyue.lygame.utils.rxJava.ProgressObserver;
import com.laoyuegou.android.lib.mvp.BaseImpl;
import com.laoyuegou.android.lib.mvp.basemvps.BaseMvpPresenter;
import com.laoyuegou.android.lib.utils.NetworkUtils;
import com.lygame.libadapter.HttpRequestFactory;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.database.transaction.QueryTransaction;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Author       : yizhihao (Merlin)
 * Create time  : 2017-09-19 17:01
 * contact      :
 * 562536056@qq.com || yizhihao.hut@gmail.com
 * 标签列表页
 */
public class TagListPresent extends BaseMvpPresenter<TagListView> {
    /**
     * activity fragment不能和view进行耦合
     * 他们是 1..n 的关系
     * BaseImpl是activity的抽象
     *
     * @param mView
     * @param baseImpl
     */
    public TagListPresent(TagListView mView, BaseImpl baseImpl) {
        super(mView, baseImpl);
    }

    public void load(String tagId, boolean showProgress) {
        if (!NetworkUtils.hasNetwork(baseImpl.getContext())) {
            SQLite.select().from(GameBean.class).leftOuterJoin(GameBean_Tag.class)
                    .on(GameBean_Tag_Table.gameBean_id.eq(GameBean_Table.id))
                    .where(GameBean_Tag_Table.tag_id.is(tagId))
                    .async().queryListResultCallback(new QueryTransaction.QueryResultListCallback<GameBean>() {
                @Override
                public void onListQueryResult(QueryTransaction transaction, @NonNull List<GameBean> tResult) {
                    for (final GameBean gameBean : tResult) {
                        gameBean.itemType = AppConstants.ItemType.GAME_TAG_LIST;
                    }
                    mView.setDataOnMain(tResult);
                }
            }).execute();
        }
        HttpRequestFactory.retrofit().create(ApiService.class)
                .obtainTagList(tagId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ProgressObserver<BaseResponse<GameBean.TagList>>(baseImpl, showProgress) {
                    @Override
                    public void onSuccess(BaseResponse<GameBean.TagList> response) {
                        final List<GameBean> gameBeanList = response.realData.list;
                        if (gameBeanList != null) {
                            for (final GameBean gameBean : gameBeanList) {
                                gameBean.itemType = AppConstants.ItemType.GAME_TAG_LIST;
                            }
                            mView.setDataOnMain(response.realData.list);
                        }
                    }
                });
    }
}
