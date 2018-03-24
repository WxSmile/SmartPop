package com.weixiao.smartpop;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.weixiao.psmart.Location;
import com.weixiao.psmart.SmartPop;

import static com.weixiao.psmart.PopSize.*;

public class MainActivity extends Activity implements PopupWindow.OnDismissListener, View.OnClickListener {

    private View popupContentView;
    private SmartPop.Builder builder;
    private SmartPop smartPop;
    private static final String TAG = "TEST";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        popupContentView = LayoutInflater.from(this).inflate(R.layout.popup_content_layout,null);
        builder = new SmartPop.Builder();
        builder.setContentView(popupContentView)
                .setSizeType(TYPE_WRAP_CONTENT)
                .setOutsideTouchable(true)
                .setDismissListener(this);
    }

    public void smartPopLT(View view) {
        smartPop = builder.setAnchorView(view)
                .setLocale(Location.TO_ANCHOR_LEFT)
                .setContentBaseLine(1.0f)
                .build();
        smartPop.show();
    }

    public void smartPopRT(View view) {
        smartPop = builder.setAnchorView(view)
                .setLocale(Location.TO_ANCHOR_LEFT)
                .setContentBaseLine(-1.0f)
                .build();
        smartPop.show();
    }

    public void smartPopLC(View view) {
//        smartPop = builder.setAnchorView(view)
//                .setLocale(Location.TO_ANCHOR_LEFT)
//                .setContentBaseLine(1.0f)
//                .build();
//        smartPop.show();
        View popupContentView = LayoutInflater.from(this).inflate(R.layout.popuw_content_top_arrow_layout,null);

        SmartPop smartPop = new SmartPop.Builder()
                .setContentView(popupContentView)
                .setAnchorView(view)
                .setArrowView(R.id.down_arrow)
                .setLocale(Location.ABOVE_ANCHOR)
                .setSizeType(TYPE_WRAP_CONTENT)
                .setContentBaseLine(0.7f)
                .build();
        smartPop.show();
    }

    public void smartPopRC(View view) {
        smartPop = builder.setAnchorView(view)
                .setLocale(Location.TO_ANCHOR_LEFT)
                .setContentBaseLine(-1.0f)
                .build();
        smartPop.show();
    }

    public void smartPopLB(View view) {
        View popupContentView = LayoutInflater.from(this).inflate(R.layout.popup_content_layout,null);
        SmartPop smartPop = new SmartPop.Builder().setAnchorView(view)
                .setContentView(popupContentView)
                .setLocale(Location.BELOW_ANCHOR)
                .setSizeType(TYPE_WRAP_CONTENT)
                .setContentBaseLine(1.0f)
                .build();
        smartPop.show();
    }

    public void smartPopRB(View view) {
        smartPop = builder.setAnchorView(view)
                .setLocale(Location.BELOW_ANCHOR)
                .setContentBaseLine(-1.0f)
                .build();
        TextView menuItem1 = smartPop.getView(R.id.menu_item1);
        menuItem1.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        menuItem1.setOnClickListener(this);
        smartPop.show();
    }

    @Override
    public void onDismiss() {
        Log.d(TAG, "onDismiss: cc");
    }

    @Override
    public void onClick(View view) {
        Log.d(TAG, "onClick: cc");
        smartPop.dismiss();
    }
}