package jwxt.cacher.cc.jwxt;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.ExpandedMenuView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Exchanger;

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

    private Context context;
    private boolean logining;
    private int currentVersion;
    private int updateVersion;

    private SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_szsd_login);
        szsdConnection = new SZSDConnection();
        context = this;
        textViewUsername = (TextView) findViewById(R.id.tv_szsd_username);
        textViewPassword = (TextView) findViewById(R.id.tv_szsd_password);
        checkBoxRememberPass=(CheckBox)findViewById(R.id.checkbox_rememberPass);
        checkBoxAutoLog=(CheckBox)findViewById(R.id.checkbox_autoLogin);
        sharedPreferences=this.getSharedPreferences("userInfo",Context.MODE_PRIVATE);

        if(sharedPreferences.getBoolean("IS_CHECKED",false)){
            checkBoxRememberPass.setChecked(true);
            textViewUsername.setText(sharedPreferences.getString("ACCOUNT",""));
            textViewPassword.setText(sharedPreferences.getString("PASSWORD",""));
        }else{
            checkBoxRememberPass.setChecked(false);
        }

        PackageManager packageManager=getPackageManager();
        try{
            PackageInfo packageInfo=packageManager.getPackageInfo(getPackageName(),0);
            currentVersion=packageInfo.versionCode;
        }catch (Exception e){
            e.printStackTrace();
        }

        initHandler();
        logining = false;
        new Thread(new Runnable() {
            @Override
            public void run() {
                updateVersion=szsdConnection.getVersionCode();
                Map<String,Object> updateInfo=szsdConnection.getUpdateInfo();
                String[] info=(String[])updateInfo.get("info");
                String link=(String)updateInfo.get("link");
                for(String str:info){
                    System.out.println(str);
                }
                System.out.println(link);
                Message msg=handlerUpdate.obtainMessage();
                msg.obj=info;
                handlerUpdate.sendMessage(msg);
                if(updateVersion>currentVersion){

                }
            }
        }).start();
    }

    public void onSZSDLoginClick(View view) {
        if(logining==false){
            final String username = textViewUsername.getText().toString();
            final String password = textViewPassword.getText().toString();
            if(checkBoxRememberPass.isChecked()){
                sharedPreferences.edit().putBoolean("IS_CHECKED",true).commit();
                sharedPreferences.edit().putString("ACCOUNT",username).commit();
                sharedPreferences.edit().putString("PASSWORD",password).commit();
            }else{
                sharedPreferences.edit().putBoolean("IS_CHECKED",false).commit();
                sharedPreferences.edit().putString("ACCOUNT","").commit();
                sharedPreferences.edit().putString("PASSWORD","").commit();
            }
            new Thread(new Runnable() {
                @Override
                public void run() {
                    logining=true;
                    Message msg = handlerLogin.obtainMessage();
                    if (username.length() == 0 || password.length() == 0) {
                        msg.arg1 = 3;//用户名或密码为空
                    } else if (szsdConnection.szsdLogin(username, password, context) == false) {
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
                    logining=false;
                    handlerLogin.sendMessage(msg);
                }
            }).start();
        }

    }

    private void initHandler() {
        handlerLogin = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                System.out.println(msg);
                switch (msg.arg1) {
                    case 1:
                        Toast.makeText(context, "用户名或密码错误", Toast.LENGTH_SHORT).show();
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
        handlerUpdate=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                String[] info=(String[])msg.obj;
                showUpdateDialog(info);
            }
        };
    }
    private void showUpdateDialog(final String[] info){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("有更新啦~");
        builder.setMessage(info[0]+"\n"+info[1]);
        builder.setCancelable(false);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            URL url=new URL("http://120.27.117.34:4549/SZSDServlet2/UPCAid-1.0.0.apk");
                            HttpURLConnection httpURLConnection=(HttpURLConnection)url.openConnection();
                            httpURLConnection.setDoInput(true);
                            httpURLConnection.connect();
                            InputStream inputStream=new BufferedInputStream(httpURLConnection.getInputStream());
                            System.out.println(inputStream.available());
                            System.out.println(httpURLConnection.getContentLength());
                        }catch (Exception e){
                            e.printStackTrace();
                        }


                    }
                }).start();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.create().show();
    }
}
