package com.danny.chat.widget.expressionview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.view.ViewPager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.danny.chat.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 聊天表情页面
 * Created by danny on 3/29/18.
 */

public class ExpressionView extends LinearLayout implements ViewPager.OnPageChangeListener,
        AdapterView.OnItemClickListener, View.OnClickListener {
    public static int TAG_MODE_IMG = 1;
    public static int TAG_MODE_EMOJI = 2;
    // Convert to Html Tag, using for webPage
    private static String imgTag = "<img src=\"http://www.host.com/images/-1.png\" border=\"0\" alt=\"\"/>";
    private int mExpressionDefaultSize = 17;
    private List<View> mExpressionPagerView;
    private ViewPager mViewPager;
    private List<ImageView> mDotView;
    private LinearLayout mDotLayout;
    private Context mContext;
    private OnExpressionSelectedListener mSelectedListener;//表情包选择回调
    private EditText mEdit;//输入框
    private View mBtnView;//表情和键盘切换view
    private int m_tagMode;
    private int faceStyleResource;
    private int textStyleResource;

    /**
     * 实例化方法
     *
     * @param context
     * @param attrs
     */
    public ExpressionView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        m_tagMode = TAG_MODE_IMG;
        LayoutInflater.from(context).inflate(R.layout.expression_view, this, true);
        initView();
    }

    // 设置标签模式
    public void setTagMode(int tagMode) {
        m_tagMode = tagMode;
    }

    /**
     * set the button of show or hide the faceview
     *
     * @param btnView
     * @author saderos
     */
    public void setBtnView(View btnView) {
        mBtnView = btnView;
        mBtnView.setOnClickListener(this);
    }

    public void setFaceAndTextStyle(int faceStyle, int textStyle) {
        this.faceStyleResource = faceStyle;
        this.textStyleResource = textStyle;
    }


    /**
     * if you just need to set the editText， and them ,you don't have to append
     * text to the editText yourself
     *
     * @param editText
     * @author saderos
     */
    public void setEdit(EditText editText) {
        mEdit = editText;
        mEdit.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    ExpressionView.this.setVisibility(View.GONE);
                    setShowImageResource();
                }
                return false;
            }
        });
    }

    public void setHideToggle(View v) {
        v.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    ExpressionView.this.setVisibility(View.GONE);
                    setShowImageResource();
                }
                return false;
            }
        });
    }

    public void setOnExpressionSelectedListener(OnExpressionSelectedListener listener) {
        mSelectedListener = listener;
    }

    /**
     * 初始化视图
     */
    private void initView() {
        if (isInEditMode()) {//指示此视图当前是否处于编辑模式
            return;
        }
        mViewPager = findViewById(R.id.chat_expression_pager);
        mDotLayout = findViewById(R.id.chat_expression_bottom_dot);
        initFaceBar();
    }

    private void initFaceBar() {
        mExpressionPagerView = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            GridView view = new GridView(mContext);
            ExpressionAdapter adapter = new ExpressionAdapter(mContext, i);
            view.setOnItemClickListener(this);
            view.setAdapter(adapter);
            view.setNumColumns(7);
            view.setBackgroundColor(Color.TRANSPARENT);
            view.setHorizontalSpacing(1);
            view.setVerticalSpacing(1);
            view.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
            view.setCacheColorHint(0);
            view.setSelector(new ColorDrawable(Color.TRANSPARENT));
            view.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
                    LayoutParams.WRAP_CONTENT));
            view.setGravity(Gravity.CENTER);
            mExpressionPagerView.add(view);
        }

        ViewPagerAdapter adapter = new ViewPagerAdapter(mExpressionPagerView);
        if (!isInEditMode()) {
            mViewPager.setAdapter(adapter);
            mViewPager.setCurrentItem(1);
        }
        mViewPager.setOnPageChangeListener(this);
        initDot();
    }

    private void initDot() {
        mDotView = new ArrayList<>();
        if (mExpressionPagerView.size() <= 3) return;
        for (int i = 0; i < mExpressionPagerView.size() - 2; i++) {
            ImageView imgView = new ImageView(mContext);
            imgView.setBackgroundResource(R.mipmap.expression_view_pager_point_normal);
            LayoutParams params = new LayoutParams(
                    new ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT,
                            LayoutParams.WRAP_CONTENT));
            DisplayMetrics dm = getResources().getDisplayMetrics();
            params.leftMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, dm);
            params.rightMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, dm);
            mDotLayout.addView(imgView, params);
            mDotView.add(imgView);
        }
        setSelDot(0);
    }

    private void setSelDot(int nSelIndex) {
        for (int i = 0; i < mDotView.size(); i++) {
            if (nSelIndex == i) {
                mDotView.get(i).setBackgroundResource(
                        R.mipmap.expression_view_pager_point_abnormal);
            } else {
                mDotView.get(i).setBackgroundResource(
                        R.mipmap.expression_view_pager_point_normal);
            }
        }
    }

    @Override
    public void onPageSelected(int arg0) {

        if (0 == arg0) {
            mViewPager.setCurrentItem(arg0 + 1);
        } else if (arg0 == mExpressionPagerView.size() - 1) {
            mViewPager.setCurrentItem(arg0 - 1);
        } else {
            setSelDot(arg0 - 1);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View v, int arg2, long arg3) {
        View img = v.findViewById(R.id.expression_item_face);
        String selImgTag = "";
        int id = Integer.parseInt(img.getTag().toString());
        if (m_tagMode == TAG_MODE_IMG) {
            selImgTag = imgTag.replace("-1", id + "");
        } else {
            selImgTag = getImgTag(id);
        }
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
                ExpressionManager.getInstance().getExpression(id));
        if (mSelectedListener != null) {
            mSelectedListener.onExpressionSelected(selImgTag, bitmap);
        }
        if (mEdit != null) {
            Drawable drawable = new BitmapDrawable(mContext.getResources(), bitmap);
            int size = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mExpressionDefaultSize, getResources().getDisplayMetrics());
            drawable.setBounds(0, 0, size, size);
            ImageSpan imgSpan = new ImageSpan(drawable);

            SpannableString spanString = new SpannableString(selImgTag);
            spanString.setSpan(imgSpan, 0, spanString.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            mEdit.append(spanString);
        }
    }

    @Override
    public void onPageScrollStateChanged(int arg0) {
    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
    }

    @Override
    public void onClick(View v) {
        if (v == mBtnView) {// 点击输入框，则隐藏表情
            InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (this.getVisibility() == View.GONE) {
                imm.hideSoftInputFromWindow(getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                this.setVisibility(View.VISIBLE);
            } else {
                imm.showSoftInput(mEdit, InputMethodManager.SHOW_IMPLICIT);
                this.setVisibility(View.GONE);
            }
            setShowImageResource();
        }

    }

    private void setShowImageResource() {
        if (textStyleResource == 0 || faceStyleResource == 0) {
            //设置默认显示
            if (mBtnView instanceof ImageView) {
                if (getVisibility() == View.GONE)
                    ((ImageView) mBtnView).setImageResource(R.mipmap.chat_expression);
                else
                    ((ImageView) mBtnView).setImageResource(R.mipmap.chat_keybroad);
            }
        } else {
            if (getVisibility() == View.GONE)
                mBtnView.setBackgroundResource(faceStyleResource);
            else
                mBtnView.setBackgroundResource(textStyleResource);
        }
    }

    private String getImgTag(int position) {
        String imgTag = "";
        // the number of total image is 134,so when the image number larger than 134 ,them return null
        if (position > 134)
            return null;
        imgTag = position + "";
        switch (imgTag.length()) {
            case 1:
                imgTag = "/e00" + imgTag;
                break;
            case 2:
                imgTag = "/e0" + imgTag;
                break;
            case 3:
                imgTag = "/e" + imgTag;
                break;
            default:
                break;
        }
        return imgTag;
    }

    public interface OnExpressionSelectedListener {
        void onExpressionSelected(String imgTag, Bitmap expression);
    }
}
