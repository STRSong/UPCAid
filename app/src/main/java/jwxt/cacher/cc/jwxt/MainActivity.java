package jwxt.cacher.cc.jwxt;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.renderscript.ScriptGroup;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jwxt.cacher.cc.jwxt.info.BookInfo;
import jwxt.cacher.cc.jwxt.info.Course;
import jwxt.cacher.cc.jwxt.util.ObjectSaveUtils;

public class MainActivity extends AppCompatActivity {
    private TextView textViewLib;
    private TextView textViewCard;
    private TextView textViewName;

    private SZSDConnection szsdConnection;
    private Handler handlerCourse;
    private Handler handlerClassRoom;
    private Handler handlerLibrary;
    private Handler handlerFeedback;
    private ProgressDialog progressDialog;
    private Context context;
    private Bitmap checkBitmap;
    private Toolbar toolbar;

    private SharedPreferences sharedPreferences;
    private boolean isSameUser;
    private PopupWindow popupWindow;
    private View popupView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context=this;

        textViewLib=(TextView)findViewById(R.id.tv_main_lib);
        textViewCard=(TextView)findViewById(R.id.tv_main_card);
        textViewName=(TextView)findViewById(R.id.tv_main_name);
        toolbar=(Toolbar)findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayHomeAsUpEnabled(false);
        }
//        ActionBar actionBar=getSupportActionBar();
        toolbar.setOnMenuItemClickListener(getMenuItemClickListener());

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
        initPopupWindow();
//        System.out.println("account:"+sharedPreferences.getString("lastUser",""));
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);


        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        return super.onPrepareOptionsMenu(menu);
    }
    private Toolbar.OnMenuItemClickListener getMenuItemClickListener(){
        return new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.main_feedback:
//                        Toast.makeText(context,"AA",Toast.LENGTH_SHORT).show();
                        if(popupWindow!=null&&!popupWindow.isShowing()){
                            popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);
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
        progressDialog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                ArrayList<BookInfo> bookInfoArrayList = szsdConnection.getBookList();
                checkBitmap=szsdConnection.getLibCaptcha();
                if(bookInfoArrayList==null){
                    bookInfoArrayList=new ArrayList<BookInfo>();
                }
                Message msg=handlerLibrary.obtainMessage();
                msg.obj=bookInfoArrayList;
                handlerLibrary.sendMessage(msg);
            }
        }).start();
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
        handlerLibrary=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                ArrayList<BookInfo> bookInfoArrayList=(ArrayList<BookInfo>)msg.obj;
                progressDialog.cancel();
                Intent intent=new Intent(MainActivity.this,LibraryActivity.class);
                intent.putExtra("connection",szsdConnection);
                intent.putExtra("bookInfoArrayList",bookInfoArrayList);
                intent.putExtra("captcha",checkBitmap);
                startActivity(intent);
            }
        };
        handlerFeedback=new Handler(){

            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Toast.makeText(context,"谢谢您的支持！ ^_^",Toast.LENGTH_SHORT).show();
            }
        };
    }
    private void initPopupWindow(){
        popupView=View.inflate(context,R.layout.feedback_popupwindow,null);
        final EditText editTextContent=(EditText)popupView.findViewById(R.id.main_feedback_content);
        final EditText editTextConnect=(EditText)popupView.findViewById(R.id.main_feedback_connect);
        Button button=(Button)popupView.findViewById(R.id.btn_main_feedback);
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
                final String account=sharedPreferences.getString("lastUser","");
                final String content=editTextContent.getText().toString();
                final String connect=editTextConnect.getText().toString();
                if(content.length()==0){
                    Toast.makeText(context,"没有什么问题吗？O_O",Toast.LENGTH_SHORT).show();
                }else{
                    if(popupWindow.isShowing()){
                        popupWindow.dismiss();
                    }
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            szsdConnection.feedback(account,content,connect);
                            Message msg=handlerFeedback.obtainMessage();
                            handlerFeedback.sendMessage(msg);
                        }
                    }).start();
                }
            }
        });

        popupWindow=new PopupWindow(context);
        popupWindow.setContentView(popupView);
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
    }
}
