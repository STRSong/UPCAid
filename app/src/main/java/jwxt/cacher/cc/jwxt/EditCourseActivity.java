package jwxt.cacher.cc.jwxt;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.GridView;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jwxt.cacher.cc.jwxt.info.Course;
import jwxt.cacher.cc.jwxt.picker.MyPickerView;
import jwxt.cacher.cc.jwxt.util.ConvertUtils;
import jwxt.cacher.cc.jwxt.util.ObjectSaveUtils;
import jwxt.cacher.cc.jwxt.views.WeekGridViewAdapter;

/**
 * Created by xhaiben on 2016/11/8.
 */

public class EditCourseActivity extends AppCompatActivity {
    private List<Course> courseList;
    private int courseIndex;
    private Toolbar toolbar;
    private TextView textViewCourseName;
    private TextView textViewPosition;
    private TextView textViewTeacherName;
    private TextView textViewWeek;
    private TextView textViewSection;
    private Context context;
    private Activity activity;
    private PopupWindow sectionChoiceWindow;
    private PopupWindow weekChoiceWindow;

    private Course course;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_course);

        context = this;
        activity = this;

        courseList = (ArrayList<Course>) getIntent().getSerializableExtra("course");
        courseIndex = getIntent().getIntExtra("courseIndex", -1);
        toolbar = (Toolbar) findViewById(R.id.toolbar_edit_course);
        setSupportActionBar(toolbar);
        toolbar.setOnMenuItemClickListener(getMenuItemClickListener());
        toolbar.setNavigationOnClickListener(getNavigationOnClickListener());
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        textViewCourseName = (TextView) findViewById(R.id.add_course_edt_courseName);
        textViewPosition = (TextView) findViewById(R.id.add_course_edt_position);
        textViewTeacherName = (TextView) findViewById(R.id.add_course_edt_teacher);
        textViewWeek = (TextView) findViewById(R.id.edit_course_txv_week);
        textViewSection = (TextView) findViewById(R.id.edit_course_txv_section);
        course = courseList.get(courseIndex);
        textViewCourseName.setText(course.getCourseName());
        textViewPosition.setText(course.getClassRoom());
        textViewTeacherName.setText(course.getTeacherName());
        textViewSection.setText("周" + ConvertUtils.intToZH(course.getDay()) + " " + "第"
                + String.valueOf(course.getBeginLesson()) + "-"
                + String.valueOf(course.getEndLesson()) + "节");

        sectionChoiceWindow = initPopupWindowSection(textViewSection);
        weekChoiceWindow = initPopupWindowWeek(textViewWeek);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.create_course_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        return super.onPrepareOptionsMenu(menu);
    }

    private Toolbar.OnMenuItemClickListener getMenuItemClickListener() {
        return new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.create_course_menu_confirm:
                        String courseName = textViewCourseName.getText().toString();
                        String teacherName = textViewTeacherName.getText().toString();
                        String classRoom = textViewPosition.getText().toString();
                        String weekInfo = textViewWeek.getText().toString();
                        String sectionInfo = textViewSection.getText().toString();
                        boolean isError = false;
                        if (courseName.isEmpty()) {
                            showHintDialog("课程名不能为空");
                            isError = true;
                            return false;
                        }
                        if (isError == false) {
                            course.setCourseName(courseName);
                            if (classRoom.isEmpty()) {
                                classRoom = "未知";
                            }
                            if (teacherName.isEmpty()) {
                                teacherName = "未知";
                            }
                            course.setClassRoom(classRoom);
                            course.setTeacherName(teacherName);
                            //设置周
                            if (!weekInfo.equals("选择上课周数")) {
                                weekInfo = getWeekString(weekInfo);
                                String[] weeks = weekInfo.split("[ ]");
                                Set<Integer> weekSet = new HashSet<>();
                                for (String str : weeks) {
                                    weekSet.add(Integer.parseInt(str));
                                }
                                course.setExpected(weekSet);
                                course.setBeginWeek(Integer.parseInt(weeks[0]));
                                course.setEndWeek(Integer.parseInt(weeks[weeks.length - 1]));
                            }
                            //设置节
                            StringBuilder builder = new StringBuilder();
                            builder.append(String.valueOf(ConvertUtils.ZHToint(sectionInfo.charAt(1))) + " ");
                            sectionInfo = sectionInfo.substring(sectionInfo.indexOf("第") + 1, sectionInfo.indexOf("节"));
                            String[] strings = sectionInfo.split("[-]");
                            builder.append(strings[0] + " " + strings[1] + " ");
                            sectionInfo = builder.toString();

                            String[] sections = sectionInfo.split("[ ]");
                            course.setDay(Integer.parseInt(sections[0]));
                            course.setBeginLesson(Integer.parseInt(sections[1]));
                            course.setEndLesson(Integer.parseInt(sections[2]));
                            ObjectSaveUtils objectSaveUtils = new ObjectSaveUtils(context, "courseInfo");
                            objectSaveUtils.setObject("courseList", courseList);
                            EditCourseActivity.this.finish();
                        }
                        break;
                }
                return false;
            }
        };
    }

    private View.OnClickListener getNavigationOnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditCourseActivity.this.finish();
            }
        };
    }

    public void on_edit_section_click(View view) {
        final TextView textView = (TextView) view.findViewById(R.id.edit_course_txv_section);
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

    public void on_edit_week_click(View view) {
        final TextView textView = (TextView) view.findViewById(R.id.edit_course_txv_week);
        if (weekChoiceWindow == null) {
            initPopupWindowWeek(textView);
        }
        if (weekChoiceWindow != null && !weekChoiceWindow.isShowing()) {
            weekChoiceWindow.showAtLocation(weekChoiceWindow.getContentView(), Gravity.CENTER, 0, 0);
            WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
            lp.alpha = 0.5f;
            activity.getWindow().setAttributes(lp);
        }
    }

    private PopupWindow initPopupWindowWeek(final TextView weekTextView) {
        final PopupWindow
                popupWindowWeek = new PopupWindow(context);
        View popupView = View.inflate(context, R.layout.dlg_week_choose, null);
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
        Set<Integer> expected = course.getExpected();
        Iterator iterator = expected.iterator();
        while (iterator.hasNext()) {
            int n = (int) iterator.next();
            adapter.setTextViewSelected(n - 1, true);
            //System.out.println(n);
        }
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

        final MyPickerView pickerViewSection = (MyPickerView) popupView.findViewById(R.id.section_picker);
        List<String> strings1 = new ArrayList<>();
        for (int i = 1; i <= 11; i += 2) {
            strings1.add("第" + String.valueOf(i) + "-" + String.valueOf(i + 1) + "节");
        }
        pickerViewSection.setData(strings1);
        Button buttonCancel = (Button) popupView.findViewById(R.id.wheel_view_btn_cancel);
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (popupWindow.isShowing()) {
                    popupWindow.dismiss();
                }
            }
        });
        //设置节数初始值
        String sectionInfo = textViewSection.getText().toString();
        String[] info = sectionInfo.split("[ ]");
        pickerViewDay.setSelected(info[0]);
        pickerViewSection.setSelected(info[1]);

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

    private void showHintDialog(String hint) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("提示");
        builder.setMessage(hint);
        builder.setCancelable(true);
        builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.create().show();
    }

    private String getWeekString(String weekInfo) {
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
        return builder.toString();
    }
}
