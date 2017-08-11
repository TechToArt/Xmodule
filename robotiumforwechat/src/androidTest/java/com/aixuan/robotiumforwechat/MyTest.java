package com.aixuan.robotiumforwechat;

import android.app.Activity;
import android.app.Instrumentation;
import android.test.ActivityInstrumentationTestCase2;

import com.robotium.solo.Solo;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by aixuan on 2017/8/1.
 */

public class MyTest extends ActivityInstrumentationTestCase2{
    private static final String TEST_ACTIVITY_NAME = "com.aixuan.robotiumforwechat.MainActivity";
    private static Class<?> testActivityClass;
    private Solo solo = null;
    static {
        try {

            testActivityClass = Class.forName(TEST_ACTIVITY_NAME);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    public MyTest(String pkg, Class activityClass) {
        super(pkg, activityClass);
    }

    public MyTest(Class activityClass) {
        super(activityClass);
    }

    public MyTest(){
        super(testActivityClass);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        solo = new Solo(getInstrumentation());
        getActivity();
    }

    @Override
    protected void tearDown() throws Exception {
        solo.finishOpenedActivities();
        super.tearDown();
    }

    public void testRun() {
        getInstrumentation();
        solo.clickOnText("按钮");
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
            Solo solo = new Solo(instrumentation);
            return solo;
        } catch (Exception e) {
            //Utils.log("test Exception" + e.toString());
            e.printStackTrace();
        }

            return null;

    }
}
