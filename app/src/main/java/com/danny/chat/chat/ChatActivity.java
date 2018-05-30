package com.danny.chat.chat;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.MenuPopupHelper;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.danny.chat.FriendInfoActivity;
import com.danny.chat.MainActivity;
import com.danny.chat.R;
import com.danny.chat.videocall.CallManager;
import com.danny.chat.videocall.VideoCallActivity;
import com.danny.chat.widget.expressionview.ExpressionView;
import com.hyphenate.EMCallBack;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMCmdMessageBody;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = ChatActivity.class.getSimpleName();
    private ChatActivity mActivity;
    private ImageButton mMore;
    private ImageButton mSend;
    private EditText mMessageEdit;
    private RecyclerView mRecyclerView;
    private ImageView mIvExpression;
    private ExpressionView mExpressionView;
    private ImageButton mChatQuit;
    private TextView mFriendName;

    private String mMessage;
    private String mCurrentId;
    private String mUserId;
    private String mToChatId;
    private String mToChatAlias;
    private EMConversation mConversation;
    private ReceiverMessage mMessageListener;
    private MessageAdapter mAdapter;
    private List<Message> mList = new ArrayList<>();

    private boolean mExpressionState = true;//true-表情   false-键盘
    private InputMethodManager mInputMethodManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        mActivity = this;
        Intent intent = getIntent();
        if (intent != null) {
            mCurrentId = intent.getStringExtra("currentId");
            mUserId = intent.getStringExtra("userId");
            mToChatId = intent.getStringExtra("toChatId");
            mToChatAlias = intent.getStringExtra("tiChatAlias");
        }
        initView();
        initConversation();
        mMessageListener = new ReceiverMessage();

        LinearLayoutManager manager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(manager);
        if (mList != null && mList.size() > 0) {
            mAdapter = new MessageAdapter(mActivity, mList);
            mRecyclerView.setAdapter(mAdapter);
        }
        if (!TextUtils.isEmpty(mToChatAlias)) {
            mFriendName.setText(mToChatAlias);
        }
    }

    //init conversation
    private void initConversation() {
        mConversation = EMClient.getInstance().chatManager().getConversation(mToChatId, null, true);
        mConversation.markAllMessagesAsRead();//将所有消息都标为已读
        int count = mConversation.getAllMessages().size();//获取此conversation当前内存所有的message。如果内存中为空，再从db中加载。
        if (count < mConversation.getAllMsgCount() && count < 20) {//获取本地存储会话的全部消息数目
            String msgId = mConversation.getAllMessages().get(0).getMsgId();//最上面消息
            // 分页加载更多消息，需要传递已经加载的消息的最上边一条消息的id，以及需要加载的消息的条数
            mConversation.loadMoreMsgFromDB(msgId, 20 - count);
        }
        // 打开聊天界面获取最后一条消息内容并显示
        if (mConversation.getAllMessages().size() > 0) {
            EMMessage message = mConversation.getLastMessage();
            EMTextMessageBody body = (EMTextMessageBody) message.getBody();
            Log.d(TAG, "initConversation: " + body.getMessage());
            Message msg = new Message(Message.TYPE_RECEIVED, body.getMessage());
            mList.add(msg);
            if (mList != null && mList.size() > 0) {
                mAdapter = new MessageAdapter(mActivity, mList);
                mRecyclerView.setAdapter(mAdapter);
            }
            // 将消息内容和时间显示出来
            Log.d(TAG, "initConversation-聊天记录:" + mConversation.getLastMessage().getMsgTime() + ":" + body.getMessage());
        } else {
            Message msg = new Message(Message.TYPE_RECEIVED, "快来和我聊天吧！");
            mList.add(msg);
            if (mList != null && mList.size() > 0) {
                mAdapter = new MessageAdapter(mActivity, mList);
                mRecyclerView.setAdapter(mAdapter);
            }
        }
    }

    //send message
    private void sendMessage() {
        mMessage = mMessageEdit.getText().toString().trim();
        Log.d(TAG, "sendMessage: " + mMessage);
        if (!TextUtils.isEmpty(mMessage)) {
            mMessageEdit.setText("");
            if (!TextUtils.isEmpty(mToChatId) && !TextUtils.isEmpty(mCurrentId)) {
                EMMessage message = EMMessage.createTxtSendMessage(mMessage, mToChatId);
                Message msg = new Message(Message.TYPE_SEND, mMessage);
                mList.add(msg);
                mAdapter.notifyItemInserted(mList.size() - 1);
                mRecyclerView.scrollToPosition(mList.size() - 1);
                EMClient.getInstance().chatManager().sendMessage(message);
                message.setMessageStatusCallback(new EMCallBack() {
                    @Override
                    public void onSuccess() {
                        Log.d(TAG, "onSuccess: 发送成功");
                    }

                    @Override
                    public void onError(int i, String s) {
                        Log.d(TAG, "onError: 发送失败");
                    }

                    @Override
                    public void onProgress(int i, String s) {
                    }
                });
            }
        } else {
            Toast.makeText(this, "请输入发送内容", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        EMClient.getInstance().chatManager().addMessageListener(mMessageListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        EMClient.getInstance().chatManager().removeMessageListener(mMessageListener);
    }

    //init view
    private void initView() {
        mMore = findViewById(R.id.chat_more);
        mSend = findViewById(R.id.chat_btn_send);
        mRecyclerView = findViewById(R.id.chat_msg_recycler_view);
        mMessageEdit = findViewById(R.id.chat_message_input);
        mIvExpression = findViewById(R.id.chat_send_expression);
        mExpressionView = findViewById(R.id.chat_expression_show);
        mChatQuit = findViewById(R.id.chat_quit_chat);
        mFriendName = findViewById(R.id.chat_friend_name);
        mMore.setOnClickListener(this);
        mSend.setOnClickListener(this);
        mIvExpression.setOnClickListener(this);
        mChatQuit.setOnClickListener(this);
        mFriendName.setOnClickListener(this);
        mInputMethodManager = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.chat_btn_send:
                sendMessage();
                break;
            case R.id.chat_more:
                loadPopupWindow(mMore);
                break;
            case R.id.chat_send_expression:
                sendExpression();
                break;
            case R.id.chat_quit_chat:
                quitChat();
                break;
            case R.id.chat_friend_name:
                modifyFriendInfo();
                break;
        }
    }

    //修改好友信息
    private void modifyFriendInfo() {
        Intent intent = new Intent(ChatActivity.this, FriendInfoActivity.class);
        intent.putExtra("userId", mUserId);//当前用户的uuid
        intent.putExtra("toChatId", mToChatId);//好友账号
        startActivity(intent);
    }

    //exit chat
    private void quitChat() {
        Intent intent = new Intent(mActivity, MainActivity.class);
        startActivity(intent);
        mActivity.finish();
    }

    //send expression
    private void sendExpression() {
        if (mExpressionState) {
            mIvExpression.setImageResource(R.mipmap.chat_keybroad);
            mExpressionView.setVisibility(View.VISIBLE);
            mInputMethodManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        } else {
            mIvExpression.setImageResource(R.mipmap.chat_expression);
            mExpressionView.setVisibility(View.GONE);
            mInputMethodManager.showSoftInput(mMessageEdit, InputMethodManager.SHOW_IMPLICIT);//显示
        }
        mExpressionState = !mExpressionState;
        mExpressionView.setEdit(mMessageEdit);
        mExpressionView.setBtnView(mIvExpression);
    }

    //video call
    private void videoCall() {
        Toast.makeText(mActivity, "打电话", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(mActivity, VideoCallActivity.class);
        CallManager.getInstance().setToChatId(mToChatId);
        CallManager.getInstance().setInComingCall(false);//呼出电话
        CallManager.getInstance().setCallType(CallManager.CallType.VIDEOCALL);
        startActivity(intent);
    }

    //更多操作
    @SuppressLint("RestrictedApi")
    private void loadPopupWindow(View view) {
//        mInputMethodManager.toggleSoftInput(0,InputMethodManager.HIDE_NOT_ALWAYS);
        // View当前PopupMenu显示的相对View的位置
        PopupMenu popupMenu = new PopupMenu(mActivity, view);
        // menu布局
        popupMenu.getMenuInflater().inflate(R.menu.video_call, popupMenu.getMenu());
        // menu的item点击事件
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.chat_video_call:
                        videoCall();
                        break;
                    default:
                        break;
                }
                Toast.makeText(mActivity, item.getTitle(), Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        // PopupMenu关闭事件
        popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
            @Override
            public void onDismiss(PopupMenu menu) {
                Toast.makeText(mActivity, "关闭PopupMenu", Toast.LENGTH_SHORT).show();
                mInputMethodManager.showSoftInput(mMessageEdit, InputMethodManager.SHOW_IMPLICIT);//显示
            }
        });
        //使用反射，强制显示菜单图标
        try {
            Field field = popupMenu.getClass().getDeclaredField("mPopup");
            field.setAccessible(true);
            MenuPopupHelper mHelper = (MenuPopupHelper) field.get(popupMenu);
            mHelper.setForceShowIcon(true);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
        popupMenu.show();
    }

    //message listener
    private class ReceiverMessage implements EMMessageListener {
        @Override
        public void onMessageReceived(List<EMMessage> list) {
            Log.d(TAG, "onMessageReceived: 收到消息");
            for (final EMMessage message : list) {
                if (message.getFrom().equals(mToChatId)) {
                    mConversation.markMessageAsRead(message.getMsgId());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            EMTextMessageBody body = (EMTextMessageBody) message.getBody();
                            Message msg = new Message(Message.TYPE_RECEIVED, body.getMessage());
                            mList.add(msg);
                            mAdapter.notifyItemInserted(mList.size() - 1);
                            mRecyclerView.scrollToPosition(mList.size() - 1);
                        }
                    });
                } else {
                    //不是当前用户发的，发通知
                    NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    NotificationCompat.Builder builder = new NotificationCompat.Builder(mActivity);
                    builder.setSmallIcon(R.mipmap.chat_notification);
                    builder.setTicker("收到一条新消息");
                    builder.setContentInfo(message.getMsgId());
                    Notification notification = builder.build();
                    manager.notify(0, notification);
                }
            }
        }

        @Override
        public void onCmdMessageReceived(List<EMMessage> list) {
            Log.d(TAG, "onCmdMessageReceived: 收到透传消息");
            for (int i = 0; i < list.size(); i++) {
                EMMessage cmdMessage = list.get(i);
                EMCmdMessageBody body = (EMCmdMessageBody) cmdMessage.getBody();
                Log.d(TAG, "onCmdMessageReceived: " + body.action());
            }
        }

        @Override
        public void onMessageRead(List<EMMessage> list) {
            Log.d(TAG, "onMessageRead: 收到已读回执");
        }

        @Override
        public void onMessageDelivered(List<EMMessage> list) {
            Log.d(TAG, "onMessageDelivered: 收到已送达回执");
        }

        @Override
        public void onMessageRecalled(List<EMMessage> list) {
            Log.d(TAG, "onMessageRecalled: 消息被撤回");
        }

        @Override
        public void onMessageChanged(EMMessage emMessage, Object o) {
            Log.d(TAG, "onMessageChanged: 消息状态变动");
        }
    }
}
