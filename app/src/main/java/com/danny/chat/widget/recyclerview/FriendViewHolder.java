package com.danny.chat.widget.recyclerview;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.danny.chat.R;


/**
 * Created by danny on 3/31/18.
 */

public class FriendViewHolder extends RecyclerView.ViewHolder {
    public LinearLayout mFriendItemRoot;
    public TextView mFriendAccount;
    public TextView mDelete;
    public ImageView mFriendHead;

    public FriendViewHolder(View itemView) {
        super(itemView);
        mFriendItemRoot = itemView.findViewById(R.id.huanxin_friend_item_root);
        mFriendAccount = itemView.findViewById(R.id.huanxin_friend_account);
        mDelete = itemView.findViewById(R.id.huanxin_friend_item_delete);
        mFriendHead = itemView.findViewById(R.id.huanxin_friend_head);
    }
}
