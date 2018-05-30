package com.danny.chat;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.danny.chat.chat.ChatActivity;
import com.danny.chat.data.Friend;
import com.danny.chat.data.LoginDataSource;
import com.danny.chat.data.LoginInjection;
import com.danny.chat.data.LoginRepository;
import com.danny.chat.data.User;
import com.danny.chat.data.call.CallDataSource;
import com.danny.chat.data.call.CallInjection;
import com.danny.chat.data.call.CallRepository;
import com.danny.chat.data.call.MissedCall;
import com.danny.chat.login.LoginActivity;
import com.danny.chat.service.HxService;
import com.danny.chat.utils.AgreeFriendListener;
import com.danny.chat.utils.DeleteFriendListener;
import com.danny.chat.utils.RejectFriendListener;
import com.danny.chat.utils.SpUtils;
import com.danny.chat.videocall.NotificationMissedCallListener;
import com.danny.chat.videocall.VideoCallActivity;
import com.danny.chat.widget.recyclerview.CustomRecyclerView;
import com.danny.chat.widget.recyclerview.FriendRecycleViewAdapter;
import com.danny.chat.widget.recyclerview.OnItemClickListener;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MainActivity extends AppCompatActivity implements View.OnClickListener, NotificationMissedCallListener {
    private static final String TAG = "MainActivity";
    private MainActivity mActivity;
    private RelativeLayout mRoot;
    private CustomRecyclerView mRecyclerView;
    private FriendRecycleViewAdapter mAdapter;
    private List<String> mFriends;//服务器获取好友信息
    private List<String> mFriends2 = new ArrayList<>();//用于CustomRecyclerView中的数据
    private List<Friend> mLists = new ArrayList<>();//数据库保存好友信息
    private Map<Integer, Integer> mMaps = new LinkedHashMap<>();//存储好友头像，用于CustomRecyclerView中的数据
    private String mCurrentUser;//当前用户
    private AlertDialog mDialog;

    private String toUserAccount;//好友账号
    private String toUserName;//好友名字
    private String toNote;//备注 添加好友描述信息（当前用户姓名）
    private LinearLayout friendsLayout;
    private Button search;
    private Button add;
    private TextView outputUserName;
    private EditText inputUserAccount;//好友账号
    private EditText inputUsername;//好友姓名
    private EditText inputNote;//当前用户名
    private ArrayList<Map<String, Integer>> mHeads;
    private long exitTime = 0;
    private String mUserId;//数据库中存储的用户的uuid
    private LoginRepository mRepository;
    private String regex = "[A-Za-z0-9]+";

    private InputMethodManager mInputMethodManager;

    private HxService mHxService;
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mHxService = ((HxService.MyBinder) service).getService();
            mHxService.setAgreeFriendListener(new AgreeFriendListener() {
                @Override
                public void agree(String agreeNumber) {
                    query();
                }
            });

            mHxService.setDeleteFriendListener(new DeleteFriendListener() {
                @Override
                public void delete(String deleteNumber) {
                    query();
                }
            });

            mHxService.setRejectFriendListener(new RejectFriendListener() {
                @Override
                public void reject(String rejectNumber) {
                    query();
                }
            });
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            if (mHxService != null) mHxService = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mCurrentUser = EMClient.getInstance().getCurrentUser();
        mInputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        mActivity = this;
        initView();
        VideoCallActivity.getInstance().setNotificationMissedCallListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        query();
    }

    private void query() {
        mRepository.queryUser(mCurrentUser, new LoginDataSource.LoadUserCallback() {
            @Override
            public void onSuccess(User user) {
                Log.d(TAG, "onSuccess: 查到了");
                mUserId = user.id;
                SpUtils.put(mActivity, "notification_user_id", mUserId);
                mRepository.queryAllFriend(mUserId, new LoginDataSource.LoadAllFriendCallback() {
                    @Override
                    public void onSuccess(List<Friend> friends) {
                        Log.d(TAG, "onSuccess: 查到好友");
                        mLists = friends;
                        loadFriends();
                    }

                    @Override
                    public void onError() {
                        Log.d(TAG, "onError: 还没有好友");
                        loadFriends();
                    }
                });
            }

            @Override
            public void onError() {
                Log.d(TAG, "onError: 刚刚登录");
                loadFriends();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
//        EMClient.getInstance().contactManager().setContactListener(new EMContactListener() {
//            //增加
//            @Override
//            public void onContactAdded(String s) {
//                Log.d(TAG, "onContactAdded: 添加好友"+s);
//            }
//
//            //删除
//            @Override
//            public void onContactDeleted(String s) {
//                Log.d(TAG, "onContactDeleted: 删除好友");
//                Pattern pattern=Pattern.compile(regex);
//                Matcher matcher=pattern.matcher(s);
//                if (matcher.find()){
//                    Log.d(TAG, "onContactDeleted: 账号删除好友"+s);
//                    mRepository.deleteFriend(s);
//                }else {
//                    Log.d(TAG, "onContactDeleted: 昵称删除好友"+s);
//                    mRepository.deleteAliasFriend(s);
//                }
//                query();
//            }
//
//            //收到邀请
//            @Override
//            public void onContactInvited(String account, String name) {
//                mNotificationManager= (NotificationManager) mActivity.getSystemService(Context.NOTIFICATION_SERVICE);
//                NotificationCompat.Builder builder=new NotificationCompat.Builder(mActivity);
//                builder.setSmallIcon(R.mipmap.call_notification_small_icon);
//                builder.setPriority(Notification.PRIORITY_HIGH);
//                builder.setAutoCancel(true);
//                builder.setDefaults(Notification.DEFAULT_VIBRATE | Notification.DEFAULT_LIGHTS);//使用默认通知灯，震动
//                builder.setContentText("点击同意添加好友");
//                builder.setContentTitle(name+" 要添加你为好友...");
//                Intent intent=new Intent(mActivity, AgreeFriendActivity.class);
//                intent.putExtra("account",account);
//                intent.putExtra("name",name);
//                intent.putExtra("userid",mUserId);
//                PendingIntent pi=PendingIntent.getActivity(mActivity,0,intent,PendingIntent.FLAG_CANCEL_CURRENT);
//                builder.setContentIntent(pi);//相当于添加点击事件，打开活动
//                builder.setOngoing(true);//是否为正在进行通知
//                builder.setWhen(System.currentTimeMillis());//通知时间
//                Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
//                Notification notification=builder.build();
//                notification.sound = uri;
//                mNotificationManager.notify(mFriendNotificationId,notification);
//                Log.d(TAG, "onContactInvited: 收到添加邀请"+name+"-"+account);
//            }
//
//            //同意
//            @Override
//            public void onFriendRequestAccepted(String s) {
//                Log.d(TAG, "onFriendRequestAccepted: 同意");
//                Friend friend=new Friend();
//                friend.headImage=new Random().nextInt(mHead.length);
//                friend.alias=toUserName;
//                friend.userId=mUserId;
//                friend.friendAccount=toUserAccount;
//                mRepository.insertFriend(friend);
////                loadFriends();
//                query();
//            }
//
//            //拒绝
//            @Override
//            public void onFriendRequestDeclined(String s) {Log.d(TAG, "onFriendRequestDeclined: 拒绝");}
//        });
        bindService(new Intent(mActivity, HxService.class), mConnection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unbindService(mConnection);
    }

    private void initView() {
        mRepository = LoginInjection.getInstance(mActivity);
        mRoot = findViewById(R.id.main_root);
        mRecyclerView = findViewById(R.id.huanxin_friend_list);
    }

    //加载好友
    private void loadFriends() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    mFriends = EMClient.getInstance().contactManager().getAllContactsFromServer();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (mFriends.size() > 0) {
                                mFriends2.clear();
                                for (int i = 0; i < mFriends.size(); i++) {
                                    Log.d(TAG, "run: " + mFriends.get(i));
                                    mMaps.put(i, 0);
                                    mFriends2.add(mFriends.get(i));
                                    if (mLists.size() != 0) {
                                        for (int j = 0; j < mLists.size(); j++) {
                                            Log.d(TAG, "run: " + mFriends.get(i) + "---" + mLists.get(j).friendAccount + "---" + mLists.get(j).alias);
                                            if (mFriends.get(i).equals(mLists.get(j).friendAccount)) {
                                                mFriends2.remove(i);
                                                mFriends2.add(mLists.get(j).alias);
                                                mMaps.remove(i);
                                                mMaps.put(i, mLists.get(j).headImage);
                                            }
                                        }
                                    }
                                }
                                mRecyclerView.setVisibility(View.VISIBLE);
                                LinearLayoutManager manager = new LinearLayoutManager(mActivity);
                                mAdapter = new FriendRecycleViewAdapter(mMaps, mFriends2, mActivity);
                                manager.setOrientation(LinearLayoutManager.VERTICAL);
                                mRecyclerView.setLayoutManager(manager);
                                mRecyclerView.setAdapter(mAdapter);
                                mRecyclerView.setOnItemClickListener(new OnItemClickListener() {
                                    @Override
                                    public void onItemClick(View view, int position) {
                                        Intent intent = new Intent(mActivity, ChatActivity.class);
                                        intent.putExtra("currentId", mCurrentUser);
                                        intent.putExtra("userId", mUserId);
                                        intent.putExtra("toChatId", mFriends.get(position));
                                        intent.putExtra("tiChatAlias", mFriends2.get(position));
                                        startActivity(intent);
//                                        mActivity.finish();
                                    }

                                    @Override
                                    public void onDeleteClick(final String account, final int position) {
                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Pattern pattern = Pattern.compile(regex);
                                                Matcher matcher = pattern.matcher(account);
                                                if (matcher.find()) {
                                                    deleteFriend(account, position);
                                                } else {
                                                    mRepository.queryAliasFriend(account, new LoginDataSource.LoadFriendCallback() {
                                                        @Override
                                                        public void onSuccess(final Friend friend) {
                                                            deleteFriend(friend.friendAccount, position);
                                                        }

                                                        @Override
                                                        public void onError() {
                                                        }
                                                    });
                                                }
                                            }
                                        }).start();
                                    }
                                });
                            } else {
                                mRecyclerView.setVisibility(View.GONE);
                            }
                        }
                    });
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    //删除好友
    private void deleteFriend(final String account, final int position) {
        EMClient.getInstance().contactManager().aysncDeleteContact(account, new EMCallBack() {
            @Override
            public void onSuccess() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(mActivity, "删除好友成功", Toast.LENGTH_SHORT).show();
                        mAdapter.removeItem(position);
                        mAdapter.notifyDataSetChanged();
                        Log.d(TAG, "run: 账号删除好友" + account);
                        mRepository.deleteFriend(account, null);
                    }
                });
            }

            @Override
            public void onError(int i, String s) {
            }

            @Override
            public void onProgress(int i, String s) {
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.huanxin_quit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "onOptionsItemSelected: ");
        if (item.getItemId() == R.id.quit_login) {
            quitLogin();
        } else if (item.getItemId() == R.id.menu_add_friend) {
            showDialog();
        } else if (item.getItemId() == R.id.menu_user) {
            userInfo();
        }
        return true;
    }

    //当前用户信息
    private void userInfo() {
        View view = LayoutInflater.from(mActivity).inflate(R.layout.dialog_user_info, null);
        TextView tv = view.findViewById(R.id.current_user);
        tv.setText(mCurrentUser);
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        mDialog = builder.setView(view).create();
        mDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                dialog.dismiss();
            }
        });
        mDialog.show();
    }

    //退出
    private void quitLogin() {
        EMClient.getInstance().logout(false, new EMCallBack() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "onSuccess: 退出成功");
                Intent intent = new Intent(mActivity, LoginActivity.class);
                startActivity(intent);
            }

            @Override
            public void onError(int i, String s) {
                Log.d(TAG, "onError: 退出失败");
            }

            @Override
            public void onProgress(int i, String s) {
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//            case R.id.add_friend:
//                showDialog();
//                break;
            case R.id.popup_search_friends:
                search();
                break;
            case R.id.popup_add:
                add();
                break;
        }
    }

    //添加好友
    private void add() {
        mDialog.dismiss();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    EMClient.getInstance().contactManager().addContact(toUserAccount, toNote);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            SpUtils.put(mActivity, "to_user_name", toUserName);
                            SpUtils.put(mActivity, "to_user_account", toUserAccount);
                            Toast.makeText(mActivity, "已发送好友申请", Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (HyphenateException e) {
                    e.printStackTrace();
                    Log.d(TAG, "addFriends: 添加好友失败");
                }
            }
        }).start();
    }

    //查找好友
    private void search() {
        toUserAccount = inputUserAccount.getText().toString().trim();//好友账号
        toUserName = inputUsername.getText().toString().trim();//好友名字
        toNote = inputNote.getText().toString().trim();//备注-描述信息（当前用户名）
        Log.d(TAG, "showDialog: " + toUserAccount);
        if (!TextUtils.isEmpty(toUserAccount) && !TextUtils.isEmpty(toUserName) && !TextUtils.isEmpty(toNote)) {
            if (toUserAccount.equals(mCurrentUser)) {
                Toast.makeText(mActivity, "不能添加自己为好友!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (mFriends != null && mFriends.size() > 0) {
                for (int i = 0; i < mFriends.size(); i++) {
                    if (mFriends.get(i).equals(toUserAccount)) {
                        Toast.makeText(mActivity, "你们已经是好友了,不能重复添加!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            }
            friendsLayout.setVisibility(View.VISIBLE);
            outputUserName.setText(toUserAccount);
        } else {
            Toast.makeText(mActivity, "请输入好友账号与姓名", Toast.LENGTH_SHORT).show();
        }
    }

    //查找好友对话框
    private void showDialog() {
        View view = LayoutInflater.from(mActivity).inflate(R.layout.popup_window_add_friends, null);
        inputUserAccount = view.findViewById(R.id.popup_friends_account);
        inputUsername = view.findViewById(R.id.popup_friends_name);
        inputNote = view.findViewById(R.id.popup_friends_note);
        search = view.findViewById(R.id.popup_search_friends);
        friendsLayout = view.findViewById(R.id.popup_friends_info);
        outputUserName = view.findViewById(R.id.popup_to_username);
        add = view.findViewById(R.id.popup_add);
        search.setOnClickListener(this);
        add.setOnClickListener(this);

        inputUsername.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
//                    mInputMethodManager.hideSoftInputFromWindow(mNote.getWindowToken(), 0);//隐藏键盘
                    mInputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
                }
                return false;
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        mDialog = builder.setView(view).create();
        mDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                dialog.dismiss();
            }
        });
        mDialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        quitLogin();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    //连续两次后退键退出应用
    private void exit() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Toast.makeText(getApplicationContext(), "再按一次退出应用",
                    Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
            finish();
            System.exit(0);
        }
    }

    @Override
    public void notification() {
        CallRepository callRepository = CallInjection.getInstance(mActivity);
        callRepository.queryAllCall(new CallDataSource.LoadCallCallback() {
            @Override
            public void onSuccess(List<MissedCall> calls) {
                StringBuilder sb = new StringBuilder();
                for (MissedCall call : calls) {
                    sb.append("[" + call.callName + ":" + call.callAccount + "]\n");
                }
                AlertDialog dialog = new AlertDialog.Builder(mActivity).setIcon(R.drawable.ic_launcher_background)
                        .setTitle("未接电话").setMessage(sb.toString()).create();
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
            }

            @Override
            public void onError() {

            }
        });
    }
}
