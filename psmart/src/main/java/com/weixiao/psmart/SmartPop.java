package com.weixiao.psmart;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;

import static com.weixiao.psmart.Location.*;
import static com.weixiao.psmart.PopSize.*;
import static com.weixiao.psmart.ScreenSize.*;

public class SmartPop {

    /**
     * 锚点
     */
    private View mAnchorView;

    /**
     * 展示的内容
     */
    private View mContentView;
    private SparseArray<View> mViews;

    private View mArrowView;

    /**
     * 宽高
     */
    @PopSize
    private int mSizeType;
    private int mPopupWindowWidth;
    private int mPopupWindowHeight;
    private int mPopupWindowMaxWidth;
    private int mPopupWindowMaxHeight;

    /**
     * 位置
     */
    @Location
    private int mLocale;
    private int mPopupWindowXPos;
    private int mPopupWindowYPos;

    /**
     * 与默认Location垂直的方向上的偏移
     */
    private float mIntersectOffset;

    /**
     * -1 < x < 1
     * 默认Location上的位置偏移
     */
    private float contentBaseLine;

    private PopupWindow mPopupWindow;

    private SmartPop() {}

    private SmartPop(SmartPop smartPop) {
        mPopupWindow = smartPop.getPopupWindow();
        mViews = smartPop.getViews();
        mAnchorView = smartPop.getAnchorView();
        mContentView = smartPop.getContentView();
        mSizeType = smartPop.getSizeType();
        mLocale = smartPop.getLocale();
        mIntersectOffset = smartPop.getIntersectOffset();
        contentBaseLine = smartPop.getContentBaseLine();
        mPopupWindowXPos = smartPop.mPopupWindowXPos;
        mPopupWindowYPos = smartPop.mPopupWindowYPos;
    }

    public PopupWindow getPopupWindow() {
        return mPopupWindow;
    }

    public View getAnchorView() {
        return mAnchorView;
    }

    public View getContentView() {
        return mContentView;
    }

    public int getSizeType() {
        return mSizeType;
    }

    public int getLocale() {
        return mLocale;
    }

    public float getIntersectOffset() {
        return mIntersectOffset;
    }

    public float getContentBaseLine() {
        return contentBaseLine;
    }

    public SparseArray<View> getViews() {
        return mViews;
    }

