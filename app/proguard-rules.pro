#指定压缩级别
-optimizationpasses 5

#不跳过非公共的库的类成员
-dontskipnonpubliclibraryclassmembers

#混淆时采用的算法
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*

#把混淆类中的方法名也混淆了
-useuniqueclassmembernames

#指定不去忽略非公共的库的类
-dontskipnonpubliclibraryclasses

#不做预检验，preverify是proguard的四大步骤之一,可以加快混淆速度
-dontpreverify
-verbose
-printmapping proguardMapping.txt


# 忽略警告（？）
#-ignorewarnings

#混淆时不使用大小写混合，混淆后的类名为小写(大小写混淆容易导致class文件相互覆盖）
-dontusemixedcaseclassnames

#优化时允许访问并修改有修饰符的类和类的成员
-allowaccessmodification

#将文件来源重命名为“SourceFile”字符串
-renamesourcefileattribute SourceFile
#保留行号
-keepattributes SourceFile,LineNumberTable
#保持泛型
-keepattributes Signature
# 保持注解
-keepattributes *Annotation*,InnerClasses

-keepattributes EnclosingMethod, InnerClasses
-flattenpackagehierarchy androidx.base
-repackageclasses androidx.base


# 保持测试相关的代码
-dontnote junit.framework.**
-dontnote junit.runner.**
-dontwarn android.test.**
-dontwarn android.support.test.**
-dontwarn org.junit.**

# Parcelable
-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}
# Serializable
-keepnames class * implements java.io.Serializable
-keepclassmembers class * implements java.io.Serializable {
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# 保留R下面的资源
-keep class **.R$* {*;}

# 保留四大组件，自定义的Application,Fragment等这些类不被混淆
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Fragment
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference

# 保留我们使用的四大组件，自定义的Application等等这些类不被混淆
# 因为这些子类都有可能被外部调用
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application.**
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class * extends android.view.View
-keep public class com.android.vending.licensing.ILicensingService.**

# 保留support下的所有类及其内部类
-keep class android.support.** {*;}
# 保留继承的
-keep public class * extends android.support.v4.**
-keep public class * extends android.support.v7.**
-keep public class * extends android.support.annotation.**

-keep class com.google.android.material.** { *; }
-keep class androidx.** { *; }
-keep interface androidx.** { *; }
-keep class org.xmlpull.v1.** {*;}
-keep class **.R$* {*;}
# 保留本地native方法不被混淆
-keepclasseswithmembernames class * {
    native <methods>;
}


## support
-dontwarn android.support.**
-keep class android.support.v4.app.** { *; }
-keep interface android.support.v4.app.** { *;}
-keep public class * extends android.support.v7.**
-keep public class * extends android.support.annotation.**

-keep public class * extends android.support.v4.view.ActionProvider {
    public <init>(android.content.Context);
}

# 保留枚举类不被混淆
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# 保留在Activity中的方法参数是view的方法，
# 这样以来我们在layout中写的onClick就不会被影响
-keepclassmembers class * extends android.app.Activity{
    public void *(android.view.View);
}

# 保留本地native方法不被混淆
-keepclasseswithmembers class * {
    native <methods>;
}

# 对于带有回调函数的onXXEvent、**On*Listener的，不能被混淆
-keepclassmembers class * {
    void *(**On*Event);
    void *(**On*Listener);
}

-keepclassmembers public class * extends android.view.View {
   void set*(***);
   *** get*();
}

#保留在Activity中的方法参数是view的方法，
-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}

# For XML inflating, keep views' constructoricon.png    自定义view
-keep public class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
    public void set*(...);
}
# androidx 混淆
-keep class com.google.android.material.** {*;}
-keep class androidx.** {*;}
-keep public class * extends androidx.**
-keep interface androidx.** {*;}
-dontwarn com.google.android.material.**
-dontnote com.google.android.material.**
-dontwarn androidx.**
-printconfiguration
-keep,allowobfuscation @interface androidx.annotation.Keep

-keep @androidx.annotation.Keep class *
-keepclassmembers class * {
    @androidx.annotation.Keep *;
}
# google gson
-keep class com.google.gson.** { *; }
-keep class com.google.** { *;}

# OkHttp3
-keep class com.squareup.okhttp3.** { *;}
-keep interface com.squareup.okhttp3.** { *;}
-keep class okio.**{*;}
-keep interface okio.**{*;}


-keep class com.orhanobut.hawk.** { *; }

# 保留Parcelable序列化类不被混淆
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

# 保留Serializable序列化的类不被混淆
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    !static !transient <fields>;
    !private <fields>;
    !private <methods>;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# 对于带有回调函数的onXXEvent、**On*Listener的，不能被混淆
