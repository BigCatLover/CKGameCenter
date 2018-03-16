package com.jingyue.lygame.modules.download;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.RecyclerView;
import android.widget.RelativeLayout;

import com.jingyue.lygame.ActionBarUtil;
import com.jingyue.lygame.BaseActivity;
import com.jingyue.lygame.R;
import com.jingyue.lygame.utils.download.TasksManager;
import com.jingyue.lygame.utils.recycleview.ItemDividerDecoration;
import com.jingyue.lygame.widget.BaseLinearLayoutManager;
import com.jingyue.lygame.widget.RecycleViewDivider;

import butterknife.BindView;

public class DownloadCenterActivity extends BaseActivity {

    @BindView(R.id.actionbar_group)
    RelativeLayout actionbarGroup;
    @BindView(R.id.rcy_downlist)
    RecyclerView rcyDownlist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBarUtil.inject(this).title(R.string.a_0309)
                .hideShare()
                .hideCollect();
        rcyDownlist.setLayoutManager(new BaseLinearLayoutManager(this));
        rcyDownlist.addItemDecoration(new RecycleViewDivider(this, ItemDividerDecoration.HORIZONTAL_LIST,2, ResourcesCompat.getColor(getResources(),
                R.color.bg_common, getTheme()), false));
        rcyDownlist.setAdapter(new DownloadAdapter(this, TasksManager.getImpl().getAllTasks()));
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_download_center;
    }

    public static void start(Context context){
        Intent i=new Intent(context,DownloadCenterActivity.class);
        context.startActivity(i);
    }
}
