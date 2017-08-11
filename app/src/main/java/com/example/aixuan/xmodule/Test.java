package com.example.aixuan.xmodule;

/**
 * Created by aixuan on 2017/7/26.
 */

import android.util.Log;
import android.widget.TextView;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static android.content.ContentValues.TAG;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;


public class Test implements IXposedHookLoadPackage{
    public static final String TAG = "Test";
    /**
     * 包加载时候的回调
     */
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {

        // 将包名不是 com.example.login 的应用剔除掉
        if (!lpparam.packageName.equals("com.example.aixuan.xmodule"))
            return;
        XposedBridge.log("Loaded app: " + lpparam.packageName);
        Class clazz = lpparam.classLoader.loadClass("com.example.aixuan.xmodule.MainActivity");

        // Hook MainActivity中的isCorrectInfo(String,String)方法
/*
        findAndHookMethod("com.example.aixuan.xmodule.MainActivity", lpparam.classLoader, "isCorrectInfo", String.class,
                String.class, new XC_MethodHook() {

                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        XposedBridge.log("开始劫持了~");
                        XposedBridge.log("参数1 = " + param.args[0]);
                        XposedBridge.log("参数2 = " + param.args[1]);
                        Log.i(TAG, "参数1 = " + param.args[0]);
                        Log.i(TAG, "参数2 = " + param.args[1]);
                    }

                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        XposedBridge.log("劫持结束了~");
                        XposedBridge.log("参数1 = " + param.args[0]);
                        XposedBridge.log("参数2 = " + param.args[1]);
                        Log.i(TAG, "参数1 = " + param.args[0]);
                        Log.i(TAG, "参数2 = " + param.args[1]);

                    }
                });
*/
        XposedBridge.hookAllMethods(clazz, "onClick", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                XposedBridge.log("beforeHookedMethod");
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                XposedBridge.log("afterHookedMethod");
            }
        });
    }
}
