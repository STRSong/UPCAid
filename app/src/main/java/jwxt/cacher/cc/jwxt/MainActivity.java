package jwxt.cacher.cc.jwxt;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.renderscript.ScriptGroup;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.TintableBackgroundView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
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

import org.w3c.dom.Text;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jwxt.cacher.cc.jwxt.info.BookInfo;
import jwxt.cacher.cc.jwxt.info.Course;
import jwxt.cacher.cc.jwxt.util.CacherUtils;
import jwxt.cacher.cc.jwxt.util.ObjectSaveUtils;

public class MainActivity extends AppCompatActivity {
    private TextView textViewLib;
    private TextView textViewCard;
    private TextView textViewName;
    private TextView textViewAPPTitle;

    private SZSDConnection szsdConnection;
    private Handler handlerCourse;
    private Handler handlerClassRoom;
    private Handler handlerLibrary;
    private Handler handlerFeedback;
    private Handler handlerUpdate;
    private Handler handlerProcessDialog;

    private ProgressDialog progressDialog;
    private Context context;
    private Activity activity;
    private Bitmap checkBitmap;
    private Toolbar toolbar;

    private SharedPreferences sharedPreferences;
    private boolean isSameUser;
    private PopupWindow popupWindow;
    private View popupView;
    private int currentVersion;
    private int updateVersion;
    private ProgressDialog downFileDialog;

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        activity = this;
        textViewLib = (TextView) findViewById(R.id.tv_main_lib);
        textViewCard = (TextView) findViewById(R.id.tv_main_card);
        textViewName = (TextView) findViewById(R.id.tv_main_name);
        textViewAPPTitle = (TextView) findViewById(R.id.tv_app_title);

//        textViewAPPTitle.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Uri uri = Uri.parse("https://cacher.cc/2016/12/28/UPCAid/");
//                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
//                startActivity(intent);
//            }
//        });

        toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayHomeAsUpEnabled(false);
        }
//        ActionBar actionBar=getSupportActionBar();
        toolbar.setOnMenuItemClickListener(getMenuItemClickListener());

        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("正在加载，请稍后...");
        progressDialog.setCancelable(false);

        sharedPreferences = this.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        isSameUser = sharedPreferences.getBoolean("sameUser", true);
        szsdConnection = SZSDConnection.getInstance();
        String name = (String) getIntent().getSerializableExtra("name");
        String lib = (String) getIntent().getSerializableExtra("lib");
        String card = (String) getIntent().getSerializableExtra("card");
        textViewName.setText(name);
        System.out.println(textViewName.getTextSize());
        if (name.length() >= 6) {
            textViewName.setTextSize(TypedValue.COMPLEX_UNIT_PX, textViewName.getTextSize() - (name.length() - 6) * 4);
        }
        System.out.println(textViewName.getTextSize());
        textViewLib.setText(lib);
        textViewCard.setText(card);
        initHandler();
        initPopupWindow();
//        System.out.println("account:"+sharedPreferences.getString("lastUser",""));

        //获取当前版本号
        PackageManager packageManager = getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
            currentVersion = packageInfo.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }

        initHandler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    updateVersion = szsdConnection.getVersionCode();
                    if (updateVersion != 0) {
                        if (updateVersion > currentVersion) {
                            Map<String, Object> updateInfo = szsdConnection.getUpdateInfo();
                            Message msg = handlerUpdate.obtainMessage();
                            msg.obj = updateInfo;
                            handlerUpdate.sendMessage(msg);
                        }
                    } else {
                        //获取版本号失败
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }).start();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);


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
                    case R.id.main_feedback:
