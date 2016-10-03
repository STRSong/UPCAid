package jwxt.cacher.cc.jwxt.picker;

import android.app.Activity;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import jwxt.cacher.cc.jwxt.R;
import jwxt.cacher.cc.jwxt.util.ConvertUtils;

/**
 * Created by xhaiben on 2016/10/3.
 */

public class MyOptionPicker extends OptionPicker {
    public MyOptionPicker(Activity activity, String[] options) {
        super(activity, options);
    }

    public MyOptionPicker(Activity activity, ArrayList<String> options) {
        super(activity, options);
    }

    @Nullable
    @Override
    protected View makeHeaderView() {
        LinearLayout linearLayout=new LinearLayout(activity);
        linearLayout.setLayoutParams(new RelativeLayout.LayoutParams(MATCH_PARENT, ConvertUtils.toPx(activity, 40)));
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.setGravity(Gravity.CENTER);
        TextView textView=new TextView(activity);
        textView.setText("设置当前周");
        textView.setTextColor(Color.argb(0xff,0x2f,0x97,0xd6));
        textView.setTextSize(18);
        linearLayout.addView(textView);
        return linearLayout;
    }

    @Nullable
    @Override
    protected View makeFooterView() {
        RelativeLayout topButtonLayout = new RelativeLayout(activity);
        topButtonLayout.setLayoutParams(new RelativeLayout.LayoutParams(MATCH_PARENT, ConvertUtils.toPx(activity, 40)));
        topButtonLayout.setBackgroundColor(topBackgroundColor);
        topButtonLayout.setGravity(Gravity.CENTER_VERTICAL);

//        Button cancelButton = new Button(activity);
//        cancelButton.setVisibility(cancelVisible ? View.VISIBLE : View.GONE);
//        RelativeLayout.LayoutParams cancelButtonLayoutParams = new RelativeLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
//        cancelButtonLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
//        cancelButtonLayoutParams.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
//        cancelButton.setLayoutParams(cancelButtonLayoutParams);
//        cancelButton.setBackgroundColor(Color.TRANSPARENT);
//        cancelButton.setGravity(Gravity.CENTER);
//        if (!TextUtils.isEmpty(cancelText)) {
//            cancelButton.setText(cancelText);
//        }
//        cancelButton.setTextColor(cancelTextColor);
//        cancelButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dismiss();
//                onCancel();
//            }
//        });
//        topButtonLayout.addView(cancelButton);

//        TextView titleView = new TextView(activity);
//        RelativeLayout.LayoutParams titleLayoutParams = new RelativeLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
//        int margin = ConvertUtils.toPx(activity, 20);
//        titleLayoutParams.leftMargin = margin;
//        titleLayoutParams.rightMargin = margin;
//        titleLayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
//        titleLayoutParams.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
//        titleView.setLayoutParams(titleLayoutParams);
//        titleView.setGravity(Gravity.CENTER);
//        if (!TextUtils.isEmpty(titleText)) {
//            titleView.setText(titleText);
//        }
//        titleView.setTextColor(titleTextColor);
//        topButtonLayout.addView(titleView);

        Button submitButton = new Button(activity);
        RelativeLayout.LayoutParams submitButtonLayoutParams = new RelativeLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        submitButtonLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
        submitButtonLayoutParams.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
        submitButton.setLayoutParams(submitButtonLayoutParams);
        submitButton.setBackgroundColor(Color.TRANSPARENT);
        submitButton.setGravity(Gravity.CENTER);
        if (!TextUtils.isEmpty(submitText)) {
            submitButton.setText(submitText);
        }
        submitButton.setTextColor(Color.argb(0xff,0x2f,0x97,0xd6));
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                onSubmit();
            }
        });
        topButtonLayout.addView(submitButton);

        return topButtonLayout;
    }
}
