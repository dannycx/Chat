package com.danny.chat.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import com.danny.chat.AgreeFriendActivity;
import com.danny.chat.R;
import com.danny.chat.data.Friend;
import com.danny.chat.data.LoginDataSource;
import com.danny.chat.data.LoginInjection;
import com.danny.chat.data.LoginRepository;
import com.danny.chat.utils.AgreeFriendListener;
import com.danny.chat.utils.DeleteFriendListener;
import com.danny.chat.utils.RejectFriendListener;
import com.danny.chat.utils.SpUtils;
import com.hyphenate.EMContactListener;
import com.hyphenate.chat.EMClient;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HxService extends Service {
    private static final String TAG = HxService.class.getSimpleName();
    private Context mContext;
    private MyBinder mBinder = new MyBinder();
    /**
     * 添加好友监听
     */
    private String regex = "[A-Za-z0-9]+";
    private LoginRepository mRepository;
    private NotificationManager mNotificationManager;
    private AgreeFriendListener mAgreeListener;
    private RejectFriendListener mRejectListener;
    private DeleteFriendListener mDeleteListener;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        mRepository = LoginInjection.getInstance(this);
        addFriendListener();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void setAgreeFriendListener(AgreeFriendListener listener) {
        mAgreeListener = listener;
    }

    public void setRejectFriendListener(RejectFriendListener listener) {
        mRejectListener = listener;
    }

    public void setDeleteFriendListener(DeleteFriendListener listener) {
        mDeleteListener = listener;
    }

    private void addFriendListener() {
        EMClient.getInstance().contactManager().setContactListener(new EMContactListener() {
            //增加
            @Override
            public void onContactAdded(String s) {
                Log.d(TAG, "onContactAdded: 添加好友" + s);
            }

            //删除
            @Override
            public void onContactDeleted(final String s) {
                Log.d(TAG, "onContactDeleted: 删除好友");
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(s);
                if (matcher.find()) {
                    Log.d(TAG, "onContactDeleted: 账号删除好友" + s);
                    mRepository.deleteFriend(s, new LoginDataSource.DeleteFriendSuccessCallback() {
                        @Override
                        public void onSuccess() {
                            if (mDeleteListener != null) {
                                mDeleteListener.delete(s);
                            }
                        }
                    });
                } else {
                    Log.d(TAG, "onContactDeleted: 昵称删除好友" + s);
                    mRepository.deleteAliasFriend(s, new LoginDataSource.DeleteFriendSuccessCallback() {
                        @Override
                        public void onSuccess() {
                            if (mDeleteListener != null) {
                                mDeleteListener.delete(s);
                            }
                        }
                    });
                }
            }

            //收到邀请
            @Override
            public void onContactInvited(String s, String s1) {
                mNotificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());
                builder.setSmallIcon(R.mipmap.call_notification_small_icon);
                builder.setPriority(Notification.PRIORITY_HIGH);
                builder.setAutoCancel(true);
                builder.setContentText("点击同意添加好友");
                builder.setContentTitle(s1 + " 要添加你为好友...");
                Intent intent = new Intent(getApplicationContext(), AgreeFriendActivity.class);
                SpUtils.put(getApplicationContext(), "notification_account", s);
                SpUtils.put(getApplicationContext(), "notification_name", s1);
//                SpUtils.put(getApplicationContext(), "notification_userid", mUserId);
                PendingIntent pi = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
                builder.setContentIntent(pi);//相当于添加点击事件，打开活动
                builder.setOngoing(true);//是否为正在进行通知
                builder.setWhen(System.currentTimeMillis());//通知时间
                Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
                Notification notification = builder.build();
                notification.sound = uri;
                mNotificationManager.notify(0, notification);
                Log.d(TAG, "onContactInvited: 收到添加邀请" + s1 + "-" + s);
            }

            //同意
            @Override
            public void onFriendRequestAccepted(final String s) {
                Log.d(TAG, "onFriendRequestAccepted: 同意:" + s);
                String toUserName = (String) SpUtils.get(getApplicationContext(), "to_user_name", "");
                String toUserAccount = (String) SpUtils.get(getApplicationContext(), "to_user_account", "");
                Log.d(TAG, "onFriendRequestAccepted: " + toUserAccount + "-" + toUserName);
                if (!TextUtils.isEmpty(toUserName) && !TextUtils.isEmpty(toUserAccount)) {
                    Friend friend = new Friend();
                    friend.alias = toUserName;
                    String userId = (String) SpUtils.get(mContext, "hyphenate_current_user", "");
                    Log.d(TAG, "onFriendRequestAccepted: ");
                    friend.userId = userId;
                    friend.friendAccount = toUserAccount;
                    mRepository.insertFriend(friend, new LoginDataSource.AddFriendSuccessCallback() {
                        @Override
                        public void onSuccess() {
                            if (mAgreeListener != null) {
                                mAgreeListener.agree(s);
                            }
                        }
                    });
                    SpUtils.put(getApplicationContext(), "to_user_name", "");
                    SpUtils.put(getApplicationContext(), "to_user_account", "");
                }
            }

            //拒绝
            @Override
            public void onFriendRequestDeclined(String s) {
                Log.d(TAG, "onFriendRequestDeclined: 拒绝" + s);
                if (mRejectListener != null) {
                    mRejectListener.reject(s);
                }
            }
        });
    }

    public class MyBinder extends Binder {
        public HxService getService() {
            return HxService.this;
        }
    }
}
