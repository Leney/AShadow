package com.mosect.ashadow;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * 表示一种阴影
 */
public abstract class Shadow {

    private int usedCount;
    protected Object key;

    /**
     * 如果有地方引用此对象，需要调用此方法
     */
    public void bind() {
        synchronized (this) {
            usedCount++;
        }
    }

    /**
     * 如果不需要引用此对象，调用此方法
     */
    public void unbind() {
        synchronized (this) {
            if (usedCount > 0) {
                usedCount--;
            }
            if (usedCount == 0) {
                onDestroy();
            }
        }
    }

    /**
     * 判断此阴影对象是否被引用
     *
     * @return true，被引用
     */
    public boolean isUsed() {
        return usedCount > 0;
    }

    /**
     * 获取阴影key
     *
     * @return 阴影key
     */
    public Object getKey() {
        return key;
    }

    /**
     * 释放阴影对象
     */
    protected abstract void onDestroy();

    /**
     * 将阴影对象画在画布上
     *
     * @param canvas 画布
     * @param rect   位置和大小
     * @param paint  额外画笔
     */
    public abstract void draw(@NonNull Canvas canvas, @NonNull Rect rect, @Nullable Paint paint);
}