    public <T extends View> T getView(int viewId) {
        View view = mViews.get(viewId);
        if (view == null)
        {
            view = mContentView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return (T) view;
    }

    public void show() {
        mPopupWindow.showAtLocation(mAnchorView, Gravity.TOP | Gravity.START,mPopupWindowXPos,mPopupWindowYPos);
    }

    public void dismiss() {
        if (mPopupWindow == null) return;
        if (mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
        }
    }


    public static class Builder {

        private SmartPop target;

        public Builder() {
            target = new SmartPop();
            target.mPopupWindow = new PopupWindow();
            target.mViews = new SparseArray<>();
        }

        public Builder setAnchorView(@NonNull View mAnchorView) {
            target.mAnchorView = mAnchorView;
            return this;
        }

        public Builder setContentView(@NonNull View mContentView) {
            target.mContentView = mContentView;
            return this;
        }

        public Builder setArrowView(int viewId) {
            target.mArrowView = target.getView(viewId);
            return this;
        }

        public Builder setSizeType(@PopSize int mSizeType) {
            target.mSizeType = mSizeType;
            return this;
        }

        public Builder setLocale(@Location int mLocale) {
            target.mLocale = mLocale;
            return this;
        }

        public Builder setIntersectOffset(float mOffset) {
            target.mIntersectOffset = mOffset;
            return this;
        }

        public Builder setContentBaseLine(float contentBaseLine) {
            target.contentBaseLine = contentBaseLine;
            return this;
        }

        public Builder setDismissListener(OnDismissListener smartDismissListener) {
            if (smartDismissListener == null) return this;
            target.mPopupWindow.setOnDismissListener(smartDismissListener);
            return this;
        }

        public Builder setAnimation(int animationStyle) {
            target.mPopupWindow.setAnimationStyle(animationStyle);
            return this;
        }

        public Builder setOutsideTouchable(boolean touchable) {
            target.mPopupWindow.setOutsideTouchable(touchable);
            return this;
        }

        public SmartPop build() {
            init();
            return new SmartPop(target);
        }

        private void init() {
            if (target.mAnchorView == null) throw new IllegalArgumentException("the anchor view cannot be null");
            if (target.mContentView == null) throw new IllegalArgumentException("the popup content view view cannot be null");

            target.mPopupWindow.setContentView(target.mContentView);

            //在一些版本中，如果不设置PopupWindow背景，那么点击外部区域和Back键都无法dismiss
            target.mPopupWindow.setBackgroundDrawable(new ColorDrawable());

            //popupWindow内容区域可以响应点击事件
            target.mPopupWindow.setTouchable(true);
            target.mPopupWindow.setFocusable(true);

            target.mPopupWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
            //整个popupWindow被输入法顶上去
            target.mPopupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
            calculatePosition();
        }

        private int getScreenSize(Context context, @ScreenSize int screenSize) {
            WindowManager wm = (WindowManager) context
                    .getSystemService(Context.WINDOW_SERVICE);
            DisplayMetrics outMetrics = new DisplayMetrics();
            wm.getDefaultDisplay().getMetrics(outMetrics);
            if (screenSize == SCREEN_WIDTH) {
                return outMetrics.widthPixels;
            }else if (screenSize == SCREEN_HEIGHT){
                return outMetrics.heightPixels;
            }else {
                throw new IllegalArgumentException("screenSize param error");
            }
        }

        /**
         * 计算popupWindow的坐标位置
         */
        private void calculatePosition() {
            int screenWidth = getScreenSize(target.mAnchorView.getContext(),SCREEN_WIDTH);
            int screenHeight = getScreenSize(target.mAnchorView.getContext(),SCREEN_HEIGHT);
            if (target.mPopupWindowMaxWidth == 0) target.mPopupWindowMaxWidth = screenWidth;
            if (target.mPopupWindowMaxHeight == 0) target.mPopupWindowMaxHeight = screenHeight;

            int widthSpec = View.MeasureSpec.makeMeasureSpec(target.mPopupWindowMaxWidth, View.MeasureSpec.AT_MOST);
            int heightSpec = View.MeasureSpec.makeMeasureSpec(target.mPopupWindowMaxHeight, View.MeasureSpec.AT_MOST);

            //measure the content how big should be
            target.mContentView.measure(widthSpec,heightSpec);

            switch (target.mSizeType) {
                case TYPE_WRAP_CONTENT:
                    target.mPopupWindowWidth = target.mContentView.getMeasuredWidth();
                    target.mPopupWindowHeight = target.mContentView.getMeasuredHeight();
                    break;
                case TYPE_CUSTOM_CONTENT:
                    if (target.mPopupWindowWidth == TYPE_WRAP_CONTENT) {
                        target.mPopupWindowWidth = target.mContentView.getMeasuredWidth();
                    }
                    if (target.mPopupWindowHeight == TYPE_WRAP_CONTENT) {
                        target.mPopupWindowHeight = target.mContentView.getMeasuredHeight();
                    }

                    break;
                default: throw new IllegalArgumentException("popupWindow size type is error");
            }

            target.mPopupWindow.setWidth(target.mPopupWindowWidth);
            target.mPopupWindow.setHeight(target.mPopupWindowHeight);

            //计算锚点的坐标和size
            Rect anchorViewRect = new Rect();
            target.mAnchorView.getGlobalVisibleRect(anchorViewRect);
            int anchorWidth = target.mAnchorView.getWidth();
            int anchorHeight = target.mAnchorView.getHeight();

            //计算锚点左上剩余空间
            int mAnchorLeftAreaSize = anchorViewRect.left;
            int mAnchorTopAreaSize = anchorViewRect.top;

            //默认popupWindow和锚点中线对齐为标准，计算popupWindow的坐标位置
            int xStandardSize = (target.mPopupWindowWidth - anchorWidth)/2;
            int yStandardSize = (target.mPopupWindowHeight - anchorHeight)/2;
            int standardXPos = mAnchorLeftAreaSize - xStandardSize;
            int standardYPos = mAnchorTopAreaSize - yStandardSize;
            if (standardYPos < 0) {
                standardYPos = 0;
            }

            target.mContentView.getViewTreeObserver()
                    .addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

                @SuppressLint("NewApi")
                @Override
                public void onGlobalLayout() {
                    if (target.mArrowView == null) return;
                    if (target.mArrowView.getRootView() != target.mContentView.getRootView()) return;
                    adjustArrowViewPosition();
                    target.mContentView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            });

            switch (target.mLocale) {
                case BELOW_ANCHOR:
                case ABOVE_ANCHOR:
                    int xOffset = (int) (target.contentBaseLine * Math.abs(xStandardSize));
                    target.mPopupWindowXPos = standardXPos + xOffset;
                    if (target.mLocale == BELOW_ANCHOR) {
                        target.mPopupWindowYPos = mAnchorTopAreaSize + anchorHeight;
                        if ((target.mPopupWindowYPos + target.mPopupWindowHeight) > screenHeight) {
                            target.mLocale = ABOVE_ANCHOR;
                            target.mPopupWindowYPos = mAnchorTopAreaSize - target.mPopupWindowHeight;
                        }
                    }else {
                        target.mPopupWindowYPos = mAnchorTopAreaSize - target.mPopupWindowHeight;
                        if (target.mPopupWindowYPos < 0) {
                            target.mLocale = BELOW_ANCHOR;
                            target.mPopupWindowYPos = mAnchorTopAreaSize + anchorHeight;
                        }
                    }
                    target.mPopupWindowYPos += target.mIntersectOffset;
                    if ((target.mPopupWindowXPos + target.mPopupWindowWidth) > screenWidth) {
                        target.mPopupWindowXPos = screenWidth - target.mPopupWindowWidth;
                    }else if (target.mPopupWindowXPos < 0) {
                        target.mPopupWindowXPos = 0;
                    }
                    break;
                case TO_ANCHOR_LEFT:
                case TO_ANCHOR_RIGHT:
                    int yOffset = (int) (target.contentBaseLine * Math.abs(yStandardSize));
                    target.mPopupWindowYPos = standardYPos + yOffset;
                    if (target.mLocale == TO_ANCHOR_LEFT) {
                        target.mPopupWindowXPos = mAnchorLeftAreaSize - target.mPopupWindowWidth;
                        if (target.mPopupWindowXPos < 0) {
                            target.mLocale = TO_ANCHOR_RIGHT;
                            target.mPopupWindowXPos = anchorViewRect.right;
                        }
                    }else {
                        target.mPopupWindowXPos = anchorViewRect.right;
                        if ((target.mPopupWindowXPos + target.mPopupWindowWidth) > screenWidth) {
                            target.mLocale = TO_ANCHOR_LEFT;
                            target.mPopupWindowXPos = mAnchorLeftAreaSize - target.mPopupWindowWidth;
                        }
                    }
                    target.mPopupWindowXPos += target.mIntersectOffset;
                    break;
            }
        }

        /**
         * 调整箭头的位置，指向锚点居中的位置
         */
        private void adjustArrowViewPosition() {
            Rect anchorRect = new Rect();
            target.mAnchorView.getGlobalVisibleRect(anchorRect);
            ViewGroup.MarginLayoutParams marginLayoutParams;
            if (target.mArrowView.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
                marginLayoutParams = (ViewGroup.MarginLayoutParams)target.mArrowView.getLayoutParams();
                //箭头指向锚点中部对齐
                if (target.mLocale == BELOW_ANCHOR || target.mLocale == ABOVE_ANCHOR) {
                    int arrowLeftMargin = anchorRect.left - target.mPopupWindowXPos + target.mAnchorView.getWidth() / 2 - target.mArrowView.getWidth() / 2;
                    marginLayoutParams.leftMargin = arrowLeftMargin;
                }else {
                    int arrowTopMargin = anchorRect.top - target.mPopupWindowYPos + target.mAnchorView.getHeight() / 2 - target.mArrowView.getHeight() / 2;
                    marginLayoutParams.topMargin = arrowTopMargin;
                }
            }
        }
    }
}
