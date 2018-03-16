package com.jingyue.lygame;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.jingyue.lygame.constant.AppConstants;
import com.laoyuegou.android.lib.mvp.BaseImpl;
import com.laoyuegou.android.lib.mvp.BaseMvpFragment;
import com.laoyuegou.android.lib.utils.EBus;
import com.lygame.libadapter.OpenEventCollectManager;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * Fragment基类
 * Created by yizhihao on 15/3/12.
 * <p>
 * 关于{@link #showProgress(String)} 因为简单的将显示进度条委托给了activity
 * 所以，在调用的时候会跟baseActivity进行关联，showProgress又和{@link com.jingyue.lygame.utils.rxJava.ProgressObserver}
 * 进行了关联，那么调用p层的load只能在attachActivity之后
 */
public abstract class BaseFragment extends BaseMvpFragment implements BaseImpl {

    protected BaseActivity baseActivity;

    protected abstract int getLayoutId();

    protected View mCacheView;
    private Unbinder mUnBinder;

    private CompositeDisposable disposables2Destroy;// 管理Destroy取消订阅者者
    private boolean enableEventBus = false;

    public void setEnableEventBus(boolean enableEventBus) {
        this.enableEventBus = enableEventBus;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (container == null) return null;
        if (mCacheView == null) {
            mCacheView = inflater.inflate(getLayoutId(), null);
            findView(mCacheView);
        } else {
            ViewParent p = mCacheView.getParent();
            if (p != null) {
                ((ViewGroup) p).removeAllViews();
            }
        }
        if (enableEventBus) {
            EBus.getDefault().register(this);
        }
        mUnBinder = ButterKnife.bind(this, mCacheView);
        return mCacheView;
    }

    @Override
    public void onResume() {
        super.onResume();
        OpenEventCollectManager.getInstance().onPageStart(name());
    }

    @Override
    public void onPause() {
        super.onPause();
        OpenEventCollectManager.getInstance().onPageEnd(name());
    }

    /**
     * view 的初始化findview操作最为耗时 将该操作缓存
     *
     * @param view
     */
    protected void findView(View view) {
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnBinder.unbind();
        if (enableEventBus && EBus.getDefault().isRegistered(this)) {
            EBus.getDefault().unregister(this);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        disposables2Destroy.dispose();
        disposables2Destroy = null;
    }

    @Override
    public void onAttach(Context context) {
        if (context instanceof BaseActivity) {
            baseActivity = (BaseActivity) context;
        }
        if(disposables2Destroy == null){
            disposables2Destroy = new CompositeDisposable();
        }
        super.onAttach(context);
    }

    @Override
    public boolean addRxStop(Disposable disposable) {
        if (disposables2Destroy == null) {
            throw new IllegalStateException(
                    "addUtilDestroy should be called between onCreate and onDestroy");
        }
        return disposables2Destroy.add(disposable);
    }

    public boolean addRxDestroy(Disposable disposable) {
        if (disposables2Destroy == null) {
            throw new IllegalStateException(
                    "addUtilDestroy should be called between onCreate and onDestroy");
        }
        return disposables2Destroy.add(disposable);
    }

    @Override
    public void remove(Disposable disposable) {
        disposables2Destroy.remove(disposable);
    }

    @Override
    public void showProgress(String msg) {
        if (baseActivity != null) {
            baseActivity.showProgress(msg);
        }
    }

    @Override
    public void dismissProgress() {
        if (baseActivity != null) {
            baseActivity.dismissProgress();
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        disposables2Destroy = new CompositeDisposable();
    }


    private Handler mHandler;

    private void ensuerHanler() {
        if (mHandler == null) {
            mHandler = new Handler();
        }
    }

    protected void delayClick(final View view) {
        delayClick(view, AppConstants.DEFAULT_CLICK_DELAY_TIME);
    }

    protected void delayClick(final View view, long time) {
        view.setEnabled(false);
        ensuerHanler();
        this.mHandler.postDelayed(new Runnable() {
            public void run() {
                if (view != null) {
                    view.setEnabled(true);
                }

            }
        }, time);
    }

    @Override
    public String name() {
        return getClass().getSimpleName();
    }

    @Override
    public Context getContext() {
        return baseActivity;
    }
}