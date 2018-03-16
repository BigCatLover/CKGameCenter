package com.jingyue.lygame.modules.rcmd.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.jingyue.lygame.R;
import com.jingyue.lygame.bean.LoginInfoBean;
import com.jingyue.lygame.utils.RecordUserPopUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liu hong liang on 2016/9/27.
 */

public class RecordUserAdapter extends BaseAdapter {
    private List<LoginInfoBean> userLoginInfo;
    private ImageView more_ll;
    private ImageView del;
    private ImageView more;
    private EditText name;
    private EditText pwd;
    private String currentname;

    public RecordUserAdapter(List<LoginInfoBean> loginInfoList, EditText userName, EditText userPwd, ImageView more, ImageView del, ImageView iv, String currentname) {
        userLoginInfo = new ArrayList<>(loginInfoList);
        this.more = iv;
        more_ll = more;
        this.del = del;
        name = userName;
        this.currentname = currentname;
    }

    public void clear() {
        userLoginInfo.clear();
    }

    public void addAll(List<LoginInfoBean> loginInfoList) {
        userLoginInfo.addAll(loginInfoList);
    }

    @Override
    public int getCount() {
        return userLoginInfo.size();
    }

    @Override
    public Object getItem(int position) {
        return userLoginInfo.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final LoginInfoBean bean = userLoginInfo.get(position);
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.personal_view_record_user_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.tvUserName = (TextView) convertView.findViewById(R.id.tv_user_name);
            viewHolder.ivDelete = (ImageView) convertView.findViewById(R.id.iv_delete);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name.setText(bean.username);
                RecordUserPopUtil.hide();
            }
        });
        viewHolder.ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentname.equals(bean.username)) {
                    name.setText("");
                    pwd.setText("");
                    del.setVisibility(View.INVISIBLE);
                }
                bean.async().delete();
                userLoginInfo.remove(position);

                if (userLoginInfo.size() > 0) {
                    more_ll.setVisibility(View.VISIBLE);
                    more.setImageResource(R.mipmap.arrow_bottom);
                } else {
                    more_ll.setVisibility(View.INVISIBLE);
                    if (name.getText().toString().trim().length() > 0) {
                        del.setVisibility(View.VISIBLE);
                    } else {
                        del.setVisibility(View.INVISIBLE);
                    }

                    if (RecordUserPopUtil.getPopupWindow() != null) {
                        RecordUserPopUtil.hide();
                    }
                }
                notifyDataSetChanged();
            }
        });
        viewHolder.tvUserName.setText(bean.username);
        return convertView;
    }

    public static class ViewHolder {
        public TextView tvUserName;
        public ImageView ivDelete;
    }
}
