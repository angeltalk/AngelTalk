# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/actmember/Library/Android/sdk/tools/proguard/proguard-android.txt
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

# Platform calls Class.forName on types which do not exist on Android to determine platform.
-dontnote retrofit2.Platform
# Platform used when running on Java 8 VMs. Will not be used at runtime.
-dontwarn retrofit2.Platform$Java8
# Retain generic type information for use by reflection by converters and adapters.
-keepattributes Signature
# Retain declared checked exceptions for use by a Proxy instance.
-keepattributes Exceptions

-keep class android.support.v7.app.** { *; }
-keep interface android.support.v7.app.** { *; }

-dontwarn afu.org.checkerframework.checker.nullness.NullnessUtils
-dontwarn afu.org.checkerframework.checker.regex.RegexUtil
-dontwarn afu.org.checkerframework.checker.units.UnitsTools
-dontwarn com.google.common.util.concurrent.FuturesGetChecked$GetCheckedTypeValidatorHolder$ClassValueValidator
-dontwarn com.google.common.util.concurrent.FuturesGetChecked$GetCheckedTypeValidatorHolder$ClassValueValidator$1
-dontwarn afu.org.checkerframework.checker.formatter.FormatUtil
-dontwarn afu.org.checkerframework.checker.formatter.FormatUtil$Conversion
-dontwarn afu.org.checkerframework.checker.formatter.FormatUtil$IllegalFormatConversionCategoryException
-dontwarn com.google.errorprone.annotations.ForOverride
-dontwarn com.google.errorprone.annotations.IncompatibleModifiers
-dontwarn com.google.errorprone.annotations.RequiredModifiers
-dontwarn com.google.errorprone.annotations.Var