package com.weixiao.psmart;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;



@IntDef({Location.TO_ANCHOR_LEFT, Location.ABOVE_ANCHOR, Location.TO_ANCHOR_RIGHT, Location.BELOW_ANCHOR})
@Retention(RetentionPolicy.SOURCE)
public @interface Location {
    int TO_ANCHOR_LEFT = -1;
    int ABOVE_ANCHOR = -2;
    int TO_ANCHOR_RIGHT = -3;
    int BELOW_ANCHOR = -4;
}
