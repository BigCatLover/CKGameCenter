package com.laoyuegou.android.common.glide.gift;

public interface ProgressListener {

    void progress(long bytesRead, long contentLength, boolean done);

    void onResourceReady();

}
