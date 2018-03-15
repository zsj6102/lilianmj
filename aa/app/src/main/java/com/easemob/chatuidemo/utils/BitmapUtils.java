package com.easemob.chatuidemo.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PorterDuff;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;

import org.soshow.beautyedu.utils.ScreenUtils;


/**
 * Created by Administrator on 2018/3/7 0007.
 */

public class BitmapUtils {
    public static Bitmap textAsBitmap(String text, float textSize,Context context) {
        TextPaint textPaint = new TextPaint();
// textPaint.setARGB(0x31, 0x31, 0x31, 0);
        textPaint.setColor(Color.BLACK);
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(textSize);
        StaticLayout layout = new StaticLayout(text, textPaint, ScreenUtils.getScreenWidth(context) ,
                Layout.Alignment.ALIGN_NORMAL, 1.3f, 0.0f, true);
        Bitmap bitmap = Bitmap.createBitmap(layout.getWidth(),
                layout.getHeight() + 20, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.translate(0, 0);
        canvas.drawColor(Color.WHITE);
// canvas.drawColor(Color.GRAY);
//        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);//绘制透明色
        layout.draw(canvas);
        return bitmap;
    }
    public static Bitmap add2Bitmap(Bitmap first, Bitmap second,Context context) {
        int firstWidth = first.getWidth();
        int firstHeight  = first.getHeight();
        // 计算缩放比例.
        float scaleWidth = ((float) ScreenUtils.getScreenWidth(context)) / firstWidth;
        float scaleHeight = ((float) ScreenUtils.getScreenHeight(context)) / firstHeight;
        // 取得想要缩放的matrix参数.
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // 得到新的图片.
        Bitmap newbm = Bitmap.createBitmap(first, 0, 0, firstWidth, firstHeight, matrix, true);
        int height = newbm.getHeight() + second.getHeight();
        Bitmap result = Bitmap.createBitmap(ScreenUtils.getScreenWidth(context) , height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);
        canvas.drawBitmap(newbm,0, 0, null);
        canvas.drawBitmap(second, 0, newbm.getHeight(), null);
        return result;
    }
}
