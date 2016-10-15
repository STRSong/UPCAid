package jwxt.cacher.cc.jwxt;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.internal.view.SupportSubMenu;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jwxt.cacher.cc.jwxt.util.ObjectSaveUtils;
import jwxt.cacher.cc.jwxt.views.MyNiceSpinner;
import jwxt.cacher.cc.jwxt.views.NiceSpinner;

/**
 * Created by xhaiben on 2016/9/20.
 */

public class ClassRoomActivity extends AppCompatActivity {
    private TextView textViewNJ;
    private TextView textViewNT;
    private TextView textViewXH;
    private TextView textViewXL;
    private TextView textViewDH;
    private TextView textViewDL;
    private TextView textViewTitle;
    private Toolbar toolbar;
    private MyNiceSpinner spinnerWeek;
    private MyNiceSpinner spinnerDay;
    private MyNiceSpinner spinnerN;
    private SZSDConnection szsdConnection;
    private HashMap<String, String> classRoomMap;
    private Context context;
    private ProgressDialog progressDialog;
    private View dialogView;
    private Calendar calendar;

    private PopupWindow popupWindow;

    private Handler handlerSearch;
    private SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classroom);
        context = this;

        sharedPreferences = this.getSharedPreferences("courseInfo", Context.MODE_PRIVATE);

        toolbar = (Toolbar) findViewById(R.id.toolbar_classroom);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(getNavigationOnClickListener());
        toolbar.setOnMenuItemClickListener(getMenuItemClickListener());
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        progressDialog=new ProgressDialog(context);
        progressDialog.setMessage("正在加载，请稍后...");
        progressDialog.setCancelable(false);

        textViewNJ=(TextView)findViewById(R.id.tv_classroom_nj);
        textViewNT=(TextView)findViewById(R.id.tv_classroom_nt);
        textViewXH=(TextView)findViewById(R.id.tv_classroom_xh);
        textViewXL=(TextView)findViewById(R.id.tv_classroom_xl);
        textViewDH=(TextView)findViewById(R.id.tv_classroom_dh);
        textViewDL=(TextView)findViewById(R.id.tv_classroom_dl);
        textViewTitle=(TextView)findViewById(R.id.tv_classroom_title);
        szsdConnection=(SZSDConnection)getIntent().getSerializableExtra("connection");
        classRoomMap=(HashMap<String,String>)getIntent().getSerializableExtra("classRoomMap");
        setClassRoom(classRoomMap);
        initHandler();
        initPopupwindow();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.classroom_menu,menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        return super.onPrepareOptionsMenu(menu);
    }

    private View.OnClickListener getNavigationOnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClassRoomActivity.this.finish();
            }
        };
    }

    private Toolbar.OnMenuItemClickListener getMenuItemClickListener(){
        return new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.classroom_search:
                        //Toast.makeText(context,"AA",Toast.LENGTH_SHORT).show();
                        if(popupWindow!=null&&!popupWindow.isShowing()){
                            popupWindow.showAtLocation(dialogView, Gravity.CENTER, 0, 0);
                            WindowManager.LayoutParams lp=getWindow().getAttributes();
                            lp.alpha=0.6f;
                            getWindow().setAttributes(lp);
                        }

                        break;
                }
                return false;
            }
        };
    }
    private void initPopupwindow(){
        dialogView = View.inflate(this, R.layout.classroom_search_popupwindow, null);
        dialogView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        spinnerWeek = (MyNiceSpinner) dialogView.findViewById(R.id.niceSpinner_week);
        MyNiceSpinner.mItemHeight=spinnerWeek.getMeasuredHeight();
        List<String> listWeekData = new ArrayList<>();
        for (int i = 1; i <= 18; i++) {
            listWeekData.add("第" + String.valueOf(i) + "周");
        }
        spinnerWeek.attachDataSource(listWeekData);
        spinnerDay = (MyNiceSpinner) dialogView.findViewById(R.id.niceSpinner_day);
        List<String> listDay = new ArrayList<>();
        listDay.add("星期日");
        listDay.add("星期一");
        listDay.add("星期二");
        listDay.add("星期三");
        listDay.add("星期四");
        listDay.add("星期五");
        listDay.add("星期六");
        spinnerDay.attachDataSource(listDay);
        spinnerN = (MyNiceSpinner) dialogView.findViewById(R.id.niceSpinner_n);
        List<String> listN = new ArrayList<>();
        listN.add("第一小节");
        listN.add("第二小节");
        listN.add("第三小节");
        listN.add("第四小节");
        listN.add("第五小节");
        listN.add("第六小节");
        listN.add("第七小节");
        listN.add("第八小节");
        listN.add("第九小节");
        listN.add("第十小节");
        listN.add("第十一小节");
        listN.add("第十二小节");
        spinnerN.attachDataSource(listN);
        MyNiceSpinner.centerView = toolbar;

        int currentWeek=sharedPreferences.getInt("currentWeek",0);
        if(currentWeek!=0){
            spinnerWeek.setSelectedIndex(currentWeek-1);
        }
        calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.SUNDAY);
        //星期天是1，星期六是7
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        if(day!=0){
            spinnerDay.setSelectedIndex(day-1);
        }
        int hour=calendar.get(Calendar.HOUR_OF_DAY);
        if(hour<=8||hour>22){
            spinnerN.setSelectedIndex(0);
        }else if(hour<=10){
            spinnerN.setSelectedIndex(1);
        }else if(hour<=12){
            spinnerN.setSelectedIndex(3);
        }else if(hour<=14){
            spinnerN.setSelectedIndex(4);
        }else if(hour<=16){
            spinnerN.setSelectedIndex(6);
        }else if(hour<=18){
            spinnerN.setSelectedIndex(7);
        }else if(hour<=20){
            spinnerN.setSelectedIndex(8);
        }else if(hour<=21){
            spinnerN.setSelectedIndex(9);
        }else if(hour<=22){
            spinnerN.setSelectedIndex(10);
        }

        Button button=(Button)dialogView.findViewById(R.id.btn_classroom_search);
        button.setOnTouchListener(new View.OnTouchListener() {
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
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(popupWindow.isShowing()){
                    popupWindow.dismiss();
                }
                progressDialog.show();
                final String week=String.valueOf(spinnerWeek.getSelectedIndex()+1);
                final String day;
                final String n=String.valueOf(spinnerN.getSelectedIndex()+1);
                if(spinnerDay.getSelectedIndex()==0){
                    day=String.valueOf(7);
                }else{
                    day=String.valueOf(spinnerDay.getSelectedIndex());
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Map<String,String> classRoomMap=szsdConnection.getClassRoom(week,day,n);
                        Message msg=handlerSearch.obtainMessage();
                        msg.obj=classRoomMap;
                        handlerSearch.sendMessage(msg);

                    }
                }).start();
                textViewTitle.setText("查询结果");
            }
        });

        Button buttonCurrent=(Button)dialogView.findViewById(R.id.btn_classroom_current);
        buttonCurrent.setOnTouchListener(new View.OnTouchListener() {
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
        buttonCurrent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(popupWindow.isShowing()){
                    popupWindow.dismiss();
                }
                //progressDialog.show();
                setClassRoom(classRoomMap);
                textViewTitle.setText("当前可用教室");
//                final String week=String.valueOf(spinnerWeek.getSelectedIndex()+1);
//                final String day;
//                final String n=String.valueOf(spinnerN.getSelectedIndex()+1);
//                if(spinnerDay.getSelectedIndex()==0){
//                    day=String.valueOf(7);
//                }else{
//                    day=String.valueOf(spinnerDay.getSelectedIndex());
//                }
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Map<String,String> classRoomMap=szsdConnection.getClassRoom(week,day,n);
//                        Message msg=handlerSearch.obtainMessage();
//                        msg.obj=classRoomMap;
//                        handlerSearch.sendMessage(msg);
//
//                    }
//                }).start();

            }
        });

        popupWindow = new PopupWindow(context);
        popupWindow.setContentView(dialogView);
        popupWindow.setAnimationStyle(R.style.PopupAnimationClassRoomSearch);
        popupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(ContextCompat.getDrawable(context,R.drawable.classroom_search_white));

        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp=getWindow().getAttributes();
                lp.alpha=1f;
                getWindow().setAttributes(lp);
            }
        });
        //popupWindow.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.drop_down_shadow));

    }
    private void initHandler(){
        handlerSearch=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                HashMap<String,String> classMap=(HashMap<String,String>)msg.obj;
                progressDialog.cancel();
                setClassRoom(classMap);
            }
        };
    }
    private void setClassRoom(HashMap<String,String> map){
        if(map!=null){
            textViewNJ.setText(map.get("NJ"));
            textViewNT.setText(map.get("NT"));
            textViewXH.setText(map.get("XH"));
            textViewXL.setText(map.get("XL"));
            textViewDH.setText(map.get("DH"));
            textViewDL.setText(map.get("DL"));
        }else{
            Toast.makeText(context,"故障啦o(╯□╰)o",Toast.LENGTH_LONG);
        }
    }
}
