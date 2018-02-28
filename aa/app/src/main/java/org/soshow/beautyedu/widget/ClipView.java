/**
 * Copyright © 1999－2015 闲闪(厦门)互动文化传播有限公司(Soshow.org). All rights reserved.
 * you may not use this file except in compliance with the License.
 * http://www.soshow.org
 */
package org.soshow.beautyedu.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * 
 * @author king
 * @time 2014-6-18 3:53:00
 */
public class ClipView extends View {

    public static final int BORDERDISTANCE = 20;

    private Paint paint;

    public ClipView(Context context) {
        this(context, null);
    }

    public ClipView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ClipView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        paint = new Paint();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = this.getWidth();
        int height = this.getHeight();

        int borderlength = (width - BORDERDISTANCE * 2);
        paint.setColor(0xaa000000);

        // top
        canvas.drawRect(0, 0, width, (height - borderlength) / 2, paint);
        // bottom
        canvas.drawRect(0, (height + borderlength) / 2, width, height, paint);
        // left
        canvas.drawRect(0, (height - borderlength) / 2, BORDERDISTANCE,
                (height + borderlength) / 2, paint);
        // right
        canvas.drawRect(width - BORDERDISTANCE, (height - borderlength) / 2,
                width, (height + borderlength) / 2, paint);

        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(2.0f);
        // top
        canvas.drawLine(BORDERDISTANCE, (height - borderlength) / 2, width
                - BORDERDISTANCE, (height - borderlength) / 2, paint);
        // bottom
        canvas.drawLine(BORDERDISTANCE, (height + borderlength) / 2, width
                - BORDERDISTANCE, (height + borderlength) / 2, paint);
        // left
        canvas.drawLine(BORDERDISTANCE, (height - borderlength) / 2,
                BORDERDISTANCE, (height + borderlength) / 2, paint);
        // right
        canvas.drawLine(width - BORDERDISTANCE, (height - borderlength) / 2,
                width - BORDERDISTANCE, (height + borderlength) / 2, paint);
    }

}
