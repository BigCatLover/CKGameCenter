package com.jingyue.lygame.modules.rcmd.present;

import android.os.Handler;
import android.os.Looper;

import com.jingyue.lygame.bean.AppDataBase;
import com.jingyue.lygame.bean.GameBean;
import com.jingyue.lygame.bean.GameBean_Tag;
import com.jingyue.lygame.bean.TagBusinessViewBean;
import com.jingyue.lygame.bean.internal.BaseResponse;
import com.jingyue.lygame.constant.UrlConstant;
import com.jingyue.lygame.interfaces.ApiService;
import com.jingyue.lygame.model.LoginManager;
import com.jingyue.lygame.model.TagRp;
import com.jingyue.lygame.model.internal.DBRpManager;
import com.jingyue.lygame.modules.rcmd.view.TagView;
import com.jingyue.lygame.utils.rxJava.ProgressObserver;
import com.laoyuegou.android.lib.mvp.BaseImpl;
import com.laoyuegou.android.lib.mvp.basemvps.BaseMvpPresenter;
import com.laoyuegou.android.lib.utils.DebugUtils;
import com.laoyuegou.android.lib.utils.LogUtils;
import com.laoyuegou.android.lib.utils.NetworkUtils;
import com.laoyuegou.android.lib.utils.RxMap;
import com.laoyuegou.android.lib.utils.ToastUtils;
import com.lygame.libadapter.HttpRequestFactory;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;
import com.raizlabs.android.dbflow.structure.database.transaction.ITransaction;

import java.util.List;

import io.reactivex.schedulers.Schedulers;

/**
 * Author       : yizhihao (Merlin)
 * Create time  : 2017-09-18 20:46
 * contact      :
 * 562536056@qq.com || yizhihao.hut@gmail.com
 */
public class TagPresent extends BaseMvpPresenter<TagView> {

    private TagRp mTagRp;
    private Handler mHandler;
    private GameBean_Tag mGameBeanTag;
    private GameBean mGameBean;

    /**
     * activity fragment不能和view进行耦合
     * 他们是 1..n 的关系
     * BaseImpl是activity的抽象
     *
     * @param mView
     * @param baseImpl
     */
    public TagPresent(TagView mView, BaseImpl baseImpl, TagRp tagRp) {
        super(mView, baseImpl);
        this.mTagRp = tagRp;
        mHandler = new Handler(Looper.getMainLooper());
    }

    /**
     * 标签数据被缓存， 标签与游戏之间的关系也会被保存
     * 获取标签数据时，同步当前游戏标签数据。
     *
     * @param appId
     * @param showProgress
     */
    public void load(final String appId, boolean showProgress) {
        final boolean hasNetWork = NetworkUtils.hasNetwork(baseImpl.getContext());
        mTagRp.getEntry(new UrlConstant.Builder(false).floatMoblieV2().gameTags()
                , new RxMap().put("app_id", appId).build(),
                true,
                hasNetWork)
                .subscribe(new ProgressObserver<BaseResponse<TagBusinessViewBean>>(baseImpl, showProgress) {
                    @Override
                    public void onSuccess(final BaseResponse<TagBusinessViewBean> response) {
                        final List<GameBean.Tag> user = response.realData.user;
                        final List<GameBean.Tag> defaultTags = response.realData.defaultX;
                        FlowManager.getDatabase(AppDataBase.class)
                                .beginTransactionAsync(new ITransaction() {
                                    @Override
                                    public void execute(DatabaseWrapper databaseWrapper) {
                                        //同步客户端标签数据
                                        //以服务端数据为准
                                        asynTagData(response, appId, defaultTags, user);
                                    }
                                }).execute();
                        mView.setDataOnMain(response.realData);
                    }
                });
    }

