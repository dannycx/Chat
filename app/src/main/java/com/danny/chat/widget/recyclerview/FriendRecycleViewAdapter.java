package com.danny.chat.widget.recyclerview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.danny.chat.R;

import java.util.List;
import java.util.Map;

/**
 * Created by danny on 3/31/18.
 */

public class FriendRecycleViewAdapter extends RecyclerView.Adapter {
    private static final String TAG = FriendRecycleViewAdapter.class.getSimpleName();
    //图标
    int[] mHeads = new int[]{R.mipmap.chat_friend_head_1, R.mipmap.chat_friend_head_2, R.mipmap.chat_friend_head_3,
            R.mipmap.chat_friend_head_4, R.mipmap.chat_friend_head_5, R.mipmap.chat_friend_head_6,
            R.mipmap.chat_friend_head_7, R.mipmap.chat_friend_head_8, R.mipmap.chat_friend_head_9};
    private List<String> mList;
    private Context mContext;
    private OnFriendHeadListener mHeadListener;
    private Map<Integer, Integer> mMaps;

    public FriendRecycleViewAdapter(Map<Integer, Integer> maps, List<String> list, Context context) {
        mMaps = maps;
        mList = list;
        mContext = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new FriendViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.huanxin_friend_item, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final FriendViewHolder viewHolder = (FriendViewHolder) holder;
        viewHolder.mFriendAccount.setText(mList.get(position));
        if (mMaps != null && mMaps.size() > position) {
            Log.d(TAG, "onBindViewHolder: " + mMaps.get(position));
            viewHolder.mFriendHead.setImageResource(mHeads[mMaps.get(position).intValue()]);
        }

//        viewHolder.mFriendHead.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(final View v) {
//                mHeadListener.onFriendHead(new OnFriendHeadSelectListener() {
//                    @Override
//                    public void onSelection(int position) {
//                        viewHolder.mFriendHead.setImageResource(head[position]);
//                    }
//                });
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void removeItem(int position) {
        if (mList.size() > position) {
            mList.remove(position);
        }
        notifyDataSetChanged();
    }
}
