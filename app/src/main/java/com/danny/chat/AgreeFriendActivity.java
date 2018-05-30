package com.danny.chat;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.danny.chat.data.Friend;
import com.danny.chat.data.LoginInjection;
import com.danny.chat.data.LoginRepository;
import com.danny.chat.utils.SpUtils;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

import java.util.Random;

/**
 * Created by danny on 4/10/18.
 */

public class AgreeFriendActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = AgreeFriendActivity.class.getSimpleName();
    private AgreeFriendActivity mActivity;
    private TextView mTvName;
    private TextView mTvAccount;
    private ImageButton mAgree;
    private ImageButton mRefuse;
    private String mName;
    private String mAccount;
    private String mUserId;
    private LoginRepository mRepository;

    //图标
    private int mHead[] = {R.mipmap.chat_friend_head_1, R.mipmap.chat_friend_head_2, R.mipmap.chat_friend_head_3,
            R.mipmap.chat_friend_head_4, R.mipmap.chat_friend_head_5, R.mipmap.chat_friend_head_6,
            R.mipmap.chat_friend_head_7, R.mipmap.chat_friend_head_8, R.mipmap.chat_friend_head_9};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agree_friend);
        mActivity = this;
        mRepository = LoginInjection.getInstance(mActivity);
        mAccount = (String) SpUtils.get(this, "notification_account", "");
        mName = (String) SpUtils.get(this, "notification_name", "");
        mUserId = (String) SpUtils.get(mActivity, "notification_user_id", "");
//        Intent intent = getIntent();
//        if (intent != null) {
//            mName = intent.getStringExtra("name");
//            mAccount = intent.getStringExtra("account");
//            mUserId = intent.getStringExtra("userid");
//        }
        initView();
    }

    private void initView() {
        mTvName = findViewById(R.id.agree_friend_name);
        mTvAccount = findViewById(R.id.agree_friend_account);
        mAgree = findViewById(R.id.agree_friend_agree);
        mRefuse = findViewById(R.id.agree_friend_refuse);
        mTvName.setText(mName);
        mTvAccount.setText(mAccount);
        mAgree.setOnClickListener(this);
        mRefuse.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.agree_friend_agree:
                agree();
                break;
            case R.id.agree_friend_refuse:
                refuse();
                break;
            default:
                break;
        }
    }

    //拒绝
    private void refuse() {
        try {
            EMClient.getInstance().contactManager().declineInvitation(mAccount);
            Intent intent = new Intent(mActivity, MainActivity.class);
            startActivity(intent);
            mActivity.finish();
        } catch (HyphenateException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "refuse: ");
    }

    //同意
    private void agree() {
        EMClient.getInstance().contactManager().asyncAcceptInvitation(mAccount, new EMCallBack() {
            @Override
            public void onSuccess() {
                Friend friend = new Friend();
                friend.headImage = new Random().nextInt(mHead.length);
                friend.alias = mName;
                friend.userId = mUserId;
                friend.friendAccount = mAccount;
                mRepository.insertFriend(friend, null);
                Log.d(TAG, "onSuccess: " + friend.toString());
                Intent intent = new Intent(mActivity, MainActivity.class);
                startActivity(intent);
                mActivity.finish();
                Log.d(TAG, "agree: ");
            }

            @Override
            public void onError(int i, String s) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(mActivity, "添加失败，请重试！", Toast.LENGTH_SHORT).show();
                    }
                });
                Log.d(TAG, "onError: 添加失败");
            }

            @Override
            public void onProgress(int i, String s) {
            }
        });
    }
}