    /**
     * 同步tag 数据
     *
     * @param response
     * @param appId
     * @param defaultTags
     * @param user
     */
    private void asynTagData(BaseResponse<TagBusinessViewBean> response, String appId, List<GameBean.Tag> defaultTags, List<GameBean.Tag> user) {
        if (mGameBeanTag == null) {
            mGameBeanTag = new GameBean_Tag();
            mGameBean = new GameBean();
            mGameBean.id = Integer.valueOf(appId);
            mGameBeanTag.setGameBean(mGameBean);
        }
        if (!response.fromCache) {
            mTagRp.clearCache(appId);
        }
        if (defaultTags != null) {
            for (final GameBean.Tag defaultTag : defaultTags) {
                defaultTag.setIsDefault(true);
                mGameBeanTag.setTag(defaultTag);
                mGameBeanTag.save();
            }
        }
        if (user != null) {
            for (final GameBean.Tag tag : user) {
                tag.isUserCustom = true;
                if (tag.isSelected()) {
                    mGameBeanTag.setTag(tag);
                    mGameBeanTag.save();
                }
            }
        }
    }

    /**
     * 添加标签
     *
     * @param tag
     * @param appId
     * @param showProgress
     */
    public void addTag(final String tag, final String appId, boolean showProgress) {
        HttpRequestFactory.retrofit().create(ApiService.class)
                .addTag(LoginManager.getInstance().getEncryptHsToken()
                        , tag)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new ProgressObserver<BaseResponse<GameBean.Tag>>(baseImpl, showProgress) {
                    @Override
                    public void onSuccess(final BaseResponse<GameBean.Tag> response) {
                        if (response.realData != null) {
                            //服务器接口没有返回name  需要自己添加
                            response.realData.name = tag;
                            response.realData.isUserCustom = true;
                            response.realData.async().save();
                            //save relationShip with game
                            try {
                                mGameBeanTag.setTag(response.realData);
                                GameBean gameBean = new GameBean();
                                gameBean.id = Integer.valueOf(appId);
                                mGameBeanTag.setGameBean(gameBean);
                                mGameBeanTag.async().save();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    if(mView != null){
                                        mView.onTagAdded(response.realData);
                                    }
                                }
                            });
                        }
                        LogUtils.d(" add tag " + tag + "uploadGuideInfoSucess return tag id : " + response.realData.id);
                    }
                });
    }

    public void selectTag(List<GameBean.Tag> defaultChoosed, final List<GameBean.Tag> userChoosed, String appId, final GameBean.Tag tag, boolean showProgress) {
        String choosedDefaultStr = "";
        String choosedUserStr = "";

        choosedDefaultStr = getUploadTagString(defaultChoosed, choosedDefaultStr);
        choosedUserStr = getUploadTagString(userChoosed, choosedUserStr);

        HttpRequestFactory.retrofit().create(ApiService.class)
                .selectTag(choosedDefaultStr, choosedUserStr, appId)
                .subscribeOn(Schedulers.io())
                .subscribe(new ProgressObserver<BaseResponse>(baseImpl, showProgress) {
                    @Override
                    public void onSuccess(BaseResponse response) {
                        if (DebugUtils.DEBUG) {
                            ToastUtils.showShortSafe("上传标签成功");
                        }
                        //选中标签如果为用户标签
                        if (mGameBeanTag != null) {
                            FlowManager.getDatabase(AppDataBase.class)
                                    .beginTransactionAsync(new ITransaction() {
                                        @Override
                                        public void execute(DatabaseWrapper databaseWrapper) {
                                            mGameBeanTag.setTag(tag);
                                            if (tag.isSelected()) {
                                                mGameBeanTag.async().save();
                                            } else {
                                                mGameBeanTag.delete();
                                            }
                                        }
                                    }).execute();

                        }
                    }

                    @Override
                    public boolean onFail(BaseResponse response) {
                        super.onFail(response);
                        if (DebugUtils.DEBUG) {
                            ToastUtils.showShortSafe("上传标签失败");
                        }
                        return false;
                    }
                });
    }

    private String getUploadTagString(List<GameBean.Tag> defaultChoosed, String choosedDefaultStr) {
        if (defaultChoosed != null && !defaultChoosed.isEmpty()) {
            StringBuilder sb = new StringBuilder(defaultChoosed.size() * 2 + 2);
            for (final GameBean.Tag tag : defaultChoosed) {
                if (!tag.isSelected()) continue;
                if (sb.length() > 0) {
                    sb.append(",");
                }
                sb.append(tag.id);
            }
            choosedDefaultStr = sb.toString();
        }
        return choosedDefaultStr;
    }


}
