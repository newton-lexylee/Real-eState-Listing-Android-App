package tmedia.ir.melkeurmia.tools;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by tmedia on 9/29/2017.
 */


public class NonSwipeableViewPagerBase extends ViewPager {

    public NonSwipeableViewPagerBase(Context context) {
        super(context);
    }

    public NonSwipeableViewPagerBase(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        // Never allow swiping to switch between pages
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Never allow swiping to switch between pages
        return false;
    }


}