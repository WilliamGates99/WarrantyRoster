# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
-renamesourcefileattribute SourceFile


#🍅---------------Begin: Proguard Configuration for Obfuscation Mapping---------------🍅
-printseeds obfuscation/seeds.txt
-printmapping obfuscation/mapping.txt
#🍅---------------End: Proguard Configuration for Obfuscation Mapping---------------🍅


#🍅---------------Begin: Proguard Configuration for Firebase Crashlytics---------------🍅
-keepattributes SourceFile,LineNumberTable        # Keep file names and line numbers.
-keep public class * extends java.lang.Exception  # Optional: Keep custom exceptions.
#🍅---------------End: Proguard Configuration for Firebase Crashlytics---------------🍅


#🍅---------------Begin: Missing Rules---------------🍅
# Please add these rules to your existing keep rules in order to suppress warnings.
# This is generated automatically by the Android Gradle plugin.
#🍅---------------End: Missing Rules---------------🍅