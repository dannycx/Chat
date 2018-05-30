package com.danny.chat.widget.expressionview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.danny.chat.R;


/**
 * Created by danny on 3/28/18.
 */
public class ExpressionAdapter extends BaseAdapter {

    private final int mPerPagerCount = 21;//每页表情个数
    private Context mContext;//上下文
    private int mExpressionPagerNum;//第几页
    private int mExpressionItemNum;//第几个表情

    public ExpressionAdapter(Context context, int expressionPagerNum) {
        mContext = context;
        this.mExpressionPagerNum = expressionPagerNum;
    }

    @Override
    public int getCount() {
        return mPerPagerCount;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (null == convertView) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.expression_pager_item, parent, false);
            holder = new ViewHolder();
            holder.mItem = convertView.findViewById(R.id.expression_item_face);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        mExpressionItemNum = (mExpressionPagerNum - 1) * mPerPagerCount + position;
        holder.mItem.setImageResource(ExpressionManager.getInstance().getExpression(mExpressionItemNum));
        holder.mItem.setTag(mExpressionItemNum);
        return convertView;
    }

    class ViewHolder {
        ImageView mItem;
    }
}
