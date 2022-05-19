package com.example.rxjava;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;

import androidx.annotation.NonNull;

/**
 * Created by maozonghong
 * <p>
 * on 5/18/22
 **/
public class HookHelper {

    public static final String TAG = "HookHelper";

    public static final String EXTRA_TARGET_INTENT = "extra_target_intent";

    public static void hookAMSAidl() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
            hookIActivityTaskManager();
        } else {
            hookIActivityManager();
        }
    }

    private static void hookIActivityManager() {
        try {
            Field singletonFiled;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Class activityManager = Class.forName("android.app.ActivityManager");
                singletonFiled = activityManager.getDeclaredField("IActivityManagerSingleton");
            } else {
                Class activityManager = Class.forName("android.app.ActivityManagerNative");
                singletonFiled = activityManager.getDeclaredField("gDefault");
            }
            singletonFiled.setAccessible(true);

            Object singleton = singletonFiled.get(null);

            Class singletonClass = Class.forName("android.util.Singleton");

            Field mInstanceFiled = singletonClass.getDeclaredField("mInstance");

            mInstanceFiled.setAccessible(true);

            //原始的IActivityTaskManager对象
            Object iRawActivityTaskManager = mInstanceFiled.get(singleton);

            Object proxy = Proxy.newProxyInstance(Thread.currentThread()
                                                          .getContextClassLoader(),
                                                  new Class[]{
                                                          Class.forName(
                                                                  "android.app.IActivityManager")
                                                  }, new InvocationHandler() {
                        @Override
                        public Object invoke(Object proxy, Method method, Object[] args) throws
                                Throwable {
                            //托梁换柱
                            Intent raw = null;
                            int index = -1;
                            if ("startActivity".equals(method.getName())) {
                                Log.e(TAG, "invoke start activity 启动准备");

                                for (int i = 0; i < args.length; i++) {
                                    if (args[i] instanceof Intent) {
                                        raw = (Intent) args[i];
                                        index = i;
                                    }
                                }
                                if (raw != null && index != -1) {
                                    Log.e(TAG, "invoke raw:" + raw);
                                    //代替的Intent
                                    Intent newIntent = new Intent();
                                    newIntent.setComponent(new ComponentName("com.example.rxjava",
                                                                             StubActivity.class.getName()));
                                    newIntent.putExtra(EXTRA_TARGET_INTENT, raw);
                                    args[index] = newIntent;
                                }
                            }
                            return method.invoke(iRawActivityTaskManager, args);
                        }
                    });
            //新的IActivityTaskManager代理对象注入framework
            mInstanceFiled.set(singleton, proxy);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static void hookIActivityTaskManager() {

        try {

            Class activityTaskManagerClass = Class.forName("android.app.ActivityTaskManager");

            Field singletonField = activityTaskManagerClass.getDeclaredField(
                    "IActivityTaskManagerSingleton");

            singletonField.setAccessible(true);

            // IActivityTaskManagerSingleton对象
            Object singleton = singletonField.get(null);

            Class singletonClass = Class.forName("android.util.Singleton");

            Field mInstanceFiled = singletonClass.getDeclaredField("mInstance");
            mInstanceFiled.setAccessible(true);

            //原始的IActivityTaskManager对象
            Object iActivityTaskManager = mInstanceFiled.get(singleton);

            if (iActivityTaskManager == null) {
                Log.e(TAG, "iActivityTaskManager is null");
            }

            if (iActivityTaskManager != null) {
                Object proxy = Proxy.newProxyInstance(Thread.currentThread()
                                                              .getContextClassLoader(),
                                                      new Class[]{
                                                              Class.forName(
                                                                      "android.app.IActivityTaskManager")
                                                      }, new InvocationHandler() {
                            @Override
                            public Object invoke(Object proxy, Method method, Object[] args) throws
                                    InvocationTargetException,
                                    IllegalAccessException {
                                //托梁换柱
                                Intent raw = null;
                                int index = -1;
                                if ("startActivity".equals(method.getName())) {
                                    Log.e(TAG, "invoke start activity 启动准备");

                                    for (int i = 0; i < args.length; i++) {
                                        if (args[i] instanceof Intent) {
                                            raw = (Intent) args[i];
                                            index = i;
                                        }
                                    }
                                    if (raw != null && index != -1) {
                                        Log.e(TAG, "invoke raw:" + raw);
                                        //代替的Intent
                                        Intent newIntent = new Intent();
                                        newIntent.setComponent(new ComponentName(
                                                "com.example.rxjava",
                                                StubActivity.class.getName()));
                                        newIntent.putExtra(EXTRA_TARGET_INTENT, raw);
                                        args[index] = newIntent;
                                    }
                                }
                                return method.invoke(iActivityTaskManager, args);
                            }
                        });

                //新的IActivityTaskManager代理对象注入framework
                mInstanceFiled.set(singleton, proxy);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public static void hookInstrumentation(Activity activity) {
        try {
            Class activityClass = Activity.class;

//            Class activityThreadClass = Class.forName("android.app.ActivityThread");
//
//            Field mActivityThreadField = activityThreadClass.getDeclaredField(
//                    "sCurrentActivityThread");
//
//            mActivityThreadField.setAccessible(true);
//
//            Object currentActivityThread = mActivityThreadField.get(null);
//
////            通过Activity.class对象获取 mInstrumentation字段
//            Field field = activityThreadClass.getDeclaredField("mInstrumentation");
//            field.setAccessible(true);

            Field field = activityClass.getDeclaredField("mInstrumentation");
            field.setAccessible(true);

//            Instrumentation mInstrumentation = (Instrumentation) field.get(currentActivityThread);
            Instrumentation mInstrumentation = (Instrumentation) field.get(activity);

            //创建代理对象，Instrumentation是类 不是接口  所以只能用静态代理
            InstrumentationProxy instrumentationProxy = new InstrumentationProxy(mInstrumentation);

            //进行替换
//            field.set(currentActivityThread, instrumentationProxy);
            field.set(activity, instrumentationProxy);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    static class InstrumentationProxy extends Instrumentation {

        private static final String TAG = "InstrumentationProxy";

        private Instrumentation mBase;

        public InstrumentationProxy(Instrumentation instrumentation) {
            this.mBase = instrumentation;
        }

        public ActivityResult execStartActivity(
                Context who, IBinder contextThread, IBinder token, Activity target,
                Intent intent, int requestCode, Bundle options) {
            Log.e(TAG,
                  "执行了StartActivity,参数如下:" + "who=[" + who + "]," + "contextThread=[" + contextThread + "],"
                          + "token=[" + token + "]," + "target=[" + target + "]," + "intent=[" + intent + "]," + "requestCode=[" + requestCode + "],"
                          + "options=[" + options + "]");

            //由于这个方法是隐藏的 所以需要反射调用
            try {
                Method execStartActivity = Instrumentation.class.
                        getDeclaredMethod("execStartActivity",
                                          Context.class,
                                          IBinder.class,
                                          IBinder.class,
                                          Activity.class,
                                          Intent.class,
                                          int.class,
                                          Bundle.class);
                execStartActivity.setAccessible(true);
                Intent newIntent = new Intent();
                newIntent.setComponent(new ComponentName("com.example.rxjava",
                                                         StubActivity.class.getName()));
                newIntent.putExtra(EXTRA_TARGET_INTENT, intent);
                return (ActivityResult) execStartActivity.invoke(mBase,
                                                                 who,
                                                                 contextThread,
                                                                 token,
                                                                 target,
                                                                 newIntent,
                                                                 requestCode,
                                                                 options);
            } catch (Exception ex) {
                ex.printStackTrace();
                throw new RuntimeException("does not support please adapter it");
            }
        }
    }

    public static void hookHandler() {
        try {
            Class activityThreadClass = Class.forName("android.app.ActivityThread");
            Field mActivityThreadField = activityThreadClass.getDeclaredField(
                    "sCurrentActivityThread");
            mActivityThreadField.setAccessible(true);

            Object currentActivityThread = mActivityThreadField.get(null);

            Field mFieldH = activityThreadClass.getDeclaredField("mH");
            mFieldH.setAccessible(true);

            Handler mH = (Handler) mFieldH.get(currentActivityThread);

            Field filedCallBack = Handler.class.getDeclaredField("mCallback");
            filedCallBack.setAccessible(true);

            filedCallBack.set(mH, new Handler.Callback() {
                @Override
                public boolean handleMessage(@NonNull Message msg) {
                    Log.e(TAG, "handleMessage:" + msg.what);
                    switch (msg.what) {
                        case 159:   // mH.EXECUTE_TRANSACTION
                            Object object = msg.obj;
                            try {
                                Field mActivityCallbackField = object.getClass()
                                        .getDeclaredField("mActivityCallbacks");

                                mActivityCallbackField.setAccessible(true);

                                List mActivityCallbacks = (List) mActivityCallbackField.get(object);

                                if (mActivityCallbacks != null && mActivityCallbacks.size() > 0) {

                                    Log.e(TAG, "mCallBacks:" + mActivityCallbacks);

                                    String className = "android.app.servertransaction.LaunchActivityItem";

                                    for (int i = 0; i < mActivityCallbacks.size(); i++) {

                                        if (mActivityCallbacks.get(i)
                                                .getClass()
                                                .getCanonicalName()
                                                .equals(className)) {
                                            Object mTransactionItem = mActivityCallbacks.get(i);

                                            Field mIntentField = mTransactionItem.getClass()
                                                    .getDeclaredField("mIntent");

                                            mIntentField.setAccessible(true);

                                            //从intent中取出Target
                                            Intent intent = (Intent) mIntentField.get(
                                                    mTransactionItem);
                                            Intent targetIntent = intent.getParcelableExtra(
                                                    EXTRA_TARGET_INTENT);
                                            intent.setComponent(targetIntent.getComponent());

                                            break;
                                        }
                                    }
                                }

                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                            break;
                    }
                    mH.handleMessage(msg);
                    return true;
                }
            });

        } catch (Exception ex) {
            Log.e(TAG, "hookHandler:" + ex.getMessage());
            ex.printStackTrace();
        }
    }

}
