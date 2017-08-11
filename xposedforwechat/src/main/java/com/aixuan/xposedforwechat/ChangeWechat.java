package com.aixuan.xposedforwechat;

import android.app.Activity;
import android.app.Application;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.aixuan.reflect.Reflect;
import com.robotium.solo.Solo;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Created by aixuan on 2017/7/27.
 */

public class ChangeWechat implements IXposedHookLoadPackage {

    Application application;
    ClassLoader mApplicationClassLoader;
    List<TextView> textViews = new ArrayList<TextView>();
    List<Button> buttons = new ArrayList<Button>();
    EditText mwg = null;
    private android.os.Handler handler = null;

    volatile int count = 0;

    @Override
    public void handleLoadPackage(
            final XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        //微信的启动界面com.tencent.mm.ui.LauncherUI
        // Application a = /null;
        if (!loadPackageParam.packageName.equals("com.tencent.mm"))
            return;


        /*
        1、更改界面
         */
        final Class launcherUIClass = loadPackageParam.classLoader.loadClass("com.tencent.mm.ui.LauncherUI");
        /*XposedHelpers.findAndHookMethod(launcherUIClass, "onResume", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {

            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Activity thisObject = (Activity) param.thisObject;
                View decorView = thisObject.getWindow().getDecorView();
                getViewList(decorView);
                XposedBridge.log("textViews.size()=" + textViews.size());
                for (int i = 0; i < textViews.size(); i++) {
                    ((TextView) textViews.get(i)).setBackgroundColor(0x00ff00);
                }

                for (int i = 0; i < buttons.size(); i++) {
                    if (((TextView) buttons.get(i)).getText().equals("send")) ;
                }
            }
        });*/

        /*
        2、打印日志
         */

        final Class logClass = loadPackageParam.classLoader.loadClass("com.tencent.mm.sdk.platformtools.v");

        XposedHelpers.findAndHookMethod(logClass, "a", String.class, Throwable.class, String.class, Object[].class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                StringBuilder sb = new StringBuilder("");
                for (int i = 0; i < param.args.length; i++) {
                    sb.append(param.args[i]);
                    sb.append(',');
                }
                Log.i("a", "param:" + sb.toString());
            }
        });
        XposedHelpers.findAndHookMethod(logClass, "d", String.class, String.class, Object[].class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                StringBuilder sb = new StringBuilder("");
                for (int i = 0; i < param.args.length; i++) {
                    sb.append(param.args[i]);
                    sb.append(',');
                }
                Log.i("d", "param:" + sb.toString());
            }
        });
        XposedHelpers.findAndHookMethod(logClass, "e", String.class, String.class, Object[].class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                StringBuilder sb = new StringBuilder("");
                for (int i = 0; i < param.args.length; i++) {
                    sb.append(param.args[i]);
                    sb.append(',');
                }
                Log.i("e", "param:" + sb.toString());

            }
        });

        XposedHelpers.findAndHookMethod(logClass, "f", String.class, String.class, Object[].class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                StringBuilder sb = new StringBuilder("");
                for (int i = 0; i < param.args.length; i++) {
                    sb.append(param.args[i]);
                    sb.append(',');
                }
                Log.i("f", "param:" + sb.toString());
            }
        });


        /*
        3、在发送的消息后面随机添加表情
         */
        Class chatFooterClass = loadPackageParam.classLoader.loadClass("com.tencent.mm.pluginsdk.ui.chat.ChatFooter");
        XposedHelpers.findAndHookMethod(chatFooterClass, "a", chatFooterClass, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {

            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                mwg = (EditText) param.getResult();
            }
        });

        XposedHelpers.findAndHookMethod("com.tencent.mm.pluginsdk.ui.chat.ChatFooter$2",
                loadPackageParam.classLoader, "onClick",
                View.class, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        /*Class chatFooterClass = loadPackageParam.classLoader.loadClass("com.tencent.mm.pluginsdk.ui.chat.ChatFooter");
                        Field field = XposedHelpers.findField(chatFooterClass, "mwg");
                        TextView mwg = (TextView) field.get(chatFooterClass.newInstance());
                        XposedBridge.log("obj:"+mwg.getText().toString());*/
                        int r = (int) (Math.random() * (0x1F636 - 0x1F600) + 0x1F600);
                        XposedBridge.log("mwg.getText():" + mwg.getText().toString() + " r=" + r);
                        String emoji = new String(Character.toChars(r));
                        mwg.setText(mwg.getText().toString() + emoji);
                    }

                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    }
                });


/*
        XposedBridge.hookAllMethods(launcherUIClass, "onClick", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                XposedBridge.log("beforeHookedMethod");
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                XposedBridge.log("afterHookedMethod");
            }
        });
*/