//                        Toast.makeText(context,"AA",Toast.LENGTH_SHORT).show();
                        if (popupWindow != null && !popupWindow.isShowing()) {
                            popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);
                            WindowManager.LayoutParams lp = getWindow().getAttributes();
                            lp.alpha = 0.6f;
                            getWindow().setAttributes(lp);
                        }
                        break;
                    case R.id.main_changeAccount:
                        Intent intent = new Intent(MainActivity.this, SZSDLoginActivity.class);
                        startActivity(intent);
                        MainActivity.this.finish();
                        break;
                    case R.id.main_about:
                        Uri uri = Uri.parse("https://www.cacher.cc/2016/12/28/UPCAid.html");
                        intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);
                        break;
                }
                return false;
            }
        };
    }

    public void onCoursesClick(View view) {
        progressDialog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                ObjectSaveUtils objectSaveUtils = new ObjectSaveUtils(context, "courseInfo");
                List<Course> courseList = null;
                if (isSameUser) {
                    courseList = objectSaveUtils.getObject("courseList");
                }
                isSameUser = true;
                //尝试重新从教务系统获取课表。
                if (courseList == null || (courseList.size() > 0 && courseList.get(0).getCourseName().equals("评教未完成"))) {
                    //课表学期
                    courseList = szsdConnection.getCourseInfo(CourseActivity.xq, "");
                    objectSaveUtils.setObject("courseList", courseList);
                }
                Message msg = handlerCourse.obtainMessage();
                msg.obj = courseList;
                handlerCourse.sendMessage(msg);
            }
        }).start();

    }

    public void onScoreClick(View view) {
        Intent intent = new Intent(MainActivity.this, ScoreActivity.class);
        startActivity(intent);
    }

    public void onClassRoomClick(View view) {
        progressDialog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Map<String, String> classRoomMap = szsdConnection.getCurrentClassRoom();
                Message msg = handlerClassRoom.obtainMessage();
                msg.obj = classRoomMap;
                handlerClassRoom.sendMessage(msg);
            }
        }).start();

    }

    public void onLibClick(View view) {
        progressDialog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                ArrayList<BookInfo> bookInfoArrayList = szsdConnection.getBookList();
                checkBitmap = szsdConnection.getLibCaptcha();
                if (bookInfoArrayList == null) {
                    bookInfoArrayList = new ArrayList<BookInfo>();
                }
                Message msg = handlerLibrary.obtainMessage();
                msg.obj = bookInfoArrayList;
                handlerLibrary.sendMessage(msg);
            }
        }).start();
    }

    private void initHandler() {
        handlerCourse = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                ArrayList<Course> courseList = (ArrayList<Course>) msg.obj;

                progressDialog.cancel();
                if (courseList != null) {
                    if (courseList.size() == 1 && courseList.get(0).getCourseName().equals("评教未完成")) {
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
                    } else {
                        Intent intent = new Intent(MainActivity.this, CourseActivity.class);
                        intent.putExtra("course", courseList);
                        startActivity(intent);
                    }
                }


            }
        };
        handlerClassRoom = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                HashMap<String, String> classMap = (HashMap<String, String>) msg.obj;

                progressDialog.cancel();

                Intent intent = new Intent(MainActivity.this, ClassRoomActivity.class);
                intent.putExtra("classRoomMap", classMap);
                startActivity(intent);
            }
        };
        handlerLibrary = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                ArrayList<BookInfo> bookInfoArrayList = (ArrayList<BookInfo>) msg.obj;
                progressDialog.cancel();
                Intent intent = new Intent(MainActivity.this, LibraryActivity.class);
                intent.putExtra("bookInfoArrayList", bookInfoArrayList);
                intent.putExtra("captcha", checkBitmap);
                startActivity(intent);
            }
        };
        handlerFeedback = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Toast.makeText(context, "谢谢您的支持！ ^_^", Toast.LENGTH_SHORT).show();
            }
        };
        handlerUpdate = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Map<String, Object> updateInfo = (Map<String, Object>) msg.obj;
                //权限检查
