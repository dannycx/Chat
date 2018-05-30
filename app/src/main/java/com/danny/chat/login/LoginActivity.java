package com.danny.chat.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.danny.chat.MainActivity;
import com.danny.chat.R;
import com.danny.chat.data.LoginDataSource;
import com.danny.chat.data.LoginInjection;
import com.danny.chat.data.LoginRepository;
import com.danny.chat.data.User;
import com.danny.chat.utils.SpUtils;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = LoginActivity.class.getSimpleName();
    private LoginActivity mActivity;
    private EditText mUsername;
    private EditText mPassword;
    private Button mLogin;
    private Button mRegister;
    private String mName;
    private String mPwd;
    private LoginRepository mRepository;
    private User mUser;
    private InputMethodManager mInputMethodManager;
    private TextView mFormat;
    private String mUsernameRegex = "[A-Za-z0-9]+";
    private String mPasswordRegex = "[A-Za-z0-9]+";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mActivity = this;
        mInputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        mUsername = findViewById(R.id.login_username);
        mPassword = findViewById(R.id.login_password);
        mLogin = findViewById(R.id.login_login);
        mLogin.setOnClickListener(this);
        mRegister = findViewById(R.id.login_register);
        mRegister.setOnClickListener(this);
        mFormat = findViewById(R.id.login_format);
        mRepository = LoginInjection.getInstance(mActivity);
        mUser = new User();

        mPassword.setOnKeyListener(new View.OnKeyListener() {
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
        mPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Log.d(TAG, "onFocusChange: " + hasFocus);
                if (hasFocus) {
                    Log.d(TAG, "onFocusChange: 隐藏");
                    mFormat.setVisibility(View.GONE);
                } else {
                    mFormat.setVisibility(View.VISIBLE);
                    Log.d(TAG, "onFocusChange: " + result());
                    mFormat.setText(result());
                }
            }
        });
    }

    private String result() {
        String username = mUsername.getText().toString().trim();
        String password = mPassword.getText().toString().trim();
        Pattern patternUsr = Pattern.compile(mUsernameRegex);
        Pattern patternPwd = Pattern.compile(mPasswordRegex);
        Matcher matcherUsr = patternUsr.matcher(username);
        Matcher matcherPwd = patternPwd.matcher(password);
        if (TextUtils.isEmpty(username) && TextUtils.isEmpty(password)) {
            return "用户名和密码不能为空!";
        } else if (TextUtils.isEmpty(username)) {
            return "用户名不能为空!";
        } else if (TextUtils.isEmpty(password)) {
            return "密码不能为空!";
        } else if (!matcherUsr.find() && !matcherPwd.find()) {
            return "用户名和密码格式均不正确!";
        } else if (!matcherUsr.find()) {
            return "用户名格式不正确!";
        } else if (!matcherPwd.find()) {
            return "密码格式不正确!";
        } else {
            return "";
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_login:
                Log.d(TAG, "onClick: " + result());
                login();
                break;
            case R.id.login_register:
                Log.d(TAG, "onClick: " + result());
                register();
                break;
            default:
                break;
        }
    }

    //登录
    private void login() {
        mName = mUsername.getText().toString().trim();
        mPwd = mPassword.getText().toString().trim();
        if (TextUtils.isEmpty(mName) || TextUtils.isEmpty(mPwd)) {
            Toast.makeText(getApplication(), "用户名或密码为空", Toast.LENGTH_SHORT).show();
            return;
        }
        EMClient.getInstance().login(mName, mPwd, new EMCallBack() {
            @Override
            public void onSuccess() {
                EMClient.getInstance().chatManager().loadAllConversations();//同步加载所有的会话
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "登录成功", Toast.LENGTH_SHORT).show();
                        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
                        mUser.id = uuid;
                        SpUtils.put(mActivity, "hyphenate_current_user", uuid);
                        mUser.account = mName;
                        mRepository.queryUser(mName, new LoginDataSource.LoadUserCallback() {
                            @Override
                            public void onSuccess(User user) {
                                Log.d(TAG, "onSuccess: 用户已存在");
                            }

                            @Override
                            public void onError() {
                                Log.d(TAG, "run: 用户不存在" + mUser.id);
                                mRepository.insertUser(mUser);
                            }
                        });
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        mActivity.finish();
                    }
                });
            }

            @Override
            public void onError(final int i, final String s) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "登录失败:" + i + "-" + s, Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onProgress(int i, String s) {
            }
        });
    }

    //注册
    private void register() {
        mName = mUsername.getText().toString().trim();
        mPwd = mPassword.getText().toString().trim();
        if (TextUtils.isEmpty(mName) || TextUtils.isEmpty(mPwd)) {
            Toast.makeText(getApplication(), "用户名或密码为空", Toast.LENGTH_SHORT).show();
            return;
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    EMClient.getInstance().createAccount(mName, mPwd);
                    Toast.makeText(getApplicationContext(), "注册成功", Toast.LENGTH_SHORT).show();
                } catch (HyphenateException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "注册失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
