/*
 *  Copyright 2016 Eric Liu
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 */
package com.laoyuegou.android.lib.widget.ripple;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

/**
 * Created by Eric on 2016/11/2.
 */
public class Ripple {

    public static final String ARG_START_LOCATION = "START_LOCATION";

    private Ripple() {
    }

    public static int dip2px(Context context, float dip) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dip * scale + 0.5f);
    }

    public static void startActivity(Context context, Class<?> cls, View startView) {
        startActivity(context,cls,startView,null);
    }

    public static void startActivity(Context context, Class<?> cls, View startView,Bundle bundle) {
        int[] location = {0, 0};
        startView.getLocationOnScreen(location);
        location[1] = location[1] - dip2px(context, 25);
        Point point = new Point(location[0] + startView.getWidth() / 2, location[1] + startView.getHeight() / 2);
        startActivity(context, cls, point,bundle);
    }


    public static void startActivity(Context context, Class<?> cls, Point point) {
        startActivity(context,cls,point,null);
    }
    public static void startActivity(Context context, Class<?> cls, Point point,Bundle extra) {
        Intent intent = new Intent(context, cls);
        Bundle bundle = new Bundle();
        if(extra != null){
            bundle.putAll(extra);
        }
        bundle.putParcelable(ARG_START_LOCATION, point);
        intent.putExtras(bundle);
        context.startActivity(intent);
        if (context instanceof Activity) {
            ((Activity) context).overridePendingTransition(0, 0);
        }
    }

    public static void startActivityForResult(Activity activity, Class<?> cls, int requestCode,View startView) {
        int[] location = {0, 0};
        startView.getLocationOnScreen(location);
        location[1] = location[1] - dip2px(activity, 25);
        Point point = new Point(location[0] + startView.getWidth() / 2, location[1] + startView.getHeight() / 2);
        Intent intent = new Intent(activity, cls);
        Bundle bundle = new Bundle();
        bundle.putParcelable(ARG_START_LOCATION, point);
        intent.putExtras(bundle);
        activity.startActivityForResult(intent,requestCode);
        activity.overridePendingTransition(0, 0);
    }

    public static void startActivityForResult(Fragment fragment, Class<?> cls, int requestCode, View startView) {
        int[] location = {0, 0};
        startView.getLocationOnScreen(location);
        location[1] = location[1] - dip2px(fragment.getActivity(), 25);
        Point point = new Point(location[0] + startView.getWidth() / 2, location[1] + startView.getHeight() / 2);
        Intent intent = new Intent(fragment.getContext(), cls);
        Bundle bundle = new Bundle();
        bundle.putParcelable(ARG_START_LOCATION, point);
        intent.putExtras(bundle);
        fragment.startActivityForResult(intent,requestCode);
        fragment.getActivity().overridePendingTransition(0, 0);
    }
}
