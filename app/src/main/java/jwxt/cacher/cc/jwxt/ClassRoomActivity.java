package jwxt.cacher.cc.jwxt;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
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
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

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
    private Toolbar toolbar;
    private MyNiceSpinner spinnerWeek;
    private MyNiceSpinner spinnerDay;
    private MyNiceSpinner spinnerN;
    private SZSDConnection szsdConnection;
    private HashMap<String, String> classRoomMap;
    private Context context;
    private View dialogView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classroom);
        context = this;

        toolbar = (Toolbar) findViewById(R.id.toolbar_classroom);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(getNavigationOnClickListener());
        //toolbar.setOnMenuItemClickListener(getMenuItemClickListener());
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }


        textViewNJ=(TextView)findViewById(R.id.tv_classroom_nj);
        textViewNT=(TextView)findViewById(R.id.tv_classroom_nt);
        textViewXH=(TextView)findViewById(R.id.tv_classroom_xh);
        textViewXL=(TextView)findViewById(R.id.tv_classroom_xl);
        textViewDH=(TextView)findViewById(R.id.tv_classroom_dh);
        textViewDL=(TextView)findViewById(R.id.tv_classroom_dl);
//
//        szsdConnection=(SZSDConnection)getIntent().getSerializableExtra("connection");
//        classRoomMap=(HashMap<String,String>)getIntent().getSerializableExtra("classRoomMap");
//
//        System.out.println(classRoomMap);
//        textViewNJ.setText(classRoomMap.get("NJ"));
//        textViewNT.setText(classRoomMap.get("NT"));
//        textViewXH.setText(classRoomMap.get("XH"));
//        textViewXL.setText(classRoomMap.get("XL"));
//        textViewDH.setText(classRoomMap.get("DH"));
//        textViewDL.setText(classRoomMap.get("DL"));


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

    }

    private View.OnClickListener getNavigationOnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClassRoomActivity.this.finish();
            }
        };
    }

    public void on_buttonSearchRoom_click(View view) {
        PopupWindow window = new PopupWindow(context);

        window.setContentView(dialogView);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        WelcomeActivity.screenHeight = dm.heightPixels;
        WelcomeActivity.screenWidth = dm.widthPixels;
        Rect frame=new Rect();
        getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        WelcomeActivity.statusBarHeight=frame.top;

        window.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        window.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        window.setFocusable(true);
        window.setBackgroundDrawable(ContextCompat.getDrawable(context,R.drawable.classroom_search_white));
        window.showAtLocation(view, Gravity.CENTER, 0, 0);
    }
}
