package com.danny.chat.widget.expressionview;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import java.util.List;

/**
 * Created by danny on 3/28/18.
 */
public class ViewPagerAdapter extends PagerAdapter {

    private List<View> mPageViews;

    public ViewPagerAdapter(List<View> pageViews) {
        super();
        this.mPageViews = pageViews;
    }

    @Override
    public int getCount() {
        return mPageViews.size();
    }// 获取要滑动的控件的数量

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == arg1;
    }//判断显示的是否是同一张图片,将两个参数比较返回即可

    @Override
    public int getItemPosition(Object object) {
        return super.getItemPosition(object);
    }

    @Override// PagerAdapter只缓存三张要显示的图片，如果滑动的图片超出了缓存的范围，就会调用这个方法，将图片销毁
    public void destroyItem(View arg0, int arg1, Object arg2) {
        ((ViewPager) arg0).removeView(mPageViews.get(arg1));
    }

    @Override//当要显示的图片可以进行缓存的时候，会调用这个方法进行显示图片的初始化，我们将要显示的ImageView加入到ViewGroup中，然后作为返回值返回即可
    public Object instantiateItem(View arg0, int arg1) {
        ((ViewPager) arg0).addView(mPageViews.get(arg1));
        return mPageViews.get(arg1);
    }
}