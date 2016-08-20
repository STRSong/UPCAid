package jwxt.cacher.cc.jwxt;

import android.content.Context;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;


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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);
        context=this;
        textViewMonth=(TextView)findViewById(R.id.course_month);
        textViewSun=(TextView)findViewById(R.id.course_sun);
        textViewMon=(TextView)findViewById(R.id.course_mon);
        textViewTue=(TextView)findViewById(R.id.course_tue);
        textViewWed=(TextView)findViewById(R.id.course_wed);
        textViewThu=(TextView)findViewById(R.id.course_thu);
        textViewFri=(TextView)findViewById(R.id.course_fri);
        textViewSat=(TextView)findViewById(R.id.course_sat);

        Calendar calendar=Calendar.getInstance();
        int month=calendar.get(Calendar.MONTH)+1;
        int day=calendar.get(Calendar.DAY_OF_MONTH);
        int week=calendar.get(Calendar.DAY_OF_WEEK);
        calendar.add(Calendar.DAY_OF_WEEK,-week+1);
        textViewSun.setText(String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)));
        calendar.add(Calendar.DAY_OF_MONTH,1);
        textViewMon.setText(String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)));
        calendar.add(Calendar.DAY_OF_MONTH,1);
        textViewTue.setText(String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)));
        calendar.add(Calendar.DAY_OF_MONTH,1);
        textViewWed.setText(String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)));
        calendar.add(Calendar.DAY_OF_MONTH,1);
        textViewThu.setText(String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)));
        calendar.add(Calendar.DAY_OF_MONTH,1);
        textViewFri.setText(String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)));
        calendar.add(Calendar.DAY_OF_MONTH,1);
        textViewSat.setText(String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)));

        textViewMonth.setText(String.valueOf(month)+"月");


        DisplayMetrics dm=new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int firstColHeight=dm.heightPixels/12;
        int firstColWidth=dm.widthPixels/30*2;

        int courseColWidth=dm.widthPixels/30*4;
        courseRelative=(RelativeLayout)findViewById(R.id.course_relative);
        System.out.println(firstColWidth);
        System.out.println(firstColHeight);
        for(int i=1;i<=12;i++){
            for(int j=1;j<=8;j++){
                if(j==1){
                    //第一列
                    RelativeLayout.LayoutParams lp=new RelativeLayout.LayoutParams(firstColWidth,firstColHeight);
                    TextView textView=new TextView(context);
                    textView.setTextSize(18);
                    textView.setTextColor(ContextCompat.getColor(context,R.color.courseWeek));
                    textView.setBackgroundResource(R.drawable.course_first_textview);
                    @android.support.annotation.IdRes int id=i;
                    textView.setId(id);
                    textView.setText(String.valueOf(i));
                    textView.setGravity(Gravity.CENTER);
                    if(i==1){
                        //lp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                    }else{
                        lp.addRule(RelativeLayout.BELOW,i-1);
                    }
                    courseRelative.addView(textView,lp);
                }else{
                    //课程信息
                    RelativeLayout.LayoutParams lp=new RelativeLayout.LayoutParams(courseColWidth,firstColHeight);
                    TextView textView=new TextView(context);
                    textView.setBackgroundResource(R.drawable.course_first_textview);
                    @android.support.annotation.IdRes int id=(i+1)*12+j-2;
                    textView.setId(id);
                    if(j==2){
                        lp.addRule(RelativeLayout.RIGHT_OF,j-1);
                    }else{
                        lp.addRule(RelativeLayout.RIGHT_OF,(i+1)*12+j-2-1);
                    }
                    if(i>1){
                        lp.addRule(RelativeLayout.BELOW,(i)*12+j-2);
                    }
                    courseRelative.addView(textView,lp);
                }
            }
        }
        TextView courseText=new TextView(context);
        RelativeLayout.LayoutParams lp=new RelativeLayout.LayoutParams(courseColWidth-4,firstColHeight*2-6);
        lp.addRule(RelativeLayout.RIGHT_OF,(2+1)*12+1);
        lp.rightMargin=-courseColWidth+2;
        lp.topMargin=3;
        lp.bottomMargin=3;
        lp.leftMargin=2;
        courseText.setBackgroundResource(R.drawable.course_info_green);
        courseRelative.addView(courseText,lp);
    }

}
