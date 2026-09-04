package com.king.drawboard.draw;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;

import com.king.drawboard.action.MotionAction;

public abstract class Draw implements MotionAction {
    Bitmap bitmap;
    Paint paint;
    float maxY;
    float maxX;
    float upY;
    float upX;

    public Draw() {

    }

    public void setUpY(float upY) {
        this.upY = upY;
    }
    public float getUpY() {
        return upY;
    }

    public void setUpX(float upX) {
        this.upX = upX;
    }
    public float getUpX() {
        return upX;
    }

    public void setMaxY(float maxY) {
        this.maxY = maxY;
    }

    public void setMaxX(float maxX) {
        this.maxX = maxX;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public Paint getPaint() {
        return paint;
    }

    public void setPaint(Paint paint) {
        this.paint = paint;
    }

    @Override
    public void actionDown(Canvas canvas, float x, float y) {
    }

    @Override
    public void actionMove(Canvas canvas, float x, float y) {
    }

    @Override
    public void actionUp(Canvas canvas, float x, float y) {
    }

    @Override
    public void actionDelete(Canvas canvas) {

    }


    public abstract void draw(Canvas canvas);
}
