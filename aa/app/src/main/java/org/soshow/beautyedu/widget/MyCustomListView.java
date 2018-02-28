package org.soshow.beautyedu.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

public class MyCustomListView extends ListView {

    public MyCustomListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public MyCustomListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyCustomListView(Context context) {
        super(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }

}
