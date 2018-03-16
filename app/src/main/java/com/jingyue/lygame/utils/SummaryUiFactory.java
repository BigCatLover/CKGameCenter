package com.jingyue.lygame.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.jingyue.lygame.R;
import com.laoyuegou.android.lib.utils.Utils;

import java.util.concurrent.atomic.AtomicReference;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Author       : yizhihao (Merlin)
 * Create time  : 2017-09-02 17:13
 * contact      :
 * 562536056@qq.com || yizhihao.hut@gmail.com
 */
public class SummaryUiFactory {

    public class ViewHolder {
        public ViewHolder(View view) {
            ButterKnife.bind(view);
        }

        @BindView(R.id.summary_goup_title)
        public TextView summaryGoupTitle;
        @BindView(R.id.comment_group_more)
        public TextView summaryGroupMore;
        @BindView(R.id.summary_group_comment)
        public TextView summaryGroupComment;
    }

    public View generateSummary() {
        View result = null;
        result = LayoutInflater.from(mContext).inflate(R.layout.rcmd_view_summary_group, null);
        result.setTag(new ViewHolder(result));
        return result;
    }

    private static final AtomicReference<SummaryUiFactory> INSTANCE = new AtomicReference<SummaryUiFactory>();

    private SummaryUiFactory() {
        mContext = Utils.getContext().getApplicationContext();
    }

    private Context mContext;

    public static SummaryUiFactory getInstance() {
        for (; ; ) {
            SummaryUiFactory current = INSTANCE.get();
            if (current != null) {
                return current;
            }
            current = new SummaryUiFactory();
            if (INSTANCE.compareAndSet(null, current)) {
                return current;
            }
        }
    }
}
