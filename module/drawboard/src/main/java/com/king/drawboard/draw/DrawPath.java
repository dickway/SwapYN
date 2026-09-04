package com.king.drawboard.draw;

import android.graphics.Canvas;
import android.graphics.Path;
import android.util.Log;

public class DrawPath extends Draw {

    private Path path;

    private float lastX;
    private float lastY;

    public DrawPath() {
        path = new Path();
    }

    public void setPath(Path path) {
        this.path = path;
    }

    @Override
    public void actionDown(Canvas canvas, float x, float y) {
        super.actionDown(canvas, x, y);
        path.moveTo(x, y);
        lastX = x;
        lastY = y;
    }

    @Override
    public void actionMove(Canvas canvas, float x, float y) {
        super.actionMove(canvas, x, y);
        path.quadTo(lastX, lastY, (x + lastX) / 2, (y + lastY) / 2);
        canvas.drawPath(path, paint);
        lastX = x;
        lastY = y;
    }

    @Override
    public void actionUp(Canvas canvas, float x, float y) {
    }

    @Override
    public void actionDelete(Canvas canvas) {

        float imgW = (float) bitmap.getWidth();
        float imgH = (float) bitmap.getHeight();

        float bitmapX;
        float bitmapY;

        if (lastY <= 0) {//竖直方向超过图片
            bitmapY = lastY;
        } else if (lastY >= maxY) {
            bitmapY = lastY - imgH;
        } else {
            bitmapY = lastY - (imgH / 2);
        }

        if (lastX <= 0) {//横向方向超过图片
            bitmapX = lastX;
        } else if (lastX >= maxX) {
            bitmapX = lastX - imgW;
        } else {
            bitmapX = lastX - (imgW / 2);
        }

        canvas.drawBitmap(bitmap, bitmapX, bitmapY, null);
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawPath(path, paint);
    }
}
