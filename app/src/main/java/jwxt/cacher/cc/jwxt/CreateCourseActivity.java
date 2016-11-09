package jwxt.cacher.cc.jwxt;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.IntentCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ContentFrameLayout;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TooManyListenersException;

import jwxt.cacher.cc.jwxt.info.Course;
import jwxt.cacher.cc.jwxt.util.ObjectSaveUtils;
import jwxt.cacher.cc.jwxt.util.UIUtils;
import jwxt.cacher.cc.jwxt.views.CreateCourseTimeAdapter;

/**
 * Created by xhaiben on 2016/11/3.
 */

public class CreateCourseActivity extends AppCompatActivity {
    private ListView listView;
    private Context context;
    private CreateCourseTimeAdapter adapter;
    private Activity activity;
    private Toolbar toolbar;
    private TextView tvCourseName;
    private TextView tvTeacherName;

    private List<Course> courseList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_course);
        context = this;
        activity = this;

        toolbar = (Toolbar) findViewById(R.id.toolbar_create_course);
        setSupportActionBar(toolbar);
        toolbar.setOnMenuItemClickListener(getMenuItemClickListener());
        toolbar.setNavigationOnClickListener(getNavigationOnClickListener());
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        courseList = (ArrayList<Course>) getIntent().getSerializableExtra("course");

        tvCourseName = (TextView) findViewById(R.id.add_course_edt_courseName);
        tvTeacherName = (TextView) findViewById(R.id.add_course_edt_teacher);

        listView = (ListView) findViewById(R.id.create_course_listview);
        adapter = new CreateCourseTimeAdapter(context, activity, listView);
        listView.setAdapter(adapter);
        UIUtils.setListViewHeightBasedOnItems(listView);
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
                        String courseName = tvCourseName.getText().toString();
                        String teacherName = tvTeacherName.getText().toString();
                        if (teacherName.isEmpty()) {
                            teacherName = "未知";
                        }
                        boolean isError = false;
                        if (courseName.isEmpty()) {
                            showHintDialog("课程名不能为空");
                            isError = true;
                            return false;
                        }
                        Map<Integer, String[]> re = adapter.getCourseInfo();
                        for (int i = 0; i < re.size(); i++) {
                            String[] infos = re.get(i);
                            System.out.println(i);
                            if (infos[0] == null) {
                                showHintDialog("请选择上课周数");
                                isError = true;
                                return false;
                            }
                            if (infos[1] == null) {
                                showHintDialog("请选择上课节数");
                                isError = true;
                                return false;
                            }
                            for (String str : infos) {
                                System.out.println(str);
                            }
                        }
                        if (isError == false) {
                            for (int i = 0; i < re.size(); i++) {
                                String[] infos = re.get(i);
                                Course course = new Course();
                                course.setCourseName(courseName);
                                course.setTeacherName(teacherName);
                                String[] weeks = infos[0].split("[ ]");
                                Set<Integer> weekSet = new HashSet<>();
                                for (String str : weeks) {
                                    weekSet.add(Integer.parseInt(str));
                                }
                                course.setExpected(weekSet);
                                course.setBeginWeek(Integer.parseInt(weeks[0]));
                                course.setEndWeek(Integer.parseInt(weeks[weeks.length - 1]));
                                String[] sections = infos[1].split("[ ]");
                                course.setDay(Integer.parseInt(sections[0]));
                                course.setBeginLesson(Integer.parseInt(sections[1]));
                                course.setEndLesson(Integer.parseInt(sections[2]));
                                if (infos[2].isEmpty()) {
                                    course.setClassRoom("未知");
                                } else {
                                    course.setClassRoom(infos[2]);
                                }
                                courseList.add(course);
                                ObjectSaveUtils objectSaveUtils = new ObjectSaveUtils(context, "courseInfo");
                                objectSaveUtils.setObject("courseList", courseList);
                            }
                            CreateCourseActivity.this.finish();
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
                CreateCourseActivity.this.finish();
            }
        };
    }

    public void on_create_course_add_time_click(View view) {
        adapter.addOtherTime();
        adapter.notifyDataSetChanged();
        UIUtils.setListViewHeightBasedOnItems(listView);
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
}
