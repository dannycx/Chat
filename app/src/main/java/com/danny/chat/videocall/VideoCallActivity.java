package com.danny.chat.videocall;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.danny.chat.R;
import com.danny.chat.data.call.CallDataSource;
import com.danny.chat.data.call.CallRepository;
import com.danny.chat.data.call.MissedCall;
import com.hyphenate.chat.EMCallStateChangeListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;
import com.hyphenate.media.EMCallSurfaceView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 视频通话界面
 * Created by danny on 3/28/18.
 */

public class VideoCallActivity extends CallActivity implements View.OnClickListener {
    private static final String TAG = VideoCallActivity.class.getSimpleName();
    private static VideoCallActivity sInstance = null;
    private VideoCallActivity mActivity;
    private int mSurfaceState = -1;//-1 表示通话没有接通  0 表示本小远大  1 表示远小本大

    private int mLittleWidth;
    private int mLittleHeight;
    private int mRightMargin;
    private int mTopMargin;

    private EMCallSurfaceView mLocalSurface;
    private EMCallSurfaceView mOppositeSurface;
    private RelativeLayout.LayoutParams mLocalParams;
    private RelativeLayout.LayoutParams mOppositeParams;

    private RelativeLayout mSurfaceParent;
    private RelativeLayout mViewContentDisplay;
    private TextView mTvCallState;
    private TextView mTvCallTime;
    private TextView mTvCallContact;
    private ImageButton mIbMic;
    private ImageButton mIbSpeaker;
    private FloatingActionButton mFabReject;
    private FloatingActionButton mFabAnswer;
    private FloatingActionButton mFabEnd;

    private int mDownTime;
    private Timer mTimer;
    private TimerTask mTask;

    private MissedCall mMissedCall;
    private CallRepository mCallRepository;
    private boolean mIsConnect;
    private NotificationMissedCallListener mMissedCallListener;

    public static VideoCallActivity getInstance() {
        if (sInstance == null) {
            sInstance = new VideoCallActivity();
        }
        return sInstance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        mActivity = this;
        init();
        initView();
        initTimer();
    }

    //初始化组件
    private void init() {
        mDownTime = 30 * 1000;
        mSurfaceParent = findViewById(R.id.video_call_surface_contain);
        mViewContentDisplay = findViewById(R.id.video_call_display);
        mTvCallState = findViewById(R.id.video_call_state);
        mTvCallTime = findViewById(R.id.video_call_time);
        mTvCallContact = findViewById(R.id.video_call_contact);
        mIbMic = findViewById(R.id.video_call_mic);
        mIbSpeaker = findViewById(R.id.video_call_speaker);
        mFabReject = findViewById(R.id.video_call_reject);
        mFabEnd = findViewById(R.id.video_call_end);
        mFabAnswer = findViewById(R.id.video_call_answer);
        mViewContentDisplay.setOnClickListener(this);
        mIbMic.setOnClickListener(this);
        mIbSpeaker.setOnClickListener(this);
        mFabAnswer.setOnClickListener(this);
        mFabEnd.setOnClickListener(this);
        mFabReject.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mTimer == null) {
            initTimer();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        destroyTimer();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        exitFullScreen();
    }

    private void exitFullScreen() {
        CallManager.getInstance().addCallNotification();
        onFinish();
    }

    @Override
    public void onFinish() {
        if (mLocalSurface != null) {
            if (mLocalSurface.getRenderer() != null) {
                mLocalSurface.getRenderer().dispose();
            }
            mLocalSurface.release();
            mLocalSurface = null;
        }
        if (mOppositeSurface != null) {
            if (mOppositeSurface.getRenderer() != null) {
                mOppositeSurface.getRenderer().dispose();
            }
            mOppositeSurface.release();
            mOppositeSurface = null;
        }
        super.onFinish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        destroyTimer();
    }