/*
        XposedHelpers.findAndHookMethod(launcherUIClass, "onClick",View.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                XposedBridge.log("beforeHookedMethod");
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                View view = (View) param.thisObject;
                view.setBackgroundColor(0xffff00);
                XposedBridge.log("afterHookedMethod");
            }
        });
*/

        /*
        4、操作微信界面，查看朋友圈
         */
        /*XposedHelpers.findAndHookMethod(launcherUIClass, "onCreate", Bundle.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {

            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                final Activity activity = (Activity) param.thisObject;
                handler = new android.os.Handler(Looper.getMainLooper());
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            final Solo solo = soloInit(activity);
                            new Thread() {
                                @Override
                                public void run() {
                                    super.run();
                                    solo.clickOnText("发现");
                                    try {
                                        sleep(1000);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    solo.clickOnText("朋友圈");
                                    try {
                                        sleep(3000);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    //点击头像
                                    //solo.clickOnView(solo.getView("j0"));

                                    //获取listView
                                    ListView listView = (ListView) solo.getView("cp7");
                                    *//*Log.i("listView", "listView.getChildCount()" + listView.getChildCount());
                                    Log.i("listView", "head:" + listView.getHeaderViewsCount());
                                    for (int i = 0; i < listView.getChildCount() + listView.getFooterViewsCount(); i++) {
                                        Log.i("listView", "lastVisible" + listView.getLastVisiblePosition());
                                        Log.i("listView", "Count:" + listView.getAdapter().getCount());
                                        ;
                                    }
                                    View item = listView.getChildAt(1);
                                    ArrayList<View> childs = solo.getViews(item);
                                    for (int i = 0; i < childs.size(); i++) {
                                        Log.i("childs", "" + childs.get(i));
                                    }*//*

                                    //listView的遍历点击操作
                                    int startViewPosition = listView.getHeaderViewsCount();
                                    int lastVisiblePosition = listView.getLastVisiblePosition();
                                    int scrollHeight = 0;
                                    int width = listView.getWidth();
                                    Log.i("seek", "lastVisiblePosition="+lastVisiblePosition);
                                    Log.i("seek", "listView.getChildCount():"+listView.getChildCount());
                                    Log.i("seek", "listView.getCount():"+listView.getCount());
                                    Log.i("seek", "listView.getAdapter().getCount():"+listView.getAdapter().getCount());
                                    Log.i("seek", "listView.getHeaderViewsCount():"+listView.getHeaderViewsCount());
                                    Log.i("seek", "listView.getFooterViewsCount():"+listView.getFooterViewsCount());


                                    while(true){

                                        for (int i = startViewPosition; i <= lastVisiblePosition; i++) {
                                            Log.i("seek", "循环执行" + i);
                                            View item = listView.getChildAt(i-listView.getFirstVisiblePosition());
                                            Log.i("seek", "item:"+item);
                                            scrollHeight += item.getHeight();
                                            Log.i("seek", "即将根据id查找view");
                                            //分享的文章
                                            View co4 = item.findViewById(0x7f101229);
                                            //发的照片
                                            View cmm = item.findViewById(0x7f1011f2);
                                            Log.i("seek", "查找View结束");
                                            if (co4 != null) {
                                                Log.i("seek", "即将点击co4");
                                                solo.clickOnView(co4);
                                            } else if (cmm != null) {
                                                Log.i("seek", "即将点击cmm");
                                                solo.clickOnView(cmm);
                                            } else
                                                continue;
                                            solo.sleep(2000);
                                            solo.goBack();
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
                            }.start();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, 5000);

            }
        });*/

        /*
        5、自动同意好友请求
         */
        Log.i("accept", "即将执行同意好友操作");
        Class contactStorageClass = loadPackageParam.classLoader.loadClass("com.tencent.mm.ar.c");
        final Class wClass = loadPackageParam.classLoader.loadClass("com.tencent.mm.storage.w");
        final Class acClass = loadPackageParam.classLoader.loadClass("com.tencent.mm.storage.ac");
        XposedHelpers.findAndHookMethod(contactStorageClass, "KR", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                count = (Integer) param.getResult();
                if ((Integer) param.getResult() > 0) {
                    Log.i("accept", "有好友请求 getNewCount=" + (Integer) param.getResult());
                    Method method = XposedHelpers.findMethodExact(acClass, "a", String.class, wClass);
/*
                    Log.i("accept", "method："+method);
                    method.invoke(acClass.newInstance(), new Object[]{ param.args[0],param.args[1]});
*/
                    //coord: (0,565,16) | addr: Lcom/tencent/mm/ui/k;->a(Lcom/tencent/mm/ui/k;Landroid/view/MenuItem;Lcom/tencent/mm/ui/k$a;)V
                    /*Class kClass = loadPackageParam.classLoader.loadClass("com.tencent.mm.ui.k");
                    Class kaClass = loadPackageParam.classLoader.loadClass("com.tencent.mm.ui.k$a");
                    XposedHelpers.findAndHookMethod(kClass, "a", kClass, MenuItem.class, kaClass, new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            Log.i("accept", "完成按钮点击 "+param.args[0].getClass().getName()+" "+param.args[1].getClass().getName()+" "+param.args[2].getClass().getName());
                            //View.OnClickListener onClickListener = (View.OnClickListener) param.args[2];
                            Field f =param.args[2].getClass().getDeclaredField("nPJ");
                            f.setAccessible(true);
                            MenuItem.OnMenuItemClickListener nPJ = (MenuItem.OnMenuItemClickListener) f.get(param.args[2]);
                            if (nPJ instanceof MenuItem.OnMenuItemClickListener)
                                Log.i("accept", "nPJ:"+f);
                            nPJ.onMenuItemClick((MenuItem)param.args[1]);
                        }
                    });*/

                    Log.i("accept", "同意好友执行成功");
                } else {
                    Log.i("accept", "getNewCount=" + (Integer) param.getResult());

                }
            }
        });


        Class applicationClass = loadPackageParam.classLoader.loadClass("com.tencent.mm.app.Application");
        XposedHelpers.findAndHookMethod(applicationClass, "onCreate", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                application = (Application) param.thisObject;
                mApplicationClassLoader = application.getClassLoader();
                final Class sayHiWithSnsPermissionUIclass = mApplicationClassLoader.loadClass("com.tencent.mm.plugin.profile.ui.SayHiWithSnsPermissionUI");
                XposedHelpers.findAndHookMethod(sayHiWithSnsPermissionUIclass, "On", new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        Activity activity = (Activity) param.thisObject;
                        Intent intent = activity.getIntent();
                        Log.i("userInfo", intent.getExtras().toString());

                    }
                });
                //coord: (0,78,31) | addr: Lcom/tencent/mm/plugin/profile/ui/SayHiWithSnsPermissionUI$a;->onClick(Landroid/view/View;)V | loc: ?
                /*Class sayHiWithSnsPermissionUIaclass = mApplicationClassLoader.loadClass("com.tencent.mm.plugin.profile.ui.SayHiWithSnsPermissionUI$a");
                XposedHelpers.findAndHookMethod(sayHiWithSnsPermissionUIaclass, "onClick", View.class, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {

                    }

                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        Log.i("accept", "$a的onClick执行");
                    }
                });*/

                //Lcom/tencent/mm/ar/c
                //.method public final a(Ljava/lang/String;Lcom/tencent/mm/sdk/d/l;)V


                //coord: (0,735,28) | addr: Lcom/tencent/mm/storage/ac;->P(Lcom/tencent/mm/storage/w;)Z
                /*Class pClass = loadPackageParam.classLoader.loadClass("com.tencent.mm.storage.ac");
                Class wClass = loadPackageParam.classLoader.loadClass("com.tencent.mm.storage.w");
                XposedHelpers.findAndHookMethod(pClass, "P", wClass, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        Log.i("accept", "P方法执行");
                        Class launcherUIClass = loadPackageParam.classLoader.loadClass("com.tencent.mm.ui.LauncherUI");
                        Intent addFriend = new Intent(application, sayHiWithSnsPermissionUIclass);
                        addFriend.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        addFriend.putExtra("Contact_RemarkName", "");
                        addFriend.putExtra("Contact_Nick", "VERTU");
                        addFriend.putExtra("Contact_User", "wxid_2wmipjg34hvc22");
                        addFriend.putExtra("Contact_Scene", 30);
                        addFriend.putExtra("Verify_ticket", "v2_4f4d09a8f10517599241097c2b28aabd03672513c4d8bceb647207cbb964c4184e1f264ddcebfff5483135b410090139d37134349726aff268ea9d6f242571fc@stranger");
                        addFriend.putExtra("sayhi_with_sns_perm_set_label", true);
                        addFriend.putExtra("sayhi_with_sns_perm_add_remark", true);
                        addFriend.putExtra("sayhi_with_sns_perm_send_verify", false);
                        application.startActivity(addFriend);
                    }
                });*/


                //获取微信id
                //Lcom/tencent/mm/ar/c;->kK(Ljava/lang/String;)Lcom/tencent/mm/ar/b;
                Class cClass = loadPackageParam.classLoader.loadClass("com.tencent.mm.ar.c");
                XposedHelpers.findAndHookMethod(cClass, "kK", String.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        Log.i("accept", "wxid:" + param.args[0]);
                        if (count > 0) {
                            Log.i("accept", "count=" + count);
                            Class launcherUIClass = loadPackageParam.classLoader.loadClass("com.tencent.mm.ui.LauncherUI");
                            Intent addFriend = new Intent(application, sayHiWithSnsPermissionUIclass);
                            addFriend.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            addFriend.putExtra("Contact_RemarkName", "");
                            addFriend.putExtra("Contact_Nick", "");
                            addFriend.putExtra("Contact_User", param.args[0].toString());
                            addFriend.putExtra("Contact_Scene", 30);
                            addFriend.putExtra("Verify_ticket", param.args[0].toString());
                            addFriend.putExtra("sayhi_with_sns_perm_set_label", true);
                            addFriend.putExtra("sayhi_with_sns_perm_add_remark", true);
                            addFriend.putExtra("sayhi_with_sns_perm_send_verify", false);
                            application.startActivity(addFriend);
                            Log.i("accept", "启动同意界面完成");

                            XposedHelpers.findAndHookMethod(sayHiWithSnsPermissionUIclass, "onCreate", Bundle.class, new XC_MethodHook() {
                                @Override
                                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                                    Log.i("accept", "同意好友页面创建完成");
                                    final Activity activity = (Activity) param.thisObject;
                                    handler = new android.os.Handler(Looper.getMainLooper());
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                final Solo solo = soloInit(activity);
                                                new Thread() {
                                                    @Override
                                                    public void run() {
                                                        super.run();
                                                        Log.i("accept", "run开始执行");
                                                        solo.clickOnText("完成");
                                                        Log.i("accept", "run执行完毕 count=" + count);
                                                    }
                                                }.start();
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }, 1000);
                                }
                            });
                        }
                    }
                });


                //获取ticket
                // Lcom/tencent/mm/model/n;->eE(Ljava/lang/String;)Z
                Class nClass = loadPackageParam.classLoader.loadClass("com.tencent.mm.model.n");
                XposedHelpers.findAndHookMethod(nClass, "eE", String.class, new

                        XC_MethodHook() {
                            @Override
                            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                                Log.i("accept", "ticket:" + param.args[0].toString());
                            }
                        });


            }
        });





