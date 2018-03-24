package com.weixiao.psmart;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@IntDef({PopSize.TYPE_WRAP_CONTENT, PopSize.TYPE_CUSTOM_CONTENT})
@Retention(RetentionPolicy.SOURCE)
public @interface PopSize {
    int TYPE_WRAP_CONTENT = 1;
    int TYPE_CUSTOM_CONTENT = 2;
}