    @Override
    public void initView() {
        mLittleWidth = (int) getResources().getDimension(R.dimen.little_surface_width);
        mLittleHeight = (int) getResources().getDimension(R.dimen.little_surface_height);
        mRightMargin = (int) getResources().getDimension(R.dimen.little_surface_margin_right);
        mTopMargin = (int) getResources().getDimension(R.dimen.little_surface_margin_top);
        super.initView();

        if (CallManager.getInstance().isInComingCall()) {//呼入
            mFabEnd.setVisibility(View.GONE);
            mFabReject.setVisibility(View.VISIBLE);
            mFabAnswer.setVisibility(View.VISIBLE);
            mTvCallState.setText("对方申请与你进行通话");
        } else {
            mFabEnd.setVisibility(View.VISIBLE);
            mFabReject.setVisibility(View.GONE);
            mFabAnswer.setVisibility(View.GONE);
            mTvCallState.setText("正在呼叫...");
        }
        mIbMic.setActivated(!CallManager.getInstance().isMicOpen());
        mIbSpeaker.setActivated(CallManager.getInstance().isSpeakerOpen());

        initSurfaceView();
        if (CallManager.getInstance().getCallState() == CallManager.CallState.ACCEPTED) {
            mFabEnd.setVisibility(View.VISIBLE);
            mFabAnswer.setVisibility(View.GONE);
            mFabReject.setVisibility(View.GONE);
            mTvCallState.setText("通话已接通");
            refreshCallTime();
            onCallSurface();
        }

        try {//前置摄像头
            EMClient.getInstance().callManager().setCameraFacing(android.hardware.Camera.CameraInfo.CAMERA_FACING_FRONT);
        } catch (HyphenateException e) {
            e.printStackTrace();
        }
        CallManager.getInstance().setCallCameraDataProcessor();//通话图像
    }