/*
        XposedHelpers.findAndHookMethod(acClass, "a", String.class, wClass, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                Log.i("accept", "a即将执行");
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {

                Method method = XposedHelpers.findMethodExact(acClass, "a", String.class, wClass);
                Log.i("accept", "method"+method);
                method.invoke(acClass.newInstance(), new Object[]{ param.args[0],param.args[1]});
                Log.i("accept", "同意好友执行成功");
            }
        });
*/

        Log.i("accept", "同意好友执行完毕");
    }

    public void getViewList(View view) {
        if (view instanceof ViewGroup) {
            ViewGroup vg = (ViewGroup) view;
            for (int i = 0; i < vg.getChildCount(); i++) {
                getViewList(vg.getChildAt(i));
            }
        } else if (view instanceof TextView) {
            textViews.add((TextView) view);
        } else if (view instanceof Button) {
            buttons.add((Button) view);
        }
    }

    private void printView(ViewGroup view, int deep) {
        String viewgroupDeepFormat = "";
        String viewDeepFormat = "";
        for (int i = 0; i < deep - 1; i++) {
            viewgroupDeepFormat += "\t";
        }
        viewDeepFormat = viewgroupDeepFormat + "\t";
        XposedBridge.log(viewgroupDeepFormat + view.toString());
        Log.i("LauncherUI", viewgroupDeepFormat + view.toString());
        int count = view.getChildCount();
        for (int i = 0; i < count; i++) {
            if (view.getChildAt(i) instanceof ViewGroup) {
                printView((ViewGroup) view.getChildAt(i), deep + 1);
            } else {
                XposedBridge.log(viewDeepFormat + view.getChildAt(i).toString());
                Log.i("LauncherUI", viewDeepFormat + view.getChildAt(i).toString());
            }
        }
    }

    /**
     * Instrumentation 初始化
     */
    public Solo soloInit(Activity mActivity) {
        Class clazz = null;
        try {
            Class<?> activityThreadClz = mActivity.getClassLoader().loadClass("android.app.ActivityThread");
            Method getMethod = activityThreadClz.getMethod("currentActivityThread",
                    null);
            getMethod.setAccessible(true);
            Object atThreadObj = getMethod.invoke(null, null);
            Method getInstrumentationMethod = activityThreadClz.getMethod("getInstrumentation", null);
            Instrumentation instrumentation = (Instrumentation) getInstrumentationMethod.invoke(atThreadObj, null);
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
            Solo solo = new Solo(instrumentation);
            return solo;
        } catch (Exception e) {
            //Utils.log("test Exception" + e.toString());
            e.printStackTrace();
        }

        return null;

    }
}