//                int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
//                if (permission != PackageManager.PERMISSION_GRANTED) {
//                    ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
//                }
                showUpdateDialog(updateInfo);
            }
        };
        handlerProcessDialog = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                int result = msg.arg1;
                //1登录显示，2登录隐藏，3下载隐藏
                switch (result) {
                    case 1:
                        progressDialog.show();
                        break;
                    case 2:
                        progressDialog.cancel();
                        break;
                    case 3:
                        downFileDialog.cancel();
                        update();
                        break;
                }
            }
        };
    }

    private void initPopupWindow() {
        popupView = View.inflate(context, R.layout.feedback_popupwindow, null);
        final EditText editTextContent = (EditText) popupView.findViewById(R.id.main_feedback_content);
        final EditText editTextConnect = (EditText) popupView.findViewById(R.id.main_feedback_connect);
        Button button = (Button) popupView.findViewById(R.id.btn_main_feedback);
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
                final String account = sharedPreferences.getString("lastUser", "");
                final String content = editTextContent.getText().toString();
                final String connect = editTextConnect.getText().toString();
                if (content.length() == 0) {
                    Toast.makeText(context, "没有什么问题吗？O_O", Toast.LENGTH_SHORT).show();
                } else {
                    if (popupWindow.isShowing()) {
                        popupWindow.dismiss();
                    }
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            szsdConnection.feedback(account, content, connect);
                            Message msg = handlerFeedback.obtainMessage();
                            handlerFeedback.sendMessage(msg);
                        }
                    }).start();
                }
            }
        });

        popupWindow = new PopupWindow(context);
        popupWindow.setContentView(popupView);
        popupWindow.setAnimationStyle(R.style.PopupAnimationClassRoomSearch);
        popupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.classroom_search_white));
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1f;
                getWindow().setAttributes(lp);
            }
        });
    }

    private void showUpdateDialog(final Map<String, Object> updateInfo) {
        String[] info = (String[]) updateInfo.get("info");
        final String link = (String) updateInfo.get("link");
        for (String str : info) {
            System.out.println(str);
        }
        System.out.println(link);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("有更新啦~");
        StringBuilder message = new StringBuilder();
        for (int i = 0; i < info.length; i++) {
            message.append(info[i]);
            if (i < info.length - 1) {
                message.append("\n");
            }
        }
        builder.setMessage(message.toString());
        builder.setCancelable(false);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                downFile(link);
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.create().show();
    }

    private void downFile(final String downUrl) {
        downFileDialog = new ProgressDialog(context);
        downFileDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        downFileDialog.setTitle("正在下载");
        downFileDialog.setMessage("请稍后...");
        downFileDialog.setProgress(0);
        downFileDialog.setCancelable(false);
        downFileDialog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(downUrl);
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setDoInput(true);
                    httpURLConnection.connect();
                    InputStream inputStream = new BufferedInputStream(httpURLConnection.getInputStream());

                    int fileLength = httpURLConnection.getContentLength();
                    System.out.println("文件大小:" + fileLength);
                    //设置进度条总进度
                    downFileDialog.setMax(fileLength);

//                    File dir = new File(Environment.getExternalStorageDirectory(), "SZSD");
//                    if (!dir.exists()) {
//                        dir.mkdirs();
//                    }
                    File dir = CacherUtils.getExternalCacheDirectory(context, "");
                    File apkFile = new File(dir, "UPCAid.apk");
                    if (!apkFile.exists()) {
                        apkFile.createNewFile();
                    }
                    FileOutputStream fileOutputStream = new FileOutputStream(apkFile);
                    byte[] buf = new byte[1024];
                    int length = inputStream.read(buf);
                    int process = 0;
                    while (length != -1) {
                        fileOutputStream.write(buf, 0, length);
                        length = inputStream.read(buf);
                        process += length;
                        downFileDialog.setProgress(process);
                    }
                    fileOutputStream.flush();
                    fileOutputStream.close();
                    inputStream.close();
                    Message msg = handlerProcessDialog.obtainMessage();
                    msg.arg1 = 3;
                    handlerProcessDialog.sendMessage(msg);
                    if (httpURLConnection != null) {
                        httpURLConnection.disconnect();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    //打开apk安装界面
    private void update() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(new File(CacherUtils.getExternalCacheDirectory(context, ""),
                "UPCAid.apk")), "application/vnd.android.package-archive");
        startActivity(intent);
    }
}
