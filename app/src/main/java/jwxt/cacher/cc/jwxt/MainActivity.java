package jwxt.cacher.cc.jwxt;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Handler;
import android.os.Message;
import android.provider.DocumentsContract;
import android.support.annotation.Nullable;
import android.support.v4.graphics.BitmapCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ButtonBarLayout;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.w3c.dom.Text;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    private TextView textViewLib;
    private TextView textViewCard;
    private TextView textViewName;

    private SZSDConnection szsdConnection;
    private Handler handlerInfo;
    private Handler handlerCourse;
    private Handler handlerClassRoom;
    private Handler handlerProgressDialog;
    private ProgressDialog progressDialog;
    private Context context;
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
                List<Course> courseList=szsdConnection.getCourseInfo("2016-2017-1","");
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
        handlerInfo=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Map<String,String> info=(HashMap<String, String>)msg.obj;
                textViewName.setText(info.get("name"));
                textViewCard.setText(info.get("card"));
                textViewLib.setText(info.get("lib"));
            }
        };
        handlerCourse=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                ArrayList<Course> courseList=(ArrayList<Course>)msg.obj;

                progressDialog.cancel();

                Intent intent=new Intent(MainActivity.this,CourseActivity.class);
                intent.putExtra("connection",szsdConnection);
                intent.putExtra("course",courseList);
                startActivity(intent);
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
        handlerProgressDialog=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
            }
        };
    }
}
