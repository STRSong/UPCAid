package jwxt.cacher.cc.jwxt;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jwxt.cacher.cc.jwxt.picker.MyOptionPicker;
import jwxt.cacher.cc.jwxt.picker.OptionPicker;


/**
 * Created by xhaiben on 2016/8/28.
 */
public class SZSDLoginActivity extends AppCompatActivity {
    private SZSDConnection szsdConnection;
    private TextView textViewUsername;
    private TextView textViewPassword;
    private CheckBox checkBoxRememberPass;
    private CheckBox checkBoxAutoLog;

    private Handler handlerLogin;
    private Handler handlerUpdate;
    private Handler handlerProcessDialog;

    private Context context;
    private int currentVersion;
    private int updateVersion;

    private SharedPreferences sharedPreferences;
    private ProgressDialog progressDialog;
    private ProgressDialog downFileDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_szsd_login);
        szsdConnection = new SZSDConnection();
        context = this;
        textViewUsername = (TextView) findViewById(R.id.tv_szsd_username);
        textViewPassword = (TextView) findViewById(R.id.tv_szsd_password);
        checkBoxRememberPass = (CheckBox) findViewById(R.id.checkbox_rememberPass);
        checkBoxAutoLog = (CheckBox) findViewById(R.id.checkbox_autoLogin);
        sharedPreferences = this.getSharedPreferences("userInfo", Context.MODE_PRIVATE);

        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("正在登录，请稍后...");
        progressDialog.setCancelable(false);

        if (sharedPreferences.getBoolean("IS_CHECKED", false)) {
            checkBoxRememberPass.setChecked(true);
            textViewUsername.setText(sharedPreferences.getString("ACCOUNT", ""));
            textViewPassword.setText(sharedPreferences.getString("PASSWORD", ""));
        } else {
            checkBoxRememberPass.setChecked(false);
        }
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
                try{
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
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        }).start();




    }

    public void onSZSDLoginClick(View view) {

        final String username = textViewUsername.getText().toString();
        final String password = textViewPassword.getText().toString();
        if (checkBoxRememberPass.isChecked()) {
            sharedPreferences.edit().putBoolean("IS_CHECKED", true).commit();
            sharedPreferences.edit().putString("ACCOUNT", username).commit();
            sharedPreferences.edit().putString("PASSWORD", password).commit();
        } else {
            sharedPreferences.edit().putBoolean("IS_CHECKED", false).commit();
            sharedPreferences.edit().putString("ACCOUNT", "").commit();
            sharedPreferences.edit().putString("PASSWORD", "").commit();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {

                Message msg = handlerLogin.obtainMessage();
                Message msgProcess = handlerProcessDialog.obtainMessage();
                msgProcess.arg1 = 1;
                handlerProcessDialog.sendMessage(msgProcess);
                boolean loginResult = szsdConnection.szsdLogin(username, password, context);

                if (username.length() == 0 || password.length() == 0) {
                    msg.arg1 = 3;//用户名或密码为空
                } else if (loginResult == false) {
                    msg.arg1 = 1;//登陆失败
                } else {
                    msg.arg1 = 2;//登陆成功
                    Map<String, String> infoMap1 = szsdConnection.getSelfInfo();
                    Map<String, String> infoMap2 = szsdConnection.getLibAndCardInfo();
                    Map<String, String> info = new HashMap<String, String>();
                    info.put("name", infoMap1.get("USER_NAME"));
                    info.put("lib", infoMap2.get("bookNum"));
                    info.put("card", infoMap2.get("card"));
                    msg.obj = info;
                }

                msgProcess = handlerProcessDialog.obtainMessage();
                msgProcess.arg1 = 2;
                handlerProcessDialog.sendMessage(msgProcess);

                handlerLogin.sendMessage(msg);

            }
        }).start();


    }

    private void initHandler() {
        handlerLogin = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                System.out.println(msg);
                switch (msg.arg1) {
                    case 1:
                        Toast.makeText(context, "登录失败，请重试", Toast.LENGTH_SHORT).show();
                        break;
                    case 2:
                        Map<String, String> info = (HashMap<String, String>) msg.obj;
                        SZSDConnection conn = szsdConnection;
                        Intent intent = new Intent(SZSDLoginActivity.this, MainActivity.class);
                        intent.putExtra("connection", conn);
                        intent.putExtra("name", info.get("name"));
                        intent.putExtra("card", info.get("card"));
                        intent.putExtra("lib", info.get("lib"));
                        startActivity(intent);
                        break;
                    case 3:
                        Toast.makeText(context, "用户名或密码不能为空", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };
        handlerUpdate = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Map<String, Object> updateInfo = (Map<String, Object>) msg.obj;
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
    private void showUpdateDialog(final Map<String, Object> updateInfo) {
        String[] info = (String[]) updateInfo.get("info");
        final String link = (String) updateInfo.get("link");
        for (String str : info) {
            System.out.println(str);
        }
        System.out.println(link);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("有更新啦~");
        StringBuilder message=new StringBuilder();
        for(int i=0;i<info.length;i++){
            message.append(info[i]);
            if(i<info.length-1){
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

                    File file = new File(Environment.getExternalStorageDirectory(), "SZSD");
                    if (!file.exists()) {
                        file.mkdir();
                    }
                    File apkFile = new File(file, "UPCAid.apk");
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
        intent.setDataAndType(Uri.fromFile(new File(Environment.getExternalStorageDirectory(),
                "SZSD" + File.separator + "UPCAid.apk")), "application/vnd.android.package-archive");
        startActivity(intent);
    }
}
