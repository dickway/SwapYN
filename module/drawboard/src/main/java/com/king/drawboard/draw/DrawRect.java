package com.king.drawboard.draw;

import android.graphics.Canvas;
import android.graphics.RectF;

public class DrawRect extends Draw {

    private RectF rect;

    public DrawRect() {
        rect = new RectF();
    }

    public void setRect(RectF rect) {
        this.rect = rect;
    }

    @Override
    public void actionDown(Canvas canvas, float x, float y) {
        super.actionDown(canvas, x, y);
        rect.left = x;
        rect.top = y;
    }

    @Override
    public void actionMove(Canvas canvas, float x, float y) {
        super.actionMove(canvas, x, y);
        rect.right = x;
        rect.bottom = y;
        canvas.drawRect(rect, paint);
    }

    @Override
    public void actionDelete(Canvas canvas) {
        float imgW = (float) bitmap.getWidth();
        float imgH = (float) bitmap.getHeight();

        float bitmapX;
        float bitmapY;

        if (rect.bottom <= 0) {//竖直方向超过图片
            bitmapY = rect.bottom;
        } else if (rect.bottom >= maxY) {
            bitmapY = rect.bottom - imgH;
        } else {
            bitmapY = rect.bottom - (imgH / 2);
        }

        if (rect.right <= 0) {//横向方向超过图片
            bitmapX = rect.right;
        } else if (rect.right >= maxX) {
            bitmapX = rect.right - imgW;
        } else {
            bitmapX = rect.right - (imgW / 2);
        }

        canvas.drawBitmap(bitmap, bitmapX, bitmapY, null);
    }


    @Override
    public void draw(Canvas canvas) {
        canvas.drawRect(rect, paint);
    }
}
