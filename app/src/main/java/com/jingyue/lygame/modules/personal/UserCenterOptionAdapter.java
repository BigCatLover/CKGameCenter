package com.jingyue.lygame.modules.personal;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jingyue.lygame.R;
import com.jingyue.lygame.bean.UserCenterOptionBean;
import com.jingyue.lygame.constant.UrlConstant;
import com.jingyue.lygame.modules.common.webview.WebViewActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by zhanglei on 2017/9/19.
 */
public class UserCenterOptionAdapter extends RecyclerView.Adapter {
    private List<UserCenterOptionBean> datas=new ArrayList<>();
    private Context mContext;
    public UserCenterOptionAdapter(Context context) {
        this.mContext=context;
        datas.add(new UserCenterOptionBean(mContext.getString(R.string.a_0140), R.mipmap.ic_gamelist));
        datas.add(new UserCenterOptionBean(mContext.getString(R.string.a_0085), R.mipmap.ic_moon_circle));
        datas.add(new UserCenterOptionBean(mContext.getString(R.string.a_0141), R.mipmap.ic_service));
        datas.add(new UserCenterOptionBean(mContext.getString(R.string.a_0142), R.mipmap.ic_email));
        datas.add(new UserCenterOptionBean(mContext.getString(R.string.a_0143), R.mipmap.ic_phone_secret));
        datas.add(new UserCenterOptionBean(mContext.getString(R.string.a_0144), R.mipmap.ic_change_password));

    }

    @Override
    public UserCenterOptionAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_option_user_center, parent, false);
        return new ViewHolder(view);
    }

    private StringBuilder mStringBuilder = null;

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        final ViewHolder vh = (ViewHolder) holder;
        vh.optionImg.setImageResource(datas.get(position).getImageId());
        vh.optionName.setText(datas.get(position).getName());
        vh.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mStringBuilder == null){
                    mStringBuilder = new StringBuilder();
                }
                mStringBuilder.delete(0,mStringBuilder.length());
                mStringBuilder.append(UrlConstant.BASE_H5_URL)
                        .append(UrlConstant.SHARE_URL_SUFFIX);
                switch (datas.get(vh.getAdapterPosition()).getImageId()){
                    case R.mipmap.ic_service: // 进入到客服界面
                        mStringBuilder.append("customerpage");
                        WebViewActivity.start(mContext, mContext.getString(R.string.a_0145),
                                mStringBuilder.toString());
                        break;
                    case R.mipmap.ic_email: // 进入到密保邮箱界面
                        mStringBuilder.append("setEmailPage");
                        WebViewActivity.start(mContext, mContext.getString(R.string.a_0142),
                                mStringBuilder.toString());
                        break;
                    case R.mipmap.ic_phone_secret: // 进入到密保手机
                        mStringBuilder.append("setMobilePage");
                        WebViewActivity.start(mContext, mContext.getString(R.string.a_0143),
                                mStringBuilder.toString());
                        break;
                    case R.mipmap.ic_change_password:
                        mStringBuilder.append("UpdatePwdPage");
                        WebViewActivity.start(mContext, mContext.getString(R.string.a_0144),
                                mStringBuilder.toString());
                        break;
                    case R.mipmap.ic_gamelist:
                        GameListActivity.start(mContext);
                        break;
                    case R.mipmap.ic_moon_circle:
                        mStringBuilder.append("moonvaluepage");
                        WebViewActivity.start(mContext, mContext.getString(R.string.a_0085),
                                mStringBuilder.toString());
                        break;
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.option_img)
        ImageView optionImg;
        @BindView(R.id.option_name)
        TextView optionName;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
