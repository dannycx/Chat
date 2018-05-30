package com.danny.chat.videocall;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.hyphenate.chat.EMClient;

/**
 * 电话广播接收者
 * Created by danny on 3/23/18.
 */

public class VideoCallReceiver extends BroadcastReceiver {
    private static final String TAG = VideoCallReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        // 判断环信是否登录成功
        if (!EMClient.getInstance().isLoggedInBefore()) {
            return;
        }
        String from = intent.getStringExtra("from");//// 呼叫方的usernmae
        String to = intent.getStringExtra("to");// 呼叫接收方
        String ext = EMClient.getInstance().callManager().getCurrentCallSession().getExt();
        Log.d(TAG, "扩展信息为: " + ext);
        Log.d(TAG, from + " 给 " + to + " 打电话");
        Intent videocallIntent = new Intent(context, VideoCallActivity.class);
        CallManager.getInstance().setCallType(CallManager.CallType.VIDEOCALL);
        CallManager.getInstance().setToChatId(from);
        CallManager.getInstance().setInComingCall(true);
        videocallIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(videocallIntent);
    }
}
