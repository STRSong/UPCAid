package jwxt.cacher.cc.jwxt;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jwxt.cacher.cc.jwxt.info.Course;
import jwxt.cacher.cc.jwxt.picker.MyOptionPicker;
import jwxt.cacher.cc.jwxt.picker.OptionPicker;
import jwxt.cacher.cc.jwxt.util.ObjectSaveUtils;
import jwxt.cacher.cc.jwxt.util.ScreenUtils;
import jwxt.cacher.cc.jwxt.views.CourseAdapter;
import jwxt.cacher.cc.jwxt.views.RotateTransformer;
import jwxt.cacher.cc.jwxt.views.WeekGridViewAdapter;


/**
 * Created by xhaiben on 2016/8/20.
 */
public class CourseActivity extends AppCompatActivity {
    private RelativeLayout courseRelative;
    private Context context;
    private TextView textViewMonth;
    private TextView textViewSun;
    private TextView textViewMon;
    private TextView textViewTue;
    private TextView textViewWed;
    private TextView textViewThu;
    private TextView textViewFri;
    private TextView textViewSat;
    private TextView textViewSun1;
    private TextView textViewMon1;
    private TextView textViewTue1;
    private TextView textViewWed1;
    private TextView textViewThu1;
    private TextView textViewFri1;
    private TextView textViewSat1;
    private TextView textViewWeekChoice;
    private TextView textViewWeek;
    private LinearLayout linearLayoutSun;
    private LinearLayout linearLayoutMon;
    private LinearLayout linearLayoutTue;
    private LinearLayout linearLayoutWed;
    private LinearLayout linearLayoutThu;
    private LinearLayout linearLayoutFri;
    private LinearLayout linearLayoutSat;

    private Handler courseHandler;
    private List<Course> courseList;
    private SZSDConnection szsdConnection;
    private Toolbar toolbar;
    int firstColHeight;
    int firstColWidth;
    int courseColWidth;
    private PopupWindow weekChoicePopup;
    int[] backDrawable;
    List<TextView> currentCourses;
    private Map<String, Integer> back;

