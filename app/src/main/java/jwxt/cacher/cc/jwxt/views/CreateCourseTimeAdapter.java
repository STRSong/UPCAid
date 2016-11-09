package jwxt.cacher.cc.jwxt.views;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.opengl.Visibility;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import jwxt.cacher.cc.jwxt.R;
import jwxt.cacher.cc.jwxt.picker.MyPickerView;
import jwxt.cacher.cc.jwxt.util.ConvertUtils;
import jwxt.cacher.cc.jwxt.util.UIUtils;

/**
 * Created by xhaiben on 2016/11/4.
 */

public class CreateCourseTimeAdapter extends BaseAdapter {
    private ListView listView;
    private List<View> viewList;
    private List<TextView> titleList;
    private Context context;
    private Activity activity;
    private int time;
    private CreateCourseTimeAdapter adapter;
    private View popupView;
    private TextView textViewWeek;
    private TextView textViewSection;
    private TextView textViewPosition;


    public CreateCourseTimeAdapter(Context context, Activity activity, ListView listView) {
        this.adapter = this;
        this.context = context;
        this.activity = activity;
        this.viewList = new ArrayList<>();
        this.titleList = new ArrayList<>();
        this.time = 1;
        this.listView = listView;
        View view = View.inflate(context, R.layout.view_create_course_time, null);
        this.setWeekChoice(view);
        this.setSectionChoice(view);
        viewList.add(view);
        initHandler();
    }

    public CreateCourseTimeAdapter(Context context, int n) {

    }

    public CreateCourseTimeAdapter(Context context, List<View> viewList) {
        this.viewList = viewList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return viewList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return viewList.get(position);
    }

