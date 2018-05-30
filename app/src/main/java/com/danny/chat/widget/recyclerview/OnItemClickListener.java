package com.danny.chat.widget.recyclerview;

import android.view.View;

/**
 * Created by danny on 28/12/17.
 */

public interface OnItemClickListener {

    void onItemClick(View view, int position);

    void onDeleteClick(String account, int position);
}