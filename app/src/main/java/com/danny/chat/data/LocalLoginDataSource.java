package com.danny.chat.data;


import com.danny.chat.utils.LoginExecutors;

import java.util.List;

/**
 * 数据库操作类实现
 * Created by danny on 3/30/18.
 */

public class LocalLoginDataSource implements LoginDataSource {
    private static volatile LocalLoginDataSource sInstance;
    private LoginExecutors mLoginExecutors;
    private UserDao mUserDao;
    private User mUser;
    private List<User> mUsers;
    private Friend mFriend;
    private List<Friend> mFriends;
    private List<String> mAccounts;

    private LocalLoginDataSource(LoginExecutors loginExecutors, UserDao userDao) {
        mLoginExecutors = loginExecutors;
        mUserDao = userDao;
    }

    public static LocalLoginDataSource getInstance(LoginExecutors loginExecutors, UserDao userDao) {
        if (sInstance == null) {
            synchronized (LocalLoginDataSource.class) {
                if (sInstance == null) {
                    sInstance = new LocalLoginDataSource(loginExecutors, userDao);
                }
            }
        }
        return sInstance;
    }

    @Override
    public void insertUser(final User user) {
        mLoginExecutors.getIoExecutor().execute(new Runnable() {
            @Override
            public void run() {
                mUserDao.insertUser(user);
            }
        });
    }

    @Override
    public void queryUser(final String account, final LoadUserCallback callback) {
        mLoginExecutors.getIoExecutor().execute(new Runnable() {
            @Override
            public void run() {
                mUser = mUserDao.queryUser(account);
                mLoginExecutors.getMainExecutor().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (mUser != null) {
                            if (callback != null) {
                                callback.onSuccess(mUser);
                            }
                        } else {
                            if (callback != null) {
                                callback.onError();
                            }
                        }
                    }
                });
            }
        });
    }

    @Override
    public void queryAllUser(final LoadAllUserCallback callback) {
        mLoginExecutors.getIoExecutor().execute(new Runnable() {
            @Override
            public void run() {
                mUsers = mUserDao.queryAllUser();
                mLoginExecutors.getMainExecutor().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (!mUsers.isEmpty()) {
                            if (callback != null) {
                                callback.onSuccess(mUsers);
                            }
                        } else {
                            if (callback != null) {
                                callback.onError();
                            }
                        }
                    }
                });
            }
        });
    }

    @Override
    public void deleteUser() {
        mLoginExecutors.getIoExecutor().execute(new Runnable() {
            @Override
            public void run() {
                mUserDao.deleteUser();
            }
        });
    }

    @Override
    public void deleteUser(final String account) {
        mLoginExecutors.getIoExecutor().execute(new Runnable() {
            @Override
            public void run() {
                mUserDao.deleteUser(account);
            }
        });
    }

    @Override
    public void insertFriend(final Friend friend, final AddFriendSuccessCallback callback) {
        mLoginExecutors.getIoExecutor().execute(new Runnable() {
            @Override
            public void run() {
                mUserDao.insertFriend(friend);
                mLoginExecutors.getMainExecutor().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (callback != null) {
                            callback.onSuccess();
                        }
                    }
                });
            }
        });
    }

    @Override
    public void queryFriend(final String friendAccount, final LoadFriendCallback callback) {
        mLoginExecutors.getIoExecutor().execute(new Runnable() {
            @Override
            public void run() {
                mFriend = mUserDao.queryFriend(friendAccount);
                mLoginExecutors.getMainExecutor().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (mFriend != null) {
                            if (callback != null) {
                                callback.onSuccess(mFriend);
                            }
                        } else {
                            if (callback != null) {
                                callback.onError();
                            }
                        }
                    }
                });
            }
        });
    }

    @Override
    public void queryAliasFriend(final String alias, final LoadFriendCallback callback) {
        mLoginExecutors.getIoExecutor().execute(new Runnable() {
            @Override
            public void run() {
                mFriend = mUserDao.queryAliasFriend(alias);
                mLoginExecutors.getMainExecutor().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (mFriend != null) {
                            if (callback != null) {
                                callback.onSuccess(mFriend);
                            }
                        } else {
                            if (callback != null) {
                                callback.onError();
                            }
                        }
                    }
                });
            }
        });
    }

    @Override
    public void queryAllFriend(final String userId, final LoadAllFriendCallback callback) {
        mLoginExecutors.getIoExecutor().execute(new Runnable() {
            @Override
            public void run() {
                mFriends = mUserDao.queryAllFriend(userId);
                mLoginExecutors.getMainExecutor().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (!mFriends.isEmpty()) {
                            if (callback != null) {
                                callback.onSuccess(mFriends);
                            }
                        } else {
                            if (callback != null) {
                                callback.onError();
                            }
                        }
                    }
                });
            }
        });
    }

    @Override
    public void queryAllFriendAccount(final String userId, final LoadAllFriendAccountCallback callback) {
        mLoginExecutors.getIoExecutor().execute(new Runnable() {
            @Override
            public void run() {
                mAccounts = mUserDao.queryAllFriendAccount(userId);
                mLoginExecutors.getMainExecutor().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (!mAccounts.isEmpty()) {
                            if (callback != null) {
                                callback.onSuccess(mAccounts);
                            }
                        } else {
                            if (callback != null) {
                                callback.onError();
                            }
                        }
                    }
                });
            }
        });
    }

    @Override
    public void deleteFriend() {
        mLoginExecutors.getIoExecutor().execute(new Runnable() {
            @Override
            public void run() {
                mUserDao.deleteFriend();
            }
        });
    }

    @Override
    public void deleteFriend(final String friendAccount, final DeleteFriendSuccessCallback callback) {
        mLoginExecutors.getIoExecutor().execute(new Runnable() {
            @Override
            public void run() {
                mUserDao.deleteFriend(friendAccount);
                mLoginExecutors.getMainExecutor().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (callback != null) {
                            callback.onSuccess();
                        }
                    }
                });
            }
        });
    }

    @Override
    public void deleteAliasFriend(final String alias, final DeleteFriendSuccessCallback callback) {
        mLoginExecutors.getIoExecutor().execute(new Runnable() {
            @Override
            public void run() {
                mUserDao.deleteAliasFriend(alias);
                mLoginExecutors.getMainExecutor().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (callback != null) {
                            callback.onSuccess();
                        }
                    }
                });
            }
        });
    }
}
