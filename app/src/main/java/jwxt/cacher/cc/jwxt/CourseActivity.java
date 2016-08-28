package jwxt.cacher.cc.jwxt;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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
    private TextView textViewWeekChoice;
    private TextView textViewWeek;
    private Handler courseHandler;
    private List<Course> courseList;
    private JWXTConnection connection;
    private Toolbar toolbar;
    int firstColHeight;
    int firstColWidth;
    int courseColWidth;
    private PopupWindow weekChoicePopup;
    int[] backDrawable;
    List<TextView> currentCourses;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_course);
        context = this;

        toolbar=(Toolbar) findViewById(R.id.toolbar_course);
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        textViewMonth = (TextView) findViewById(R.id.course_month);
        textViewSun = (TextView) findViewById(R.id.course_sun);
        textViewMon = (TextView) findViewById(R.id.course_mon);
        textViewTue = (TextView) findViewById(R.id.course_tue);
        textViewWed = (TextView) findViewById(R.id.course_wed);
        textViewThu = (TextView) findViewById(R.id.course_thu);
        textViewFri = (TextView) findViewById(R.id.course_fri);
        textViewSat = (TextView) findViewById(R.id.course_sat);
        textViewWeek=(TextView)findViewById(R.id.course_week);
        textViewWeekChoice=(TextView)findViewById(R.id.course_choice);
        Typeface iconfont=Typeface.createFromAsset(getAssets(), "iconfont/iconfont.ttf");
        textViewWeekChoice.setTypeface(iconfont);
        currentCourses=new ArrayList<>();



        connection = (JWXTConnection) getIntent().getSerializableExtra("connection");

        backDrawable=new int[9];
        backDrawable[0]=R.drawable.course_info_blue;
        backDrawable[1]=R.drawable.course_info_green;
        backDrawable[2]=R.drawable.course_info_pink;
        backDrawable[3]=R.drawable.course_info_red;
        backDrawable[4]=R.drawable.course_info_yellow;
        backDrawable[5]=R.drawable.course_info_greedyellow;
        backDrawable[6]=R.drawable.course_info_qing;
        backDrawable[7]=R.drawable.course_info_purple;
        backDrawable[8]=R.drawable.course_info_brown;

        Calendar calendar = Calendar.getInstance();
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

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        firstColHeight = dm.heightPixels / 12;
        firstColWidth = dm.widthPixels / 30 * 2;

        courseColWidth = dm.widthPixels / 30 * 4;
        courseRelative = (RelativeLayout) findViewById(R.id.course_relative);
        System.out.println(firstColWidth);
        System.out.println(firstColHeight);
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
                    TextView textView = new TextView(context);
                    //textView.setBackgroundResource(R.drawable.course_first_textview);
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
                }
            }
        }
        initHandler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                courseList = connection.getCourseInfo("2016-2017-1","");
                Message msg=courseHandler.obtainMessage();
                msg.arg1=1;
                courseHandler.sendMessage(msg);
            }
        }).start();

        initPopup();

    }


    private void initHandler() {
        courseHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
//                List<Course> courses = (List<Course>) msg.obj;

                if(currentCourses.size()!=0){
                    for(int i=0;i<currentCourses.size();i++){
                        courseRelative.removeView(currentCourses.get(i));
                    }
                    currentCourses.clear();
                }
                int week = msg.arg1;
                List<Course> thisWeekCourses = new ArrayList<>();
                Map<String, Integer> back = new HashMap<>();
                //获取本周课程
                for (int i = 0,b=0; i < courseList.size(); i++) {
                    Course course = courseList.get(i);
                    if (course.isThisWeek(week)) {
                        thisWeekCourses.add(course);
                        if (!back.containsKey(course.getCourseName())) {
                            back.put(course.getCourseName(), b);
                            b++;
                            if (b > 8) {
                                b = 0;
                            }
                        }
                    }
                }
                for (int i = 0; i < thisWeekCourses.size(); i++) {
                    Course course = thisWeekCourses.get(i);

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
                    courseText.setBackgroundResource(backDrawable[back.get(course.getCourseName())]);
                    courseText.setText(course.getCourseName() + "@" + course.getClassRoom());
                    currentCourses.add(courseText);
                    courseRelative.addView(courseText, lp);
                }
            }

        };
    }
    public void on_ChoiceWeek_Click(View view){
        if(weekChoicePopup!=null){
            weekChoicePopup.showAsDropDown(toolbar,233,0);
        }

    }
    private void initPopup(){
        View weekChoice= LayoutInflater.from(context).inflate(R.layout.week_choice_popupwindow,null);
        final ListView listViewWeek=(ListView)weekChoice.findViewById(R.id.listView_week);
        WeekChoiceAdapter adapter=new WeekChoiceAdapter(weekChoice.getContext());
        listViewWeek.setAdapter(adapter);
        listViewWeek.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                Message msg=courseHandler.obtainMessage();
                msg.arg1=position+1;
                courseHandler.sendMessage(msg);
                LinearLayout linearLayout=(LinearLayout)view;
                TextView textView=(TextView) linearLayout.getChildAt(0);
                textViewWeek.setText(textView.getText());
                weekChoicePopup.dismiss();
                System.out.println(position);
            }
        });

        Button weekSetBtn=(Button)weekChoice.findViewById(R.id.btn_weekSet);
        weekSetBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction()==MotionEvent.ACTION_DOWN){
                    v.setBackgroundResource(R.drawable.ic_week_set_button_press);
                }else if(event.getAction()==MotionEvent.ACTION_UP){
                    v.setBackgroundResource(R.drawable.ic_week_set_button);
                }
                return false;
            }
        });
        weekChoicePopup=new PopupWindow(weekChoice);
        weekChoicePopup.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        weekChoicePopup.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        weekChoicePopup.setFocusable(true);

        weekChoicePopup.setBackgroundDrawable(ContextCompat.getDrawable(context,R.drawable.ic_dropdown_week_bg));

    }
}
