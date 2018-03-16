package com.jingyue.lygame.modules.personal;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.jingyue.lygame.R;
import com.jingyue.lygame.utils.ResourceUtils;
import com.jingyue.lygame.utils.WebpUtils;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by zhanglei on 2017/9/21.
 */
public class DefaultAvaterAdapter extends RecyclerView.Adapter {
    private List<String> datas = new ArrayList<>();
    private ClickListener listener;
    private Context context;
    public DefaultAvaterAdapter(Context context,ClickListener clickListener) {
        this.listener = clickListener;
        this.context = context;
        for(int i=0;i<16;i++){
            if(i<9){
                datas.add("00"+String.valueOf(i+1));
            }else {
                datas.add("0"+String.valueOf(i+1));
            }
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.default_avater_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        ViewHolder vh = (ViewHolder)holder;
        String resName = "expr_"+datas.get(position);
        final int resId = ResourceUtils.getDrawableId(context,resName);
        vh.iv.setImageDrawable(WebpUtils.webpToDrawable(resId));
        vh.iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener!=null){
                    listener.onItemClick(resId);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv)
        ImageView iv;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public interface ClickListener {
        void onItemClick(int resId);
    }
}
