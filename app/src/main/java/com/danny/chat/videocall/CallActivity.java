package com.danny.chat.videocall;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import org.greenrobot.eventbus.EventBus;

/**
 * 打电话基类
 * Created by danny on 3/28/18.
 */

public class CallActivity extends AppCompatActivity {
    public int mCallOrAnswer = 0;
    private CallActivity mActivity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = this;
        //初始化界面,全屏,解锁,关闭输入法,屏幕常亮
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
    }

    public void initView() {
        if (CallManager.getInstance().getCallState() == CallManager.CallState.DISCONNECTED) {//默认状态
            // 收到呼叫或者呼叫对方时初始化通话状态监听
            CallManager.getInstance().setCallState(CallManager.CallState.CONNECTING);
            CallManager.getInstance().registerCallStateListener();
            CallManager.getInstance().playCallHintAudio();

            if (!CallManager.getInstance().isInComingCall()) {//默认接听
                CallManager.getInstance().makeVideoCall();//拨打电话
            }
        }
    }

    //拒接
    public void reject() {
        CallManager.getInstance().rejectCall();
        mCallOrAnswer = 1;
        onFinish();
    }

    //接听
    public void answer() {
        mCallOrAnswer = 1;
        CallManager.getInstance().answerCall();
    }

    //结束
    public void end() {
        mCallOrAnswer = 1;
        CallManager.getInstance().endCall();
        onFinish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);//注册观察者模式
    }

    @Override
    protected void onResume() {
        if (CallManager.getInstance().getCallState() == CallManager.CallState.DISCONNECTED) {
            onFinish();
            return;
        } else {
            CallManager.getInstance().cancelCallNotification();
        }
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);//取消观察者模式
    }

    //通话结束,销毁界面
    public void onFinish() {
        mActivity.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mActivity = null;
    }
}
