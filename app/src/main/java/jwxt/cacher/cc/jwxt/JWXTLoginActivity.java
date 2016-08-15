package jwxt.cacher.cc.jwxt;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class JWXTLoginActivity extends AppCompatActivity {

    ImageView imageView;
    Button button;
    EditText edit_Username;
    EditText edit_PassWd;
    EditText edit_randomCode;
    Context context;
    HttpURLConnection connection;
    ExecutorService threadPool;

    JWXTConnection jwxtConnection;
    public static List<HashMap<String,String>> data;
    String cookie;
    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            imageView.setImageBitmap((Bitmap) msg.obj);
        }
    };
    Handler handler1=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            data=(List<HashMap<String,String>>)msg.obj;
            Intent intent=new Intent(JWXTLoginActivity.this,ScoreActivity.class);
            startActivityForResult(intent,1);
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jwxt_login);
        context=this;

        jwxtConnection=new JWXTConnection();

        imageView=(ImageView)findViewById(R.id.randomImg);
        button=(Button)findViewById(R.id.btn_login);
        edit_Username=(EditText)findViewById(R.id.edit_username);
        edit_PassWd=(EditText)findViewById(R.id.edit_passwd);
        edit_randomCode=(EditText)findViewById(R.id.edit_randomcode);
        threadPool= Executors.newSingleThreadExecutor();

        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap=jwxtConnection.getRandomCode();
                if(bitmap!=null){
                    Message msg=handler.obtainMessage();
                    msg.obj=bitmap;
                    handler.sendMessage(msg);
                }
            }
        });

    }
    public void onRandomImgClick(View view){
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap=jwxtConnection.getRandomCode();
                if(bitmap!=null){
                    Message msg=handler.obtainMessage();
                    msg.obj=bitmap;
                    handler.sendMessage(msg);
                }
            }
        });
    }
    public void onBtn_loginClick(View view){
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                try{
                    String result=jwxtConnection.connect(edit_Username.getText().toString(),
                            edit_PassWd.getText().toString(),edit_randomCode.getText().toString());
                    System.out.println(result);
                    if(result!=null){
                        Toast.makeText(context,result,Toast.LENGTH_SHORT).show();
                    }
//                    Message msg=handler1.obtainMessage();
//                    msg.obj=data;
//                    handler1.sendMessage(msg);

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }
}
