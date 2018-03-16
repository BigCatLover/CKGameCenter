package com.jingyue.lygame.utils;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.jingyue.lygame.R;
import com.jingyue.lygame.bean.LoginInfoBean;
import com.jingyue.lygame.modules.rcmd.adapter.RecordUserAdapter;
import com.laoyuegou.android.lib.utils.LogUtils;

import java.util.List;

/**
 * Created by liu hong liang on 2016/9/27.
 * modify by yizhihao
 * 复用popupWidow並且暴露釋放popupwindow方法
 * 对静态popupwindow释放引用
 */
public class RecordUserPopUtil {
    private static PopupWindow popupWindow = null;

    public static PopupWindow getPopupWindow() {
        return popupWindow;
    }

    public static void showRecordUserListPop(Context context, final EditText userName, final EditText userPwd, ImageView more,
                                             ImageView del, ImageView iv, String name, final List<LoginInfoBean> loginInfoList) {
        try {
            if (popupWindow != null) {
                View contentView = popupWindow.getContentView();
                if (contentView != null) {
                    ListView listView = (ListView) contentView.getTag();
                    RecordUserAdapter adapter = (RecordUserAdapter) listView.getAdapter();
                    adapter.clear();
                    adapter.addAll(loginInfoList);
                    adapter.notifyDataSetChanged();
                }
            } else {
                View contentView = LayoutInflater.from(context).inflate(R.layout.view_pop_show_user_list, null);
                popupWindow = new PopupWindow(contentView,
                        ((View) userName.getParent()).getWidth(),
                        ViewGroup.LayoutParams.WRAP_CONTENT);

                final ListView lvRecordUser = (ListView) contentView.findViewById(R.id.lv_user_record);
                lvRecordUser.setAdapter(new RecordUserAdapter(loginInfoList, userName, userPwd, more, del, iv, name));

                popupWindow.getContentView().setTag(lvRecordUser);
                popupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
                popupWindow.setOutsideTouchable(true);
            }

            popupWindow.showAsDropDown((View) userName.getParent());
        } catch (Exception e) {
            LogUtils.e(e);
        }
    }

    public static void hide() {
        popupWindow.dismiss();
    }

    public static void releasae() {
        popupWindow = null;
    }
}