    //接通电话界面调整
    private void onCallSurface() {
        mSurfaceState = 0;
        mLocalParams = new RelativeLayout.LayoutParams(mLittleWidth, mLittleHeight);
        mLocalParams.width = mLittleWidth;
        mLocalParams.height = mLittleHeight;
        mLocalParams.rightMargin = mRightMargin;
        mLocalParams.topMargin = mTopMargin;
        mLocalParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        mLocalSurface.setLayoutParams(mLocalParams);
        mLocalSurface.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeSurfaceView();
            }
        });
        mOppositeSurface.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                display();
            }
        });
    }

    //通话时间
    private void refreshCallTime() {
        int t = CallManager.getInstance().getCallTime();
        int h = t / 60 / 60;
        int m = t / 60 % 60;
        int s = t % 60 % 60;
        String time = "";
        time = h > 9 ? "" + h : "0" + h;
        time += m > 9 ? ":" + h : ":0" + m;
        time += s > 9 ? ":" + s : ":0" + s;
        if (!mTvCallTime.isShown()) {
            mTvCallTime.setVisibility(View.VISIBLE);
        }
        mTvCallTime.setText(time);
    }

    //初始化视频聊天界面
    private void initSurfaceView() {
        mOppositeSurface = new EMCallSurfaceView(mActivity);
        mOppositeParams = new RelativeLayout.LayoutParams(0, 0);
        mOppositeParams.width = RelativeLayout.LayoutParams.MATCH_PARENT;
        mOppositeParams.height = RelativeLayout.LayoutParams.MATCH_PARENT;
        mOppositeSurface.setLayoutParams(mOppositeParams);
        mSurfaceParent.addView(mOppositeSurface);

        mLocalSurface = new EMCallSurfaceView(mActivity);
        mLocalParams = new RelativeLayout.LayoutParams(0, 0);
        mLocalParams.width = RelativeLayout.LayoutParams.MATCH_PARENT;
        mLocalParams.height = RelativeLayout.LayoutParams.MATCH_PARENT;
        mLocalParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        mLocalSurface.setLayoutParams(mLocalParams);
        mSurfaceParent.addView(mLocalSurface);

        mLocalSurface.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                display();
            }
        });
        //界面位于上层
        mLocalSurface.setZOrderOnTop(false);//true:1(APPLICATION_PANEL_SUBLAYER)    false:-2(APPLICATION_MEDIA_SUBLAYER)
        mLocalSurface.setZOrderMediaOverlay(true);//true:-1(APPLICATION_MEDIA_OVERLAY_SUBLAYER)     false:-2(APPLICATION_MEDIA_SUBLAYER)  负值在主窗口下面
        mLocalSurface.setScaleMode(com.superrtc.sdk.VideoView.EMCallViewScaleMode.EMCallViewScaleModeAspectFill);
        mOppositeSurface.setScaleMode(com.superrtc.sdk.VideoView.EMCallViewScaleMode.EMCallViewScaleModeAspectFill);
        EMClient.getInstance().callManager().setSurfaceView(mLocalSurface, mOppositeSurface);
    }

    //切换界面
    private void changeSurfaceView() {
        if (mSurfaceState == 0) {
            mSurfaceState = 1;
            EMClient.getInstance().callManager().setSurfaceView(mOppositeSurface, mLocalSurface);
        } else {
            mSurfaceState = 0;
            EMClient.getInstance().callManager().setSurfaceView(mLocalSurface, mOppositeSurface);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.video_call_answer:
                answer();
                break;
            case R.id.video_call_reject:
                reject();
                break;
            case R.id.video_call_end:
                end();
                break;
            case R.id.video_call_mic:
                mic();
                break;
            case R.id.video_call_speaker:
                speaker();
                break;
            case R.id.video_call_display:
                display();
                break;
        }
    }

    @Override
    public void answer() {
        super.answer();
        mFabEnd.setVisibility(View.VISIBLE);
        mFabReject.setVisibility(View.GONE);
        mFabAnswer.setVisibility(View.GONE);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventBus(CallEvent event) {
        if (event.isState()) {
            refreshCallView(event);
        }
        if (event.isCheckTime()) {
            refreshCallTime();
        }// 不论什么情况都检查下当前时间
    }

    //实时刷新通话机界面
    private void refreshCallView(CallEvent event) {
        EMCallStateChangeListener.CallError error = event.getCallError();
        EMCallStateChangeListener.CallState state = event.getCallState();
        switch (state) {
            case CONNECTING:
                Log.d("正在呼叫对方", error.toString());
                break;
            case CONNECTED:
                Log.d("正在连接", error.toString());
                if (CallManager.getInstance().isInComingCall()) {
                    mTvCallState.setText(R.string.call_connected_is_incoming);
                    if (!mTvCallContact.isShown()) {
                        mTvCallContact.setVisibility(View.VISIBLE);
                    }
                    mTvCallContact.setText(CallManager.getInstance().getToChatId());
                } else {
                    mTvCallState.setText(R.string.call_connected);
                }
                break;
            case ACCEPTED:
                mIsConnect = true;
                Log.d(TAG, "通话已接通");
                mTvCallState.setText(R.string.call_accepted);
                if (!mTvCallContact.isShown()) {
                    mTvCallContact.setVisibility(View.VISIBLE);
                }
                mTvCallContact.setText(CallManager.getInstance().getToChatId());
                onCallSurface();// 通话接通，更新界面
                break;
            case DISCONNECTED:
                Log.d("通话已结束", error.toString());
                if (error.toString().equals("error_none")) {
                    if (!mIsConnect && mCallOrAnswer == 0) {
                        Log.d(TAG, "refreshCallView: 未接电话");
                        final String callAccount = CallManager.getInstance().getToChatId();
                        mCallRepository.queryPointCall(callAccount, new CallDataSource.LoadPointCallCallback() {
                            @Override
                            public void onSuccess(MissedCall call) {
                                Log.d(TAG, "onSuccess: 未接电话已存在累加");
                                mCallRepository.updateCallCount(1, callAccount, new CallDataSource.SuccessCallback() {
                                    @Override
                                    public void onSuccess() {
                                        if (mMissedCallListener != null) {
                                            mMissedCallListener.notification();
                                        }
                                    }
                                });
                            }

                            @Override
                            public void onError() {
                                Log.d(TAG, "onError: 未接电话存储");
                                mMissedCall = new MissedCall();
                                mMissedCall.callAccount = callAccount;
                                mMissedCall.callName = CallManager.getInstance().getToChatId();
                                mMissedCall.count = 1;
                                mCallRepository.insertCall(mMissedCall, new CallDataSource.SuccessCallback() {
                                    @Override
                                    public void onSuccess() {
                                        if (mMissedCallListener != null) {
                                            mMissedCallListener.notification();
                                        }
                                    }
                                });
                            }
                        });
                    }
                }
                CallManager.getInstance().cancelCallNotification();
                onFinish();
                break;
            case NETWORK_DISCONNECTED:
                Toast.makeText(mActivity, "对方网络断开", Toast.LENGTH_SHORT).show();
                CallManager.getInstance().cancelCallNotification();
                end();
                break;
            case NETWORK_NORMAL:
                Log.d(TAG, "网络正常");
                break;
            case NETWORK_UNSTABLE:
                if (error == EMCallStateChangeListener.CallError.ERROR_NO_DATA) {
                    Log.d("没有通话数据", error.toString());
                } else {
                    Log.d("网络不稳定", error.toString());
                }
                CallManager.getInstance().cancelCallNotification();
                end();
                break;
            case VIDEO_PAUSE:
                Toast.makeText(mActivity, "对方已暂停视频传输", Toast.LENGTH_SHORT).show();
                break;
            case VIDEO_RESUME:
                Toast.makeText(mActivity, "对方已恢复视频传输", Toast.LENGTH_SHORT).show();
                break;
            case VOICE_PAUSE:
                Toast.makeText(mActivity, "对方已暂停语音传输", Toast.LENGTH_SHORT).show();
                break;
            case VOICE_RESUME:
                Toast.makeText(mActivity, "对方已恢复语音传输", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }

    public void setNotificationMissedCallListener(NotificationMissedCallListener listener) {
        mMissedCallListener = listener;
    }


    //界面内容显示
    private void display() {
        if (mViewContentDisplay.isShown()) {
            mViewContentDisplay.setVisibility(View.GONE);
        } else {
            mViewContentDisplay.setVisibility(View.VISIBLE);
        }
    }

    //扬声器
    private void speaker() {
        if (mIbSpeaker.isActivated()) {
            mIbSpeaker.setActivated(false);
            CallManager.getInstance().closeSpeaker();
            CallManager.getInstance().setSpeakerOpen(false);
        } else {
            mIbSpeaker.setActivated(true);
            CallManager.getInstance().openSpeaker();
            CallManager.getInstance().setSpeakerOpen(true);
        }
    }

    //麦克风
    private void mic() {
        try {
            if (mIbMic.isActivated()) {
                mIbMic.setActivated(false);
                EMClient.getInstance().callManager().resumeVideoTransfer();
                CallManager.getInstance().setMicOpen(true);
            } else {
                mIbMic.setActivated(true);
                EMClient.getInstance().callManager().pauseVideoTransfer();
                CallManager.getInstance().setMicOpen(false);
            }
        } catch (HyphenateException e) {
            e.printStackTrace();
        }

    }

    //初始化计时器
    private void initTimer() {
        if (mTimer == null) {
            mTimer = new Timer();
        }
        mTask = new TimerTask() {
            @Override
            public void run() {
                if (mDownTime == 0) {
                    mCallOrAnswer = 1;
                    if (CallManager.getInstance().getCallState() != CallManager.CallState.ACCEPTED) {
                        end();
                        return;
                    }
                } else {
                    mDownTime--;
                }
            }
        };
        mTimer.schedule(mTask, 0, 1000);
    }

    //销毁计时器
    private void destroyTimer() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        if (mTask != null) {
            mTask.cancel();
            mTask = null;
        }
    }
}
