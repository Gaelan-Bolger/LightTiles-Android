# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:\android-sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.preference.Preference
-keep public class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
-keep public class roboguice.**
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}
-keepclassmembers class * extends android.content.Context {
    public void *(android.view.View);
    public void *(android.view.MenuItem);
}

# SupportLibrary.
-keep class android.support.v4.app.** { *; }
-keep interface android.support.v4.app.** { *; }

# SupportLibrary.v7
-keep class android.support.v7.** { *; }
-keep interface android.support.v7.** { *; }

# IAB
-keep public class com.android.vending.billing.IInAppBillingService
-keep class day.cloudy.apps.tiles.inappbilling.util.**
-keep class com.android.vending.billing.**
-keepattributes *Annotation*
-keep public class com.android.vending.licensing.ILicensingService

# GooglePlayServices
-keep class * extends java.util.ListResourceBundle {
    protected Object[][] getContents();
}
-keep public class com.google.android.gms.common.internal.safeparcel.SafeParcelable {
    public static final *** NULL;
}
-keepnames @com.google.android.gms.common.annotation.KeepName class *
-keepclassmembernames class * {
    @com.google.android.gms.common.annotation.KeepName *;
}
-keepnames class * implements android.os.Parcelable {
    public static final ** CREATOR;
}

#Google Analytics
-keep public class com.google.android.gms.**
-dontwarn com.google.android.gms.**

# Mint
-keep class com.splunk.** { *; }
-dontwarn com.splunk.**
#-libraryjars libs/mint-4.2.1.jar

# Parse
-keepattributes SourceFile,LineNumberTable
-keepattributes *Annotation*
-keepattributes Signature
-keep class com.parse.** { *; }
-keep class com.squareup.** { *; }
-dontwarn com.parse.**
-dontwarn com.squareup.**
-dontwarn com.squareup.picasso.**
-dontwarn com.facebook.**
-keepclasseswithmembernames class * { native <methods>; }

# LicensesDialog
-keep class de.psdev.licensesdialog.** { *; }

# ColorPickerPreference
-keep class net.margaritov.preference.colorpicker.** { *; }

# NumberPickerPreference
-keep class com.vanniktech.vntnumberpickerpreference.** { *; }

# NumberPickerCompat
-keep class net.simonvt.numberpicker.** { *; }

# EventBus
-keepclassmembers class ** {
    public void onEvent(**);
}
-keepclassmembers class ** {
    public void onEventMainThread(**);
}

# Permissions Dipatcher
-dontwarn permissions.dispatcher.processor.**
-keep class permissions.dispatcher.** { *; }
-keep class **PermissionsDispatcher { *; }
-keepclasseswithmembernames class * {
    @permissions.dispatcher.* <methods>;
}

# Butterknife
-keep class butterknife.** { *; }
-keep class **$$ViewBinder { *; }
-keep class **$$ViewInjector { *; }
-keepnames class * { @butterknife.InjectView *;}
-dontwarn butterknife.internal.**
-dontwarn butterknife.Views$InjectViewProcessor
-dontwarn com.gc.materialdesign.views.**
-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}
-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}

# Sugar ORM
-keep public class * extends com.orm.SugarRecord { *; }
-keepnames public class * extends com.orm.SugarRecord { *; }
-keepclassmembernames public class * extends com.orm.SugarRecord { *; }
-keep class com.orm.** { *; }

# Simple SQLite
-keepattributes *Annotation*,EnclosingMethod,Signature
-dontwarn javax.**

# Spotlight
-keep class com.wooplr.spotlight.** { *; }
-keep interface com.wooplr.spotlight.**
-keep enum com.wooplr.spotlight.**

# AppIntro
-keep class com.github.paolorotolo.** { *; }
-keep interface com.github.paolorotolo.**
-keep enum com.github.paolorotolo.**

# DebugDrawer
-keep class io.palaima.debugdrawer.** { *; }
-keep interface io.palaima.debugdrawer.**
-keep enum io.palaima.debugdrawer.**