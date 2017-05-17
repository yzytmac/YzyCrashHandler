package com.example.yzy.myapplication;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.util.Log;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * UncaughtException处理类,当程序发生Uncaught异常的时候,有该类来接管程序,并记录发送错误报告.
 * <p>
 * Created by yuyuhang on 15/12/7.
 */
public class CrashHandler implements UncaughtExceptionHandler {

    //系统默认的UncaughtException处理类
    private Thread.UncaughtExceptionHandler mDefaultHandler;
    //CrashHandler实例
    private static CrashHandler INSTANCE;
    //程序的Context对象
    private Context mContext;
    //用来存储设备信息和异常信息
    private Map<String, String> exceptInfo = new HashMap<String, String>();

    private CrashHandler() {
    }

    /**
     * 获取CrashHandler实例 ,单例模式
     */
    public static CrashHandler getInstance() {
        if (INSTANCE == null)
            INSTANCE = new CrashHandler();
        return INSTANCE;
    }

    /**
     *
     *@author YangZhenYu
     *created at 2017/3/22 21:50
     *功能： 
     */
    public void init(Context context) {
        mContext = context;
        //获取系统默认的UncaughtException处理器
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        //设置自己的这个CrashHandler为程序的默认处理器
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    /**
     * 当有未捕获的异常发生时执行该函数来处理
     */
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        //自定义的CrashHandler来处理未捕获的异常，如果异常处理的将返回true
        boolean vB = handleException(ex);
        //如果用户处理了就退出程序
        if(vB) {
            try {
                Thread.sleep(3000);
                //退出程序
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(1);
            } catch (InterruptedException pE) {
                pE.printStackTrace();
            }
        }else {
            //没有处理则用系统默认的异常处理器来处理
            if(mDefaultHandler != null) {
                mDefaultHandler.uncaughtException(thread, ex);
            }
        }
    }

    /**
     * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.
     *
     * @param ex
     * @return true:如果处理了该异常信息;否则返回false.
     */
    private boolean handleException(Throwable ex) {
        if (ex == null) {
            return false;
        }
        //收集设备参数信息
        collectDeviceInfo(mContext);
        //保存日志文件
        saveCrashInfo2File(ex);
        return true;
    }

    /**
     * 收集设备参数信息
     *
     * @param ctx
     */
    public void collectDeviceInfo(Context ctx) {
        try {
            PackageManager pm = ctx.getPackageManager();
            PackageInfo info = pm.getPackageInfo(ctx.getPackageName(), PackageManager.GET_ACTIVITIES);
            if (info != null) {
                String versionName = info.versionName == null ? "null" : info.versionName;
                String versionCode = info.versionCode + "";
                exceptInfo.put("versionName", versionName);
                exceptInfo.put("versionCode", versionCode);
            }
        } catch (NameNotFoundException e) {
            Log.e("yzy", "---118:" + e.toString());
        }
        //把整个Build类厘米那的信息都拿出来，如手机品牌、型号、系统版本等等信息
        Field[] fields = Build.class.getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                exceptInfo.put(field.getName(), field.get(null).toString());
            } catch (Exception e) {
                Log.e("yzy", "---127:" + e.toString());
            }
        }
    }

    /**
     * 保存错误信息到文件中
     *
     * @param ex
     * @return 返回文件名称, 便于将文件传送到服务器
     */
    private String saveCrashInfo2File(Throwable ex) {

        StringBuffer sb = new StringBuffer();
        sb.append("--------------start---------------");
        for (Map.Entry<String, String> entry : exceptInfo.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            sb.append(key + "=" + value + "\n");
        }

        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        //将异常信息写入printWriter中
        ex.printStackTrace(printWriter);

        Throwable cause = ex.getCause();
        while (cause != null) {
            //把异常原因写入printWriter中，其实写上面的异常信息就足够了
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        printWriter.close();
        String result = writer.toString();
        sb.append(result);
        sb.append("-------------end---------------");
        Log.e("yzy", "---163: " + sb.toString());//此处就可以将异常信息发送给服务器
        return null;
    }
}