-keepclassmembers class * {
    void *(**On*Event);
    void *(**On*Listener);
}
#xwalk
-keep class org.xwalk.core.** { *; }
-keep class org.crosswalk.engine.** { *; }
-keep class org.chromium.** { *; }
-dontwarn android.view.**
-dontwarn android.media.**
-dontwarn org.chromium.**
#okhttp
-keep class okhttp3.**{*;}
#okio
-keep class okio.**{*;}
#loadsir
-keep class com.kingja.loadsir.** {*;}
#gson
# Gson specific classes
#-keep class com.google.gson.stream.** { *; }
# Application classes that will be serialized/deserialized over Gson
-keep class com.google.gson.examples.android.model.** { <fields>; }
# Prevent proguard from stripping interface information from TypeAdapter, TypeAdapterFactory,
# JsonSerializer, JsonDeserializer instances (so they can be used in @JsonAdapter)
-keep class * extends com.google.gson.TypeAdapter
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer
# Prevent R8 from leaving Data object members always null
-keepclassmembers,allowobfuscation class * {
  @com.google.gson.annotations.SerializedName <fields>;
}
#xstream
-keep class com.thoughtworks.xstream.converters.extended.SubjectConverter { *; }
-keep class com.thoughtworks.xstream.converters.extended.ThrowableConverter { *; }
-keep class com.thoughtworks.xstream.converters.extended.StackTraceElementConverter { *; }
-keep class com.thoughtworks.xstream.converters.extended.CurrencyConverter { *; }
-keep class com.thoughtworks.xstream.converters.extended.RegexPatternConverter { *; }
-keep class com.thoughtworks.xstream.converters.extended.CharsetConverter { *; }
-keep class com.thoughtworks.xstream.** { *; }
#eventbus
-keepclassmembers class * {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }
# And if you use AsyncExecutor:
-keepclassmembers class * extends org.greenrobot.eventbus.util.ThrowableFailureEvent {
    <init>(java.lang.Throwable);
}
#bugly
-keep public class com.tencent.bugly.**{*;}
-keep class android.support.**{*;}

#dkplayer
-keep class com.dueeeke.videoplayer.** { *; }

# IjkPlayer
-keep class tv.danmaku.ijk.** { *; }

# ExoPlayer
-keep class com.google.android.exoplayer2.** { *; }

# 实体类
#-keep class com.github.tvbox.osc.bean.** { *; }
#CardView
-keep class com.github.tvbox.osc.ui.tv.widget.card.**{*;}
#ViewObj
-keep class com.github.tvbox.osc.ui.tv.widget.ViewObj{
    <methods>;
}

-keep class com.github.catvod.crawler.*{*;}
# 迅雷下载模块
-keep class com.xunlei.downloadlib.** {*;}
# quickjs引擎
-keep class com.whl.quickjs.** {*;}
# 支持影视的ali相关的jar
-keep class com.google.gson.**{*;}
# Zxing
-keep class com.google.zxing.**{*;}
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# 保留我们自定义控件（继承自View）不被混淆
-keep public class * extends android.view.View{
    *** get*();
    void set*(***);
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

-keep public class * extends androidx.recyclerview.widget.RecyclerView$LayoutManager{
    *** get*();
    void set*(***);
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

-keep class com.orhanobut.hawk.** { *; }

# 保留Parcelable序列化类不被混淆
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

# 保留Serializable序列化的类不被混淆
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    !static !transient <fields>;
    !private <fields>;
    !private <methods>;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# 对于带有回调函数的onXXEvent、**On*Listener的，不能被混淆
-keepclassmembers class * {
    void *(**On*Event);
    void *(**On*Listener);
}

-keep class okhttp3.**{*;}

-keep class okio.**{*;}
-keep class com.kingja.loadsir.** {*;}
-keep class com.google.gson.examples.android.model.** { <fields>; }
# JsonSerializer, JsonDeserializer instances (so they can be used in @JsonAdapter)
-keep class * extends com.google.gson.TypeAdapter
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer
# Prevent R8 from leaving Data object members always null
-keepclassmembers,allowobfuscation class * {
  @com.google.gson.annotations.SerializedName <fields>;
}
#xstream
-keep class com.thoughtworks.xstream.converters.extended.SubjectConverter { *; }
-keep class com.thoughtworks.xstream.converters.extended.ThrowableConverter { *; }
-keep class com.thoughtworks.xstream.converters.extended.StackTraceElementConverter { *; }
-keep class com.thoughtworks.xstream.converters.extended.CurrencyConverter { *; }
-keep class com.thoughtworks.xstream.converters.extended.RegexPatternConverter { *; }
-keep class com.thoughtworks.xstream.converters.extended.CharsetConverter { *; }
-keep class com.thoughtworks.xstream.** { *; }
#eventbus
-keepclassmembers class * {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }
# And if you use AsyncExecutor:
-keepclassmembers class * extends org.greenrobot.eventbus.util.ThrowableFailureEvent {
    <init>(java.lang.Throwable);
}
-keep public class com.tencent.bugly.**{*;}
-keep class android.support.**{*;}

#dkplayer
-keep class com.dueeeke.videoplayer.** { *; }
-keep class tv.danmaku.ijk.** { *; }
-keep class com.google.android.exoplayer2.** { *; }

# 实体类
#-keep class com.github.tvbox.osc.bean.** { *; }
#CardView
-keep class com.github.tvbox.osc.ui.tv.widget.card.**{*;}
#ViewObj
-keep class com.github.tvbox.osc.ui.tv.widget.ViewObj{
    <methods>;
}

-keep class com.github.catvod.crawler.*{*;}
# 迅雷下载模块
-keep class com.xunlei.downloadlib.** {*;}
# quickjs引擎
-keep class com.whl.quickjs.** {*;}
# 支持影视的ali相关的jar
-keep class com.google.gson.**{*;}
# Zxing
-keep class com.google.zxing.**{*;}
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
-keep class androidx.media3.**{*;}