    public void addOtherTime() {
        final View view = View.inflate(this.context, R.layout.view_create_course_time, null);

        this.setWeekChoice(view);
        this.setSectionChoice(view);

        LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.create_course_time_linear);
        View viewTitle = View.inflate(this.context, R.layout.view_create_course_other, null);
        final TextView textView = (TextView) viewTitle.findViewById(R.id.view_header);
        textView.setText("其他时段 " + String.valueOf(time) + " ");
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String string = textView.getText().toString();
                try {
                    String[] strings = string.split("[ ]");
                    if (strings != null) {
                        int position = Integer.parseInt(strings[1]);
                        time--;
                        for (int i = 0, j = 1; i < titleList.size(); i++) {
                            if (i != position - 1) {
                                titleList.get(i).setText("其他时段 " + String.valueOf(j) + " ");
                                j++;
                            }
                        }
                        titleList.remove(position - 1);
                        viewList.remove(position);
                        adapter.notifyDataSetChanged();
                        listView.setAdapter(adapter);
                        UIUtils.setListViewHeightBasedOnItems(listView);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        titleList.add(textView);
        linearLayout.addView(viewTitle, 0);
        viewList.add(view);
        this.time++;
    }

    private void setWeekChoice(View view) {
        RelativeLayout layout = (RelativeLayout) view.findViewById(R.id.add_course_rlyt_week);
        final TextView textView = (TextView) view.findViewById(R.id.edit_course_txv_week);
        final PopupWindow weekChoiceWindow = initPopupWindowWeek(textView);
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (weekChoiceWindow == null) {
                    initPopupWindowWeek(textView);
                }
                if (weekChoiceWindow != null && !weekChoiceWindow.isShowing()) {
                    weekChoiceWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);
                    WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
                    lp.alpha = 0.5f;
                    activity.getWindow().setAttributes(lp);
                }
            }
        });
    }

    private void setSectionChoice(View view) {
        RelativeLayout layout = (RelativeLayout) view.findViewById(R.id.add_course_rlyt_section);
        final TextView textView = (TextView) view.findViewById(R.id.edit_course_txv_section);
        final PopupWindow sectionChoiceWindow = initPopupWindowSection(textView);
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sectionChoiceWindow == null) {
                    initPopupWindowSection(textView);
                }
                if (sectionChoiceWindow != null && !sectionChoiceWindow.isShowing()) {
                    sectionChoiceWindow.showAtLocation(sectionChoiceWindow.getContentView(), Gravity.CENTER, 0, 0);
                    WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
                    lp.alpha = 0.5f;
                    activity.getWindow().setAttributes(lp);
                }
            }
        });
    }

    private void initHandler() {

    }

    private PopupWindow initPopupWindowWeek(final TextView weekTextView) {
        final PopupWindow
                popupWindowWeek = new PopupWindow(context);
        popupView = View.inflate(context, R.layout.dlg_week_choose, null);
        final WeekGridViewAdapter adapter = new WeekGridViewAdapter(context);
        final List<View> viewList = adapter.getViewList();
        final Button buttonOdd = (Button) popupView.findViewById(R.id.dlg_week_choose_odd);
        final Button buttonEven = (Button) popupView.findViewById(R.id.dlg_week_choose_Even);
        final Button buttonFull = (Button) popupView.findViewById(R.id.dlg_week_choose_Full);
        final Button buttonCancel = (Button) popupView.findViewById(R.id.dlg_week_choose_btn_cancel);
        final Button buttonConfirm = (Button) popupView.findViewById(R.id.dlg_week_choose_btn_confirm);
        for (int i = 0; i < viewList.size(); i++) {
            final TextView textView = (TextView) viewList.get(i).findViewWithTag("text");
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    textView.setSelected(!textView.isSelected());
                    switch (adapter.getSelectedStatus()) {
                        case 1:
                            buttonOdd.setSelected(true);
                            break;
                        case 2:
                            buttonEven.setSelected(true);
                            break;
                        case 3:
                            buttonFull.setSelected(true);
                            break;
                        case 4:
                            buttonOdd.setSelected(false);
                            buttonEven.setSelected(false);
                            buttonFull.setSelected(false);
                            break;
                    }
                }
            });
        }

        buttonOdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonOdd.setSelected(!buttonOdd.isSelected());
                if (buttonEven.isSelected()) {
                    buttonEven.setSelected(false);
                }
                if (buttonFull.isSelected()) {
                    buttonFull.setSelected(false);
                }
                if (buttonOdd.isSelected()) {
                    for (int i = 1; i <= 25; i++) {
                        View view = viewList.get(i - 1);
                        TextView textView = (TextView) view.findViewWithTag("text");
                        if (i % 2 == 1) {
                            textView.setSelected(true);
                        } else {
                            textView.setSelected(false);
                        }
                    }
                } else {
                    for (int i = 1; i <= 25; i++) {
                        if (i % 2 == 1) {
                            View view = viewList.get(i - 1);
                            TextView textView = (TextView) view.findViewWithTag("text");
                            textView.setSelected(false);
                        }
                    }
                }

            }
        });

        buttonEven.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonEven.setSelected(!buttonEven.isSelected());
                if (buttonOdd.isSelected()) {
                    buttonOdd.setSelected(false);
                }
                if (buttonFull.isSelected()) {
                    buttonFull.setSelected(false);
                }
                if (buttonEven.isSelected()) {
                    for (int i = 1; i <= 25; i++) {
                        View view = viewList.get(i - 1);
                        TextView textView = (TextView) view.findViewWithTag("text");
                        if (i % 2 == 0) {

                            textView.setSelected(true);
                        } else {
                            textView.setSelected(false);
                        }
                    }
                } else {
                    for (int i = 1; i <= 25; i++) {
                        if (i % 2 == 0) {
                            View view = viewList.get(i - 1);
                            TextView textView = (TextView) view.findViewWithTag("text");
                            textView.setSelected(false);
                        }
                    }
                }
            }
        });
        buttonFull.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonFull.setSelected(!buttonFull.isSelected());
                if (buttonOdd.isSelected()) {
                    buttonOdd.setSelected(false);
                }
                if (buttonEven.isSelected()) {
                    buttonEven.setSelected(false);
                }
                if (buttonFull.isSelected()) {
                    for (int i = 0; i < 25; i++) {
                        View view = viewList.get(i);
                        TextView textView = (TextView) view.findViewWithTag("text");
                        textView.setSelected(true);
                    }
                } else {
                    for (int i = 0; i < 25; i++) {
                        View view = viewList.get(i);
                        TextView textView = (TextView) view.findViewWithTag("text");
                        textView.setSelected(false);
                    }
                }
            }
        });
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (popupWindowWeek.isShowing()) {
                    popupWindowWeek.dismiss();
                }
            }
        });
        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (buttonOdd.isSelected()) {
                    weekTextView.setText("1-25周(单周)");
                } else if (buttonEven.isSelected()) {
                    weekTextView.setText("1-25周(双周)");
                } else if (buttonFull.isSelected()) {
                    weekTextView.setText("1-25周");
                } else {
                    StringBuilder stringBuilder = new StringBuilder();
                    List<Integer> integers = new ArrayList<>();
                    boolean isQueue = true;
                    for (int i = 1; i <= 25; i++) {
                        View view = viewList.get(i - 1);
                        TextView textView = (TextView) view.findViewWithTag("text");
                        if (textView.isSelected()) {
                            integers.add(i);
                        }
                    }
                    if (integers.size() > 0) {
                        if (integers.size() == 1) {
                            stringBuilder.append("第" + String.valueOf(integers.get(0)));
                        } else {
                            int temp = integers.get(0);
                            for (int i = 1; i < integers.size(); i++) {
                                if (integers.get(i) - i != temp) {
                                    isQueue = false;
                                }
                            }
                            if (isQueue == true) {
                                stringBuilder.append(String.valueOf(integers.get(0))
                                        + "-"
                                        + String.valueOf(integers.get(integers.size() - 1)));
                            } else {
                                for (int i = 0; i < integers.size(); i++) {
                                    if (i != integers.size() - 1) {
                                        stringBuilder.append(String.valueOf(integers.get(i)) + " ");
                                    } else {
                                        stringBuilder.append(String.valueOf(integers.get(i)));
                                    }
                                }
                            }
                        }

                        stringBuilder.append("周");
                        weekTextView.setText(stringBuilder);
                    }
                }
                popupWindowWeek.dismiss();
            }
        });
        final GridView gridView = (GridView) popupView.findViewById(R.id.gridView_week);
        gridView.setAdapter(adapter);
        /*修复少1像素Bug*/
        gridView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        ViewGroup.LayoutParams params = gridView.getLayoutParams();
        params.height = gridView.getMeasuredHeight() * 5 + 1;
        gridView.setLayoutParams(params);
        /********/
        popupWindowWeek.setContentView(popupView);
        popupWindowWeek.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindowWeek.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindowWeek.setFocusable(true);
        popupWindowWeek.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.classroom_search_white));
        popupWindowWeek.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
                lp.alpha = 1f;
                activity.getWindow().setAttributes(lp);
            }
        });
        return popupWindowWeek;
    }

    private PopupWindow initPopupWindowSection(final TextView sectionTextView) {
        final PopupWindow
                popupWindow = new PopupWindow(context);
        final View popupView = View.inflate(context, R.layout.dlg_section_choose, null);
        final MyPickerView pickerViewDay = (MyPickerView) popupView.findViewById(R.id.day_picker);
        List<String> strings = new ArrayList<>();
        strings.add("周日");
        for (int i = 1; i <= 6; i++) {
            strings.add("周" + ConvertUtils.intToZH(i));
        }
        pickerViewDay.setData(strings);
        pickerViewDay.setSelected(0);

        final MyPickerView pickerViewSection = (MyPickerView) popupView.findViewById(R.id.section_picker);
        List<String> strings1 = new ArrayList<>();
        for (int i = 1; i <= 11; i += 2) {
            strings1.add("第" + String.valueOf(i) + "-" + String.valueOf(i + 1) + "节");
        }
        pickerViewSection.setData(strings1);
        pickerViewSection.setSelected(0);
        Button buttonCancel = (Button) popupView.findViewById(R.id.wheel_view_btn_cancel);
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (popupWindow.isShowing()) {
                    popupWindow.dismiss();
                }
            }
        });
        Button buttonSubmit = (Button) popupView.findViewById(R.id.wheel_view_btn_submit);
        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sectionTextView.setText(pickerViewDay.getSelected() + " " + pickerViewSection.getSelected());
                popupWindow.dismiss();
            }
        });
        popupWindow.setContentView(popupView);
        popupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.classroom_search_white));
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
                lp.alpha = 1f;
                activity.getWindow().setAttributes(lp);
            }
        });
        return popupWindow;
    }

    public Map<Integer, String[]> getCourseInfo() {
        Map<Integer, String[]> remap = new HashMap<>();
        for (int i = 0; i < viewList.size(); i++) {
            textViewWeek = (TextView) viewList.get(i).findViewById(R.id.edit_course_txv_week);
            textViewSection = (TextView) viewList.get(i).findViewById(R.id.edit_course_txv_section);
            textViewPosition = (TextView) viewList.get(i).findViewById(R.id.add_course_edt_position);
            String weekInfo = textViewWeek.getText().toString();
            String sectionInfo = textViewSection.getText().toString();
            String positionInfo = textViewPosition.getText().toString();
            //info[0] 周数 info[1] 节数 info[3] 地点
            String[] info = new String[3];
            //info[0]
            if (!weekInfo.equals("选择上课周数")) {
                StringBuilder builder = new StringBuilder();
                if (weekInfo.equals("1-25周(单周)")) {
                    for (int j = 1; j <= 25; j++) {
                        if (j % 2 == 1) {
                            builder.append(String.valueOf(j) + " ");
                        }
                    }
                } else if (weekInfo.equals("1-25周(双周)")) {
                    for (int j = 1; j <= 25; j++) {
                        if (j % 2 == 0) {
                            builder.append(String.valueOf(j) + " ");
                        }
                    }
                } else if (weekInfo.contains("-")) {
                    weekInfo = weekInfo.substring(0, weekInfo.indexOf("周"));
                    String[] strings = weekInfo.split("[-]");
                    int h = Integer.parseInt(strings[0]);
                    int t = Integer.parseInt(strings[1]);
                    for (int j = h; j <= t; j++) {
                        builder.append(String.valueOf(j) + " ");
                    }
                } else if (weekInfo.contains("第")) {
                    weekInfo = weekInfo.substring(1, weekInfo.indexOf("周"));
                    builder.append(weekInfo + " ");
                } else {
                    weekInfo = weekInfo.substring(0, weekInfo.indexOf("周"));
                    builder.append(weekInfo);
                }
                info[0] = builder.toString();
            } else {
                info[0] = null;
                info[1] = null;
                info[2] = null;
                remap.put(i, info);
                return remap;
            }
            //info[1]
            if (!sectionInfo.equals("选择上课节数")) {
                StringBuilder builder = new StringBuilder();
                builder.append(String.valueOf(ConvertUtils.ZHToint(sectionInfo.charAt(1))) + " ");
                sectionInfo = sectionInfo.substring(sectionInfo.indexOf("第") + 1, sectionInfo.indexOf("节"));
                String[] strings = sectionInfo.split("[-]");
                builder.append(strings[0] + " " + strings[1] + " ");
                info[1] = builder.toString();
            } else {
                info[1] = null;
                remap.put(i, info);
                return remap;
            }
            info[2] = positionInfo;
            remap.put(i, info);
        }
        return remap;
    }
}
