package in.srain.cube.views.ptr;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import in.srain.cube.views.ptr.header.MaterialHeader;

import in.srain.cube.views.ptr.util.PtrLocalDisplay;

public class PtrClassicFrameLayout extends PtrFrameLayout {

    private MaterialHeader mPtrClassicHeader;
    private MaterialHeader mPtrClassicFooter;

    public PtrClassicFrameLayout(Context context) {
        super(context);
        initViews();
    }

    public PtrClassicFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViews();
    }

    public PtrClassicFrameLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initViews();
    }

    private void initViews() {
        setDurationToClose(50);
        mPtrClassicHeader = new MaterialHeader(getContext());
        mPtrClassicHeader.setLayoutParams(new PtrFrameLayout.LayoutParams(-1, -2));
        mPtrClassicHeader.setPadding(0, PtrLocalDisplay.dp2px(15), 0, PtrLocalDisplay.dp2px(10));
        mPtrClassicHeader.setPtrFrameLayout(this);
        setHeaderView(mPtrClassicHeader);
        addPtrUIHandler(mPtrClassicHeader);
        mPtrClassicFooter = new MaterialHeader(getContext());
        mPtrClassicFooter.setLayoutParams(new PtrFrameLayout.LayoutParams(-1, -2));
        mPtrClassicFooter.setPadding(0, PtrLocalDisplay.dp2px(15), 0, PtrLocalDisplay.dp2px(10));
        setFooterView(mPtrClassicFooter);
        addPtrUIHandler(mPtrClassicFooter);
        setPinContent(false,true);
        setEnabledNextPtrAtOnce(true);
        //setPullToRefresh(true);
    }

}
