package org.vosk.demo;

import android.graphics.Bitmap;

import java.util.List;

public interface Callback {
    public void updateMyList(List lstObj);
    public void updateImg(Bitmap bitmap);
}
