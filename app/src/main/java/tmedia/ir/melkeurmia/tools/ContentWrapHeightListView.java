package tmedia.ir.melkeurmia.tools;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * Created by tmedia on 6/28/2018.
 */

public class ContentWrapHeightListView extends ListView {

    public ContentWrapHeightListView  (Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ContentWrapHeightListView  (Context context) {
        super(context);
    }

    public ContentWrapHeightListView  (Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }

}