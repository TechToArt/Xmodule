package com.aixuan.xposedforwechat;

import android.app.Activity;
import android.app.Application;
import android.app.Instrumentation;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.aixuan.reflect.Reflect;
import com.robotium.solo.Solo;
import com.robotium.solo.SystemUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Created by aixuan on 2017/8/9.
 */

public class CreateGroup implements IXposedHookLoadPackage {
    private static final String TAG = "CreateNewGroupChat";
    List<TextView> textViews = new ArrayList<>();
    private Solo solo;

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        if (!loadPackageParam.packageName.equals("com.tencent.mm"))
            return;
        Class launcherUIClass = loadPackageParam.classLoader.loadClass("com.tencent.mm.ui.LauncherUI");
        XposedHelpers.findAndHookMethod(launcherUIClass, "onCreate", Bundle.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                Log.i(TAG, "LauncherUI即将启动 "+System.currentTimeMillis());
            }

            @Override
            protected void afterHookedMethod(final MethodHookParam param) throws Throwable {
                Log.i(TAG, "LauncherUI启动完毕 "+ System.currentTimeMillis() );
                final Activity launcherActivity = (Activity) param.thisObject;
                Handler handler = new android.os.Handler(Looper.getMainLooper());
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            solo = soloInit(launcherActivity);
                            new Thread() {
                                @Override
                                public void run() {
                                    super.run();
                                    final Solo solo = soloInit(launcherActivity);
                                    Log.i(TAG, "当前线程：" + Thread.currentThread().getName() + " " + System.currentTimeMillis());
                                    solo.sleep(6000);
                                    Log.i(TAG, "即将点击通讯录 " + System.currentTimeMillis());
                                    solo.clickOnText("通讯录");
                                    solo.clickOnText("群聊");
                                    Log.i(TAG, "点击群聊结束");
                                    solo.sleep(1000);

                                    Activity chatroomContactActivity = solo.getCurrentActivity();
                                    View newGroupChat = null;
                                    View decorView = chatroomContactActivity.getWindow().getDecorView();
                                    getViewList(decorView);
                                    Log.i(TAG, "遍历结束 textViews.size()=" + textViews.size());
                                    for (int i = 0; i < textViews.size(); i++) {
                                        if ("新群聊".equals(textViews.get(i).getContentDescription())) {
                                            newGroupChat = textViews.get(i);
                                            Log.i(TAG, "找到新建群聊按钮");
                                            break;
                                        }
                                        //Log.i(TAG, "content-desc:"+textViews.get(i).getContentDescription());
                                    }
                                    solo.clickOnView(newGroupChat);
                                    solo.sleep(1000);

                                    //选择好友建群
                                    Activity selectContactActivity = solo.getCurrentActivity();

                                    ListView listView = (ListView) solo.getView("es");
                                    ListAdapter listAdapter = listView.getAdapter();
                                    int firstVisiblePosition = listView.getFirstVisiblePosition();

                                    /*Log.i(TAG, "listView HeaderViewsCount:" + listView.getHeaderViewsCount());
                                    Object item = listAdapter.getItem(listView.getHeaderViewsCount());
                                    int headerViews = listView.getHeaderViewsCount();
                                    Log.i(TAG, "item:"+item);
                                    Object[] items = new Object[listAdapter.getCount()];
                                    for (int i=0; i<listAdapter.getCount(); i++){
                                        items[i] = listAdapter.getItem(i);
                                    }
                                    Map<String, String> userMap = getGroupUsers(items,"chatroom item");
                                    Set<Map.Entry<String, String>> userSet = userMap.entrySet();
                                    for (Map.Entry<String, String> user : userSet){
                                        Log.i(TAG, user.getKey()+"："+user.getValue());
                                        int position = Integer.parseInt(user.getKey());
                                        Log.i(TAG, "position="+position);
                                        //判断当前显示页面是否有要勾选的复选框
                                        while(position>lastVisiblePosition){
                                            solo.drag(500, 500, listView.getChildAt(lastVisiblePosition).getY(),
                                                    listView.getChildAt(firstVisiblePosition).getY(), 55);
                                            firstVisiblePosition = listView.getFirstVisiblePosition();
                                            lastVisiblePosition = listView.getLastVisiblePosition();
                                        }
                                        View itemView = listView.getChildAt(position+headerViews);
                                        solo.clickOnView(itemView);
                                        *//*CheckBox checkBox = (CheckBox) itemView.findViewById(0x7f10020c);
                                        if (checkBox == null)
                                            Log.i(TAG, "checkBox == null");
                                        else
                                            Log.i(TAG, "checkBox != null");
                                        checkBox.setChecked(true);*//*

                                    }
                                    solo.clickOnView(solo.getView("gd"));*/

                                    //listView的遍历点击操作
                                    scrollListViewAndSelectItem(listView, 0x7f10020c, new MyItemCallback() {
                                        @Override
                                        public void handleItem(View view) {
                                            solo.clickOnView(view);
                                        }
                                    });
                                    int[] positions = {4, 8};
                                    //scrollListViewAndSelectItem(listView, positions);

                                }
                            }.start();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, 1000);
                /*new Thread(){
                    @Override
                    public void run() {
                        super.run();
                        try{

                        }catch (Exception e){
                            e.printStackTrace();
                        }


                    }
                }.start();*/
            }
        });
    }

    /**
     * 通过元素id在ListView中查找指定元素
     * @param listView  包含要查找元素的ListView
     * @param id        要查找元素的id
     */
    public void scrollListViewAndSelectItem(ListView listView, int id, MyItemCallback callback){
        Log.i("seek", "通过id查找元素的方法开始执行");
        int scrollHeight = 0;
        int width = listView.getWidth();
        int lastVisiblePosition =  listView.getLastVisiblePosition();
        Activity activity = (Activity) listView.getContext();
        Solo solo = soloInit(activity);
        Log.i("seek", "lastVisiblePosition="+lastVisiblePosition);
        Log.i("seek", "listView.getChildCount():"+listView.getChildCount());
        Log.i("seek", "listView.getCount():"+listView.getCount());
        Log.i("seek", "listView.getAdapter().getCount():"+listView.getAdapter().getCount());
        Log.i("seek", "listView.getHeaderViewsCount():"+listView.getHeaderViewsCount());
        Log.i("seek", "listView.getFooterViewsCount():"+listView.getFooterViewsCount());
        int startViewPosition = listView.getHeaderViewsCount();

        while(true){
            for (int i = startViewPosition; i <= lastVisiblePosition; i++) {
                Log.i("seek", "循环执行" + i);
                View item = listView.getChildAt(i-listView.getFirstVisiblePosition());
                Log.i("seek", "item position："+(i-listView.getFirstVisiblePosition())+" item:"+item);
                scrollHeight += item.getHeight();
                Log.i("seek", "即将根据id查找view");
                //复选框
                View nh = item.findViewById(id);
                Log.i("seek", "查找View结束");
                if (nh != null) {
                    Log.i("seek", "即将点击nh");
                    callback.handleItem(item);
                } else
                    continue;
                Log.i("seek", "点击" + i);
            }
            Log.i("seek", "for循环结束");
            startViewPosition = lastVisiblePosition+1;
            solo.drag(width / 2, width / 2, scrollHeight, 0, 55);
            lastVisiblePosition = listView.getLastVisiblePosition();
            Log.i("seek", "lastVisiblePosition"+lastVisiblePosition);
            if (lastVisiblePosition >= listView.getCount()-1){
                Log.i("seek", "开始sleep");
                solo.sleep(3000);
                lastVisiblePosition = listView.getLastVisiblePosition();
                Log.i("seek", "lastVisiblePosition"+lastVisiblePosition);
            }
            if (startViewPosition >= listView.getCount()-1)
                break;
            Log.i("seek", "startViewPosition:"+startViewPosition);
        }
        Log.i("seek", "while循环结束");
    }

    /**
     * 通过元素在ListView中的位置查找指定item
     * @param listView     包含要查找元素的ListView
     * @param positions    存放要查找元素位置的数组
     */
    public void scrollListViewAndSelectItem(ListView listView, int[] positions){
        Arrays.sort(positions);
        Log.i("seek", "通过id查找元素的方法开始执行");
        int scrollHeight = 0;
        int width = listView.getWidth();
        int lastVisiblePosition =  listView.getLastVisiblePosition();
        Activity activity = (Activity) listView.getContext();
        Solo solo = soloInit(activity);
        Log.i("seek", "lastVisiblePosition="+lastVisiblePosition);
        Log.i("seek", "listView.getChildCount():"+listView.getChildCount());
        Log.i("seek", "listView.getCount():"+listView.getCount());
        Log.i("seek", "listView.getAdapter().getCount():"+listView.getAdapter().getCount());
        Log.i("seek", "listView.getHeaderViewsCount():"+listView.getHeaderViewsCount());
        Log.i("seek", "listView.getFooterViewsCount():"+listView.getFooterViewsCount());
        Log.i("seek", "positions.length:"+positions.length);

        int startViewPosition = listView.getHeaderViewsCount();
        for (int i=0; i<positions.length; i++){
            Log.i("seek", "i:"+i);
            if (positions[i] >= listView.getFirstVisiblePosition()
                    && positions[i] <= listView.getLastVisiblePosition()){
                //当前显示界面有要查找的item
                View item = listView.getChildAt(positions[i] - listView.getFirstVisiblePosition());
                solo.clickOnView(item);
            }else if (positions[i] > listView.getLastVisiblePosition()){
                //当前界面中没有要查找的元素
                startViewPosition = lastVisiblePosition+1;
                solo.drag(width / 2, width / 2, listView.getHeight(), 0, 55);
                lastVisiblePosition = listView.getLastVisiblePosition();
                Log.i("seek", "lastVisiblePosition"+lastVisiblePosition);
                if (lastVisiblePosition >= listView.getCount()-1){
                    Log.i("seek", "开始sleep");
                    solo.sleep(3000);
                    lastVisiblePosition = listView.getLastVisiblePosition();
                    Log.i("seek", "lastVisiblePosition"+lastVisiblePosition);
                }
                if (startViewPosition >= listView.getCount()-1)
                    break;
                Log.i("seek", "startViewPosition:"+startViewPosition);
            }
        }
        Log.i("seek", "查找元素结束");
    }

    interface MyItemCallback {
        void handleItem(View view);
    }

    public Solo soloInit(Activity mActivity) {
        if(solo!=null){
            return solo;
        }
        Class clazz = null;
        try {
            Class<?> activityThreadClz = mActivity.getClassLoader().loadClass("android.app.ActivityThread");
            Method getMethod = activityThreadClz.getMethod("currentActivityThread");
            getMethod.setAccessible(true);
            Object atThreadObj = getMethod.invoke(null);
            System.out.println("currentActivityThread:"+atThreadObj);
            Method getInstrumentationMethod = activityThreadClz.getMethod("getInstrumentation");
            Instrumentation instrumentation = (Instrumentation) getInstrumentationMethod.invoke(atThreadObj);
            if (instrumentation != null && instrumentation.getClass().getName().contains("performance")) {
                instrumentation = Reflect.on(instrumentation).field("ieS").get().field("ieR").get().unwrap();
            }
            try {
                Field appContextField = Instrumentation.class.getDeclaredField("mAppContext");
                appContextField.setAccessible(true);
                appContextField.set(instrumentation, mActivity.getApplicationContext());
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("instrumentation:" + instrumentation);
             solo = new Solo(instrumentation);
            return solo;
        } catch (Exception e) {
            //Utils.log("test Exception" + e.toString());
            e.printStackTrace();
        }

        return null;

    }

    public void getViewList(View view) {
        if (view instanceof ViewGroup) {
            ViewGroup vg = (ViewGroup) view;
            for (int i = 0; i < vg.getChildCount(); i++) {
                getViewList(vg.getChildAt(i));
            }
        } else if (view instanceof TextView) {
            textViews.add((TextView) view);
        }
    }

    /**
     * 遍历args中各个元素包含的子元素
     * @param args      需要遍历的Object数组
     * @param headers   打印输出的头
     */
    private Map<String, String> getGroupUsers(Object[] args, String... headers) {
        int i = 0;
        Map<String, String> userMap = new LinkedHashMap<String, String>();
        for (Object arg : args) {
            //对需要遍历的对象的类型进行过滤
            if (arg instanceof String || arg instanceof Integer || arg == null) {
                i++;
                continue;
            }
            if (arg instanceof Intent) {
                String header = headers[0];
                if (headers == null || headers.length == 0) {
                    header = "printAllField";
                }
                Log.i(TAG, header + ((Intent) arg).getExtras());
                continue;
            }

            String position = null;
            String username = null;
            //得到要遍历对象中的字段
            Reflect on = Reflect.on(arg);
            Map<String, Reflect.FieldReflect> fields = on.fields();
            Set<Map.Entry<String, Reflect.FieldReflect>> entries = fields.entrySet();
            for (Map.Entry<String, Reflect.FieldReflect> e :
                    entries) {
                Reflect.FieldReflect value = e.getValue();
                Object param = value.get().unwrap();
                String header = null;
                if (headers == null || headers.length == 0) {
                    header = "printAllField";
                } else {
                    header = headers[0];
                }
                if ("position".equals(e.getKey()))
                    position = param.toString();
                if ("username".equals(e.getKey()))
                    username = param.toString();
                if (position != null && username != null){
                    userMap.put(position, username);
                }
                //Log.i(TAG, header + ": " + "index:" + i + " " + arg.getClass().getName() + " field name:" + e.getKey() + " field value:" + param);
            }
            //Log.i(TAG, "*************************************");
            i++;
        }
        //修改之后
        return userMap;
    }

}
