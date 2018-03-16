package com.jingyue.lygame.utils;

import android.text.TextUtils;

import com.jingyue.lygame.R;
import com.laoyuegou.android.lib.utils.Utils;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Formatter;
import java.util.Locale;

/**
 * Author       : yizhihao (Merlin)
 * Create time  : 2017-09-07 14:06
 * contact      :
 * 562536056@qq.com || yizhihao.hut@gmail.com
 */
public class StringUtils {

    public static final String NONE = "无";
    public static final String ZERO = "0";
    public static final String UNKNOW = "未知";

    public static boolean isEmptyOrNull(String str) {
        return (TextUtils.isEmpty(str) || "null".equals(str.trim()) || "".equals(str.trim()));
    }

    public static boolean isEmpty(String str) {
        return TextUtils.isEmpty(str);
    }

    /**
     * 将毫秒数转化为字符串格式的时长
     *
     * @param timeMs 目标毫秒数
     * @return 转换后的字符串
     */
    public static String stringForTime(long timeMs) {
        if (timeMs <= 0 || timeMs >= 24 * 60 * 60 * 1000) {
            return "00:00";
        }
        long totalSeconds = timeMs / 1000;
        long seconds = totalSeconds % 60;
        long minutes = (totalSeconds / 60) % 60;
        long hours = totalSeconds / 3600;

        StringBuilder stringBuilder = new StringBuilder();
        Formatter mFormatter = new Formatter(stringBuilder, Locale.getDefault());

        if (hours > 0) {
            return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return mFormatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }

    public static String optString(String serverText, String defaultValue) {
        if (isEmptyOrNull(serverText)) {
            return defaultValue;
        } else {
            return serverText;
        }
    }

    public static String optString(String serverText) {
        return optString(serverText, UNKNOW);
    }

    public static String optInt(String intStr) {
        if (TextUtils.isEmpty(intStr)) {
            return ZERO;
        } else {
            return intStr;
        }
    }

    public static String formatString(String content) {
        if (content.endsWith("\n")) {
            String s = content.substring(0, content.lastIndexOf("\n"));
            return s;
        } else {
            return content;
        }
    }

    public static String formatNumebr(String number) {
        try {
            int inumber = Integer.valueOf(number);
            if (inumber / 10000 > 0) {
                return String.format("%s.%sW", inumber / 10000, (inumber / 1000) % 10);
            }
        } catch (Exception e) {
        }
        return optString(number);
    }

    public static String generateTime(boolean isAdd) {
        String add = "";
        if (isAdd) {
            add = Utils.getContext().getString(R.string.a_0214);
        } else {
            add = Utils.getContext().getString(R.string.a_0215);
        }
        StringBuilder builder = new StringBuilder();
        Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        builder.append(sdf.format(d)).append(" ").append(add);
        return builder.toString();
    }

    public static String generateTime(int time, boolean isAdd) {
        String add = "";
        if (isAdd) {
            add = Utils.getContext().getString(R.string.a_0214);
        } else {
            add = Utils.getContext().getString(R.string.a_0215);
        }
        StringBuilder builder = new StringBuilder();
        builder.append(generateSpaceTime(time)).append(" ").append(add);
        return builder.toString();
    }

    //计算传过来的时间距离现在多少
    public static String generateSpaceTime(int createtime) {
        String result;
        StringBuilder time = new StringBuilder();
        long currentTime = System.currentTimeMillis() / 1000;
        int gap = (int) currentTime - createtime;
        int h = 0;
        boolean flag;
        String s = "";
        float hour = gap / 3600f;
        float min = gap / 60f;
        if (min < 1) {
            flag = false;
            h = gap;
            s = "秒前";
        } else if (hour < 1) {
            flag = false;
            h = gap / 60;
            s = "分钟前";
        } else if (hour < 24) {
            flag = false;
            h = (int) hour;
            s = "小时前";
        } else if (hour / 24 <= 30) {
            flag = false;
            h = gap / (3600 * 24);
            s = "天前";
        } else {
            flag = true;
        }
        if (flag) {
            long lt = new Long(createtime);
            Date date = new Date(lt * 1000);
            SimpleDateFormat sdf;
            sdf = new SimpleDateFormat("yyyy-MM-dd");
            result = deletZero(sdf.format(date));
        } else {
            result = time.append(String.valueOf(h)).append(s).toString();
        }

        return result;
    }

    public static String deletZero(String date) {
        String[] ss = date.split("-");
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < ss.length; i++) {
            if (ss[i].startsWith("0")) {
                builder.append(ss[i].replace("0", ""));
            } else {
                builder.append(ss[i]);
            }
            if (i < ss.length - 1) {
                builder.append("-");
            }
        }
        return builder.toString();
    }

    public static boolean isSameYear(int createtime) {
        Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String now = sdf.format(d);

        long lt = new Long(createtime);
        Date date = new Date(lt * 1000);
        String result = sdf.format(date);

        String[] s1 = now.split("-");
        String[] s2 = result.split("-");
        return s1[0].equals(s2[0]);
    }

    public boolean isNull(String str) {
        return TextUtils.isEmpty(str);
    }

    public static int getProgress(int total, int sofar) {
        if (total <= 0) return 0;
        return (int) (sofar * 100. / total);
    }

    public static String getRemainTime(int total, int sofar, int speed) {
        StringBuilder time = new StringBuilder();
        int num;
        String str;
        float second = (total - sofar) * 1.0f / (speed * 1024);
        if (second / 60 < 1) {
            num = (int) second;
            str = Utils.getContext().getString(R.string.a_0306);
        } else if (second / 3600 < 1) {
            num = (int) second / 60;
            str = Utils.getContext().getString(R.string.a_0307);
        } else {
            num = (int) second / 3600;
            str = Utils.getContext().getString(R.string.a_0308);
        }
        return time.append(String.valueOf(num)).append(str).toString();
    }

    public static String formateSpeed(int speed) {
        if (speed / 1024 < 1) {
            return String.valueOf(speed) + Utils.getContext().getString(R.string.a_0311);
        } else {
            float sd = speed * 1f / 1024;
            BigDecimal b = new BigDecimal(sd);
            float f1 = b.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
            return String.valueOf(f1) + Utils.getContext().getString(R.string.a_0312);
        }
    }

    public static String formateSize(int currentBytes) {
        float s = currentBytes * 1f / (1024 * 1024);
        BigDecimal b = new BigDecimal(s);
        float f1 = b.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();

        return String.valueOf(f1);
    }

}
