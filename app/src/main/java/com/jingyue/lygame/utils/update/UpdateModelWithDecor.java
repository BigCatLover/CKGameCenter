package com.jingyue.lygame.utils.update;

/**
 * Author       : yizhihao (Merlin)
 * Create time  : 2017-10-25 18:59
 * contact      :
 * 562536056@qq.com || yizhihao.hut@gmail.com
 */
public class UpdateModelWithDecor extends UpdateModel {

    @Override
    public void downloadNewVersionIOThread(String url, String dir, String newVersion, long lastDownloadSize, UpdatePresenter observer) {
        startUpgradeApkDownload(url, newVersion, observer);
    }

    /**
     * @param url
     * @param observer
     */
    private void startUpgradeApkDownload(String url, String newVersion, UpdatePresenter observer) {
    }
}
