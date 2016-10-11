package jwxt.cacher.cc.jwxt;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jwxt.cacher.cc.jwxt.util.ObjectSaveUtils;

public class MainActivity extends AppCompatActivity {
    private TextView textViewLib;
    private TextView textViewCard;
    private TextView textViewName;

    private SZSDConnection szsdConnection;
    private Handler handlerCourse;
    private Handler handlerClassRoom;
    private ProgressDialog progressDialog;
    private Context context;

    private SharedPreferences sharedPreferences;
    private boolean isSameUser;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context=this;

        textViewLib=(TextView)findViewById(R.id.tv_main_lib);
        textViewCard=(TextView)findViewById(R.id.tv_main_card);
        textViewName=(TextView)findViewById(R.id.tv_main_name);

        progressDialog=new ProgressDialog(context);
        progressDialog.setMessage("正在加载，请稍后...");
        progressDialog.setCancelable(false);

        sharedPreferences=this.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        isSameUser=sharedPreferences.getBoolean("sameUser",true);
        szsdConnection=(SZSDConnection)getIntent().getSerializableExtra("connection");
        String name=(String)getIntent().getSerializableExtra("name");
        String lib=(String)getIntent().getSerializableExtra("lib");
        String card=(String)getIntent().getSerializableExtra("card");
        textViewName.setText(name);
        textViewLib.setText(lib);
        textViewCard.setText(card);
        initHandler();

    }
    public void onCoursesClick(View view){
        progressDialog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                ObjectSaveUtils objectSaveUtils = new ObjectSaveUtils(context, "courseInfo");
                List<Course> courseList=null;
                if(isSameUser) {
                    courseList = objectSaveUtils.getObject("courseList");
                }
                isSameUser=true;
                if(courseList==null||courseList.get(0).getCourseName().equals("评教未完成")){
                    courseList=szsdConnection.getCourseInfo("2016-2017-1","");
                    objectSaveUtils.setObject("courseList",courseList);
                }
                Message msg=handlerCourse.obtainMessage();
                msg.obj=courseList;
                handlerCourse.sendMessage(msg);
            }
        }).start();

    }
    public void onScoreClick(View view){
        Intent intent=new Intent(MainActivity.this,ScoreActivity.class);
        intent.putExtra("connection",szsdConnection);
        startActivity(intent);
    }
    public void onClassRoomClick(View view){
        progressDialog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Map<String,String> classRoomMap=szsdConnection.getCurrentClassRoom();
                Message msg=handlerClassRoom.obtainMessage();
                msg.obj=classRoomMap;
                handlerClassRoom.sendMessage(msg);
            }
        }).start();

    }
    public void onLibClick(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("");
        builder.setMessage("开发中...");
        builder.setCancelable(false);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.create().show();
    }
    private void initHandler(){
        handlerCourse=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                ArrayList<Course> courseList=(ArrayList<Course>)msg.obj;

                progressDialog.cancel();
                if(courseList.size()==1&&courseList.get(0).getCourseName().equals("评教未完成")){
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("");
                    builder.setMessage("评教未完成，无法获取课表。");
                    builder.setCancelable(false);
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });

                    builder.create().show();
                }else{
                    Intent intent=new Intent(MainActivity.this,CourseActivity.class);
                    intent.putExtra("connection",szsdConnection);
                    intent.putExtra("course",courseList);
                    startActivity(intent);
                }

            }
        };
        handlerClassRoom=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                HashMap<String,String> classMap=(HashMap<String,String>)msg.obj;

                progressDialog.cancel();

                Intent intent=new Intent(MainActivity.this,ClassRoomActivity.class);
                intent.putExtra("connection",szsdConnection);
                intent.putExtra("classRoomMap",classMap);
                startActivity(intent);
            }
        };

    }
}
