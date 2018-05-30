package com.danny.chat.videocall;

import com.hyphenate.chat.EMCallStateChangeListener;

/**
 * 观察者模式实时检测状态、时间
 * Created by danny on 3/28/18.
 */

public class CallEvent {
    private boolean mIsState;//是否在打通话
    private boolean mCheckTime;//通话时间
    private EMCallStateChangeListener.CallError mCallError;//状态监听错误码
    private EMCallStateChangeListener.CallState mCallState;//状态监听码

    public boolean isState() {
        return mIsState;
    }

    public void setState(boolean state) {
        mIsState = state;
    }

    public boolean isCheckTime() {
        return mCheckTime;
    }

    public void setCheckTime(boolean checkTime) {
        mCheckTime = checkTime;
    }

    public EMCallStateChangeListener.CallError getCallError() {
        return mCallError;
    }

    public void setCallError(EMCallStateChangeListener.CallError callError) {
        mCallError = callError;
    }

    public EMCallStateChangeListener.CallState getCallState() {
        return mCallState;
    }

    public void setCallState(EMCallStateChangeListener.CallState callState) {
        mCallState = callState;
    }
}
