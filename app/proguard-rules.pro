# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/merlin/Library/Android/sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interfaces
# class:
#-keepclassmembers class fqcn.of.javascript.interfaces.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontpreverify
-dontoptimize
-verbose
-ignorewarnings
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*

#-dontwarn
-keepattributes InnerClasses
-keepattributes *Annotation*
-keepattributes Signature

-keep public class * extends android.app.Activity  #所有activity的子类不要去混淆
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class com.android.vending.licensing.ILicensingService #指定具体类不要去混淆

-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

#share jar
-keep class cn.sharesdk.**{*;}
-keep class com.sina.**{*;}
-keep class **.R$* {*;}
-keep class **.R{*;}
-keep class com.mob.**{*;}
-dontwarn com.mob.**
-dontwarn cn.sharesdk.**
-dontwarn **.R$*

-keep class cn.sharesdk.tencent.qq.** { *; }
-keep class cn.sharesdk.tencent.qzone.** { *; }
-keep class cn.sharesdk.sina.weibo.** { *; }
-keep class cn.sharesdk.wechat.friends.** { *; }
-keep class cn.sharesdk.wechat.utils.** { *; }
-keep class cn.sharesdk.wechat.moments.** { *; }

-dontwarn android.support.v4.**
-keep class android.support.v4.** { *; }
-keep interface android.support.v4.app.** { *; }
-keep public class * extends android.support.v4.**
-keep public class * extends android.app.Fragment

-keep class me.zhanghai.android.materialprogressbar.** { *; }
-keepclassmembers class com.jingyue.lygame.widget.DrawerNestScrollViewBehavior {
    public DrawerNestScrollViewBehavior();
}
##################################################################
# 下面都是项目中引入的第三方 jar 包。第三方 jar 包中的代码不是我们的目标和关心的对象，故而对此我们全部忽略不进行混淆。
##################################################################
## umeng start
-keepclassmembers class * {
   public <init> (org.json.JSONObject);
}

-dontwarn com.alibaba.sdk.android.**
-dontnote com.alibaba.sdk.android.**
-keep class com.alibaba.sdk.android.** {*;}

-keep public class com.jingyue.lygame.R$*{
public static final int *;
}
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
## umeng end

######for glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.AppGlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}

-dontwarn com.tencent.mm.opensdk.**
-keep class com.tencent.mm.opensdk.** { *; }
-keep class com.tencent.wxop.** { *; }
-keep class com.tencent.mm.sdk.** { *; }

# for DexGuard only

######butterknife
-keep class butterknife.** { *; }
-dontwarn butterknife.internal.**
-keep class **$$ViewBinder { *; }
-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}
-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}

-keep class com.jingyue.lygame.bean.**{*;} #保护javaBean
-keep class com.jingyue.lygame.events.** {*;}
-keep class com.lygame.libadapter.utils.** {*;}
-keep class com.jingyue.lygame.model.** {*;}
# keep annotated by NotProguard 保护使用NotProguard不混淆
# 特别注意内部类会被混淆掉
-keep class com.game.sdk.domain.NotProguard
-keep @com.game.sdk.domain.NotProguard class * {*;}

#DBFLow
-keep class com.raizlabs.android.dbflow.config.* {*;}

#youku
-keep class com.youku.** {*;}
-keep class com.ut.** {*;}


-keepclasseswithmembers class * {
    @com.game.sdk.domain.NotProguard <methods>;
}
#
-keepclasseswithmembers class * {
    @com.game.sdk.domain.NotProguard <fields>;
}
#
-keepclasseswithmembers class * {
    @com.game.sdk.domain.NotProguard <init>(...);
}

#okhttp-3.2.0.jar
-keepattributes Signature
-keepattributes *Annotation*
-dontwarn okhttp3.**
-dontnote okhttp3.**
-keep class okhttp3.** { *; }
-keep class okhttp3.internal.framed.** { *; }
-keep class okhttp3.internal.http.** { *; }
-keep class okhttp3.internal.io.** { *; }
-keep class okhttp3.internal.tls.** { *; }
-keep class okhttp3.internal.** { *; }

#umeng
-dontwarn com.umeng.**
-keep class com.umeng.** { *; }
-keep class com.umeng.analytics.** { *; }
-keep class com.umeng.analytics.** { *; }
-keep class com.umeng.analytics.onlineconfig.** { *; }
-keep class com.umeng.analytics.social.** { *; }
-keep class u.upd.** { *; }
-keep class u.aly.** { *; }

#=================  rxjava2  =================
-dontwarn io.reactivex.**
-keep class io.reactivex.** { *; }
-keep interface io.reactivex.** { *; }

#================= eventbus ===============
-keepclassmembers class ** {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }

# Only required if you use AsyncExecutor
-keepclassmembers class * extends org.greenrobot.eventbus.util.ThrowableFailureEvent {
    <init>(java.lang.Throwable);
}

#fastjson
-keep class com.alibaba.** {*;}

