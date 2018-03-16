package com.jingyue.lygame.utils;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.jingyue.lygame.R;

/**
 * Created by zhanglei on 2017/8/16.
 */

public class DialogUtils {
    public static void showTipDialog(final Context context, String titlename) {
        View contentView = LayoutInflater.from(context).inflate(R.layout.score_instruction, null);
        ImageView close = (ImageView) contentView.findViewById(R.id.iv_return);
        TextView title = (TextView) contentView.findViewById(R.id.tv_titleName);
        final ImageView check = (ImageView) contentView.findViewById(R.id.check);
        final ImageView check1 = (ImageView) contentView.findViewById(R.id.check1);
        final ImageView check2 = (ImageView) contentView.findViewById(R.id.check2);

        final ScrollView content = (ScrollView) contentView.findViewById(R.id.ll_content);
        final ScrollView content1 = (ScrollView) contentView.findViewById(R.id.ll_content1);
        final TextView content2 = (TextView) contentView.findViewById(R.id.ll_content2);

        title.setText(titlename);
        check.setOnClickListener(new View.OnClickListener() {
            boolean flag = true;

            @Override
            public void onClick(View v) {
                if (flag) {
                    content.setVisibility(View.VISIBLE);
                    content1.setVisibility(View.GONE);
                    content2.setVisibility(View.GONE);
                    check.setImageResource(R.mipmap.arrow_top);
                    check1.setImageResource(R.mipmap.arrow_bottom);
                    check2.setImageResource(R.mipmap.arrow_bottom);
                    flag = false;
                } else {
                    flag = true;
                    content.setVisibility(View.GONE);
                    check.setImageResource(R.mipmap.arrow_bottom);
                }
            }
        });
        check1.setOnClickListener(new View.OnClickListener() {
            boolean flag = true;

            @Override
            public void onClick(View v) {
                if (flag) {
                    content1.setVisibility(View.VISIBLE);
                    content.setVisibility(View.GONE);
                    content2.setVisibility(View.GONE);
                    check1.setImageResource(R.mipmap.arrow_top);
                    check.setImageResource(R.mipmap.arrow_bottom);
                    check2.setImageResource(R.mipmap.arrow_bottom);
                    flag = false;
                } else {
                    flag = true;
                    content1.setVisibility(View.GONE);
                    check1.setImageResource(R.mipmap.arrow_bottom);
                }
            }
        });
        check2.setOnClickListener(new View.OnClickListener() {
            boolean flag = true;

            @Override
            public void onClick(View v) {
                if (flag) {
                    content2.setVisibility(View.VISIBLE);
                    content.setVisibility(View.GONE);
                    content1.setVisibility(View.GONE);
                    check2.setImageResource(R.mipmap.arrow_top);
                    check1.setImageResource(R.mipmap.arrow_bottom);
                    check.setImageResource(R.mipmap.arrow_bottom);
                    flag = false;
                } else {
                    flag = true;
                    content2.setVisibility(View.GONE);
                    check2.setImageResource(R.mipmap.arrow_bottom);
                }
            }
        });
        final Dialog dialog = new Dialog(context, R.style.common_dialogwithani);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.setContentView(contentView);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }
}