    private SharedPreferences sharedPreferences;
    private int currentWeek;
    private int lastCurrentWeek;
    private int currentWeekOfYear;
    private int timeWeek;
    private int currentShowWeek;
    private ListView listViewWeek;
    private WeekChoiceAdapter adapter;
    private Calendar calendar;
    private Activity activity;
    private ProgressDialog progressDialog;
    private Thread threadFlushCourse;
    private ViewPager viewPager;
    private List<TextView> wkTextViewList;
    private PopupWindow courseInfoPopup;
    private ScrollView courseScroll;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_course);
        context = this;
        activity = this;
        toolbar = (Toolbar) findViewById(R.id.toolbar_course);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(getNavigationOnClickListener());
        toolbar.setOnMenuItemClickListener(getMenuItemClickListener());
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        wkTextViewList = new ArrayList<>();

        textViewMonth = (TextView) findViewById(R.id.course_month);
        textViewSun = (TextView) findViewById(R.id.course_sun);
        textViewMon = (TextView) findViewById(R.id.course_mon);
        textViewTue = (TextView) findViewById(R.id.course_tue);
        textViewWed = (TextView) findViewById(R.id.course_wed);
        textViewThu = (TextView) findViewById(R.id.course_thu);
        textViewFri = (TextView) findViewById(R.id.course_fri);
        textViewSat = (TextView) findViewById(R.id.course_sat);

        textViewSun1 = (TextView) findViewById(R.id.course_sun1);
        textViewMon1 = (TextView) findViewById(R.id.course_mon1);
        textViewTue1 = (TextView) findViewById(R.id.course_tue1);
        textViewWed1 = (TextView) findViewById(R.id.course_wed1);
        textViewThu1 = (TextView) findViewById(R.id.course_thu1);
        textViewFri1 = (TextView) findViewById(R.id.course_fri1);
        textViewSat1 = (TextView) findViewById(R.id.course_sat1);

        textViewWeekChoice = (TextView) findViewById(R.id.course_choice);
        Typeface iconfont = Typeface.createFromAsset(getAssets(), "iconfont/iconfont.ttf");
        textViewWeekChoice.setTypeface(iconfont);
        currentCourses = new ArrayList<>();
        textViewWeek = (TextView) findViewById(R.id.course_week);
        sharedPreferences = this.getSharedPreferences("courseInfo", Context.MODE_PRIVATE);
        /*计算时间*/
        calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.SUNDAY);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int week = calendar.get(Calendar.DAY_OF_WEEK);
        calendar.add(Calendar.DAY_OF_WEEK, -week + 1);
        textViewSun.setText(String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)));
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        textViewMon.setText(String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)));
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        textViewTue.setText(String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)));
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        textViewWed.setText(String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)));
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        textViewThu.setText(String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)));
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        textViewFri.setText(String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)));
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        textViewSat.setText(String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)));

        switch(week){
            case 1:
                textViewSun.setTextColor(getResources().getColor(R.color.orange_dark));
                textViewSun1.setTextColor(getResources().getColor(R.color.orange_dark));
                break;
            case 2:
                textViewMon.setTextColor(getResources().getColor(R.color.orange_dark));
                textViewMon1.setTextColor(getResources().getColor(R.color.orange_dark));
                break;
            case 3:
                textViewTue.setTextColor(getResources().getColor(R.color.orange_dark));
                textViewTue1.setTextColor(getResources().getColor(R.color.orange_dark));
                break;
            case 4:
                textViewWed.setTextColor(getResources().getColor(R.color.orange_dark));
                textViewWed1.setTextColor(getResources().getColor(R.color.orange_dark));
                break;
            case 5:
                textViewThu.setTextColor(getResources().getColor(R.color.orange_dark));
                textViewThu1.setTextColor(getResources().getColor(R.color.orange_dark));
                break;
            case 6:
                textViewFri.setTextColor(getResources().getColor(R.color.orange_dark));
                textViewFri1.setTextColor(getResources().getColor(R.color.orange_dark));
                break;
            case 7:
                textViewSat.setTextColor(getResources().getColor(R.color.orange_dark));
                textViewSat1.setTextColor(getResources().getColor(R.color.orange_dark));
                break;
            default:
                break;
        }

        //星期天是1，星期六是7
        sharedPreferences.edit().putInt("currentDayOfWeek", week).commit();

        textViewMonth.setText(String.valueOf(month) + "月");
        currentWeekOfYear = sharedPreferences.getInt("currentWeekOfYear", 0);
        if (currentWeekOfYear == 0) {
            currentWeekOfYear = calendar.get(Calendar.WEEK_OF_YEAR);
            sharedPreferences.edit().putInt("currentWeekOfYear", calendar.get(Calendar.WEEK_OF_YEAR)).commit();
        }
        /***********************/

        /*设置当前周*/
        currentWeek = sharedPreferences.getInt("currentWeek", 0);
        if (currentWeek == 0) {
            ArrayList<String> data = new ArrayList<>();
            for (int i = 1; i <= 25; i++) {
                data.add(String.valueOf(i));
            }
            MyOptionPicker picker = new MyOptionPicker(this, data);
            picker.setOffset(2);
            picker.setGravity(Gravity.CENTER);
            picker.setOnOptionPickListener(new OptionPicker.OnOptionPickListener() {
                @Override
                public void onOptionPicked(int position, String option) {
                    currentWeek = Integer.parseInt(option);
                    currentShowWeek = currentWeek;
                    sharedPreferences.edit().putInt("currentWeek", currentWeek).commit();
                    textViewWeek.setText("第" + currentWeek + "周");
                    Message msg = courseHandler.obtainMessage();
                    msg.arg1 = currentWeek;
                    courseHandler.sendMessage(msg);
                }
            });
            picker.show();
        }

        if (calendar.get(Calendar.WEEK_OF_YEAR) > currentWeekOfYear) {
            currentWeek += 1;
            sharedPreferences.edit().putInt("currentWeek", currentWeek).commit();
            sharedPreferences.edit().putInt("currentWeekOfYear", calendar.get(Calendar.WEEK_OF_YEAR)).commit();
        }
        lastCurrentWeek = sharedPreferences.getInt("lastCurrentWeek", 0);
        if (lastCurrentWeek == 0) {
            lastCurrentWeek = 1;
        }
        textViewWeek.setText("第" + currentWeek + "周");
        /**************/


        szsdConnection = SZSDConnection.getInstance();
        courseList = (ArrayList<Course>) getIntent().getSerializableExtra("course");
        backDrawable = new int[18];
        //正常 0,2,4,6,8 多个1,3,5,7,9
        backDrawable[0] = R.drawable.ic_course_bg_lv;
        backDrawable[1] = R.drawable.ic_course_bg_lv_multi;
        backDrawable[2] = R.drawable.ic_course_bg_cheng;
        backDrawable[3] = R.drawable.ic_course_bg_cheng_multi;
        backDrawable[4] = R.drawable.ic_course_bg_lan;
        backDrawable[5] = R.drawable.ic_course_bg_lan_multi;
        backDrawable[6] = R.drawable.ic_course_bg_qing;
        backDrawable[7] = R.drawable.ic_course_bg_qing_multi;
        backDrawable[8] = R.drawable.ic_course_bg_fen;
        backDrawable[9] = R.drawable.ic_course_bg_fen_multi;
        backDrawable[10] = R.drawable.ic_course_bg_huang;
        backDrawable[11] = R.drawable.ic_course_bg_huang_multi;
        backDrawable[12] = R.drawable.ic_course_bg_cyan;
        backDrawable[13] = R.drawable.ic_course_bg_cyan_multi;
        backDrawable[14] = R.drawable.ic_course_bg_bohelv;
        backDrawable[15] = R.drawable.ic_course_bg_bohelv_multi;
        backDrawable[16] = R.drawable.ic_course_bg_molan;
        backDrawable[17] = R.drawable.ic_course_bg_molan_multi;
        /*绘制课程格子*/
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        firstColHeight = dm.heightPixels / 12;
        firstColWidth = dm.widthPixels / 30 * 2;
        courseColWidth = dm.widthPixels / 30 * 4;
        courseRelative = (RelativeLayout) findViewById(R.id.course_relative);
        courseScroll = (ScrollView) findViewById(R.id.course_info_scroll);
        courseScroll.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    for (TextView tv : wkTextViewList) {
                        if (tv.getText().equals("+")) {
                            tv.setText(" ");
                        }
                    }
                }
                return false;
            }
        });
        for (int i = 1; i <= 12; i++) {
            for (int j = 1; j <= 8; j++) {
                if (j == 1) {
                    //第一列
                    RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(firstColWidth, firstColHeight);
                    TextView textView = new TextView(context);
                    textView.setTextSize(18);
                    textView.setTextColor(ContextCompat.getColor(context, R.color.courseWeek));
                    textView.setBackgroundResource(R.drawable.course_first_textview);
                    @android.support.annotation.IdRes int id = i;
                    textView.setId(id);
                    textView.setText(String.valueOf(i));
                    textView.setGravity(Gravity.CENTER);
                    if (i == 1) {
                        //lp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                    } else {
                        lp.addRule(RelativeLayout.BELOW, i - 1);
                    }
                    courseRelative.addView(textView, lp);
                } else {
                    //课程信息
                    RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(courseColWidth, firstColHeight);
                    final TextView textView = new TextView(context);
//                    textView.setBackgroundResource(R.drawable.course_first_textview);
                    textView.setTextSize(23);
                    textView.setGravity(Gravity.CENTER);
                    textView.setText(" ");
                    textView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            TextView t = (TextView) v;
                            if (t.getText().equals("+")) {
//                                Toast.makeText(context, "两次点击", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(CourseActivity.this, CreateCourseActivity.class);
                                intent.putExtra("course", (ArrayList<Course>) courseList);
                                startActivity(intent);
                                t.setText(" ");
                            } else {
                                for (TextView tv : wkTextViewList) {
                                    if (tv.getText().equals("+")) {
                                        tv.setText(" ");
                                    }
                                }
                                t.setText("+");
                            }
                        }
                    });

                    @android.support.annotation.IdRes int id = (i + 1) * 12 + j - 2;
                    textView.setId(id);
                    //textView.setText(String.valueOf(id));
                    if (j == 2) {
                        lp.addRule(RelativeLayout.RIGHT_OF, j - 1);
                    } else {
                        lp.addRule(RelativeLayout.RIGHT_OF, (i + 1) * 12 + j - 2 - 1);
                    }
                    if (i > 1) {
                        lp.addRule(RelativeLayout.BELOW, (i) * 12 + j - 2);
                    }
                    courseRelative.addView(textView, lp);
                    wkTextViewList.add(textView);
                }
            }
        }
        /******************/
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("正在加载，请稍后...");
        progressDialog.setCancelable(false);
        progressDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                        if (threadFlushCourse != null && !threadFlushCourse.isInterrupted()) {
                            threadFlushCourse.interrupt();
                        }
                    }
                    return true;
                }
                return false;
            }
        });

        initHandler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message msg = courseHandler.obtainMessage();
                msg.arg1 = currentWeek;
                courseHandler.sendMessage(msg);
            }
        }).start();

        initPopup();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.course_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    protected void onResume() {
        ObjectSaveUtils objectSaveUtils = new ObjectSaveUtils(context, "courseInfo");
        courseList = objectSaveUtils.getObject("courseList");
        Message msg = courseHandler.obtainMessage();
        if (currentShowWeek == 0) {
            currentShowWeek = currentWeek;
        }
        msg.arg1 = currentShowWeek;
        courseHandler.sendMessage(msg);
        super.onResume();
    }

    private Toolbar.OnMenuItemClickListener getMenuItemClickListener() {
        return new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.update_course:
                        progressDialog.show();
//                        Toast.makeText(context,"AA",Toast.LENGTH_SHORT).show();
                        threadFlushCourse = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                ObjectSaveUtils objectSaveUtils = new ObjectSaveUtils(context, "courseInfo");
                                courseList = szsdConnection.getCourseInfo("2016-2017-1", "");
                                objectSaveUtils.setObject("courseList", courseList);
                                Message msg = courseHandler.obtainMessage();
                                msg.arg1 = currentWeek;
                                courseHandler.sendMessage(msg);
                            }
                        });
                        threadFlushCourse.start();
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
                CourseActivity.this.finish();
            }
        };
    }

    private void initHandler() {
        courseHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
//                List<Course> courses = (List<Course>) msg.obj;
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                if (currentCourses.size() != 0) {
                    for (int i = 0; i < currentCourses.size(); i++) {
                        courseRelative.removeView(currentCourses.get(i));
                    }
                    currentCourses.clear();
                }
                int week = msg.arg1;
                List<Course> thisWeekCourses = new ArrayList<>();
                final Map<String, List<Course>> multiCourse = new HashMap<>();
                back = new HashMap<>();
                //获取本周课程
                if (courseList != null) {
                    for (int i = 0, b = 0; i < courseList.size(); i++) {
                        Course course = courseList.get(i);
                        if (course.isThisWeek(week)) {
                            thisWeekCourses.add(course);
                            course.setMulti(false);
                            if (!back.containsKey(course.getCourseName())) {
                                back.put(course.getCourseName(), b * 2);
                                b++;
                                if (b > 8) {
                                    b = 0;
                                }
                            }
                        }
                    }
                }
                for (int i = 0; i < thisWeekCourses.size() - 1; i++) {
                    List<Course> sameTimeList = new ArrayList<>();
                    for (int j = i + 1; j < thisWeekCourses.size(); j++) {
                        //如果是同一天
                        if (thisWeekCourses.get(i).getDay() == thisWeekCourses.get(j).getDay()) {
                            //如果是同一时间
                            if (thisWeekCourses.get(i).getBeginLesson() == thisWeekCourses.get(j).getBeginLesson()) {
                                thisWeekCourses.get(i).setMulti(true);

                                if (!sameTimeList.contains(thisWeekCourses.get(i))) {
                                    sameTimeList.add(thisWeekCourses.get(i));
                                }
                                sameTimeList.add(thisWeekCourses.get(j));
                                thisWeekCourses.remove(j);
                                j--;
                            }
                        }
                    }
                    if (sameTimeList.size() > 0) {
                        multiCourse.put(thisWeekCourses.get(i).getCourseName(), sameTimeList);
                    }
                }
//                for (int i = 0; i < multiCourse.size(); i++) {
//                    System.out.println(multiCourse.get(i));
//                }
                for (int i = 0; i < thisWeekCourses.size(); i++) {
                    final Course course = thisWeekCourses.get(i);
                    TextView courseText = new TextView(context);
                    RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(courseColWidth - 6, firstColHeight * 2 - 8);
                    courseText.setGravity(Gravity.CENTER_HORIZONTAL);
                    courseText.setTextColor(Color.WHITE);

                    int lessonNum = course.getBeginLesson();
                    int day = course.getDay();
                    if (day == 0) {
                        lp.addRule(RelativeLayout.RIGHT_OF, 1);
                    } else {
                        lp.addRule(RelativeLayout.RIGHT_OF, 24 + day - 1);
                    }
                    if (lessonNum != 1) {
                        lp.addRule(RelativeLayout.BELOW, 12 * lessonNum);
                    }
                    lp.rightMargin = 3;
                    lp.topMargin = 4;
                    lp.bottomMargin = 4;
                    lp.leftMargin = 3;
                    courseText.setText(course.getCourseName() + "@" + course.getClassRoom());

                    currentCourses.add(courseText);
                    courseRelative.addView(courseText, lp);

                    if (course.isMulti() == true) {
                        //如果有重课
                        courseText.setBackgroundResource(backDrawable[back.get(course.getCourseName()) + 1]);
                        courseText.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //Toast.makeText(context,"AAA",Toast.LENGTH_SHORT).show();
                                showMultiCourseDialog(multiCourse, course.getCourseName());
                                WindowManager.LayoutParams lp = getWindow().getAttributes();
                                lp.alpha = 0.5f;
                                getWindow().setAttributes(lp);
                            }
                        });
                    } else {
                        //无重课
                        courseText.setBackgroundResource(backDrawable[back.get(course.getCourseName())]);
                        courseText.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //Toast.makeText(context,"AAA",Toast.LENGTH_SHORT).show();
                                showCourseInfoDialog(course);
                                if (courseInfoPopup != null && !courseInfoPopup.isShowing()) {
                                    courseInfoPopup.showAtLocation(courseInfoPopup.getContentView(), Gravity.CENTER, 0, 0);
                                    WindowManager.LayoutParams lp = getWindow().getAttributes();
                                    lp.alpha = 0.5f;
                                    getWindow().setAttributes(lp);
                                }

                            }
                        });
                    }

                }
            }

        };

    }

    public void on_ChoiceWeek_Click(View view) {
        if (weekChoicePopup != null) {
            int moveX = ScreenUtils.widthPixels(context) / 2 - dip2px(context, 200.0f) / 2;
            weekChoicePopup.showAsDropDown(toolbar, moveX, 0);
            if (listViewWeek != null) {
                /*设置list position*/
                String week = textViewWeek.getText().toString();
                week = week.substring(1, week.indexOf("周"));
                int position = currentWeek;
                try {
                    position = Integer.parseInt(week);
                    timeWeek = Integer.parseInt(week);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                listViewWeek.setSelection(position - 3);
                /*添加当前周String*/
                int cWeek = sharedPreferences.getInt("currentWeek", 0);
                int lWeek = sharedPreferences.getInt("lastCurrentWeek", 0);
                if (cWeek != 0) {
                    adapter.weekList.set(cWeek - 1, "第" + cWeek + "周" + "(本周)");
                }
                if (lWeek != 0) {
                    adapter.weekList.set(lWeek - 1, "第" + lWeek + "周");
                }
            }
        }

    }

    private void initPopup() {
        final View weekChoice = LayoutInflater.from(context).inflate(R.layout.week_choice_popupwindow, null);
        listViewWeek = (ListView) weekChoice.findViewById(R.id.listView_week);
        adapter = new WeekChoiceAdapter(weekChoice.getContext());
        listViewWeek.setAdapter(adapter);
        listViewWeek.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                Message msg = courseHandler.obtainMessage();
                currentShowWeek = position + 1;
                msg.arg1 = currentShowWeek;
                courseHandler.sendMessage(msg);
                int deltaWeek = position + 1 - timeWeek;
                calendar.add(Calendar.WEEK_OF_YEAR, deltaWeek);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                //System.out.println(simpleDateFormat.format(calendar.getTime()));
                int month = calendar.get(Calendar.MONTH) + 1;
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int week = calendar.get(Calendar.DAY_OF_WEEK);
                calendar.add(Calendar.DAY_OF_WEEK, -week + 1);
                textViewSun.setText(String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)));
                calendar.add(Calendar.DAY_OF_MONTH, 1);
                textViewMon.setText(String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)));
                calendar.add(Calendar.DAY_OF_MONTH, 1);
                textViewTue.setText(String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)));
                calendar.add(Calendar.DAY_OF_MONTH, 1);
                textViewWed.setText(String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)));
                calendar.add(Calendar.DAY_OF_MONTH, 1);
                textViewThu.setText(String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)));
                calendar.add(Calendar.DAY_OF_MONTH, 1);
                textViewFri.setText(String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)));
                calendar.add(Calendar.DAY_OF_MONTH, 1);
                textViewSat.setText(String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)));

                textViewMonth.setText(String.valueOf(month) + "月");

                LinearLayout linearLayout = (LinearLayout) view;
                TextView textView = (TextView) linearLayout.getChildAt(0);
                String contentWeek = textView.getText().toString();
                if (contentWeek.contains("(")) {
                    contentWeek = contentWeek.substring(0, contentWeek.indexOf("(本"));

                }
                textViewWeek.setText(contentWeek);
                weekChoicePopup.dismiss();
            }
        });

        Button weekSetBtn = (Button) weekChoice.findViewById(R.id.btn_weekSet);
        weekSetBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    v.setBackgroundResource(R.drawable.ic_week_set_button_press);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    v.setBackgroundResource(R.drawable.ic_week_set_button);
                }
                return false;
            }
        });
        weekSetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (weekChoicePopup != null) {
                    weekChoicePopup.dismiss();
                }
                ArrayList<String> data = new ArrayList<>();
                for (int i = 1; i <= 25; i++) {
                    data.add(String.valueOf(i));
                }
                MyOptionPicker picker = new MyOptionPicker(activity, data);
                picker.setOffset(2);
                picker.setCancelable(true);
                picker.setCanceledOnTouchOutside(true);
                picker.setGravity(Gravity.CENTER);
                picker.setOnOptionPickListener(new OptionPicker.OnOptionPickListener() {
                    @Override
                    public void onOptionPicked(int position, String option) {
                        currentWeek = Integer.parseInt(option);
                        textViewWeek.setText("第" + currentWeek + "周");
                        Message msg = courseHandler.obtainMessage();
                        msg.arg1 = currentWeek;
                        courseHandler.sendMessage(msg);

                        int cWeek = currentWeek;
                        int lWeek = sharedPreferences.getInt("currentWeek", 0);
                        if (lWeek != cWeek) {
                            sharedPreferences.edit().putInt("currentWeek", cWeek).commit();
                            sharedPreferences.edit().putInt("lastCurrentWeek", lWeek).commit();
                        }
                        calendar = Calendar.getInstance();
                        calendar.setFirstDayOfWeek(Calendar.SUNDAY);
                        int month = calendar.get(Calendar.MONTH) + 1;
                        int day = calendar.get(Calendar.DAY_OF_MONTH);
                        int week = calendar.get(Calendar.DAY_OF_WEEK);
                        calendar.add(Calendar.DAY_OF_WEEK, -week + 1);
                        textViewSun.setText(String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)));
                        calendar.add(Calendar.DAY_OF_MONTH, 1);
                        textViewMon.setText(String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)));
                        calendar.add(Calendar.DAY_OF_MONTH, 1);
                        textViewTue.setText(String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)));
                        calendar.add(Calendar.DAY_OF_MONTH, 1);
                        textViewWed.setText(String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)));
                        calendar.add(Calendar.DAY_OF_MONTH, 1);
                        textViewThu.setText(String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)));
                        calendar.add(Calendar.DAY_OF_MONTH, 1);
                        textViewFri.setText(String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)));
                        calendar.add(Calendar.DAY_OF_MONTH, 1);
                        textViewSat.setText(String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)));

                        textViewMonth.setText(String.valueOf(month) + "月");
                    }
                });
                picker.show();
            }
        });
        weekChoicePopup = new PopupWindow(weekChoice);
        weekChoicePopup.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        weekChoicePopup.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        weekChoicePopup.setFocusable(true);

        weekChoicePopup.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.ic_dropdown_week_bg));

    }

    private void showMultiCourseDialog(Map<String, List<Course>> multiCourse, String courseName) {
        final PopupWindow popupWindow = new PopupWindow(context);
        final View dialogView = View.inflate(context, R.layout.more_course_dialog, null);
        List<View> viewList = new ArrayList<>();
        List<Course> courseList = multiCourse.get(courseName);
        Iterator iterator = courseList.iterator();
        while (iterator.hasNext()) {
            View view = View.inflate(context, R.layout.multi_course_textview, null);
            TextView courseText = (TextView) view.findViewById(R.id.course_textView);

            final Course course = (Course) iterator.next();
            courseText.setText(course.getCourseName() + "@" + course.getClassRoom());
            courseText.setBackgroundResource(backDrawable[back.get(course.getCourseName())]);
            courseText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Toast.makeText(context,"AAA",Toast.LENGTH_SHORT).show();
                    showCourseInfoDialog(course);
                    if (popupWindow != null && popupWindow.isShowing()) {
                        popupWindow.dismiss();
                    }
                    if (courseInfoPopup != null && !courseInfoPopup.isShowing()) {
                        courseInfoPopup.showAtLocation(courseInfoPopup.getContentView(), Gravity.CENTER, 0, 0);
                        WindowManager.LayoutParams lp = getWindow().getAttributes();
                        lp.alpha = 0.5f;
                        getWindow().setAttributes(lp);
                    }
                }
            });
            viewList.add(view);
        }

        viewPager = (ViewPager) dialogView.findViewById(R.id.viewpager);
        viewPager.setPageTransformer(true, new RotateTransformer());
        viewPager.setPageMargin(dip2px(this, -30));
        dialogView.findViewById(R.id.page_container).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return viewPager.dispatchTouchEvent(event);
            }
        });
        CourseAdapter adapter = new CourseAdapter(context);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(viewList.size());
        adapter.addAll(viewList);

        popupWindow.setContentView(dialogView);
        popupWindow.setFocusable(true);
        popupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setBackgroundDrawable(new ColorDrawable());
        popupWindow.setAnimationStyle(R.style.PopupAnimationCourseInfo);

        popupWindow.showAtLocation(popupWindow.getContentView(), Gravity.CENTER, 0, 0);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1f;
                getWindow().setAttributes(lp);

            }
        });
    }

    private int dip2px(Context context, float dipValue) {
        float m = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * m + 0.5f);
    }

    private void showCourseInfoDialog(final Course course) {
        View popupView = View.inflate(context, R.layout.course_info_popupwindow, null);
        TextView textViewCourseName = (TextView) popupView.findViewById(R.id.course_info_courseName);
        TextView textViewClassRoom = (TextView) popupView.findViewById(R.id.course_info_classRoom);
        TextView textViewTeacherName = (TextView) popupView.findViewById(R.id.course_info_teacherName);
        GridView gridView = (GridView) popupView.findViewById(R.id.gridView_week_show);
        textViewCourseName.setText(course.getCourseName());
        textViewClassRoom.setText(course.getClassRoom());
        textViewTeacherName.setText(course.getTeacherName());
        WeekGridViewAdapter adapter = new WeekGridViewAdapter(context);
        Set<Integer> expected = course.getExpected();
        Iterator iterator = expected.iterator();
        while (iterator.hasNext()) {
            int n = (int) iterator.next();
            adapter.setTextViewSelected(n - 1, true);
            //System.out.println(n);
        }
        //System.out.println(expected);
        gridView.setAdapter(adapter);
        /*修复少1像素Bug*/
        gridView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        ViewGroup.LayoutParams params = gridView.getLayoutParams();
        params.height = gridView.getMeasuredHeight() * 5 + 1;
        gridView.setLayoutParams(params);

        Button btnDelete = (Button) popupView.findViewById(R.id.course_info_btn_delete);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("提示");
                builder.setMessage("确定要删除该课程？");
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        courseList.remove(course);
                        ObjectSaveUtils objectSaveUtils = new ObjectSaveUtils(context, "courseInfo");
                        objectSaveUtils.setObject("courseList", courseList);
                        courseInfoPopup.dismiss();
                        Message msg = courseHandler.obtainMessage();
                        msg.arg1 = currentShowWeek;
                        courseHandler.sendMessage(msg);
                    }
                });
                builder.create().show();
            }
        });

        Button buttonEdit = (Button) popupView.findViewById(R.id.course_info_btn_edit);
        buttonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CourseActivity.this, EditCourseActivity.class);
                intent.putExtra("course", (ArrayList<Course>) courseList);
                intent.putExtra("courseIndex", courseList.indexOf(course));
                startActivity(intent);
                courseInfoPopup.dismiss();
            }
        });

        courseInfoPopup = new PopupWindow(context);
        courseInfoPopup.setContentView(popupView);
        courseInfoPopup.setAnimationStyle(R.style.PopupAnimationClassRoomSearch);
        courseInfoPopup.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        courseInfoPopup.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        courseInfoPopup.setFocusable(true);

        courseInfoPopup.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.classroom_search_white));
//        courseInfoPopup.setBackgroundDrawable(new ColorDrawable());

        courseInfoPopup.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1f;
                getWindow().setAttributes(lp);
            }
        });
    }
}
