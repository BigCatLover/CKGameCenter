package com.jingyue.lygame.modules.personal;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.TextView;

import com.jingyue.lygame.ActionBarUtil;
import com.jingyue.lygame.BaseActivity;
import com.jingyue.lygame.R;

import butterknife.BindView;

/**
 * Author       : yizhihao (Merlin)
 * Create time  : 2017-09-05 14:41
 * contact      :
 * 562536056@qq.com || yizhihao.hut@gmail.com
 */
public class AgreementActivity extends BaseActivity {
    @BindView(R.id.lyg_agreen_content)
    TextView lygAgreenContent;

    @Override
    public int getLayoutId() {
        return R.layout.personal_agreement;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBarUtil.inject(this).hideCollect().hideShare().title(R.string.a_0222);

        lygAgreenContent.setText(R.string.user_agereement_content);
    }
}
