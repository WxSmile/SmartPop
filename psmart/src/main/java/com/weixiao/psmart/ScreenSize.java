package com.weixiao.psmart;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@IntDef({ScreenSize.SCREEN_WIDTH, ScreenSize.SCREEN_HEIGHT})
@Retention(RetentionPolicy.SOURCE)
public @interface ScreenSize {

    int SCREEN_WIDTH = 1;
    int SCREEN_HEIGHT = 2;
}
