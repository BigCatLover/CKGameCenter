package com.jingyue.lygame.utils.rxJava;

import android.net.ParseException;
import android.text.TextUtils;
import android.widget.Toast;

import com.google.gson.JsonParseException;
import com.jingyue.lygame.BuildConfig;
import com.jingyue.lygame.R;
import com.jingyue.lygame.bean.internal.BaseResponse;
import com.jingyue.lygame.model.LoginManager;
import com.laoyuegou.android.lib.mvp.BaseImpl;
import com.laoyuegou.android.lib.utils.LogUtils;
import com.laoyuegou.android.lib.utils.ToastUtils;

import org.json.JSONException;

import java.io.InterruptedIOException;
import java.net.ConnectException;
import java.net.UnknownHostException;

import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import retrofit2.HttpException;

/**
 * Author       : yizhihao (Merlin)
 * Create time  : 2017-08-23 19:47
 * contact      :
 * 562536056@qq.com || yizhihao.hut@gmail.com
 */
public abstract class ProgressObserver<T extends BaseResponse> extends BaseObserver<T> {

    private BaseImpl mBaseImpl;
    //  Activity 是否在执行onStop()时取消订阅
    private boolean isAddInStop = false;
    private boolean needProgress = false;
    private boolean needShowMsg = true;

    public ProgressObserver(BaseImpl mBaseImpl, boolean needProgress) {
        this(mBaseImpl, needProgress, true);
    }

    public ProgressObserver(BaseImpl mBaseImpl, boolean needProgress, boolean needShowMsg) {
        super(mBaseImpl != null ? mBaseImpl.name() : "ProgressObserver");
        this.needProgress = needProgress;
        this.mBaseImpl = mBaseImpl;
        this.needShowMsg = needShowMsg;
    }

    @Override
    public void onSubscribe(@NonNull Disposable d) {
        if (mBaseImpl == null) return;
        if (needProgress)
            mBaseImpl.showProgress(mBaseImpl.getContext().getString(R.string.a_0021));
        if (isAddInStop) {    //  在onStop中取消订阅
            mBaseImpl.addRxStop(d);
        } else { //  在onDestroy中取消订阅
            mBaseImpl.addRxDestroy(d);
        }
    }

    @Override
    public void onNext(@NonNull T tBaseResponce) {
        LogUtils.e(String.format("ErrorCode = %s ,is from cache %s , %20s", tBaseResponce.errCode, tBaseResponce.fromCache, tBaseResponce.realData));
        if (tBaseResponce.errCode == 0 || tBaseResponce.fromCache) {
            try {
                onSuccess(tBaseResponce);
            } catch (NullPointerException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        } else {
            onFail(tBaseResponce);
        }
        if (needProgress) {
            mBaseImpl.dismissProgress();
        }
    }

    @Override
    public void onError(Throwable e) {
        if (BuildConfig.DEBUG) {
            printError(e);
        }

        if (mBaseImpl != null) {
            mBaseImpl.dismissProgress();
        }

        if (onFail(null)) return;
        onException(e);
    }

    //throwable 栈不会打印
    //手动拼接打印
    private void printError(Throwable e) {
        StringBuilder sb = new StringBuilder();
        StackTraceElement[] stacks = e.getStackTrace();
        sb.append(mBaseImpl != null ? mBaseImpl.name() : "");
        sb.append("\n");
        sb.append(e.getMessage());
        sb.append("\n");
        for (StackTraceElement stack : stacks) {
            sb.append("at ");
            sb.append(stack.getClassName());
            sb.append(".");
            sb.append(stack.getMethodName());
            sb.append("(");
            sb.append(stack.getClassName().substring(stack.getClassName().lastIndexOf(".") + 1));
            sb.append(".java:");
            sb.append(stack.getLineNumber());
            sb.append(")");
            sb.append("\n");
        }
        LogUtils.e("Retrofit", sb.toString());
    }

    @Override
    public void onComplete() {
        if (mBaseImpl == null) return;
        if (needProgress) mBaseImpl.dismissProgress();
    }

    /**
     * 请求成功
     *
     * @param response 服务器返回的数据
     */
    abstract public void onSuccess(T response) throws Exception;

    /**
     * 服务器返回数据，但响应码不为200
     *
     * @param response 服务器返回的数据
     */
    public boolean onFail(T response) {
        //某些接口可能传递的baseimple为null
        if (mBaseImpl != null) {
            if (response == null && !TextUtils.isEmpty(mBaseImpl.name())) {
                LogUtils.e(mBaseImpl.name() + " data is null!");
                return false;
            }
            if (response.errCode == BaseResponse.UN_LOGIN) {
                //需要登录
                ToastUtils.showShortSafe(response.errMsg);
                //此处必须传activity的context否则会跳转失败。
                //因为startactivityforresult必须是activity
                LoginManager.getInstance().reLogin(mBaseImpl.getContext());
                return true;
            }
        }
        if (needShowMsg && response != null) {
            String message = response.errMsg;
            if (TextUtils.isEmpty(message)) {
                ToastUtils.showShortSafe(R.string.a_0020);
            } else {
                ToastUtils.showShortSafe(message);
            }
            return true;
        }
        return false;
    }

    /**
     * 请求异常
     */
    public void onException(Throwable e) {
        ExceptionReason reason;
        if (e instanceof HttpException) {                 //   HTTP错误
            reason = ExceptionReason.BAD_NETWORK;
        } else if (e instanceof ConnectException
                || e instanceof UnknownHostException) {   //   连接错误
            reason = ExceptionReason.CONNECT_ERROR;
        } else if (e instanceof InterruptedIOException) { //   连接超时
            reason = ExceptionReason.CONNECT_TIMEOUT;
        } else if (e instanceof JsonParseException
                || e instanceof JSONException
                || e instanceof ParseException) {         //   解析错误
            reason = ExceptionReason.PARSE_ERROR;
        } else {
            reason = ExceptionReason.UNKNOWN_ERROR;
        }
        switch (reason) {
            case CONNECT_ERROR:
                ToastUtils.showShort(R.string.a_0019, Toast.LENGTH_SHORT);
                break;

            case CONNECT_TIMEOUT:
                ToastUtils.showShort(R.string.a_0018, Toast.LENGTH_SHORT);
                break;

            case BAD_NETWORK:
                ToastUtils.showShort(R.string.a_0017, Toast.LENGTH_SHORT);
                break;

            case PARSE_ERROR:
                ToastUtils.showShort(R.string.a_0016, Toast.LENGTH_SHORT);
                break;

            case UNKNOWN_ERROR:
            default:
                ToastUtils.showShort(R.string.a_0015, Toast.LENGTH_SHORT);
                break;
        }
    }

    /**
     * 请求网络失败原因
     */
    public enum ExceptionReason {
        /**
         * 解析数据失败
         */
        PARSE_ERROR,
        /**
         * 网络问题
         */
        BAD_NETWORK,
        /**
         * 连接错误
         */
        CONNECT_ERROR,
        /**
         * 连接超时
         */
        CONNECT_TIMEOUT,
        /**
         * 未知错误
         */
        UNKNOWN_ERROR,
    }
}
