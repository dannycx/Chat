package com.danny.chat;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.IntentFilter;
import android.util.Log;
import android.widget.Toast;

import com.danny.chat.videocall.CallManager;
import com.danny.chat.videocall.VideoCallReceiver;
import com.hyphenate.EMConnectionListener;
import com.hyphenate.EMError;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMOptions;

import java.util.Iterator;
import java.util.List;

/**
 * 环信sdk初始化
 * Created by danny on 3/23/18.
 */

public class HuanxinApp extends Application {
    private static final String TAG = HuanxinApp.class.getSimpleName();
    private Context mContext;
    private VideoCallReceiver mCallReceiver;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        initHyphenate();
    }

    public Context getContext() {
        return mContext;
    }

    private void initHyphenate() {
        int pid = android.os.Process.myPid();
        String pName = getProcessName(pid);
        if (pName == null || !pName.equalsIgnoreCase(mContext.getPackageName())) return;
        EMOptions options = new EMOptions();
        options.setAutoLogin(true);//自动登录
        options.setAcceptInvitationAlways(false);
        EMClient.getInstance().init(mContext, options);
        EMClient.getInstance().setDebugMode(true);//测试阶段使用

        CallManager.getInstance().init(mContext);

        IntentFilter filter = new IntentFilter();
        filter.addAction(EMClient.getInstance().callManager().getIncomingCallBroadcastAction());
        if (mCallReceiver == null) {
            mCallReceiver = new VideoCallReceiver();
        }
        registerReceiver(mCallReceiver, filter);

//        setConnectionListener();
    }

    //设置链接监听
    private void setConnectionListener() {
        EMConnectionListener conn = new EMConnectionListener() {
            @Override
            public void onConnected() {
                Toast.makeText(mContext, "连接成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDisconnected(int i) {
                String str = "" + i;
                switch (i) {
                    case EMError.USER_REMOVED:
                        str = "账户被移除";
                        break;
                    case EMError.USER_LOGIN_ANOTHER_DEVICE:
                        str = "其他设备登录";
                        break;
                    case EMError.USER_KICKED_BY_OTHER_DEVICE:
                        str = "其他设备强制下线";
                        break;
                    case EMError.USER_KICKED_BY_CHANGE_PASSWORD:
                        str = "密码修改";
                        break;
                    case EMError.SERVER_SERVICE_RESTRICTED:
                        str = "被后台限制";
                        break;
                }
                Log.d(TAG, str);
            }
        };
        EMClient.getInstance().addConnectionListener(conn);
    }

    private String getProcessName(int id) {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        List list = manager.getRunningAppProcesses();
        Iterator i = list.iterator();
        while (i.hasNext()) {
            ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo) i.next();
            if (info.pid == id) {
                return info.processName;
            }
        }
        return null;
    }
}
