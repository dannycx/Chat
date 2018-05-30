package com.danny.chat;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.danny.chat.data.Friend;
import com.danny.chat.data.LoginDataSource;
import com.danny.chat.data.LoginInjection;
import com.danny.chat.data.LoginRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FriendInfoActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = FriendInfoActivity.class.getSimpleName();
    private FriendInfoActivity mActivity;
    private LinearLayout mRoot;
    private ImageButton mBack;
    private ImageButton mSave;
    private ImageView mHead;
    private EditText mEtAlias;
    private TextView mTvAccount;

    private String mAccount;
    private String mAlias;
    private int mHeadIndex = 0;
    private String mUserId;
    private LoginRepository mRepository;
    private Friend mFriend;
    private ArrayList<Map<String, Integer>> mHeads;
    private InputMethodManager mInputMethodManager;
    //图标
    private int[] mImages = new int[]{R.mipmap.chat_friend_head_1, R.mipmap.chat_friend_head_2, R.mipmap.chat_friend_head_3,
            R.mipmap.chat_friend_head_4, R.mipmap.chat_friend_head_5, R.mipmap.chat_friend_head_6,
            R.mipmap.chat_friend_head_7, R.mipmap.chat_friend_head_8, R.mipmap.chat_friend_head_9};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_info);
        mActivity = this;
        mInputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        Intent intent = getIntent();
        if (intent != null) {
            mUserId = intent.getStringExtra("userId");
            mAccount = intent.getStringExtra("toChatId");
        }
        initView();
    }

    private void initView() {
        mRepository = LoginInjection.getInstance(mActivity);
        mFriend = new Friend();
        mRoot = findViewById(R.id.friend_info_root);
        mBack = findViewById(R.id.friend_info_back_chat);
        mSave = findViewById(R.id.friend_info_save);
        mHead = findViewById(R.id.friend_info_head);
        mEtAlias = findViewById(R.id.friend_info_alias);
        mTvAccount = findViewById(R.id.friend_info_account);
        mBack.setOnClickListener(this);
        mSave.setOnClickListener(this);
        mHead.setOnClickListener(this);
        mTvAccount.setText(mAccount);

        mEtAlias.setOnKeyListener(new View.OnKeyListener() {
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
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.friend_info_back_chat:
                back();
                break;
            case R.id.friend_info_save:
                save();
                break;
            case R.id.friend_info_head:
                chooseHead();
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        LoginInjection.getInstance(this).queryFriend(mAccount, new LoginDataSource.LoadFriendCallback() {
            @Override
            public void onSuccess(Friend friend) {
                mEtAlias.setText(friend.alias);
                mHead.setImageResource(mImages[friend.headImage]);
            }

            @Override
            public void onError() {
            }
        });
    }

    //选择头像
    private void chooseHead() {
        popWindow();
    }

    private void popWindow() {
        initData();
        View contentView = LayoutInflater.from(FriendInfoActivity.this).inflate(R.layout.main_activity_friend_head, null);
        GridView gridView = contentView.findViewById(R.id.main_activity_friend_head);
        SimpleAdapter adapter = new SimpleAdapter(mActivity, mHeads, R.layout.main_activity_friend_head_item
                , new String[]{"img"}, new int[]{R.id.friend_head_item});
        gridView.setAdapter(adapter);
        final PopupWindow popWnd = new PopupWindow(mActivity);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "onItemClick: " + position + "-" + id);
                mHead.setImageResource(mImages[position]);
                mHeadIndex = position;
                popWnd.dismiss();
            }
        });
        popWnd.setContentView(contentView);
        popWnd.setWidth(mRoot.getWidth() / 2);
        popWnd.setHeight(mRoot.getHeight() / 4);
        popWnd.setOutsideTouchable(true);
        popWnd.setBackgroundDrawable(new ColorDrawable(Color.GRAY));
        popWnd.showAsDropDown(mHead);
    }

    void initData() {
        mHeads = new ArrayList<>();
        for (int i = 0; i < mImages.length; i++) {
            Map<String, Integer> map = new HashMap<>();
            map.put("img", mImages[i]);
            mHeads.add(map);
        }
    }

    //保存好友信息到数据库
    private void save() {
        mAlias = mEtAlias.getText().toString().trim();
        if (!TextUtils.isEmpty(mAlias)) {
            mFriend.alias = mAlias;
        }
        mFriend.friendAccount = mAccount;
        mFriend.userId = mUserId;
        mFriend.headImage = mHeadIndex;
        mRepository.deleteFriend(mAccount, null);
        mRepository.insertFriend(mFriend, new LoginDataSource.AddFriendSuccessCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(mActivity, "保存成功!", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    //返回
    private void back() {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
